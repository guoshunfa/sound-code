package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import java.util.LinkedList;

public class WorkQueueImpl implements WorkQueue {
   private ThreadPool workerThreadPool;
   private LinkedList theWorkQueue;
   private long workItemsAdded;
   private long workItemsDequeued;
   private long totalTimeInQueue;
   private String name;
   private MonitoredObject workqueueMonitoredObject;

   public WorkQueueImpl() {
      this.theWorkQueue = new LinkedList();
      this.workItemsAdded = 0L;
      this.workItemsDequeued = 1L;
      this.totalTimeInQueue = 0L;
      this.name = "default-workqueue";
      this.initializeMonitoring();
   }

   public WorkQueueImpl(ThreadPool var1) {
      this(var1, "default-workqueue");
   }

   public WorkQueueImpl(ThreadPool var1, String var2) {
      this.theWorkQueue = new LinkedList();
      this.workItemsAdded = 0L;
      this.workItemsDequeued = 1L;
      this.totalTimeInQueue = 0L;
      this.workerThreadPool = var1;
      this.name = var2;
      this.initializeMonitoring();
   }

   private void initializeMonitoring() {
      this.workqueueMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.name, "Monitoring for a Work Queue");
      LongMonitoredAttributeBase var1 = new LongMonitoredAttributeBase("totalWorkItemsAdded", "Total number of Work items added to the Queue") {
         public Object getValue() {
            return new Long(WorkQueueImpl.this.totalWorkItemsAdded());
         }
      };
      this.workqueueMonitoredObject.addAttribute(var1);
      LongMonitoredAttributeBase var2 = new LongMonitoredAttributeBase("workItemsInQueue", "Number of Work items in the Queue to be processed") {
         public Object getValue() {
            return new Long((long)WorkQueueImpl.this.workItemsInQueue());
         }
      };
      this.workqueueMonitoredObject.addAttribute(var2);
      LongMonitoredAttributeBase var3 = new LongMonitoredAttributeBase("averageTimeInQueue", "Average time a work item waits in the work queue") {
         public Object getValue() {
            return new Long(WorkQueueImpl.this.averageTimeInQueue());
         }
      };
      this.workqueueMonitoredObject.addAttribute(var3);
   }

   MonitoredObject getMonitoredObject() {
      return this.workqueueMonitoredObject;
   }

   public synchronized void addWork(Work var1) {
      ++this.workItemsAdded;
      var1.setEnqueueTime(System.currentTimeMillis());
      this.theWorkQueue.addLast(var1);
      ((ThreadPoolImpl)this.workerThreadPool).notifyForAvailableWork(this);
   }

   synchronized Work requestWork(long var1) throws TimeoutException, InterruptedException {
      ((ThreadPoolImpl)this.workerThreadPool).incrementNumberOfAvailableThreads();
      Work var3;
      if (this.theWorkQueue.size() != 0) {
         var3 = (Work)this.theWorkQueue.removeFirst();
         this.totalTimeInQueue += System.currentTimeMillis() - var3.getEnqueueTime();
         ++this.workItemsDequeued;
         ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
         return var3;
      } else {
         try {
            long var4 = var1;
            long var6 = System.currentTimeMillis() + var1;

            do {
               this.wait(var4);
               if (this.theWorkQueue.size() != 0) {
                  var3 = (Work)this.theWorkQueue.removeFirst();
                  this.totalTimeInQueue += System.currentTimeMillis() - var3.getEnqueueTime();
                  ++this.workItemsDequeued;
                  ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
                  return var3;
               }

               var4 = var6 - System.currentTimeMillis();
            } while(var4 > 0L);

            ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
            throw new TimeoutException();
         } catch (InterruptedException var8) {
            ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
            throw var8;
         }
      }
   }

   public void setThreadPool(ThreadPool var1) {
      this.workerThreadPool = var1;
   }

   public ThreadPool getThreadPool() {
      return this.workerThreadPool;
   }

   public long totalWorkItemsAdded() {
      return this.workItemsAdded;
   }

   public int workItemsInQueue() {
      return this.theWorkQueue.size();
   }

   public synchronized long averageTimeInQueue() {
      return this.totalTimeInQueue / this.workItemsDequeued;
   }

   public String getName() {
      return this.name;
   }
}
