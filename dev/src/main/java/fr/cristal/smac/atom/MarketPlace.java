/********************************************************** 
ATOM : ArTificial Open Market

Author  : P Mathieu, O Brandouy, Y Secq, Univ Lille, France
Email   : philippe.mathieu@univ-lille.fr
Address : Philippe MATHIEU, CRISTAL, UMR CNRS 9189, 
          Lille  University
          59655 Villeneuve d'Ascq Cedex, france
Date    : 14/12/2008

***********************************************************/


package fr.cristal.smac.atom;

import java.util.*;

public class MarketPlace
{

    public Logger log = Logger.getLogger();
    public Map<String, OrderBook> orderBooks;
    public static final int CONTINUOUS = 0;
    public static final int FIX = 1;
    public static int fixing = CONTINUOUS; // can be Continous or Fix
    public static final int SHORT = 1;
    public static final int LONG = 2;
    public static int logType = LONG;
    
    public static long bank = 0;
    public static double cost = 0.0;
    public static boolean acceptNegativeValues = true;

    public MarketPlace()
    {
        orderBooks = new HashMap<String, OrderBook>();
        clear();
    }
    
    public void setLogger(Logger log)
    {
        this.log = log;
        for (OrderBook ob : orderBooks.values())
            ob.log = log;
    }

    public void clear()
    {
        // on nettoie les carnets pour demarrer une nouvelle journÃ©e
        // par contre le numberOfRounds, numberOfOrdersReceived etc ne
        // sont pas rÃ©initialisÃ©s.
        for (OrderBook ob : orderBooks.values())
            ob.clear(); /// on rÃ©initialise tout chaque jour !
        fixing = CONTINUOUS;
        logType = LONG;
    }

    public void printState()
    {   // on ne passe que par market maintenant
        for (OrderBook ob : orderBooks.values()) {
            ob.printState();
        }
    }

    public void send(Agent a, Order lo)
    {   // on ne passe que par market maintenant
        lo.sender = a;
        if (!orderBooks.containsKey(lo.obName))
                throw new RuntimeException(lo.obName+" is not present in the market");
        orderBooks.get(lo.obName).send(lo);
    }

    public void setFixingPeriod(int period)
    {
        if (period != CONTINUOUS && period != FIX)
            throw new RuntimeException("Error in period setting");
        // no change at all
        if (MarketPlace.fixing == period)
            return;

        // just before changing the period, need to fix prices
        for (OrderBook ob : orderBooks.values())
            ob.fixingEnd();

        // and we set the new one
        MarketPlace.fixing = period;

        // on loggue la commande
        if (period == FIX)
            log.command(StringOrderParser.FIX);
        else
            log.command(StringOrderParser.CONTINUOUS);
    }


    /*
     * fondamental en multi-fixing pour calculer le prix quand on
     * termine sur une period FIX
     */
    public void close()
    {
        // close all agents
        // TO DO: warn them

        // close all orderbooks
        String invest;
        for (Iterator<String> it = orderBooks.keySet().iterator(); it.hasNext();)
        {
            invest = it.next();
            orderBooks.get(invest).fixingEnd();
        }
        // Saving extraday history
        log.command(StringOrderParser.CLOSE);
    }
    
}
