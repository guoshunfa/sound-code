package com.sun.corba.se.impl.activation;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

public class ProcessMonitorThread extends Thread {
   private HashMap serverTable;
   private int sleepTime;
   private static ProcessMonitorThread instance = null;

   private ProcessMonitorThread(HashMap var1, int var2) {
      this.serverTable = var1;
      this.sleepTime = var2;
   }

   public void run() {
      while(true) {
         try {
            Thread.sleep((long)this.sleepTime);
         } catch (InterruptedException var6) {
            break;
         }

         Iterator var1;
         synchronized(this.serverTable) {
            var1 = this.serverTable.values().iterator();
         }

         try {
            this.checkServerHealth(var1);
         } catch (ConcurrentModificationException var5) {
            break;
         }
      }

   }

   private void checkServerHealth(Iterator var1) {
      if (var1 != null) {
         while(var1.hasNext()) {
            ServerTableEntry var2 = (ServerTableEntry)var1.next();
            var2.checkProcessHealth();
         }

      }
   }

   static void start(HashMap var0) {
      int var1 = 1000;
      String var2 = System.getProperties().getProperty("com.sun.CORBA.activation.ServerPollingTime");
      if (var2 != null) {
         try {
            var1 = Integer.parseInt(var2);
         } catch (Exception var4) {
         }
      }

      instance = new ProcessMonitorThread(var0, var1);
      instance.setDaemon(true);
      instance.start();
   }

   static void interruptThread() {
      instance.interrupt();
   }
}
