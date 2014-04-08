import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;


public class ExtrapolatedCurve extends Curve {
	final double curvatureRMax = 1000d;
	
	Point[] seedPoints;
	Point terminal;
	Vector terminalVector;
	Arc arc;
	
	public ExtrapolatedCurve(ArrayList<Point> points) {
		super(points);
		seedPoints = new Point[3];

		// terminal information
		terminal = pixels.get(pixels.size()-1);
		
		// calculate extrapolate curve
		double maxR = -1d;
		for(int interval=3, i=0; interval<=20; interval+=2, i++) {
			int tail = pixels.size() - 1;
			Arc newarc = getExtrapolatedArc(
					pixels.get(tail),
					pixels.get(tail - interval),
					pixels.get(tail - interval * 2),
					pixels.size()/interval);

			if( newarc.curvatureR > maxR ) {
				arc = newarc;
				seedPoints[0] = pixels.get(tail);
				seedPoints[1] = pixels.get(tail - interval);
				seedPoints[2] = pixels.get(tail - interval * 2);
				maxR = newarc.curvatureR;
			}
		}
		
		// terminal vector
		terminalVector = new Vector(
				terminal.x - seedPoints[1].x,
				terminal.y - seedPoints[1].y).normalize();
	}
	
	private Arc getExtrapolatedArc(Point p0, Point p1, Point p2, int n) {
		double curvatureR;
		Vector center;
		boolean clockwise;
		
		Vector v01 = new Vector(p1.x-p0.x, p1.y-p0.y);
		Vector v12 = new Vector(p2.x-p1.x, p2.y-p1.y);
		Vector v01n = v01.normal();
		
		curvatureR = (n*Math.pow(v01.norm(), 2d)) / ((n-1)*(v12.innerProduct(v01n)));

		if( curvatureR >= curvatureRMax ) {
			curvatureR = curvatureRMax;
		} else if( curvatureR <= -curvatureRMax ) {
			curvatureR = -curvatureRMax;
		}
		
		center = new Vector(p0.x + v01n.x*curvatureR, p0.y + v01n.y*curvatureR);

		if( curvatureR < 0d ) {
			clockwise = false;
			curvatureR = -curvatureR;
		} else {
			clockwise = true;
		}
		
		return new Arc(curvatureR, center, terminal, clockwise);
	}

	public void draw(Graphics g) {
		g.setColor(Color.red);
		for(int i=0; i<3; i++) {
			Point p = seedPoints[i];
			//g.drawOval(p.x-2, p.y-2, 4, 4);
		}
		/*
		g.setColor(Color.blue);
		g.drawOval((int)(center.x-curvatureR), (int)(center.y-curvatureR), (int)(curvatureR*2), (int)(curvatureR*2));
		 */
		
		arc.draw(g);
		
		//g.setColor(Color.red);
		//g.drawLine(terminal.x, terminal.y, terminal.x+(int)(terminalVector.x*60), terminal.y+(int)(terminalVector.y*60));
		super.draw(g);
	}

	class Arc {
		Point start;
		double curvatureR;
		Vector center;
		boolean clockwise;
		double length;
		
		public Arc(double cr, Vector ce, Point st, boolean cw) {
			curvatureR = cr;
			center = ce;
			start = st;
			clockwise = cw;
			length = 0.5d * Math.PI * curvatureR;
		}
		
		public void draw(Graphics g) {
			Vector tc = new Vector(start.x-center.x, start.y-center.y);
			double startAngle, endAngle;
			if(clockwise) {
				startAngle = tc.angle();
				endAngle = tc.angle() + 90d;
			} else {
				startAngle = tc.angle() - 90d;
				endAngle = tc.angle();
			}
			g.setColor(Color.blue);
			g.drawArc(
					(int)(center.x-curvatureR), 
					(int)(center.y-curvatureR), 
					(int)(curvatureR*2), 
					(int)(curvatureR*2), 
					-(int)startAngle, 
					-(int)(endAngle-startAngle));
		}
		
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
	}
}
