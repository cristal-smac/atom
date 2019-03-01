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

import java.io.PrintStream;

public class FilteredLogger extends Logger {

    public boolean orders = true;
    public boolean prices = true;
    public boolean agents = true;
    public boolean infos = true;
    public boolean ticks = true;
    public boolean days = true;
    public boolean commands = true;
    public boolean exec = true;
    public boolean auctions = true; // utilise orderBookVisibleSpread


    public FilteredLogger() {
        super();
    }
    
    public FilteredLogger(PrintStream pw) {
        super(pw);
    }

    public FilteredLogger(String filename) {
        super(filename);
    }

    public void none() {
        orders = false; prices = false; agents = false;
        infos = false; ticks = false; days = false;
        commands = false; exec = false; auctions=false;
    }
    
    @Override
    public void print(String s) {
        if ((days && StringOrderParser.isDay(s))         ||
            (ticks && StringOrderParser.isTick(s))       ||
            (commands && StringOrderParser.isCommand(s)) ||
	    (orders && StringOrderParser.isOrder(s))     ||
	    (prices && StringOrderParser.isPrice(s))     ||
	    (agents && StringOrderParser.isAgent(s))     ||
	    (infos && StringOrderParser.isInfo(s))       || 
            (auctions && StringOrderParser.isAuctions(s)) ||
            (exec && StringOrderParser.isExec(s)))
	    super.print(s);
        /*        
        else
	    System.err.println("["+getClass().getSimpleName()+"] "+s);
        */
    }
    
    @Override
    public void println(String s) {
        print(s+"\n");
    }

}

