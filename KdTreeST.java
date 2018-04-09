package a062dTree;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import java.util.ArrayList;
import java.util.StringJoiner;
/**
 *
 * @author gordl
 * @param <Value>
 */
public class KdTreeST<Value> {
    public class Node {
       public Node left, right;
       public Point2D key;
       public Double xmax;
       public Double xmin;
       public Double ymin;
       public Double ymax;
       public Value value;
       public Boolean isHorizontal;
       public Integer size;
       public Node(Point2D key, Value val, int size, boolean isHorizontal, double xmin, double xmax, double ymin, double ymax) {
           left = null;
           right = null;
           this.key = key;
           this.value = val;
           this.isHorizontal = isHorizontal;
           this.size = size;
           this.xmin = xmin;
           this.xmax = xmax;
           this.ymin = ymin;
           this.ymax = ymax;
       }
       
       @Override
       public String toString() {
           StringJoiner sj = new StringJoiner(", ");
           sj.add(key.toString());
           sj.add(value.toString());
           sj.add(size.toString());
           sj.add(isHorizontal.toString());
           return sj.toString();
       }
   }
   private Node root;
   public KdTreeST() { }
   
   public boolean isEmpty() {
     return size() == 0;
   } 
   
   public int size() {
     return size(root);  
   }
   
    private int size(Node x) {
        if (x == null) return 0;
        else return x.size;
    }
   
