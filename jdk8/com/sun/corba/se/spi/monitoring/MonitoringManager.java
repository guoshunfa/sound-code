package com.sun.corba.se.spi.monitoring;

import java.io.Closeable;

public interface MonitoringManager extends Closeable {
   MonitoredObject getRootMonitoredObject();

   void clearState();
}
