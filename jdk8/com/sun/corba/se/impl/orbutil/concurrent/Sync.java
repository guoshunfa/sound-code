package com.sun.corba.se.impl.orbutil.concurrent;

public interface Sync {
   long ONE_SECOND = 1000L;
   long ONE_MINUTE = 60000L;
   long ONE_HOUR = 3600000L;
   long ONE_DAY = 86400000L;
   long ONE_WEEK = 604800000L;
   long ONE_YEAR = 31556952000L;
   long ONE_CENTURY = 3155695200000L;

   void acquire() throws InterruptedException;

   boolean attempt(long var1) throws InterruptedException;

   void release();
}
