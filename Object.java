import java.awt.Color;

public class Object {
    public Vector origin = new Vector();
    public Vector size = new Vector(150, 50);
    public Color color = Color.GRAY;
    public double angle = 0;
    public double reflectivity = 0;
    public int type = 0; /* 0 square 1 cylinder */

    Object() {}
    Object(Vector origin) { this.origin = origin; }
    Object(Vector origin, Vector size, Color color) {
        this.origin = origin;
        this.size = size;
        this.color = color;
    }

    public boolean pointIntersects(Vector point) {
        if (type == 0) {
            Vector translatedPoint = point.sub(origin).angle(-angle);
            return (translatedPoint.X() >= 0 && translatedPoint.X() <= size.X() &&
                    translatedPoint.Y() >= 0 && translatedPoint.Y() <= size.Y());
        } else if (type == 1) {
            double mag = size.magnitude()/2;
            double distance = Vector.distance(point, origin.add(new Vector(mag, mag)));
            return (distance <= mag);
        }
        return false;
    }
    
    public double reflect(Vector point, double angle) {
        if (type == 0) {
            return angle - 2 * (angle - this.angle);
        } else if (type == 1) {
            return -angle;
        }
        return angle;
    }

    public int sideOf(Vector point) {
        if (type == 0) {
            Vector translatedPoint = point.sub(origin).angle(-angle);
            if (translatedPoint.X() < size.X()/2) return 0;
            else return 1;
        } else if (type == 1) {
            double mag = size.magnitude()/2;
            double distance = Vector.distance(point, origin.add(new Vector(mag, mag)));
            if (distance < mag) return 0;
            else return 1;
        }
        return 0;
    }
}