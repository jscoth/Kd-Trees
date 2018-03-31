import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
/**
 *
 * @author gordl
 */
public class KdTreeST<Value> {
   private class Node {
       public Node left, right;
       Point2D key;
       public Value value;
       public Boolean isHorizontal;
       public Node(Point2D key, Value val, boolean isHorizontal) {
           left = null;
           right = null;
           this.key = key;
           this.value = val;
           this.isHorizontal = isHorizontal;
       }
       
       @Override
       public String toString() {
           StringBuilder sb = new StringBuilder(",");
           sb.append(key.toString());
           sb.append(value.toString());
           sb.append(isHorizontal.toString());
           return sb.toString();
       }
   }
   private Node root;
   private int size;
   public KdTreeST() { 
       size = 0;
   }
   public boolean isEmpty() {
     return size == 0;
   } 
   public int size() {
     return size;  
   } 
   public void put(Point2D p, Value val) {
       if (p == null) throw new IllegalArgumentException("calls put() with a null key");
       if (val == null) throw new IllegalArgumentException("delete operation is not supported");
       boolean horiz = false;  // root splits plane vertically
       root = put(root, p, val, horiz);
   }
   private Node put(Node r, Point2D key, Value val, boolean splitsHorizontally) {
       if (r == null) return new Node(key, val, splitsHorizontally);
       int cmp;
       if(splitsHorizontally) cmp = Point2D.Y_ORDER.compare(key, r.key);
       else cmp = Point2D.X_ORDER.compare(key, r.key);
       splitsHorizontally = !splitsHorizontally;
       if(cmp < 0) r.left = put(r.left, key, val, splitsHorizontally);
       else if(cmp > 0) r.right = put(r.right, key, val, splitsHorizontally);
       else r.value = val;  // overwrites value at tree[key]
       // calculate tree/subtree size
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

   public static void main(String[] args) {
       KdTreeST<String> st = new KdTreeST<>();
       st.put(new Point2D(0.7, 0.2), "root (0.7, 0.2)");
       st.put(new Point2D(0.5, 0.4), "lvl 1 (0.5, 0.4)");
       st.put(new Point2D(0.2, 0.3), "lvl 2 (0.2, 0.3)");
       st.put(new Point2D(0.4, 0.7), "lvl 2 (0.4, 0.7)");
       st.put(new Point2D(0.9, 0.6), "lvl 1 (0.9, 0.6)");
       for(Point2D p : st.points()) {
           System.out.printf(" %s", st.get(p));
       }
       System.out.printf("%n%b", st.contains(new Point2D(0.4, 0.7)));
   }
}
