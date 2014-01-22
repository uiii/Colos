package Colos;

import java.awt.Color;

import java.awt.Shape;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

/**
 * Slider filler used to fill lines with color on both ends
 */
class LineSliderFiller extends SliderFiller {
    protected Color startColor_;
    protected Color endColor_;

    /**
     * LineSliderFiller's constructor
     *
     * @param startColor
     *     Color to draw on the starting end of line
     * @param endColor
     *     Color to draw on the ending end of line
     */
    public LineSliderFiller(Color startColor, Color endColor) {
        startColor_ = startColor;
        endColor_ = endColor;
    }

    /**
     * Gets color for the pixels on the line
     *
     * Makes a gradient between both end colors
     */
    @Override
    public Color getColor(int x, int y) {
        double ratio = slider_.getAxisRatio(0, new Point2D.Double(x, y));

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
