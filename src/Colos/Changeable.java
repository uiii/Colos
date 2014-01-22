package Colos;

import javax.swing.event.ChangeListener;

/**
 * Represents changeable object.
 *
 * It means object will fire change event
 * when it's state changes
 */
public interface Changeable {
    /**
     * Add change listener to the list
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Remove change listener from the list
     */
    public void removeChangeListener(ChangeListener l);
}
