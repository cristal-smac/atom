/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cristal.smac.atom.test.extraday;

import java.util.ArrayList;
import java.util.List;
import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;
import fr.cristal.smac.atom.agents.ZIT;

/**
 *
 * @author mathieu
 */
public class TestMultiDayCalibratedZIT
{

    public static void main(String[] args)
    {
        // On met 0 au CLOSE car il n'est pas utilisé, mais il pourrait permettre
        // de comparer le cours de clotûre réel (du fichier) avec ce qui est généré 
        // par l'expérience.
        DayLog jour1 = new DayLog("APL", 1200, 1000, 2000, 0);
        DayLog jour2 = new DayLog("APL", 3200, 3000, 4000, 0);
        DayLog jour3 = new DayLog("APL", 5200, 5000, 6000, 0);

        List<DayLog> dayLogs = new ArrayList<DayLog>();
        dayLogs.add(jour1);
        dayLogs.add(jour2);
        dayLogs.add(jour3);

        Simulation sim = new MonothreadedSimulation();
        Logger logger = new Logger("calibratedZIT.atom");
        sim.setLogger(logger);

        for (int i = 0; i < 3; i++)
            sim.addNewAgent(new CalibratedZIT("zit" + i));

        sim.initDayLog(dayLogs);
        System.out.println(sim.market.orderBooks.get("APL").extradayLog);
        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 1000), 3);

    }
}

class CalibratedZIT extends Agent
{

    // On initialise les bornes à partir des prix low et high du fichier
    // ATTENTION: getLowest et getHighest sont changés dynamiquement
    long low = 0, high = 0;

    public CalibratedZIT(String name)
    {
        super(name);
    }

    @Override
    public Order decide(String obName, Day day)
    {

        // Permet de tester le début d'une période
        // if (day.currentPeriod().currentTick() == 1)
        // Permet de tester le début de la première période d'une journée
        if (day.isNewDay())
        {
            low = getLowestPrice(obName, day);
            high = getHighestPrice(obName, day);
            System.out.println(name + "  " + day.dayNumber + " Low: " + low + " High: " + high);
        }
        // si oui, il choisit un ordre
        char dir = (Math.random() > 0.5) ? LimitOrder.BID : LimitOrder.ASK;
        long price = low + (int) (Math.random() * (high - low));
        return new LimitOrder(obName, ""+myId, dir, 1, price);
    }

}
