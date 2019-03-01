/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.Agent;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.Logger;
import fr.cristal.smac.atom.MultithreadedSimulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

/**
 *
 * @author mathieu
 */
public class TestThreads
{

    public static void main(String args[])
    {
        Simulation sim = new MultithreadedSimulation();
        sim.setLogger(new Logger(System.out));


        sim.tempo = 500; // 1/2 sec pour chaque tour

        // sim.market.setFixingPeriod(MarketPlace.FIX);
        // sim.market.setFixingPeriod(MarketPlace.CONTINUOUS);

        // sim.market.logType = MarketPlace.SHORT;
        // sim.market.logType = MarketPlace.LONG;

        sim.addNewOrderBook("lvmh");
        for (int i = 1; i <= 3; i++)
        {
            Agent z = new ZIT("zit_" + i, 10000);
            z.speed = 2 * i; // parle tous les "speed" tours
            sim.addNewAgent(z);
        }
        // mettre speed=1; si on veut qu'il parle toutes les secondes.

        // sim.runExtraday(1, 0, 24, 0); // 12 secondes
        sim.run(Day.createEuroNEXT(0, 24, 0), 1); // 12 secondes

        sim.market.close();

    }
}
