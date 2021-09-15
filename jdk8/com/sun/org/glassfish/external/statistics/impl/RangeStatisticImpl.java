package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.RangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class RangeStatisticImpl extends StatisticImpl implements RangeStatistic, InvocationHandler {
   private long currentVal = 0L;
   private long highWaterMark = Long.MIN_VALUE;
   private long lowWaterMark = Long.MAX_VALUE;
   private final long initCurrentVal;
   private final long initHighWaterMark;
   private final long initLowWaterMark;
   private final RangeStatistic rs = (RangeStatistic)Proxy.newProxyInstance(RangeStatistic.class.getClassLoader(), new Class[]{RangeStatistic.class}, this);

   public RangeStatisticImpl(long curVal, long highMark, long lowMark, String name, String unit, String desc, long startTime, long sampleTime) {
      super(name, unit, desc, startTime, sampleTime);
      this.currentVal = curVal;
      this.initCurrentVal = curVal;
      this.highWaterMark = highMark;
      this.initHighWaterMark = highMark;
      this.lowWaterMark = lowMark;
      this.initLowWaterMark = lowMark;
   }

   public synchronized RangeStatistic getStatistic() {
      return this.rs;
   }

   public synchronized Map getStaticAsMap() {
      Map m = super.getStaticAsMap();
      m.put("current", this.getCurrent());
      m.put("lowwatermark", this.getLowWaterMark());
      m.put("highwatermark", this.getHighWaterMark());
      return m;
   }

   public synchronized long getCurrent() {
      return this.currentVal;
   }

   public synchronized void setCurrent(long curVal) {
      this.currentVal = curVal;
      this.lowWaterMark = curVal >= this.lowWaterMark ? this.lowWaterMark : curVal;
      this.highWaterMark = curVal >= this.highWaterMark ? curVal : this.highWaterMark;
      this.sampleTime = System.currentTimeMillis();
   }

   public synchronized long getHighWaterMark() {
      return this.highWaterMark;
   }

   public synchronized void setHighWaterMark(long hwm) {
      this.highWaterMark = hwm;
   }

   public synchronized long getLowWaterMark() {
      return this.lowWaterMark;
   }

   public synchronized void setLowWaterMark(long lwm) {
      this.lowWaterMark = lwm;
   }

   public synchronized void reset() {
      super.reset();
      this.currentVal = this.initCurrentVal;
      this.highWaterMark = this.initHighWaterMark;
      this.lowWaterMark = this.initLowWaterMark;
      this.sampleTime = -1L;
   }

   public synchronized String toString() {
      return super.toString() + NEWLINE + "Current: " + this.getCurrent() + NEWLINE + "LowWaterMark: " + this.getLowWaterMark() + NEWLINE + "HighWaterMark: " + this.getHighWaterMark();
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
