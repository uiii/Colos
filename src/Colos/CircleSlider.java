package Colos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.RenderingHints;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class CircleSlider<T> extends Slider {

    protected AdjustableModel<T> model_;
    protected Ellipse2D ellipse_;
    protected Pointer pointer_;

    protected int thickness_;

    protected int x_;
    protected int y_;
    
    protected int width_;
    protected int height_;

    protected double startAngle_ = 0;

    protected Point2D center_;

    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    
    public CircleSlider(AdjustableModel<T> model) {
        model_ = model;

        thickness_ = 11;
        setSize(50, 50);

        addMouseListener(new MouseScaleAdapter());
        addMouseMotionListener(new MouseScaleAdapter());
        addMouseWheelListener(new MouseScaleAdapter());

        model_.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        //fireStateChanged_();
                        repaint();
                    }
                }
        );
    }

    public T value() {
        return model_.value();
    }

    /*@Override
    public Shape shape() {
        return ellipse_;
    }*/

    public void setValue(T value) {
        model_.setValue(value);
        repaint();
    }

    @Override
    public void setLocation(int x, int y) {
        x_ = x;
        y_ = y;

        int halfThickness = thickness_ / 2;

        super.setLocation(x - halfThickness, y - halfThickness);
    }

    @Override
    public void setPreferredSize(Dimension d) {
        setSize((int) d.getWidth(), (int) d.getHeight());
    }

    public void setSize(int width, int height) {
        int startX;
        int startY;

        int endX;
        int endY;

        width_ = width;
        height_ = height;

        int halfThickness = thickness_ / 2;

        startX = halfThickness;
        startY = halfThickness;

        if(ellipse_ == null) {
            ellipse_ = new Ellipse2D.Double();
        }

        ellipse_.setFrame(
            startX, startY,
            width_, height_
        );

        center_ = new Point2D.Double(
                halfThickness + width_ / 2.0,
                halfThickness + height / 2.0
        );

        super.setPreferredSize(
            new Dimension(
                width_ + thickness_,
                height_ + thickness_
            )
        );

        Point loc = getLocation();
        int x = (int) loc.getX();
        int y = (int) loc.getY();

        super.setBounds(
            x, y,
            width_ + thickness_ + 1,
            height_ + thickness_ + 1
        );

        if(pointer_ == null) {
            pointer_ = new Pointer();
        }
    }
    
    public void setThickness(int thickness) {
        thickness_ = thickness;
        setLocation(x_, y_);
        setSize(width_, height_);
    }

    public void setStartAngle(double angle) {
        startAngle_ = angle;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        pointer_.update();

        Graphics2D g2d = (Graphics2D) g;

        setDoubleBuffered(true);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        
        if(filler() != null) {
            g2d.setPaint(filler());
        }
        
        g2d.setStroke(
                new BasicStroke(
                    (float) thickness_,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
                )
        );

        g2d.draw(ellipse_);

        pointer_.draw(g2d);
    }

    class Pointer extends Ellipse2D.Double {
        protected boolean grabbed_;
        protected Color color_;

        public Pointer() {
            super();

            update();
        }

        public void draw(Graphics2D g2d) {
            g2d.setPaint(color_);
            g2d.fill(pointer_);
        }

        public void update() {
            double ratio = model_.ratio();
            double angle = ratio * 2 * Math.PI + startAngle_;

            double ellipseRadius = getRadius(angle);

            double pointerX = center_.getX() + ellipseRadius * Math.cos(angle);
            double pointerY = center_.getY() - ellipseRadius * Math.sin(angle);

            double pointerRadius = (thickness_ / 2.0) - 2;

            setFrame (
                pointerX - pointerRadius,
                pointerY - pointerRadius,
                2 * pointerRadius,
                2 * pointerRadius
            );

            if(filler() != null) {
                Color color = filler().getColor((int) x, (int) y);
                int avg = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                if(avg < 128) {
                    color_ = Color.white;
                } else {
                    color_ = Color.black;
                }
            } else {
                color_ = Color.white;
            }            
        }

        public void setGrabbed(boolean grabbed) {
            grabbed_ = grabbed;
        }

        public boolean grabbed() {
            return grabbed_;
        }
    }

    protected double getRadius(double angle) {
        double ellipseA = width_ / 2;
        double ellipseB = height_ / 2;

        double ellipseRadius = ellipseA * ellipseB
                / Math.sqrt(
                        Math.pow(ellipseB * Math.cos(angle), 2) 
                        + Math.pow(ellipseA * Math.sin(angle), 2) 
                  );

        return ellipseRadius;
    }

    protected double pointToAngle_(Point2D point) {
        double centerDist = center_.distance(point);
        double xAxisDist = 
            new Line2D.Double(
                center_,
                new Point2D.Double(center_.getX() + 1, center_.getY())
            ).ptLineDist(point);
        
        double rawAngle = Math.asin(xAxisDist / centerDist);

        double angle = rawAngle;

        if(point.getY() > center_.getY()) {
            angle = -angle;
        }

        if(point.getX() < center_.getX()) {
            angle = Math.PI - angle;
        } else {
            angle = (2 * Math.PI + angle) % (2 * Math.PI);
        }

        return angle;
    }
    
    public double getAxisRatio(int axisNumber, Point2D point) {
        if(axisNumber != 0) {
            return 0.0;
        }

        return pointToAngle_(point) / (2 * Math.PI);
    }

    class MouseScaleAdapter extends MouseAdapter implements MouseWheelListener {
        public void mousePressed(MouseEvent e) {
            Point2D point = new Point2D.Double(e.getX(), e.getY());
            double dist = center_.distance(point);
            double radius = getRadius(pointToAngle_(point));
            double halfThickness = thickness_ / 2.0;

            if((radius - halfThickness - 2 <= dist)
                    && (dist <= radius + halfThickness + 2)) {
                adjust_(e);
                pointer_.setGrabbed(true);
            }
        }

        public void mouseReleased(MouseEvent e) {
            pointer_.setGrabbed(false);
        }

        public void mouseDragged(MouseEvent e) {
            if(pointer_.grabbed()) {
                adjust_(e);
            }
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                Point2D point = new Point2D.Double(e.getX(), e.getY());
                double dist = center_.distance(point);
                double radius = getRadius(pointToAngle_(point));
                double halfThickness = thickness_ / 2.0;

                if((radius - halfThickness - 2 <= dist)
                        && (dist <= radius + halfThickness + 2)) {
                    double ratio = model_.ratio();
                    ratio += e.getWheelRotation() * -0.02;
                    if(ratio > 1) ratio -= 1;
                    else if(ratio < 0) ratio += 1;
                    model_.adjustValue(ratio);
                    repaint();
                }
            }
        }

        private void adjust_(MouseEvent e) {
            double ratio =
                getAxisRatio(0, new Point2D.Double(e.getX(), e.getY()));

            model_.adjustValue(ratio);
            repaint();
        }
    }

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged_() 
    {
        Object[] listeners = listenerList.getListenerList();
        for(int i = listeners.length - 2; i >= 0; i -=2 ) {
            if(listeners[i] == ChangeListener.class) {
                if(changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }

                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }          
        }
    }
}
