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

/**
 * 
 * {@code
 * cat bid | java -cp dist/fr.cristal.smac.atom.jar v14.Replay > RESULT_IN
 * java -cp dist/fr.cristal.smac.atom.jar v14.Replay bid > RESULT_FILE
 * diff RESULT_IN RESULT_FILE 
 * {@code
 * }
 * while :; do
 *   echo "Order;lvmh;bob;1;L;B;45500;13"; echo "Order;lvmh;bob;1;L;A;45500;13";
 *    done | java -cp dist/fr.cristal.smac.atom.jar v14.Replay | grep Price > toto
 * }
 * @author mathieu
 */
public class Replay
{
    
    private String sourceFilename;
    public Simulation sim;

    // En général, on passe le Printstream pour la destination
    public Replay(String sourceFilename, PrintStream outputStream)
    {
        sim = new MonothreadedSimulation();
        
        sim.setLogger(new Logger(outputStream));
        this.sourceFilename = sourceFilename;
    }

    // Par défaut, on lit le fichier et balance sur la sortie standard
    public Replay(String sourceFilename)
    {
        this(sourceFilename, System.out);
        
    }
    
    public void handleOneLine(String line)
    {
        if (StringOrderParser.isCommentOrEmpty(line))
            return;
        // Si on rencontre une info ou un agent, on copie simplement
        if (StringOrderParser.isDay(line))
        {
            sim.log.println(line);
            sim.market.clear();
        }
        else if (StringOrderParser.isInfo(line) || StringOrderParser.isTick(line))
            sim.log.println(line); // on ne recopie pas l'execution puisqu'on va re-générer cette ligne
        else if (StringOrderParser.isCommand(line))
            StringOrderParser.parseAndexecuteCommand(line, sim);
        else if (StringOrderParser.isOrder(line))
        {
            Order o = StringOrderParser.parseOrder(line, sim);
            sim.market.send(o.sender, o);
        }
        else if (StringOrderParser.isAuctions(line) && line.contains(";BID;"))
        {
            sim.log.dumpOrderBook(line.split(";")[1]);
            // on ne parse pas la ligne ASK ... elle est sautée automatiquement
        }
        else
        {
            // Skipping Price and Agent (because they are generated)
            //System.err.println("Replay: skipped "+line);
        }
    }
    
    public void go() throws FileNotFoundException, IOException
    {
        String line;
        BufferedReader file;
        file = new BufferedReader(new FileReader(sourceFilename));
        while ((line = file.readLine()) != null)
            handleOneLine(line);
        file.close();
    }
    
    public static void main(String args[]) throws Exception
    {
        // On lit les ordres sur l'entrée standard
        if (args.length == 0)
        {
            Simulation sim = new MonothreadedSimulation(new Logger(System.out));
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (in.ready())
            {
                String line = in.readLine();
                if (StringOrderParser.isOrder(line))
                {
                    Order o = StringOrderParser.parseOrder(line, sim);
                    // Détecter si l'OB existe ou pas et sinon ça plante
                    sim.market.send(o.sender, o);
                }
            }
        }
        else if (args.length == 1) // Sinon soit IHM, soit lecture dans un fichier
        	/*
            if (args[0].equals("-ihm"))
            {
                JFrame f = new JFrame("ATOM simulation replayer");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ReplayUI ui = new ReplayUI(f);
                f.getContentPane().add(ui);
                f.pack();
                f.setVisible(true);
            }
            else
            */
            { // On lit dans un fichier
                Replay rep = new Replay(args[0], System.out);
                rep.go();
            }
        else
        {
            System.err.println("Syntax: cat orders.txt | java fr.cristal.smac.atom.Replay | grep Prices > prices.txt");
            System.err.println("Syntax: java fr.cristal.smac.atom.Replay <filenameToReplay>");
            System.err.println("Syntax: java fr.cristal.smac.atom.Replay -ihm");
            System.exit(1);
        }
    }
}
