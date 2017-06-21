package fr.uvsq.adam.tiorg.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ZoomBar extends JPanel implements ActionListener, ChangeListener {

	private static final int MIN_ZOOM = 0;
    private static final int MAX_ZOOM = 400;
    private static final int DEFAULT_ZOOM = 100;

    private static final int MAJOR_ZOOM_SPACING = 50;
    private static final int MINOR_ZOOM_SPACING = 10;
    
    private JLabel zoomAmount;
    private JButton minus;
    private JButton plus;
    private JSlider slider;
    private int zoomValue=DEFAULT_ZOOM;
    private ArrayList<PropertyChangeListener> listenersChanged = null;
    
    private ArrayList<PropertyChangeListener> getListenersChanged()
	{
    	if(listenersChanged == null)
    		listenersChanged = new ArrayList<PropertyChangeListener>();
		return listenersChanged;
	}
    
    public void addListenerChanged(PropertyChangeListener listener)
    {
    	if(listener != null && getListenersChanged().indexOf(listener) == -1)
    		listenersChanged.add(listener);
    }
    
    public void removeListenerChanged(ChangeListener listener)
    {
    	if(listener != null)
    		listenersChanged.remove(listener);
    }
    
    public int getZoomValue()
	{
		return zoomValue;
	}
    public void setZoomValue(int value)
	{
    	getSlider().setValue(value);
	}
    
    private JSlider getSlider()
	{
    	if(slider == null)
    	{
    		slider = new JSlider(MIN_ZOOM, MAX_ZOOM, DEFAULT_ZOOM);
            slider.setPreferredSize(new Dimension(200, 20));
            slider.setMinorTickSpacing(MINOR_ZOOM_SPACING);
            slider.setMajorTickSpacing(MAJOR_ZOOM_SPACING);
            slider.setPaintTicks(true);
            slider.setSnapToTicks(true);
            slider.addChangeListener(this);
    	}
		return slider;
	}
    
    @Override
    public void setBackground(Color bg)
    {
    	super.setBackground(bg);
    	getSlider().setBackground(bg);
    }

    public ZoomBar() {
        super();

        minus = new JButton("-");
        minus.setBorder(null);
        minus.setHorizontalTextPosition(SwingConstants.CENTER);
        minus.setFocusable(false);
        minus.setFocusPainted(false);
        minus.setBackground(Color.LIGHT_GRAY);
        minus.setFont(new Font("Arial", Font.BOLD, 11));
        minus.setMinimumSize(new Dimension(24, 24));
        minus.setMaximumSize(new Dimension(24, 24));
        minus.setMargin(new Insets(0, 0, 0, 0));
        minus.setPreferredSize(new Dimension(24, 24));
        minus.setSize(15, 15);
        minus.setBorderPainted(false);
        plus = new JButton("+");
        plus.setBorder(null);
        plus.setIconTextGap(0);
        plus.setHorizontalTextPosition(SwingConstants.CENTER);
        plus.setFocusable(false);
        plus.setFocusPainted(false);
        plus.setBackground(Color.LIGHT_GRAY);
        plus.setFont(new Font("Arial", Font.BOLD, 11));
        plus.setMinimumSize(new Dimension(24, 24));
        plus.setMaximumSize(new Dimension(24, 24));
        plus.setMargin(new Insets(0, 0, 0, 0));
        plus.setPreferredSize(new Dimension(24, 24));
        plus.setSize(15, 15);
        plus.setBorderPainted(false);
        
        zoomAmount = new JLabel("100%");
        zoomAmount.setHorizontalAlignment(SwingConstants.CENTER);
        zoomAmount.setPreferredSize(new Dimension(28, 24));
        zoomAmount.setMinimumSize(new Dimension(28, 24));
        zoomAmount.setMaximumSize(new Dimension(28, 24));
        zoomAmount.setFont(new Font("Arial", Font.BOLD, 10));

        add(zoomAmount);
        add(minus);
        add(getSlider());
        add(plus);

        plus.addActionListener(this);
        minus.addActionListener(this);
        
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zoom bar clone");
        frame.setContentPane(new ZoomBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == plus) {
            slider.setValue(slider.getValue() + MINOR_ZOOM_SPACING);
        }
        else if (e.getSource() == minus) {
            slider.setValue(slider.getValue() - MINOR_ZOOM_SPACING);
        }
    }

    public void stateChanged(ChangeEvent e) {
        // While slider is moving, snap it to midpoint
        int value = slider.getValue();
        if (slider.getValueIsAdjusting()) {
            return;
        }

        int old = zoomValue;
        zoomValue = value;//fromSlider(value);
        zoomAmount.setText(zoomValue + "%");
        
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "zoomValue", old, zoomValue);
        for(PropertyChangeListener listener : getListenersChanged())
        	listener.propertyChange(evt);
    }

    public int fromSlider(int sliderValue) {
        int mappedValue = 0;
        if (sliderValue <= 50) {
            // Map from [0, 50] to [MIN ... DEFAULT]
            mappedValue = (int) map(sliderValue, 0, 50, MIN_ZOOM, DEFAULT_ZOOM);
        }
        else {
            // Convert from  (50, 100] to (DEFAULT ... MAX]
            mappedValue = (int) map(sliderValue, 50, 100, DEFAULT_ZOOM, MAX_ZOOM);
        }
        return mappedValue;
    }

    public int toSlider(int modelValue) {
        int mappedValue = 0;
        if (modelValue <= DEFAULT_ZOOM) {
            // Map from [MIN_ZOOM, DEFAULT_ZOOM] to [0 ... 50]
            mappedValue = (int) map(modelValue, MIN_ZOOM, DEFAULT_ZOOM, 0, 50);
        }
        else {
            // Convert from  (DEFAULT ... MAX] to (50, 100]
            mappedValue = (int) map(modelValue, DEFAULT_ZOOM, MAX_ZOOM, 50, 100);
        }
        return mappedValue;
    }


    /**
     * @param value The incoming value to be converted
     * @param low1  Lower bound of the value's current range
     * @param high1 Upper bound of the value's current range
     * @param low2  Lower bound of the value's target range
     * @param high2 Upper bound of the value's target range
     * @return
     */
    public static final double map(double value, double low1, double high1, double low2, double high2) {

        double diff = value - low1;
        double proportion = diff / (high1 - low1);

        return lerp(low2, high2, proportion);
    }

    public static final double lerp(double value1, double value2, double amt) {
        return ((value2 - value1) * amt) + value1;
    }

}
