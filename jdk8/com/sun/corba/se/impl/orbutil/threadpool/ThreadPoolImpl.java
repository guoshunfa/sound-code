package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import java.io.Closeable;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPoolImpl implements ThreadPool {
   private static AtomicInteger threadCounter = new AtomicInteger(0);
   private static final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");
   private WorkQueue workQueue;
   private int availableWorkerThreads;
   private int currentThreadCount;
   private int minWorkerThreads;
   private int maxWorkerThreads;
   private long inactivityTimeout;
   private boolean boundedThreadPool;
   private AtomicLong processedCount;
   private AtomicLong totalTimeTaken;
   private String name;
   private MonitoredObject threadpoolMonitoredObject;
   private ThreadGroup threadGroup;
   Object workersLock;
   List<ThreadPoolImpl.WorkerThread> workers;

   public ThreadPoolImpl(ThreadGroup var1, String var2) {
      this.availableWorkerThreads = 0;
      this.currentThreadCount = 0;
      this.minWorkerThreads = 0;
      this.maxWorkerThreads = 0;
      this.boundedThreadPool = false;
      this.processedCount = new AtomicLong(1L);
      this.totalTimeTaken = new AtomicLong(0L);
      this.workersLock = new Object();
      this.workers = new ArrayList();
      this.inactivityTimeout = 120000L;
      this.maxWorkerThreads = Integer.MAX_VALUE;
      this.workQueue = new WorkQueueImpl(this);
      this.threadGroup = var1;
      this.name = var2;
      this.initializeMonitoring();
   }

   public ThreadPoolImpl(String var1) {
      this(Thread.currentThread().getThreadGroup(), var1);
   }

   public ThreadPoolImpl(int var1, int var2, long var3, String var5) {
      this.availableWorkerThreads = 0;
      this.currentThreadCount = 0;
      this.minWorkerThreads = 0;
      this.maxWorkerThreads = 0;
      this.boundedThreadPool = false;
      this.processedCount = new AtomicLong(1L);
      this.totalTimeTaken = new AtomicLong(0L);
      this.workersLock = new Object();
      this.workers = new ArrayList();
      this.minWorkerThreads = var1;
      this.maxWorkerThreads = var2;
      this.inactivityTimeout = var3;
      this.boundedThreadPool = true;
      this.workQueue = new WorkQueueImpl(this);
      this.name = var5;

      for(int var6 = 0; var6 < this.minWorkerThreads; ++var6) {
         this.createWorkerThread();
      }

      this.initializeMonitoring();
   }

   public void close() throws IOException {
      ArrayList var1 = null;
      synchronized(this.workersLock) {
         var1 = new ArrayList(this.workers);
      }

      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ThreadPoolImpl.WorkerThread var3 = (ThreadPoolImpl.WorkerThread)var2.next();
         var3.close();

         while(var3.getState() != Thread.State.TERMINATED) {
            try {
               var3.join();
            } catch (InterruptedException var5) {
               wrapper.interruptedJoinCallWhileClosingThreadPool((Throwable)var5, var3, this);
            }
         }
      }

      this.threadGroup = null;
   }

   private void initializeMonitoring() {
      MonitoredObject var1 = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", (String)null).getRootMonitoredObject();
      MonitoredObject var2 = var1.getChild("threadpool");
      if (var2 == null) {
         var2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("threadpool", "Monitoring for all ThreadPool instances");
         var1.addChild(var2);
      }

      this.threadpoolMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.name, "Monitoring for a ThreadPool");
      var2.addChild(this.threadpoolMonitoredObject);
      LongMonitoredAttributeBase var3 = new LongMonitoredAttributeBase("currentNumberOfThreads", "Current number of total threads in the ThreadPool") {
         public Object getValue() {
            return new Long((long)ThreadPoolImpl.this.currentNumberOfThreads());
         }
      };
      this.threadpoolMonitoredObject.addAttribute(var3);
      LongMonitoredAttributeBase var4 = new LongMonitoredAttributeBase("numberOfAvailableThreads", "Current number of total threads in the ThreadPool") {
         public Object getValue() {
            return new Long((long)ThreadPoolImpl.this.numberOfAvailableThreads());
         }
      };
      this.threadpoolMonitoredObject.addAttribute(var4);
      LongMonitoredAttributeBase var5 = new LongMonitoredAttributeBase("numberOfBusyThreads", "Number of busy threads in the ThreadPool") {
         public Object getValue() {
            return new Long((long)ThreadPoolImpl.this.numberOfBusyThreads());
         }
      };
      this.threadpoolMonitoredObject.addAttribute(var5);
      LongMonitoredAttributeBase var6 = new LongMonitoredAttributeBase("averageWorkCompletionTime", "Average elapsed time taken to complete a work item by the ThreadPool") {
         public Object getValue() {
            return new Long(ThreadPoolImpl.this.averageWorkCompletionTime());
         }
      };
      this.threadpoolMonitoredObject.addAttribute(var6);
      LongMonitoredAttributeBase var7 = new LongMonitoredAttributeBase("currentProcessedCount", "Number of Work items processed by the ThreadPool") {
         public Object getValue() {
            return new Long(ThreadPoolImpl.this.currentProcessedCount());
         }
      };
      this.threadpoolMonitoredObject.addAttribute(var7);
      this.threadpoolMonitoredObject.addChild(((WorkQueueImpl)this.workQueue).getMonitoredObject());
   }

   MonitoredObject getMonitoredObject() {
      return this.threadpoolMonitoredObject;
   }

   public WorkQueue getAnyWorkQueue() {
      return this.workQueue;
   }

   public WorkQueue getWorkQueue(int var1) throws NoSuchWorkQueueException {
      if (var1 != 0) {
         throw new NoSuchWorkQueueException();
      } else {
         return this.workQueue;
      }
   }

   void notifyForAvailableWork(WorkQueue var1) {
      synchronized(var1) {
         if (this.availableWorkerThreads < var1.workItemsInQueue()) {
            this.createWorkerThread();
         } else {
            var1.notify();
         }

      }
   }

   private Thread createWorkerThreadHelper(String var1) {
      ThreadPoolImpl.WorkerThread var2 = new ThreadPoolImpl.WorkerThread(this.threadGroup, var1);
      synchronized(this.workersLock) {
         this.workers.add(var2);
      }

      var2.setDaemon(true);
      wrapper.workerThreadCreated(var2, var2.getContextClassLoader());
      var2.start();
      return null;
   }

   void createWorkerThread() {
      final String var1 = this.getName();
      synchronized(this.workQueue) {
         try {
            if (System.getSecurityManager() == null) {
               this.createWorkerThreadHelper(var1);
            } else {
               AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     return ThreadPoolImpl.this.createWorkerThreadHelper(var1);
                  }
               });
            }
         } catch (Throwable var9) {
            this.decrementCurrentNumberOfThreads();
            wrapper.workerThreadCreationFailure(var9);
         } finally {
            this.incrementCurrentNumberOfThreads();
         }

      }
   }

   public int minimumNumberOfThreads() {
      return this.minWorkerThreads;
   }

   public int maximumNumberOfThreads() {
      return this.maxWorkerThreads;
   }

   public long idleTimeoutForThreads() {
      return this.inactivityTimeout;
   }

   public int currentNumberOfThreads() {
      synchronized(this.workQueue) {
         return this.currentThreadCount;
      }
   }

   void decrementCurrentNumberOfThreads() {
      synchronized(this.workQueue) {
         --this.currentThreadCount;
      }
   }

   void incrementCurrentNumberOfThreads() {
      synchronized(this.workQueue) {
         ++this.currentThreadCount;
      }
   }

   public int numberOfAvailableThreads() {
      synchronized(this.workQueue) {
         return this.availableWorkerThreads;
      }
   }

   public int numberOfBusyThreads() {
      synchronized(this.workQueue) {
         return this.currentThreadCount - this.availableWorkerThreads;
      }
   }

   public long averageWorkCompletionTime() {
      synchronized(this.workQueue) {
         return this.totalTimeTaken.get() / this.processedCount.get();
      }
   }

   public long currentProcessedCount() {
      synchronized(this.workQueue) {
         return this.processedCount.get();
      }
   }

   public String getName() {
      return this.name;
   }

   public int numberOfWorkQueues() {
      return 1;
   }

   private static synchronized int getUniqueThreadId() {
      return threadCounter.incrementAndGet();
   }

   void decrementNumberOfAvailableThreads() {
      synchronized(this.workQueue) {
         --this.availableWorkerThreads;
      }
   }

   void incrementNumberOfAvailableThreads() {
      synchronized(this.workQueue) {
         ++this.availableWorkerThreads;
      }
   }

   private class WorkerThread extends Thread implements Closeable {
      private Work currentWork;
      private int threadId = 0;
      private volatile boolean closeCalled = false;
      private String threadPoolName;
      private StringBuffer workerThreadName = new StringBuffer();

      WorkerThread(ThreadGroup var2, String var3) {
         super(var2, "Idle");
         this.threadId = ThreadPoolImpl.getUniqueThreadId();
         this.threadPoolName = var3;
         this.setName(this.composeWorkerThreadName(var3, "Idle"));
      }

      public synchronized void close() {
         this.closeCalled = true;
         this.interrupt();
      }

      private void resetClassLoader() {
      }

      private void performWork() {
         long var1 = System.currentTimeMillis();

         try {
            this.currentWork.doWork();
         } catch (Throwable var5) {
            ThreadPoolImpl.wrapper.workerThreadDoWorkThrowable(this, var5);
         }

         long var3 = System.currentTimeMillis() - var1;
         ThreadPoolImpl.this.totalTimeTaken.addAndGet(var3);
         ThreadPoolImpl.this.processedCount.incrementAndGet();
      }

      public void run() {
         while(true) {
            boolean var14 = false;

            label116: {
               try {
                  var14 = true;
                  if (!this.closeCalled) {
                     try {
                        this.currentWork = ((WorkQueueImpl)ThreadPoolImpl.this.workQueue).requestWork(ThreadPoolImpl.this.inactivityTimeout);
                        if (this.currentWork == null) {
                           continue;
                        }
                     } catch (InterruptedException var17) {
                        ThreadPoolImpl.wrapper.workQueueThreadInterrupted((Throwable)var17, this.getName(), this.closeCalled);
                        continue;
                     } catch (Throwable var18) {
                        ThreadPoolImpl.wrapper.workerThreadThrowableFromRequestWork(this, var18, ThreadPoolImpl.this.workQueue.getName());
                        continue;
                     }

                     this.performWork();
                     this.currentWork = null;
                     this.resetClassLoader();
                     continue;
                  }

                  var14 = false;
                  break label116;
               } catch (Throwable var20) {
                  ThreadPoolImpl.wrapper.workerThreadCaughtUnexpectedThrowable(this, var20);
                  var14 = false;
               } finally {
                  if (var14) {
                     synchronized(ThreadPoolImpl.this.workersLock) {
                        ThreadPoolImpl.this.workers.remove(this);
                     }
                  }
               }

               synchronized(ThreadPoolImpl.this.workersLock) {
                  ThreadPoolImpl.this.workers.remove(this);
                  return;
               }
            }

            synchronized(ThreadPoolImpl.this.workersLock) {
               ThreadPoolImpl.this.workers.remove(this);
            }

            return;
         }
      }

      private String composeWorkerThreadName(String var1, String var2) {
         this.workerThreadName.setLength(0);
         this.workerThreadName.append("p: ").append(var1);
         this.workerThreadName.append("; w: ").append(var2);
         return this.workerThreadName.toString();
      }
   }
}
