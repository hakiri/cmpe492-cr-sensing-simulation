package Animation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Splash Screen of user interface
 */
public class SplashScreen extends JWindow {
    
    private int duration;
    
    /**
     * Constructs a splash screen with the time of appearance.
     * @param d Duration of splash screen
     */
    public SplashScreen(int d) {
        duration = d;
    }
    
    /**
     * A method to show a title screen in the center 
     * of the screen for the amount of time given in the constructor
     */
    public void showSplash() {
        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.WHITE);
        // Set the window's bounds, centering the window
        int width = 950;
        int height =255;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);
        // Build the splash screen
        JLabel labelLoading = new JLabel(new ImageIcon("loading.gif"));
        JLabel label = new JLabel(new ImageIcon("Network.png"));
        JLabel copyrt = new JLabel
                ("    Copyright 2012, Bilal Acar & Mehmet Akif Ersoy", JLabel.LEFT);
        copyrt.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        content.add(label, BorderLayout.NORTH);
        content.add(labelLoading, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.SOUTH);
		content.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
															"ZoneS Simulator", TitledBorder.LEFT, TitledBorder.TOP));
        // Display it
        setVisible(true);   
        // Wait a little while, maybe while loading resources
        try { Thread.sleep(duration); } catch (Exception e) {}
        
        setVisible(false);
    }
}