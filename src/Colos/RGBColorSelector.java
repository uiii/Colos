package Colos;

import java.awt.Color;

import java.awt.Component;
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

import javax.swing.SwingUtilities;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Dimension;
import java.awt.Font;

import java.util.Formatter;

public class RGBColorSelector extends ColorSelector {
    protected IntegerModel redModel_;
    protected IntegerModel greenModel_;
    protected IntegerModel blueModel_;

    private class MouseEventLayerListener extends MouseAdapter implements MouseWheelListener {
        private JLayeredPane pane_;
        private Component listenerComponent_;

        public MouseEventLayerListener(Component listenerComponent, JLayeredPane pane) {
            pane_ = pane;
            listenerComponent_ = listenerComponent;
        }

        public void mousePressed(MouseEvent e) {
            dispatchEvent_(e, false);
        }

        public void mouseReleased(MouseEvent e) {
            dispatchEvent_(e, true);
        }

        public void mouseDragged(MouseEvent e) {
            dispatchEvent_(e, true);
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            dispatchEvent_(e, false);
        }

        private void dispatchEvent_(MouseEvent e, boolean toAll) {
            Component[] components = pane_.getComponents();

            for(int i = 0; i < components.length; ++i) {
                if(components[i] == listenerComponent_) continue;

                if(toAll || components[i].getBounds().contains(e.getPoint())) {
                    MouseEvent event =
                            SwingUtilities.convertMouseEvent(
                                    (Component) e.getSource(),
                                    e,
                                    components[i]
                            );

                    components[i].dispatchEvent(event);
                }
            }
        }
    }

