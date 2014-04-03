import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;


public class HermiteCurve extends Curve {

	final int STEPS = 64;
	
	public HermiteCurve(Point p0, Vector v0, Point p1, Vector v1) {
		for(int i=0; i<STEPS; i++) {
			double u1 = (double)i / (double)STEPS;
			double u2 = u1*u1;
			double u3 = u1*u1*u1;
			double h1 = 2*u3 - 3*u2 + 1;
			double h2 = -2*u3 + 3*u2;
			double h3 = u3 - 2*u2 + u1;
			double h4 = u3 - u2;
			
			this.points.add(
				new Point(
					(int)Math.floor(h1*p0.x + h2*p1.x + h3*v0.x + h4*v1.x),
					(int)Math.floor(h1*p0.y + h2*p1.y + h3*v0.y + h4*v1.y)
				)
			);
		}
		
		createPixels();
	}
	
	public void draw(Graphics g) {
		Point prev = null;
		g.setColor(Color.yellow);
		for(Point p : points) {
			if(prev != null) {
				g.drawLine(prev.x, prev.y, p.x, p.y);
			}
			prev = p;
		}
	}
}
