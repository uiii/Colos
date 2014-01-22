package Colos;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Model stores value.
 *
 * Value can be set to the model or retrieved from the model.
 */
public class Model<T> implements Changeable {
    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    protected T value_;

    /**
     * Gets model's current value
     *
     * @return
     *     Model's current value
     */
    public T value() {
        return value_;
    }

    /**
     * Sets values as model's current value
     *
     * @param value
     *     Value to be set
     */
    public void setValue(T value) {
        T oldValue = value_;

        value_ = value;

        if(oldValue == null || ! oldValue.equals(value)) {
            fireStateChanged_();
        }
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
