package com.sun.org.glassfish.external.statistics;

public interface BoundaryStatistic extends Statistic {
   long getUpperBound();

   long getLowerBound();
}
