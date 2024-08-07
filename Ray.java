import java.awt.Color;

public class Ray {
    private Vector p1 = new Vector(), p2 = new Vector();
    private double direction;
    private Color color;
    private double distance2 = 0;
    private boolean success = true;
    private Object origBounceOff;
    private Vector origBounceOffPos;
    
    // Ray() { p1 = new Vector(); p2 = new Vector(); direction = 0; }
    // Ray(Vector from, Vector to) { p1 = from; p2 = to; direction = p1.lookAt(p2); }
    Ray(Vector origin, double angle, boolean Collisions, Object[] objects, Vector region) { reCast(origin, angle, Collisions, objects, region); }

    public void reCast(Vector origin, double angle, boolean Collisions, Object[] objects, Vector region) {
        p1 = origin;
        p2 = origin;
        direction = angle;
        distance2 = 0;
        double distance = 0;
        boolean pointIntersects = false;
        boolean outOfBounds = false;
        int maxLength = 2000;
        int bouncesRemaining = 5;
        while (!pointIntersects) {
            distance += 5;
            distance2 += 5;
            p2 = p1.move(0, distance, angle);
            if (Collisions)
                for (Object object : objects)
                    if (object.pointIntersects(p2)) {
                        pointIntersects = true;
                        color = object.color;
                        if (object.reflectivity > 0 && bouncesRemaining > 0) {
                            while (object.pointIntersects(p2.move(0,1,angle))) {
                                distance--;
                                distance2--;
                                p2 = p1.move(0, distance, angle);
                            }
                            if (origBounceOff == null) {
                                origBounceOff = object;
                                origBounceOffPos = p2;
                            }
                            pointIntersects = false;
                            angle = object.reflect(p2, angle);
                            p1 = p2;
                            distance = 0;
                            bouncesRemaining--;
                        }
                        break;
                    }
            if (distance > maxLength) {
                color = Color.BLACK;
                success = false;
                return;
            }
            // if (p2.X() < 0 || p2.Y() < 0 || p2.X() > region.X() || p2.Y() > region.Y())
            //     outOfBounds = true;
            // if (outOfBounds)
            //     break;
        }
        if (Collisions && !outOfBounds)
        while (pointIntersects) {
            distance -= 1;
            distance2 -= 1;
            p2 = p1.move(0, distance, angle);
            for (Object object : objects) {
                pointIntersects = object.pointIntersects(p2);
                if (pointIntersects) {
                    color = object.color;
                    break;  
                }
            }
            // if (p2.X() < 0 || p2.Y() < 0 || p2.X() > region.X() || p2.Y() > region.Y())
            //     outOfBounds = true;
        }
        distance = distance2;
    }
    
    public Vector origin() { return p1; }
    public Vector collision() { return p2; }
    public double direction() { return direction; }
    public Color color() { return color; }
    public double distance() { return distance2; }
    public boolean success() { return success; }
    public Object origBounceOff() { return origBounceOff; }
    public Vector origBounceOffPos() { return origBounceOffPos; }
}