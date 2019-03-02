import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.agents.*;
import fr.cristal.smac.atom.orders.*;


public class Tutorial
{

    public static void main(String args[])
    {
        /*
         * COMMENT CREER UNE EXPERIENCE AVEC ATOM ?
         *
         * Tout d'abord, creer une simulation (mono ou multithreadée) Définir la
         * manière de logguer les infos (rien, fichier, console) ensuite y
         * ajouter carnets d'ordres et agents (autant que l'on veut) et enfin
         * lancer l'expérience éventuellement sur plusieurs jours.
         */

        Simulation sim = new MonothreadedSimulation();
        /*
         * Il y a deux types de simulations : MonothreadedSimulation : tours de
         * paroles équitables entre les agents Multithreadedimulation : l'OS
         * garde le controle sur les agents. Dans une experience classique, sans
         * humain dans la boucle, Monothreaded est préférable.
         */

        //sim.market.logType=MarketPlace.LONG; // ou SHORT
        /*
         * Deux mécanismes de fixation du prix sont codés dans ATOM : LONG ou
         * SHORT. Cela correspond aux nombre de lignes fixées quand un ordre en
         * match plusieurs. Avec Long, à chaque ordre touché, une ligne de prix
         * est générée Avec short, seule la dernière est affichée. Euronext
         * fonctionne en SHORT, mais c'est beaucoup plus clair et facile à
         * débugguer en LONG. C'est donc la valeur par défaut.
         */

        sim.setLogger(new Logger(System.out));
        /*
         * Par défaut ATOM ne loggue rien. Si on lui précise un Logger, de
         * nombreuses informations sur les ordres, les agents, les prix et le
         * rythme de la simulation seront indiqués. En général, l'affichage à la
         * console est suffisant et permet le cas échéant, une redirection dans
         * un fichier. Sinon : sim.setLogger(new Logger("monfichier.txt"));
         *
         * Il est aussi possible d'utiliser un FilteredLogger qui permet de ne
         * logguer que certaines informations. FilteredLogger flog = new
         * FilteredLogger(System.out); flog.orders = true; flog.prices = false;
         * flog.agents = false; flog.infos = false; flog.commands = false;
         * sim.setLogger(flog);
         *
         * Sauf cas particulier, il est plutot conseillé de tout logguer avec le
         * loggueur classique, puis éventuellement de filtrer le fichier avec
         * des grep/awk/sed/cut pour extraire ce que l'on souhaite.
         */

        String obName = "lvmh";
        sim.addNewOrderBook(obName);
        // sim.addNewMicrostructure(new OrderBook(obName)); //idem précédente
        /*
         * ATOM est multi-orderbooks. Il peut gérer en simultané autant
         * d'OrderBooks que necessaire. On associe toujours un titre à un et un
         * seul orderbook. Il est aussi possible d'utiliser un OrderBook
         * spécifique, par ex utilisant un MarketMaker. Cela se fait alors avec
         * addNewMicrostructure(new MarketMaker(name))
         */


        sim.addNewAgent(new ZIT("paul")); // cash, bornes par défaut
        // sim.addNewAgent(new ZIT("paul",10000)); // bornes par défaut
        // sim.addNewAgent(new ZIT("paul",0,10000,20000,10,50, new double[]{0.25 , 0.25}, 1.0));
        sim.addNewAgent(new MonAgent("pierre", 0, 1000));
        /*
         * Il faut ensuite ajouter les agents. ATOM est fourni avec un nombre
         * important d'agents. Le plus simple d'entre eux est le Zero
         * Intelligent Trader qui envoie des LimitOrder en tirant direction,
         * prix et quantité de manière aléatoire entre des bornes éventuellement
         * passées en paramètres.
         */

        sim.run(Day.createEuroNEXT(0, 1000, 0), 1);
        // sim.run(Day.createSinglePeriod(MarketPlace.FIX, 1000));
        /*
         * On lance ensuite la simulation. D'ue manière classique, une
         * simulation dure un certain nombre de jours (si on est en extraday).
         * Un jour est constitué comme sur Euronext d'une période d'ouverture
         * d'une période continue et d'une periode de cloture. Chacune de ces
         * périodes s'exprime en tours de paroles. La ligne ci dessus effectue 1
         * seul jour, sans ouverture ni cloture mais avec 1000 tours de parole
         * en continu. ATOM permet de construire n'importe quelle structure de
         * jour, le premier paramètre de run concerne cette structure, par
         * ailleurs accessible aussi à l'agent lors de l'appel à la méthode
         * "decide" pour lui permettre de raisonner sur le temps. La méthode
         * createEuroNext crée directement une journée en 3 étapes
         * fix,continu,fix dont les durées sont respectivement fournies par les
         * 3 arguments. Le second paramètre est le nombre de jours.
         */


        sim.market.printState();
        /*
         * Une fois l'expérience terminée il est possible d'accéder à de
         * nombreuses informations sur le marché, les agents ou les orderbooks.
         * La méthode printState par exemple, indique pour tous les orderbooks
         * le nombre d'ordres reçus, le nombre de prix fixés et le nombre
         * d'ordres restants sans contrepartie.
         */

    }
}
/*
 * COMMENT CREER UN AGENT ?
 *
 * Dans sa version la plus basique, un agent hérite de la classe Agent et
 * surcharge la méthode decide(obName,day) qui est appelée automatiquement par
 * le marché à chaque fois que cet agent a la possibilité de s'exprimer sur cet
 * obName. La structure Day permet à l'agent d'avoir des informations sur le
 * temps quis'écoule durant l'expérience.
 *
 * L'agent ci dessous est un agent qui n'enverra qu'un seul ordre à un moment de
 * la journée passé en paramètre dans son constructeur. Cet agent, très trivial
 * n'a évidemment pour but que de montrer comment écrire son propre agent. Il
 * possède néanmoins un paramètre spécifique, raisonne sur le temps et ne parle
 * pas tout le temps.
 */
class MonAgent extends Agent
{

    private int declenche;

    public MonAgent(String name, long cash, int declenche)
    {
        super(name, cash);
        this.declenche = declenche;
    }

    public Order decide(String obName, Day day)
    {
        if (day.currentPeriod().isContinuous() && day.currentPeriod().currentTick() == declenche)
        {
            return new LimitOrder(obName, "" + myId, LimitOrder.ASK, 10, 10000);
        }
        else
            return null;
        /*
         * Bien sur, l'agent a le droit de ne rien faire !
         */
    }

    public void touchedOrExecutedOrder(Event e, Order o, PriceRecord p)
    {
        /*
         * L'agent est notifié quand l'un de ses ordres a été touché ou exécuté
         * A lui de voir ce qu'il fait de cette information.
         * Cette méthode n'est pas obligatoire.
         *
         * if (e == Event.EXECUTED && o.extId.equals(...)
         * {
         *
         * }
         *
         */
    }
}
/*
 * Il suffit pour utiliser cet agent d'ajouter dans l'expérience précédente la
 * ligne sim.addNewAgent(new MonAgent("pierre",0,100); L'agent se déclenchera au
 * 100è tour de parole. On vérifiera cela aisément dans la trace d'exécution
 * générée.
 */
