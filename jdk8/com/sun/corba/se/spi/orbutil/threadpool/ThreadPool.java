package com.sun.corba.se.spi.orbutil.threadpool;

import java.io.Closeable;

public interface ThreadPool extends Closeable {
   WorkQueue getAnyWorkQueue();

   WorkQueue getWorkQueue(int var1) throws NoSuchWorkQueueException;

   int numberOfWorkQueues();

   int minimumNumberOfThreads();

   int maximumNumberOfThreads();

   long idleTimeoutForThreads();

   int currentNumberOfThreads();

   int numberOfAvailableThreads();

   int numberOfBusyThreads();

   long currentProcessedCount();

   long averageWorkCompletionTime();

   String getName();
}
