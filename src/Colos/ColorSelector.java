package Colos;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.BorderFactory;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Can be used to select color
 */
public abstract class ColorSelector extends JPanel implements Changeable {
    protected String name_;
    protected Model<Color> colorModel_;

    protected boolean receiveChangeEvent_ = true;

    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    protected boolean colorSet_ = false;

    /**
     * ColorSelector's constructor
     *
     * @param name
     *     Name of the selector
     */
    public ColorSelector(String name) {
        name_ = name;

        setBorder(BorderFactory.createTitledBorder(name_));
    }

    /**
     * Gets the name of the selector
     */
    public String name() {
        return name_;
    }

    /**
     * Sets model to the selector which stores the selected color
     *
     * @param model
     *     Model which will store selected color
     */
    public void setModel(Model<Color> model) {
        colorModel_ = model;

        colorModel_.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        if(receiveChangeEvent_) {
                            colorChanged_();
                        }
                    }
                }
        );
    }

    protected void setColor(Color color) {
        receiveChangeEvent_ = false;
        colorModel_.setValue(color);
        receiveChangeEvent_ = true;
    }

    protected void colorChanged_() {
        // need override
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    @Override
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged_()
    {
        Object[] listeners = listenerList.getListenerList();
        for(int i = listeners.length - 2; i >= 0; i -=2 ) {
            if(listeners[i] == ChangeListener.class) {
                if(changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }

                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
}
