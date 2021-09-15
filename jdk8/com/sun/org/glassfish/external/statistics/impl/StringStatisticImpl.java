package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.StringStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class StringStatisticImpl extends StatisticImpl implements StringStatistic, InvocationHandler {
   private volatile String str;
   private final String initStr;
   private final StringStatistic ss;

   public StringStatisticImpl(String str, String name, String unit, String desc, long sampleTime, long startTime) {
      super(name, unit, desc, startTime, sampleTime);
      this.str = null;
      this.ss = (StringStatistic)Proxy.newProxyInstance(StringStatistic.class.getClassLoader(), new Class[]{StringStatistic.class}, this);
      this.str = str;
      this.initStr = str;
   }

   public StringStatisticImpl(String name, String unit, String desc) {
      this("", name, unit, desc, System.currentTimeMillis(), System.currentTimeMillis());
   }

   public synchronized StringStatistic getStatistic() {
      return this.ss;
   }

   public synchronized Map getStaticAsMap() {
      Map m = super.getStaticAsMap();
      if (this.getCurrent() != null) {
         m.put("current", this.getCurrent());
      }

      return m;
   }

   public synchronized String toString() {
      return super.toString() + NEWLINE + "Current-value: " + this.getCurrent();
   }

   public String getCurrent() {
      return this.str;
   }

   public void setCurrent(String str) {
      this.str = str;
      this.sampleTime = System.currentTimeMillis();
   }

   public synchronized void reset() {
      super.reset();
      this.str = this.initStr;
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
