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

public class PriceRecord
{
    public String obName;
    public long price;
    public int quantity;
    public char dir;
    public String extId1;
    public String extId2;
    public long timestamp; 

    public PriceRecord(String obName, long price, int quantity, char dir, String extId1, String extId2)
    {
	this.obName=obName;
	this.price = price;
	this.quantity = quantity;
	this.dir = dir;
	this.extId1 = extId1;
	this.extId2 = extId2;
    }

    public String toString()
    {	return obName+";"+price+";"+quantity+";"+dir+";"+extId1+";"+extId2;
    }
}

