package com.sun.org.glassfish.external.statistics;

public interface RangeStatistic extends Statistic {
   long getHighWaterMark();

   long getLowWaterMark();

   long getCurrent();
}
