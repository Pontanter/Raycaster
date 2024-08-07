import java.awt.Dimension;

public class Vector {
    private double X,Y;

    Vector(double X, double Y){ this.X = X; this.Y = Y; }
    Vector() { X = 0; Y = 0; }

    public double X() { return X; }
    public double Y() { return Y; }
    public float Xf() { return (float) X; }
    public float Yf() { return (float) Y; }
    public int Xi() { return (int) X; }
    public int Yi() { return (int) Y; }
    public String Xs() { return String.valueOf(X); }
    public String Ys() { return String.valueOf(Y); }
    
    public Vector add(Vector other) { return new Vector(X() + other.X(), Y() + other.Y()); }
    public Vector sub(Vector other) { return new Vector(X() - other.X(), Y() - other.Y()); }
    public Vector mul(double scalar) { return new Vector(X() * scalar, Y() * scalar); }
    public Vector div(double scalar) { return new Vector(X() / scalar, Y() / scalar); }
    
    public double lookAt(Vector other) { return Math.atan2(other.Y() - Y(), other.X() - X()); }
    public double magnitude() { return Math.sqrt(X() * X() + Y() * Y()); }
    public Vector removeAxis(int axis) { return axis == 0? new Vector(0, Y()) : axis == 1? new Vector(X(), 0) : clone(); }
    public Vector normalize() { return this.div(this.magnitude()); }
    public Vector clone() { return new Vector(X(), Y()); }
    public Vector move(double X, double Y, double direction) {
        double newX = X * Math.cos(direction) - Y * Math.sin(direction);
        double newY = X * Math.sin(direction) + Y * Math.cos(direction);
        return add(new Vector(newX, newY));
    }
    public Vector angle(double angle) {
        double newX = X() * Math.cos(angle) - Y() * Math.sin(angle);
        double newY = X() * Math.sin(angle) + Y() * Math.cos(angle);
        return new Vector(newX, newY);
    }

    @Override
    public String toString() { return "Vector("+Xs()+", "+Ys()+")"; }

    public boolean equals(Vector other) { return X() == other.X() && Y() == other.Y(); }
    
    public Dimension Dimension() { return new Dimension(Xi(), Yi()); } /* convert vector into a disappointment */

    public static double distance(Vector a, Vector b) { return a.sub(b).magnitude(); }
}