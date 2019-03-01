package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.Agent;
/*
 * Comment executer deux experiences avec les memes agents mis à zero et les
 * mêmes ordres.
 * ça fonctionne bien si les agents ne possèdent ni cash ni invest
 */
import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.*;

import fr.cristal.smac.atom.agents.DumbAgent;
/*
 * Sans classe Simulation
 */
public class TestReinitSimu
{

    public static void main(String args[])
    {
        // COMMENT FAIRE SA PROPRE EXPERIENCE

        MarketPlace market = new MarketPlace();
        String obName = "lvmh";
        market.orderBooks.put(obName, new OrderBook(obName));

        Order orders[] =
        {
            new LimitOrder(obName, "a", LimitOrder.BID, 50, (long) 100),
            new LimitOrder(obName, "b", LimitOrder.BID, 30, (long) 110),
            new LimitOrder(obName, "c", LimitOrder.ASK, 10, (long) 150),
            new LimitOrder(obName, "d", LimitOrder.ASK, 20, (long) 130),
            new LimitOrder(obName, "e", LimitOrder.BID, 35, (long) 135),
            new LimitOrder(obName, "f", LimitOrder.ASK, 100, (long) 100)
        };

        // on cree autant d'agents que d'ordres avec les mêmes indices
        Agent agents[] = new Agent[orders.length];
        for (int i = 0; i < orders.length; i++)
            agents[i] = new DumbAgent("a" + i);



        /*
         * L'EXPERIENCE COMMENCE ICI : PREMIERE EXECUTION
         */

        // Nettoyer les agents : cash=0 invests=vide
        for (Agent a : agents)
            a.clear();

        // envoyer les ordres
        for (int i = 0; i < orders.length; i++)
        {
            orders[i].sender = agents[i];
            market.send(agents[i], orders[i]);
        }

        market.printState();


        // PB : dans l'ordre il faut le nom de l'agent !!

        /*
         * L'EXPERIENCE REPREND : SECONDE EXECUTION
         */

        market.clear();  // 

        // Nettoyer les agents : cash=0 invests=vide
        
         for (Agent a : agents)
         a.clear();
         
        // OU
        // réinstancier les agents
        /*
        agents = new Agent[orders.length];
        for (int i = 0; i < orders.length; i++)
            agents[i] = new DumbAgent("a" + i);
            */

        // réinitialiser les ordres
        for (Order o : orders)
        {
            LimitOrder lo = ((LimitOrder) o);
            lo.quantity = lo.initQuty;
        }

        // envoyer les ordres
        for (int i = 0; i < orders.length; i++)
        {
            orders[i].sender = agents[i];
            market.send(agents[i], orders[i]);
        }

        market.printState();


        market.close();
    }
}


/*
 * - Est-ce que ça fonctionne avec n'importe quelle serie d'ordres ?
 * 
 * - Est-ce qu'il est préférable de faire des clone ou utiliser initquty ?
 * 
 * - Si on devait réinitialiser le cash et l'invest on fait comment ? (voir Matthis)
 * 
 */
