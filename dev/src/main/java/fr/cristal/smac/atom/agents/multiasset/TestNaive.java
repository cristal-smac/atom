package fr.cristal.smac.atom.agents.multiasset;

import fr.cristal.smac.atom.Agent;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.Logger;
import fr.cristal.smac.atom.MarketPlace;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZITimproved;

public class TestNaive
{

    public static void main(String args[])
    {
        // COMMENT FAIRE SA PROPRE EXPERIENCE

        Simulation sim = new MonothreadedSimulation();
        sim.market.setFixingPeriod(MarketPlace.CONTINUOUS); // ou FIX
        sim.market.logType = MarketPlace.LONG; // ou SHORT
        sim.setLogger(new Logger("toto"));
        //sim.setLogger(new Logger(System.out));


        // create orderbooks
        for (int i = 1; i <= 3; i++)
            sim.addNewOrderBook("lvmh" + i);

        // initOrderBooks prices
/*
         * for (OrderBook ob : sim.market.orderBooks.values())
         *         ob.setNewPrice(new PriceRecord(ob.obName,10000+Random.nextInt(1000),0,'A',null,null));
         */
        // set agents
        for (int i = 1; i <= 5; i++)
            sim.addNewAgent(new ZITimproved("zit" + i, 10000, 800, 1200, 10, 100));
        sim.addNewAgent(new NaiveMarket("naive", 100000, 1400, 3));

        //sim.runExtraday(1, 0, 100, 0);
        sim.run(Day.createEuroNEXT(0, 1000, 0), 1);

        sim.market.printState();
        sim.market.close();

        // lequel et le plus riche
        for (Agent a : sim.agentList.values())
            System.out.println(a.name + " -> " + a.getWealth());

    }
}
