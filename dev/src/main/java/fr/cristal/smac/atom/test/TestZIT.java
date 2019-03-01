package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.agents.ZIT;

public class TestZIT {
        public static void main(String args[])
    {
        /*
         * ZIT z = new ZIT("paul", 1000, 14000, 15000, 10, 100, new
         * double[]{0.2, 0.3}, 0.3);
         * for (int i = 1; i <= 50; i++)
         * {
         * System.out.println("tick " + i + "\t" + z.decide("lvmh", null));
         * }
         */


        ZIT z1 = new ZIT("z1", 1000, 14000, 15000, 100, 100, new double[]
                {
                    0.25, 0.25
                }, 1.0);
        ZIT chunk = new ZIT("chunk", 1000, 14000, 15000, 100, 100, new double[]
                {
                    0.25, 0.25
                }, 0.2);
        Simulation sim = new MonothreadedSimulation();
        sim.market.setFixingPeriod(MarketPlace.CONTINUOUS); // ou FIX
        sim.market.logType = MarketPlace.LONG; // ou SHORT
        sim.setLogger(new Logger("/tmp/trace.atom"));
        //sim.setLogger(new Logger(System.out));

        String obName = "lvmh";
        sim.addNewOrderBook(obName);
        sim.addNewAgent(z1);
        sim.addNewAgent(chunk);
        sim.run(Day.createEuroNEXT(0, 20, 0), 1);
        sim.market.printState();
        sim.market.close();
    }
}
