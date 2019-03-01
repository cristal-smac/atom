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
 Stop-Loss-Market-Order : il est mis en attente tant que le prix de contrepartie n'est pas compatible avec 
 le BEST de l'autre coté : si c'est ASK, tant que son prix est inférieur ou égal au best BID,  si c'est 
 un BID, tant que son prix est supérieur ou égal au best ASK. Au seuil de déclenchement, il devient 
 ordre au Marché (il a un seuil de déclenchement, mais il n'a pas de plafond))
 
 Si il n'y a rien, dans le carnet, il attend.
 
 Dans ATOM il est indiqué avec la lettre 
 
 new StopLossMarketOrder(obName,id,LimitOrder.BID, 8, (long)200);
 Le prix indiqué est le prix de déclenchement

*/

package fr.cristal.smac.atom.orders;


public class StopLossMarketOrder extends StopLossLimitOrder 
{
	public long seuil;
	
	public StopLossMarketOrder(String obName, String extId, char direction, int quantity, long seuil , int validity) 
	{
		super(obName, extId, direction, quantity, (direction == LimitOrder.ASK ? 0 : Long.MAX_VALUE), seuil, validity);
		type = 'R';
	}

	public StopLossMarketOrder(String obName, String extId, char direction, int quantity, long seuil) 
	{
		this(obName, extId, direction, quantity, seuil, -1);
	} // infinite life

	
	public String toString() 
	{
		return ("Order;" + obName + ";" + (sender != null ? sender.name : "UNKNOWN") + ";" + extId + ";"+type+";" + direction
				+ ";" + quantity + ";" + seuil + ";" + validity);
	}
}
