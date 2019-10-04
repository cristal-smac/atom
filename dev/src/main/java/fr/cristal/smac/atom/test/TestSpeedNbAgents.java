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
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

/*
 * Speed test. Test de montee en charge du nombre d'agents. On crée 40.000 agents qui
 * enverrons chacun 1 seul ordre. 20.000 d'entre eux envoient un ASK, les 20.000
 * autres envoient un BID dans un ordre indéterminé mais de telle sorte que tous
 * les ordres se matchent et fournissent au carnet vide. L'expérience est
 * renouvelée 10 fois. ce qui totalise 400.000 ordres. Le carnet n'est pas vide
 * puisque même si tout ordre a son symétrique, ils n'arrivent pas dans le même
 * ordre et ne sont donc pas tous matchés. Il n'y a pas de Logger.
 * Usually it take less than 2 sec
 */
public class TestSpeedNbAgents
{

    public static void main(String args[])
    {

        Simulation s = new MonothreadedSimulation();
        String obName = "lvmh";
        s.addNewOrderBook(obName);

        long timebefore = System.currentTimeMillis();

        for (int x = 1; x <= 10; x++)
        {
            for (int i = 1; i <= 20000; i++)
            {
                s.addNewAgent(new ZIT("ask" + i, 0, 10000 + i, 10000 + i, 10, 10, new double[]
                        {
                            1.0, 0.0
                        }));
                s.addNewAgent(new ZIT("bid" + i, 0, 10000 + i, 10000 + i, 10, 10, new double[]
                        {
                            0.0, 1.0
                        }));
            }
            s.run(Day.createEuroNEXT(0, 1, 0), 1);
            s.agentList.clear();
        }

        long timeafter = System.currentTimeMillis();

        System.out.println("Time   : " + (timeafter - timebefore) + " millisec");
        s.market.printState();
        s.market.close();
    }
}
