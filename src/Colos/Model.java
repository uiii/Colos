package Colos;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class Model<T> implements Changeable {
    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    protected T value_;

    public T value() {
        return value_;
    }

    public void setValue(T value) {
        T oldValue = value_;

        value_ = value;

        if(oldValue == null || ! oldValue.equals(value)) {
            fireStateChanged_();
        }
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
