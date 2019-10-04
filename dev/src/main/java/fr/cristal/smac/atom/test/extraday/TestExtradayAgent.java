/*
 * To change this template, choose Tools | Templates
 * and OPEN the template in the editor.
 */
package fr.cristal.smac.atom.test.extraday;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.agents.ZIT;

/**
 *
 * @author mathieu
 */



/* CET AGENT AFFICHE LES HISTO DE PRIX UNIQUEMENT DE LVMH1 */
class Traceur extends Agent
{
    public Traceur()
    {
        super("traceur", 0);
    }
    
    public Order decide(String obName, Day day)
    {
        System.out.print("TRACE intra :");
        for (PriceRecord p : market.orderBooks.get("lvmh1").lastPrices)
            System.out.print(p.price+",");
        System.out.println();
        System.out.print("TRACE extra :");
        for (DayLog log : market.orderBooks.get("lvmh1").extradayLog)
            System.out.print(log.OPEN+",");
        System.out.println();
        return null;
    }
}


/* CET AGENT NE PARLE QU'A CHAQUE JOURNEE */
class ExtradayAgent extends Agent
{
    public ExtradayAgent()
    {
        super("extradayAgent", 0);
    }
    
    public Order decide(String obName, Day day)
    {
        if (day.currentPeriod==1 && day.currentTick()==1)
        {
                System.out.println("TRACE : " + this.name+" : Je ne parle qu'en fin de journée ! " + obName);
        }
        return null;
    }
}



public class TestExtradayAgent {

    public static void main(String args[]) {
        Simulation sim = new MonothreadedSimulation();

        // sim.market.setFixingPeriod(MarketPlace.FIX);
        // sim.market.setFixingPeriod(MarketPlace.CONTINUOUS);

        // sim.market.logType = MarketPlace.SHORT;
        // sim.market.logType = MarketPlace.LONG;

        /**
         * ** Test Loggers ***
         */
        /*
         * Avec le Logger par défaut
         */
        // si rien n'est dit, il n'y a pas de log
        // sinon tout est loggé
        // Logger log = new Logger(System.out);
        // sim.setLogger(log);
        /*
         * Avec un FilteredLogger
         */
        FilteredLogger flog = new FilteredLogger("toto.csv");
        // FilteredLogger flog = new FilteredLogger(System.out);
        flog.orders = false;
        flog.prices = false;
        flog.agents = false;
        flog.infos = true;
        flog.commands = false;
        flog.exec = false;
        flog.ticks=false;

        for (int i = 1; i <= 50; i++) {
            sim.addNewOrderBook("lvmh" + i);
        }
        sim.setLogger(flog);

        for (int i = 1; i <= 1000; i++) {
            sim.addNewAgent(new ZIT("zit_" + i, 10000));
            // sim.addNewAgent(new ZITimproved("IMP_" + i, 10000));
            //sim.addNewAgent(new NaiveLimit("naive" + i, 100000, 10000, 3 * i));
        }
        // sim.addNewAgent(new Traceur());
        //sim.addNewAgent(new ExtradayAgent());

        //sim.runExtraday(1, 0, 10000, 0);
        sim.run(Day.createEuroNEXT(0, 20, 0), 1000);
        // 1000 days. 0 ticks for opening period, 1000 ticks for
        // continuous period and 0 closing period each day.
        // 1000 agents.

    }
}
