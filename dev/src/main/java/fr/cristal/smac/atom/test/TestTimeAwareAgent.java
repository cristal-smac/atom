/**
 * ********************************************************
 * ATOM : ArTificial Open Market
 *
 * Author : P Mathieu, O Brandouy, Univ Lille, France 
 * Email  :  philippe.mathieu@lifl.fr 
 * Address : Philippe MATHIEU, LIFL, UMR CNRS 8022,
 *           Lille 1 University 59655 Villeneuve d'Ascq Cedex, france 
 * Date : 14/12/2008
 *
 **********************************************************
 */
package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.Agent;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.Logger;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Order;
import fr.cristal.smac.atom.Simulation;

/*
 * Un agent conscient du temps qui s'écoule. Cet agent prend la parole nbParts
 * fois réparties équitablement sur chaque période continue, le début et la fin
 * n'étant pas compris.
 */
class TimeAwareAgent extends Agent
{

    int nbParts;

    public TimeAwareAgent(String name, long cash, int nbParts)
    {
        super(name, cash);
        this.nbParts = nbParts;
    }

    public Order decide(String obName, Day day)
    {
        
        // test d'un nouveau jour
        if (day.currentPeriod().currentTick()==1) 
            System.out.println("\nIt's a new day :-)");
            
        // si c'est pas la periode continue en 2e, on ne fait rien
        if (!day.currentPeriod().isContinuous()) 
            return null;

        // si c'est le dernier tour de la periode on ne fait rien
        if (day.currentPeriod().currentTick() == day.currentPeriod().totalTicks())
            return null;

        // Rappel : le premier tick est à 1
        int tranche = (day.currentPeriod().totalTicks())/nbParts ;
        if (day.currentPeriod().currentTick() % tranche != 0) return null;

        int pc = (day.currentPeriod().currentTick()*100)/day.currentPeriod().totalTicks() ;
        System.out.println("I have the talk at " + day.currentPeriod().currentTick() + ".\t" + pc + "% fait");

        //return new LimitOrder(obName, "" + myId, dir, quty, price);
        return null;
    }
}

public class TestTimeAwareAgent
{
    public static void main(String args[])
    {
        Simulation sim = new MonothreadedSimulation();
        sim.setLogger(new Logger("test1.log"));      
        sim.addNewOrderBook("lvmh");
        sim.addNewAgent(new TimeAwareAgent("paul", 0, 5));
        // sim1.runExtraday(1,0,100,0);
        sim.run(Day.createEuroNEXT(0, 100, 0), 1);
        // sim.market.printState();
        sim.market.close();
    }
}
