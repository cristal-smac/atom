package fr.cristal.smac.atom.agents.multiasset;

import java.util.*;
import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.*;

import fr.cristal.smac.atom.agents.ModerateAgent;

/**
 *
 * This is naive multiasset strategy that diversify basically her portfolio.
 * She starts with an initial cash an a rebalancing frequency.
 * The general idea is that at each period she evaluates her portfolio to keep
 * equal parts of wealth in each asset.
 * To evaluate her portfolio she uses for each orderbook the value of the last
 * fixed price. If there is not, she then uses the initPrice given as parameter.
 * During the rebancing phase, She cancels all its positions, then compute the
 * new volume of orders for each asset.
 * The volume is the difference between the current position an the target
 * position. if this difference is positive the agent will send a ASK order.
 * If this diffence is negative she will send a BID order.
 * Otherwise the agent stays inactive, wating for execution of her orders.
 * Note that she can hold maximum one order per asset (orderbook).
 * 
 * Unfortunately, even if we verify that she has suffisient cash each time she 
 * sends a bid order, as she uses several assets simultaneously, if all the bid
 * orders sent are executed, this leads to negative cash. To avoid that problem,
 * one shoud use hypothetical reasoning bases on available cash ans invests.
 *
 */
public class NaiveLimit extends ModerateAgent
{

    int period;
    long initPrice;

    public NaiveLimit(String name, long cash, long initPrice, int period)
    {
        super(name, cash);
        this.period = period;
        this.initPrice = initPrice;
    }

    public Order decide(String invest, Day day)
    {

        Order o = null;
        int quty;
        long price;
        OrderBook ob = market.orderBooks.get(invest);

        int n = market.orderBooks.size();

        // Just to verify if all is ok
        /*
         * Le pb est que les BID sont calculés en fonction du cash disponible,
         * et comme l'agent utilise plusieurs assets, si tous les BID sont
         * executés simultanément, cela amène à un cash négatif.
         * 
        if (cash < 0 || getInvest(invest) < 0 || pendings.size() > n)
        {
            System.out.println("Pb with Naive : Cash or invest negative : " + cash + " " + getInvest(invest) + " " + pendings.size());
            System.exit(1);
        }
        */

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

        if (day.currentPeriod().currentTick() % period == 1) // on rééquilibre
        {
            long lastPrice;
            if (ob.lastPrices.size() != 0)
                lastPrice = ob.lastFixedPrice.price;
            else
                lastPrice = initPrice; // initial value     

            // re-evaluate positions. les orderbooks sont évalués 1 à la fois, 
            // pas simultanément ! donc il faut recalculer le total weath (Wt)
            // à chaque tour.

            // calcul de la richesse globale; tous les orderbooks cumulés
            // auquel on ajoute le cash résiduel cumulé.
            long Wt = 0;
            for (OrderBook ob2 : market.orderBooks.values())
                Wt += lastPrice * getInvest(ob2.obName); // System.out.print(getInvest(ob2.obName)+" ");
            Wt += cash;
            // System.out.println(" Cash="+cash+" Wealth = " + Wt);


            // on calcule la différence entre ce que l'on a et ce que l'on aimerait avoir
            // L'idée ici c'est de répartir équitablement le wealth
            int diff = getInvest(invest) - (int) (Wt / (n * lastPrice));

            // Quand 0<diff<1 , on considère qu'il a son objectif

            // Si diff > 1 , on est au dessus des objectifs (de la part des 
            // richesses mises dans ce titre), il faut vendre
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


            // Si diff < 1 , on est en dessous des objectifs (de la part des 
            // richesses mises dans ce titre), il faut acheter
            if (diff < 0)
            {
                // PB : combien en prix et quty ?
                if (ob.bid.isEmpty())
                    price = lastPrice;
                else
                    price = (long) (ob.bid.first().price + Math.random() * 100);
                // on vérifie que l'on a suffisamment de cash
                if (Math.abs(diff) * price < cash)
                    o = new LimitOrder(invest, myId + "", LimitOrder.BID, Math.abs(diff), price);

            }
        }


        // si period==0 on envoie un cancel de la postion actuelle
        // si perdio==1 on envoie un limit correspondant au rebalancing
        // dans tous les autres cas on renvoie null ... aucun ordre
        return o;
    }
}
