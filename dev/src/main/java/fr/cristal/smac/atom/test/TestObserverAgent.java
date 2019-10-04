package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.Agent;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.MarketPlace;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Order;
import fr.cristal.smac.atom.OrderBook;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

/*
 * This experience shows how to build a basic agent who just have a look to 
 * the market without sending any order.
 * With trhis technic you can for example test, during an experience,
 * if a specific property occurs.
 */
class Observateur extends Agent
{

    Observateur(String name, long cash)
    {
        super(name, cash);
    }

    public Order decide(String obName, Day day)
    {
        OrderBook ob = market.orderBooks.get(obName);
        if (ob.numberOfPricesFixed > 0)
        {
            long p1 = ob.lastFixedPrice.price;
            long p2 = ob.lastPrices.getFirst().price;
            long p3 = ob.lastPrices.get(0).price;
            if (p1 != p2 || p1 != p3)
            {
                System.out.println("Big problem");
                System.exit(1);
            }
        }
        /*
         for (int i = 0; i < 5 && i < ob.lastPrices.size(); i++)
         System.out.print(ob.lastPrices.get(i).price + " ");
         System.out.println();
         */
        return null;
    }
}

public class TestObserverAgent
{

    public static void main(String args[])
    {
        Simulation sim = new MonothreadedSimulation();
        // sim.market.setFixingPeriod (MarketPlace.CONTINUOUS); // ou FIX
        sim.market.logType = MarketPlace.SHORT; // ou SHORT
        // Logger.log.setOutput(System.out);

        String obName = "lvmh";
        sim.addNewOrderBook(obName);
        sim.addNewAgent(new Observateur("obs", 0));
        for (int i = 1; i <= 3; i++)
            sim.addNewAgent(new ZIT("ag" + i, 1000));

        sim.run(Day.createEuroNEXT(0, 1000, 0), 1);

        sim.market.printState();
        sim.market.close();
    }
}
