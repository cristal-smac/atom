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

import java.util.LinkedList;

public class LimitedLinkedList<E> extends LinkedList<E>
{
    int maxSize;
    public LimitedLinkedList(int maxSize) {super(); this.maxSize=maxSize;}


    // Avérifier, implémenter le add et addAll pour utiliser le addFirst
    public boolean add(E e) {
        addFirst(e);
        return true;
    }
    
    public void addFirst(E e)
    {
	if(this.size()==maxSize)
            removeLast();
	super.addFirst(e);
    }

}
