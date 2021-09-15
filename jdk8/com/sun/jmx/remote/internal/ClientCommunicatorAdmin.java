package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.InterruptedIOException;

public abstract class ClientCommunicatorAdmin {
   private static volatile long threadNo = 1L;
   private final ClientCommunicatorAdmin.Checker checker;
   private long period;
   private static final int CONNECTED = 0;
   private static final int RE_CONNECTING = 1;
   private static final int FAILED = 2;
   private static final int TERMINATED = 3;
   private int state = 0;
   private final int[] lock = new int[0];
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ClientCommunicatorAdmin");

   public ClientCommunicatorAdmin(long var1) {
      this.period = var1;
      if (var1 > 0L) {
         this.checker = new ClientCommunicatorAdmin.Checker();
         Thread var3 = new Thread(this.checker, "JMX client heartbeat " + ++threadNo);
         var3.setDaemon(true);
         var3.start();
      } else {
         this.checker = null;
      }

   }

   public void gotIOException(IOException var1) throws IOException {
      this.restart(var1);
   }

   protected abstract void checkConnection() throws IOException;

   protected abstract void doStart() throws IOException;

   protected abstract void doStop();

   public void terminate() {
      synchronized(this.lock) {
         if (this.state != 3) {
            this.state = 3;
            this.lock.notifyAll();
            if (this.checker != null) {
               this.checker.stop();
            }

         }
      }
   }

   private void restart(IOException var1) throws IOException {
      synchronized(this.lock) {
         if (this.state == 3) {
            throw new IOException("The client has been closed.");
         }

         if (this.state == 2) {
            throw var1;
         }

         if (this.state == 1) {
            while(this.state == 1) {
               try {
                  this.lock.wait();
               } catch (InterruptedException var9) {
                  InterruptedIOException var4 = new InterruptedIOException(var9.toString());
                  EnvHelp.initCause(var4, var9);
                  throw var4;
               }
            }

            if (this.state == 3) {
               throw new IOException("The client has been closed.");
            }

            if (this.state != 0) {
               throw var1;
            }

            return;
         }

         this.state = 1;
         this.lock.notifyAll();
      }

      try {
         this.doStart();
         synchronized(this.lock) {
            if (this.state == 3) {
               throw new IOException("The client has been closed.");
            } else {
               this.state = 0;
               this.lock.notifyAll();
            }
         }
      } catch (Exception var12) {
         logger.warning("restart", "Failed to restart: " + var12);
         logger.debug("restart", (Throwable)var12);
         synchronized(this.lock) {
            if (this.state == 3) {
               throw new IOException("The client has been closed.");
            }

            this.state = 2;
            this.lock.notifyAll();
         }

         try {
            this.doStop();
         } catch (Exception var8) {
         }

         this.terminate();
         throw var1;
      }
   }

   private class Checker implements Runnable {
      private Thread myThread;

      private Checker() {
      }

      public void run() {
         this.myThread = Thread.currentThread();

         while(ClientCommunicatorAdmin.this.state != 3 && !this.myThread.isInterrupted()) {
            try {
               Thread.sleep(ClientCommunicatorAdmin.this.period);
            } catch (InterruptedException var4) {
            }

            if (ClientCommunicatorAdmin.this.state == 3 || this.myThread.isInterrupted()) {
               break;
            }

            try {
               ClientCommunicatorAdmin.this.checkConnection();
            } catch (Exception var7) {
               synchronized(ClientCommunicatorAdmin.this.lock) {
                  if (ClientCommunicatorAdmin.this.state == 3 || this.myThread.isInterrupted()) {
                     break;
                  }
               }

               Exception var1 = (Exception)EnvHelp.getCause(var7);
               if (!(var1 instanceof IOException) || var1 instanceof InterruptedIOException) {
                  ClientCommunicatorAdmin.logger.warning("Checker-run", "Failed to check the connection: " + var1);
                  ClientCommunicatorAdmin.logger.debug("Checker-run", (Throwable)var1);
                  break;
               }

               try {
                  ClientCommunicatorAdmin.this.gotIOException((IOException)var1);
               } catch (Exception var5) {
                  ClientCommunicatorAdmin.logger.warning("Checker-run", "Failed to check connection: " + var1);
                  ClientCommunicatorAdmin.logger.warning("Checker-run", "stopping");
                  ClientCommunicatorAdmin.logger.debug("Checker-run", (Throwable)var1);
                  break;
               }
            }
         }

         if (ClientCommunicatorAdmin.logger.traceOn()) {
            ClientCommunicatorAdmin.logger.trace("Checker-run", "Finished.");
         }

      }

      private void stop() {
         if (this.myThread != null && this.myThread != Thread.currentThread()) {
            this.myThread.interrupt();
         }

      }

      // $FF: synthetic method
      Checker(Object var2) {
         this();
      }
   }
}
