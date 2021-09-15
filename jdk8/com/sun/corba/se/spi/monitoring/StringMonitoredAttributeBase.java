package com.sun.corba.se.spi.monitoring;

public abstract class StringMonitoredAttributeBase extends MonitoredAttributeBase {
   public StringMonitoredAttributeBase(String var1, String var2) {
      super(var1);
      MonitoredAttributeInfoFactory var3 = MonitoringFactories.getMonitoredAttributeInfoFactory();
      MonitoredAttributeInfo var4 = var3.createMonitoredAttributeInfo(var2, String.class, false, false);
      this.setMonitoredAttributeInfo(var4);
   }
}
