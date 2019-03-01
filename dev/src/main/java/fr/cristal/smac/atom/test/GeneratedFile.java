package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.FilteredLogger;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

public class GeneratedFile {

    public static void main(String args[]) {
        System.out.println(System.getProperty("java.version"));
        
        Simulation sim = new MonothreadedSimulation();
        // Setting a filtered logger to have only orders, prices and info
        FilteredLogger flog = new FilteredLogger(System.out);
        flog.orders = true;
        flog.prices = true;
        flog.ticks = true;
        flog.days = true;
        flog.agents = true;
        flog.infos = false;
        
        flog.commands = false;
        sim.setLogger(flog);
        // Creating three orderbooks
        sim.addNewOrderBook("IBM");
        sim.addNewOrderBook("AAPL");
        sim.addNewOrderBook("GOOG");
        // Adding some ZIT agents
        for (int i = 1; i <= 3; i++) {
            sim.addNewAgent(new ZIT("zit_" + i, 10000));
        }
        // Launching a simulation on 3 days
        sim.run(Day.createEuroNEXT(0, 5, 0), 2);
        sim.market.close();
        
    }
}
