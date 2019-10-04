package fr.cristal.smac.atom.agents.multiasset;

import java.util.*;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.agents.ModerateAgent;
import fr.cristal.smac.atom.orders.*;

/* 
 * To work, it needs to have a first price fixed for all orderbooks.
 * 
 */
public class MarketPortfolio extends ModerateAgent {

    int period;
    HashMap<String, Double> weights = null;
    boolean allPricesExist = false;

    public MarketPortfolio(String name, long cash, int period) {
        super(name, cash);
        this.period = period;
    }
    
    @Override
    public Order decide(String invest, Day day) {
        // PB : il doit y avoir eu des prix pour TOUS  !!
        if (!allPricesExist) {
            allPricesExist = true;
            for (OrderBook ob : market.orderBooks.values())
                if (ob.lastPrices.size() == 0)
                    allPricesExist = false;

            if (allPricesExist)
            {
                // si il y a un prix pour tous, on calcule les répartitions en fonction des prix.
                double sum = 0.0;
                for (OrderBook ob : market.orderBooks.values())
                    sum += ob.lastFixedPrice.price;
                
                weights = new HashMap<String, Double>();                        
                for (OrderBook ob : market.orderBooks.values())
                    weights.put(ob.obName, ob.lastFixedPrice.price / sum);
            }
            else
            {
                // System.out.println("null");
                return null;
            }
        }

        // si tous les carnets ont un prix fixé
        Order o = null;
        int quty;
        long price;
        OrderBook ob = market.orderBooks.get(invest);

        // At most I only have 1 order waiting in each orderbook 
        if (day.currentPeriod().currentTick() % period == 0) // on Cancel tout
        {
            boolean found = false;
            Order o1 = null;
            for (Iterator it = pendings.iterator(); it.hasNext() && !found;)
            {
                o1 = (LimitOrder) it.next();
                found = o1.obName.equals(invest);
            }
            if (found)
                o = new CancelOrder(invest, "" + myId, o1.extId);
        }

        if (day.currentPeriod().currentTick() % period == 1) // on rééquilibre en fonction des weights
        {
            long lastPrice = ob.lastFixedPrice.price;
            
            // calcul du wealth global; tous les orderbooks cumulés
            long Wt = 0;
            for (OrderBook ob2 : market.orderBooks.values())
            {
                Wt += lastPrice * getInvest(ob2.obName);
                // System.out.print(getInvest(ob2.obName) + " ");
            }
            Wt += cash;
            // System.out.println(" Cash=" + cash + " Wealth = " + Wt);


            // on calcule la différence entre ce que l'on a et ce que l'on aimerait avoir
            // On répartit en fonction des prix
            int diff = getInvest(invest) - (int) (Wt * weights.get(invest)/lastPrice);

            if (diff > 1)
            {
                // si c'est plus élevé que 1, il faut vendre
                // PB : combien en prix et quty ?
                if (ob.ask.isEmpty())
                    price = lastPrice;
                else
                    price = (long) (ob.ask.first().price - Math.random() * 100);
                o = new LimitOrder(invest, myId + "", LimitOrder.ASK, diff, price);
            }
            if (diff < 0)
            {
                // PB : combien en prix et quty ?
                if (ob.bid.isEmpty())
                    price = lastPrice;
                else
                    price = (long) (ob.bid.first().price + Math.random() * 100);
                o = new LimitOrder(invest, myId + "", LimitOrder.BID, Math.abs(diff), price);

            }
        }

        return o;
    }
}
