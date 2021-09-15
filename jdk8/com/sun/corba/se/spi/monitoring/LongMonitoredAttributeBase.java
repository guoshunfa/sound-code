package com.sun.corba.se.spi.monitoring;

public abstract class LongMonitoredAttributeBase extends MonitoredAttributeBase {
   public LongMonitoredAttributeBase(String var1, String var2) {
      super(var1);
      MonitoredAttributeInfoFactory var3 = MonitoringFactories.getMonitoredAttributeInfoFactory();
      MonitoredAttributeInfo var4 = var3.createMonitoredAttributeInfo(var2, Long.class, false, false);
      this.setMonitoredAttributeInfo(var4);
   }
}
