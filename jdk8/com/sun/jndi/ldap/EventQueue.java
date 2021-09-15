package com.sun.jndi.ldap;

import java.util.EventObject;
import java.util.Vector;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.ldap.UnsolicitedNotificationListener;

final class EventQueue implements Runnable {
   private static final boolean debug = false;
   private EventQueue.QueueElement head = null;
   private EventQueue.QueueElement tail = null;
   private Thread qThread;

   EventQueue() {
      this.qThread = Obj.helper.createThread(this);
      this.qThread.setDaemon(true);
      this.qThread.start();
   }

   synchronized void enqueue(EventObject var1, Vector<NamingListener> var2) {
      EventQueue.QueueElement var3 = new EventQueue.QueueElement(var1, var2);
      if (this.head == null) {
         this.head = var3;
         this.tail = var3;
      } else {
         var3.next = this.head;
         this.head.prev = var3;
         this.head = var3;
      }

      this.notify();
   }

   private synchronized EventQueue.QueueElement dequeue() throws InterruptedException {
      while(this.tail == null) {
         this.wait();
      }

      EventQueue.QueueElement var1 = this.tail;
      this.tail = var1.prev;
      if (this.tail == null) {
         this.head = null;
      } else {
         this.tail.next = null;
      }

      var1.prev = var1.next = null;
      return var1;
   }

   public void run() {
      while(true) {
         try {
            EventQueue.QueueElement var1;
            if ((var1 = this.dequeue()) != null) {
               EventObject var2 = var1.event;
               Vector var3 = var1.vector;

               for(int var4 = 0; var4 < var3.size(); ++var4) {
                  if (var2 instanceof NamingEvent) {
                     ((NamingEvent)var2).dispatch((NamingListener)var3.elementAt(var4));
                  } else if (var2 instanceof NamingExceptionEvent) {
                     ((NamingExceptionEvent)var2).dispatch((NamingListener)var3.elementAt(var4));
                  } else if (var2 instanceof UnsolicitedNotificationEvent) {
                     ((UnsolicitedNotificationEvent)var2).dispatch((UnsolicitedNotificationListener)var3.elementAt(var4));
                  }
               }

               var1 = null;
               var2 = null;
               var3 = null;
               continue;
            }
         } catch (InterruptedException var5) {
         }

         return;
      }
   }

   void stop() {
      if (this.qThread != null) {
         this.qThread.interrupt();
         this.qThread = null;
      }

   }

   private static class QueueElement {
      EventQueue.QueueElement next = null;
      EventQueue.QueueElement prev = null;
      EventObject event = null;
      Vector<NamingListener> vector = null;

      QueueElement(EventObject var1, Vector<NamingListener> var2) {
         this.event = var1;
         this.vector = var2;
      }
   }
}
