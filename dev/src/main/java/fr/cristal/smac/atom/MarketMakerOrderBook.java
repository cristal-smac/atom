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

import java.util.Iterator;
import fr.cristal.smac.atom.agents.DumbAgent;
import fr.cristal.smac.atom.orders.LimitOrder;;


/**
 *
 * Fonctionnement du MarketMaker : Ne fonctionne qu'en quotation continue. le
 * marketmaker se fixe au depart une meilleure limite ask et une meilleure
 * limite bid qu'il concrétise par deux ordres placés en tête des carnets
 * d'ordres. Il raisonne ensuite à chaque réception d'un ordre. Soit cet ordre
 * est compatible avec ses propres propositions auquel cas, il execute l'ordre.
 * soit ce n'est pas compatible, auquel cas il classe dans le carnet d'ordre
 * l'ordre non compatible. Si sa meilleure proposition a été exécutée il remet
 * une meilleure proposition juste au dessous des ordres encore présents dans le
 * carnet (ici +/-1 en prix et 10% du meilleur en qute): il donc s'arrange
 * toujours pour être en tête
 */
public class MarketMakerOrderBook extends OrderBook
{

    public DumbAgent mm;
    int myId;

    public MarketMakerOrderBook(String obName, long cash, int qute)
    {
        super(obName);
        mm = new DumbAgent("mm", cash);
        mm.setInvest(obName, qute);
        myId = 0;
    }

    public MarketMakerOrderBook(String obName)
    {
        this(obName, 0, 0);
    }

    public void marketMakerReasoning()
    {
        // j'enleve les ordres du mm s'il en reste 
        // parce qu'il faut réévaluer le prix dans tous les cas, et donc réordonner
        // C'est une sorte de Cancel mais aux forceps ;-)
        if (!ask.isEmpty() && ask.first().sender != null)
        // le MM est en premiere position
        {
            if (mm.equals(ask.first().sender))
            {
                ask.pollFirst();
            }
            else
            {
                // ou alors il est forcément en 2è après celui qui vient d'etre introduit
                Iterator it = ask.iterator();
                it.next();
                if (it.hasNext())
                {
                    LimitOrder lo = (LimitOrder) it.next();
                    if (mm.equals(lo.sender))
                    {
                        ask.remove(lo);
                    }
                }
            }
        }

        if (!bid.isEmpty() && bid.first().sender != null)
        {
            if (mm.equals(bid.first().sender))
            {
                bid.pollFirst();
            }
            else
            {
                Iterator it = bid.iterator();
                it.next();
                if (it.hasNext())
                {
                    LimitOrder lo = (LimitOrder) it.next();
                    if (mm.equals(lo.sender))
                    {
                        bid.remove(lo);
                    }
                }
            }
        }

        // quand on arrive ici il n'y a plus d'ordre du MM
        // il faut en remettre un de chaque coté


        // on va d'abord chercher les prix et les quantités
        // coté ASK
        int bbq = (bid.isEmpty() ? 100 : (int) (1.01 * bid.first().quantity));
        long bap = (ask.isEmpty() ? Long.MAX_VALUE : ask.first().price - 1);
        // Coté BID
        int baq = (ask.isEmpty() ? 100 : (int) (1.01 * ask.first().quantity));
        long bbp = (bid.isEmpty() ? 0 : bid.first().price + 1);

        // Si la fourchette n'est que de 1, on a des prix qui se croisent !!!!
        if (bap < bbp) { // Dans ce cas, on réinverse les prix ...
            bap = bbp;
            bbp --;
        }
        
        // On réintroduit les ordres du MarketMaket
        // on n'appelle pas send pour éviter la récursivité
        LimitOrder lo = new LimitOrder(obName, "" + myId, LimitOrder.ASK, bbq, bap);
        myId++;
        lo.sender = mm;
        marketMakerStampAndLog(lo);
        ask.add(lo);

        // on n'appelle pas send pour éviter la récursivité
        lo = new LimitOrder(obName, "" + myId, LimitOrder.BID, baq, bbp);
        lo.sender = mm;
        myId++;
        marketMakerStampAndLog(lo);
        bid.add(lo);
        // il y a juste un bug ... le MM doit s'etre positionné des le départ ! donc dans setFixingPeriod


        // System.out.println(mm); // .market.orderBooks.get("lvmh").lastFixedPrice+";"+ mm.getInvest("lvmh") + ";"+mm.getWealth());
    }

    protected synchronized void fixingEachContinuous()
    {
        super.fixingEachContinuous();
        marketMakerReasoning();
    }

    protected synchronized void fixingEndPreopening()
    {
        super.fixingEndPreopening();
        marketMakerReasoning();
    }
    
    /**
     * Pour éviter le problème du MarketMaker qui n'arrive pas à se mettre 
     * en tête car la fourchette est trop petite (écart de 1 entre le prix du
     * meilleur ASK et du meilleur BID), on trafique le timestamp.
     * 
     * Cette méthode est copiée/collée d'OrderBook avec juste un changement 
     * sur le timestamp.
     * 
     * @param lo limit order to execute by the markerMaker
     */
    protected void marketMakerStampAndLog(Order lo)
    {
        numberOfOrdersReceived++;
        lo.timestamp = 0;
        lo.id = numberOfOrdersReceived; // id unique gere manuellement car timestamp ne marche pas

        /*
         * TRACE
         */ log.order(lo);
    }
    
}

class Test
{
    public static void main(String args[])
    {

        Simulation s = new MonothreadedSimulation();
        String obName = "lvmh";
        MarketMakerOrderBook mm = new MarketMakerOrderBook(obName);
        // more precise : MarketMaker(obName,cash,invest)
        s.addNewMicrostructure(mm);

        s.setLogger(new Logger(System.out));
        DumbAgent a = new DumbAgent("buyer", 10000);
        DumbAgent v = new DumbAgent("seller", 10000);
        s.addNewAgent(a);
        s.addNewAgent(v);

        // Quand il commence il doit sortir d'une periode FIX
        mm.marketMakerReasoning();
        // System.out.println(s.market.orderBooks.get("lvmh"));

        // on introduit un ordre qui doit repositionner le MM
        s.market.send(v, new LimitOrder("lvmh", "0", LimitOrder.ASK, 2, (long) 180));
        // System.out.println(s.market.orderBooks.get("lvmh"));

        s.market.send(a, new LimitOrder("lvmh", "0", LimitOrder.BID, 1, (long) 160));
        // System.out.println(s.market.orderBooks.get("lvmh"));


        // maintenant un ordre qui match
        s.market.send(a, new LimitOrder("lvmh", "0", LimitOrder.BID, 3, (long) 200));
        // il execute l'ordre du MM, execute aussi l'ordre du départ.
        // le marketmaker reintroduit un Ask
        // System.out.println(s.market.orderBooks.get("lvmh"));


        /*
         * Simulation s = new MonothreadedSimulation();
         * s.addNewMarketMaker("lvmh"); //
         * ((MarketMaker)s.market.orderBooks.get("lvmh")).mm.market=s.market ;
         * // bidouille !! pour que l'agent connaisse le marché
         *
         * s.setLogger(new Logger(System.out)); for (int i = 1; i <= 10; i++)
         * s.addNewAgent(new ZIT("z" + i));
         *
         * s.runExtraday(1,10,3,0);
         *
         * System.out.println(s.market.orderBooks.get("lvmh"));
         *
         */
    }
}
