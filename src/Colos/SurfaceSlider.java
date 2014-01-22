package Colos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.BasicStroke;
import java.awt.Dimension;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import java.awt.RenderingHints;

/**
 * Represents 2-dimensional slider specified by polygon and X and Y axis.
 *
 * Pointer can move on the area specified by the X and Y axis. Axis are
 * specified by their origin and end points. Its a new transformed coordinate system
 * limited by axis's end points. The specified area is a parallelogram
 * with axis as its two sides, the another two sides are parallel to them.
 *
 * Polygon specifing the slider only defines how the slider will look.
 */
public class SurfaceSlider<T> extends Slider {
    protected AdjustableModel<T> xAxisModel_;
    protected AdjustableModel<T> yAxisModel_;

    protected Point2D origin_ = new Point2D.Double(0, 0);

    protected int thickness_ = 11;

    protected double xMax_ = 50;
    protected double yMax_ = 50;

    protected Path2D surface_;

    protected Pointer pointer_;    

    protected double[][] transformMatrix_ = {
            {1, 0},
            {0, 1}
    };

    protected double[][] inverseTransformMatrix_ = {
            {1, 0},
            {0, 1}
    };

    /**
     * Represents pointer of slider
     *
     * Shows current slider value
     * Can be grabed by mouse and moved
     */
    class Pointer extends Ellipse2D.Double {
        protected boolean grabbed_;
        protected Color color_;

        /**
         * Pointer's constructor
         */
        public Pointer() {
            super();

            update();
        }

        /**
         * Draws a pointer on the slider
         *
         * @param g2d
         *     Graphics context in which to paint
         */
        public void draw(Graphics2D g2d) {
            g2d.setPaint(color_);
            g2d.fill(pointer_);
        }

        /**
         * Updates pointer position and color
         */
        public void update() {
            double xRatio = xAxisModel_.ratio();
            double yRatio = yAxisModel_.ratio();

            double radius = (thickness_ / 2.0) - 2;

            double x = xRatio * xMax_;
            double y = yRatio * yMax_;

            double transformedX =
                x * transformMatrix_[0][0]
                + y * transformMatrix_[1][0]
                + origin_.getX();

            double transformedY =
                x * transformMatrix_[0][1]
                + y * transformMatrix_[1][1]
                + origin_.getY();

            setFrame (
                transformedX + 0.5 - radius,
                transformedY + 0.5 - radius,
                2 * radius,
                2 * radius
            );

            if(filler() != null) {
                Color color = filler().getColor((int) transformedX, (int) transformedY);
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

        /**
         * Mark pointer as grabbed
         *
         * It indicates whether the pointer is grabbed by mouse
         *
         * @param grabbed
         *     Grabbed flag
         */
        public void setGrabbed(boolean grabbed) {
            grabbed_ = grabbed;
        }

        /**
         * Indicates whether the pointer is grabbed by mouse
         *
         * @return
         *     True or false wheter the pointer is grabbed
         */
        public boolean grabbed() {
            return grabbed_;
        }
    }

    /**
     * SurfaceSlider's constructor
     *
     * Creates a model with specified models for both axis
     *
     * @param xAxisModel
     *     Model specifying values on X axis
     *
     * @param yAxisModel
     *     Model specifying values on Y axis
     */
    public SurfaceSlider(AdjustableModel<T> xAxisModel, AdjustableModel<T> yAxisModel) {
        xAxisModel_ = xAxisModel;
        yAxisModel_ = yAxisModel;

        setSurface(
                new Point2D.Double(0, 0),
                new Point2D.Double(50, 0),
                new Point2D.Double(50, 50),
                new Point2D.Double(0, 50),
                new Point2D.Double(0, 0)
        );

        addMouseListener(new MouseScaleAdapter());
        addMouseMotionListener(new MouseScaleAdapter());

        xAxisModel_.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        repaint();
                    }
                }
        );

