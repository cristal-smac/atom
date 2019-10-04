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

import fr.cristal.smac.atom.orders.*;
import java.util.*;

public class OrderBook
{
    // this class is used only in the bestEvenPrice() method, to compute the
    // pre-opening period

    class Quintuplet
    {

        int cumul1; // for Ask
        int qute1; // for Ask
        long price;
        int qute2; // for Bid
        int cumul2; // for Bid
        int maxExec;
        int minSurplus;

        public String toString()
        {
            return cumul1 + ";" + qute1 + ";" + price + ";" + qute2 + ";"
                    + cumul2 + ";" + maxExec + ";" + minSurplus;
        }
    }
    public Logger log;
    public String obName;
    public TreeSet<LimitOrder> ask;
    public TreeSet<LimitOrder> bid;
    public LimitedLinkedList<PriceRecord> lastPrices;
    public List<DayLog> extradayLog;
    public long numberOfOrdersReceived;
    public long numberOfPricesFixed;
    private Order lastOrder;
    public PriceRecord lastFixedPrice;
    public List<StopLossLimitOrder> gateway; // waiting orders (kind of StopLimit)
    private List<StopLossLimitOrder> toRemove; // used in send to remove from gateway

    
    // extraday - Bandes de bollinger
    public long firstPriceOfDay, lastPriceOfDay, highestPriceOfDay, lowestPriceOfDay;

    public int minQty;
    public int maxQty;
    
    public OrderBook(String obName)
    {
        init();
        this.obName = obName;
        // Each ob has its logger, unless it leaves in a simulation
        // if it is in a simulation, a logger is shared by all orderbooks and
        // it's the simulation responsability to initialize this attribute after
        // the orderbook instanciation (done in Simulation.addNewOrderBook)
        log = Logger.getLogger();
    }

    private void init()
    {
        ask = new TreeSet<LimitOrder>(Sort.ASK);
        bid = new TreeSet<LimitOrder>(Sort.BID);
        lastPrices = new LimitedLinkedList<PriceRecord>(10000);
        gateway = new ArrayList<StopLossLimitOrder>();
        toRemove = new ArrayList<StopLossLimitOrder>();

        extradayLog = new ArrayList<DayLog>(300);
        clear();
    }

    public void clear()
    {
        ask.clear();
        bid.clear();
        lastPrices.clear();
        numberOfOrdersReceived = 0;
        numberOfPricesFixed = 0;
        lastOrder = null;
        lastFixedPrice = null;

        /*
         * Bollinger - utilisé pour la trace Day
         */
        firstPriceOfDay = -1;
        lastPriceOfDay = -1;
        highestPriceOfDay = -1;
        lowestPriceOfDay = -1;
    }

    void printState()
    {
        System.err.println(obName + ": nb orders received  : " + numberOfOrdersReceived);
        System.err.println(obName + ": nb fixed prices     : " + numberOfPricesFixed + (lastPrices.isEmpty()?"":"\t (last : "+lastFixedPrice+")"));
        System.err.println(obName + ": leaving ask size    : " + ask.size() + (ask.isEmpty()?"":"\t (best : "+ask.first()+")"));
        System.err.println(obName + ": leaving bid size    : " + bid.size() + (bid.isEmpty()?"":"\t (best : "+bid.first()+")"));
        System.err.println(obName + ": leaving in gateway  : " + gateway.size());
    }

    public void setNewPrice(PriceRecord pc, Order o1, Order o2)
    {
        lastPrices.addFirst(pc); // comment to Speed Up
//        lastBestBidPrices.addFirst(new Long(ask.first().price));
        numberOfPricesFixed++;
        lastFixedPrice = pc;
        pc.timestamp = System.currentTimeMillis();
        /*
         * TRACE
         */
        log.price(pc, (ask.isEmpty() ? -1 : ask.first().price), 
                (bid.isEmpty() ? -1 : bid.first().price), o1, o2);
        /*
         * Bandes de Bollinger - Extraday
         */
        if (numberOfPricesFixed == 1)
        {
            firstPriceOfDay = pc.price;
            lowestPriceOfDay = pc.price;
        }
        if (pc.price > highestPriceOfDay)
            highestPriceOfDay = pc.price;

        if (pc.price < lowestPriceOfDay)
            lowestPriceOfDay = pc.price;
        lastPriceOfDay = pc.price;
    }

