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
       Point2D key;
       public Value value;
       public Boolean isHorizontal;
       public Integer size;
       public Node(Point2D key, Value val, int size, boolean isHorizontal) {
           left = null;
           right = null;
           this.key = key;
           this.value = val;
           this.isHorizontal = isHorizontal;
           this.size = size;
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
       root = put(root, p, val, horiz);
   }
   private Node put(Node r, Point2D key, Value val, boolean splitsHorizontally) {
       if (r == null) return new Node(key, val, 1, splitsHorizontally);
       int cmp;
       if(splitsHorizontally) cmp = Point2D.Y_ORDER.compare(key, r.key);
       else cmp = Point2D.X_ORDER.compare(key, r.key);
       splitsHorizontally = !splitsHorizontally;
       if(cmp < 0) r.left = put(r.left, key, val, splitsHorizontally);
       else if(cmp > 0) r.right = put(r.right, key, val, splitsHorizontally);
       else r.value = val;  // overwrites value at tree[key]
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
       for(Point2D p : st.points()) {
           System.out.printf("%s ", p.toString());
       }
       System.out.printf("%n%b%n", st.contains(new Point2D(0.4, 0.7)));
       System.out.println(st.toString());
       System.out.printf("tree size: %s%n", new Integer(st.size()).toString());
   }
}
