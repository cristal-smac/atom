package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.MarketPlace;
import fr.cristal.smac.atom.OrderBook;
import fr.cristal.smac.atom.PriceRecord;

public class TestRentability
{
    public static void main(String args[])
    {
        MarketPlace m= new MarketPlace();
        String name ="lvmh";
        OrderBook ob = new OrderBook(name);
        m.orderBooks.put(name,ob);
        ob.lastPrices.add(new PriceRecord(name,10,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,5,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,8,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,15,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,17,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,12,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,16,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,20,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,22,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,16,0,'A',null,null));
        ob.lastPrices.add(new PriceRecord(name,18,0,'A',null,null));
        ob.numberOfPricesFixed=11;
        System.out.println(ob.getIntradayMeanReturn(2)); // 0.125
        System.out.println(ob.getIntradayVariance(2)); // 0.0
        System.out.println();
        System.out.println(ob.getIntradayMeanReturn(3)); // -0,073
        System.out.println(ob.getIntradayVariance(3)); // 0.02636
        System.out.println();
        System.out.println(ob.getIntradayMeanReturn(200)); // 0.1349
        System.out.println(ob.getIntradayVariance(200)); // 0.0078566
        
        /*
        // COMMENT FAIRE SA PROPRE EXPERIENCE

	Simulation sim = new Simulation();
      	sim.market.setFixingPeriod (MarketPlace.CONTINUOUS); // ou FIX
        sim.market.logType=MarketPlace.LONG; // ou SHORT
        Logger.log.setOutput(System.out);
        
        // create orderbooks
	for (int i=1; i<=2; i++)
            sim.addNewOrderBook("lvmh"+i);

        // init prices
        for (OrderBook ob : sim.market.orderBooks.values())
            ob.setNewPrice(new PriceRecord(ob.obName,10000+Random.nextInt(1000),0,'A',null,null));

        // set agents
        for (int i=1;i<=5; i++)
            sim.addNewAgent(new ZIT("zit"+i,10000,8000,12000,10,100));
        sim.addNewAgent(new Naive("naive",100000,2));
        
        sim.run(1, 0, 200, 0);

	sim.market.printState();
	sim.market.close();
        
        // lequel et le plus riche
        for (Agent a : sim.agentList.values())
            System.out.println(a.name+" -> "+a.getWealth());
         
         */

    }
}
