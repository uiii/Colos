package Colos;

import java.awt.GridLayout;
import javax.swing.BoxLayout;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTextField;

import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Dimension;

public class RGBColorSelector extends ColorSelector {

    protected LineSlider redSlider_;

    protected JSpinner redSpinner_;
    protected JSpinner greenSpinner_;
    protected JSpinner blueSpinner_;

    protected JTextField hexField_;

    public RGBColorSelector() {
        super("RGB");
    }

    @Override
    protected void initUI_() {

        redSlider_ = new LineSlider(0, 255, 1);
        redSlider_.setFiller(new Filler() {
                public Color getColor(Shape shape, int x, int y) {
                    Line2D line = (Line2D) shape;

                    Point2D point = new Point2D.Double(x, y);
                    Point2D start = line.getP1();
                    Point2D end = line.getP2();

                    double length = start.distance(end);
                    double dist = start.distance(point);

                    double ratio = dist / length;
                    if(ratio < 0) ratio = 0.0;
                    else if(ratio > 1) ratio = 1.0;

                    Color startColor = Color.black;
                    Color endColor = Color.red;

                    Color color = new Color(
                        (int)(startColor.getRed() + 
                            ratio * (endColor.getRed() - startColor.getRed())),
                        (int)(startColor.getGreen() + 
                            ratio * (endColor.getGreen() - startColor.getGreen())),
                        (int)(startColor.getBlue() + 
                            ratio * (endColor.getBlue() - startColor.getBlue())),
                        (int)(startColor.getAlpha() + 
                            ratio * (endColor.getAlpha() - startColor.getAlpha()))
                    );

                    return color;
                }
            }
        );

        JPanel colorPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                redSlider_.draw(g, new Point2D.Double(15,15), new Point2D.Double(50,50), 15);
            }
        };

        colorPanel.setPreferredSize(new Dimension(65, 0));
        colorPanel.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));

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

        JLabel hexLabel = new JLabel("Hex: ");
        hexLabel.setLabelFor(hexField_);
        hexField_ = new JTextField();

        JPanel rgbPanel = new JPanel();
        rgbPanel.setLayout(new GridLayout(3, 2, 0, 5));
        rgbPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

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

        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.add(rgbPanel);
        valuePanel.add(hexPanel);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(colorPanel);
        add(valuePanel);
    }

}
