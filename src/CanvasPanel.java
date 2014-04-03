
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;


class CanvasPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	int width, height;
	ArrayList<ExtrapolatedCurve> curves;
	ArrayList<HermiteCurve> connects;
	int[][] bitmap;
	BufferedImage buf;
	
	CanvasPanel(int w, int h) {
		width = w;
		height = h;
		curves = new ArrayList<ExtrapolatedCurve>();
		connects = new ArrayList<HermiteCurve>();
		
		// initialize bitmap
		bitmap = new int[width][height];
		
		// mouse event
		MouseDispatcher mouseDispatcher = new MouseDispatcher();
		addMouseListener( mouseDispatcher );
		addMouseMotionListener( mouseDispatcher );
		addMouseWheelListener( mouseDispatcher );
		
		repaint();
	}
	
	public void openImage(File file) {
		repaint();
	}
	
	public void repaint(Graphics g) {
		paint(g);
	}

	public void paint(Graphics bg) {
		if( buf == null ) {
			buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			clearBuffer();
			repaint();
		} else {
			bg.drawImage(buf, 0, 0, this);
			
			for(Curve c : curves) {
				c.draw(bg);
			}
			for(Curve c : connects) {
				c.draw(bg);
			}
		}
	}
	
	private void clearBuffer() {
		Graphics g = buf.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
	}
	
	private void drawLine(int x1, int y1, int x2, int y2) {
		Graphics g = buf.getGraphics();
		g.setColor(Color.black);
		g.drawLine(x1, y1, x2, y2);
	}
	
	// create hermite curve which connect c1 and c2
	private HermiteCurve createHermiteCurve(ExtrapolatedCurve c1, ExtrapolatedCurve c2) {
		double dist = new Vector(c2.terminal.x-c1.terminal.x, c2.terminal.y-c1.terminal.y).norm();
		return new HermiteCurve(
					c1.terminal, 
					c1.terminalVector.scalar(dist), 
					c2.terminal, 
					c2.terminalVector.reverse().scalar(dist));
	}
	
	private void addCurve(ArrayList<Point> points) {
		// create extrapolated curve
		ExtrapolatedCurve ec = new ExtrapolatedCurve(points);
		
		// connect terminals of each curve
		for(ExtrapolatedCurve c : curves) {
			connects.add(createHermiteCurve(ec, c));
		}
		
		// add to list
		curves.add(ec);
	}
	
	//
	// Mouse events
	//
	public class MouseDispatcher extends MouseAdapter {
		Point prevPoint;
		ArrayList<Point> points;
		
		MouseDispatcher() {
			prevPoint = null;
			points = new ArrayList<Point>();
		}
		
		public void mouseDragged(MouseEvent e) {
			Point cur = e.getPoint();
			if( prevPoint != null ) {
				drawLine(prevPoint.x, prevPoint.y, cur.x, cur.y);
				points.add(prevPoint);
			}
			prevPoint = cur;
			repaint();
		}
		
		public void mousePressed(MouseEvent e) {
			prevPoint = e.getPoint();
		}
		
		public void mouseReleased(MouseEvent e) {
			points.add(prevPoint);
			if(points.size() >= 2) {
				addCurve(points);
			}
			points.clear();
			prevPoint = null;
			clearBuffer();
			repaint();
		}
	}
}