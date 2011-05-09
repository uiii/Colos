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
import java.awt.color.ColorSpace;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Dimension;
import java.awt.Font;

public class HSVColorSelector extends ColorSelector {

    protected CircleSlider<Integer> hueSlider_;

    protected JSpinner hueSpinner_;
    protected JSpinner saturationSpinner_;
    protected JSpinner valueSpinner_;

    protected JTextField hexField_;

    protected JPanel colorIndicator_;

    protected boolean receiveChangeEvent_ = true;

    protected ColorSpace colorSpace_ = ColorSpace.getInstance(ColorSpace.TYPE_HSV); // TODO

    public HSVColorSelector() {
        super("HSV");

        color_ = Color.black;
    }

    @Override
    protected void initUI_() {

        hueSlider_ = new CircleSlider<Integer>(new IntegerModel(0, 359, 1));
        //hueSlider_.setFiller(new LineFiller(Color.black, Color.hue));

        JLayeredPane colorPanel = new JLayeredPane();
        colorPanel.setPreferredSize(new Dimension(200, 200));

        colorPanel.add(hueSlider_, 0);

        hueSlider_.setLocation(7,7);
        hueSlider_.setThickness(10);
        hueSlider_.setSize(186,186);
        hueSlider_.setBackground(Color.black);

        ChangeListener sliderChangeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if(receiveChangeEvent_) {
                        float[] components = color_.getColorComponents(colorSpace_, null);
                        components[0] = hueSlider_.value() / 359f;
                        color_ = new Color(colorSpace_, components, 1);

                        colorChanged_();
                    }
                }
        };

        hueSlider_.addChangeListener(sliderChangeListener);

        JLabel hueLabel = new JLabel("Hue: ");
        hueLabel.setLabelFor(hueSpinner_);
        JLabel saturationLabel = new JLabel("Saturation: ");
        saturationLabel.setLabelFor(saturationSpinner_);
        JLabel valueLabel = new JLabel("Value: ");
        valueLabel.setLabelFor(valueSpinner_);

        SpinnerNumberModel hueModel = new SpinnerNumberModel(0, 0, 359, 1);
        SpinnerNumberModel saturationModel = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
        SpinnerNumberModel valueModel = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);

        hueSpinner_ = new JSpinner(hueModel);
        saturationSpinner_ = new JSpinner(valueModel);
        valueSpinner_ = new JSpinner(saturationModel);

        ChangeListener spinnerChangeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if(receiveChangeEvent_) {
                        color_ = new Color(colorSpace_,
                            new float[] {
                                (Integer) hueSpinner_.getValue() / 359f,
                                (Float) saturationSpinner_.getValue(),
                                (Float) valueSpinner_.getValue()
                            }, 1
                        );

                        colorChanged_();
                    }
                }
        };

        hueSpinner_.addChangeListener(spinnerChangeListener);
        saturationSpinner_.addChangeListener(spinnerChangeListener);
        valueSpinner_.addChangeListener(spinnerChangeListener);
        
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

        JPanel hsvPanel = new JPanel();
        hsvPanel.setLayout(new GridLayout(3, 2, 0, 5));
        //rgbPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        hsvPanel.add(hueLabel);
        hsvPanel.add(hueSpinner_);
        hsvPanel.add(saturationLabel);
        hsvPanel.add(saturationSpinner_);
        hsvPanel.add(valueLabel);
        hsvPanel.add(valueSpinner_);
        
        JPanel hexPanel = new JPanel();
        hexPanel.setLayout(new BoxLayout(hexPanel, BoxLayout.X_AXIS));
        hexPanel.add(hexLabel);
        hexPanel.add(hexField_);

        colorIndicator_ = new JPanel();
        colorIndicator_.setBackground(Color.black);
        colorIndicator_.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.add(hsvPanel);
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

        float[] components = color_.getColorComponents(colorSpace_, null);

        hueSpinner_.setValue(components[0] * 359);
        saturationSpinner_.setValue(components[1]);
        valueSpinner_.setValue(components[2]);

        hexField_.setText(Integer.toHexString(color_.getRGB()).substring(2, 8));

        colorIndicator_.setBackground(color_);

        receiveChangeEvent_ = true;

        fireStateChanged_();
    }
}
