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
import java.util.StringTokenizer;
import fr.cristal.smac.atom.orders.*;
import fr.cristal.smac.atom.agents.*;

/**
 * A simple order parser that transform a string representing an order in its
 * corresponding Object.
 *
 * Usage: StringOrderParser.parse("toto;LVMH;1;M;A;10000");
 *
 */
public class StringOrderParser {

    public static final char FIX = 'F';
    public static final char CONTINUOUS = 'C';
    public static final char CLOSE = 'K';
    public static final char PRINT = 'P';
    public static final char SHORT = 'S';
    public static final char LONG = 'L';

    public static boolean isCommentOrEmpty(String currentLine) {
        return (currentLine == null | currentLine.length() == 0
                || currentLine.charAt(0) == '#'
                || currentLine.startsWith("Order;obname")
                || currentLine.startsWith("Tick;numtick")
                || currentLine.startsWith("Price;obname")
                || currentLine.startsWith("Agent;name")
                || currentLine.startsWith("Exec;agent")
                || currentLine.startsWith("Day;numday"));
    }

    public static boolean isDay(String currentLine) {
        return currentLine.startsWith("Day");
    }

    public static boolean isTick(String currentLine) {
        return currentLine.startsWith("Tick");
    }

    public static boolean isCommand(String currentLine) {
        return currentLine.startsWith("!");
    }

    public static boolean isOrder(String currentLine) {
        return currentLine.startsWith("Order");
    }

    public static boolean isAgent(String currentLine) {
        return currentLine.startsWith("Agent");
    }

    public static boolean isPrice(String currentLine) {
        return currentLine.startsWith("Price");
    }

    public static boolean isInfo(String currentLine) {
        return currentLine.startsWith("Info");
    }

    public static boolean isAuctions(String currentLine) {
        return currentLine.startsWith("Auctions");
    }

    public static boolean isExec(String currentLine) {
        return currentLine.startsWith("Exec");
    }

    public static void parseAndexecuteCommand(String currentLine, Simulation s) {
        if (currentLine.charAt(0) == '!' && currentLine.length() >= 2) {
            switch (currentLine.charAt(1)) {
                case PRINT:
                    // on affiche tous les orderbook
                    for (String obName : s.market.orderBooks.keySet()) {
                        OrderBook ob = s.market.orderBooks.get(obName);
                        // On n'utilise pas le ob.toString car il nous faut un "#" devant
                        StringBuffer sb = new StringBuffer("\n");
                        sb.append("#-----Order Book BEGIN -----\n");
                        for (Iterator<LimitOrder> it = ob.ask.descendingIterator(); it.hasNext();) {
                            sb.append(it.next());
                            sb.append("\n");
                        }
                        sb.append("#----------------\n");
                        for (Iterator<LimitOrder> it = ob.bid.iterator(); it.hasNext();) {
                            sb.append(it.next());
                            sb.append("\n");
                        }
                        sb.append("\n");
                        sb.append("#-----Order Book END -----\n");
                        s.log.print(sb.toString());
                    }
                    break;
                case CONTINUOUS:
                    // System.out.println("COMMAND: Setting fixing mechanism to CONTINUOUS");
                    s.market.setFixingPeriod(MarketPlace.CONTINUOUS);
                    break;
                case FIX:
                    // System.out.println("COMMAND: Setting fixing mechanism to FIX");
                    s.market.setFixingPeriod(MarketPlace.FIX);
                    break;
                case CLOSE:
                    // System.out.println("COMMAND: Closing market and fixing closing prices");
                    s.market.close();
                    break;
                case SHORT:
                    MarketPlace.logType = MarketPlace.SHORT;
                    break;
                case LONG:
                    MarketPlace.logType = MarketPlace.LONG;
                    break;
                default:
                    System.err.println("COMMAND: invalid command: " + currentLine);
                    System.exit(1);
            }
        }
    }

    /**
     *
     * obName open low high closing nbPricesFixed
     * Day;2;IBM;14725;14109;14725;14725;12;
     *
     * @param dayLog line with describes a day in the log
     * @param s      the simulation class
     * @return a new daylog
     */
    public static DayLog parseDay(String dayLog, Simulation s) {
        String[] tokens = dayLog.split(";");
        String obName = tokens[2];
        createOrderbookIfItDoesNotExistYet(obName, s);
        long open = Long.parseLong(tokens[3]);
        long low = Long.parseLong(tokens[4]);
        long high = Long.parseLong(tokens[5]);
        long close = Long.parseLong(tokens[6]);
        return new DayLog(obName, open, low, high, close);
    }

