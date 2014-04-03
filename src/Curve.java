import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class Curve {
	ArrayList<Point> points;
	ArrayList<Point> pixels;
	
	public Curve() {
		this.points = new ArrayList<Point>();
	}
	
	public Curve(ArrayList<Point> points) {
		this.points = new ArrayList<Point>(points);
		createPixels();
	}
	
	protected void createPixels() {
		pixels = new ArrayList<Point>();
		
		Point prev = null;
		for(Point p : points) {
			if(prev != null) {
				Point from, to;
				double slope;
				
				if( Math.abs((double)(prev.y - p.y) / (double)(prev.x - p.x)) <= 1.0 ) {
					slope = (double)(p.y - prev.y) / (double)(p.x - prev.x);
					double y = prev.y;
					if( prev.x < p.x ) {
						for( int x = prev.x; x <= p.x; x++ ) {
							pixels.add(new Point(x, (int)Math.round(y)));
							y += slope;
						}
					} else {
						for( int x = prev.x; x >= p.x; x-- ) {
							pixels.add(new Point(x, (int)Math.round(y)));
							y -= slope;
						}
					}
				} else {
					slope = (double)(p.x - prev.x) / (double)(p.y - prev.y);
					double x = prev.x;
					if( prev.y < p.y ) {
						for( int y = prev.y; y <= p.y; y++ ) {
							pixels.add(new Point((int)Math.round(x), y));
							x += slope;
						}
					} else {
						for( int y = prev.y; y >= p.y; y-- ) {
							pixels.add(new Point((int)Math.round(x), y));
							x -= slope;
						}
					}
				}
			}
			prev = p;
		}
	}
	
	public void draw(Graphics g) {
		Point prev = null;
		g.setColor(Color.black);
		for(Point p : points) {
			if(prev != null) {
				g.drawLine(prev.x, prev.y, p.x, p.y);
			}
			prev = p;
		}
		
		g.setColor(Color.red);
		for(Point p : points) {
			//g.drawOval(p.x-1, p.y-1, 3, 3);
		}
	}
}
