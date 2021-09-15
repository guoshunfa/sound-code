package com.sun.jmx.snmp.tasks;

import java.util.ArrayList;

public class ThreadService implements TaskServer {
   private ArrayList<Runnable> jobList = new ArrayList(0);
   private ThreadService.ExecutorThread[] threadList;
   private int minThreads = 1;
   private int currThreds = 0;
   private int idle = 0;
   private boolean terminated = false;
   private int priority;
   private ThreadGroup threadGroup = new ThreadGroup("ThreadService");
   private ClassLoader cloader;
   private static long counter = 0L;
   private int addedJobs = 1;
   private int doneJobs = 1;

   public ThreadService(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("The thread number should bigger than zero.");
      } else {
         this.minThreads = var1;
         this.threadList = new ThreadService.ExecutorThread[var1];
         this.priority = Thread.currentThread().getPriority();
         this.cloader = Thread.currentThread().getContextClassLoader();
      }
   }

   public void submitTask(Task var1) throws IllegalArgumentException {
      this.submitTask((Runnable)var1);
   }

   public void submitTask(Runnable var1) throws IllegalArgumentException {
      this.stateCheck();
      if (var1 == null) {
         throw new IllegalArgumentException("No task specified.");
      } else {
         synchronized(this.jobList) {
            this.jobList.add(this.jobList.size(), var1);
            this.jobList.notify();
         }

         this.createThread();
      }
   }

   public Runnable removeTask(Runnable var1) {
      this.stateCheck();
      Runnable var2 = null;
      synchronized(this.jobList) {
         int var4 = this.jobList.indexOf(var1);
         if (var4 >= 0) {
            var2 = (Runnable)this.jobList.remove(var4);
         }
      }

      if (var2 != null && var2 instanceof Task) {
         ((Task)var2).cancel();
      }

      return var2;
   }

   public void removeAll() {
      this.stateCheck();
      Object[] var1;
      synchronized(this.jobList) {
         var1 = this.jobList.toArray();
         this.jobList.clear();
      }

      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Object var4 = var1[var3];
         if (var4 != null && var4 instanceof Task) {
            ((Task)var4).cancel();
         }
      }

   }

   public void terminate() {
      if (!this.terminated) {
         this.terminated = true;
         synchronized(this.jobList) {
            this.jobList.notifyAll();
         }

         this.removeAll();

         for(int var1 = 0; var1 < this.currThreds; ++var1) {
            try {
               this.threadList[var1].interrupt();
            } catch (Exception var3) {
            }
         }

         this.threadList = null;
      }
   }

   private void stateCheck() throws IllegalStateException {
      if (this.terminated) {
         throw new IllegalStateException("The thread service has been terminated.");
      }
   }

   private void createThread() {
      if (this.idle < 1) {
         synchronized(this.threadList) {
            if (this.jobList.size() > 0 && this.currThreds < this.minThreads) {
               ThreadService.ExecutorThread var2 = new ThreadService.ExecutorThread();
               var2.start();
               this.threadList[this.currThreds++] = var2;
            }
         }
      }

   }

   private class ExecutorThread extends Thread {
      public ExecutorThread() {
         super(ThreadService.this.threadGroup, "ThreadService-" + ThreadService.counter++);
         this.setDaemon(true);
         this.setPriority(ThreadService.this.priority);
         this.setContextClassLoader(ThreadService.this.cloader);
         ThreadService.this.idle++;
      }

      public void run() {
         while(!ThreadService.this.terminated) {
            Runnable var1 = null;
            synchronized(ThreadService.this.jobList) {
               if (ThreadService.this.jobList.size() <= 0) {
                  try {
                     try {
                        ThreadService.this.jobList.wait();
                     } catch (InterruptedException var19) {
                     }
                     continue;
                  } finally {
                     ;
                  }
               }

               var1 = (Runnable)ThreadService.this.jobList.remove(0);
               if (ThreadService.this.jobList.size() > 0) {
                  ThreadService.this.jobList.notify();
               }
            }

            if (var1 != null) {
               try {
                  ThreadService.this.idle--;
                  var1.run();
               } catch (Exception var17) {
                  var17.printStackTrace();
               } finally {
                  ThreadService.this.idle++;
               }
            }

            this.setPriority(ThreadService.this.priority);
            Thread.interrupted();
            this.setContextClassLoader(ThreadService.this.cloader);
         }

      }
   }
}
