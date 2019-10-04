package fr.cristal.smac.atom.test;

import java.util.*;

import fr.cristal.smac.atom.Agent;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.FilteredLogger;
import fr.cristal.smac.atom.MarketPlace;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Order;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.DumbAgent;
import fr.cristal.smac.atom.agents.ZIT;
import fr.cristal.smac.atom.orders.MarketOrder;

/*
 PRINCIPE DES AGENTS INFORMES

/*
 Avec ATOM, réaliser des agents "informateurs" avec un agent Trader classique 
 n'est pas chose aisée. En effet, l'agent Trader n'a pas accès à l'expérience 
 générale (mais uniquement au market) et n'accède donc pas à la liste des autres 
 agents, ce qui limite fortement ses capacités.

 Nous avons donc mis en place un mecanisme d'agents "informateurs" qui ont la 
 capacité d'accéder à la liste des autres agents afin de leur diffuser de 
 l'information. Une information est un objet Java classique, qui peut donc 
 renfermer n'importe quel type d'information. L'agent informateur est appelé 
 à chaque fin d'un tour de parole (chaque changement de tick). Cet aspect des 
 choses permet d'assurer une certaine équité entre les agents : dans un tour 
 de parole, tous ont la même information.

 Un agent "informateur" est un agent de base (DumbAgent) qui n'est pas destiné 
 à "trader". S'il a une méthode "decide", celle-ci n'est donc pas appelée. 
 Sa caractéristique tient en deux points :
 - il implémente une méthode
 broadcastNews(Day day, Map<String, Agent> traders)
 qui lui permet de diffuser l'information qu'il souhaite aux autres agents par 
 appel de la méthode news() chez ceux-ci.
 Libre à lui de décider quand il diffuse (en testant Day) et à qui il diffuse 
 (en testant les agents), puisqu'il a accès à ces informations.
 - il est enregistré dans la simulation par la methode
 addNewInfoAgent(). C'est grâce à cela qu'il est appelé une fois à chaque 
 tick et que sa méthode "decide" n'est pas appelée.
 (pour mémoire les autres sont enregistrés avec addNewAgent())

 Un agent "informé" doit implémenter la methode news(Day day, Object news) 
 pour recevoir l'information. C'est par celle-ci qu'il récupère l'information 
 et qu'il la range dans ses attributs. Il pourra alors l'utiliser quand ce sera 
 son tour lors de l'appel à sa méthode "decide". 
 */

public class InformantAgentExample
{

    public static void main(String[] args)
    {
        Simulation sim = new MonothreadedSimulation();
        FilteredLogger log = new FilteredLogger(System.out);
        log.none();
        log.infos = true;
        log.days = true;
        sim.setLogger(log);

        sim.addNewOrderBook("AAPL");
        sim.addNewInfoAgent(new ReuterAgent("Joe"));
        for (int i = 0; i < 5; i++)
        {
            sim.addNewAgent(new ZIT("zit" + i));
            sim.addNewAgent(new ReuterFollower("reuterBiased" + i));
        }

        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 100), 1);
    }
}

class ReuterAgent extends DumbAgent
{

    public ReuterAgent(String name)
    {
        super(name);
    }

    public void broadcastNews(Day day, Map<String, Agent> traders)
    {
        if (day.currentTick() % 10 == 0)
        {
            for (Agent a : traders.values())
            {
                a.news(day, "BUY");
            }
        }
    }
}

class ReuterFollower extends Agent
{

    private boolean buy = false;

    public ReuterFollower(String name)
    {
        super(name);
    }

    @Override
    public Object news(Day day, Object news)
    {
        buy = news.equals("BUY");
        return null;
    }

    @Override
    public Order decide(String obName, Day day)
    {
        Order o = null;
        if (buy)
        {
            o = new MarketOrder(obName, "", 'B', 100);
            market.log.info(name + " sends a BUY order at tick = " + day.currentTick() + ".");
            buy = false;
        }
        return o;
    }
}
