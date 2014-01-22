package Colos;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;

import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.LinkedList;
import java.util.ListIterator;

import java.awt.Toolkit;
import java.awt.Dimension;

/**
 * Represents Colos main class
 */
public class Colos extends JFrame {
    protected JPanel buttonPanel_;
    protected JPanel selectorPanel_;

    protected LinkedList<ColorSelector> selectors_ = new LinkedList<ColorSelector>();

    protected Model<Color> colorModel_;

    protected int visibleSelectors_ = 0;

    protected class SelectorButtonClickListener implements ActionListener {
            protected ColorSelector selector_;
            protected JToggleButton button_;

            public SelectorButtonClickListener(JToggleButton button, ColorSelector selector) {
                button_ = button;
                selector_ = selector;
            }

            public void actionPerformed(ActionEvent e) {
                if(button_.isSelected()) {
                    selector_.setVisible(true);
                    ++visibleSelectors_;
                } else {
                    if(visibleSelectors_ == 1) {
                        button_.setSelected(true);
                    } else {
                        selector_.setVisible(false);
                        --visibleSelectors_;
                    }
                }

                pack();
            }
    }

    /**
     * Colos's constructor
     */
    public Colos() {
        colorModel_ = new Model<Color>();
        colorModel_.setValue(Color.black);

        initUI_();
    }

    /**
     * Registers new color selector.
     *
     * Selector will be shown as and separated panel. Can be shown or hidden
     * by clicking on selector's button.
     *
     * @param selector
     *     Selector to register
     */
    public void registerColorSelector(ColorSelector selector) {
        selector.setModel(colorModel_);
        selector.setVisible(false);

        JToggleButton selectorButton = new JToggleButton(selector.name());
        selectorButton.addActionListener(new SelectorButtonClickListener(selectorButton, selector));
        buttonPanel_.add(selectorButton);

        selectors_.add(selector);
        selectorPanel_.add(selector);

        pack();

        selectorButton.doClick();
    }

    @Override
    public void setVisible(boolean visible) {
        JToggleButton firstButton = (JToggleButton) buttonPanel_.getComponent(0);
        if(visible && ! firstButton.isSelected()) {
            firstButton.doClick();
        }

        super.setVisible(visible);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }

    private void initUI_() {
        buttonPanel_ = new JPanel();
        selectorPanel_ = new JPanel();

        buttonPanel_.setLayout(new BoxLayout(buttonPanel_, BoxLayout.X_AXIS));
        selectorPanel_.setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(buttonPanel_);
        panel.add(selectorPanel_);

        getContentPane().add(panel);
        setLayout(new FlowLayout());
        
        setTitle("Colos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
