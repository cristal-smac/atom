# ATOM
ATOM means *Artificial Open Market* and is a Java API built in 2008 by Pr Philippe Mathieu ([CRISTAL](http://www.cristal.univ-lille.fr), [SMAC team](https://www.cristal.univ-lille.fr/?rubrique27&eid=17), [Lille University](http://www.univ-lille.fr)) and Pr Olivier Brandouy ([Gretha](https://gretha.u-bordeaux.fr/), [Bordeaux university](https://www.u-bordeaux.fr/))

Contact : atom at univ-lille.fr

ATOM is a Java API with which you can build any kind of experiments on order-driven markets like NYSE or Euronext. ATOM contains a Market on which you can add any number of double auction orderbooks, one for each asset (option) you want to trade. It also manages some artificial agents that you may have to build and add to your experiments. We give in this package few basic agents like the classic Zero Intelligent Trader (ZIT). The given agents are able to trade simultaneously on all the orderbooks added in the Market. But you can of course easily build your own experiments with your own agents.
ATOM contains a powerful log system. This aspect facilitates all the statistics you could need, curves you could have to plot or stylized facts you could have to verify. This log file can also be integrally replayed with the ATOM flow replayer, not only able to send once again the orders, but also able to re-create all the agents asking all of them to re-send their own orders in the same scheduling.

Main features :
- Multi-agents. you can create and add any kind of new agent or new behaviour for your experiments
- Multi-orders. All the Euronext orders are included : Limit, Market, MarketToLimit, Iceberg, StopLimit, StopMarket, Cancel and Update orders.
- Muti price fixing procedures. Mainly Fix price fixing, and continuous price fixing are available
- Open day configuration. Your can easily build your own day configuration mixing fix and continuous periods. Euronext (3 periods : open, continuous and close) is of course direcly available.
- Multiagent flow replayer. When a data file is replayed, all the cited agents are created and impacted, allowing to study the feedback when replaying
- Intraday or Extraday experiments
- Fast : 100 agents during 10 days of 1000 ticks (thus 1 million orders !) is done in less than 12 seconds

## Two directories are available :
- `dist` for classical users (jar provided with java examples)
- `dev` for source code (Maven Project able to re-build the jar file)

# Examples

You should have Java (version 1.7 and above is required) installed on your system (freely available on
[Oracle web site](http://www.oracle.com/technetwork/java/javase/downloads)).

You have two main commands given with ATOM : 
- `Generate` to generate an experiment with ZIT agents. 
- `Replay` to use ATOM as a flow replayer. 

Here are three basic ATOM usages (consider you are in the `dist` directory) : 

## First one : Generate and analyze data
```
java -cp atom-1.14.jar fr.cristal.smac.atom.Generate 10 1 1000 1
```
`Syntax: <nbAgents> <nbOrderbooks> <nbTurns> <nbDays>`
You then have a log file that you can analyse. You can notably see the orders sent, the prices, the agents's states etc ...

Use [Grep](https://www.gnu.org/software/grep/manual/grep.html) for example to filter lines (or any Spreadsheet if you prefer)
```
java -cp atom-1.14.jar fr.cristal.smac.atom.Generate 10 1 1000 1 > myfile
grep '^Price' myfile
grep '^Agent' myfile | grep ZIT1
```

Use [R project](https://www.r-project.org) (or [gnuplot](http://www.gnuplot.info/)) to plot prices
```
java -cp atom-1.14.jar fr.cristal.smac.atom.Generate 10 1 1000 1 | grep '^Price' > prices.csv
```
and in R
```
prices <- read.csv(file='prices.csv', sep=";" , header=TRUE)
plot(prices$price, type='l', col='red', ylim=c(13000,16000))
```

Or if you want to see one agent' evolution
```
java -cp atom-1.14.jar fr.cristal.smac.atom.Generate 10 1 1000 1 | grep '^Agent;ZIT2' > agent.csv
```
and in R
```
agent <- read.csv(file='agent.csv', sep=";" , header=FALSE)
plot((agent$V3+(agent$V5*agent$V6), type='l', col='blue')
```

## Second one : replay files
Several examples are given in the `data` repository. 
```
java -cp atom-1.14.jar fr.cristal.smac.atom.Replay data/orderFileExample1
```
Have a look to the given data files. Several commands can be used in a data file allowing to understand or test many situations. It is really easy to build your own. Whatever the content, only lines beginning with the keyword *Order* are taken into account; the others are simply skipped.

ATOM ensures a vertuous loop between Replay and Generate. From a previously generated log file, ATOM is able to replay exactly the same scenario which have generated the log file in the first place, including the agents, the orderbooks, ...
```
java -cp atom-1.14.jar fr.cristal.smac.atom.Generate 10 1 1000 1 > myfile1
java -cp atom-1.14.jar fr.cristal.smac.atom.Replay myfile1 > myfile2
diff myfile1 myfile2
```
The two files are perfectly identical. You can see that ATOM's flow replayer allows you to perfectly recreate orderbooks and agents from a previous experience, in a virtuous loop.


## Third one : Write your own code, with your agents
In this case you need to know Java coding. Include the jar file in your IDE (Eclipse, Netbeans, IntelliJ) or use command line to build exactly what you need. The `Tutorial.java` illustrates how to build its own experience and its own agents.
```
javac -cp .:atom-1.14.jar Tutorial.java
java  -cp .:atom-1.14.jar Tutorial
```


# How to make a reference to ATOM

- Philippe Mathieu and Olivier Brandouy. *A Generic Architecture for Realistic Simulations of Complex Financial Dynamics.* in Advances in Practical Applications of Agents and Multiagent Systems, 8th International conference on Practical Applications of Agents and Multi-Agents Systems (PAAMS'2010), pages 185-197, Salamanca (Spain), 26-28th April, 2010 ([bibtex](https://scholar.google.fr/scholar?hl=fr&as_sdt=0%2C5&q=A+Generic+Architecture+for+Realistic+Simulations+of+Complex+Financial+Dynamics&btnG=))
- Philippe Mathieu and Olivier Brandouy. *Efficient Monitoring of Financial Orders with Agent-Based Technologies.* in Proceedings of the 9th International conference on Practical Applications of Agents and Multi-Agents Systems (PAAMS'2011), pages 277-286, Salamanca (Spain), 6th-8th April 2011 ([bibtex](https://scholar.google.fr/scholar?hl=fr&as_sdt=0%2C5&q=Efficient+Monitoring+of+Financial+Orders+with+Agent-Based+Technologies&btnG=))
- Philippe Mathieu and Olivier Brandouy. *Introducing ATOM*. in Proceedings of the 10th International conference on Practical
Applications of Agents and Multi-Agents Systems (PAAMS'2012), Salamanca (Spain) -- 28th-30th March 2012 ([bibtex](https://scholar.google.fr/scholar?hl=fr&as_sdt=0%2C5&q=Introducing+ATOM+PAAMS+Mathieu+Brandouy&btnG=))

Our other publications [here](https://scholar.google.fr/scholar?hl=fr&as_sdt=0%2C5&q=Mathieu+Brandouy&btnG=)

# License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.
If not, see http://www.gnu.org/licenses/.
