package com.sun.org.glassfish.external.statistics;

public interface TimeStatistic extends Statistic {
   long getCount();

   long getMaxTime();

   long getMinTime();

   long getTotalTime();
}