    public double getIntradayVariance(int n)
    {
        double avg = getIntradayMeanReturn(n);
        double sum = 0.0;
        double rent = 0.0;
        int nb = (int) (n >= numberOfPricesFixed ? numberOfPricesFixed - 1 : n - 1);
        // sum (p(t+1)-p(t))/p(t)
        for (int i = 1; i <= nb; i++)
        {
            // somme des ecarts à la moyenne puissance 2
            rent = (Math.log(lastPrices.get(i - 1).price) - Math.log(lastPrices.get(i).price));
            sum += Math.pow(rent - avg, 2);
        }
        return sum / nb;
    }

    // 10 means that we cumulate 9 rentabilities, thus computed on the 10 last prices.
    public double getIntradayMeanReturn(int n)
    {
        double rentability = 0.0;
        int nb = (int) (n >= numberOfPricesFixed ? numberOfPricesFixed - 1 : n - 1);
        // sum (p(t+1)-p(t))/p(t)  ou log(dernier)-log(avant)
        for (int i = 1; i <= nb; i++) // System.out.println(i+"\t"+lastPrices.get(i).price+"\t"+lastPrices.get(i-1).price);

            rentability += (Math.log(lastPrices.get(i - 1).price) - Math.log(lastPrices.get(i).price));
        return rentability / nb;
    }

    // utilise pour Cancel et Update le contains ne renvoie pas
    // l'ordre ...  le ask.remove ne peut pas fonctionner a  cause du
    // equals qui s'appuie sur extId alors que le compare s'appuie sur
    // le Id
    public LimitOrder findOrder(Agent sender, String extId, boolean ask)
    {
        TreeSet<LimitOrder> tab = (ask ? this.ask : this.bid);

        for (LimitOrder lo : tab)
            if (extId.equals(lo.extId) && lo.sender == sender)
                return lo;
        return null;
    }

    protected void stampAndLog(Order lo)
    {
        numberOfOrdersReceived++;
        lo.timestamp = System.currentTimeMillis();
        lo.id = numberOfOrdersReceived; // id unique gÃ©rÃ© manuellement car timestamp ne marche pas

        if (lo instanceof LimitOrder) {
            LimitOrder order = (LimitOrder) lo;
            if (order.quantity > maxQty) { maxQty = order.quantity; }
            if (order.quantity < minQty) { minQty = order.quantity; }
        }
        /*
         * TRACE
         */ log.order(lo);
    }

    synchronized void send(Order lo)
    {
        stampAndLog(lo);

        lo.execute(this);

        // At the end we use orders in Gateway
        // at this time there are only LimitOrders in the Gateway

        // le Gateway qui n'est pas trié actuellement. On part du
        // principe que le dernier entré est toujours à la fin.

        // SPEED : if you don't have any StopOrder ... comment this block, it
        // will run faster

        toRemove.clear();
        StopLossLimitOrder so=null;
        for (Iterator<StopLossLimitOrder> it = gateway.iterator(); it.hasNext();) {
            so = it.next();
            if (so.isTrue()) {
                //so.setId(clock);
                //clock++;
            	// System.out.println("sortie du gateway");
            	toRemove.add(so);
                so.executeSuper(this);
            }
        }
        // remove all triggered StopLimit orders
        for (Iterator<StopLossLimitOrder> it2 = toRemove.iterator(); it2.hasNext();) {
                so = it2.next();
                gateway.remove(so);
        }
        
        fixingEach();
        lastOrder = lo;
    }

    protected void fixingEach()
    {
        switch (MarketPlace.fixing)
        {
            case MarketPlace.CONTINUOUS:
                fixingEachContinuous();
                break;
            case MarketPlace.FIX:
                fixingEachPreopening();
                break;
            default:
                throw new RuntimeException("FixingEach unknown " + MarketPlace.fixing);
        }
    }

    private void fixingEachPreopening()
    {
        // empty
    }

