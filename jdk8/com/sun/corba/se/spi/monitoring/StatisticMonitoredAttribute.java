package com.sun.corba.se.spi.monitoring;

public class StatisticMonitoredAttribute extends MonitoredAttributeBase {
   private StatisticsAccumulator statisticsAccumulator;
   private Object mutex;

   public StatisticMonitoredAttribute(String var1, String var2, StatisticsAccumulator var3, Object var4) {
      super(var1);
      MonitoredAttributeInfoFactory var5 = MonitoringFactories.getMonitoredAttributeInfoFactory();
      MonitoredAttributeInfo var6 = var5.createMonitoredAttributeInfo(var2, String.class, false, true);
      this.setMonitoredAttributeInfo(var6);
      this.statisticsAccumulator = var3;
      this.mutex = var4;
   }

   public Object getValue() {
      synchronized(this.mutex) {
         return this.statisticsAccumulator.getValue();
      }
   }

   public void clearState() {
      synchronized(this.mutex) {
         this.statisticsAccumulator.clearState();
      }
   }

   public StatisticsAccumulator getStatisticsAccumulator() {
      return this.statisticsAccumulator;
   }
}
