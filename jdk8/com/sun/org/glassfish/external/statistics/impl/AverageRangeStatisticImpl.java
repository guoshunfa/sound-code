package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.AverageRangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class AverageRangeStatisticImpl extends StatisticImpl implements AverageRangeStatistic, InvocationHandler {
   private long currentVal = 0L;
   private long highWaterMark = Long.MIN_VALUE;
   private long lowWaterMark = Long.MAX_VALUE;
   private long numberOfSamples = 0L;
   private long runningTotal = 0L;
   private final long initCurrentVal;
   private final long initHighWaterMark;
   private final long initLowWaterMark;
   private final long initNumberOfSamples;
   private final long initRunningTotal;
   private final AverageRangeStatistic as = (AverageRangeStatistic)Proxy.newProxyInstance(AverageRangeStatistic.class.getClassLoader(), new Class[]{AverageRangeStatistic.class}, this);

   public AverageRangeStatisticImpl(long curVal, long highMark, long lowMark, String name, String unit, String desc, long startTime, long sampleTime) {
      super(name, unit, desc, startTime, sampleTime);
      this.currentVal = curVal;
      this.initCurrentVal = curVal;
      this.highWaterMark = highMark;
      this.initHighWaterMark = highMark;
      this.lowWaterMark = lowMark;
      this.initLowWaterMark = lowMark;
      this.numberOfSamples = 0L;
      this.initNumberOfSamples = this.numberOfSamples;
      this.runningTotal = 0L;
      this.initRunningTotal = this.runningTotal;
   }

   public synchronized AverageRangeStatistic getStatistic() {
      return this.as;
   }

   public synchronized String toString() {
      return super.toString() + NEWLINE + "Current: " + this.getCurrent() + NEWLINE + "LowWaterMark: " + this.getLowWaterMark() + NEWLINE + "HighWaterMark: " + this.getHighWaterMark() + NEWLINE + "Average:" + this.getAverage();
   }

   public synchronized Map getStaticAsMap() {
      Map m = super.getStaticAsMap();
      m.put("current", this.getCurrent());
      m.put("lowwatermark", this.getLowWaterMark());
      m.put("highwatermark", this.getHighWaterMark());
      m.put("average", this.getAverage());
      return m;
   }

   public synchronized void reset() {
      super.reset();
      this.currentVal = this.initCurrentVal;
      this.highWaterMark = this.initHighWaterMark;
      this.lowWaterMark = this.initLowWaterMark;
      this.numberOfSamples = this.initNumberOfSamples;
      this.runningTotal = this.initRunningTotal;
      this.sampleTime = -1L;
   }

   public synchronized long getAverage() {
      return this.numberOfSamples == 0L ? -1L : this.runningTotal / this.numberOfSamples;
   }

   public synchronized long getCurrent() {
      return this.currentVal;
   }

   public synchronized void setCurrent(long curVal) {
      this.currentVal = curVal;
      this.lowWaterMark = curVal >= this.lowWaterMark ? this.lowWaterMark : curVal;
      this.highWaterMark = curVal >= this.highWaterMark ? curVal : this.highWaterMark;
      ++this.numberOfSamples;
      this.runningTotal += curVal;
      this.sampleTime = System.currentTimeMillis();
   }

   public synchronized long getHighWaterMark() {
      return this.highWaterMark;
   }

   public synchronized long getLowWaterMark() {
      return this.lowWaterMark;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      this.checkMethod(method);

      try {
         Object result = method.invoke(this, args);
         return result;
      } catch (InvocationTargetException var6) {
         throw var6.getTargetException();
      } catch (Exception var7) {
         throw new RuntimeException("unexpected invocation exception: " + var7.getMessage());
      }
   }
}
