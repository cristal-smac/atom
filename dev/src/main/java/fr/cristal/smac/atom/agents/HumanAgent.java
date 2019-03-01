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
import fr.cristal.smac.atom.orders.LimitOrder;;

public class HumanAgent extends Agent
{
    List<Order> attente;
    
    public HumanAgent(String name, long cash)
    {
        super(name,cash);
        attente = new ArrayList<Order>();
    }

    public boolean shouldSpeak()
    {
        return true;
    }
    
    public void addNewOrder(LimitOrder o)
    {
        attente.add(o);
    }

    public Order decide(String obName, Day day)
    {
        int indFirst=-1;
        
        for (int i=0;i<attente.size() && indFirst==-1 ;i++)
                if (attente.get(i).obName.equals(obName))
                    indFirst=i;
        
        if (indFirst==-1) return null;
        
        Order o = attente.get(indFirst);
        attente.remove(indFirst);
        return o;       
    }
    
    
    
}
