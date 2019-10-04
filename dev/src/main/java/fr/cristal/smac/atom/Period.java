/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cristal.smac.atom;

/**
 * A period represents a market period within a simulation.
 * A period is characterized by its price fixing mechanism (FIX or
 * CONTINUOUS), a total number of ticks (the number of round of talk 
 * for agents) and a current tick (where we are in the period).
 * 
 */
public class Period {

    protected final int fixingMechanism;
    protected final int totalTicks;
    protected int currentTick;

    public Period(int fixingMechanism, int totalTicks) {
        this.fixingMechanism = fixingMechanism;
        this.totalTicks = totalTicks;
        this.currentTick = 0;
    }

    public boolean isFix() {
        return this.fixingMechanism == MarketPlace.FIX;
    }

    public boolean isContinuous() {
        return this.fixingMechanism == MarketPlace.CONTINUOUS;
    }

    public int getFixing() {
        return fixingMechanism;
    }

    public int currentTick() {
        return currentTick;
    }

    public void setCurrentTick(int t) {
        this.currentTick = t;
    }
    
    public int totalTicks() {
        return totalTicks;
    }

    public String toString() {
        return (isFix() ? "FIX" : "CONT.") + "(" + totalTicks
                + "|" + currentTick + ")";
    }
}
