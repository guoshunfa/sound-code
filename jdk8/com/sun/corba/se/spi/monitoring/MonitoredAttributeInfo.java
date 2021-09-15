package com.sun.corba.se.spi.monitoring;

public interface MonitoredAttributeInfo {
   boolean isWritable();

   boolean isStatistic();

   Class type();

   String getDescription();
}
