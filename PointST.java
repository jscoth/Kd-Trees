import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import java.util.ArrayList;
import java.util.TreeMap;
/**
 *
 * @author gordl
 */
public class PointST<Value> {
   private TreeMap<Point2D, Value> m;
   public PointST() { 
       m = new TreeMap<>();
   }                                // construct an empty symbol table of points 
   public boolean isEmpty() {
     return m.isEmpty();
   } // is the symbol table empty? 
   public int size() {
     return m.size();  
   } // number of points 
   public void put(Point2D p, Value val) {
     m.put(p, val);  
   } // associate the value val with point p
   public Value get(Point2D p) {
     return m.get(p);
   } // value associated with point p 
   public boolean contains(Point2D p) {
    return m.get(p) != null;   
   } // does the symbol table contain point p? 
   public Iterable<Point2D> points() {
       return m.keySet();
   } // all points in the symbol table 
   
   
   public Iterable<Point2D> range(RectHV rect) {
       ArrayList<Point2D> a = new ArrayList();
       for(Point2D p : m.keySet()) {
             if(rect.contains(p)) a.add(p);
       }
       return a;
   } // all points that are inside the rectangle
   
   // returns null if symbol table is empty
   public Point2D nearest(Point2D p) {
       if(this.isEmpty()) return null;
       Double shortestDistance = null;
       Point2D tmp = null;
       for(Point2D np : m.keySet()) {
           Double curDistance = np.distanceTo(p);
           if(shortestDistance == null || curDistance < shortestDistance) {
                shortestDistance = curDistance;
                tmp = np;
           }
       }
       return tmp;
   } // a nearest neighbor to point p; null if the symbol table is empty
//   public static void main(String[] args)                  // unit testing of the methods (not graded) 
}
