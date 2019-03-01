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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import fr.cristal.smac.atom.agents.ZIT;

/**
 * Simulation represents a stock market simulation. An agent population has to
 * be defined, then the simulation can be executed by calling one of the run
 * methods that simulates a stock market running on one or several days.
 * <p>
 * Two implementations are provided: one monothreaded and the other
 * multithreaded.
 * </p>
 */
public abstract class Simulation
{

    public LinkedHashMap<String, Agent> agentList;
    // to handle exogenous agents that are not trading in the market
    // typically, agents that need to send news to other agents
    public Map<String, Agent> nonTradingAgentList;
    public MarketPlace market;
    public Day day;
    public int currentDay;
    public int tempo = 0;
    public boolean alive;
    public int totalDays;
    protected Logger log;
    protected boolean shuffleAgentList = true;
    private boolean initDayLogUsed = false;
    protected boolean keepLastDayOrderbook = false;

    public void setLogger(Logger log)
    {
        this.market.setLogger(log);
        this.log = log;
        this.log.setMarket(this.market);
    }

    public void setKeepLastDayOrderbook(boolean keep)
    {
        keepLastDayOrderbook = keep;
    }

    public void initDayLog(DayLog[] logs)
    {
        initDayLog(new ArrayList<DayLog>(Arrays.asList(logs)));
    }

    public Logger getLogger()
    {
        return this.log;
    }

    public Simulation()
    {
        agentList = new LinkedHashMap<String, Agent>();
        nonTradingAgentList = new LinkedHashMap<String, Agent>();
        this.market = new MarketPlace();
        this.log = market.log;
    }

    public Simulation(Logger log)
    {
        this();
        this.market.setLogger(log);
        this.log = log;
    }

    public void clear()
    {
        market.clear();
        for (Agent a : agentList.values())
            a.clear();
    }

    /**
     * L'ordre doit contenir une référence de l'agent émetteur.
     * Si on passe par le fichier, c'est le 'parseOrder' qui créé
     * un DumbAgent à partir de la chaîne de caractères.
     * Pour cette méthode, il faut que les ordres contiennent la
     * référence vers un DumbAgent (pour la mise à jour du portefeuille)
     * qui NE DOIT PAS ETRE AJOUTE à la Simulation.
     * 
     * @param a the reference to the agent who will execute the orders
     * @param orders la liste d'ordres pour initialiser les carnets d'ordre
     *
     */
    public void initOrderBooks(Agent a, List<Order> orders)
    {
        for (Order o : orders)
        {
            o.sender = a;
            market.send(o.sender, o);
        }
    }

    /* Utilisée uniquement pour mémoriser la meilleure offre et la meilleure demande.
     Classe écrite quand on lne souhaitait pas les ordres se croisent à l'initialisation
     */
    /*
     class Spread {
     String obName;
     long bask = Long.MAX_VALUE;
     long bbid = -1;
     public Spread(String obName) {
     this.obName = obName;
     }
     public boolean isCrossing() {
     return (bask != -1) && (bbid != Long.MAX_VALUE) && (bask <= bbid);
     }
     }
     */
    /**
     * Initialise externalDayLog de chacun des orderbooks à partir d'une
     * trace ATOM en ne considérant que les lignes DAY. Ces infos ne servent
     * que sur des expérimentations en extraday.
     * Le nombre du jour du fichier initialisera le nombre de jours de la
     * simulation.
     * 
     * @param orders name of the file of orders to use to initialize the orderbook
     * @throws FileNotFoundException the file to use is not found
     * @throws IOException the file is not accessible
     */
    public void initDayLog(File orders) throws FileNotFoundException, IOException
    {
        List<DayLog> dayLogs = new ArrayList<>();
        this.initDayLogUsed = true;
        BufferedReader in = new BufferedReader(new FileReader(orders));
        while (in.ready())
        {
            String line = in.readLine();
            if (StringOrderParser.isDay(line))
                dayLogs.add(StringOrderParser.parseDay(line, this));
        }
        in.close();
        initDayLog(dayLogs);
    }

