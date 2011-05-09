package Colos;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        Colos picker = new Colos();
                        picker.registerColorSelector(new RGBColorSelector());
                        picker.registerColorSelector(new HSVColorSelector());
                        picker.setVisible(true);
                    }
                }
        );
    }
}
