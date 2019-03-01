/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cristal.smac.atom.test.extraday;

import java.io.File;
import java.io.IOException;
import java.util.List;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.Day;
import fr.cristal.smac.atom.DayLog;
import fr.cristal.smac.atom.Logger;
import fr.cristal.smac.atom.Logger;
import fr.cristal.smac.atom.MarketPlace;
import fr.cristal.smac.atom.MarketPlace;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.MonothreadedSimulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.Simulation;
import fr.cristal.smac.atom.agents.ZIT;

/**
 *
 * Cet exemple montre comment calibrer les prix d'ouverture d'une simulation 
 * à partir d'un fichier de log d'une autre simulation. ATTENTION: seuls les 
 * prix d'ouverture sont calibrés, les LOW/HIGH/CLOSE sont dépendants de 
 * l'exécution de la simulation.
 * 
 * Les agents utilisés étant des ZIT, ce qui est comparable (et testé ci-dessous) 
 * ce sont les prix d'ouverture des lignes "Day".
 * 
 * @author mathieu
 */
public class TestExtradayInit
{

    public static void main(String args[]) throws IOException {
        Simulation sim = new MonothreadedSimulation();

        // TODO: Pourquoi pas un constructeur new Logger(File f) ?
        Logger logger = new Logger("extradayInitTest.atom");
        sim.setLogger(logger);
        
        for (int i=0; i<10; i++) {
            sim.addNewAgent(new ZIT("zit"+i, 0, 1000, 5000, 1, 10));
        }
        sim.addNewOrderBook("APL");
        // On génére un fichier pour pouvoir ensuite récupérer les logs 
        // de Day pour calibrer la seconde simulation
        sim.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 1000), 3);
        
        // obtenir les prix d'ouverture, low et high pour un orderbook
        // le pb c'est que c'est surtout l'agent qui doit l'avoir
        List<DayLog> extradayHistorySimulation1 = sim.market.orderBooks.get("APL").extradayLog;
        
        Simulation sim2 = new MonothreadedSimulation();
        Logger logger2 = new Logger("extradayInitTest2.atom");
        sim2.setLogger(logger2);
        
        for (int i=0; i<10; i++) {
            sim2.addNewAgent(new ZIT("zit"+i, 0, 1000, 5000, 1, 10));
        }
        // On réinjecte les prix d'ouverture, low et high
        sim2.initDayLog(new File("extradayInitTest.atom"));
        // On ne définit plus le nombre de jours car c'est automatiquement 
        // récupéré lors du parsing initDayLog
        sim2.run(Day.createSinglePeriod(MarketPlace.CONTINUOUS, 1000));
        
        List<DayLog> extradayHistorySimulation2 = sim2.market.orderBooks.get("APL").extradayLog;

        // On vérifie que les prix d'ouverture sont les mêmes dans la première
        // et seconde simulation. Par contre, on ne peut pas vérifier les low
        // et high car les ZIT ne sont pas calibrés !
        
        // On vérifie qu'il y a le même nombre de jours dans les deux 
        // simulations
        if (extradayHistorySimulation1.size() != extradayHistorySimulation2.size()) {
            throw new RuntimeException("Il y a pas le même nombre de jours dans les deux simulations !");
        }
        
        int idx = 0; // parcours de la seconde liste de DayLog
        for (DayLog daySimulation1 : extradayHistorySimulation1) {
            DayLog daySimulation2 = extradayHistorySimulation2.get(idx++);
            if (daySimulation1.OPEN != daySimulation2.OPEN) {
                throw new RuntimeException("Les prix d'ouverture des day logs ne sont pas égaux !");
            }
        }
        
        idx = 0;
        for (DayLog daySimulation1 : extradayHistorySimulation1) {
            System.out.println("Simulation 1 / Jour "+(idx+1)+"> "+daySimulation1);
            System.out.println("Simulation 2 / Jour "+(idx+1)+"> "+extradayHistorySimulation2.get(idx++));
        }
        
        
        // Ne fonctionne pas car le log de Day se base sur le premier prix FIXE
        // et pas le extradayLog
        
        // DONC faire le test directement sur les extradayLog des OB de sim et
        // sim2
        
    }
}
