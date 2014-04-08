import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;


public class ExtrapolatedCurve extends Curve {
	ArrayList<Point> localSeedPoints;
	ArrayList<Point> globalSeedPoints;
	ArrayList<Point> seedPoints;
	
	Point terminal;
	Vector terminalVector;
	double terminalSlope;
	double extrapolatedCurveLength;
	boolean clockwise;
	
	double curvatureR;
	Vector center;
	
	public ExtrapolatedCurve(ArrayList<Point> points) {
		super(points);

		localSeedPoints = new ArrayList<Point>();
		globalSeedPoints = new ArrayList<Point>();
		center = new Vector();
		
		// initialize local and global seed points
		int step = 10;
		for(int i=pixels.size()-1; i>=0; i-=step) {
			localSeedPoints.add(pixels.get(i));
		}
		step = 20;
		for(int i=pixels.size()-1; i>=0; i-=step) {
			globalSeedPoints.add(pixels.get(i));
		}
		
		// set seed points
		seedPoints = localSeedPoints;
		
		// calculate extrapolate curve
		extrapolateCurve();
		
		// terminal information
		terminal = seedPoints.get(0);
		terminalVector = new Vector(
				terminal.x - seedPoints.get(1).x, 
				terminal.y - seedPoints.get(1).y).normalize();
		System.out.println(terminalVector.x);
		terminalSlope = terminalVector.y / terminalVector.x;
	}
	
	private void extrapolateCurve() {
		Point p0 = seedPoints.get(0), p1 = seedPoints.get(1), p2 = seedPoints.get(2);
		Vector v01 = new Vector(p1.x-p0.x, p1.y-p0.y);
		Vector v12 = new Vector(p2.x-p1.x, p2.y-p1.y);
		Vector v01n = v01.normal();
		double n = seedPoints.size();
		
		curvatureR = (n*Math.pow(v01.norm(), 2d)) / ((n-1)*(v12.innerProduct(v01n)));

		if( curvatureR >= 10000d ) {
			curvatureR = 10000d;
		} else if( curvatureR <= -10000d ) {
			curvatureR = -10000d;
		}
		
		center.x = p0.x + v01n.x*curvatureR;
		center.y = p0.y + v01n.y*curvatureR;
		
		if( curvatureR < 0d ) {
			clockwise = false;
			curvatureR = -curvatureR;
		} else {
			clockwise = true;
		}
		
		extrapolatedCurveLength = 0.5d * Math.PI * curvatureR;
	}

	// returns a projection to extrapolated arc
	public Point getProjectionPoint(Point origin) {
		int cx = (int)Math.round(center.x), cy = (int)Math.round(center.y);
		if( cx == origin.x && cy == origin.y ) {
			return null;
		}
		else {
			Vector tc = new Vector(terminal.x-cx, terminal.y-cy);
			double start, end;
			if(clockwise) {
				start = tc.angle();
				end = tc.angle() + 90d;
			} else {
				start = tc.angle() - 90d;
				end = tc.angle();
			}
			
			Vector dir;
			for(int i=0; i<2; i++) {
				if(i == 0) dir = new Vector(origin.x-cx, origin.y-cy).normalize().scalar(curvatureR);
				else dir = new Vector(origin.x-cx, origin.y-cy).normalize().scalar(curvatureR).reverse();
				
				double dirang = dir.angle();
				
				if( (start <= dirang && end >= dirang) ||
					(start+360d <= dirang && end+360d >= dirang) ||
					(start-360d <= dirang && end-360d >= dirang) )
				{
					return new Point(cx+(int)Math.round(dir.x), cy+(int)Math.round(dir.y));
				}
			}
		}
		return null;
	}

	public void draw(Graphics g) {
		g.setColor(Color.red);
		for(int i=0; i<3; i++) {
			Point p = seedPoints.get(i);
			//g.drawOval(p.x-2, p.y-2, 4, 4);
		}
		/*
		g.setColor(Color.blue);
		g.drawOval((int)(center.x-curvatureR), (int)(center.y-curvatureR), (int)(curvatureR*2), (int)(curvatureR*2));
		 */
		
		Vector tc = new Vector(terminal.x-center.x, terminal.y-center.y);
		double start, end;
		if(clockwise) {
			start = tc.angle();
			end = tc.angle() + 90d;
		} else {
			start = tc.angle() - 90d;
			end = tc.angle();
		}
		g.setColor(Color.blue);
		g.drawArc((int)(center.x-curvatureR), (int)(center.y-curvatureR), (int)(curvatureR*2), (int)(curvatureR*2), -(int)start, -(int)(end-start));

		//g.setColor(Color.red);
		//g.drawLine(terminal.x, terminal.y, terminal.x+(int)(terminalVector.x*60), terminal.y+(int)(terminalVector.y*60));
		super.draw(g);
	}

}
