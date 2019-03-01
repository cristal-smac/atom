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

import java.io.*;
import java.util.*;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;

/*
 * Cet agent envoie uniquement les ordres qu'on lui a donné à la création. Il
 * fonctionne comme un automate. Le booléen permet d'indiquer si on attend ou
 * pas l'exécution de chaque ordre avant d'en envoyer un nouveau. Avec false, on
 * envoie systématiquement, avec true on attend l'exécution complète du
 * précédent.
 */
public class Automata extends Agent
{

    private boolean executed = true;
    private List<Order> orders;
    private int ind = 0;
    private boolean wait;

    public Automata(String name, String filename, boolean wait) throws FileNotFoundException
    {
        super(name);
        this.wait = wait;
        String line = "";
        orders = new ArrayList<Order>();
        BufferedReader file = new BufferedReader(new FileReader(filename));
        Simulation bidon = new MonothreadedSimulation();
        try
        {
            while ((line = file.readLine()) != null)
                //System.err.println(">>> "+line);
                orders.add(StringOrderParser.parseOrder(line, bidon));
        }
        catch (IOException ex)
        {
            System.err.println("Automata: problem while analyzing file " + filename);
            System.err.println("Automata: offending line: " + line);
            System.exit(1);
        }
    }

    public Automata(String name, List<Order> orders, boolean wait)
    {
        super(name);
        this.orders = orders;
        this.wait = wait;
    }

    public void touchedOrExecutedOrder(Event e, Order o, PriceRecord p)
    {
        if (e == Event.EXECUTED && o.extId.equals(orders.get(ind - 1).extId))
        {
            //System.out.println("well executed " + o.extId);
            executed = true;
        }
        //else
            //System.out.println("only touched " + o.extId);
    }

    public Order decide(String investName, Day day)
    {
        if (ind == orders.size())
        {
            //System.out.println(this.name + " has no more order");
            return null;
        }

        if (executed || !wait)
        {
            //System.out.println(this.name + " post the next order");
            executed = false;
            if (ind < orders.size())
            {
                ind++;
                return (Order) orders.get(ind - 1);
            }
        }
        return null;
    }

    public static void main(String args[]) throws IOException
    {
        Simulation sim = new MonothreadedSimulation();
        //sim.setLogger(new Logger(System.out));
        sim.addNewOrderBook("lvmh");

        FileWriter out = new FileWriter("bids.txt");
        List<Order> bids = new ArrayList<Order>();
        for (int i = 1; i <= 10; i++)
        {
            Order o = new LimitOrder("lvmh", "b" + i, LimitOrder.BID, 2 * i, (long) 10 * i);
            bids.add(o);
            out.write(o + "\n");
        }
        out.close();

        out = new FileWriter("asks.txt");
        List<Order> asks = new ArrayList<Order>();
        for (int i = 1; i <= 10; i++)
        {
            Order o = new LimitOrder("lvmh", "ax" + i, LimitOrder.ASK, i, (long) 10 * i);
            asks.add(o);
            out.write(o + "\n");
            o = new LimitOrder("lvmh", "ay" + i, LimitOrder.ASK, i, (long) 10 * i);
            asks.add(o);
            out.write(o + "\n");
        }
        out.close();

        Automata buyer = new Automata("buyer", bids, true);
        Automata seller = new Automata("seller", asks, true);
        sim.addNewAgent(buyer);
        sim.addNewAgent(seller);
        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 25), 1);//1 jour de 10 tours

        try
        {
            Thread.currentThread().sleep(1000);
        }
        catch (InterruptedException ex)
        {
            System.out.println(ex.getMessage());
        }

        sim.market.printState();
        sim.market.close();

        //======== même chose mais à partir du fichier contenant les ordres
        try
        {
            Thread.currentThread().sleep(1000);
        }
        catch (InterruptedException ex)
        {
            System.out.println(ex.getMessage());
        }

        sim = new MonothreadedSimulation();
        //sim.setLogger(new Logger(System.out));
        sim.addNewOrderBook("lvmh");

        buyer = new Automata("buyer", "bids.txt", true);
        seller = new Automata("seller", "asks.txt", true);
        sim.addNewAgent(buyer);
        sim.addNewAgent(seller);
        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 25), 1);//1 jour de 10 tours
        try
        {
            Thread.currentThread().sleep(1000);
        }
        catch (InterruptedException ex)
        {
            System.out.println(ex.getMessage());

        }
        sim.market.printState();
        sim.market.close();

    }
}
