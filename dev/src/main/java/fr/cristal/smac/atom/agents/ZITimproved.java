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
import fr.cristal.smac.atom.orders.LimitOrder;;


/*
 * This agent is a Zero Intelligent Trader (that is, she takes its direction 
 * and quantity randomly) whow fixes her price as best bid or ask respectively.
 * She tries to decrease the bid-ask spread by increasing best Bid (decreasing 
 * best Ask) by a random value between 1 and 10.
 * If the orderbook needed part is empty, then she repeats the same procedure
 * with the lastFixedPrice.
 * If there was no fixedPrice she picks her price randomly between minPrice and 
 * MaxPrice.
 * This behavior is equivalent to a PegOrder.
 * Take care that at this time, if unlucky, it can go to a negative price 
 */
public class ZITimproved extends Agent
{

    protected long minPrice, maxPrice;
    protected int minQuty, maxQuty;

    public ZITimproved(String name, long cash, long minPrice, long maxPrice,
            int minQuty, int maxQuty)
    {
        super(name, cash);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minQuty = minQuty;
        this.maxQuty = maxQuty;
    }

    public ZITimproved(String name, long cash)
    {
        this(name, cash, 14000, 15000, 10, 100);
    }

    public ZITimproved(String name)
    {
        this(name, 0);
    }

    @Override
    public Order decide(String obName, Day day)
    {
        char dir = (Math.random() > 0.5 ? LimitOrder.ASK : LimitOrder.BID);
        int quty = minQuty + (int) (Math.random() * (maxQuty - minQuty));

        OrderBook ob = market.orderBooks.get(obName);
        long price;

        /*
         * Essaye de toujours mettre son ordre en tête si il y a quelque chose 
         * dans le carnet. Equivalent à un PegOrder ou au fonctionnement d'un 
         * MarketMaker
         */

        /* par quelle valeur on augmente ou diminue bestBid ou bestAsk */
        int tickSize = ((int)(minPrice/100));
        if (tickSize<2) tickSize=2;
                
        /*
         * Si il y a quelque chose dans le carnet on se met en première position
         * à un prix égal à l'ancien prix +/- une valeur entre 0 et 100
         */
        if (dir == LimitOrder.ASK)
            if (!ob.ask.isEmpty()) // il y a du ask
                price = ob.ask.first().price - (long) ((Math.random() * tickSize));
            else if (!ob.lastPrices.isEmpty())
                price = ob.lastFixedPrice.price - (long) ((Math.random() * tickSize));
            else
                price = minPrice + (int) (Math.random() * (maxPrice - minPrice));
        else //LimitOrder.BID
        if (!ob.bid.isEmpty()) // il y a du bid
            price = ob.bid.first().price + (long) ((Math.random() * tickSize));
        else if (!ob.lastPrices.isEmpty())
            price = ob.lastFixedPrice.price + (long) ((Math.random() * tickSize));
        else
            price = minPrice + (int) (Math.random() * (maxPrice - minPrice));

        /* Pb : cet agent peut finir par arriver à des prix négatifs.
         * Pour corriger ça, Si on n'est pas dans les bornes, on tire un prix 
         * au hasard entre les bornes
         */
        /*
        if (price<minPrice || price>maxPrice)
            price = minPrice + (int) (Math.random() * (maxPrice - minPrice));
        */
        
        return new LimitOrder(obName, "" + myId, dir, quty, price);
    }
}
