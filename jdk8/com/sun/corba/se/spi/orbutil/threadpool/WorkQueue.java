package com.sun.corba.se.spi.orbutil.threadpool;

public interface WorkQueue {
   void addWork(Work var1);

   String getName();

   long totalWorkItemsAdded();

   int workItemsInQueue();

   long averageTimeInQueue();

   void setThreadPool(ThreadPool var1);

   ThreadPool getThreadPool();
}
