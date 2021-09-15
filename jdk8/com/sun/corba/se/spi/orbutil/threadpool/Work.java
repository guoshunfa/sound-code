package com.sun.corba.se.spi.orbutil.threadpool;

public interface Work {
   void doWork();

   void setEnqueueTime(long var1);

   long getEnqueueTime();

   String getName();
}
