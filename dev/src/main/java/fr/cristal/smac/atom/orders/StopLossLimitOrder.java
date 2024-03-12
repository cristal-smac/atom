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
 Stop-Loss-Limit-Order : il est mis en attente tant que le prix de contrepartie n'est pas compatible avec 
 le BEST de l'autre coté : si c'est ASK, tant que son prix est inférieur ou égal au best BID,  si c'est 
 un BID, tant que son prix est supérieur ou égal au best ASK. Au seuil de déclenchement, il devient 
 ordre Limit au prix demandé (il a un seuil de déclenchement, et un prix d'introduction))
 
 Si il n'y a rien, dans le carnet, il attend.
 
 Dans ATOM il est indiqué avec la lettre S
 
 new StopLossLimitOrder(obName,id,LimitOrder.BID, 8, (long)200 , (long) 230);
 pour un bid, le prix d'intro est forcément supérieur ou égal au prix de déclenchement

*/

package fr.cristal.smac.atom.orders;

import fr.cristal.smac.atom.*;

public class StopLossLimitOrder extends LimitOrder {
	
public long seuil;
public long prixIntro;
private OrderBook obTest;

	public StopLossLimitOrder(String obName, String extId, char direction, int quantity, long prixIntro , long seuil, int validity) 
	{
		super(obName, extId, direction, quantity, prixIntro, validity);
		type = 'S';
		this.seuil=seuil;
		this.prixIntro=prixIntro;
		if (direction == LimitOrder.ASK && prixIntro >= seuil)
			throw new RuntimeException("StopLossLimit ASK pb : the limit price is higher than the threshold price");
		if (direction == LimitOrder.BID && prixIntro <= seuil)
			throw new RuntimeException("StopLossLimit BID pb : the limit price is lower than the threshold price");
	}

	public StopLossLimitOrder(String obName, String extId, char direction, int quantity, long prixIntro, long seuil) 
	{
		this(obName, extId, direction, quantity, prixIntro, seuil,  -1);
	} // infinite life


    /**
     * function called by the orderBook to test if yes or no the order can be
     * triggered.
     * 
     * @return True if it can be triggered, False otherwise
     */
    public boolean isTrue()
    {
            if (direction == LimitOrder.ASK && !obTest.bid.isEmpty() )
                    return (obTest.bid.first().price >= seuil);
            if (direction == LimitOrder.BID && !obTest.ask.isEmpty() )
                    return (obTest.ask.first().price <= seuil);
            throw new RuntimeException("StoLimitOrder isTrue exception");
    }


    public void execute(OrderBook ob)
    {
            // used to allow to execute the StopLimitOrder on the current orderBook:
            // if the orderbook described in the file doesn't exist, we use the
            // current one. Could be in the constructor if we had access to ob.
            if (obTest == null)
                    obTest = ob;

            ob.gateway.add(this);
    }

    
    /**
     * Allows to throw the execute method of LimitOrder. To avoid loops, when a
     * StopLimit is triggers, we must throw the execute method of LimitOrder,
     * and not the StopLimitOne (see in OrderBook/send)
     * 
     * @param ob
     */
    public void executeSuper(OrderBook ob)
    {
            super.execute(ob);
    }

    
	public String toString() {
		return ("Order;" + obName + ";" + (sender != null ? sender.name : "UNKNOWN") + ";" + extId + ";"+type+";" + direction
				+ ";" + quantity + ";" + prixIntro + ";" + seuil + ";" + validity);
	}

}
