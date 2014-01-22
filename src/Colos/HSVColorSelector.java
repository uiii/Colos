package Colos;

import java.awt.Color;

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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.Component;
import javax.swing.SwingUtilities;

import java.awt.Shape;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Can be used to select color using HSV color space
 */
public class HSVColorSelector extends ColorSelector {
    protected IntegerModel hueModel_;
    protected IntegerModel saturationModel_;
    protected IntegerModel valueModel_;

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

    /**
     * HSVColorSelector's constructor
     */
    public HSVColorSelector() {
        super("HSV");

        hueModel_ = new IntegerModel(0, 359, 1);
        saturationModel_ = new IntegerModel(0, 100, 1);
        valueModel_ = new IntegerModel(0, 100, 1);

        ChangeListener modelListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(receiveChangeEvent_) {
                    setColor(
                        ColorProcessor.createFromHSV(
                            hueModel_.value(),
                            saturationModel_.value() / 100f,
                            valueModel_.value() / 100f
                        )
                    );
                }
            }
        };

        hueModel_.addChangeListener(modelListener);
        saturationModel_.addChangeListener(modelListener);
        valueModel_.addChangeListener(modelListener);        

        initUI_();
    }

    protected void initUI_() {
        CircleSlider<Integer> hueSlider = new CircleSlider<Integer>(hueModel_);
        hueSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double ratio = slider_.getAxisRatio(0, new Point2D.Double(x, y));

                        return ColorProcessor.createFromHSV(
                                (int) (ratio * 359), 1f, 1f
                        );
                    }
                }
        );
        hueSlider.setLocation(6,6);
        hueSlider.setThickness(11);
        hueSlider.setSize(190,190);
        hueSlider.setBackground(Color.black);

        LineSlider<Integer> saturationSlider = new LineSlider<Integer>(saturationModel_);
        saturationSlider.repaintOnChange(hueModel_);
        saturationSlider.repaintOnChange(valueModel_);
        saturationSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double saturationRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));

                        return ColorProcessor.createFromHSV(
                            hueModel_.value(),
                            (float) saturationRatio,
                            valueModel_.value() / 100f
                        );
                    }
                }
        );
        saturationSlider.setThickness(11);
        saturationSlider.setLocation(19, 102);
        saturationSlider.setSize(-72,-72);

        LineSlider<Integer> valueSlider = new LineSlider<Integer>(valueModel_);
        valueSlider.repaintOnChange(hueModel_);
        valueSlider.repaintOnChange(saturationModel_);
        valueSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double valueRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));

                        return ColorProcessor.createFromHSV(
                            hueModel_.value(),
                            saturationModel_.value() / 100f,
                            (float) valueRatio
                        );
                    }
                }
        );
        valueSlider.setThickness(11);
        valueSlider.setLocation(111, 102);
        valueSlider.setSize(72,-72);

        SurfaceSlider<Integer> saturationValueSlider = new SurfaceSlider<Integer>(saturationModel_, valueModel_);
        saturationValueSlider.repaintOnChange(hueModel_);
        saturationValueSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double saturationRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));
                        double valueRatio = slider_.getAxisRatio(1, new Point2D.Double(x, y));

                        return ColorProcessor.createFromHSV(
                            hueModel_.value(),
                            (float) saturationRatio,
                            (float) valueRatio
                        );
                    }
                }
        );
        saturationValueSlider.setThickness(11);
        saturationValueSlider.setLocation(29,20);
        saturationValueSlider.setSurface(
                new Point2D.Double(72,0),
                new Point2D.Double(144,72),
                new Point2D.Double(72,144),
                new Point2D.Double(0,72)
        );
        saturationValueSlider.setOrigin(new Point2D.Double(72,144));
        saturationValueSlider.setXAxisEndPoint(new Point2D.Double(0,72));
        saturationValueSlider.setYAxisEndPoint(new Point2D.Double(144,72));

        JLayeredPane colorPanel = new JLayeredPane();
        colorPanel.setPreferredSize(new Dimension(205, 205));

        colorPanel.add(hueSlider, 0);
        colorPanel.add(saturationSlider, 0);
        colorPanel.add(valueSlider, 0);
        colorPanel.add(saturationValueSlider, 0);

        JPanel mouseEventLayer = new JPanel();

        MouseEventLayerListener listener = new MouseEventLayerListener(mouseEventLayer, colorPanel);
        mouseEventLayer.addMouseListener(listener);
        mouseEventLayer.addMouseMotionListener(listener);
        mouseEventLayer.addMouseWheelListener(listener);
        mouseEventLayer.setBounds(0, 0, 200, 205);
        mouseEventLayer.setOpaque(false);

        colorPanel.add(mouseEventLayer, new Integer(1));

        SpinnerNumberModel hueSpinnerModel = new SpinnerNumberModel(0, 0, 359, 1){
            public Object getNextValue() {
                if((Integer) getNumber() == 359) {
                    return new Integer(0);
                } else {
                    return super.getNextValue();
                }
            }

            public Object getPreviousValue() {
                if((Integer) getNumber() == 0) {
                    return new Integer(359);
                } else {
                    return super.getPreviousValue();
                }
            }
        };
        SpinnerNumberModel saturationSpinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        SpinnerNumberModel valueSpinnerModel = new SpinnerNumberModel(0, 0, 100, 1);

        synchronizeSpinnerModel(hueModel_, hueSpinnerModel);
        synchronizeSpinnerModel(saturationModel_, saturationSpinnerModel);
        synchronizeSpinnerModel(valueModel_, valueSpinnerModel);

        JSpinner hueSpinner = new JSpinner(hueSpinnerModel);
        JSpinner saturationSpinner = new JSpinner(saturationSpinnerModel);
        JSpinner valueSpinner = new JSpinner(valueSpinnerModel);

        JLabel hueLabel = new JLabel("H : ", JLabel.RIGHT);
        hueLabel.setLabelFor(hueSpinner);
        JLabel saturationLabel = new JLabel("S : ", JLabel.RIGHT);
        saturationLabel.setLabelFor(saturationSpinner);
        JLabel valueLabel = new JLabel("V : ", JLabel.RIGHT);
        valueLabel.setLabelFor(valueSpinner);

        JPanel hsvPanel = new JPanel();
        hsvPanel.setLayout(new GridLayout(3, 2, 0, 5));

        hsvPanel.add(hueLabel);
        hsvPanel.add(hueSpinner);
        hsvPanel.add(saturationLabel);
        hsvPanel.add(saturationSpinner);
        hsvPanel.add(valueLabel);
        hsvPanel.add(valueSpinner);
        
        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(Color.black);
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.black));

        synchronizeColorIndicator(
                    hueModel_,
                    saturationModel_,
                    valueModel_,
                    colorIndicator
        );

        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.add(hsvPanel);
        valuePanel.add(javax.swing.Box.createVerticalStrut(48));
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

    protected void synchronizeColorIndicator(
                    final IntegerModel hueModel,
                    final IntegerModel saturationModel,
                    final IntegerModel valueModel,
                    final JPanel colorIndicator) {
        hueModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(
                            ColorProcessor.createFromHSV(
                                hueModel_.value(),
                                saturationModel_.value() / 100f,
                                valueModel_.value() / 100f
                            )
                        );
                    }
                }
        );

        saturationModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(
                            ColorProcessor.createFromHSV(
                                hueModel_.value(),
                                saturationModel_.value() / 100f,
                                valueModel_.value() / 100f
                            )
                        );
                    }
                }
        );

        valueModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(
                            ColorProcessor.createFromHSV(
                                hueModel_.value(),
                                saturationModel_.value() / 100f,
                                valueModel_.value() / 100f
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

        int hue = ColorProcessor.getHueHSV(color);
        int saturation = Math.round(ColorProcessor.getSaturationHSV(color) * 100);
        int value = Math.round(ColorProcessor.getValueHSV(color) * 100);

        if(hue >= 0) hueModel_.setValue(hue);
        if(value != 0) saturationModel_.setValue(saturation);
        valueModel_.setValue(value);

        receiveChangeEvent_ = true;
    }
}
