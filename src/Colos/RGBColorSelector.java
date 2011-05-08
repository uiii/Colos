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

class LineFiller extends Filler {
    protected Color startColor_;
    protected Color endColor_;

    public LineFiller(Color startColor, Color endColor) {
        startColor_ = startColor;
        endColor_ = endColor;
    }

    @Override
    public Color getColor(Shape shape, int x, int y) {
        Line2D line = (Line2D) shape;

        Point2D point = new Point2D.Double(x, y);
        Point2D start = line.getP1();
        Point2D end = line.getP2();

        Point2D normalVector = new Point2D.Double(start.getY() - end.getY(), end.getX() - start.getX());

        Point2D middlePoint = new Point2D.Double((start.getX() + end.getX()) / 2.0, (start.getY() + end.getY()) / 2.0);
        Line2D lineAxis = new Line2D.Double(middlePoint, new Point2D.Double(
                    middlePoint.getX() + normalVector.getX(),
                    middlePoint.getY() + normalVector.getY()
        ));

        double halfLength = start.distance(end) / 2.0;

        double middleDist = lineAxis.ptLineDist(point);
        double startDist = start.distance(point);
        double endDist = end.distance(point);

        double ratio;
        if(startDist < endDist) {
            ratio = 1.0/2.0 * (1.0 - middleDist / halfLength);
            if(ratio < 0) ratio = 0;
        } else {
            ratio = 1.0/2.0 * (1 + middleDist / halfLength);
            if(ratio > 1) ratio = 1;
        }

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

public class RGBColorSelector extends ColorSelector {

    protected Color color_;

    protected LineSlider<Integer> redSlider_;
    protected LineSlider<Integer> greenSlider_;
    protected LineSlider<Integer> blueSlider_;

    protected JSpinner redSpinner_;
    protected JSpinner greenSpinner_;
    protected JSpinner blueSpinner_;

    protected JTextField hexField_;

    protected JPanel colorIndicator_;

    class SpinnerChangeListener implements ChangeListener {
            @Override
            public void stateChanged(ChangeEvent e) {
                color_ = new Color(
                    (Integer) redSpinner_.getValue(),
                    (Integer) greenSpinner_.getValue(),
                    (Integer) blueSpinner_.getValue()
                );

                colorChanged_();
            }
    }

    class SliderChangeListener implements ChangeListener {
            @Override
            public void stateChanged(ChangeEvent e) {
                color_ = new Color(
                    redSlider_.value(),
                    greenSlider_.value(),
                    blueSlider_.value()
                );

                colorChanged_();
            }
    }

    public RGBColorSelector() {
        super("RGB");

        color_ = Color.black;
    }

    @Override
    protected void initUI_() {

        redSlider_ = new LineSlider<Integer>(new IntegerModel(0, 255, 1));
        redSlider_.setFiller(new LineFiller(Color.black, Color.red));

        greenSlider_ = new LineSlider<Integer>(new IntegerModel(0, 255, 1));
        greenSlider_.setFiller(new LineFiller(Color.black, Color.green));

        blueSlider_ = new LineSlider<Integer>(new IntegerModel(0, 255, 1));
        blueSlider_.setFiller(new LineFiller(Color.black, Color.blue));

        JLayeredPane colorPanel = new JLayeredPane();
        colorPanel.setPreferredSize(new Dimension(175, 194));

        colorPanel.add(redSlider_, 0);
        colorPanel.add(greenSlider_, 0);
        colorPanel.add(blueSlider_, 0);

        redSlider_.setLocation(10,103);
        redSlider_.setBackground(Color.black);
        redSlider_.setSize(-69,40);

        greenSlider_.setLocation(94,103);
        greenSlider_.setSize(69,40);
        greenSlider_.setBackground(Color.yellow);

        blueSlider_.setLocation(87, 10);
        blueSlider_.setSize(0,-80);
        blueSlider_.setBackground(Color.yellow);

        ChangeListener sliderChangeListener = new SliderChangeListener();
        redSlider_.addChangeListener(sliderChangeListener);
        greenSlider_.addChangeListener(sliderChangeListener);
        blueSlider_.addChangeListener(sliderChangeListener);

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

        ChangeListener spinnerChangeListener = new SpinnerChangeListener();
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
                        System.out.println("action");
                        try {
                            String hex = hexField_.getText();
                            if(hex.length() == 0 || hex.charAt(0) != '#') {
                                hex = "#" + hex;
                            }
                            System.out.println(hex);
                            color_ = Color.decode(hex);
                        } catch(NumberFormatException ex) {
                            System.out.print("Invalid color hex '");
                            System.out.print(hexField_.getText());
                            System.out.println("'");
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

    private void colorChanged_() {
        redSpinner_.setValue(color_.getRed());
        greenSpinner_.setValue(color_.getGreen());
        blueSpinner_.setValue(color_.getBlue());

        redSlider_.setValue(color_.getRed());
        greenSlider_.setValue(color_.getGreen());
        blueSlider_.setValue(color_.getBlue());

        hexField_.setText(Integer.toHexString(color_.getRGB()).substring(2, 8));

        colorIndicator_.setBackground(color_);
    }
}
