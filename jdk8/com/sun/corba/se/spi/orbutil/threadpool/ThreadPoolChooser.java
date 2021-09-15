package com.sun.corba.se.spi.orbutil.threadpool;

public interface ThreadPoolChooser {
   ThreadPool getThreadPool();

   ThreadPool getThreadPool(int var1);

   String[] getThreadPoolIds();
}
