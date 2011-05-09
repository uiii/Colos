package Colos;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.RenderingHints;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class CircleSlider<T> extends Slider {

    protected Model<T> model_;
    protected Ellipse2D ellipse_;
    protected Pointer pointer_;

    protected int thickness_;
    
    protected int width_;
    protected int height_;

    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    
    public CircleSlider(Model<T> model) {
        model_ = model;

        thickness_ = 11;
        setSize(50, 50);

        addMouseListener(new MouseScaleAdapter());
        addMouseMotionListener(new MouseScaleAdapter());
        addMouseWheelListener(new MouseScaleAdapter());

        model_.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        fireStateChanged_();
                    }
                }
        );
    }

    public T value() {
        return model_.value();
    }

    @Override
    public Shape shape() {
        return ellipse_;
    }

    public void setValue(T value) {
        model_.setValue(value);
        repaint();
    }

    @Override
    public void setLocation(int x, int y) {
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
            width_ + thickness_,
            height_ + thickness_
        );

        if(pointer_ == null) {
            pointer_ = new Pointer();
        }
    }
    
    public void setThickness(int thickness) {
        thickness_ = thickness;
        setPreferredSize(new Dimension(width_, height_));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        pointer_.updatePosition();

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

        g2d.setPaint(Color.white);

        g2d.fill(pointer_);
    }

    class Pointer extends Ellipse2D.Double {
        protected boolean grabbed_;

        public Pointer() {
            super();

            updatePosition();
        }

        public void updatePosition() {
            double ratio = model_.ratio();
            double radius = (thickness_ / 2.0) - 2;

            setFrame (
                ellipse_.getX() + 0.5 + (ratio * width_) - radius,
                ellipse_.getY() + 0.5 + (ratio * height_) - radius,
                2 * radius,
                2 * radius
            );
        }

        public void setGrabbed(boolean grabbed) {
            grabbed_ = grabbed;
        }

        public boolean grabbed() {
            return grabbed_;
        }
    }

    class MouseScaleAdapter extends MouseAdapter implements MouseWheelListener {
        public void mousePressed(MouseEvent e) {
            /*if(ellipse_.ptSegDist(e.getX(), e.getY()) <= thickness_) {
                adjust_(e);
                pointer_.setGrabbed(true);
            }*/
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
                /*if(ellipse_.ptSegDist(e.getX(), e.getY()) <= thickness_) {
                    double ratio = model_.ratio();
                    ratio += e.getWheelRotation() * -0.05;
                    model_.adjustValue(ratio);
                    repaint();
                }*/
            }
        }

        private void adjust_(MouseEvent e) {
            /*Point2D point = new Point2D.Double(e.getX(), e.getY());
            Point2D start = ellipse_.getP1();
            Point2D end = ellipse_.getP2();

            Point2D normalVector = new Point2D.Double(
                    start.getY() - end.getY(),
                    end.getX() - start.getX()
            );

            Point2D middlePoint = new Point2D.Double(
                    (start.getX() + end.getX()) / 2.0,
                    (start.getY() + end.getY()) / 2.0
            );

            Ellipse2D lineAxis = new Ellipse2D.Double(middlePoint, new Point2D.Double(
                        middlePoint.getX() + normalVector.getX(),
                        middlePoint.getY() + normalVector.getY()
            ));

            double halfLength = start.distance(end) / 2.0;

            double middleDist = lineAxis.ptLineDist(point);
            double startDist = start.distance(point);
            double endDist = end.distance(point);

            double ratio;
            if(startDist < endDist) {
                ratio = 1.0/2.0 * (1.0 - middleDist / halfLength);
                if(ratio < 0) ratio = 0;
            } else {
                ratio = 1.0/2.0 * (1 + middleDist / halfLength);
                if(ratio > 1) ratio = 1;
            }

            model_.adjustValue(ratio);
            repaint();*/
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
