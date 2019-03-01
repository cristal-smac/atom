/**
 * ********************************************************
 * ATOM : ArTificial Open Market
 *
 * Author : P Mathieu, O Brandouy, Univ Lille, France Email :
 * philippe.mathieu@lifl.fr Address : Philippe MATHIEU, LIFL, UMR CNRS 8022,
 * Lille 1 University 59655 Villeneuve d'Ascq Cedex, france Date : 14/12/2008
 *
 **********************************************************
 */
package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;
import fr.cristal.smac.atom.agents.DumbAgent;

/*
 * Test of the loading charge capacity of the orderbook. Here orders are sent
 * directly to the book in two sequences : 10000 ask that are stacked, then
 * 10000 bid that match the latter. The experiment is run 50 times, which
 * delivers an increase in the loading charge of 20.000 orders each time. We
 * thus end-up with 1million orders to be executed. At the end the orderbook
 * must definitely be empty and 500.000 prices must have been fixed. Here I
 * don'y use the Simulation class nor any Logger.
 * Usually it take less than 1 sec
 */
public class TestSpeedNbOrders
{

    public static void main(String args[])
    {
        MarketPlace market = new MarketPlace();
        market.setFixingPeriod(MarketPlace.CONTINUOUS);
        market.logType = MarketPlace.LONG;
        String obName = "lvmh";
        market.orderBooks.put(obName, new OrderBook(obName));
        Agent a = new DumbAgent("paul");

        long timebefore = System.currentTimeMillis();

        for (int x = 1; x <= 50; x++)
        {
            for (int i = 1; i <= 10000; i++)
                market.send(a, new LimitOrder(obName, "", LimitOrder.ASK, 10, i));

            for (int i = 1; i <= 10000; i++)
                market.send(a, new LimitOrder(obName, "", LimitOrder.BID, 10, i));
        }


        long timeafter = System.currentTimeMillis();

        System.out.println("Time   : " + (timeafter - timebefore) + " millisec");
        market.printState();
        market.close();
    }
}
