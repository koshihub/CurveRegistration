
public class Vector {
	double x, y;
	
	public Vector() {
		x = y = 0d;
	}
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	

	public double innerProduct(Vector v) {
		return x*v.x + y*v.y;
	}
	
	public double norm() {
		return Math.sqrt(x*x + y*y);
	}
	
	public Vector normal() {
		return new Vector(y/norm(), -x/norm());
	}
	
	public Vector normalize() {
		return new Vector(x/norm(), y/norm());
	}
	
	public Vector reverse() {
		return new Vector(-x, -y);
	}
	
	public Vector scalar(double s) {
		return new Vector(x*s, y*s);
	}
}