    public RGBColorSelector() {
        super("RGB");

        redModel_ = new IntegerModel(0, 255, 1);
        greenModel_ = new IntegerModel(0, 255, 1);
        blueModel_ = new IntegerModel(0, 255, 1);

        ChangeListener modelListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(receiveChangeEvent_) {
                    setColor(
                        new Color(
                            redModel_.value(),
                            greenModel_.value(),
                            blueModel_.value()
                        )
                    );
                }
            }
        };

        redModel_.addChangeListener(modelListener);
        greenModel_.addChangeListener(modelListener);
        blueModel_.addChangeListener(modelListener);

        initUI_();
    }

    protected void initUI_() {
        // SLIDERS
        LineSlider<Integer> redSlider = new LineSlider<Integer>(redModel_);
        redSlider.setFiller(new LineSliderFiller(Color.black, Color.red));
        redSlider.setLocation(18,107);
        redSlider.setBackground(Color.black);
        redSlider.setSize(-68,40);

        LineSlider<Integer> greenSlider = new LineSlider<Integer>(greenModel_);
        greenSlider.setFiller(new LineSliderFiller(Color.black, Color.green));
        greenSlider.setLocation(114,107);
        greenSlider.setSize(68,40);
        greenSlider.setBackground(Color.yellow);

        LineSlider<Integer> blueSlider = new LineSlider<Integer>(blueModel_);
        blueSlider.setFiller(new LineSliderFiller(Color.black, Color.blue));
        blueSlider.setLocation(100, 5);
        blueSlider.setSize(0,-77);
        blueSlider.setBackground(Color.yellow);

        SurfaceSlider<Integer> redBlueSlider = new SurfaceSlider<Integer>(redModel_, blueModel_);
        redBlueSlider.repaintOnChange(greenModel_);
        redBlueSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double redRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));
                        double blueRatio = slider_.getAxisRatio(1, new Point2D.Double(x, y));

                        return new Color(
                            (int) Math.round(redRatio * 255),
                            greenModel_.value(),
                            (int) Math.round(blueRatio * 255)
                        );
                    }
                }
        );
        redBlueSlider.setThickness(11);
        redBlueSlider.setLocation(18, 5);
        redBlueSlider.setSurface(
                new Point2D.Double(0, 47),
                new Point2D.Double(69, 7),
                new Point2D.Double(69, 86),
                new Point2D.Double(0, 126)
        );
        redBlueSlider.setOrigin(new Point2D.Double(69, 86));
        redBlueSlider.setXAxisEndPoint(new Point2D.Double(0, 126));
        redBlueSlider.setYAxisEndPoint(new Point2D.Double(69, 7));

        SurfaceSlider<Integer> greenRedSlider = new SurfaceSlider<Integer>(greenModel_, redModel_);
        greenRedSlider.repaintOnChange(blueModel_);
        greenRedSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double greenRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));
                        double redRatio = slider_.getAxisRatio(1, new Point2D.Double(x, y));

                        return new Color(
                            (int) Math.round(redRatio * 255),
                            (int) Math.round(greenRatio * 255),
                            blueModel_.value()
                        );
                    }
                }
        );
        greenRedSlider.setThickness(11);
        greenRedSlider.setLocation(32, 114);
        greenRedSlider.setSurface(
                new Point2D.Double(0, 41),
                new Point2D.Double(68, 0),
                new Point2D.Double(136, 41),
                new Point2D.Double(68, 82)
        );
        greenRedSlider.setOrigin(new Point2D.Double(68, 0));
        greenRedSlider.setXAxisEndPoint(new Point2D.Double(136, 41));
        greenRedSlider.setYAxisEndPoint(new Point2D.Double(0, 41));

        SurfaceSlider<Integer> blueGreenSlider = new SurfaceSlider<Integer>(blueModel_, greenModel_);
        blueGreenSlider.repaintOnChange(redModel_);
        blueGreenSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double blueRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));
                        double greenRatio = slider_.getAxisRatio(1, new Point2D.Double(x, y));

                        return new Color(
                            redModel_.value(),
                            (int) Math.round(greenRatio * 255),
                            (int) Math.round(blueRatio * 255)
                        );
                    }
                }
        );
        blueGreenSlider.setThickness(11);
        blueGreenSlider.setLocation(113, 5);
        blueGreenSlider.setSurface(
                new Point2D.Double(69, 47),
                new Point2D.Double(0, 7),
                new Point2D.Double(0, 86),
                new Point2D.Double(69, 126)
        );
        blueGreenSlider.setOrigin(new Point2D.Double(0, 86));
        blueGreenSlider.setXAxisEndPoint(new Point2D.Double(0, 7));
        blueGreenSlider.setYAxisEndPoint(new Point2D.Double(69, 126));

        // SPINNERS
        SpinnerNumberModel redSpinnerModel = new SpinnerNumberModel(0, 0, 255, 1);
        SpinnerNumberModel greenSpinnerModel = new SpinnerNumberModel(0, 0, 255, 1);
        SpinnerNumberModel blueSpinnerModel = new SpinnerNumberModel(0, 0, 255, 1);

        synchronizeSpinnerModel(redModel_, redSpinnerModel);
        synchronizeSpinnerModel(greenModel_, greenSpinnerModel);
        synchronizeSpinnerModel(blueModel_, blueSpinnerModel);

        JSpinner redSpinner = new JSpinner(redSpinnerModel);
        JSpinner greenSpinner = new JSpinner(greenSpinnerModel);
        JSpinner blueSpinner = new JSpinner(blueSpinnerModel);

        JLabel redLabel = new JLabel("R : ", JLabel.RIGHT);
        redLabel.setLabelFor(redSpinner);
        JLabel greenLabel = new JLabel("G : ", JLabel.RIGHT);
        greenLabel.setLabelFor(greenSpinner);
        JLabel blueLabel = new JLabel("B : ", JLabel.RIGHT);
        blueLabel.setLabelFor(blueSpinner);

        // COLOR PANEL
        JLayeredPane colorPanel = new JLayeredPane();
        colorPanel.setPreferredSize(new Dimension(200, 205));
        colorPanel.setBackground(new Color(80, 80, 80));
        //colorPanel.setOpaque(true);

        colorPanel.add(redSlider, 0);
        colorPanel.add(greenSlider, 0);
        colorPanel.add(blueSlider, 0);

        colorPanel.add(redBlueSlider, 0);
        colorPanel.add(blueGreenSlider, 0);
        colorPanel.add(greenRedSlider, 0);

        JPanel mouseEventLayer = new JPanel();

        MouseEventLayerListener listener = new MouseEventLayerListener(mouseEventLayer, colorPanel);
        mouseEventLayer.addMouseListener(listener);
        mouseEventLayer.addMouseMotionListener(listener);
        mouseEventLayer.addMouseWheelListener(listener);
        mouseEventLayer.setBounds(0, 0, 200, 205);
        mouseEventLayer.setOpaque(false);

        colorPanel.add(mouseEventLayer, new Integer(1));

        /*JPanel hexagonPanel = new JPanel() {
            public void paint(Graphics g) {
                super.paint(g);

                Graphics2D g2d = (Graphics2D) g;

                setDoubleBuffered(true);
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setStroke(
                        new java.awt.BasicStroke(
                            (float) 7,
                            java.awt.BasicStroke.CAP_ROUND,
                            java.awt.BasicStroke.JOIN_ROUND
                        )
                );

                java.awt.geom.GeneralPath hexagon = new java.awt.geom.GeneralPath();
                hexagon.moveTo(100, -8);
                hexagon.lineTo(193, 45);
                hexagon.lineTo(193, 154);
                hexagon.lineTo(100, 209);
                hexagon.lineTo(7, 154);
                hexagon.lineTo(7, 45);
                hexagon.closePath();

                g2d.draw(hexagon);
            }
        };

        hexagonPanel.setBounds(0, 0, 200, 250);
        hexagonPanel.setOpaque(false);

        colorPanel.add(hexagonPanel, 0);*/

        JTextField hexField = new JTextField(5);
        hexField.setMaximumSize(hexField.getPreferredSize());
        hexField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        hexField.setText("000000");

        JLabel hexLabel = new JLabel("Hex: #");
        hexLabel.setLabelFor(hexField);

        synchronizeHexField(
                    redModel_,
                    greenModel_,
                    blueModel_,
                    hexField
        );

        JPanel rgbPanel = new JPanel();
        rgbPanel.setLayout(new GridLayout(3, 2, 0, 5));
        //rgbPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        rgbPanel.add(redLabel);
        rgbPanel.add(redSpinner);
        rgbPanel.add(greenLabel);
        rgbPanel.add(greenSpinner);
        rgbPanel.add(blueLabel);
        rgbPanel.add(blueSpinner);
        
        JPanel hexPanel = new JPanel();
        hexPanel.setLayout(new BoxLayout(hexPanel, BoxLayout.X_AXIS));
        hexPanel.add(hexLabel);
        hexPanel.add(hexField);

        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(Color.black);
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.black));

        synchronizeColorIndicator(
                    redModel_,
                    greenModel_,
                    blueModel_,
                    colorIndicator
        );

        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.add(rgbPanel);
        valuePanel.add(javax.swing.Box.createVerticalStrut(15));
        valuePanel.add(hexPanel);
        valuePanel.add(javax.swing.Box.createVerticalStrut(15));
        valuePanel.add(colorIndicator);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(colorPanel);
        add(valuePanel);
    }

    protected void synchronizeSpinnerModel(final IntegerModel model, final SpinnerNumberModel spinnerModel) {
        model.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        spinnerModel.setValue(model.value());
                    }
                }
        );

        spinnerModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        model.setValue(spinnerModel.getNumber().intValue());
                    }
                }
        );
    }

    protected void synchronizeHexField(
                    final IntegerModel redModel,
                    final IntegerModel greenModel,
                    final IntegerModel blueModel,
                    final JTextField hexField) {
        redModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        //hexField_.setText(Integer.toHexString(color.getRGB()).substring(2, 8));
                        hexField.setText(
                            toHexString(redModel_.value())
                            + toHexString(greenModel_.value())
                            + toHexString(blueModel_.value())
                        );
                    }
                }
        );

        greenModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        //hexField_.setText(Integer.toHexString(color.getRGB()).substring(2, 8));
                        hexField.setText(
                            toHexString(redModel_.value())
                            + toHexString(greenModel_.value())
                            + toHexString(blueModel_.value())
                        );
                    }
                }
        );

        blueModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        //hexField_.setText(Integer.toHexString(color.getRGB()).substring(2, 8));
                        hexField.setText(
                            toHexString(redModel_.value())
                            + toHexString(greenModel_.value())
                            + toHexString(blueModel_.value())
                        );
                    }
                }
        );

        hexField.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String hex = hexField.getText();

                            if(hex.length() == 0 || hex.charAt(0) != '#') {
                                hex = String.format("#%s", hex);
                            }

                            Color newColor = Color.decode(hex);

                            redModel_.setValue(newColor.getRed());
                            greenModel_.setValue(newColor.getGreen());
                            blueModel_.setValue(newColor.getBlue());
                        } catch(NumberFormatException ex) {
                            System.err.printf("Invalid color hex '%s'\n",
                                hexField.getText());

                            hexField.setText(
                                toHexString(redModel_.value())
                                + toHexString(greenModel_.value())
                                + toHexString(blueModel_.value())
                            );
                        }
                    }
                }
        );
    }

    protected void synchronizeColorIndicator(
                    final IntegerModel redModel,
                    final IntegerModel greenModel,
                    final IntegerModel blueModel,
                    final JPanel colorIndicator) {
        redModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(new Color(
                                    redModel_.value(),
                                    greenModel_.value(),
                                    blueModel_.value()
                                )
                        );
                    }
                }
        );

        greenModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(new Color(
                                    redModel_.value(),
                                    greenModel_.value(),
                                    blueModel_.value()
                                )
                        );
                    }
                }
        );

        blueModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(new Color(
                                    redModel_.value(),
                                    greenModel_.value(),
                                    blueModel_.value()
                                )
                        );
                    }
                }
        );
    }

    protected String toHexString(Integer i) {
        return String.format("%02x", i);
    }

    @Override
    protected void colorChanged_() {
        receiveChangeEvent_ = false;

        Color color = colorModel_.value();

        redModel_.setValue(color.getRed());
        greenModel_.setValue(color.getGreen());
        blueModel_.setValue(color.getBlue());

        receiveChangeEvent_ = true;

        //fireStateChanged_();
    }
}
