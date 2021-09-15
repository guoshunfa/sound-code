package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.Statistic;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StatisticImpl implements Statistic {
   private final String statisticName;
   private final String statisticUnit;
   private final String statisticDesc;
   protected long sampleTime;
   private long startTime;
   public static final String UNIT_COUNT = "count";
   public static final String UNIT_SECOND = "second";
   public static final String UNIT_MILLISECOND = "millisecond";
   public static final String UNIT_MICROSECOND = "microsecond";
   public static final String UNIT_NANOSECOND = "nanosecond";
   public static final String START_TIME = "starttime";
   public static final String LAST_SAMPLE_TIME = "lastsampletime";
   protected final Map<String, Object> statMap;
   protected static final String NEWLINE = System.getProperty("line.separator");

   protected StatisticImpl(String name, String unit, String desc, long start_time, long sample_time) {
      this.sampleTime = -1L;
      this.statMap = new ConcurrentHashMap();
      if (isValidString(name)) {
         this.statisticName = name;
      } else {
         this.statisticName = "name";
      }

      if (isValidString(unit)) {
         this.statisticUnit = unit;
      } else {
         this.statisticUnit = "unit";
      }

      if (isValidString(desc)) {
         this.statisticDesc = desc;
      } else {
         this.statisticDesc = "description";
      }

      this.startTime = start_time;
      this.sampleTime = sample_time;
   }

   protected StatisticImpl(String name, String unit, String desc) {
      this(name, unit, desc, System.currentTimeMillis(), System.currentTimeMillis());
   }

   public synchronized Map getStaticAsMap() {
      if (isValidString(this.statisticName)) {
         this.statMap.put("name", this.statisticName);
      }

      if (isValidString(this.statisticUnit)) {
         this.statMap.put("unit", this.statisticUnit);
      }

      if (isValidString(this.statisticDesc)) {
         this.statMap.put("description", this.statisticDesc);
      }

      this.statMap.put("starttime", this.startTime);
      this.statMap.put("lastsampletime", this.sampleTime);
      return this.statMap;
   }

   public String getName() {
      return this.statisticName;
   }

   public String getDescription() {
      return this.statisticDesc;
   }

   public String getUnit() {
      return this.statisticUnit;
   }

   public synchronized long getLastSampleTime() {
      return this.sampleTime;
   }

   public synchronized long getStartTime() {
      return this.startTime;
   }

   public synchronized void reset() {
      this.startTime = System.currentTimeMillis();
   }

   public synchronized String toString() {
      return "Statistic " + this.getClass().getName() + NEWLINE + "Name: " + this.getName() + NEWLINE + "Description: " + this.getDescription() + NEWLINE + "Unit: " + this.getUnit() + NEWLINE + "LastSampleTime: " + this.getLastSampleTime() + NEWLINE + "StartTime: " + this.getStartTime();
   }

   protected static boolean isValidString(String str) {
      return str != null && str.length() > 0;
   }

   protected void checkMethod(Method method) {
      if (method == null || method.getDeclaringClass() == null || !Statistic.class.isAssignableFrom(method.getDeclaringClass()) || Modifier.isStatic(method.getModifiers())) {
         throw new RuntimeException("Invalid method on invoke");
      }
   }
}
