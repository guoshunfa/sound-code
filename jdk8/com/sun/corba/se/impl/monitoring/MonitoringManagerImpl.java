package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;

public class MonitoringManagerImpl implements MonitoringManager {
   private final MonitoredObject rootMonitoredObject;

   MonitoringManagerImpl(String var1, String var2) {
      MonitoredObjectFactory var3 = MonitoringFactories.getMonitoredObjectFactory();
      this.rootMonitoredObject = var3.createMonitoredObject(var1, var2);
   }

   public void clearState() {
      this.rootMonitoredObject.clearState();
   }

   public MonitoredObject getRootMonitoredObject() {
      return this.rootMonitoredObject;
   }

   public void close() {
      MonitoringManagerFactory var1 = MonitoringFactories.getMonitoringManagerFactory();
      var1.remove(this.rootMonitoredObject.getName());
   }
}
