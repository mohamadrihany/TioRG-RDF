package fr.views;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static tiorg.TioRG.TIORG_APP;

public class MainMenuBar extends JMenuBar {
    private final static Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    public MainMenuBar(MDIDesktopPane desktop) {
        createFileMenu();
        createClusteringMenu();
        createSearchMenu();
        createWindowMenu(desktop);
    }

    private void createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem menuItem = new JMenuItem("New Project");
        menuItem.addActionListener(this::newProjectActionPerformed);
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Open Project");
        menuItem.addActionListener(this::openProjectActionPerformed);
        fileMenu.add(menuItem);
        fileMenu.addSeparator();

        menuItem = new JMenuItem("Save Project");
        menuItem.addActionListener(this::saveProjectActionPerformed);
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Close Project");
        menuItem.addActionListener(this::closeProjectActionPerformed);
        fileMenu.add(menuItem);
        fileMenu.addSeparator();

        JMenuItem exportMenuItem = new JMenuItem("Export Graph");
        exportMenuItem.setEnabled(false);
        exportMenuItem.addActionListener(this::exportProjectActionPerformed);
        fileMenu.add(exportMenuItem);
        fileMenu.addComponentListener(new ComponentAdapter() {
                                          public void componentShown(ComponentEvent evt) {
                                              exportMenuItem.setEnabled(TIORG_APP.getMainWindow().isExportMenuActivated());
                                          }
                                      }
        );

        add(fileMenu);
    }

    private void createClusteringMenu() {
        JMenu clusteringMenu = new JMenu("Clustering");

        JMenuItem menuItem = new JMenuItem("Clustering Parameters");
        menuItem.addActionListener(this::setClusteringParametersActionPerformed);
        clusteringMenu.add(menuItem);

        menuItem = new JMenuItem("Start the Clustering");
        menuItem.addActionListener(this::StartClusteringActionPerformed);
        clusteringMenu.add(menuItem);

        add(clusteringMenu);
    }

    private void createSearchMenu() {
        JMenu searchMenu = new JMenu("Search");

        JMenuItem menuItem = new JMenuItem("Keyword Search");
        menuItem.addActionListener(this::searchActionPerformed);
        searchMenu.add(menuItem);

        menuItem = new JMenuItem("Keyword Search Options");
        menuItem.addActionListener(this::setSearchOptionsActionPerformed);
        searchMenu.add(menuItem);

        add(searchMenu);
    }

    private void createWindowMenu(MDIDesktopPane desktop) {
        add(new WindowMenu(desktop));
    }

    private void newProjectActionPerformed(ActionEvent evt) {
        try {
            if (!TIORG_APP.getMainWindow().closeCurrentProject()) return;

            CreateProjectDialog chooser = new CreateProjectDialog();
            chooser.setVisible(true);
            if (chooser.getDialogResult() == JOptionPane.OK_OPTION) {
                TIORG_APP.getMainWindow().newProject(chooser.getProjectLocation(), chooser.getProjectName(), chooser.getGraphLocation(), chooser.getMoveGraph());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(TIORG_APP.getMainWindow(), ex.getMessage(), "Project creation error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void openProjectActionPerformed(ActionEvent evt) {
        try {
            JFileChooser chooser = new JFileChooser(new File(".").getAbsolutePath());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showDialog(TIORG_APP.getMainWindow(), "Project location");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                TIORG_APP.getMainWindow().openProject(chooser.getSelectedFile());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(TIORG_APP.getMainWindow(), ex.getMessage(), "Project loading error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProjectActionPerformed(ActionEvent evt) {
        TIORG_APP.getMainWindow().saveProject();
    }

    private void closeProjectActionPerformed(ActionEvent evt) {
        TIORG_APP.getMainWindow().closeCurrentProject();
    }

    private void exportProjectActionPerformed(ActionEvent evt) {
        TIORG_APP.getMainWindow().exportProject();
    }

    private void setClusteringParametersActionPerformed(ActionEvent evt) {
        TIORG_APP.getMainWindow().setClusteringParameters();
    }

    private void StartClusteringActionPerformed(ActionEvent evt) {
        TIORG_APP.getMainWindow().startClustering();
    }

    private void searchActionPerformed(ActionEvent evt) {
        TIORG_APP.getMainWindow().search();
    }

    private void setSearchOptionsActionPerformed(ActionEvent evt) {
        TIORG_APP.getMainWindow().setSearchOptions();
    }
}
