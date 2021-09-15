package com.sun.corba.se.spi.monitoring;

public interface MonitoringManagerFactory {
   MonitoringManager createMonitoringManager(String var1, String var2);

   void remove(String var1);
}
