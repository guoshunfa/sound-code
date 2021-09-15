package com.sun.corba.se.spi.orbutil.threadpool;

import java.io.Closeable;

public interface ThreadPoolManager extends Closeable {
   ThreadPool getThreadPool(String var1) throws NoSuchThreadPoolException;

   ThreadPool getThreadPool(int var1) throws NoSuchThreadPoolException;

   int getThreadPoolNumericId(String var1);

   String getThreadPoolStringId(int var1);

   ThreadPool getDefaultThreadPool();

   ThreadPoolChooser getThreadPoolChooser(String var1);

   ThreadPoolChooser getThreadPoolChooser(int var1);

   void setThreadPoolChooser(String var1, ThreadPoolChooser var2);

   int getThreadPoolChooserNumericId(String var1);
}
