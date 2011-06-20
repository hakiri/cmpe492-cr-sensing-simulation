/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;

public class CRBase extends Node{
    /**
     * Creates a CRBase at the given position.
     * @param pos Position of the CRBase
     */
    public CRBase(Point2D.Double pos){
        this.position = new Point2D.Double(pos.x, pos.y);
        this.velocity = 0.0;
    }
    
}
