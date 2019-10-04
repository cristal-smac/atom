package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.*;

public class TestSinusoide
{

    public static void main(String args[])
    {
        Simulation sim = new MonothreadedSimulation();
        sim.setLogger(new Logger(System.out));
        String obName = "lvmh";
        sim.addNewOrderBook(obName);
        sim.addNewAgent(new MonAgentAsk("pierre"));
        sim.addNewAgent(new MonAgentBid("paul"));
        sim.run(Day.createSinglePeriod(MarketPlace.FIX, 1000),1);
        sim.market.printState();
        sim.market.close();
    }
}

class MonAgentAsk extends Agent
{

    private int stepSize = 10;
    private int nbSteps = 10;
    private int nbSeries = 3;
    private long firstPrice = 10000;
    private int currentStep = 0;
    private int currentSerie = 0;

    public MonAgentAsk(String name)
    {
        super(name, 0);
    }

    public Order decide(String obName, Day day)
    {
        long price = 0;
        if (day.currentPeriod().currentTick() == 1)
            price = firstPrice;
        else
        {
            if (currentStep == nbSteps)
            {
                currentStep = 0;
                currentSerie++;
                stepSize *= -1;
            }
            else
                currentStep++;

            if (currentSerie == nbSeries)
                return null;
            else
                currentSerie++;

        }

        price += stepSize;

        return new LimitOrder(obName, "" + myId, LimitOrder.ASK, 10, price);
    }
}

class MonAgentBid extends Agent
{

    public MonAgentBid(String name)
    {
        super(name, 0);
    }

    public Order decide(String obName, Day day)
    {
        return new MarketOrder(obName,"" + myId, LimitOrder.BID, 10);
    }
}
