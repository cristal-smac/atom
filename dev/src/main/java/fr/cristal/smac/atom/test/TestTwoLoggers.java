package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.Agent;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.Logger;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/* Deux loggers dans deu simulations différentes */
public class TestTwoLoggers
{
    public static void main(String args[])
    {
        Simulation sim1 = new MonothreadedSimulation();
        Simulation sim2 = new MonothreadedSimulation();

        sim1.setLogger(new Logger("test1.log"));
        sim2.setLogger(new Logger("test2.log"));
        
        sim1.addNewOrderBook("lvmh");
        Agent a = new ZIT("paul");
        sim1.addNewAgent(a);
        
        sim2.addNewOrderBook("apple");
        Agent b = new ZIT("joe");
        sim2.addNewAgent(b);

        //sim1.runExtraday(1,0,100,0);
        sim1.run(Day.createEuroNEXT(0, 100, 0), 1);
        //sim2.runExtraday(1,0,100,0);
        sim2.run(Day.createEuroNEXT(0, 100, 0), 1);


    }
}