    public void initDayLog(List<DayLog> dayLogs)
    {
        this.initDayLogUsed = true;
        for (DayLog dl : dayLogs)
        {
            OrderBook ob = market.orderBooks.get(dl.obName);
            // Si l'OB n'existe pas, on le créé
            if (ob == null)
            {
                // System.err.println("Creating OB "+dl.obName);
                addNewOrderBook(dl.obName);
                ob = market.orderBooks.get(dl.obName); // TODO: redondance ...
            }
            ob.extradayLog.add(dl);
        }
    }

    public void initOrderBooks(File orders) throws FileNotFoundException, IOException
    {
        /*
         Utilisée uniquement quand on ne voulait pas que les ordres se croisent
        
         Map<String, Spread> spreads = new HashMap<>();
         */

        BufferedReader in = new BufferedReader(new FileReader(orders));
        while (in.ready())
        {
            String line = in.readLine();
            //System.out.println("Handling: "+line);
            if (StringOrderParser.isOrder(line))
            {
                Order o = StringOrderParser.parseOrder(line, this);
                /*
                 // On vérifie qu'il n'y a pas de prix fixé lors de l'initialisation
                 // du carnet d'ordre
                 LimitOrder lo = (LimitOrder) o;
                 if (!spreads.containsKey(lo.obName)) {
                 spreads.put(lo.obName, new Spread(lo.obName));
                 }
                 Spread spread = spreads.get(lo.obName);
                 if (lo.direction == LimitOrder.ASK && lo.price < spread.bask) {
                 spread.bask = lo.price;
                 } else if (lo.direction == LimitOrder.BID && lo.price > spread.bbid) {
                 spread.bbid = lo.price;
                 }
                
                 System.out.println(spread.bask + " <= "+ spread.bbid);
                 if (spread.isCrossing()) {
                 throw new RuntimeException("Price is created while initiliazing price history from file !");
                 }
                 */

                //System.out.println("Sending order to market: "+o);
                market.send(o.sender, o);
            }
            else if (StringOrderParser.isPrice(line))
            {
                PriceRecord price = StringOrderParser.parsePrice(line, this);
                OrderBook ob = market.orderBooks.get(price.obName);
                ob.lastPrices.add(price);
            }
            in.close();
        }
        /**
         * On supprime tous les DumbAgent qui ont été créés automatiquement
         * par le parser, sinon lors du lancement de la simulation, le 'decide'
         * de DumbAgent lèverait une exception
         * <p>
         * C'est différent de Replay car il n'y a pas d'appel à 'run'.
         */
        agentList.clear();
    }

    public void addNewOrderBook(String name)
    {
        addNewMicrostructure(new OrderBook(name));
    }

    public void addNewMarketMaker(String name)
    {
        addNewMicrostructure(new MarketMakerOrderBook(name));
    }

    public void addNewMicrostructure(OrderBook ob)
    {
        ob.log = market.log;
        market.orderBooks.put(ob.obName, ob);
    }

    public void addNewAgent(Agent a)
    {
        a.market = market;
        if (agentList.containsKey(a.name))
            throw new RuntimeException("addNewAgent : not allowed to overwrite an existing agent ! => " + a.name);
        // On pourrait supprimer le addNewInfoAgent en testant par reflexivité si l'agent implemente broadcastNews.
        agentList.put(a.name, a);
    }

    public void addNewInfoAgent(Agent a)
    {
        a.market = market;
        if (nonTradingAgentList.containsKey(a.name))
            throw new RuntimeException("addNewInfoAgent : not allowed to overwrite an existing agent ! => " + a.name);
        nonTradingAgentList.put(a.name, a);
    }

