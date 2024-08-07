import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Font;
import java.awt.AlphaComposite;
import java.awt.geom.Point2D;
import java.awt.LinearGradientPaint;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Main extends JFrame implements KeyListener {
    private JPanel panel;

    private int c_fps;
    private int FPS;

    private Vector Resolution = new Vector(1920, 1080); /* 1360, 768 */
    
    private int FOV = 90;
    private int Detail = 6; /* if this num is not divisible by 2, it does not work, I tried fixing it but to no avail. */

    private int Direction = 90;
    private Vector position = Resolution.div(2);
    private int Size = 35;
    private double Speed = 7.5;
    private int turnSpeed = 3;
    private double Height = -150;
    private double yVel = 0;
    private double Off = 0;
    private double needOff = 0;
    private int jumps = 1;
    private double turnYawVel = 0;
    private int frame;
    private int boostCool = 0;
    private double Delta = 2;
    private Vector velocity = new Vector();

    private Object[] objects = {
        new Object(
            new Vector(Resolution.X()/3+25, Resolution.Y()/2),
            new Vector(200, 50),
            new Color(248, 172, 0)
        ),
        new Object(
            new Vector(Resolution.X()-51, Resolution.Y()/2-150),
            new Vector(50, 150),
            new Color(248, 172, 0)
        ),
        new Object(
            new Vector(Resolution.X()/3+25, Resolution.Y()/2),
            new Vector(50, 200),
            new Color(248, 172, 0)
        ),
        new Object(
            new Vector(0, 0),
            new Vector(50, Resolution.Y()),
            new Color(172, 248, 172)
        ),
        new Object(
            new Vector(Resolution.X()-50, 0),
            new Vector(50, Resolution.Y()),
            new Color(172, 248, 172)
        ),
        new Object(
            new Vector(0, 0),
            new Vector(Resolution.X(), 50),
            new Color(172, 248, 172)
        ),
        new Object(
            new Vector(200, Resolution.Y()-50),
            new Vector(Resolution.X()-200, 50),
            new Color(172, 248, 172)
        )
    };
    private Ray[] rays = new Ray[FOV*Detail];

    private boolean Debug = false;

    private boolean wDown = false;
    private boolean aDown = false;
    private boolean sDown = false;
    private boolean dDown = false;

    private boolean upDown = false;
    private boolean downDown = false;

    private boolean leftDown = false;
    private boolean rightDown = false;

    private int viewMode = 1; /* 0 = raycaster view; 1 = 3D view; 2 = both */

    Main() {
        objects[1].reflectivity = .7;

        objects[2].reflectivity = .7;

        objects[3].reflectivity = .1;

        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                g2D.setColor(Color.BLACK);
                g2D.fillRect(0, 0, Resolution.Xi(), Resolution.Yi());
                if (viewMode == 1 || viewMode == 2) {
                    g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                    g2D.setColor(new Color(0, 172, 248));
                    g2D.fillRect(0, 0, Resolution.Xi(), Resolution.Yi());
                    int i = -1;
                    for (Ray ray : rays) {
                        i++;
                        if (ray != null) {
                            double distance = ray.distance();
                            int height = (int) (Resolution.Y()/distance*200);
                            double width = Resolution.X()/(double)(FOV*Detail);
                            int x = (int) (i*width);
                            int mul = Height < -145? (int) (Math.sin((double)frame/5)*(velocity.magnitude()/2)) : 0;
                            double distX = (((double)x - Resolution.X()/2) / 6.0)/64;
                            int add = (int) (Math.sin((double)frame/(Height < -145? 2.5 : 5))*(Height < -145? velocity.magnitude()/4 : (Height+150)/15)*distX);
                            int add2 = Height < -145? (int) (Math.sin((double)frame/30)*50) : 0;
                            Vector tiltVec = velocity.angle(rad(-Direction)).removeAxis(1);
                            double tiltMag = tiltVec.magnitude();
                            double tilt = tiltVec.X() > 0? -tiltMag : tiltMag;
                            int add3 = tiltMag > 5? (int) (tilt*distX) : 0;
                            int y = (int)((Height+add2)/(distance/150)-(Off+mul+add+add3)) + Resolution.Yi()/2 - height/2;
                            Color color = ray.color();
                            float alpha = 1 - ((float)distance/Resolution.Xf());
                            Point2D start = new Point2D.Float(0, 0);
                            Point2D end = new Point2D.Float(0, Resolution.Yf());
                            float point = (float)(y+height/2)/Resolution.Yf();
                            float[] dist = {0.0f, point < .001f? .001f : point > .999f? .999f : point, 1.0f};
                            Color[] colors = {Color.BLACK, Color.BLACK, new Color(64, 128, 32)};
                            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                            try { g2D.setPaint(new LinearGradientPaint(start, end, dist, colors)); } catch (Exception e) {}
                            g2D.fillRect(x, y+height/2, (int) Math.round(width), Resolution.Yi()*5);
                            if (ray.success()) {
                                g2D.setColor(Color.BLACK);
                                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                                g2D.fillRect(x, y, (int) Math.round(width), height);
                                g2D.setColor(color == null? new Color(172, 172, 172) : color);
                                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha > 1? 1 : alpha < 0? 0 : alpha));
                                g2D.fillRect(x, y, (int) Math.round(width), height);
                            }
                            if (ray.origBounceOff() != null) {
                                double distance2 = Vector.distance(position, ray.origBounceOffPos());
                                int height2 = (int) (Resolution.Y()/distance2*200);
                                int y2 = (int)((Height+add2)/(distance2/150)-(Off+mul+add+add3)) + Resolution.Yi()/2 - height2/2;
                                float alpha2 = (1 - ((float)distance2/Resolution.Xf())) - (float) ray.origBounceOff().reflectivity;
                                alpha2 = alpha2 > 1? 1 : alpha2 < 0? 0 : alpha2;
                                g2D.setColor(ray.origBounceOff().color);
                                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha2));
                                g2D.fillRect(x, y2, (int) Math.round(width), height2);
                            }
                        }
                    }
                }
                if (viewMode == 2)
                    g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
                if (viewMode == 0 || viewMode == 2) {
                    Vector p1 = position.move(0, Size/2, rad(Direction));
                    Vector p2 = position.move(Size/4, -Size/2, rad(Direction));
                    Vector p3 = position.move(0, -Size/3, rad(Direction));
                    Vector p4 = position.move(-Size/4, -Size/2, rad(Direction));
                    Polygon p = new Polygon();
                    p.addPoint((int)p1.X(), (int)p1.Y());
                    p.addPoint((int)p2.X(), (int)p2.Y());
                    p.addPoint((int)p3.X(), (int)p3.Y());
                    p.addPoint((int)p4.X(), (int)p4.Y());
                    g2D.setColor(Color.CYAN);
                    for (Ray ray : rays)
                        if (ray != null)
                            g2D.drawLine(ray.origin().Xi(), ray.origin().Yi(), ray.collision().Xi(), ray.collision().Yi());
                    g2D.setColor(Color.GREEN);
                    for (Object object : objects)
                        if (object != null) {
                            Vector origin = object.origin;
                            if (object.type == 0) {
                                g2D.rotate(object.angle, origin.X(), origin.Y());
                                g2D.drawRect(origin.Xi(), origin.Yi(), object.size.Xi(), object.size.Yi());
                                g2D.rotate(-object.angle, origin.X(), origin.Y());
                            } else if (object.type == 1) {
                                g2D.drawOval(origin.Xi(), origin.Yi(), (int) object.size.magnitude(), (int) object.size.magnitude());
                            }
                        }
                    g2D.setColor(Color.WHITE);
                    g2D.fillPolygon(p);
                }
                if (Debug) {
                    g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                    g2D.setColor(Color.WHITE);
                    g2D.setFont(new Font("Consolas", Font.PLAIN, 20));
                    g2D.drawString("FPS: " + FPS, 20, 40);
                    g2D.drawString("Direction: " + Direction + "\u00B0", 20, 60);
                    g2D.drawString("Position: " + position, 20, 80);
                    g2D.drawString("Resolution: " + Resolution, 20, 100);
                    g2D.drawString("FOV: " + FOV + "\u00B0", 20, 120);
                    g2D.drawString("Height: " + Height, 20, 140);
                    g2D.drawString("Pitch: " + Off, 20, 160);
                    g2D.drawString("Velocity: " + velocity, 20, 180);
                    g2D.drawString("Jumps: " + jumps, 20, 200);
                    g2D.drawString("Frame: " + frame, 20, 220);
                    g2D.drawString("Boost Cool: " + boostCool, 20, 240);
                    g2D.drawString("View Mode: " + viewMode, 20, 260);
                    g2D.drawString("Objects: " + objects.length, 20, 280);
                    g2D.drawString("Detail: " + Detail, 20, 300);
                    g2D.drawString("YVel: " + yVel, 20, 320);
                    g2D.drawString("Delta: " + Delta, 20, 340);
                    g2D.drawString("Loaded Rays: " + rays.length, 20, 360);
                    g2D.drawString("Rays;", 20, 380);
                    for (int i = 0; i < rays.length; i++) {
                        Ray ray = rays[i];
                        String display = "NULL";
                        if (ray != null)
                            display = ray.origin() + " to " + ray.collision();
                        g2D.drawString("#" + i + ": " + display, 40, 400 + i*20);
                    }
                }
                g2D.dispose();
                g.dispose();
            }
        };

        add(panel);
        setUndecorated(true);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Resolution.Dimension());
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(false);
        addKeyListener(this);
        setTitle("Raycasting");
        setVisible(true);

        new Timer(1000/60, e -> {
            for (int i = 0; i < rays.length; i++) {
                double angle = rad((double)Direction - FOV/2 + (double)i/Detail);
                rays[i] = Raycast(position, angle);
            }
            turnYawVel = (turnYawVel + (double) (leftDown? -turnSpeed : rightDown? turnSpeed : 0)) * .75;
            Direction += Math.round(turnYawVel);
            Direction %= 360;
            if (Direction < 0) Direction += 360;
            velocity = velocity.move(dDown? -Speed : aDown? Speed : 0, sDown? -Speed : wDown? Speed : 0, rad(Direction)).mul(.75);
            position = position.add(velocity.removeAxis(0));
            boolean didAllowWallJump = false;
            if (pointInWall(position)) {
                position = position.sub(velocity.removeAxis(0));
                if (!didAllowWallJump) {
                    jumps++;
                    didAllowWallJump = true;
                }
            }
            position = position.add(velocity.removeAxis(1));
            if (pointInWall(position)) {
                position = position.sub(velocity.removeAxis(1));
                if (!didAllowWallJump) {
                    jumps++;
                    didAllowWallJump = true;
                }
            }
            Height += yVel;
            if (Height < -150) {
                yVel *= -.45;
                jumps = 1;
            } else if (Height > -150)
                yVel -= 5;
            needOff += upDown? -50 : downDown? 50 : 0;
            needOff = needOff > 300? 300 : needOff < -300? -300 : needOff;
            Off = Off + (needOff - Off) * .15;
            Object obj = objects[0];
            Object obj2 = objects[2];
            objects[0].angle = rad(frame*5);
            objects[0].origin = new Vector(Resolution.X()/3+25, Resolution.Y()/2).move(obj.size.negate().X()/2, obj.size.negate().Y()/2, obj.angle);
            objects[2].angle = rad(frame*-5);
            objects[2].origin = new Vector(Resolution.X()/1.5, Resolution.Y()/2+25).move(obj2.size.negate().X()/2, obj2.size.negate().Y()/2, obj2.angle);
            panel.repaint();
            c_fps++;
            frame++;
        }).start();

        new Timer(1000, e -> {
            FPS = c_fps;
            Delta = 60.0/(double)FPS;
            c_fps = 0;
            setTitle("Raycasting - " + FPS + " FPS");
        }).start();
    }

    private boolean pointInWall(Vector p) {
        for (Object object : objects)
            if (object != null)
                if (object.pointIntersects(p))
                    return true;
        return false;
    }

    private double rad(double deg) { return deg * Math.PI / 180; }

    private Ray Raycast(Vector origin, double direction) { return new Ray(origin, direction, true, objects, Resolution); }

    public static void main(String[] args) {
        new Main();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                wDown = true;
                break;
            case KeyEvent.VK_A:
                aDown = true;
                break;
            case KeyEvent.VK_S:
                sDown = true;
                break;
            case KeyEvent.VK_D:
                dDown = true;
                break;
            case KeyEvent.VK_UP:
                upDown = true;
                break;
            case KeyEvent.VK_DOWN:
                downDown = true;
                break;
            case KeyEvent.VK_LEFT:
                leftDown = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightDown = true;
                break;
            case KeyEvent.VK_SPACE:
                if (jumps < 1)
                    break;
                yVel = 60;
                jumps--;
                break;
            case KeyEvent.VK_R:
                Height = 0;
                velocity = new Vector();
                turnYawVel = 0;
                needOff = 0;
                position = Resolution.mul(.5);
                Off = 0;
                Direction = 0;
                jumps = 1;
                frame = 0;
                boostCool = 0;
                break;
            case KeyEvent.VK_SHIFT:
                if (boostCool > frame)
                    break;
                velocity = velocity.add(new Vector(0, 145).angle(rad(Direction)));
                boostCool = frame + 30;
                break;
            case KeyEvent.VK_F1:
                Debug = !Debug;
                break;
            case KeyEvent.VK_F2:
                viewMode = (viewMode+1)%3;
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                wDown = false;
                break;
            case KeyEvent.VK_A:
                aDown = false;
                break;
            case KeyEvent.VK_S:
                sDown = false;
                break;
            case KeyEvent.VK_D:
                dDown = false;
                break;
            case KeyEvent.VK_UP:
                upDown = false;
                break;
            case KeyEvent.VK_DOWN:
                downDown = false;
                break;
            case KeyEvent.VK_LEFT:
                leftDown = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightDown = false;
                break;
        }
    }
}