package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;

public abstract class ServerCommunicatorAdmin {
   private long timestamp;
   private final int[] lock = new int[0];
   private int currentJobs = 0;
   private long timeout;
   private boolean terminated = false;
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ServerCommunicatorAdmin");
   private static final ClassLogger timelogger = new ClassLogger("javax.management.remote.timeout", "ServerCommunicatorAdmin");

   public ServerCommunicatorAdmin(long var1) {
      if (logger.traceOn()) {
         logger.trace("Constructor", "Creates a new ServerCommunicatorAdmin object with the timeout " + var1);
      }

      this.timeout = var1;
      this.timestamp = 0L;
      if (var1 < Long.MAX_VALUE) {
         ServerCommunicatorAdmin.Timeout var3 = new ServerCommunicatorAdmin.Timeout();
         Thread var4 = new Thread(var3);
         var4.setName("JMX server connection timeout " + var4.getId());
         var4.setDaemon(true);
         var4.start();
      }

   }

   public boolean reqIncoming() {
      if (logger.traceOn()) {
         logger.trace("reqIncoming", "Receive a new request.");
      }

      synchronized(this.lock) {
         if (this.terminated) {
            logger.warning("reqIncoming", "The server has decided to close this client connection.");
         }

         ++this.currentJobs;
         return this.terminated;
      }
   }

   public boolean rspOutgoing() {
      if (logger.traceOn()) {
         logger.trace("reqIncoming", "Finish a request.");
      }

      synchronized(this.lock) {
         if (--this.currentJobs == 0) {
            this.timestamp = System.currentTimeMillis();
            this.logtime("Admin: Timestamp=", this.timestamp);
            this.lock.notify();
         }

         return this.terminated;
      }
   }

   protected abstract void doStop();

   public void terminate() {
      if (logger.traceOn()) {
         logger.trace("terminate", "terminate the ServerCommunicatorAdmin object.");
      }

      synchronized(this.lock) {
         if (!this.terminated) {
            this.terminated = true;
            this.lock.notify();
         }
      }
   }

   private void logtime(String var1, long var2) {
      timelogger.trace("synchro", var1 + var2);
   }

   private class Timeout implements Runnable {
      private Timeout() {
      }

      public void run() {
         boolean var1 = false;
         synchronized(ServerCommunicatorAdmin.this.lock) {
            if (ServerCommunicatorAdmin.this.timestamp == 0L) {
               ServerCommunicatorAdmin.this.timestamp = System.currentTimeMillis();
            }

            ServerCommunicatorAdmin.this.logtime("Admin: timeout=", ServerCommunicatorAdmin.this.timeout);
            ServerCommunicatorAdmin.this.logtime("Admin: Timestamp=", ServerCommunicatorAdmin.this.timestamp);

            while(!ServerCommunicatorAdmin.this.terminated) {
               try {
                  for(; !ServerCommunicatorAdmin.this.terminated && ServerCommunicatorAdmin.this.currentJobs != 0; ServerCommunicatorAdmin.this.lock.wait()) {
                     if (ServerCommunicatorAdmin.logger.traceOn()) {
                        ServerCommunicatorAdmin.logger.trace("Timeout-run", "Waiting without timeout.");
                     }
                  }

                  if (ServerCommunicatorAdmin.this.terminated) {
                     return;
                  }

                  long var3 = ServerCommunicatorAdmin.this.timeout - (System.currentTimeMillis() - ServerCommunicatorAdmin.this.timestamp);
                  ServerCommunicatorAdmin.this.logtime("Admin: remaining timeout=", var3);
                  if (var3 > 0L) {
                     if (ServerCommunicatorAdmin.logger.traceOn()) {
                        ServerCommunicatorAdmin.logger.trace("Timeout-run", "Waiting with timeout: " + var3 + " ms remaining");
                     }

                     ServerCommunicatorAdmin.this.lock.wait(var3);
                  }

                  if (ServerCommunicatorAdmin.this.currentJobs <= 0) {
                     long var5 = System.currentTimeMillis() - ServerCommunicatorAdmin.this.timestamp;
                     ServerCommunicatorAdmin.this.logtime("Admin: elapsed=", var5);
                     if (!ServerCommunicatorAdmin.this.terminated && var5 > ServerCommunicatorAdmin.this.timeout) {
                        if (ServerCommunicatorAdmin.logger.traceOn()) {
                           ServerCommunicatorAdmin.logger.trace("Timeout-run", "timeout elapsed");
                        }

                        ServerCommunicatorAdmin.this.logtime("Admin: timeout elapsed! " + var5 + ">", ServerCommunicatorAdmin.this.timeout);
                        ServerCommunicatorAdmin.this.terminated = true;
                        var1 = true;
                        break;
                     }
                  }
               } catch (InterruptedException var8) {
                  ServerCommunicatorAdmin.logger.warning("Timeout-run", "Unexpected Exception: " + var8);
                  ServerCommunicatorAdmin.logger.debug("Timeout-run", (Throwable)var8);
                  return;
               }
            }
         }

         if (var1) {
            if (ServerCommunicatorAdmin.logger.traceOn()) {
               ServerCommunicatorAdmin.logger.trace("Timeout-run", "Call the doStop.");
            }

            ServerCommunicatorAdmin.this.doStop();
         }

      }

      // $FF: synthetic method
      Timeout(Object var2) {
         this();
      }
   }
}