    protected synchronized void fixingEachContinuous()
    {
        int cumulQuty = 0;
        long localLastPrice = 0;
        LimitOrder older = null, newer = null;

        LimitOrder bask = (ask.isEmpty() ? null : ask.first()); // ask.get(0));
        LimitOrder bbid = (bid.isEmpty() ? null : bid.first()); // bid.get(0));
        while (!ask.isEmpty() && !bid.isEmpty()
                && bask.price <= bbid.price)
        {
            // calcul du prix d'execution
            if (bask.id <= bbid.id)
            {
                older = bask;
                newer = bbid;
            }
            else
            {
                older = bbid;
                newer = bask;
            }
            long prixduplusancien = 0;
            if (bask.type == 'M' && bbid.type == 'M')
                prixduplusancien = lastFixedPrice.price; // sauf si jamais rien n'a ete fixe !
            else if (bask.type == 'M')
                prixduplusancien = bbid.price;
            else if (bbid.type == 'M')
                prixduplusancien = bask.price;
            else
                prixduplusancien = older.price;

            // calcul de la quantite a executer
            int pluspetitequantite;
            if (bask.quantity <= bbid.quantity)
                pluspetitequantite = bask.quantity;
            else
                pluspetitequantite = bbid.quantity;

            long money = prixduplusancien * pluspetitequantite;

            // modify the two agents
            bbid.sender.cash -= (1 + MarketPlace.cost) * money;
            bbid.sender.setInvest(obName, bbid.sender.getInvest(obName) + pluspetitequantite);
            bask.sender.cash += (1 - MarketPlace.cost) * money;
            bask.sender.setInvest(obName, bask.sender.getInvest(obName) - pluspetitequantite);
            MarketPlace.bank += (2 * MarketPlace.cost) * money;

            // verifier que personne ne se ruine
            if (!MarketPlace.acceptNegativeValues) // booleen declare dans MarketPlace
            {
                if (bbid.sender.cash < 0 || bask.sender.cash < 0)
                    throw new RuntimeException("cash négatif : " + bbid.sender.cash + " " + bask.sender.cash);
                if (bbid.sender.getInvest(obName) < 0 || bask.sender.getInvest(obName) < 0)
                    throw new RuntimeException("invest négatif : " + bbid.sender.getInvest(obName) + " " + bask.sender.getInvest(obName));
            }

            // mise a jour des ordres
            bbid.quantity -= pluspetitequantite;
            bask.quantity -= pluspetitequantite;

            localLastPrice = prixduplusancien;
            cumulQuty += pluspetitequantite;

            PriceRecord pc = null;
            if (MarketPlace.logType == MarketPlace.LONG)
            {

                pc = new PriceRecord(obName, prixduplusancien,
                        pluspetitequantite, newer.direction,
                        bask.sender.name + ";" + bask.extId,
                        bbid.sender.name + ";" + bbid.extId);
                setNewPrice(pc, bask, bbid);
            }
            // DEPLACEMENT DE LA NOTIFICATION A L'INTERIEUR DE L'ALTERNATIVE
            // notification aux agents
            bbid.sender.touchedOrExecutedOrder(bbid.quantity == 0 ? Event.EXECUTED : Event.UPDATED, bbid, pc);
            /*
             * TRACE
             */ log.agent(bbid.sender, lastOrder, lastFixedPrice);
            /*
             * TRACE
             */ if (bbid.quantity == 0)
                log.exec(bbid);
            bask.sender.touchedOrExecutedOrder(bask.quantity == 0 ? Event.EXECUTED : Event.UPDATED, bask, pc);
            /*
             * TRACE
             */ log.agent(bask.sender, lastOrder, lastFixedPrice);
            /*
             * TRACE
             */ if (bask.quantity == 0)
                log.exec(bask);

            // Nettoyage eventuel
            if (bbid.type == 'I' && bbid.quantity == 0)
                ((IcebergOrder) bbid).raiseIceberg();
            if (bbid.quantity == 0)
                bid.pollFirst(); //bid.remove(bbid);
            if (bask.type == 'I' && bask.quantity == 0)
                ((IcebergOrder) bask).raiseIceberg();
            if (bask.quantity == 0)
                ask.pollFirst(); // ask.remove(bask);
            bask = (ask.isEmpty() ? null : ask.first()); // ask.get(0));
            bbid = (bid.isEmpty() ? null : bid.first()); //bid.get(0));
            
        }

        if (MarketPlace.logType == MarketPlace.SHORT)
            if (localLastPrice != -1 && cumulQuty != 0)
            {
                PriceRecord pc = new PriceRecord(obName, localLastPrice, cumulQuty, newer.direction, newer.sender.name + ";" + newer.extId, "-");
                setNewPrice(pc, bask, bbid);
                // AJOUT DE LA NOTIFICATION LORSQUE L'ON EST EN SHORT
                if (bbid != null && bbid.sender != null)
                    bbid.sender.touchedOrExecutedOrder(bbid.quantity == 0 ? Event.EXECUTED : Event.UPDATED, bbid, pc);
                if (bask != null && bask.sender != null)
                    bask.sender.touchedOrExecutedOrder(bask.quantity == 0 ? Event.EXECUTED : Event.UPDATED, bask, pc);
            }
    }

