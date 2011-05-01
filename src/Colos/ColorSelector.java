package Colos;

import javax.swing.JPanel;
import javax.swing.BorderFactory;

public abstract class ColorSelector extends JPanel {

    protected String name_;
    protected Color color_;

    public ColorSelector(String name) {
        name_ = name;

        initUI_();

        setBorder(BorderFactory.createTitledBorder(name_));
    }

    public String name() {
        return name_;
    }

    public Color color() {
        return color_;
    }

    public void setColor(Color color) {
        color_ = color;

        updateUI_();
    }

    protected void initUI_() {
        // need override
    }

    protected void updateUI_() {
        // need override
    }

}
