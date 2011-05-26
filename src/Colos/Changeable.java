package Colos;

import javax.swing.event.ChangeListener;

public interface Changeable {
    public void addChangeListener(ChangeListener l);
    public void removeChangeListener(ChangeListener l);
}
