/********************************************************** 
ATOM : ArTificial Open Market

Author  : P Mathieu, O Brandouy, Y Secq, Univ Lille, France
Email   : philippe.mathieu@univ-lille.fr
Address : Philippe MATHIEU, CRISTAL, UMR CNRS 9189, 
          Lille  University
          59655 Villeneuve d'Ascq Cedex, france
Date    : 14/12/2008

***********************************************************/

package fr.cristal.smac.atom;

import java.util.*;

/**
 * A day represent the structure of a day in a simulation.
 * A classical day structure is EuroNEXT one: one fix period (opening),
 * one continuous period (main trading session) and one fix period (closing).
 * 
 */
public class Day implements Iterable<Period>
{

    public Period[] periods;
    public int dayNumber = 0;
    public int currentPeriod = -1;

    public Day(Period[] periods)
    {
        this.periods = periods;
    }

    public Day(List<Period> periods)
    {
        this(periods.toArray(new Period[periods.size()]));
    }

    protected void init()
    {
        this.currentPeriod = -1;
        for (Period p : periods)
            p.currentTick = 1;
    }

    public boolean isNewDay() {
        return currentPeriod() == periods[0] && currentTick() == 1;
    }
    
    public Period currentPeriod()
    {
        if (currentPeriod >= 0 && currentPeriod < periods.length)
            return periods[currentPeriod];
        throw new RuntimeException("No more period in this day ! ");
    }

    public int currentTick()
    {
        return currentPeriod().currentTick;
    }

    public boolean hasNextPeriod()
    {
        return currentPeriod < periods.length - 1;
    }

    public void nextPeriod()
    {
        this.currentPeriod++;
    }

    protected void nextTick()
    {
        currentPeriod().currentTick++; // currentPeriod().currentTick = currentPeriod().currentTick + 1;
    }

    public static Day createSinglePeriod(int fixingMechanism, int nbTicks)
    {
        return new Day(new Period[]
        {
            new Period(fixingMechanism, nbTicks)
        });
    }

    public static Day createEuroNEXT(int openTicks, int mainTicks, int closeTicks)
    {
        return new Day(new Period[]
        {
            new Period(MarketPlace.FIX, openTicks),
            new Period(MarketPlace.CONTINUOUS, mainTicks),
            new Period(MarketPlace.FIX, closeTicks)
        });
    }

    public String toString()
    {
        return "Day has " + periods.length + " periods, current = " + currentPeriod();
    }

    public Iterator<Period> iterator()
    {
        List<Period> tmp = new ArrayList<Period>();
        for (Period p : periods)
            tmp.add(p);
        return tmp.iterator();
    }
}
