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

import java.util.*;

/**
 * This simulation is monothreaded. It means that one simulation time step is
 * organized around a talk turn for all agents. Agents list is shuffled and
 * each agent is asked to provide an order that is sent directly to the market.
 *
 * Thus, equity in information is not guaranteed: the last agent to talk can
 * take a decision based on all consequences from previous agents action (even
 * if we are still within the same time step).
 *
 */
public class MonothreadedSimulation extends Simulation {

    public MonothreadedSimulation(Logger log, boolean shuffleAgentList) {
        super(log);
        this.shuffleAgentList=shuffleAgentList;
    }
    
    public MonothreadedSimulation() {
        super();
    }
    
    public MonothreadedSimulation(boolean shuffleAgentList) {
        super(); // car dans ce cas on récupère le Logger du marché
        this.shuffleAgentList=shuffleAgentList;
    }

    public MonothreadedSimulation(Logger log) {
        this(log, true);
    }

    protected void queryAllAgents() {
        //System.out.println("MonoThreadedSimulation/queryAllAgents(Day): " + day);
        // Iterating on orderbooks to decrease validity of orders of one round
        for (OrderBook ob : market.orderBooks.values()) {
            // ob.decreaseAndDeleteUnvalid(); // Comment to SpeedUp if validity = -1
        }
        // "Tour de parole"
        List<Agent> al = new ArrayList<Agent>(agentList.values());
        if (shuffleAgentList) {
            Collections.shuffle(al);
        }
        for (Agent agent : al) {
            int currentTick = day.currentPeriod().currentTick();
            if (agent.speed == 0 || (currentTick % agent.speed == 0)) {
                for (OrderBook ob : market.orderBooks.values()) {
                    agent.beforeDecide(ob.obName, day); // changement v10
                    Order lo = agent.decide(ob.obName, day);
                    agent.afterDecide(ob.obName, day, lo); // changement v10

                    if (lo != null) {
                        market.send(agent, lo);
                    }
                }
            }
        }
        try {
            // Waiting tempo milliseconds before going to next tick
            Thread.sleep(tempo);
        } catch (InterruptedException ex) {
            log.error(ex);
        }
    }
}
