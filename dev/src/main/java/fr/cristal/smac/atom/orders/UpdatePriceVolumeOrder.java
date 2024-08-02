/********************************************************** 
ATOM : ArTificial Open Market

Author  : Rachid EL MAAZOUZ
Email   : rachid.el-maazouz.auditeur@lecnam.net
Address : 292 Rue Saint-Martin, 
          75003 Paris
Date    : 14/06/2024

***********************************************************/

package fr.cristal.smac.atom.orders;

import fr.cristal.smac.atom.*;

public class UpdatePriceVolumeOrder extends Order {
    private String extIdToUpdate;
    private int qutyToSet;
    private long priceToSet;

    public UpdatePriceVolumeOrder(String obName, String extId, String extIdToUpdate, int qutyToSet, long priceToSet) {
        super(obName, extId);
        this.extIdToUpdate = extIdToUpdate;
        this.qutyToSet = qutyToSet;
        this.priceToSet = priceToSet;
        type = 'V';
    }

    public void execute(OrderBook ob) {
        /* mÃªmes problÃ¨mes que le Cancel ... mÃªme Ã©criture */

        LimitOrder lo = ob.findOrder(sender, extIdToUpdate, true);

        if (lo == null)
            lo = ob.findOrder(sender, extIdToUpdate, false);
        if (lo != null) {
            // System.out.println("========> update order "+this);
            // System.out.println("========> update before "+lo);
            lo.quantity = qutyToSet;
            lo.price = priceToSet;
            // System.out.println("========> updtate after : "+lo);

        }

        sender.touchedOrExecutedOrder(Event.UPDATED, this, null);
    }

    public String toString() {
        return super.toString() + ";" + extIdToUpdate + ";" + qutyToSet + ";" + priceToSet;
    }
}
