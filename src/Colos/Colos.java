package Colos;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;

public class Colos extends JFrame {

    private JPanel buttonPanel_;
    private JPanel selectorPanel_;

    private JToggleButton defaultColorSelectorButton_ = null;

    public Colos() {
        initUI_();
    }

    public void registerColorSelector(ColorSelector selector) {
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
}
