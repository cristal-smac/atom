/********************************************************** 
ATOM : ArTificial Open Market

Author  : P Mathieu, O Brandouy, Y Secq, Univ Lille, France
Email   : philippe.mathieu@univ-lille.fr
Address : Philippe MATHIEU, CRISTAL, UMR CNRS 9189, 
          Lille  University
          59655 Villeneuve d'Ascq Cedex, france
Date    : 14/12/2008

***********************************************************/

package fr.cristal.smac.atom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import fr.cristal.smac.atom.orders.*;
import fr.cristal.smac.atom.agents.DumbAgent;


public class WithoutSimulation {
	
	@Test
    public void test()
    {
	// COMMENT FAIRE SA PROPRE EXPERIENCE

        MarketPlace market = new MarketPlace();
      	market.setFixingPeriod (MarketPlace.CONTINUOUS); // ou FIX
        market.logType=MarketPlace.LONG; // ou SHORT
        market.log=Logger.getLogger();
        // market.setLogger(new Logger("/tmp/test.atom"));
        // market.setLogger(new Logger(System.out));
        
	    String obName="lvmh";
        market.orderBooks.put(obName, new OrderBook(obName));

	    Agent a = new DumbAgent("paul");
        a.market = market; // pour que l'agent connaisse le marché et sache faire un getLastPrice
	    // sim.addNewAgent(a); // inutile ici

	    Order orders[] =
	    {
		// test Cancel
		new LimitOrder(obName,"x",LimitOrder.ASK, 5, (long) 100),
		new LimitOrder(obName,"y",LimitOrder.ASK, 5, (long) 100),
		new CancelOrder(obName,"z","x"),
		new CancelOrder(obName,"t","y"),

		//test Iceberg
		new IcebergOrder(obName,"a",LimitOrder.BID, 3, 10, (long) 100),
		new LimitOrder(obName,"b",LimitOrder.ASK, 5, (long) 100),
		new LimitOrder(obName,"c",LimitOrder.ASK, 5, (long) 100),

		// test Limit + Market + Iceberg
		new LimitOrder(obName,"d",LimitOrder.ASK, 5, (long) 150),
		new LimitOrder(obName,"e",LimitOrder.ASK, 5, (long) 150),
		new IcebergOrder(obName,"f",LimitOrder.BID, 3, 10, (long) 150),

		new IcebergOrder(obName,"g",LimitOrder.ASK, 3, 10, (long) 200),
		new MarketOrder(obName,"h",LimitOrder.BID, 8),
		new LimitOrder(obName,"i",LimitOrder.BID, 2, (long) 210)

	    };

        for (int i = 0; i < orders.length; i++)
            market.send(a,orders[i]);
           
	// market.printState();
	market.close();
	
	// ---------- TEST --------------
	OrderBook ob = market.orderBooks.get(obName);
	assertEquals(0, ob.ask.size() );
	assertEquals(0, ob.bid.size() );
	assertTrue(ob.lastFixedPrice.price==200 && ob.lastFixedPrice.quantity==1);
	assertEquals(15 , ob.lastPrices.size());
	
    }
}
