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

import fr.cristal.smac.atom.*;

public class CancelOrder extends Order
{
    public String extIdToKill;

    public CancelOrder(String obName, String extId, String extIdToKill)
    {
	super(obName,extId);
	this.extIdToKill = extIdToKill;
	type='C';
    }

    public void execute(OrderBook ob)
    {
	//	boolean res = ob.ask.removeFromAsk(agent,extIdtoKill) || ob.removeFromBid(agent,extIdtoKill);
	//      ou
	// 	LimitOrder lo = new LimitOrder(ob,extIdtoKill,' ',0,0);
	//      if (ob.ask.contains(lo)) System.out.println("trouveB");
	//
	//      Le probleme de la sol precedente est que dans un
	//      TreeSet le equals doit etre compatible avec le
	//      Comparator. Or le comparator s'appuie sur Id, tandis
	//      que le equals devrait s'appuyer sur le extId
	//
	//   Le cancel et le Update sont donc lineaires, pas en log(n)
        
        //System.out.println("=====> "+this);
        LimitOrder lo = ob.findOrder(sender,extIdToKill,true);
	if (lo!=null)
        {
	    ob.ask.remove(lo) ;
            //System.out.println("========> remove dans ask : "+lo);
        }
        else {
	    lo = ob.findOrder(sender,extIdToKill,false);
	    if (lo!=null)
            {   
                ob.bid.remove(lo) ;
                //System.out.println("========> remove dans bid : "+lo);

            }
	}

	// anyway it is executed
	sender.touchedOrExecutedOrder(Event.CANCELLED, this, null);
    }

    public String toString()
    {
	return super.toString()+";"+extIdToKill;
    }
}

