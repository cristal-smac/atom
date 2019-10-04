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
        
import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.*;

public class ZIT_MO extends ZIT
{

    private FortuneWheel fw;

    public ZIT_MO(String name, long cash, long minPrice, long maxPrice, int minQuty, int maxQuty)
    {
        super(name, cash, minPrice, maxPrice, minQuty, maxQuty);
        fw = new FortuneWheel(new String[]
                {
                    "Limit", "0.70", "Iceberg", "0.2",
                    "Market", "0.1", "Stop", "0.00",
                    "Cancel", "0.00", "Update", "0.0"
                });
    }

    public ZIT_MO(String name, long cash)
    {
        this(name, cash, 14000, 15000, 10, 100);
    }

    public ZIT_MO(String name)
    {
        this(name, 0);
    }

    @Override
    public LimitOrder decide(String obName, Day day)
    {
        char dir = (Math.random() > 0.5 ? LimitOrder.ASK : LimitOrder.BID);
        int quty = minQuty + (int) (Math.random() * (maxQuty - minQuty));
        long price = minPrice + (int) (Math.random() * (maxPrice - minPrice));

        // fortune weel to choose according to the stats HashMap
        String key = fw.roll();

        // create the chosen order
        LimitOrder order = null;
        if (key.equals("Market"))
            order = new MarketOrder(obName, "" + myId, dir, quty);
        else if (key.equals("Iceberg"))
            order = new IcebergOrder(obName, "" + myId, dir, (int) (quty / 3), quty, price);
        else if (key.equals("Limit"))
            order = new LimitOrder(obName, "" + myId, dir, quty, price);

        return order;
    }
}
