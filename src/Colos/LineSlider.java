package Colos;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.BasicStroke;

public class LineSlider extends Slider {

    protected double value_;

    protected double min_;
    protected double max_;
    protected double precisionStep_;

    protected Line2D line_;
    
    public LineSlider(double min, double max, double precisionStep) {
        precisionStep_ = precisionStep;

        min_ = Math.round(min / precisionStep_) * precisionStep_;
        max_ = Math.round(max / precisionStep_) * precisionStep_;
        
        value_ = min_;
    }

    public void draw(Graphics g, Point2D.Double start, Point2D.Double end, double width) {
        Graphics2D g2d = (Graphics2D) g;

        line_ = new Line2D.Double(start, end);

        if(filler() != null)
        {
            filler().setShape(line_);
            g2d.setPaint(filler());
        }

        g2d.setStroke(new BasicStroke((float) width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2d.draw(line_);

        g2d.fill(line_);
    }

    public Line2D line() {
        return line_;
    }

    public double value() {
        return value_;
    }

    public void setValue(double value) {
        value_ = Math.round(value / precisionStep_) * precisionStep_;

        if(value_ > max_)
        {
            value_ = max_;
        }
        else if(value_ < min_)
        {
            value_ = min_;
        }
    }

}
