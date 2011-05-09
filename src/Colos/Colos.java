package Colos;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.LinkedList;
import java.util.ListIterator;

public class Colos extends JFrame {

    protected Color color_;

    protected JPanel buttonPanel_;
    protected JPanel selectorPanel_;

    protected LinkedList<ColorSelector> selectors_ = new LinkedList<ColorSelector>();

    protected JToggleButton defaultColorSelectorButton_ = null;

    protected boolean receiveChangeEvent_ = true;

    protected class SelectorChangeListener implements ChangeListener {
            protected ColorSelector selector_;

            public SelectorChangeListener(ColorSelector selector) {
                selector_ = selector;
            }

            public void stateChanged(ChangeEvent e) {
                if(receiveChangeEvent_) {
                    color_ = selector_.color();

                    colorChanged_();
                }
            }
    }

    public Colos() {
        initUI_();
    }

    public void registerColorSelector(ColorSelector selector) {
        selectors_.add(selector);

        selector.addChangeListener(new SelectorChangeListener(selector));

        JToggleButton selectorButton = new JToggleButton(selector.name());
        buttonPanel_.add(selectorButton);
        selectorPanel_.add(selector);
        pack();

        if(defaultColorSelectorButton_ == null)
        {
            defaultColorSelectorButton_ = selectorButton;
        }
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
        //setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void colorChanged_() {
        receiveChangeEvent_ = false;

        ListIterator<ColorSelector> it = selectors_.listIterator();
        while(it.hasNext()) {
            it.next().setColor(color_);
        }

        receiveChangeEvent_ = true;
    }
}
