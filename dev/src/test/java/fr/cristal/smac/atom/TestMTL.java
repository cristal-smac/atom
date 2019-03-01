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

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.agents.*;
import fr.cristal.smac.atom.orders.*;


/* Tests du MarketTo-Limit Order */

public class TestMTL {

	@Test
	public void test() {
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
				
				// Le dernier ordre de chaque test permet de laisser le carnet vide
				
				// TEST 1
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1000) ,
				new MarketToLimitOrder(obName, "u", LimitOrder.ASK, 3),
				// cas trivial : Fixe 1 prix  de 3  à 1000, carnet vide
				
				// TEST 2
				new LimitOrder(obName, "-", LimitOrder.BID, 5 , (long) 1000) ,
				new MarketToLimitOrder(obName, "u", LimitOrder.ASK, 3),
				// Fixe 1 prix de 3 à 1000
				new LimitOrder(obName, "-", LimitOrder.ASK, 2 , (long) 1000) ,
				
				// TEST 3 : le MTL laisse un résidu
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1000) ,
				new MarketToLimitOrder(obName, "u", LimitOrder.ASK, 5),				
				// Fixe 1 prix de 3 à 1000; le Market transforme son résidu en Limit Bid qute 2 à 1000	
				new LimitOrder(obName, "-", LimitOrder.BID, 2 , (long) 1000) ,
								
				// TEST 4 : le MTL matche 2 ordres au meme prix
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1000) ,
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1000) ,
				new MarketToLimitOrder(obName, "u", LimitOrder.ASK, 10),				
				// Fixe 2 prix de type 3 à 1000
				new LimitOrder(obName, "-", LimitOrder.BID, 4 , (long) 1000) ,

				// TEST 5 :Le MTL laisse un résidu qui ne match pas le suivant (contrairement à un Market)
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1000) ,
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long)  800) ,
				new MarketToLimitOrder(obName, "u", LimitOrder.ASK, 10),				
				// Fixe un prix de 3 à 1000, laisse un résidu de 7 à 1000 en ASK, face à un BID non matche
				new LimitOrder(obName, "-", LimitOrder.BID, 7 , (long) 1000) ,
				new LimitOrder(obName, "-", LimitOrder.ASK, 3 , (long) 800) ,

				// TEST 6 : 
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1000) ,
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long)  800) ,
				new MarketToLimitOrder(obName, "-", LimitOrder.ASK, 1),
				// Fixe un prix de 1 à 1000, laisse un Limit BID de 2 à 1000 et de 3 à 800
				new LimitOrder(obName, "-", LimitOrder.ASK, 5 , (long) 800) ,
				
				// TEST 7 : PLANTAGE
				//new MarketToLimitOrder(obName, "u", LimitOrder.ASK, 1)		
				
				// TEST 8 : StopLossLimit
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1000) ,
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1200) ,
				new StopLossLimitOrder(obName, "-", LimitOrder.ASK, 9, (long) 1000, 1300),
				new LimitOrder(obName, "-", LimitOrder.BID, 3 , (long) 1300)
				
		};

		
		for (int i = 0; i < orders.length; i++)
			sim.market.send(a, orders[i]);

		// sim.market.printState();
		sim.market.close();
		
		
		// ---------- TEST --------------
		
		OrderBook ob = sim.market.orderBooks.get(obName);
		assertTrue(true);
		assertEquals(0, ob.ask.size() );
		assertEquals(0, ob.bid.size() );
		assertTrue(ob.lastFixedPrice.price==1000 && ob.lastFixedPrice.quantity==3);
		assertEquals(17 , ob.lastPrices.size());
	}
}
