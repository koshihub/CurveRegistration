import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class GaussianField {

	final double SIGMA = 20d;
	
	int width, height;
	double[][] field;
	
	public GaussianField(int w, int h) {
		width = w;
		height = h;
		
		field = new double[w][h];
	}
	
	public void calculateField(ArrayList<ExtrapolatedCurve> curves) {
		int J = curves.size();
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				double val = 0d;
				for(ExtrapolatedCurve c : curves) {
					Point proj = c.getProjectionPoint(new Point(x, y));
					double sd = 1d - Math.min(1d, Math.sqrt((c.terminal.x-x)*(c.terminal.x-x) + (c.terminal.y-y)*(c.terminal.y-y)) / c.extrapolatedCurveLength);
					if( proj != null ) {
						double pe = -(double)((proj.x-x)*(proj.x-x) + (proj.y-y)*(proj.y-y)) / (double)(SIGMA*SIGMA);
						double pt = -(double)((c.terminal.x-x)*(c.terminal.x-x) + (c.terminal.y-y)*(c.terminal.y-y)) / (double)(SIGMA*SIGMA);
						//val += Math.exp(pe) * Math.pow(Math.exp(pt), 0.2);
						val += Math.exp(pe) * sd;
					} else {
						double pt = -(double)((c.terminal.x-x)*(c.terminal.x-x) + (c.terminal.y-y)*(c.terminal.y-y)) / (double)(SIGMA*SIGMA);
						//val += Math.pow(Math.exp(pt), 0.2);
					}
				}
				field[x][y] = val / (double)J;
			}
		}
	}
	
	public void draw(BufferedImage buf) {
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				int val = (int)(255d-field[x][y]*255d);
				int col = new Color(val, val, val).getRGB();
				buf.setRGB(x, y, col);
			}
		}
	}
}
