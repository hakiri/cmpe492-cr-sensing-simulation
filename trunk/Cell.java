/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Cell {
    Point2D.Double pos = new Point2D.Double(0, 0);
    private CRBase baseStation = new CRBase(pos);   
    double radius;
    int number_of_sectors;
    int alpha;
    ArrayList<Integer> set_of_d;
    
    public static Point2D.Double deployNode(){
        Point2D.Double temp_position = new Point2D.Double(0, 0);
        
        
        
        return temp_position;
    }
}
