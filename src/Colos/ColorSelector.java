package Colos;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.BorderFactory;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public abstract class ColorSelector extends JPanel {

    protected String name_;
    protected Color color_;

    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    public ColorSelector(String name) {
        name_ = name;

        initUI_();

        setBorder(BorderFactory.createTitledBorder(name_));
    }

    public String name() {
        return name_;
    }

    public Color color() {
        return color_;
    }

    public void setColor(Color color) {
        color_ = color;

        colorChanged_();
    }

    protected void initUI_() {
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
