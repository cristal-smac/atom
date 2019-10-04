/********************************************************** 
ATOM : ArTificial Open Market

Author  : P Mathieu, O Brandouy, Y Secq, Univ Lille, France
Email   : philippe.mathieu@lifl.fr
Address : Philippe MATHIEU, CRISTAL, UMR CNRS 9189, 
          Lille  University
          59655 Villeneuve d'Ascq Cedex, france
Date    : 14/12/2008

***********************************************************/


/* Cet agent sert quand on envoie directement des ordres au marché. Dans ce 
 * cas on a besoin d'un agent sans aucune stratégie
 * juste pour faire sim.market.send(a,order)
 * 
 * Un DumbAgent NE DOIT JAMAIS ETRE AJOUTE a une Simulation car il n'a pas 
 * de comportement (ie. de méthode 'decide').
 */
package fr.cristal.smac.atom.agents;

import fr.cristal.smac.atom.Order;
import fr.cristal.smac.atom.Agent;
import fr.cristal.smac.atom.Day;

public class DumbAgent extends Agent
{

    public DumbAgent(String name, long cash)
    {
        super(name, cash);
    }

    public DumbAgent(String name)
    {
        this(name, 0);
    }

    public Order decide(String obName, Day day)
    {
        throw new RuntimeException("DumbAgent can't decide !");
    }
}
