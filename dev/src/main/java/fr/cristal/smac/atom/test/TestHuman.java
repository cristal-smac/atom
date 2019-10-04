package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.*;

import fr.cristal.smac.atom.agents.*;

public class TestHuman {

    public static void main(String[] args) {
        final Simulation sim = new MultithreadedSimulation();

        sim.setLogger(new Logger(System.out));

        sim.addNewOrderBook("lvmh");

        HumanAgent h1, h2;
        h1 = new HumanAgent("Foo", 0);
        h2 = new HumanAgent("Bar", 0);
        sim.addNewAgent(h1);
        sim.addNewAgent(h2);

        h1.addNewOrder(new LimitOrder("lvmh", "1", 'A', 100, 15000));
        h2.addNewOrder(new LimitOrder("lvmh", "2", 'B', 100, 15000));

        // ajout d'un zit dynamiquement
        (new Thread() {
                public void run() {
                try {
                    sleep(1500);
                    sim.addNewAgent(new ZIT("Yop"));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                }}).start();

        //sim.runExtraday(1, 0, 5, 0);
        sim.run(Day.createEuroNEXT(0, 5, 0), 1);
        sim.market.printState();
        //System.out.println("ASK: "+sim.market.orderBooks.get("lvmh").ask.first());
        //System.out.println("BID: "+sim.market.orderBooks.get("lvmh").bid.first());

    }

}
