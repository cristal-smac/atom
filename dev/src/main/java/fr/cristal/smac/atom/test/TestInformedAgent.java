/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cristal.smac.atom.test;

import java.util.*;

import fr.cristal.smac.atom.Agent;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.Logger;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Order;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

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

public class TestInformedAgent
{

    class CNN extends Agent
    {

        public CNN(String name)
        {
            super(name);
        }

        // Cette méthode ne devrait jamais être appelée ... A moins de vouloir
        // avoir des agents traders ayant la possibilité d'envoyer des news 
        // aux autres agents ...

        @Override
        public Order decide(String obName, Day day)
        {
            throw new UnsupportedOperationException("Should be never called, I'm CNN !");
        }

        // Méthode à surcharger pour les agents non trader pour pouvoir spammer
        // les agents traders
        @Override
        public void broadcastNews(Day day, Map<String, Agent> tradingAgents)
        {
            // to be defined to send news to trading agents
            System.out.println("CNN has the opportunity to spam (day=" + day.dayNumber + ") !");
            for (Agent a : tradingAgents.values())
            {
                a.news(day, "Buy Coke with " + (day.dayNumber * 10) + "% discount !");
            }
        }
    }

    class MyInformedZIT extends ZIT
    {

        public MyInformedZIT(String name)
        {
            super(name);
        }

        // Méthode à surcharger pour recevoir des informations de la part 
        // d'agents non trader

        @Override
        public Object news(Day day, Object news)
        {
            System.out.println("InformedZIT has received this news: " + news);
            return null;
        }
    }

    public void test()
    {
        Simulation sim = new MonothreadedSimulation();
        sim.setLogger(new Logger(System.out));

        sim.addNewOrderBook("APL");
        // deux agents traders: un ZIT classique et un qui suit les news
        sim.addNewAgent(new ZIT("foobar"));
        sim.addNewAgent(new MyInformedZIT("cnngeek"));
        // un agent non trader qui peut spammer les agents trader
        sim.addNewInfoAgent(new CNN("CNN"));
        // C'est parti pour deux jours de simulation d'EuroNEXT !
        sim.run(Day.createEuroNEXT(2, 5, 3), 2);
    }

    public static void main(String[] args)
    {
        new TestInformedAgent().test();
    }
}
