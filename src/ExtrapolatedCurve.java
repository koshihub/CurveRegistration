import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;


public class ExtrapolatedCurve extends Curve {
	ArrayList<Point> seedPoints;
	Point terminal;
	Vector terminalVector;
	double terminalSlope;
	
	double curvatureR;
	Vector center;
	
	public ExtrapolatedCurve(ArrayList<Point> points) {
		super(points);
		
		seedPoints = new ArrayList<Point>();
		center = new Vector();
		
		int step = 20;
		for(int i=pixels.size()-1; i>=0; i-=step) {
			seedPoints.add(pixels.get(i));
		}
		
		// terminal information
		terminal = seedPoints.get(0);
		terminalVector = new Vector(terminal.x - seedPoints.get(1).x, terminal.y - seedPoints.get(1).y).normalize();
		terminalSlope = terminalVector.y / terminalVector.x;
		
		extrapolateCurve();
	}
	
	private void extrapolateCurve() {
		Point p0 = seedPoints.get(0), p1 = seedPoints.get(1), p2 = seedPoints.get(2);
		Vector v01 = new Vector(p1.x-p0.x, p1.y-p0.y);
		Vector v12 = new Vector(p2.x-p1.x, p2.y-p1.y);
		Vector v01n = v01.normal();
		double n = seedPoints.size();
		
		curvatureR = (n*Math.pow(v01.norm(), 2d)) / ((n-1)*(v12.innerProduct(v01n)));
		center.x = p0.x + v01n.x*curvatureR;
		center.y = p0.y + v01n.y*curvatureR;
		curvatureR = Math.abs(curvatureR);
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		
		g.setColor(Color.red);
		for(Point p : seedPoints) {
			g.drawOval(p.x-1, p.y-1, 3, 3);
		}
		
		g.setColor(Color.blue);
		g.drawOval((int)(center.x-curvatureR), (int)(center.y-curvatureR), (int)(curvatureR*2), (int)(curvatureR*2));
	}

}