        yAxisModel_.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        repaint();
                    }
                }
        );
    }

    @Override
    public double getAxisRatio(int axisNumber, Point2D point) {
        double ratio = 0;

        double x = point.getX() - origin_.getX();
        double y = point.getY() - origin_.getY();

        if(axisNumber == 0) {
            double transformedX =
                x * inverseTransformMatrix_[0][0]
                + y * inverseTransformMatrix_[1][0];

            ratio = transformedX / xMax_;
            if(ratio > 1) ratio = 1;
            else if(ratio < 0) ratio = 0;

        } else if(axisNumber == 1) {
            double transformedY =
                x * inverseTransformMatrix_[0][1]
                + y * inverseTransformMatrix_[1][1];

            ratio = transformedY / yMax_;
            if(ratio > 1) ratio = 1;
            else if(ratio < 0) ratio = 0;
        }

        return ratio;
    }

    /**
     * Specifies the polygon representing the area of slider
     *
     * @param position
     *     Point from where to start specifing polygon
     *
     * @param vertices
     *     Sequence of points specifing the polygon
     */
    public void setSurface(Point2D position, Point2D... vertices) {
        double halfThickness = thickness_ / 2.0;

        surface_ = new GeneralPath();
        surface_.moveTo((float) (position.getX() + halfThickness), (float) (position.getY()) + halfThickness);
        double xMax = position.getX();
        double yMax = position.getY();

        for(int i = 0; i < vertices.length; ++i) {
            Point2D vertex = vertices[i];
            surface_.lineTo((float) (vertex.getX() + halfThickness), (float) (vertex.getY() + halfThickness));

            if(vertex.getX() > xMax) {
                xMax = vertex.getX();
            }

            if(vertex.getY() > yMax) {
                yMax = vertex.getY();
            }
        }
        surface_.closePath();

        super.setPreferredSize(
            new Dimension(
                (int) Math.round(xMax + thickness_),
                (int) Math.round(yMax + thickness_)
            )
        );

        Point loc = getLocation();
        int x = (int) loc.getX();
        int y = (int) loc.getY();

        super.setBounds(
            x, y,
            (int) Math.round(xMax + thickness_) + 2,
            (int) Math.round(yMax + thickness_) + 2
        );

        if(pointer_ == null) {
            pointer_ = new Pointer();
        }

        repaint();
    }

    /**
     * Sets origin for the axis
     *
     * @param point
     *     Point of origin
     *
     * @see #setXAxisEndPoint
     * @see #setYAxisEndPoint
     * @see SurfaceSlider
     */
    public void setOrigin(Point2D point) {
        origin_ = new Point2D.Double(
                point.getX() + thickness_ / 2,
                point.getY() + thickness_ / 2
        );
    }

    /**
     * Sets end point for X axis
     *
     * X axis's is specified as line between origin and this end point
     *
     * @param point
     *     Axis's end point
     *
     * @see #setOrigin
     * @see SurfaceSlider
     */
    public void setXAxisEndPoint(Point2D point) {
        Point2D endPoint = new Point2D.Double(
                point.getX() + thickness_ / 2,
                point.getY() + thickness_ / 2
        );

        xMax_ = origin_.distance(endPoint);

        double x = (endPoint.getX() - origin_.getX()) / xMax_;
        double y = (endPoint.getY() - origin_.getY()) / xMax_;

        transformMatrix_[0][0] = x;
        transformMatrix_[0][1] = y;

        updateInverseTransformMatrix_();

        repaint();
    }

    /**
     * Sets end point for Y axis
     *
     * Y axis's is specified as line between origin and this end point
     *
     * @param point
     *     Axis's end point
     *
     * @see #setOrigin
     * @see SurfaceSlider
     */
    public void setYAxisEndPoint(Point2D point) {
        Point2D endPoint = new Point2D.Double(
                point.getX() + thickness_ / 2,
                point.getY() + thickness_ / 2
        );

        yMax_ = origin_.distance(endPoint);

        double x = (endPoint.getX() - origin_.getX()) / yMax_;
        double y = (endPoint.getY() - origin_.getY()) / yMax_;

        transformMatrix_[1][0] = x;
        transformMatrix_[1][1] = y;

        updateInverseTransformMatrix_();

        repaint();
    }

    /**
     * Regenerates inverse transform matrix
     */
    private void updateInverseTransformMatrix_() {
        double a = transformMatrix_[0][0];
        double b = transformMatrix_[0][1];
        double c = transformMatrix_[1][0];
        double d = transformMatrix_[1][1];

        double divider = d * a - b * c;

        inverseTransformMatrix_ = new double[][] {
            { d / divider, - b / divider },
            { - c / divider, a / divider }
        };
    }

    /**
     * Gets value on X axis
     *
     * @return
     *     Value of X axis's model
     */
    public T xValue() {
        return xAxisModel_.value();
    }

    /**
     * Gets value on Y axis
     *
     * @return
     *     Value of Y axis's model
     */
    public T yValue() {
        return yAxisModel_.value();
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

        g2d.draw(surface_);
        g2d.fill(surface_);

        pointer_.draw(g2d);
    }
    
    /**
     * Sets thickness of the stroke drawing the slider
     *
     * @param thickness
     *    Thickness of the stroke
     */
    public void setThickness(int thickness) {
        thickness_ = thickness;
    }

    @Override
    public void setLocation(int x, int y) {
        int halfThickness = thickness_ / 2;

        super.setLocation(x - halfThickness, y - halfThickness);
    }

    class MouseScaleAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if(surface_.contains(e.getX(), e.getY())) {
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

        private void adjust_(MouseEvent e) {
            Point2D point = new Point2D.Double(e.getX(), e.getY());

            double xRatio = getAxisRatio(0, point);
            double yRatio = getAxisRatio(1, point);
            xAxisModel_.adjustValue(xRatio);
            yAxisModel_.adjustValue(yRatio);
            repaint();
        }
    }
}
