package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;

public class MonitoredObjectFactoryImpl implements MonitoredObjectFactory {
   public MonitoredObject createMonitoredObject(String var1, String var2) {
      return new MonitoredObjectImpl(var1, var2);
   }
}
