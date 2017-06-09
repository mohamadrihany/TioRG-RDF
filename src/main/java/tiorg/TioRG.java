/*
 * The principal class
 */
package tiorg;

import fr.views.MainWindow;

/**
 *
 * @author houk
 */
public class TioRG {

    /**
     * This method calls the principal window
     * @param args the command line arguments
     * 
     */
    public static void main(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        MainWindow.main();
    }
}
