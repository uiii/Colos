package Colos;

import java.awt.GridLayout;
import javax.swing.BoxLayout;

import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTextField;

import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Dimension;
import java.awt.Font;

public class HSVColorSelector extends ColorSelector {

    protected CircleSlider<Integer> hueSlider_;

    protected JSpinner redSpinner_;
    protected JSpinner greenSpinner_;
    protected JSpinner blueSpinner_;

    protected JTextField hexField_;

    protected JPanel colorIndicator_;

    protected boolean receiveChangeEvent_ = true;

    public HSVColorSelector() {
        super("HSV");

        color_ = Color.black;
    }

    @Override
    protected void initUI_() {

        hueSlider_ = new CircleSlider<Integer>(new IntegerModel(0, 255, 1));
        //hueSlider_.setFiller(new LineFiller(Color.black, Color.red));

        JLayeredPane colorPanel = new JLayeredPane();
        colorPanel.setPreferredSize(new Dimension(175, 194));

        colorPanel.add(hueSlider_, 0);

        hueSlider_.setLocation(10,103);
        hueSlider_.setBackground(Color.black);
        //hueSlider_.setSize(-69,40);

        ChangeListener sliderChangeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    /*if(receiveChangeEvent_) {
                        color_ = new Color(
                            redSlider_.value(),
                            greenSlider_.value(),
                            blueSlider_.value()
                        );

                        colorChanged_();
                    }*/
                }
        };

        hueSlider_.addChangeListener(sliderChangeListener);

        JLabel redLabel = new JLabel("Red: ");
        redLabel.setLabelFor(redSpinner_);
        JLabel greenLabel = new JLabel("Green: ");
        greenLabel.setLabelFor(greenSpinner_);
        JLabel blueLabel = new JLabel("Blue: ");
        blueLabel.setLabelFor(blueSpinner_);

        SpinnerNumberModel redModel = new SpinnerNumberModel(0, 0, 255, 1);
        SpinnerNumberModel greenModel = new SpinnerNumberModel(0, 0, 255, 1);
        SpinnerNumberModel blueModel = new SpinnerNumberModel(0, 0, 255, 1);

        redSpinner_ = new JSpinner(redModel);
        greenSpinner_ = new JSpinner(blueModel);
        blueSpinner_ = new JSpinner(greenModel);

        ChangeListener spinnerChangeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if(receiveChangeEvent_) {
                        color_ = new Color(
                            (Integer) redSpinner_.getValue(),
                            (Integer) greenSpinner_.getValue(),
                            (Integer) blueSpinner_.getValue()
                        );

                        colorChanged_();
                    }
                }
        };

        redSpinner_.addChangeListener(spinnerChangeListener);
        greenSpinner_.addChangeListener(spinnerChangeListener);
        blueSpinner_.addChangeListener(spinnerChangeListener);
        
        JLabel hexLabel = new JLabel("Hex: #");
        hexLabel.setLabelFor(hexField_);
        hexField_ = new JTextField(6);
        hexField_.setMaximumSize(hexField_.getPreferredSize());
        hexField_.setFont(new Font("Monospaced", Font.PLAIN, 12));
        hexField_.setText("000000");

        hexField_.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String hex = hexField_.getText();

                            if(hex.length() == 0 || hex.charAt(0) != '#') {
                                hex = String.format("#%s", hex);
                            }

                            color_ = Color.decode(hex);
                        } catch(NumberFormatException ex) {
                            System.err.printf("Invalid color hex '%s'",
                                hexField_.getText());
                        }

                        colorChanged_();
                    }
                }
        );

        JPanel rgbPanel = new JPanel();
        rgbPanel.setLayout(new GridLayout(3, 2, 0, 5));
        //rgbPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        rgbPanel.add(redLabel);
        rgbPanel.add(redSpinner_);
        rgbPanel.add(greenLabel);
        rgbPanel.add(greenSpinner_);
        rgbPanel.add(blueLabel);
        rgbPanel.add(blueSpinner_);
        
        JPanel hexPanel = new JPanel();
        hexPanel.setLayout(new BoxLayout(hexPanel, BoxLayout.X_AXIS));
        hexPanel.add(hexLabel);
        hexPanel.add(hexField_);

        colorIndicator_ = new JPanel();
        colorIndicator_.setBackground(Color.black);
        colorIndicator_.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.add(rgbPanel);
        valuePanel.add(javax.swing.Box.createVerticalStrut(15));
        valuePanel.add(hexPanel);
        valuePanel.add(javax.swing.Box.createVerticalStrut(15));
        valuePanel.add(colorIndicator_);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(colorPanel);
        add(valuePanel);
    }

    @Override
    protected void colorChanged_() {
        receiveChangeEvent_ = false;

        redSpinner_.setValue(color_.getRed());
        greenSpinner_.setValue(color_.getGreen());
        blueSpinner_.setValue(color_.getBlue());

        hexField_.setText(Integer.toHexString(color_.getRGB()).substring(2, 8));

        colorIndicator_.setBackground(color_);

        receiveChangeEvent_ = true;

        fireStateChanged_();
    }
}
