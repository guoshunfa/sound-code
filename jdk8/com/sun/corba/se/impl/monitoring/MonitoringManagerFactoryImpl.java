package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;
import java.util.HashMap;

public class MonitoringManagerFactoryImpl implements MonitoringManagerFactory {
   private HashMap monitoringManagerTable = new HashMap();

   public synchronized MonitoringManager createMonitoringManager(String var1, String var2) {
      MonitoringManagerImpl var3 = null;
      var3 = (MonitoringManagerImpl)this.monitoringManagerTable.get(var1);
      if (var3 == null) {
         var3 = new MonitoringManagerImpl(var1, var2);
         this.monitoringManagerTable.put(var1, var3);
      }

      return var3;
   }

   public synchronized void remove(String var1) {
      this.monitoringManagerTable.remove(var1);
   }
}
