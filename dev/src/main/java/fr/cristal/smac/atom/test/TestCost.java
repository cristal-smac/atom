
package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;
import fr.cristal.smac.atom.agents.DumbAgent;


public class TestCost
{
    public static void main(String args[])
    {
        Simulation s = new MonothreadedSimulation();
	String obName="lvmh";
        s.addNewOrderBook(obName);
	Agent a = new DumbAgent("paul");
       	Agent b = new DumbAgent("pierre");
        s.addNewAgent(a);
        s.addNewAgent(b);
        
        
        MarketPlace.cost=0.0; // 10pc
        System.out.println("With "+(MarketPlace.cost*100)+"pc cost");
        s.clear();
        a.cash=10000;
        b.cash=10000;
        MarketPlace.bank=0;
        s.market.send(a,new LimitOrder(obName,"x",LimitOrder.BID, 1, (long) 1000));
        s.market.send(b,new LimitOrder(obName,"x",LimitOrder.ASK, 1, (long) 1000));        
        System.out.println(a);
        System.out.println(b);
        System.out.println("Bank : "+MarketPlace.bank);
        
        MarketPlace.cost=0.2; // 10pc
        System.out.println("\nWith "+(MarketPlace.cost*100)+"pc cost");
        s.clear();
        a.cash=10000;
        b.cash=10000;
        MarketPlace.bank=0;
        s.market.send(a,new LimitOrder(obName,"x",LimitOrder.BID, 1, (long) 1000));
        s.market.send(b,new LimitOrder(obName,"x",LimitOrder.ASK, 1, (long) 1000));        
        System.out.println(a);
        System.out.println(b);
        System.out.println("Bank : "+MarketPlace.bank);
        
        s.market.close();
   }
}

