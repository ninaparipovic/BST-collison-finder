import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) { 
		// check bounds for first quadrant
		if (p2.getX() >= point.getX() && p2.getX() <= x2 && p2.getY() <= point.getY() && p2.getY() >= y1 ){
			if (hasChild(1)) { // if c1 exists, insert into c1, else create a new c1 child
				c1.insert(p2); 
			}
			else c1 = new PointQuadtree<E>(p2, (int) point.getX(),  y1, x2, (int) point.getY()); 
		}
		// check bounds for second quadrant
		else if (p2.getX() >= x1 && p2.getX() <= point.getX() && p2.getY()>= y1 && p2.getY() <= point.getY()){
			if (hasChild(2)) { // if c2 exists, insert into c2, else create a new c2 child
				c2.insert(p2); 
			}
			else c2 = new PointQuadtree<E>(p2, x1,  y1, (int) point.getX(), (int) point.getY()); 
		}
		// check bounds for third quadrant
		else if (p2.getX()>= x1 && (p2.getX() <= point.getX()) && p2.getY() <= y2 && (p2.getY() >= point.getY())){
			if (hasChild(3)) {  // if c3 exists, insert into c3, else create a new c3 child
				c3.insert(p2); }
			else {
				c3 = new PointQuadtree<E>(p2, x1, (int) point.getY(), (int) point.getX(), y2); }
		}
		// check bounds for fourth quadrant
		else if (p2.getX() >= point.getX() && p2.getX() <= x2 && p2.getY() >= point.getY() && p2.getY() <= y2){
			if (hasChild(4)) {  // if c4 exists, insert into c4, else create a new c4 child
				c4.insert(p2); }
			else {
				c4 = new PointQuadtree<E>(p2, (int) point.getX(), (int) point.getY(),  x2,  y2); }
		}
	}
			

	
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		int size = 0;
		if(point != null) {
			size = sizeHelper(1);
		}
		return size;
	}
	/**
	 * Helper method for size()
	 * @param size
	 * @return updated size
	 */
	public int sizeHelper(int size) {
		if (hasChild(1)) c1.sizeHelper(size++);
		if (hasChild(2)) c2.sizeHelper(size++);
		if (hasChild(3)) c3.sizeHelper(size++);
		if (hasChild(4)) c4.sizeHelper(size++);
		return size;
	}
	
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		List<E> list = new ArrayList<E>();
		addToAllPoints(list);
		return list;
	}	
	/**
	 * Helper for allPoints, adding points to list
	 * @param list 
	 */
	public void addToAllPoints(List<E> list) {
		if(point != null)
			list.add(point);
		if (c1 != null) c1.allPoints();
		if (c2 != null) c2.allPoints();
		if (c3 != null) c3.allPoints();
		if (c4 != null) c4.allPoints();
	}
	
	
	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		List<E> pointsInCircle = new ArrayList<E>();
		circleHelper(pointsInCircle, cx, cy, cr);
		return pointsInCircle;
	}

	/**
	 * Helper for findInCircle
	 * @param inCircle 
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 */
	public void circleHelper(List <E> inCircle, double cx, double cy, double cr) {
		// if the circle intersects the rectangle of the point
		if (Geometry.circleIntersectsRectangle( cx,  cy,  cr, x1,  y1,  x2,  y2)) {
			// if the point is within the circle
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) inCircle.add(point);
			//recurse through its children 
				if (c1 != null) c1.circleHelper(inCircle, cx, cy, cr);
				if (c2 != null) c2.circleHelper(inCircle, cx, cy, cr);
				if (c3 != null) c3.circleHelper(inCircle, cx, cy, cr);
				if (c4 != null) c4.circleHelper(inCircle, cx, cy, cr);
		}
	}

}
	
