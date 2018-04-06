package kdtree;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
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
		public Double max;
		public Double min;
		public Value value;
		public Node parent; // added
		public RectHV rect; // temporary for debug
		public Boolean isHorizontal;
		public Integer size;

		public Node(Point2D key, Value val, int size, Boolean isHorizontal) {
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

		// added
		public int compareTo(KdTreeST<Value>.Node node) {
			if (this.isHorizontal) {
				if (this.key.x() < node.key.x())
					return -1;
				if (this.key.x() > node.key.x())
					return +1;
			} else {
				if (this.key.y() < node.key.y())
					return -1;
				if (this.key.y() > node.key.y())
					return +1;
			}
			return 0;
		}
	}

	private Node root;

	public KdTreeST() {
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		return size(root);
	}

	private int size(Node x) {
		if (x == null)
			return 0;
		else
			return x.size;
	}

	public void put(Point2D p, Value val) {
		if (p == null)
			throw new IllegalArgumentException("calls put() with a null key");
		if (val == null)
			throw new IllegalArgumentException("delete operation is not supported");
		root = put(root, p, val, true);
	}

	/**
	 * Alternate put method
	 * 
	 * @param The
	 *            node to add
	 * @param Point
	 *            associated with this node
	 * @param Value
	 *            stored at this point
	 * @return A node which will be the root of the graph
	 */
	private Node put(Node r, Point2D key, Value val, Boolean split) {
		Node node = new Node(key, val, 1, !split);

		if (r == null)
			return node;

		int cmp = r.compareTo(node);
		if (cmp < 0) {
			r.left = put(r.left, key, val, !split);
		} else if (cmp > 0) {
			r.right = put(r.right, key, val, !split);
		} else
			r.value = val;
		return r;
	}

	// private Node put(Node r, Point2D key, Value val, boolean splitsHorizontally,
	// double min, double max) {
	// if (r == null) return new Node(key, val, 1, splitsHorizontally, min, max);
	//
	// int cmp;
	// if(splitsHorizontally) cmp = Point2D.Y_ORDER.compare(key, r.key);
	// else cmp = Point2D.X_ORDER.compare(key, r.key);
	//
	// splitsHorizontally = !splitsHorizontally;
	// if(r.isHorizontal) {
	// if(cmp < 0) r.left = put(r.left, key, val, splitsHorizontally, r.min,
	// r.key.y());
	// else if(cmp > 0) r.right = put(r.right, key, val, splitsHorizontally,
	// r.key.y(), r.max); // can there be a case where max will not be infinity
	// here?
	// else r.value = val; // overwrites value at tree[key]
	// } else {
	// if(cmp < 0) r.left = put(r.left, key, val, splitsHorizontally, r.min,
	// r.key.x());
	// else if(cmp > 0) r.right = put(r.right, key, val, splitsHorizontally,
	// r.key.x(), r.max);
	// else r.value = val;
	// }
	//
	// r.size = 1 + size(r.left) + size(r.right); // calculate tree/subtree size
	// rooted at r
	// return r;
	// }

	public Value get(Point2D key) {
		boolean splitsHorizontally = true;
		return get(root, key, splitsHorizontally);
	}

	private Value get(Node r, Point2D key, boolean splitsHorizontally) {
		if (key == null)
			throw new IllegalArgumentException("calls get() with a null key");
		if (r == null)
			return null;
		int cmp;
		if (splitsHorizontally)
			cmp = Point2D.Y_ORDER.compare(key, r.key);
		else
			cmp = Point2D.X_ORDER.compare(key, r.key);
		splitsHorizontally = !splitsHorizontally;
		if (cmp < 0)
			return get(r.left, key, splitsHorizontally);
		else if (cmp > 0)
			return get(r.right, key, splitsHorizontally);
		else
			return r.value;
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
			if (x == null)
				continue;
			keys.enqueue(x.key);
			queue.enqueue(x.left);
			queue.enqueue(x.right);
		}
		return keys;
	}

	public Iterable<Point2D> range(RectHV rect) {
		ArrayList<Point2D> a = new ArrayList<>();
		double[] bounds = new double[] {Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE};
		range(root, rect, a, bounds);
		return a;
	}

	/**
	 * Alternate range method
	 * 
	 * @param r
	 * @param qRect
	 * @param matches
	 */
	private void range(Node r, RectHV qRect, ArrayList<Point2D> matches, double[] bounds) {

		double[] bCopy = new double[4];
		for (int i = 0; i < 4; i++)
		{
			bCopy[i] = bounds[i];
		}
		// copy array in case of traveling both subtrees
		
		if (r == null)
			return;
		if (qRect.contains(r.key)) {
			matches.add(r.key);
			RectHV tmp = new RectHV(bounds[0], bounds[1], bounds[2], bounds[3]);
			tmp.draw();
		}
		
		RectHV bRect; 
		
		
		// less than
		if (r.left != null) 
		{
			if (r.isHorizontal) {
				bRect = new RectHV(bounds[0], bounds[1], r.key.x(), bounds[3]); // 
				StdOut.println("left horizontal" + bRect.toString());
				bCopy[2] = r.key.x();
			} else { // is vertically split - child is below
				bRect = new RectHV(bounds[0], bounds[1], bounds[2], r.key.y()); // 
				StdOut.println("left vertical" + bRect.toString()); //debug
				bCopy[3] = r.key.y();
				
			}
			if (qRect.intersects(bRect))
				range(r.left, qRect, matches, bCopy); // go left
		}
		
		bCopy = bounds.clone();
		// greater than
		if (r.right != null) 
		{
			if (r.isHorizontal) {
				bRect = new RectHV(r.key.x(), bounds[1] , bounds[2] , bounds[3]); // 
				StdOut.println("right horizontal" + bRect.toString());
				bounds[0] = r.key.x();
			} else { // is vertically split - child is above
				bRect = new RectHV(bounds[0], r.key.y(), bounds[2], bounds[3]); // 
				StdOut.println("right vertical" + bRect.toString());
				bounds[1] = r.key.y();
				
			}
			if (qRect.intersects(bRect))
				range(r.right, qRect, matches, bounds); // go right
		}

	}

	// private void range(Node r, RectHV rect, ArrayList<Point2D> a) {
	// if(r == null) return;
	// if(rect.contains(r.key)) {
	// a.add(r.key);
	// }
	//
	// if(r.left != null && r.left.min >= r.min && r.left.max <= r.max) {
	// range(r.left, rect, a); // go left
	// }
	//
	// if(r.right != null && r.right.min >= r.min && r.right.max <= r.max) {
	// range(r.right, rect, a); // go right
	// }
	// }

	// a nearest neighbor to point p; null if the symbol table is empty
	public Point2D nearest(Point2D qPoint) {
		Node champ = null;
		return nearest(root, qPoint, champ).key;
	}
	
	private Node nearest(Node n, Point2D qPoint, Node champ) {

		//check if this point is closer to the query point than the current best (champion)
		if (champ == null || qPoint.distanceTo(champ.key) > qPoint.distanceTo(n.key)) //distanceFrom(n.key,qPoint) < distanceFrom(n.key,champ.key))
			champ = n;
		
		//  qpoint is to the left of node
		if (n.isHorizontal && n.key.x() > qPoint.x()) {
				if (n.left != null)  {champ = nearest(n.left, qPoint, champ);} // check left side
				if (n.right != null) {champ = nearest(n.right, qPoint, champ);} // check right side
				
		}
		// qpoint is to the right
		else {
				if (n.right != null) {champ = nearest(n.right, qPoint, champ);} // check right side
				if (n.left != null)  {champ = nearest(n.left, qPoint, champ);} // check left side
		}
		return champ;
	}
	
	
	
	private double distanceFrom(Point2D a, Point2D b) {
	
		double x1 = a.x();
		double x2 = b.x();
		double y1 = a.y();
		double y2 = b.y();
		
		return Math.sqrt(
				((x2 - x1) * (x2 - x1)) +
				((y2 - y1) * (y2 - y1))
						);   
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner("}, {", "[{", "}]");
		Queue<Point2D> keys = new Queue<>();
		Queue<Node> queue = new Queue<>();
		queue.enqueue(root);
		while (!queue.isEmpty()) {
			Node x = queue.dequeue();
			if (x == null)
				continue;
			sj.add(x.toString());
			keys.enqueue(x.key);
			queue.enqueue(x.left);
			queue.enqueue(x.right);
		}
		return sj.toString();
	}

	public static void main(String[] args) {

		String filename = "input10.txt";
		In in = new In(filename);
		KdTreeST<Integer> kdtree = new KdTreeST<Integer>();
		for (int i = 0; !in.isEmpty(); i++) {
			double x = in.readDouble();
			double y = in.readDouble();
			Point2D p = new Point2D(x, y);
			kdtree.put(p, i);
		}

		StdOut.println(kdtree.points());
	}
}