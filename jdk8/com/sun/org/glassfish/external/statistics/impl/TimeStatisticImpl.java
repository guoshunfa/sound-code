package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.TimeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class TimeStatisticImpl extends StatisticImpl implements TimeStatistic, InvocationHandler {
   private long count = 0L;
   private long maxTime = 0L;
   private long minTime = 0L;
   private long totTime = 0L;
   private final long initCount;
   private final long initMaxTime;
   private final long initMinTime;
   private final long initTotTime;
   private final TimeStatistic ts = (TimeStatistic)Proxy.newProxyInstance(TimeStatistic.class.getClassLoader(), new Class[]{TimeStatistic.class}, this);

   public final synchronized String toString() {
      return super.toString() + NEWLINE + "Count: " + this.getCount() + NEWLINE + "MinTime: " + this.getMinTime() + NEWLINE + "MaxTime: " + this.getMaxTime() + NEWLINE + "TotalTime: " + this.getTotalTime();
   }

   public TimeStatisticImpl(long counter, long maximumTime, long minimumTime, long totalTime, String name, String unit, String desc, long startTime, long sampleTime) {
      super(name, unit, desc, startTime, sampleTime);
      this.count = counter;
      this.initCount = counter;
      this.maxTime = maximumTime;
      this.initMaxTime = maximumTime;
      this.minTime = minimumTime;
      this.initMinTime = minimumTime;
      this.totTime = totalTime;
      this.initTotTime = totalTime;
   }

   public synchronized TimeStatistic getStatistic() {
      return this.ts;
   }

   public synchronized Map getStaticAsMap() {
      Map m = super.getStaticAsMap();
      m.put("count", this.getCount());
      m.put("maxtime", this.getMaxTime());
      m.put("mintime", this.getMinTime());
      m.put("totaltime", this.getTotalTime());
      return m;
   }

   public synchronized void incrementCount(long current) {
      if (this.count == 0L) {
         this.totTime = current;
         this.maxTime = current;
         this.minTime = current;
      } else {
         this.totTime += current;
         this.maxTime = current >= this.maxTime ? current : this.maxTime;
         this.minTime = current >= this.minTime ? this.minTime : current;
      }

      ++this.count;
      this.sampleTime = System.currentTimeMillis();
   }

   public synchronized long getCount() {
      return this.count;
   }

   public synchronized long getMaxTime() {
      return this.maxTime;
   }

   public synchronized long getMinTime() {
      return this.minTime;
   }

   public synchronized long getTotalTime() {
      return this.totTime;
   }

   public synchronized void reset() {
      super.reset();
      this.count = this.initCount;
      this.maxTime = this.initMaxTime;
      this.minTime = this.initMinTime;
      this.totTime = this.initTotTime;
      this.sampleTime = -1L;
   }

   public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
      this.checkMethod(m);

      try {
         Object result = m.invoke(this, args);
         return result;
      } catch (InvocationTargetException var6) {
         throw var6.getTargetException();
      } catch (Exception var7) {
         throw new RuntimeException("unexpected invocation exception: " + var7.getMessage());
      }
   }
}
