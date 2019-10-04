package fr.cristal.smac.atom.test.extraday;

import java.util.ArrayList;
import java.util.List;
import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;
import fr.cristal.smac.atom.agents.*;

class TestZITAutoCalibrate {

    public static List<Order> generate(int price, String obName) {
        List<Order> orders = new ArrayList<Order>();
        for (int i = 1; i <= 12; i += 2) {
            orders.add(new LimitOrder(obName, "ask" + (10 + i), LimitOrder.ASK, 10, (long) price + i));
            orders.add(new LimitOrder(obName, "bid" + i, LimitOrder.BID, 10, (long) price - i));
        }
        return orders;
    }

    public static void main(String args[]) {
        Simulation sim = new MonothreadedSimulation();
        sim.market.setFixingPeriod(MarketPlace.CONTINUOUS); // ou FIX
        sim.market.logType = MarketPlace.LONG; // ou SHORT
        Logger logger = new Logger(System.out);
        sim.setLogger(logger);
        //sim.setLogger(new Logger("test.atom"));

        List<Order> orders = new ArrayList<Order>();
        // on crée une liste d'ordres par carnet
        for (int i = 0; i < 2; i++) {
            String obName = "lvmh" + i;
            sim.addNewOrderBook(obName);
            orders.addAll(generate(100, obName));           
        }
        DumbAgent a = new DumbAgent("paul");
        sim.initOrderBooks(a, orders);
        
        ZIT zit = new ZIT_Test("zorglub");
        zit.setAutoCalibrate(1); // auto-calibrate at each tick !
        sim.addNewAgent(zit);
                
        Day day = Day.createSinglePeriod(MarketPlace.CONTINUOUS, 12);
        //Day day = Day.createEuroNEXT(0, 10, 0);
        sim.run(day, 1);

        sim.market.printState();
        sim.market.close();
    }
}

class ZIT_Test extends ZIT {

    public ZIT_Test(String name) {
        super(name);
    }

    @Override
    public void afterDecide(String obName, Day d, Order o) {
        long price = ((LimitOrder) o).price;
        int quantity = ((LimitOrder) o).quantity;
        System.out.println("Price: " + minPrice + " <= " + ((LimitOrder) o).price + " <= " + maxPrice);
        assert minPrice <= price && price <= maxPrice;
        System.out.println("Qty: " + minQuty + " <= " + ((LimitOrder) o).quantity + " <= " + maxQuty);
        assert minQuty <= quantity && quantity <= maxQuty;
    }
}
