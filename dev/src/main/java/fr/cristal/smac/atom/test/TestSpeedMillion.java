/**
 * ********************************************************
 * ATOM : ArTificial Open Market
 *
 * Author : P Mathieu, O Brandouy, Univ Lille, France Email :
 * philippe.mathieu@lifl.fr Address : Philippe MATHIEU, LIFL, UMR CNRS 8022,
 * Lille 1 University 59655 Villeneuve d'Ascq Cedex, france Date : 14/12/2008
 *
 **********************************************************
 */
package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.MarketPlace;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

/*
 * Speed Test. 1000 agents send 1000 orders each, during 100 days,
 * thus 100 million orders must be executed by the market. There is no Logger.
 * Usually it takes less than 2min  (73 sec on my MacbookPro)
 */
public class TestSpeedMillion
{

    public static void main(String args[])
    {        
        MarketPlace.acceptNegativeValues=true;
       
        Simulation s = new MonothreadedSimulation();

        String obName = "lvmh";
        s.addNewOrderBook(obName);

        long timebefore = System.currentTimeMillis();

        for (int i = 1; i <= 1000; i++)
        {
            s.addNewAgent(new ZIT("ag" + i));
        }

        s.run(Day.createEuroNEXT(0, 1000, 0), 100); 
        /*
         * 100 jour : multiplier par 10
         */

        long timeafter = System.currentTimeMillis();

        System.out.println("Time   : " + (timeafter - timebefore) + " millisec");
        s.market.printState();
        s.market.close();
    }
}
