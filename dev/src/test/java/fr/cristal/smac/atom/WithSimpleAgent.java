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

import fr.cristal.smac.atom.orders.LimitOrder;


public class WithSimpleAgent 
{

	// Classe interne definissantr mon agent
	class MyAgent extends Agent {

    public MyAgent(String name) {
        super(name);
    }

    public Order decide(String obName, Day day) {
        
        if (day.currentTick() % 2 == 0) {
            market.log.info("Sending an ASK order [tick="+day.currentTick()+"]");
            return new LimitOrder(obName, "nop", LimitOrder.ASK, 50, (long) 14000);
        }
        market.log.info("Sending a BID order [tick="+day.currentTick()+"]");
        return new LimitOrder(obName, "nop", LimitOrder.BID, 50, (long) 14000);
    }
} 
    
    @Test
    public void test() {
        Simulation sim = new MonothreadedSimulation();
        // sim.setLogger(new Logger(System.out));
        sim.addNewOrderBook("AAPL");
        sim.addNewAgent(new MyAgent("Alan"));
        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 10), 1);
        // sim.market.printState();
        
		// ---------- TEST --------------
		OrderBook ob = sim.market.orderBooks.get("AAPL");
		assertTrue(true);
		assertEquals(0, ob.ask.size());
		assertEquals(0, ob.bid.size() );
		assertTrue(ob.lastFixedPrice.price==14000 && ob.lastFixedPrice.quantity==50);
		assertEquals(5 , ob.lastPrices.size());
		
    }
}
