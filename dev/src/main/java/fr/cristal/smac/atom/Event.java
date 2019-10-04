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
 * This class represent events that can be generated when the state 
 * of an order change. These events are mainly produced by orders (cancel
 * and update) or by the orderbook. These events are one of the parameters
 * of the 'Agent.touchOrExecutedOrder'.
 * 
 */
public enum Event {
    CANCELLED,
    UPDATED,
    EXECUTED
}
