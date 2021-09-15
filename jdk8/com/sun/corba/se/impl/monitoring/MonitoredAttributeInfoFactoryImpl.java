package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;
import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfoFactory;

public class MonitoredAttributeInfoFactoryImpl implements MonitoredAttributeInfoFactory {
   public MonitoredAttributeInfo createMonitoredAttributeInfo(String var1, Class var2, boolean var3, boolean var4) {
      return new MonitoredAttributeInfoImpl(var1, var2, var3, var4);
   }
}
