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

public class SurfaceSlider<T> extends Slider {

    protected AdjustableModel<T> xAxisModel_;
    protected AdjustableModel<T> yAxisModel_;

    protected Line2D xAxis_;
    protected Line2D yAxis_;

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
            double xRatio = xAxisModel_.ratio();
            double yRatio = yAxisModel_.ratio();

            double radius = (thickness_ / 2.0) - 2;

            double x = xRatio * xMax_;
            double y = yRatio * yMax_;

            //System.out.printf("\t%s %s\n", x, y);

            double transformedX =
                x * transformMatrix_[0][0]
                + y * transformMatrix_[1][0]
                + origin_.getX();

            double transformedY =
                x * transformMatrix_[0][1]
                + y * transformMatrix_[1][1]
                + origin_.getY();

            //System.out.printf("\t%s %s\n", transformedX, transformedY);

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

        public void setGrabbed(boolean grabbed) {
            grabbed_ = grabbed;
        }

        public boolean grabbed() {
            return grabbed_;
        }
    }

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
                        //fireStateChanged_();
                        repaint();
                    }
                }
        );

        yAxisModel_.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        //fireStateChanged_();
                        repaint();
                    }
                }
        );
    }

    public double getAxisRatio(int axisNumber, Point2D point) {
        double ratio = 0;

        double x = point.getX() - origin_.getX();
        double y = point.getY() - origin_.getY();

        if(axisNumber == 0) {
            double transformedX =
                x * inverseTransformMatrix_[0][0]
                + y * inverseTransformMatrix_[1][0];

            /*System.out.printf("orig x %s\n", x);
            System.out.printf("trans x %s\n", transformedX);*/
            ratio = transformedX / xMax_;
            if(ratio > 1) ratio = 1;
            else if(ratio < 0) ratio = 0;

        } else if(axisNumber == 1) {
            double transformedY =
                x * inverseTransformMatrix_[0][1]
                + y * inverseTransformMatrix_[1][1];

            
            /*System.out.printf("orig y %s\n", y);
            System.out.printf("trans y %s\n", transformedY);
            System.out.printf("max y %s\n", yMax_);*/
            ratio = transformedY / yMax_;
            if(ratio > 1) ratio = 1;
            else if(ratio < 0) ratio = 0;
        }

        return ratio;
    }

    public void setSurface(Point2D position, Point2D... vertices) {
        double halfThickness = thickness_ / 2.0;

        surface_ = new GeneralPath();
        surface_.moveTo((float) (position.getX() + halfThickness), (float) (position.getY()) + halfThickness);
        double xMax = position.getX();
        double yMax = position.getY();

        for(int i = 0; i < vertices.length; ++i) {
            Point2D vertex = vertices[i];
            System.out.println(vertex);
            surface_.lineTo((float) (vertex.getX() + halfThickness), (float) (vertex.getY() + halfThickness));

            if(vertex.getX() > xMax) {
                xMax = vertex.getX();
            }

            if(vertex.getY() > yMax) {
                yMax = vertex.getY();
            }
        }
        surface_.closePath();

        System.out.println(surface_);

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

        System.out.println(getBounds());

        if(pointer_ == null) {
            pointer_ = new Pointer();
        }

        repaint();
    }

    public void setOrigin(Point2D point) {
        origin_ = new Point2D.Double(
                point.getX() + thickness_ / 2,
                point.getY() + thickness_ / 2
        );
    }

    public void setXAxisEndPoint(Point2D point) {
        //System.out.print("set x axis ");
        System.out.print(origin_);
        System.out.println(point);

        Point2D endPoint = new Point2D.Double(
                point.getX() + thickness_ / 2,
                point.getY() + thickness_ / 2
        );

        xAxis_ = new Line2D.Double(origin_, endPoint);

        xMax_ = origin_.distance(endPoint);
        System.out.println(xMax_);

        double x = (endPoint.getX() - origin_.getX()) / xMax_;
        double y = (endPoint.getY() - origin_.getY()) / xMax_;

        transformMatrix_[0][0] = x;
        transformMatrix_[0][1] = y;

        updateInverseTransformMatrix_();

        repaint();
    }

    public void setYAxisEndPoint(Point2D point) {
        Point2D endPoint = new Point2D.Double(
                point.getX() + thickness_ / 2,
                point.getY() + thickness_ / 2
        );

        yAxis_ = new Line2D.Double(origin_, endPoint);

        yMax_ = origin_.distance(endPoint);

        double x = (endPoint.getX() - origin_.getX()) / yMax_;
        double y = (endPoint.getY() - origin_.getY()) / yMax_;

        transformMatrix_[1][0] = x;
        transformMatrix_[1][1] = y;

        updateInverseTransformMatrix_();
        /*System.out.printf("%s %s %s %s\n",
                transformMatrix_[0][0],
                transformMatrix_[0][1],
                transformMatrix_[1][0],
                transformMatrix_[1][1]
        );
        System.out.printf("%s %s %s %s\n",
                inverseTransformMatrix_[0][0],
                inverseTransformMatrix_[0][1],
                inverseTransformMatrix_[1][0],
                inverseTransformMatrix_[1][1]
        );*/

        repaint();
    }

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

    public T xValue() {
        return xAxisModel_.value();
    }

    public T yValue() {
        return yAxisModel_.value();
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

        g2d.draw(surface_);
        g2d.fill(surface_);

        /*if(xAxis_ != null && yAxis_ != null)
        {

        g2d.setStroke(
                new BasicStroke(
                    (float) 1,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
                )
        );

            System.out.println("!!!!!!!!!!!!!!!!!! axis !!!!!!!!!!!!!!!!!!!!!!");
            g2d.draw(xAxis_);
            g2d.fill(xAxis_);
            g2d.draw(yAxis_);
            g2d.fill(yAxis_);
        }*/

        /*g2d.setPaint(Color.white);
        g2d.fill(pointer_);*/

        pointer_.draw(g2d);
    }
    
    public void setThickness(int thickness) {
        thickness_ = thickness;
        // TODO přepočítat rozměry
        //setPreferredSize(new Dimension(width_, height_));
    }

    @Override
    public void setLocation(int x, int y) {
        int halfThickness = thickness_ / 2;

        super.setLocation(x - halfThickness, y - halfThickness);
    }

    class MouseScaleAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            //System.out.println(getAxisRatio(0, new Point2D.Double(e.getX(), e.getY())));
            //System.out.println(getAxisRatio(1, new Point2D.Double(e.getX(), e.getY())));
            /*System.out.print(e.getPoint());
            System.out.printf(" %s\n\n", filler_.getColor(e.getX(), e.getY()));*/

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
