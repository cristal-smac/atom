/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

/**
 *
 * @author mathieu
 */
public class Test8Threads
{

    /**
     * code permettant de lancer simultanément 8 simulations, pour voir
     * l'efficacité d'une archi multi-coeurs
     * {@code
     * 1 -> 10 sec
     * 2 -> 16 sec
     * 4 -> 35 sec
     * 8 -> 99 sec
     * }
     */
    public static void main(String args[])
    {
        for (int i = 1; i <= 8; i++)
            new Thread(new Runnable()
            {
                public void run()
                {
                    Simulation sim = new MonothreadedSimulation();
                    sim.addNewOrderBook("lvmh");
                    for (int i = 1; i <= 1000; i++)
                        sim.addNewAgent(new ZIT("zit_" + i, 10000));
                    System.out.println(sim.run(Day.createEuroNEXT(0, 100, 0), 100)); // 12 secondes
                    sim.market.close();
                }
            }).start();
    }
}
