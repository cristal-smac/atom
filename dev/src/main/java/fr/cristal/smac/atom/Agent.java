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

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public abstract class Agent
{

    public final String name;
    public long cash = 0;
    public int speed = 0;
    public MarketPlace market;
    protected long myId=1;
    public long numberOfOrdersSent=0;;
    protected Map<String, Integer> invest;

    public int getInvest(String name)
    {
        Integer result = invest.get(name);
        if (result == null)
        {
            invest.put(name, 0);
            return 0;
        } // autoboxing java 1.5
        else
            return result;
    }

    public void setInvest(String name, int value)
    {
        invest.put(name, value); // autoboxing java 1.5
    }

    // Compute the total valuation of the agent by summing its current cash
    // with the value of its invest at this time
    public long getWealth()
    {
        long total = cash;
        OrderBook ob;
        for (String inv : invest.keySet())
        {
            ob = market.orderBooks.get(inv);
            if (ob.numberOfPricesFixed > 0)
                total += ob.lastFixedPrice.price * invest.get(inv);
        }
        return total;
    }

    // Existe ou pas selon l'usage de initDayLog
    public long getOpenPrice(String obName, Day d) {
        OrderBook ob = market.orderBooks.get(obName);
        DayLog currentDayLog = ob.extradayLog.get(d.dayNumber);
        // on est sur une simulation non paramétrée par un fichier, donc
        // on récupère le premier prix fixé
        if (currentDayLog.LOW == -1) {
            if (ob.lastPrices.size() == 0) {
                throw new RuntimeException("No prices have been fixed yet !");
            }
            return ob.firstPriceOfDay;
        }
        // le premier prix fixé correspond maintenant au prix d'ouverture
        return currentDayLog.OPEN;
    }
    
    public long getHighestPrice(String obName, Day d) {
        // On n'a pas d'historique (DayLog) de la journée
        if (market.orderBooks.get(obName).extradayLog == null) {
            return market.orderBooks.get(obName).highestPriceOfDay;
        }
        return market.orderBooks.get(obName).extradayLog.get(d.dayNumber-1).HIGH;
    }
    
    public long getLowestPrice(String obName, Day d) {
        // On n'a pas d'historique (DayLog) de la journée
        if (market.orderBooks.get(obName).extradayLog == null) {
            return market.orderBooks.get(obName).lowestPriceOfDay;
        }
        return market.orderBooks.get(obName).extradayLog.get(d.dayNumber-1).LOW;
    }
    
    public long getWorstAskPrice(String obName) {
        return market.orderBooks.get(obName).ask.last().price;
    }
    public long getWorstBidPrice(String obName) {
        return market.orderBooks.get(obName).ask.last().price;
    }
    public long getBestAskPrice(String obName) {
        return market.orderBooks.get(obName).ask.first().price;
    }
    public long getBestBidPrice(String obName) {
        return market.orderBooks.get(obName).ask.first().price;
    }
    
    public Agent(String name, long cash)
    {
        this.name = name;
        this.invest = new HashMap<String, Integer>();
        clear();
        this.cash = cash;
        speed = 0;
    }

    public Agent(String name)
    {
        this(name, 0);
    }

    public void clear()
    {
        myId = 1;
        cash = 0;
        numberOfOrdersSent = 0;
        this.invest.clear();
        /*
         * la ligne précédente subsume ce paquet
        for (String key : invest.keySet())
        invest.put(key, 0);
        
         */
    }

    public void init()
    {
    }

    
    public Map<String, Object> getState()
    {
        Map<String, Object> state = new HashMap<String, Object>();
        try
        {
            Class current = getClass();
            while (!current.equals(Object.class))
            {
                Field[] fields = current.getDeclaredFields();
                for (Field f : fields)
                {
                    f.setAccessible(true);
                    if (f.getType().isPrimitive())
                        state.put(f.getName(), f.get(this));
                    else
                    {
                        Object value = f.get(this);
                        if (value != null && !value.getClass().equals(MarketPlace.class))
                        {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(baos);
                            out.writeObject(value);
                            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
                            Object copy = in.readObject();
                            state.put(f.getName(), copy);
                        }
                    }
                }
                current = current.getSuperclass();
            }
        } catch (IllegalAccessException iae)
        {
            iae.printStackTrace();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
        }
        return state;
    }

    public void setState(Map<String, Object> state)
    {
        Class current = getClass();
        try
        {
            while (!current.equals(Object.class))
            {
                Field[] fields = current.getDeclaredFields();
                for (Field f : fields)
                {
                    f.setAccessible(true);
                    f.set(this, state.get(f.getName()));
                }
                current = current.getSuperclass();
            }
        } catch (IllegalAccessException iae)
        {
            iae.printStackTrace();
        }
    }

    // utilisation d'un comportement. Retourne NULL ou un ordre
    public abstract Order decide(String obName, Day day);
    
    public void beforeDecide(String obName, Day day) {
    }

    public void afterDecide(String obName, Day day, Order o) {
                    /*
             * C'est à l'agent qu'incombe la responsabilité de gérer les Id de
             * ses ordres. s'il ne le fait pas, il ne pourra y faire référence
             * dans d'autres ordres comme des Cancel ou des Update
             */
        if (o != null) {myId++; numberOfOrdersSent++;}
    }

    public void touchedOrExecutedOrder(Event e, Order o, PriceRecord p) {
        // NOTIFICATION : do what you want
    }
    
    public void broadcastNews(Day day, Map<String, Agent> tradingAgents) {
        // to be defined to send news to trading agents
    }
    
    public Object news(Day day, Object news) {
        // to be defined to handle news sent by non trading agents
        return null;
    }

    public String toString()
    {
        String result = "";
        result = name + "(" + cash + ",[";
        for (String key : invest.keySet())
            result += key + ":" + getInvest(key) + " ";
        result += "])";
        return result;
    }
}
