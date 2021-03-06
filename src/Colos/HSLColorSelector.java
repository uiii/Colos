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
 * Can be used to select color using HSL color space
 */
public class HSLColorSelector extends ColorSelector {
    protected IntegerModel hueModel_;
    protected IntegerModel saturationModel_;
    protected IntegerModel lightnessModel_;

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
     * HSLColorSelector's constructor
     */
    public HSLColorSelector() {
        super("HSL");

        hueModel_ = new IntegerModel(0, 359, 1);
        saturationModel_ = new IntegerModel(0, 100, 1);
        lightnessModel_ = new IntegerModel(0, 100, 1);

        ChangeListener modelListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(receiveChangeEvent_) {
                    setColor(
                        ColorProcessor.createFromHSL(
                            hueModel_.value(),
                            saturationModel_.value() / 100f,
                            lightnessModel_.value() / 100f
                        )
                    );
                }
            }
        };

        hueModel_.addChangeListener(modelListener);
        saturationModel_.addChangeListener(modelListener);
        lightnessModel_.addChangeListener(modelListener);        

        initUI_();
    }

    protected void initUI_() {
        CircleSlider<Integer> hueSlider = new CircleSlider<Integer>(hueModel_);
        hueSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double ratio = slider_.getAxisRatio(0, new Point2D.Double(x, y));

                        return ColorProcessor.createFromHSL(
                                (int) (ratio * 359), 1f, 0.5f
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
        saturationSlider.repaintOnChange(lightnessModel_);
        saturationSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double saturationRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));

                        return ColorProcessor.createFromHSL(
                            hueModel_.value(),
                            (float) saturationRatio,
                            lightnessModel_.value() / 100f
                        );
                    }
                }
        );
        saturationSlider.setThickness(11);
        saturationSlider.setLocation(19, 102);
        saturationSlider.setSize(-72,-72);

        LineSlider<Integer> lightnessSlider = new LineSlider<Integer>(lightnessModel_);
        lightnessSlider.repaintOnChange(hueModel_);
        lightnessSlider.repaintOnChange(saturationModel_);
        lightnessSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double lightnessRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));

                        return ColorProcessor.createFromHSL(
                            hueModel_.value(),
                            saturationModel_.value() / 100f,
                            (float) lightnessRatio
                        );
                    }
                }
        );
        lightnessSlider.setThickness(11);
        lightnessSlider.setLocation(111, 102);
        lightnessSlider.setSize(72,-72);

        SurfaceSlider<Integer> saturationValueSlider = new SurfaceSlider<Integer>(saturationModel_, lightnessModel_);
        saturationValueSlider.repaintOnChange(hueModel_);
        saturationValueSlider.setFiller(new SliderFiller() {
                    public Color getColor(int x, int y) {
                        double saturationRatio = slider_.getAxisRatio(0, new Point2D.Double(x, y));
                        double lightnessRatio = slider_.getAxisRatio(1, new Point2D.Double(x, y));

                        return ColorProcessor.createFromHSL(
                            hueModel_.value(),
                            (float) saturationRatio,
                            (float) lightnessRatio
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
        colorPanel.add(lightnessSlider, 0);
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
        SpinnerNumberModel lightnessSpinnerModel = new SpinnerNumberModel(0, 0, 100, 1);

        synchronizeSpinnerModel(hueModel_, hueSpinnerModel);
        synchronizeSpinnerModel(saturationModel_, saturationSpinnerModel);
        synchronizeSpinnerModel(lightnessModel_, lightnessSpinnerModel);

        JSpinner hueSpinner = new JSpinner(hueSpinnerModel);
        JSpinner saturationSpinner = new JSpinner(saturationSpinnerModel);
        JSpinner lightnessSpinner = new JSpinner(lightnessSpinnerModel);

        JLabel hueLabel = new JLabel("H : ", JLabel.RIGHT);
        hueLabel.setLabelFor(hueSpinner);
        JLabel saturationLabel = new JLabel("S : ", JLabel.RIGHT);
        saturationLabel.setLabelFor(saturationSpinner);
        JLabel lightnessLabel = new JLabel("L : ", JLabel.RIGHT);
        lightnessLabel.setLabelFor(lightnessSpinner);

        JPanel hsvPanel = new JPanel();
        hsvPanel.setLayout(new GridLayout(3, 2, 0, 5));

        hsvPanel.add(hueLabel);
        hsvPanel.add(hueSpinner);
        hsvPanel.add(saturationLabel);
        hsvPanel.add(saturationSpinner);
        hsvPanel.add(lightnessLabel);
        hsvPanel.add(lightnessSpinner);
        
        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(Color.black);
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.black));

        synchronizeColorIndicator(
                    hueModel_,
                    saturationModel_,
                    lightnessModel_,
                    colorIndicator
        );

        JPanel lightnessPanel = new JPanel();
        lightnessPanel.setLayout(new BoxLayout(lightnessPanel, BoxLayout.Y_AXIS));
        lightnessPanel.add(hsvPanel);
        lightnessPanel.add(javax.swing.Box.createVerticalStrut(48));
        lightnessPanel.add(colorIndicator);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(colorPanel);
        add(lightnessPanel);
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
                    final IntegerModel lightnessModel,
                    final JPanel colorIndicator) {
        hueModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(
                            ColorProcessor.createFromHSL(
                                hueModel_.value(),
                                saturationModel_.value() / 100f,
                                lightnessModel_.value() / 100f
                            )
                        );
                    }
                }
        );

        saturationModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(
                            ColorProcessor.createFromHSL(
                                hueModel_.value(),
                                saturationModel_.value() / 100f,
                                lightnessModel_.value() / 100f
                            )
                        );
                    }
                }
        );

        lightnessModel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        colorIndicator.setBackground(
                            ColorProcessor.createFromHSL(
                                hueModel_.value(),
                                saturationModel_.value() / 100f,
                                lightnessModel_.value() / 100f
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

        int hue = ColorProcessor.getHueHSL(color);
        int saturation = Math.round(ColorProcessor.getSaturationHSL(color) * 100);
        int lightness = Math.round(ColorProcessor.getLightnessHSL(color) * 100);

        if(hue >= 0) hueModel_.setValue(hue);
        if(lightness != 0) saturationModel_.setValue(saturation);
        lightnessModel_.setValue(lightness);

        receiveChangeEvent_ = true;
    }
}