    public void fixingEnd()
    {
        switch (MarketPlace.fixing)
        {
            case MarketPlace.CONTINUOUS:
                fixingEndContinuous();
                break;
            case MarketPlace.FIX:
                fixingEndPreopening();
                break;
            default:
                throw new RuntimeException("FixingEnd unknown " + MarketPlace.fixing);
        }
    }

    private void fixingEndContinuous()
    {
        // empty
    }

    /**
     * used only at the end of the opening procedure, to fix the price see
     * http://www.asx.com.au/resources/education/basics/open_Close.htm . First
     * we have to compute the price that could maximize the quantity of orders
     * executed. Then, we execute this quantity both on Ask and Bid parts. To
     * compute the price, we first need to use a Quintuplet Array , and then
     * compute in five steps.
     *
     */
    protected synchronized void fixingEndPreopening()
    {
        // the aim of this procedure is to compute the price to fix and the
        // quantity concerned for a pre-opening or closing period.
        // maxExec and fixedPrice are the two variables used for that

        // to be called at the end of the period.
        if (ask.isEmpty() || bid.isEmpty()
                || ask.first().price > bid.first().price) // System.err.println("No matching price at the end of Fix period");

            return;

        // we fulfill one array making a join on ask and bid on price
        HashMap<Double, Quintuplet> hm = new HashMap<Double, Quintuplet>();

        // FIRST STEP : collect all information from ASK
        for (Iterator<LimitOrder> it = ask.iterator(); it.hasNext();)
        {
            LimitOrder o = (LimitOrder) it.next();
            if (o.price <= bid.first().price)
            {
                // each price must appear only one time
                Double key = Double.valueOf(o.price);
                Quintuplet t = hm.get(key);
                if (t == null)
                {
                    t = new Quintuplet();
                    hm.put(key, t);
                }
                t.cumul1 = 0;
                t.qute1 += o.quantity;
                t.price = o.price;
                t.qute2 = 0; // at this time, these is no ask
                t.cumul2 = 0;
            }
        }
        // then collect all information BID
        for (Iterator<LimitOrder> it = bid.iterator(); it.hasNext();)
        {
            LimitOrder o = (LimitOrder) it.next();
            if (o.price >= ask.first().price)
            {
                // each price must appear only one time
                Double key = Double.valueOf(o.price);
                Quintuplet t = hm.get(key);
                if (t == null)
                {
                    t = new Quintuplet();
                    hm.put(key, t);
                }
                t.cumul1 = 0;
                // t.qute1 = 0; perheaps there is a bid !
                t.price = o.price;
                t.qute2 += o.quantity;
                t.cumul2 = 0;
            }
        }

        // TODO : le new ArrayList ne doit pas etre optimal ... Ã  revoir
        ArrayList<Quintuplet> al = new ArrayList<Quintuplet>(hm.values());

        // sort of decreasing prices
        Collections.sort(al, new Comparator<Quintuplet>()
        {
            public int compare(Quintuplet t1, Quintuplet t2)
            {
                return (int) (t2.price - t1.price);
            }
        });

        
        // SECOND STEP : compute the cumul of bid in ascending order on price
        int anc2 = 0;
        for (Iterator<Quintuplet> it = al.iterator(); it.hasNext();)
        {
            Quintuplet t = it.next();
            t.cumul2 = anc2 + t.qute2;
            anc2 = t.cumul2;
        }
        // compute the cumul of ask in decreasing order on price
        int anc1 = 0;
        for (int i = al.size() - 1; i >= 0; i--)
        {
            Quintuplet t = al.get(i);
            t.cumul1 = anc1 + t.qute1;
            anc1 = t.cumul1;
        }

        
        // THIRD STEP : compute the maxExec at each price (the min of the two cumuls)
        for (Iterator<Quintuplet> it = al.iterator(); it.hasNext();)
        {
            Quintuplet t = it.next();
            t.maxExec = Math.min(t.cumul1, t.cumul2);
        }

        // if there is only one val max, we can fix the price, but if there is
        // equality, we must continue.
        // FOURTH STEP : compute the minSurplus at each price
        for (Iterator<Quintuplet> it = al.iterator(); it.hasNext();)
        {
            Quintuplet t = it.next();
            t.minSurplus = (t.maxExec - t.cumul1) + (t.maxExec - t.cumul2);
        }

        // sort on maxExec then minSurplus to have the price !
        Collections.sort(al, new Comparator<Quintuplet>()
        {
            public int compare(Quintuplet t1, Quintuplet t2)
            {
                if (t2.maxExec - t1.maxExec != 0)
                    return (int) (t2.maxExec - t1.maxExec);
                else
                    return (int) (Math.abs(t1.minSurplus) - Math.abs(t2.minSurplus));
            }
        });

        // the price is now fixed
        int maxExec = al.get(0).maxExec;
        long fixedPrice = al.get(0).price;

        /*
          System.out.println(al); System.out.println("FIX \tPeriod: " +
          // m.getPeriod() + 
          "\t maxExec: " + maxExec + "\tFixedPrice: " + fixedPrice);
        */
          
        // ==================================
        // fix and clean maxExec in ask prt
        int maxExecAsk = maxExec;
        while (maxExecAsk != 0)
        {
            int quty; // quty executed; to log price
            LimitOrder bask = ask.first();
            if (maxExecAsk > bask.quantity)
            {
                quty = bask.quantity;
                maxExecAsk -= quty;
                bask.quantity = 0;
            }
            else
            {
                quty = maxExecAsk;
                bask.quantity -= maxExecAsk;
                maxExecAsk = 0;
            }

            long money = fixedPrice * quty;

            // modify the two agents
            bask.sender.cash += money;
            bask.sender.setInvest(obName, bask.sender.getInvest(obName) - quty);

            /*
             * LONG trace: pas de sens sinon on les loggue en ASK et en BID sans
             * savoir qui est contrepartie de qui
             */
            /*
             * if (MarketPlace.logType == MarketPlace.LONG) { PriceRecord hl =
             * new PriceRecord(obName,fixedPrice, quty, bask.direction,
             * bask.sender.name+";"+bask.extId,"noname"); setNewPrice(hl); }
             */
            if (bask.quantity == 0)
            {
                ask.pollFirst(); // ask.remove(bask);
                // remove is less efficient on PriorityQueue because it uses
                // compareTo
                bask.sender.touchedOrExecutedOrder(Event.EXECUTED, bask,
                        new PriceRecord(obName, fixedPrice, maxExec, 'p', "noname", "noname"));
                // Collections.sort(ask, sortAsk); must we sort again ?
            }
            else
                bask.sender.touchedOrExecutedOrder(Event.UPDATED, bask,
                        new PriceRecord(obName, fixedPrice, maxExec, 'p', "noname", "noname"));

            // update the best offers
            //try { // TO BE FIXED !
            //    bask = ask.first();
            //} catch (NoSuchElementException nse) {
            //    nse.printStackTrace();
            //}
        }

        // ==================================
        // fix and clean maxExec in bid part
        int maxExecBid = maxExec;
        while (maxExecBid != 0)
        {
            int quty; // quty executed; to log price
            LimitOrder bbid = bid.first();
            if (maxExecBid > bbid.quantity)
            {
                quty = bbid.quantity;
                maxExecBid -= quty;
                bbid.quantity = 0;
            }
            else
            {
                quty = maxExecBid;
                bbid.quantity -= maxExecBid;
                maxExecBid = 0;
            }

            long money = fixedPrice * quty;

            // modify the two agents
            bbid.sender.cash -= money;
            bbid.sender.setInvest(obName, bbid.sender.getInvest(obName) + quty);

            /*
             * LONG trace: pas de sens sinon on les loggue en ASK et en BID sans
             * savoir qui est contrepartie de qui
             */
            /*
             * if (MarketPlace.logType == MarketPlace.LONG) { PriceRecord hl =
             * new PriceRecord(obName,fixedPrice, quty, bbid.direction,
             * bbid.sender.name+";"+bbid.extId,"noname"); setNewPrice(hl); }
             */
            if (bbid.quantity == 0)
            {
                bid.pollFirst(); // Bid.remove(bBid);
                // remove is less efficient on PriorityQueue because it uses
                // compareTo
                bbid.sender.touchedOrExecutedOrder(Event.EXECUTED, bbid,
                        new PriceRecord(obName, fixedPrice, maxExec, 'p', "noname", "noname"));
                // Collections.sort(Bid, sortBid); must we sort again ?
            }
            else
                bbid.sender.touchedOrExecutedOrder(Event.UPDATED, bbid,
                        new PriceRecord(obName, fixedPrice, maxExec, 'p', "noname", "noname"));

            // update the best offers
            //try {
            //bbid = bid.first();
            //} catch (NoSuchElementException nse) {
            //    nse.printStackTrace();
            //}
        }

        //if (MarketPlace.logType == MarketPlace.SHORT) {
        if (fixedPrice != 0 && maxExec != 0)
        {
            // En pre-opening il n'y a pas de direction !
            PriceRecord hl = new PriceRecord(obName, fixedPrice, maxExec, 'p', "noname", "noname");
            setNewPrice(hl, null, null);

        }
        //}

    }

