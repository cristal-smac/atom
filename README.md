# ATOM
ATOM means *Artificial Open Market*, package built in 2010 by Pr Philippe Mathieu (CRISTAL, University of Lille) and Pr Olivier Brandouy (Gretha, Bordeaux university)

ATOM is a Java API with which you can build any kind of experiments on order-driven markets like NYSE or Euronext. ATOM contains a Market on which you can add any number of double auction orderbooks, one for each asset (option) you want to trade. It also manages some artificial agents that you may have to build and add to your experiments. We give in this package few basic agents like the classic Zero Intelligent Trader (ZIT). The given agents are able to trade simultaneously on all the orderbooks added in the Market. But you can of course easily build your own experiments with your own agents.
ATOM contains a powerful log system. This aspect facilitates all the statistics you could need, curves you could have to plot or stylized facts you could have to verify. This log file can also be integrally replayed with the ATOM flow replayer, not only able to send once again the orders, but also able to re-create all the agents asking all of them to re-send their own orders in the same scheduling.

Main characteristics :
- Multi-agents. you can create and add any kind of new agent for your experiments
- Multi-orders. All the Euronext orders are included : Limit, Market, Iceberg, StopLimit, StopMarket, Cancel and Update orders.
- Muti price fixing procedures. Maintly Fix price fixing, and continuous price fixing
- Open day configuration. Your can easily build your own day configuration mixing fix and continuous periods.
- Intraday or Extraday experiments
- Fast : 100 agents during 10 days of 1000 ticks (thus 1 million orders !) is done in less than 12 seconds

## Two directories are available :
- `dist` for classical users (jar provided with java examples)
- `dev` for source code (Maven Project able to re-build the jar file)

# Examples

You should have Java (version 1.8 at least) installed on your system (freely available here:
http://www.oracle.com/technetwork/java/javase/downloads).

You have two main commands given with ATOM : `Generate` to generate an experiment with ZIT agents. `Replay` to use ATOM as a flow replayer. Here are three basic ATOM usages (consider you are in the `dist` directory) : 

## First one : Generate and analyze data
```
> java -cp atom.jar fr.cristal.smac.atom.Generate 10 1 1000 1
Syntax: <nbAgents> <nbOrderbooks> <nbTurns> <nbDays>
```
You then have a log file that you can analyse. You can notably see the orders sent, the prices, the agents's states etc ...

Use `Grep` for example to filter lines
```
> java -cp atom.jar fr.cristal.smac.atom.Generate 10 1 1000 1 > myfile
> grep '^Price' myfile
> grep '^Agent' myfile | grep ZIT1
```

Use `R`(or gnuplot) to plot prices
```
java -cp atom.jar fr.cristal.smac.atom.Generate 10 1 1000 1 | grep '^Price' > prices.csv
```
and in R
```
prices <- read.csv(file='prices.csv', sep=";" , header=TRUE)
plot(prices$price, type='l', col='red', ylim=c(13000,16000))
```

## Second one : replay files
```
> java -cp atom.jar fr.cristal.smac.atom.Replay orderFileExample1
```

See this file. It is really easy to build your own. but you can also replay exactly a file with several agents
as one obtained with Generate
```
> java -cp atom.jar fr.cristal.smac.atom.Generate 10 1 1000 1 > myfile1
> java -cp atom.jar fr.cristal;smac.atom.Replay myfile > myfile2
```

## Third one : Write your own code, with your agents
See `Tutorial.java`


# How to make a reference to ATOM*

- Philippe Mathieu and Olivier Brandouy. *A Generic Architecture for Realistic Simulations of Complex Financial Dynamics.* in Advances in Practical Applications of Agents and Multiagent Systems, 8th International conference on Practical Applications of Agents and Multi-Agents Systems (PAAMS'2010), pages 185-197, Salamanca (Spain), 26-28th April, 2010
- Philippe Mathieu and Olivier Brandouy. *Efficient Monitoring of Financial Orders with Agent-Based Technologies.* in Proceedings of the 9th International conference on Practical Applications of Agents and Multi-Agents Systems (PAAMS'2011), pages 277-286, Salamanca (Spain), 6th-8th April 2011
- Philippe Mathieu and Olivier Brandouy. *Introducing ATOM*. in Proceedings of the 10th International conference on Practical
Applications of Agents and Multi-Agents Systems (PAAMS'2012), Salamanca (Spain) -- 28th-30th March 2012

