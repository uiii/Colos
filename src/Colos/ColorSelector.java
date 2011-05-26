package Colos;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.BorderFactory;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public abstract class ColorSelector extends JPanel {
    protected String name_;
    protected Model<Color> colorModel_;

    protected boolean receiveChangeEvent_ = true;

    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    protected boolean colorSet_ = false;

    public ColorSelector(String name) {
        name_ = name;

        setBorder(BorderFactory.createTitledBorder(name_));
    }

    public String name() {
        return name_;
    }

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

    public Color color() {
        return colorModel_.value();
    }

    public void setColor(Color color) {
        receiveChangeEvent_ = false;
        colorModel_.setValue(color);
        receiveChangeEvent_ = true;
        /*colorSet_ = true;
        colorChanged_();
        colorSet_ = false;*/
    }

    protected void update(Color color) {
        // need override
    }

    protected void colorChanged_() {
        // need override
    }

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
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
