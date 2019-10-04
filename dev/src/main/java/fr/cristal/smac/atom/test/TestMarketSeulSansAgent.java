package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;

import fr.cristal.smac.atom.agents.DumbAgent;

public class TestMarketSeulSansAgent {
    public static void main(String[] args) {
        MarketPlace m = new MarketPlace();
        
        DumbAgent a1    = new DumbAgent("");
        DumbAgent a2    = new DumbAgent("");
        LimitOrder lo1 = new LimitOrder("lvmh", "", LimitOrder.ASK, 2, 100);
        LimitOrder lo2 = new LimitOrder("lvmh", "", LimitOrder.BID, 1, 100);
        
        m.orderBooks.put("lvmh", new OrderBook("lvmh"));
        
        m.send(a1, lo1);
        m.send(a2, lo2);
        
        m.printState();
        
        System.out.println(a1);
        System.out.println(a2);
    }
}
