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

import fr.cristal.smac.atom.orders.LimitOrder;

import java.io.*;
import java.util.*;


/*

*/


public class Logger {

    private PrintStream pw = null;
    protected MarketPlace market = null;
    
    public int orderbooksVisibleSpread = 0; // FINAXYS

    public static Logger getLogger() {
        return new Logger();
    }

    public void setMarket(MarketPlace market) {
        this.market = market;
    }
    
    
    private void printHeaders()
    {
    	pw.println("Order;obname;agent;Oid;type;dir;price;quty;validity");
    	pw.println("Tick;numtick;obname;bestask;bestbid;lastFixedPrice");
    	pw.println("Price;obname;price;quty;dirTrigger;AgTrigger;Oid;ag2;Oid2;bestask;bestbid");
    	pw.println("Agent;name;cash;obName;invests;LastFixedPrice");
    	pw.println("Exec;agent;Oid");
    	pw.println("Day;numday;obName;firstPrice;lastPrice;lowestPrice;highestPrice;nbPricesFixed");
    	// pw.println("Orderbook;5xbestask;5xbestbid");
    }
    
    public Logger() {
        this.pw = null;
    }

    public Logger(PrintStream pw) {
        this.pw = pw;
        printHeaders();
    }

    public Logger(String filename) {
        try {
            pw = new PrintStream(filename);
            printHeaders();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void println(String s) {
        if (pw != null) {
            pw.println(s);
            pw.flush();
        }
    }
    
    public void println() {
        print("\n");
    }
    
    public void print(String s) {
        if (pw != null) {
            pw.print(s);
            pw.flush();
        }
    }

    public void error(Exception e) {
        if (pw != null) {
            println("#ERROR;" + e.getMessage());
        }
    }

    public void order(Order o) {
        if (pw != null) {
            println(o.toString());
        }
    }

    public void day(int nbDays, Collection<OrderBook> orderbooks) {
        if (pw != null) {
            StringBuilder sb;
            for (OrderBook ob : orderbooks) {
                    /*
                    print(ob.obName + ";"+(ob.lastFixedPrice != null ? ob.lastFixedPrice.price : "none") + ";"
                    + ob.numberOfPricesFixed+";");
                    */
                
                    sb = new StringBuilder();
                    sb.append(ob.obName).append(";").append(ob.firstPriceOfDay);
                    sb.append(";").append(ob.lowestPriceOfDay).append(";");
                    sb.append(ob.highestPriceOfDay).append(";").append(ob.lastPriceOfDay);
                    sb.append(";").append(ob.numberOfPricesFixed).append(";");
                    println("Day;" + nbDays + ";"+sb.toString());
                
                    // A terme remplacer ce code par un dump des DayLog
                    // et remplacer le nombre de prix fixés par les volumes échangés
                /*
                    System.out.println(ob.obName+" > "+ob.extradayLog);
                    println("Day;" + nbDays + ";"+ob.extradayLog.get(nbDays-1).toString());
                */
            }
        }
    }

    public void tick(Day day, Collection<OrderBook> orderbooks) {
        if (pw != null) {
            for (OrderBook ob : orderbooks) {
                StringBuilder sb = new StringBuilder();
                sb.append("Tick;").append(day.currentPeriod().currentTick()).append(";");
                sb.append(ob.obName).append(";" + (ob.ask.size() > 0 ? ob.ask.first().price : "0"));
                sb.append(";").append(ob.bid.size() > 0 ? ob.bid.first().price : "0");
                sb.append(";").append(ob.lastFixedPrice != null ? ob.lastFixedPrice.price : "0").append(";");
                println(sb.toString());
            }
        }
    }

    public void exec(Order o) {
        if (pw != null) {
            println("Exec;" + o.sender.name + ";" + o.extId);
        }
    }

    protected String dumpOrderBook(String obName) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Auctions;").append(obName).append(";");
        
        OrderBook ob = market.orderBooks.get(obName);
        int i = 0;
        Iterator<LimitOrder> it = ob.ask.iterator();
        sb.append("ASK;");
        while (it.hasNext() && i < orderbooksVisibleSpread) {
            sb.append(it.next().price).append(";");
            i++;
        }
        sb.append("\n");
        it = ob.bid.iterator();
        i = 0;
        sb.append("Auctions;").append(obName).append(";").append("BID;");
        while (it.hasNext() && i < orderbooksVisibleSpread) {
            sb.append(it.next().price).append(";");
            i++;
        }
        sb.append("\n");
        return sb.toString();
    }
    
    public void price(PriceRecord p,long bestAskPrice, long bestBidPrice, Order bask, Order bbid) {
        if (pw != null) {
            println("Price;" + p + ";" + bestAskPrice + ";" + bestBidPrice);
            // Preference FINAXYS - Bittner
            // println("Price;" + p + ";"+bask+";"+bbid);
            if (orderbooksVisibleSpread > 0) {
                print(dumpOrderBook(p.obName));
            }
        }
    }

    public void agent(Agent a, Order o, PriceRecord lastFixed) {
        if (pw != null) {
            println("Agent;" + a.name + ";" + a.cash + ";" + o.obName + ";" + a.getInvest(o.obName) + ";" + (lastFixed != null ? lastFixed.price : "none"));
        }
    }

    public void command(char c) {
        if (pw != null) {
            println("!" + c);
        }
    }

    public void info(String s) {
        if (pw != null) {
            println("Info;" + s);
        }
    }
}
