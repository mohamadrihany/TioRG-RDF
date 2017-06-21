/*
 * The principal class
 */
package fr.uvsq.adam.tiorg;

import fr.uvsq.adam.tiorg.views.MainWindow;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the singleton patterns to provide an application class.
 *
 */
public enum TioRG {
    TIORG_APP;

    private final static Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    private MainWindow mainWindow;

    public MainWindow getMainWindow() { return mainWindow; }

    /**
     * Application main method.
     *
     * @param args command line arguments
     */
    public void run(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true"); //TODO what is the utility of this parameter ?

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }

        mainWindow = new MainWindow();
        java.awt.EventQueue.invokeLater(() -> mainWindow.setVisible(true));
    }

    /**
     * Entry point of the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        TIORG_APP.run(args);
    }
}
