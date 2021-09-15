package com.sun.org.glassfish.external.statistics;

public interface Statistic {
   String getName();

   String getUnit();

   String getDescription();

   long getStartTime();

   long getLastSampleTime();
}
