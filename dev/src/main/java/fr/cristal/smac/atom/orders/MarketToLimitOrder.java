/********************************************************** 
ATOM : ArTificial Open Market

Author  : P Mathieu, O Brandouy, Y Secq, Univ Lille, France
Email   : philippe.mathieu@lifl.fr
Address : Philippe MATHIEU, CRISTAL, UMR CNRS 9189, 
          Lille  University
          59655 Villeneuve d'Ascq Cedex, france
Date    : 14/12/2008

***********************************************************/

/*
 Market-to-limit : comme les Market on ne précise pas de prix.
 Contrairement au Market il ne touche QUE la meilleure contrepartie.
 Si son volume est plus important, il laisse la quantité restante au prix de l'ordre qui vient d'être touché.
 Si il n'y a rien, dans le carnet, l'ordre est refusé.
 
 Dans ATOM il est indiqué avec la lettre T (marketTolimit)
 
 new MarketToLimitOrder(obName,id,LimitOrder.BID, 8);

*/

package fr.cristal.smac.atom.orders;

import fr.cristal.smac.atom.OrderBook;

public class MarketToLimitOrder extends LimitOrder {
	public MarketToLimitOrder(String obName, String extId, char direction, int quantity, int validity) {
		super(obName, extId, direction, quantity, -1, validity);
		type = 'T';
	}

	public MarketToLimitOrder(String obName, String extId, char direction, int quantity) {
		this(obName, extId, direction, quantity, -1);
	} // infinite life

    public void execute(OrderBook ob)
    {
    	// Contrairement au Market à qui on donne un prix infini, un marketToLimit, on lui donne le prix du meilleur
    	// une seule fois (d'où l'init à -1)
    	// on ne peut le faire qu'à l'exec car sinon on n'a pas acces à l'orderbook
    	if (direction == LimitOrder.ASK && price == -1) 
    		if (ob.bid.isEmpty())
    			throw new RuntimeException("Ask Market-To-Limit with empty BID part");
    		else
    			price=ob.bid.first().price;
    	if (direction == LimitOrder.BID && price == -1) 
    		if (ob.ask.isEmpty())
    			throw new RuntimeException("Bid Market-To-Limit with empty ASK part");
    		else
    			price=ob.ask.first().price;    	
    	super.execute(ob);
    	
    }
   
	public String toString() {
		return ("Order;" + obName + ";" + (sender != null ? sender.name : "UNKNOWN") + ";" + extId + ";"+type+";" + direction
				+ ";" + price+";"+quantity + ";" + validity);
	}

}