    /*public String printBest(int n)
    {
        StringBuffer sb = new StringBuffer();
        for (Iterator<LimitOrder> it = bid.iterator(); it.hasNext() && i<=n ; i++)
        {
            sb.append(it.next());
            sb.append("\n");
        }        
        for (Iterator<LimitOrder> it = bid.iterator(); it.hasNext() && i<=n ; i++)
        {
            sb.append(it.next());
            sb.append("\n");
        }
        return sb.toString();

    }*/

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("\n\n");
        for (Iterator<LimitOrder> it = ask.descendingIterator(); it.hasNext();)
        {
            sb.append(it.next());
            sb.append("\n");
        }
        sb.append("----------------\n");
        for (Iterator<LimitOrder> it = bid.iterator(); it.hasNext();)
        {
            sb.append(it.next());
            sb.append("\n");
        }
        return sb.toString();
    }

    public synchronized void decreaseAndDeleteUnvalid()
    {
        HashSet<LimitOrder> toRemove = new HashSet<LimitOrder>();
        for (Iterator<LimitOrder> it = ask.iterator(); it.hasNext();)
        {
            LimitOrder o = it.next();
            if (o.validity < 0)
                continue; // infinity
            o.validity--;
            // if (o.validity < 0) System.out.println("NEGATIF !!");
            if (o.validity == 0)
            {
                toRemove.add(o);
                o.sender.touchedOrExecutedOrder(Event.CANCELLED, o, null);
                /*
                 * TRACE
                 */ log.info("Destroy;" + o.sender.name + ";" + o.extId);
            }
        }
        ask.removeAll(toRemove);

        toRemove = new HashSet<LimitOrder>();
        for (Iterator<LimitOrder> it = bid.iterator(); it.hasNext();)
        {
            LimitOrder o = it.next();
            if (o.validity < 0)
                continue; // infinity
            o.validity--;
            // if (o.validity < 0) System.out.println("NEGATIF !!");
            if (o.validity == 0)
            {
                toRemove.add(o);
                o.sender.touchedOrExecutedOrder(Event.CANCELLED, o, null);
                /*
                 * TRACE
                 */ log.info("Destroy;" + o.sender.name + ";" + o.extId);
            }
        }
        bid.removeAll(toRemove);
    }
}
