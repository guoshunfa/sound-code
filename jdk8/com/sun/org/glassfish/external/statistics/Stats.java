package com.sun.org.glassfish.external.statistics;

public interface Stats {
   Statistic getStatistic(String var1);

   String[] getStatisticNames();

   Statistic[] getStatistics();
}
