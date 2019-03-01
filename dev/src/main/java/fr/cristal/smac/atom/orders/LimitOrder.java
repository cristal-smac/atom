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

public class LimitOrder extends Order
{

    public char direction;
    public int quantity;
    public int initQuty;
    public long price;
    public long validity;
    public static final char ASK = 'A';
    public static final char BID = 'B';

    public LimitOrder(String obName, String extId, char direction, int quantity, long price, int validity)
    {
        super(obName, extId);
        this.direction = direction;
        this.quantity = quantity;
        this.initQuty = quantity;
        this.price = price;
        this.validity = validity;
        this.type = 'L';
    }

    public LimitOrder(String obName, String extId, char direction, int quantity, long price)
    {
        this(obName, extId, direction, quantity, price, -1);
    } // infinite life

    
    public void execute(OrderBook ob)
    {
        if (direction == LimitOrder.ASK)
            ob.ask.add(this); // Collections.sort(ask,Sort.ASK);
        else
            ob.bid.add(this); // Collections.sort(bid,Sort.BID);
    }

    public String toString()
    {
        return super.toString() + ";" + direction + ";" + price + ";" + quantity + ";" + validity;
    } // plus naturel !
}
