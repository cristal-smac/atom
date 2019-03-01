/********************************************************** 
ATOM : ArTificial Open Market

Author  : P Mathieu, O Brandouy, Y Secq, Univ Lille, France
Email   : philippe.mathieu@lifl.fr
Address : Philippe MATHIEU, CRISTAL, UMR CNRS 9189, 
          Lille  University
          59655 Villeneuve d'Ascq Cedex, france
Date    : 14/12/2008

***********************************************************/

package fr.cristal.smac.atom.agents;

import java.util.*;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;;


/**
 * Passe un seul ordre à la fois et ne se ruine JAMAIS !
 * @author mathieu
 */
public class IntelligentAgent extends ModerateAgent
{
    protected long frozenCash;
    protected Map<String,Integer> frozenInvest;
    protected List<Order> pendings;

    public IntelligentAgent(String name, long cash, long minPrice, long maxPrice, int minQuty, int maxQuty)
    {
	super(name,cash, minPrice, maxPrice, minQuty, maxQuty);
	pendings = new ArrayList<Order>();
	frozenCash=0;
	frozenInvest=new HashMap<String,Integer>();
	// A selled or a bidder but not both
    }

    public IntelligentAgent(String name, long cash)
    { this(name,cash,14000,15000,10,100);}

    public IntelligentAgent(String name)
    { this(name,0);}

    public int getFrozenInvest(String name)
    {
	Integer result = frozenInvest.get(name);
	if (result==null) {frozenInvest.put(name,0); return 0;} // autoboxing java 1.5
	else return result;
    }

    void setFrozenInvest(String name, int value)
    {
	frozenInvest.put(name,value); // autoboxing java 1.5
    }

    public LimitOrder decide(String obName, Day day)
    {
	int availableInvest = getInvest(obName) - getFrozenInvest(obName);
	long availableCash = cash - frozenCash;

	if (pendings.size()>0) return null; // one order each time

	char dir  = (Math.random()>0.5?LimitOrder.BID:LimitOrder.ASK);
	LimitOrder o;
	long price;
	OrderBook ob = market.orderBooks.get(obName);
	if (ob.numberOfPricesFixed == 0)
		price = minPrice + (int) (Math.random() * (maxPrice - minPrice));
	else
	    {
		if (dir == LimitOrder.ASK) // i am a Seller : Vendeur
		    {
			if (ob.ask.isEmpty())
			    price = ob.lastFixedPrice.price;
			else
			    price = (long)(ob.ask.first().price - Math.random()*100);
		    }
		else // i am a Buyer : Acheteur
		    {
			if (ob.bid.isEmpty())
			    price = ob.lastFixedPrice.price;
			else
			    price = (long)(ob.bid.first().price + Math.random()*100);
		    }
	    }

	// si on est hors limites, on recadre aleatoirement
	if (price < minPrice || price > maxPrice)
	    price = minPrice + (int) (Math.random() * (maxPrice - minPrice));

        // Valide 10 tours pour ne pas bloquer
	int valid=10;

	// int quty = minQuty + (int) (Math.random() * (maxQuty - minQuty));
	int quty;
	if (dir==LimitOrder.ASK) quty = getInvest(obName);
	else quty = (int) (cash / (double)price);
	if (quty <= 1) return null;

	o = new LimitOrder(obName,""+myId,dir,quty,price,valid);

	// mise a  jour des pendings
	if (o.direction == LimitOrder.BID)
	    frozenCash += o.price * o.quantity;
	else
	    setFrozenInvest(obName , getFrozenInvest(obName) + o.quantity);

	pendings.add(o);
	return o;
    }

    @Override
    public void touchedOrExecutedOrder(Event e, Order order, PriceRecord pr)
    {
	// NOTIFICATION : do what you want
        LimitOrder o = (LimitOrder) order;
	if (o.quantity == 0 || o.validity==0) // A VERIFIER AVEC ICEBERG !
	    {
		pendings.remove(o);

		if (o.direction == LimitOrder.BID)
		    frozenCash -= o.price * o.quantity;
		else
		    setFrozenInvest(o.obName, getFrozenInvest(o.obName) - o.quantity);
	    }
    }
}
