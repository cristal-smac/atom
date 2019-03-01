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

public abstract class Order
{

    public String obName;
    public long id;       // unique, fait par le marche
    public String extId;  // exterieur, pour references et traces
    public long timestamp;// pour info car non unique, sinon remplacerait id
    public Agent sender;
    public char type;

    public Order(String obName, String extId)
    {
        this.obName = obName;
        this.extId = extId;
        this.sender = null;
        this.id = -1; // not set at this time
        this.type = 'X'; // unknown !
    }

    public String toString()
    {
        return ("Order;"+obName + ";" + (sender != null ? sender.name : "UNKNOWN") + ";" + extId + ";" + type);
    }

    public abstract void execute(OrderBook ob);
}
