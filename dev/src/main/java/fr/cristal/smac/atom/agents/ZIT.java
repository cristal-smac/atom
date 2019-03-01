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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.LimitOrder;;


/*
 * ZIT ameliore. Cet agent code de maniere generique plusieurs comportements de
 * ZIT : il permet de fixer la fourchette de prix ou de quantités, de changer la
 * probabilité de parler, de changer l'equilibre entre les ASK et les BID et
 * donne la possibilité à l'agent de fractionner son envoi en petites quantites.
 *
 * le principe est le suivant : l'agent a une certaine probabilité de choisir un
 * ordre (1-somme(proba)). L'ordre est choisi aléatoirement en fonction des
 * paramètres fournis sur la direction le prix et la quantité. Quand il a choisi
 * un ordre, il en envoie une fraction à chaque tour.
 *
 * ZIT("paul") cree un agent classique avec cash à 0, qui parle toujours, sans
 * fractionner et avec une équité entre ASK et BID
 *
 * ZIT("paul",1000) crée un agent classique avec 1000 de cash
 *
 * ZIT("paul",1000,14000,15000,10,100) cree un agent classique qui tire ses prix
 * entre 14000 et 15000 et ses quantités entre 10 et 100.
 *
 * ZIT("paul",1000,14000,15000,10,100,new double[]{0.2,0.3}) cree un agent qui
 * ne parle pas dans 50% des cas, qui quand il parle décide d'envoyer plus de
 * BID que de ASK
 *
 * ZIT("paul",1000,14000,15000,10,100,new double[]{0.2,0.3}, 0.1) cree un
 * agent qui ne parle pas dans 50% des cas, qui quand il parle décide d'envoyer
 * plus de BID que de ASK, et qui, une fois choisi, envoie l'ordre à chaque tour
 * en morceaux de 10%
 *
 *
 */
public class ZIT extends Agent {

    protected long minPrice = -1, maxPrice = -1;
    protected int minQuty, maxQuty;
    protected double proba[];
    protected double frac;
    private int quty;
    private int alreadySentQuty;
    private int part;
    private char dir;
    private long price;
    private boolean running;

    public boolean autocalibrate = false;
    public int calibrationRate = 1;
    protected Map<String, OrderBookStats> pricesSpread = new HashMap<String, OrderBookStats>();

    class OrderBookStats {

        long minPrice; // lowest BID price
        long maxPrice; // highest ASK price
        int minQuty;   // lowest qty within ASK/BID
        int maxQuty;   // highest qty within ASK/BID

        @Override
        public String toString() {
            return minPrice + "," + maxPrice + " (price) | " + minQuty + "," + maxQuty + " (qty)";
        }
    }

    /*
     * @param name le nom de l'agent @param cash le cash initial de l'agent
     * @param minPrice la borne min de prix @param maxPrice la borne max de prix
     * @param minQuty la borne min de quty @param maxQuty la borne max de quty
     * @param proba un tableau de doubles qui indique dans l'ordre la
     * probabilité d'envoyer un ask et la probabilité d'envoyer un bid. {50.0 ,
     * 50.0} correspond à un agent qui parle tout le temps et envoie
     * équitablement un ask et un bid. {30.0 , 30.0} correspond à un agent qui
     * ne parle pas dans 40% des cas, et envoie équitablement un ask et un bid
     * dans les autres cas. @param frac le % de quantité qui sera envoye a
     * chaque tour. 1.0 correspond à un agent qui ne fractionne rien. 0.1
     * correspond à un agent qui enverra son ordre en 10 fois.
     */
    public ZIT(String name, long cash, long minPrice, long maxPrice,
            int minQuty, int maxQuty, double proba[], double frac) {
        super(name, cash);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minQuty = minQuty;
        this.maxQuty = maxQuty;
        this.proba = proba;
        this.frac = frac;
        running = false;
        if (proba[0] + proba[1] > 1.0 || frac > 1.0) {
            throw new RuntimeException("Pb in proba of ZIT Agent " + name);
        }
    }

    /*
     * idem mais sans fractionnement de l'ordre choisi
     */
    public ZIT(String name, long cash, long minPrice, long maxPrice,
            int minQuty, int maxQuty, double proba[]) {
        this(name, cash, minPrice, maxPrice, minQuty, maxQuty, proba, 1.0);
    }

    /*
     * cree un ZIT qui envoie 50% de ask et 50% de bid sans fractionnement
     */
    public ZIT(String name, long cash, long minPrice, long maxPrice,
            int minQuty, int maxQuty) {
        this(name, cash, minPrice, maxPrice, minQuty, maxQuty,
                new double[]{0.5, 0.5});

    }

    /*
     * cree un ZIT qui envoie 50% de ask et 50% de bid avec des
     * 1400>=prix>=15000 et des 10>=quty>=50 sans fractionnement
     */
    public ZIT(String name, long cash) {
        this(name, cash, 14000, 15000, 10, 100);
    }

    public ZIT(String name) {
        this(name, 0);
    }

    public void setAutoCalibrate(int rate) {
        this.autocalibrate = true;
        this.calibrationRate = rate;
    }

    public void calibrate() {
        for (OrderBook ob : market.orderBooks.values()) {
            try {
                LimitOrder highestAskPrice = ob.ask.last();
                LimitOrder lowestBidPrice = ob.bid.last();
                OrderBookStats obStat = new OrderBookStats();
                obStat.maxPrice = highestAskPrice.price;
                obStat.minPrice = lowestBidPrice.price;
                obStat.maxQuty = ob.maxQty;
                obStat.minQuty = ob.minQty;
                pricesSpread.put(ob.obName, obStat);
                System.out.println("Computed bounds: " + obStat);
            } catch (NoSuchElementException nsee) {
                //nsee.printStackTrace();
                throw new RuntimeException("Cannot auto-calibrate because orderbook " + ob.obName + " ASK or BID is empty: "
                        + "ASK=" + ob.ask.size() + " BID=" + ob.bid.size());
            }
        }
    }

    public Order decide(String obName, Day day) {
        // si le ZIT s'auto-calibre et que l'on est à un point de re-calibration
        if (autocalibrate
                && (day.currentTick() == 1
                || day.currentTick() % calibrationRate == 0)) {
            calibrate();
        }
        // si il n'a rien à faire
        if (!running) {
            // est-ce qu'il doit parler ?
            double n = Math.random();
            if (n > (proba[0] + proba[1])) {
                return null;
            }

            // Est-ce que les quantités+prix ont déjà été initialisés ?
            if (autocalibrate) {
                OrderBookStats stats = pricesSpread.get(obName);
                maxPrice = stats.maxPrice;
                minPrice = stats.minPrice;
                maxQuty = stats.maxQuty;
                minQuty = stats.minQuty;
            }

            // si oui, il choisit un ordre
            dir = (n > proba[0] ? LimitOrder.BID : LimitOrder.ASK);
            quty = minQuty + (int) (Math.random() * (maxQuty - minQuty));
            part = (int) (frac * quty);
            alreadySentQuty = 0;
            running = true;
            price = minPrice + (int) (Math.random() * (maxPrice - minPrice));
        }


        /*
         * tant qu'il n'a pas terminé d'envoyer son ordre il parle à tous les
         * tours en envoyant des parties
         */
        if (alreadySentQuty + part >= quty) {
            running = false;
            return new LimitOrder(obName, "" + myId, dir, (quty - alreadySentQuty), price);
        } else {
            alreadySentQuty += part;
            return new LimitOrder(obName, "" + myId, dir, part, price);

        }
    }

}
