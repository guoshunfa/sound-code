package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.CountStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class CountStatisticImpl extends StatisticImpl implements CountStatistic, InvocationHandler {
   private long count;
   private final long initCount;
   private final CountStatistic cs;

   public CountStatisticImpl(long countVal, String name, String unit, String desc, long sampleTime, long startTime) {
      super(name, unit, desc, startTime, sampleTime);
      this.count = 0L;
      this.cs = (CountStatistic)Proxy.newProxyInstance(CountStatistic.class.getClassLoader(), new Class[]{CountStatistic.class}, this);
      this.count = countVal;
      this.initCount = countVal;
   }

   public CountStatisticImpl(String name, String unit, String desc) {
      this(0L, name, unit, desc, -1L, System.currentTimeMillis());
   }

   public synchronized CountStatistic getStatistic() {
      return this.cs;
   }

   public synchronized Map getStaticAsMap() {
      Map m = super.getStaticAsMap();
      m.put("count", this.getCount());
      return m;
   }

   public synchronized String toString() {
      return super.toString() + NEWLINE + "Count: " + this.getCount();
   }

   public synchronized long getCount() {
      return this.count;
   }

   public synchronized void setCount(long countVal) {
      this.count = countVal;
      this.sampleTime = System.currentTimeMillis();
   }

   public synchronized void increment() {
      ++this.count;
      this.sampleTime = System.currentTimeMillis();
   }

   public synchronized void increment(long delta) {
      this.count += delta;
      this.sampleTime = System.currentTimeMillis();
   }

   public synchronized void decrement() {
      --this.count;
      this.sampleTime = System.currentTimeMillis();
   }

   public synchronized void reset() {
      super.reset();
      this.count = this.initCount;
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