   public void put(Point2D p, Value val) {
       if (p == null) throw new IllegalArgumentException("calls put() with a null key");
       if (val == null) throw new IllegalArgumentException("delete operation is not supported");
       boolean horiz = false;  // root splits plane vertically
       root = put(root, p, val, horiz, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
   }
   private Node put(Node r, Point2D key, Value val, boolean splitsHorizontally, double xmin, double xmax, double ymin, double ymax) {
       if (r == null) return new Node(key, val, 1, splitsHorizontally, xmin, xmax, ymin, ymax);
       
       int cmp;
       if(splitsHorizontally) cmp = Point2D.Y_ORDER.compare(key, r.key);
       else cmp = Point2D.X_ORDER.compare(key, r.key);
       
       splitsHorizontally = !splitsHorizontally;
       if(r.isHorizontal) {
            if(cmp < 0) r.left = put(r.left, key, val, splitsHorizontally, xmin, xmax, ymin, r.key.y());
            else if(cmp > 0) r.right = put(r.right, key, val, splitsHorizontally, xmin, xmax, r.key.y(), ymax); // can there be a case where max will not be infinity here?
            else r.value = val;  // overwrites value at tree[key]
       } else {
           if(cmp < 0) r.left = put(r.left, key, val, splitsHorizontally, xmin, r.key.x(), ymin, ymax);
           else if(cmp > 0) r.right = put(r.right, key, val, splitsHorizontally, r.key.x(), xmax, ymin, ymax);
           else r.value = val;
       }
       
       r.size = 1 + size(r.left) + size(r.right); // calculate tree/subtree size rooted at r
       return r;
   }
   
   public Value get(Point2D key) {
        boolean splitsHorizontally = false;
        return get(root, key, splitsHorizontally);
    }

    private Value get(Node r, Point2D key, boolean splitsHorizontally) {
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        if (r == null) return null;
        int cmp;
        if(splitsHorizontally) cmp = Point2D.Y_ORDER.compare(key, r.key);
        else cmp = Point2D.X_ORDER.compare(key, r.key);
        splitsHorizontally = !splitsHorizontally;
        if (cmp < 0) return get(r.left, key, splitsHorizontally);
        else if (cmp > 0) return get(r.right, key, splitsHorizontally);
        else              return r.value;
    }
    
   public boolean contains(Point2D p) {
    return get(p) != null;   
   }
   public Iterable<Point2D> points() {
        Queue<Point2D> keys = new Queue<>();
        Queue<Node> queue = new Queue<>();
        queue.enqueue(root);
        while (!queue.isEmpty()) {
            Node x = queue.dequeue();
            if (x == null) continue;
            keys.enqueue(x.key);
            queue.enqueue(x.left);
            queue.enqueue(x.right);
        }
        return keys;
    }
   
    public Iterable<Point2D> range(RectHV rect) {
        ArrayList<Point2D> a = new ArrayList<>();
        range(root, rect, a);
        return a;
    }
    
    private void range(Node r, RectHV rect, ArrayList<Point2D> a) {
        // if(r == null) return;  // // shouldn't be necessary
        if(rect.contains(r.key)) {
            a.add(r.key);
        }
        
        if(r.left != null && r.left.xmax >= rect.xmin() && r.left.xmin <= rect.xmax() && r.left.ymax >= rect.ymin() && r.left.ymin <= rect.ymax()) {
            range(r.left, rect, a);  // go left
        }
        
        if(r.right != null && r.right.xmax >= rect.xmin() && r.right.xmin <= rect.xmax() && r.right.ymax >= rect.ymin() && r.right.ymin <= rect.ymax()) {
            range(r.right, rect, a); // go right
        }
    }

	// a nearest neighbor to point p; null if the symbol table is empty
	public Point2D nearest(Point2D qPoint) {
		Node champ = null;
		return nearest(root, qPoint, champ).key;
	}
	
	private Node nearest(Node ch, Point2D qPoint, Node champ) {

		//check if this point is closer to the query point than the current best (champion)
		if (champ == null || qPoint.distanceSquaredTo(champ.key) > qPoint.distanceSquaredTo(ch.key)) //distanceFrom(n.key,qPoint) < distanceFrom(n.key,champ.key))
			champ = ch;
		
		
		boolean checkLeft = false;
		boolean checkRight = false;		
		RectHV leftBound = null;
		RectHV rightBound = null;
		
		// if bounding rectangle of this child is closer than champ
		if (ch.left != null) {
			leftBound = new RectHV(ch.left.xmin, ch.left.ymin, ch.left.xmax, ch.left.ymax);
			if (leftBound.distanceSquaredTo(qPoint) < qPoint.distanceSquaredTo(champ.key)) 
				checkLeft = true;
		}
			
		if (ch.right != null) {
			rightBound = new RectHV(ch.right.xmin, ch.right.ymin, ch.right.xmax, ch.right.ymax);
			if (rightBound.distanceSquaredTo(qPoint) < qPoint.distanceSquaredTo(champ.key)) 
				checkRight = true;
		}
			
		//  qpoint is to the left of node
		if (ch.isHorizontal && ch.key.x() > qPoint.x()) {
				if (ch.left != null && checkLeft)  {champ = nearest(ch.left, qPoint, champ);} // check left side
				if (ch.right != null && checkRight) {champ = nearest(ch.right, qPoint, champ);} // check right side
		} else 	{	
				if (ch.right != null && checkRight) {champ = nearest(ch.right, qPoint, champ);} // check right side	
				if (ch.left != null && checkLeft)  {champ = nearest(ch.left, qPoint, champ);} // check left side			
		}

		return champ;
	}
    
   @Override 
   public String toString() {
       StringJoiner sj = new StringJoiner("}, {", "[{", "}]");
       Queue<Point2D> keys = new Queue<>();
       Queue<Node> queue = new Queue<>();
       queue.enqueue(root);
       while (!queue.isEmpty()) {
            Node x = queue.dequeue();
            if (x == null) continue;
            sj.add(x.toString());
            keys.enqueue(x.key);
            queue.enqueue(x.left);
            queue.enqueue(x.right);
        }
        return sj.toString();
   }

   public static void main(String[] args) {
       KdTreeST<String> st = new KdTreeST<>();
       st.put(new Point2D(0.7, 0.2), "root");
       st.put(new Point2D(0.5, 0.4), "level 1");
       st.put(new Point2D(0.2, 0.3), "level 2");
       st.put(new Point2D(0.4, 0.7), "level 2");
       st.put(new Point2D(0.9, 0.6), "level 1");
       st.put(new Point2D(0.4, 0.1), "root");
       st.put(new Point2D(0.1, 0.2), "level 1");
       st.put(new Point2D(0.9, 0.8), "level 2");
       st.put(new Point2D(0.7, 0.1), "level 2");
       st.put(new Point2D(0.5, 0.5), "level 1");
//       for(Point2D p : st.points()) {
//           System.out.printf("%s ", p.toString());
//       }
//       System.out.printf("%n%b%n", st.contains(new Point2D(0.4, 0.7)));
//       System.out.println(st.toString());
//       System.out.printf("tree size: %s%n", new Integer(st.size()).toString());
        for (Point2D p : st.range(new RectHV(0, 0, 1, 1))) {
            System.out.println(p.toString());
        }
   }
}