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

import fr.cristal.smac.atom.agents.ZIT;

public class Generate {

    public static void main(String args[]) throws Exception {
        // On démarre en mode graphique si on n'a pas de paramètre
    	/*
        if (args.length == 0) {
            JFrame f = new JFrame("AtomLight");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ConfigurationUI ui = new ConfigurationUI(f);
            f.getContentPane().add(ui);
            f.pack();
            f.setVisible(true);
            ui.loadProperties(ATOMProperties.DEFAULT_PROPERTY_FILENAME);
        } else { // Sinon en mode texte
        */
    	// <nbAgents> <nbOrderbooks> <nbTurns>
            if (args.length != 4) {
                System.err.println("Syntax: <nbAgents> <nbOrderbooks> <nbTurns> <nbDays>");
                System.exit(1);
            }
            int numberOfAgents = Integer.parseInt(args[0]);
            int numberOfOrderbooks = Integer.parseInt(args[1]);
            int numberOfTurns = Integer.parseInt(args[2]);
            int numberOfDays = Integer.parseInt(args[3]);
            
            Simulation sim = new MonothreadedSimulation();
            sim.setLogger(new Logger(System.out));
            // Creation des agents
            for (int nbAgents = 0; nbAgents < numberOfAgents; nbAgents++) {
                sim.addNewAgent(new ZIT("ZIT"+(nbAgents+1)));
            }
            // Creation des orderbooks
            for (int nbOrderbooks = 0; nbOrderbooks < numberOfOrderbooks; nbOrderbooks++) {
                sim.addNewOrderBook("LVMH"+(nbOrderbooks+1));
            }
            sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, numberOfTurns), numberOfDays);
            // sim.runExtraday(1, 0, numberOfTurns, 0);
        }
    }

