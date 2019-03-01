/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cristal.smac.atom.test;

import java.util.*;
import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.*;
import fr.cristal.smac.atom.agents.*;

/**
 * Cheking that the new notification scheme using Event and PriceRecord works
 * 
 */
public class TestTouchOrExecutedOrder {
    protected final String ASSET = "APL";
    protected Simulation sim;
    
    public TestTouchOrExecutedOrder() {
        sim = new MonothreadedSimulation(new Logger(System.out));
        sim.addNewOrderBook(ASSET);
    }
    
    public void clear() {
        this.sim.clear();
        this.sim.agentList.clear();
    }
    
    public boolean testCancel() {
        System.out.println("------- CANCEL NOTIFICATION TEST -------");
        List<Order> orders = new ArrayList<Order>();
        orders.add(new LimitOrder(ASSET, "id1", 'A', 1000, 300));
        orders.add(new CancelOrder(ASSET, "wedontcare", "id1"));
        Agent a1 = new Automata("foobar1", orders, true);
        
        sim.addNewAgent(a1);
        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 5), 1);
        sim.market.printState();
        boolean result = sim.market.orderBooks.get(ASSET).ask.isEmpty();
        System.out.println("------- CANCEL NOTIFICATION TEST = "+result+"--");
        return result;
    }
    
    public boolean testUpdate() {
        System.out.println("------- UPDATE NOTIFICATION TEST -------");
        List<Order> orders = new ArrayList<Order>();
        orders.add(new LimitOrder(ASSET, "id1", 'A', 1000, 300));
        final int NEW_QTY = 500;
        orders.add(new UpdateOrder(ASSET, "wedontcare", "id1", NEW_QTY));
        Agent a1 = new Automata("foobar1", orders, true);
        
        sim.addNewAgent(a1);
        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 3), 1);
        sim.market.printState();
        LimitOrder o = sim.market.orderBooks.get(ASSET).ask.first();
        boolean result = (o.quantity == NEW_QTY);
        System.out.println("------- UPDATE NOTIFICATION TEST = "+result+"--");
        return result;
    }
    
    public boolean testExecute() {
        System.out.println("------- EXECUTION NOTIFICATION TEST -------");
        List<Order> orders = new ArrayList<Order>();
        orders.add(new LimitOrder(ASSET, "id1", 'A', 1000, 300));
        Agent a1 = new Automata("foobar1", orders, true);
        sim.addNewAgent(a1);
        orders = new ArrayList<Order>();
        orders.add(new LimitOrder(ASSET, "id2", 'B', 1000, 300));
        Agent a2 = new Automata("foobar2", orders, true);
        sim.addNewAgent(a2);
        
        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 3), 1);
        sim.market.printState();
        boolean result = sim.market.orderBooks.get(ASSET).numberOfPricesFixed == 1;
        System.out.println("------- EXECUTION NOTIFICATION TEST = "+result+"--");
        return result;
    }
    
    public void test() {
        
        
    }
    
    public static void main(String[] args) {
        TestTouchOrExecutedOrder test = new TestTouchOrExecutedOrder();
        
        test.testCancel();
        test.clear();
        
        test.testUpdate();
        test.clear();
        
        test.testExecute();
        test.clear();
    }
    
}
