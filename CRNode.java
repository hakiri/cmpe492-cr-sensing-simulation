/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstproject;

import java.awt.geom.Point2D;

public class CRNode extends Node{
    /**
     * snr value of the CRNode
     */
    private double snr;
    /**
     * Creates a CRNode with the given snr, position and velocity values.
     * @param snr Snr of the CRNode
     * @param pos Position of the CRNode
     * @param vel Velocity of the CRNode
     */
    public CRNode(double snr, Point2D.Double pos, double vel) {
        this.snr = snr;
        this.position = pos;
        this.velocity = vel;
    }
    /**
     * Gets the current snr value of the CRNode
     * @return Snr of the CRNode
     */
    public double getSnr() {
        return snr;
    }
    
    /**
     * Sets new snr value for the CRNode
     * @param snr Snr of the CRNode
     */
    public void setSnr(double snr) {
        this.snr = snr;
    }
    
}
