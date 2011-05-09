package Colos;

import javax.swing.JPanel;
import java.awt.Shape;

public abstract class Slider extends JPanel {

    public Filler filler_;

    public Slider() {
        filler_ = null;

        //setOpaque(false); // transparent bacground
    }

    public Filler filler() {
        return filler_;
    }

    public void setFiller(Filler filler) {
        filler_ = filler;

        filler.setShape(shape());
    }

    public abstract Shape shape();
}
