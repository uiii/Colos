package Colos;

import javax.swing.JPanel;
import java.awt.Shape;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public abstract class Slider extends JPanel {

    public SliderFiller filler_;

    public Slider() {
        filler_ = null;

        setOpaque(false); // transparent bacground
    }

    public SliderFiller filler() {
        return filler_;
    }

    public void setFiller(SliderFiller filler) {
        filler_ = filler;

        filler.setSlider(this);
    }

    public void repaintOnChange(Changeable object) {
        object.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        if(filler_ != null) {
                            //filler_.invalidateCache();
                        }

                        repaint();
                    }
                }
        );
    }

    //public abstract Shape shape();
    public abstract double getAxisRatio(int axisNumber, Point2D point);
}
