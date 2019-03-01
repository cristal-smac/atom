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


public class IcebergOrder extends LimitOrder
{
    public int total;    // will decrease until 0
    public int part;     // visible part of the Iceberg

    public IcebergOrder(String obName, String extId, char direction, int part, int total, long price, int validity)
    {
	super(obName,extId, direction, total, price);
	this.total=total;
	this.part=part;
	raiseIceberg();
	type='I';
    }

    public IcebergOrder(String obName, String extId, char direction, int part, int total, long price)
    {	this(obName,extId,direction,part,total,price,-1); }  // infinite life

    public String toString()
    {	
    //    return (obName+";"+sender.name+";"+extId+";I;"+direction+";"+price+";"+part+";"+initQuty + ";"+validity);
        return ("Order;"+obName+";"+(sender != null ? sender.name : "UNKNOWN")+";"+extId+";I;"+direction+";"+price+";"+part+";"+initQuty + ";"+validity);
    }

    public boolean isIceberg(){return true;}

    public void raiseIceberg()
    {
	if (total == 0) return;
        // System.out.println("========> Iceberg order : remontee "+this);


	if (total <= part)
	    {
		quantity = total;
		total = 0;
	    } else
	    {
		quantity = part;
		total -= part;
	    }
    }
}


