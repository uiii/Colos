package Colos;

import javax.swing.JPanel;
import java.awt.Shape;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Represents slider components
 */
public abstract class Slider extends JPanel {

    protected SliderFiller filler_;

    /**
     * Slider's constructor
     */
    public Slider() {
        filler_ = null;

        setOpaque(false); // transparent bacground
    }

    /**
     * Gets filler of the slider
     */
    public SliderFiller filler() {
        return filler_;
    }

    /**
     * Sets filler to fill this slider
     */
    public void setFiller(SliderFiller filler) {
        filler_ = filler;

        filler.setSlider(this);
    }

    /**
     * Repaint this slider when object will change
     *
     * @param object
     *     Object to depend on
     */
    public void repaintOnChange(Changeable object) {
        object.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        if(filler_ != null) {
                            filler_.invalidateCache();
                        }

                        repaint();
                    }
                }
        );
    }

    /**
     * Gets the ratio of the point on the slider's coordinate system axis.
     *
     * @param axisNumber
     *     Number of axis.
     * @param point
     *     Specified point
     */
    public abstract double getAxisRatio(int axisNumber, Point2D point);
}
