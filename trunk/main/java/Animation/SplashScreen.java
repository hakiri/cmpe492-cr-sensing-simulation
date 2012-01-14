package Animation;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class SplashScreen extends JWindow {
    
    private int duration;
    
    public SplashScreen(int d) {
        duration = d;
    }
    
    // A simple little method to show a title screen in the center
    // of the screen for the amount of time given in the constructor
    public void showSplash() {
        
        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.white);
        
        // Set the window's bounds, centering the window
        int width = 750;
        int height =355;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);
        
        // Build the splash screen
        JLabel labelLoading = new JLabel(new ImageIcon("loading.gif"));
        JLabel label = new JLabel(new ImageIcon("Network.png"));
        JLabel copyrt = new JLabel
                ("Copyright 2012, Bilal Acar & Mehmet Akif Ersoy", JLabel.CENTER);
        copyrt.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        content.add(label, BorderLayout.NORTH);
        content.add(labelLoading, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.SOUTH);
        content.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
        
        // Display it
        setVisible(true);
        
        // Wait a little while, maybe while loading resources
        try { Thread.sleep(duration); } catch (Exception e) {}
        
        setVisible(false);
        
    }
    
    public void showSplashAndExit() {
        
        showSplash();
        
    }
    
}