    /*    public long runIntradayFix(int nbFixTicks) {
     return runExtraday(1, nbFixTicks, 0, 0);
     }

     public long runIntradayContinuous(int nbContinuousTicks) {
     return runExtraday(1, 0, nbContinuousTicks, 0);
     }

     public long runExtraday(int days, int nbOpenTicks, int nbContinuousTicks, int nbCloseTicks) {
     return run(Day.createEuroNEXT(nbOpenTicks, nbContinuousTicks, nbCloseTicks), days);
     }*/
    /**
     * Récupère automatiquement le nombre de jours de la simulation au
     * niveau de l'historique des journées des orderbooks.
     * 
     * @param day structre of the Day to execute
     * @return the time in millisec took to execute this day
     */
    public long run(Day day)
    {
        if (!initDayLogUsed)
            throw new RuntimeException("You should call 'initDayLog(File)' in order to call 'run(Day)'");
        OrderBook ob = market.orderBooks.values().iterator().next();
        int numberOfDays = ob.extradayLog.size();
        if (numberOfDays == 0)
            throw new RuntimeException("Cannot initialize the number of days of the simulation");
        return run(day, numberOfDays);
    }

    public long run(Day day, int totalDays)
    {
        // On récupère un OB au pif, on extrait son historique de DayLog et l'on 
        // vérifie que c'est bien le même nombre de jours que le paramètre

        if (initDayLogUsed && totalDays != market.orderBooks.values().iterator().next().extradayLog.size())
            throw new RuntimeException("Cannot specify a number of days when using 'initDayLog(File)'");

        this.day = day;
        this.totalDays = totalDays;
        // Let's start the simulation
        currentDay = 1;
        alive = true;
        long debut = System.currentTimeMillis();
        // running Days
        while (currentDay <= totalDays && alive)
        {
            day.dayNumber = currentDay;
            day.init();
            if (currentDay > 1 && !keepLastDayOrderbook)
                market.clear();
            while (day.hasNextPeriod())
            {
                day.nextPeriod();
                market.setFixingPeriod(day.currentPeriod().getFixing());
                while (day.currentPeriod().currentTick() <= day.currentPeriod().totalTicks())
                {
                    queryAllNonTradingAgents();
                    queryAllAgents();
                    log.tick(day, market.orderBooks.values());
                    day.nextTick();
                }
            }
            // on termine la journée, ie. on fixe les prix en FIX et on ne fais rien en CONTINUOUS
            // close ne vide pas les carnets
            market.close();

            // mise à jour des DayLog pour chacun des orderbooks            
            for (OrderBook ob : market.orderBooks.values())
                if (ob.numberOfPricesFixed > 0)
                    if (initDayLogUsed)
                    {
                        // Daylog déjà présent car injecté par initDayLogs
                        // on fait donc juste une mise à jour
                        DayLog dl = ob.extradayLog.get(currentDay - 1);
                        dl.LOW = ob.lowestPriceOfDay;
                        dl.HIGH = ob.highestPriceOfDay;
                        dl.CLOSE = ob.lastPriceOfDay;
                    }
                    else
                        // ici on créé un nouvel objet en fin de journée
                        ob.extradayLog.add(new DayLog(ob.obName, ob.firstPriceOfDay,
                                ob.lowestPriceOfDay, ob.highestPriceOfDay, ob.lastFixedPrice.price));
                else
                    throw new RuntimeException("No price fixed for OrderBook "
                            + ob.obName + " during day " + currentDay);
            // external Price logging
            // là on loggue "manuellement" => logguer DayLog
            log.day(currentDay, market.orderBooks.values());
            currentDay++;
        }
        alive = false;
        // Simulation is finished, we can close the market
        long fin = System.currentTimeMillis();
        return fin - debut;
    }

    protected abstract void queryAllAgents();

    protected void queryAllNonTradingAgents()
    {
        List<Agent> al = new ArrayList<Agent>(nonTradingAgentList.values());
        if (shuffleAgentList)
            Collections.shuffle(al);
        for (Agent a : al)
            a.broadcastNews(day, agentList);
    }

    public static void main(String[] args)
    {
        Simulation s = new MonothreadedSimulation(new Logger(System.out));

        s.addNewOrderBook("APL");

        // Agent a = new ZIT("foo");
        s.addNewInfoAgent(new Agent(null)
        {
            @Override
            public void broadcastNews(Day day, Map<String, Agent> traders)
            {
            }

            @Override
            public Order decide(String obName, Day day)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        s.addNewAgent(new ZIT("zit"));

        System.out.println("Traders: " + s.agentList.values());
        System.out.println("Info   : " + s.nonTradingAgentList.values());

    }
}
