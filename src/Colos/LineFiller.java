package Colos;

import java.awt.Shape;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

class LineFiller extends Filler {
    protected Color startColor_;
    protected Color endColor_;

    public LineFiller(Color startColor, Color endColor) {
        startColor_ = startColor;
        endColor_ = endColor;
    }

    @Override
    public Color getColor(Shape shape, int x, int y) {
        Line2D line = (Line2D) shape;

        Point2D point = new Point2D.Double(x, y);
        Point2D start = line.getP1();
        Point2D end = line.getP2();

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

        Color color = new Color(
            (int)(startColor_.getRed() + 
                ratio * (endColor_.getRed() - startColor_.getRed())),
            (int)(startColor_.getGreen() + 
                ratio * (endColor_.getGreen() - startColor_.getGreen())),
            (int)(startColor_.getBlue() + 
                ratio * (endColor_.getBlue() - startColor_.getBlue())),
            (int)(startColor_.getAlpha() + 
                ratio * (endColor_.getAlpha() - startColor_.getAlpha()))
        );

        return color;
    }
}
