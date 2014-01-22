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

/**
 * Represents line slider.
 *
 * Line slider is specified by line along which can pointer move.
 */
public class LineSlider<T> extends Slider {

    protected AdjustableModel<T> model_;
    protected Line2D line_;
    protected Pointer pointer_;

    protected int thickness_;
    
    protected int width_;
    protected int height_;

    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    
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
            double radius = (thickness_ / 2.0) - 2;

            double x = line_.getP1().getX() + 0.5 + (ratio * width_);
            double y = line_.getP1().getY() + 0.5 + (ratio * height_);

            setFrame (
                x - radius,
                y - radius,
                2 * radius,
                2 * radius
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

    class MouseScaleAdapter extends MouseAdapter implements MouseWheelListener {
        public void mousePressed(MouseEvent e) {
            if(line_.ptSegDist(e.getX(), e.getY()) <= thickness_ / 2) {
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
                if(line_.ptSegDist(e.getX(), e.getY()) <= thickness_ / 2) {
                    double ratio = model_.ratio();
                    ratio += e.getWheelRotation() * -0.05;
                    model_.adjustValue(ratio);
                    repaint();
                }
            }
        }

        private void adjust_(MouseEvent e) {
            Point2D point = new Point2D.Double(e.getX(), e.getY());
            model_.adjustValue(getAxisRatio(0, point));
            repaint();
        }
    }

    /**
     * LineSlider's constructor
     *
     * @param model
     *     Model spicifying values on slider's axis
     */
    public LineSlider(AdjustableModel<T> model) {
        model_ = model;

        thickness_ = 11;
        setSize(50, 10);

        addMouseListener(new MouseScaleAdapter());
        addMouseMotionListener(new MouseScaleAdapter());
        addMouseWheelListener(new MouseScaleAdapter());

        model_.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        repaint();
                    }
                }
        );
    }

    @Override
    public double getAxisRatio(int axisNumber, Point2D point) {
        if(axisNumber != 0) {
            return 0.0;
        }

        Point2D start = line_.getP1();
        Point2D end = line_.getP2();

        Point2D normalVector = new Point2D.Double(
                start.getY() - end.getY(),
                end.getX() - start.getX()
        );

        Point2D middlePoint = new Point2D.Double(
                (start.getX() + end.getX()) / 2.0,
                (start.getY() + end.getY()) / 2.0
        );

        Line2D lineAxis = new Line2D.Double(middlePoint, new Point2D.Double(
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

        return ratio;
    }

    /**
     * Gets slider's model value
     *
     * @return
     *     Value of slider's model
     */
    public T value() {
        return model_.value();
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

    /**
     * Specifies slider's size whereby specifies slider's line
     *
     * @param width
     *     Width of the slider.
     *     Can be positive of negative - it will define the direction fo the slider.
     *     Negative value means the horizontal direction will be to the left.
     *     Positive value means the horizontal direction will be to the left.
     *
     * @param height
     *     Height of the slider
     *     Can be positive of negative - it will define the direction fo the slider.
     *     Negative value means the vertical direction will be up.
     *     Positive value means the vertical direction will be down.
     */
    public void setSize(int width, int height) {
        int startX;
        int startY;

        int endX;
        int endY;

        width_ = width;
        height_ = height;

        int halfThickness = thickness_ / 2;

        if(width_ > 0) {
            startX = halfThickness;
            endX = width_ + halfThickness;
        } else {
            endX = halfThickness;
            startX = - width_ + halfThickness;
        }

        if(height_ > 0) {
            startY = halfThickness;
            endY = height_ + halfThickness;
        } else {
            endY = halfThickness;
            startY = - height_ + halfThickness;
        }

        if(line_ == null) {
            line_ = new Line2D.Double();
        }

        line_.setLine(
            new Point2D.Double(startX, startY),
            new Point2D.Double(endX, endY)
        );

        super.setPreferredSize(
            new Dimension(
                Math.abs(width_) + (thickness_),
                Math.abs(height_) + (thickness_)
            )
        );

        Point loc = getLocation();
        int x = (int) loc.getX();
        int y = (int) loc.getY();

        super.setBounds(
            x, y,
            Math.abs(width_) + (thickness_),
            Math.abs(height_) + (thickness_)
        );

        if(pointer_ == null) {
            pointer_ = new Pointer();
        }
    }

    /**
     * Set thickes of the stroke which will draw the line
     *
     * @param thickness
     *     Thickness of the stroke
     */
    public void setThickness(int thickness) {
        Point loc = getLocation();
        int x = (int) loc.getX() + thickness_ / 2;
        int y = (int) loc.getY() + thickness_ / 2;

        thickness_ = thickness;

        setLocation(x, y);
        setPreferredSize(new Dimension(width_, height_));
    }

    @Override
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

        g2d.draw(line_);
        g2d.fill(line_);

        pointer_.draw(g2d);
    }
}
