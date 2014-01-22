package Colos;

import javax.swing.SwingUtilities;

/**
 * Creates new Colos instance and registeres color selectors.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        Colos picker = new Colos();
                        picker.registerColorSelector(new RGBColorSelector());
                        picker.registerColorSelector(new HSVColorSelector());
                        picker.registerColorSelector(new HSLColorSelector());
                        picker.setVisible(true);
                    }
                }
        );
    }
}
