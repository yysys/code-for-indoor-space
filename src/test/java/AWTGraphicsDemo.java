import dk.aau.cs.idq.indoorentities.Rect;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by Steven on 2016/7/7.
 */
public class AWTGraphicsDemo extends Frame {

    public AWTGraphicsDemo() {
        super("Java AWT Examples");
        prepareGUI();
    }

//    public static void main(String[] args) {
//        AWTGraphicsDemo awtGraphicsDemo = new AWTGraphicsDemo();
//        awtGraphicsDemo.setVisible(true);
//    }

    private void prepareGUI() {
        setSize(400, 400);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Arc2D.Float arc = new Arc2D.Float(Arc2D.OPEN);
        arc.setFrame(50, 50, 300, 300);
        arc.setAngleStart(180);
        arc.setAngleExtent(-90);
        Rectangle2D.Double frameRect = new Rectangle2D.Double(50, 50, 300, 300);
        Rectangle2D.Double region = new Rectangle2D.Double(30, 80, 100, 40);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.blue);
        g2.draw(arc);
        g2.setColor(Color.red);
        g2.draw(frameRect);

        g2.setColor(Color.black);
        g2.draw(region);
        System.out.println(arc.intersects(region));
        System.out.println(arc.getStartPoint());
    }

    public static Arc2D.Double getCommonPartOfArc(Arc2D.Float arc, Rect rect) {

        double radius = arc.getHeight();
        double centerX = arc.getCenterX();
        double centerY = arc.getCenterY();

        getIntersectionPoints(radius, centerX, centerY, rect, arc.getAngleStart(), arc.getAngleExtent());


        return null;
    }


    public static Object getIntersectionPoints(double radius, double centerX, double centerY, Rect rect, double angleStart, double angleExtent) {


        double w = rect.getWidth() / 2;
        double h = rect.getHeight() / 2;

        double rectangleCenterX = rect.getX1() + w;
        double rectangleCenterY = rect.getY1() + h;

        double dx = Math.abs(centerX - rectangleCenterX);
        double dy = Math.abs(centerY - rectangleCenterY);

        if (dx > (radius + w) || dy > (radius + h))
            return null;

        double circleDistanceX = Math.abs(centerX - rect.getX1() - w);
        double circleDistanceY = Math.abs(centerY - rect.getY1() - h);

        return null;

    }
}