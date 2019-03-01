package fr.cristal.smac.atom.test.extraday;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;
import fr.cristal.smac.atom.agents.*;

class TestZITAutoCalibrateFromFile {

    public static void main(String args[]) throws IOException {
        Simulation sim = new MonothreadedSimulation();
        sim.market.setFixingPeriod(MarketPlace.CONTINUOUS); // ou FIX
        sim.market.logType = MarketPlace.LONG; // ou SHORT
        Logger logger = new Logger(System.out);
        sim.setLogger(logger);
        //sim.setLogger(new Logger("test.atom"));

        
        // CREATION DU FICHIER
        List<Order> orders = new ArrayList<>();
        for (int i=0; i<2; i++) {
            orders.addAll(TestZITAutoCalibrate.generate(100, "lvmh"+i));
        }
        File f = File.createTempFile("orders", "atom");
        BufferedWriter os = new BufferedWriter(new FileWriter(f));
        
        // Price;AAPL;14198;57;A;zit_1-3;zit_2-3;14002;14198
        os.append("Price;lvmh0;1111;111;?;;;;;");
        os.newLine();
        os.append("Price;lvmh0;2222;222;?;;;;;");
        os.newLine();
        
        for (Order o : orders) {
            os.append(o.toString());
            os.newLine();
            //System.out.println(o);
        }
        
        os.close();
        // FIN DE CREATION DU FICHIER
        
        
        sim.initOrderBooks(f);

        //ZIT zit = new ZIT_Test2("zorglub");
        ZIT zit = new ZIT("zorglub", 0, 14000, 15000, 10, 50);
        zit.setAutoCalibrate(1); // auto-calibrate at each tick !
        sim.addNewAgent(zit);
        
        Day day = Day.createSinglePeriod(MarketPlace.CONTINUOUS, 12);
        //Day day = Day.createEuroNEXT(0, 10, 0);
        sim.run(day, 1);

        OrderBook ob = sim.market.orderBooks.get("lvmh0");
        
        for (PriceRecord p : ob.lastPrices) {
            System.out.println(p.price);
        }
        
        assert ob.lastPrices.get(ob.lastPrices.size()-1).price == 1111 : "Vérification prix 1";
        assert ob.lastPrices.get(ob.lastPrices.size()-2).price == 2222 : "Vérification prix 2";
        
       
        
        sim.market.printState();
        sim.market.close();
    }
}

class ZIT_Test2 extends ZIT {

    public ZIT_Test2(String name) {
        super(name);
    }

    @Override
    public void afterDecide(String obName, Day d, Order o) {
        long price = ((LimitOrder) o).price ;
        int quantity = ((LimitOrder) o).quantity ;
        System.out.println("Price: " + minPrice + " <= " + ((LimitOrder) o).price + " <= " + maxPrice);
        assert minPrice <=  price && price <= maxPrice;
        System.out.println("Qty: " + minQuty + " <= " + ((LimitOrder) o).quantity + " <= " + maxQuty);
        assert minQuty <=  quantity && quantity <=  maxQuty;
    }
}
