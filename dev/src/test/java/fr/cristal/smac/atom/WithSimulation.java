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

import fr.cristal.smac.atom.agents.*;
import fr.cristal.smac.atom.orders.*;

/* Avec classe Simulation */

public class WithSimulation {
	
	@Test
	public void test() {
		// COMMENT FAIRE SA PROPRE EXPERIENCE

		Simulation sim = new MonothreadedSimulation();
		sim.market.setFixingPeriod(MarketPlace.CONTINUOUS); // ou FIX
		sim.market.logType = MarketPlace.LONG; // ou SHORT
		// sim.setLogger(new Logger(System.out));
		// sim.setLogger(new Logger("test.atom"));

		String obName = "lvmh";
		sim.addNewOrderBook(obName);
		Agent a = new DumbAgent("paul");
		sim.addNewAgent(a);
		Order orders[] = { 
				
				// test cancel
				new LimitOrder(obName, "x", LimitOrder.ASK, 5, (long) 100),
				new LimitOrder(obName, "y", LimitOrder.ASK, 5, (long) 100),
				new CancelOrder(obName, "z", "x"), new CancelOrder(obName, "t", "y"),

				// test iceberg
				new IcebergOrder(obName, "a", LimitOrder.BID, 3, 10, (long) 100),
				new LimitOrder(obName, "b", LimitOrder.ASK, 5, (long) 100),
				new LimitOrder(obName, "c", LimitOrder.ASK, 5, (long) 100),

				// test limit+market+iceberg
				new LimitOrder(obName, "d", LimitOrder.ASK, 5, (long) 150),
				new LimitOrder(obName, "e", LimitOrder.ASK, 5, (long) 150),
				new IcebergOrder(obName, "f", LimitOrder.BID, 3, 10, (long) 150),

				new IcebergOrder(obName, "g", LimitOrder.ASK, 3, 10, (long) 200),
				new MarketOrder(obName, "h", LimitOrder.BID, 8),
				new LimitOrder(obName, "i", LimitOrder.BID, 2, (long) 210)

		};

		for (int i = 0; i < orders.length; i++)
			sim.market.send(a, orders[i]);

		//sim.market.printState();
		sim.market.close();

		// ---------- TEST --------------
		OrderBook ob = sim.market.orderBooks.get(obName);
		assertEquals(0, ob.ask.size());
		assertEquals(0, ob.bid.size() );
		assertTrue(ob.lastFixedPrice.price==200 && ob.lastFixedPrice.quantity==1);
		assertEquals(15 , ob.lastPrices.size());

		

	}
}
