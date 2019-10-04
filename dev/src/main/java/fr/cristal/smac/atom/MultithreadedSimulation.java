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

/**
 * This simulation is multithreaded. It means that one simulation time step is 
 * organized around a fixed delay (default is 1 second) and each agent is 
 * queried by its own thread to provide its order.
 * 
 * As in monothreaded simulation, equity in information is not guaranteed: the 
 * that talk close to the end of the current timestep takes its decision based 
 * on all consequences from previous agents actions.
 * 
 * The notion of timestamp here is "virtual" and there is no more equity in 
 * talk.
 */

public class MultithreadedSimulation extends Simulation {
	/*
    private List<MultithreadedSimulation.ThreadedAgent> agents = 
            new ArrayList<MultithreadedSimulation.ThreadedAgent>();
	 */  
    private static final Object monitor = new Object();

    public MultithreadedSimulation() {
        super();
        tempo=1000;
    }
    
    public MultithreadedSimulation(Logger log) {
        super(log);
        tempo=1000;
    }
    
    public void addNewAgent(Agent a) {
        super.addNewAgent(a);
        // Agent will be suspended until Simulation.alive == true
        (new ThreadedAgent(a)).start();
    }
    
    protected void queryAllAgents() {
        // Iterating on orderbooks to decrease validity of orders of one round
        synchronized (monitor) {
            for (OrderBook ob : market.orderBooks.values()) {
                //ob.decreaseAndDeleteUnvalid(); // Comment to SpeeUp if validity = -1
            }	// "Tour de parole"

        }
        try {
            // Waiting tempo milliseconds before going to next tick
            Thread.sleep(tempo);
        } catch (InterruptedException ex) {
            log.error(ex);
        }
    }

    class ThreadedAgent extends Thread {

        private Agent a;
        private int nbOrdersSent;

        public ThreadedAgent(Agent a) {
            this.a = a;
            nbOrdersSent = 0;
        }

        public void run() {
            while (!alive) {
                waitAbit(50);
            }
            while (alive) {
                // if (a.speed == 0 || (numberOfRounds % a.speed == 0)) {
                    for (OrderBook ob : market.orderBooks.values()) {
                        a.beforeDecide(ob.obName, day); // changement v12
                        Order lo = a.decide(ob.obName, day);
                        a.afterDecide(ob.obName, day, lo); // changement v12

                        if (lo != null) {
                            market.send(a, lo);
                            System.err.println(lo);
                            nbOrdersSent++;
                        }
                    }
                // }
                waitAbit((long) (a.speed * tempo));
            }
            System.err.println(a.name + " has sent " + nbOrdersSent + " orders.");
        }

        private void waitAbit(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                log.error(ex);
            }
        }
    }
}
