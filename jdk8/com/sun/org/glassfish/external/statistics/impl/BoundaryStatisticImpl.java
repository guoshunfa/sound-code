package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.BoundaryStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class BoundaryStatisticImpl extends StatisticImpl implements BoundaryStatistic, InvocationHandler {
   private final long lowerBound;
   private final long upperBound;
   private final BoundaryStatistic bs = (BoundaryStatistic)Proxy.newProxyInstance(BoundaryStatistic.class.getClassLoader(), new Class[]{BoundaryStatistic.class}, this);

   public BoundaryStatisticImpl(long lower, long upper, String name, String unit, String desc, long startTime, long sampleTime) {
      super(name, unit, desc, startTime, sampleTime);
      this.upperBound = upper;
      this.lowerBound = lower;
   }

   public synchronized BoundaryStatistic getStatistic() {
      return this.bs;
   }

   public synchronized Map getStaticAsMap() {
      Map m = super.getStaticAsMap();
      m.put("lowerbound", this.getLowerBound());
      m.put("upperbound", this.getUpperBound());
      return m;
   }

   public synchronized long getLowerBound() {
      return this.lowerBound;
   }

   public synchronized long getUpperBound() {
      return this.upperBound;
   }

   public synchronized void reset() {
      super.reset();
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
