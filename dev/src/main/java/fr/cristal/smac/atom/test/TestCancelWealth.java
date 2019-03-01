package fr.cristal.smac.atom.test;

import fr.cristal.smac.atom.*;
import fr.cristal.smac.atom.orders.*;

import fr.cristal.smac.atom.agents.ModerateAgent;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class TestCancelWealth
{
    
    class MyAgent extends ModerateAgent
    {
        MyAgent(String name,long cash){super(name,cash);}
        @Override
        public Order decide(String obName, Day day)
        {
            int tick = day.currentPeriod().currentTick();
            aff(tick);    
            System.out.println(pendings);
            if (tick==1)
                return new LimitOrder(obName,"1",'B',1,100);
            if (tick==2)
                return new CancelOrder(obName,"2","1");
            return null;
        }
        
        public void aff(int tick) {
            System.out.println("tick "+tick+"\t wealth = "+getWealth()+
                    "\tfrozen (cash,invest) = "+frozenCash+","+
                    frozenInvest.get("lvmh")+"\tpendings = "+pendings.size());}
        
    }

    public void test()
    { 
        Simulation s = new MonothreadedSimulation();
        s.addNewOrderBook("lvmh");
        MyAgent ma = new MyAgent("paul",1000);
        s.addNewAgent(ma);
        ma.aff(0);
        //s.runExtraday(1, 0, 3, 0);
        s.run(Day.createEuroNEXT(0, 3, 0), 1);
        ma.aff(5);
    }
    
    public static void main(String args[])
    {

        TestCancelWealth t = new TestCancelWealth();
        t.test();
   
    }
}
