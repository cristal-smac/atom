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

import java.util.*;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.*;

/**
 * Passe un seul ordre ZIT à la fois Gère ses frozen !
 *
 * @author mathieu
 */
public class ModerateAgent extends ZIT {

    protected long frozenCash;
    protected Map<String, Integer> frozenInvest;
    protected ArrayList<Order> pendings;

    public ModerateAgent(String name, long cash, long minPrice, long maxPrice, int minQuty, int maxQuty) {
        super(name, cash, minPrice, maxPrice, minQuty, maxQuty);
        pendings = new ArrayList<Order>();
        frozenCash = 0;
        frozenInvest = new HashMap<String, Integer>();
        // A selled or a bidder but not both
    }

    public ModerateAgent(String name, long cash) {
        this(name, cash, 14000, 15000, 10, 100);
    }

    public ModerateAgent(String name) {
        this(name, 0);
    }

    public int getFrozenInvest(String name) {
        Integer result = frozenInvest.get(name);
        if (result == null) {
            frozenInvest.put(name, 0);
            return 0;
        } // autoboxing java 1.5
        else {
            return result;
        }
    }

    void setFrozenInvest(String name, int value) {
        frozenInvest.put(name, value); // autoboxing java 1.5
    }

    @Override
    public Order decide(String obName, Day day) {
        // int availableInvest = getInvest(obName) - getFrozenInvest(obName);
        // long availableCash = cash - frozenCash;

        if (pendings.size() > 0) {
            return null; // one order each time
        }

        Order o = super.decide(obName, day);
        return o;

    }

    @Override
    public void afterDecide(String obName, Day day, Order o) {
        // mise Ã  jour des pendings
        if (!(o instanceof LimitOrder)) {
            return;
        }
        LimitOrder o2 = (LimitOrder) o;

        if (o2.direction == LimitOrder.BID) {
            frozenCash += o2.price * o2.quantity;
        } else {
            setFrozenInvest(obName, getFrozenInvest(obName) + o2.quantity);
        }

        pendings.add(o);

    }

    public void touchedOrExecutedOrder(Event e, Order o2, PriceRecord pr) {
        // Traitement d'un Cancel
        if (Event.CANCELLED.equals(e)) {
            LimitOrder lo = null;
            boolean trouve = false;
            for (Iterator it = pendings.iterator(); it.hasNext() && !trouve;) {
                lo = (LimitOrder) it.next();
                if (lo.extId.equals(((CancelOrder) o2).extIdToKill)) {
                    trouve = true;
                }
            }

            pendings.remove(lo);
            if (lo.direction == LimitOrder.BID) {
                frozenCash -= lo.price * lo.quantity;
            } else {
                setFrozenInvest(lo.obName, getFrozenInvest(lo.obName) - lo.quantity);
            }
        } else { // Traitement d'un Limit
            LimitOrder o = (LimitOrder) o2;
            if (o.quantity == 0 || o.validity == 0) // A VERIFIER AVEC ICEBERG !
            {
                pendings.remove(o);

                if (o.direction == LimitOrder.BID) {
                    frozenCash -= o.price * o.quantity;
                } else {
                    setFrozenInvest(o.obName, getFrozenInvest(o.obName) - o.quantity);
                }
            }
        }
    }
}