    public static PriceRecord parsePrice(String priceLog, Simulation s) {
        // Price;AAPL;14198;57;A;zit_1-3;zit_2-3;14002;14198
        StringTokenizer parse = new StringTokenizer(priceLog, ";");
        parse.nextToken(); // "Price"
        String obName = parse.nextToken();

        // si l'orderbook n'existe pas, on le cree
        OrderBook ob = s.market.orderBooks.get(obName);
        if (ob == null) {
            s.addNewOrderBook(obName);
        }

        String price = parse.nextToken();
        String quty = parse.nextToken();
        if (quty.isEmpty()) {
            quty = "0";
        }
        String dir = parse.nextToken();
        if (dir.isEmpty()) {
            dir = "X";
        }
        return new PriceRecord(obName, Long.parseLong(price),
                Integer.parseInt(quty), dir.charAt(0), "X", "X");
    }

    protected static void createOrderbookIfItDoesNotExistYet(String obName, Simulation s) {
        // si l'orderbook n'existe pas, on le cree
        OrderBook ob = s.market.orderBooks.get(obName);
        if (ob == null) {
            s.addNewOrderBook(obName);
        }
    }

    public static Order parseOrder(String currentLine, Simulation s) {
        // String obName2Test = null;
        char dir;
        int qty, part, total;
        String idToKill;
        long price, prixIntro, seuil;
        Order order = null;

        String cols[] = currentLine.split(";");
        String extId = cols[3];
        char type = cols[4].charAt(0);

        String obName = cols[1];
        createOrderbookIfItDoesNotExistYet(obName, s);

        // create and send the order
        switch (type) {
            case 'L':
                dir = cols[5].charAt(0);
                price = Long.parseLong(cols[6]);
                qty = Integer.parseInt(cols[7]);
                order = new LimitOrder(obName, extId, dir, qty, price);
                break;
            case 'M':
                dir = cols[5].charAt(0);
                qty = Integer.parseInt(cols[6]);
                order = new MarketOrder(obName, extId, dir, qty);
                break;
            case 'I':
                dir = cols[5].charAt(0);
                price = Long.parseLong(cols[6]);
                part = Integer.parseInt(cols[7]);
                total = Integer.parseInt(cols[8]);
                order = new IcebergOrder(obName, extId, dir, part, total, price);
                break;
            case 'C':
                idToKill = cols[5];
                order = new CancelOrder(obName, extId, idToKill);
                break;
            case 'U':
                idToKill = cols[5];
                qty = Integer.parseInt(cols[6]);
                order = new UpdateOrder(obName, extId, idToKill, qty);
                break; // price=orderToUpdate
            case 'V':
                // cancel the order
                idToKill = cols[5];
                Order orderCancel = new CancelOrder(obName, extId, idToKill);
                orderCancel.sender = s.agentList.get(cols[2]);

                s.market.send(orderCancel.sender, orderCancel);
                // add the new order with new price & quantity
                qty = Integer.parseInt(cols[6]);
                price = Integer.parseInt(cols[7]);
                dir = cols[5].charAt(0);
                order = new UpdatePriceVolumeOrder(obName, extId, idToKill, qty, price);

                break; // price=orderToUpdate
            case 'T': // MarketToLimit
                dir = cols[5].charAt(0);
                qty = Integer.parseInt(cols[6]);
                order = new MarketToLimitOrder(obName, extId, dir, qty);
                break;
            case 'S': // StopLossLimitOrder
                dir = cols[5].charAt(0);
                prixIntro = Long.parseLong(cols[6]);
                qty = Integer.parseInt(cols[7]);
                seuil = Long.parseLong(cols[8]);
                order = new StopLossLimitOrder(obName, extId, dir, qty, prixIntro, seuil);
                break;
            case 'R': // StopLossMarketOrder
                dir = cols[5].charAt(0);
                qty = Integer.parseInt(cols[6]);
                seuil = Long.parseLong(cols[7]);
                order = new StopLossMarketOrder(obName, extId, dir, qty, seuil);
                break;
            default:
                System.err.println("Order skipped : " + type);
        }

        String agentName = cols[2];
        Agent a = s.agentList.get(agentName);
        if (a == null) {
            a = new DumbAgent(agentName, 0);
            s.addNewAgent(a);
        }

        order.sender = a;

        // System.err.println("StringOrderParser: "+order);
        return order;
    }

}
