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


public class UpdateOrder extends Order
{
    private String extIdToUpdate;
    private int qutyToSet;

    public UpdateOrder(String obName,String extId, String extIdToUpdate, int qutyToSet)
    {
	super(obName,extId);
	this.extIdToUpdate = extIdToUpdate;
	this.qutyToSet = qutyToSet;
        type = 'U';
    }

    public void execute(OrderBook ob)
    {
	/* mÃªmes problÃ¨mes que le Cancel ... mÃªme Ã©criture*/

       LimitOrder lo = ob.findOrder(sender,extIdToUpdate,true);
    
       if (lo==null) lo = ob.findOrder(sender,extIdToUpdate,false);
       if (lo!=null) 
       {
           // System.out.println("========> update order "+this);
           // System.out.println("========> update before "+lo);
           lo.quantity=qutyToSet;
           // System.out.println("========> updtate after : "+lo);

       }

	sender.touchedOrExecutedOrder(Event.UPDATED, this, null);
    }

    public String toString()
    {
	return super.toString()+ ";" + extIdToUpdate + ";" + qutyToSet;
    }
}

