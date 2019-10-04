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

public class DayLog
{
    public final String obName;
    public final long OPEN;
    public long CLOSE = -1;
    public long HIGH = -1;
    public long LOW = -1;
    // a ajouter
    public long VOLUME;
   
    
    public DayLog(String obName, long open, long low, long high, long close) {
        this.obName = obName;
        this.OPEN = open;
        this.CLOSE = close;
        this.HIGH = high;
        this.LOW = low;
    }
    
    @Override
    public String toString() {
        return obName+";"+OPEN+";"+LOW+";"+HIGH+";"+CLOSE;
    }
    
}
