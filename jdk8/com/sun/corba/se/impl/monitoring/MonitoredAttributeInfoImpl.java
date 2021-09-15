package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;

public class MonitoredAttributeInfoImpl implements MonitoredAttributeInfo {
   private final String description;
   private final Class type;
   private final boolean writableFlag;
   private final boolean statisticFlag;

   MonitoredAttributeInfoImpl(String var1, Class var2, boolean var3, boolean var4) {
      this.description = var1;
      this.type = var2;
      this.writableFlag = var3;
      this.statisticFlag = var4;
   }

   public String getDescription() {
      return this.description;
   }

   public Class type() {
      return this.type;
   }

   public boolean isWritable() {
      return this.writableFlag;
   }

   public boolean isStatistic() {
      return this.statisticFlag;
   }
}
