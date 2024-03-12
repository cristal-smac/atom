/********************************************************** 
ATOM : ArTificial Open Market

Author  : P Mathieu, O Brandouy, Y Secq, Univ Lille, France
Email   : philippe.mathieu@lifl.fr
Address : Philippe MATHIEU, CRISTAL, UMR CNRS 9189, 
          Lille  University
          59655 Villeneuve d'Ascq Cedex, france
Date    : 14/12/2008

***********************************************************/


package fr.cristal.smac.atom.orders;

public class MarketOrder extends LimitOrder
{
    public MarketOrder(String obName, String extId,char direction, int quantity, int validity)
    {	super(obName,extId,direction,quantity,
	      //(direction == LimitOrder.ASK ? 0 : Long.MAX_VALUE), validity);
          // Pb de tri avec Long.MAX_VALUE
          (direction == LimitOrder.ASK ? 0 : 999999999), validity);
			type='M';
    }

    public MarketOrder(String obName, String extId,char direction, int quantity)
    {	this(obName,extId,direction,quantity,-1); }  // infinite life

    public String toString()
    {
	return ("Order;"+obName+";"+(sender != null ? sender.name : "UNKNOWN")+";"+extId+";M;"+direction+";"+quantity+";"+validity);
    }
     
}

