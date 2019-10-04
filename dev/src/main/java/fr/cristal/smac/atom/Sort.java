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

import fr.cristal.smac.atom.orders.*;
import java.util.*;

class Sort {

    public static final Comparator<LimitOrder> ASK = new Comparator<LimitOrder>() {
        public int compare(LimitOrder o1, LimitOrder o2) {
            if (o1.price == o2.price) {
                return (int) (o1.id - o2.id);
            }
//            return (int) (o1.price - o2.price);
            if (o1.price > o2.price) return +1; else return -1;
            
        }
    };
    public static final Comparator<LimitOrder> BID = new Comparator<LimitOrder>() {

        public int compare(LimitOrder o1, LimitOrder o2) {
            if (o1.price == o2.price) {
                return (int) (o1.id - o2.id);
            }
//            return (int) (o2.price - o1.price); bug avec les Market !
            if (o2.price > o1.price) return +1; else return -1;
        }
    };
}


