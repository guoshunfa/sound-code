package sun.net.www.http;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ProgressSource;
import sun.net.www.MeteredStream;

public class KeepAliveStream extends MeteredStream implements Hurryable {
   HttpClient hc;
   boolean hurried;
   protected boolean queuedForCleanup = false;
   private static final KeepAliveStreamCleaner queue = new KeepAliveStreamCleaner();
   private static Thread cleanerThread;

   public KeepAliveStream(InputStream var1, ProgressSource var2, long var3, HttpClient var5) {
      super(var1, var2, var3);
      this.hc = var5;
   }

   public void close() throws IOException {
      if (!this.closed) {
         if (!this.queuedForCleanup) {
            try {
               if (this.expected > this.count) {
                  long var1 = this.expected - this.count;
                  if (var1 <= (long)this.available()) {
                     while((var1 = this.expected - this.count) > 0L && this.skip(Math.min(var1, (long)this.available())) > 0L) {
                     }
                  } else if (this.expected <= (long)KeepAliveStreamCleaner.MAX_DATA_REMAINING && !this.hurried) {
                     queueForCleanup(new KeepAliveCleanerEntry(this, this.hc));
                  } else {
                     this.hc.closeServer();
                  }
               }

               if (!this.closed && !this.hurried && !this.queuedForCleanup) {
                  this.hc.finished();
               }
            } finally {
               if (this.pi != null) {
                  this.pi.finishTracking();
               }

               if (!this.queuedForCleanup) {
                  this.in = null;
                  this.hc = null;
                  this.closed = true;
               }

            }

         }
      }
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int var1) {
   }

   public void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }

   public synchronized boolean hurry() {
      try {
         if (!this.closed && this.count < this.expected) {
            if ((long)this.in.available() < this.expected - this.count) {
               return false;
            } else {
               int var1 = (int)(this.expected - this.count);
               byte[] var2 = new byte[var1];
               DataInputStream var3 = new DataInputStream(this.in);
               var3.readFully(var2);
               this.in = new ByteArrayInputStream(var2);
               this.hurried = true;
               return true;
            }
         } else {
            return false;
         }
      } catch (IOException var4) {
         return false;
      }
   }

   private static void queueForCleanup(KeepAliveCleanerEntry var0) {
      synchronized(queue) {
         if (!var0.getQueuedForCleanup()) {
            if (!queue.offer(var0)) {
               var0.getHttpClient().closeServer();
               return;
            }

            var0.setQueuedForCleanup();
            queue.notifyAll();
         }

         boolean var2 = cleanerThread == null;
         if (!var2 && !cleanerThread.isAlive()) {
            var2 = true;
         }

         if (var2) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  ThreadGroup var1 = Thread.currentThread().getThreadGroup();

                  for(ThreadGroup var2 = null; (var2 = var1.getParent()) != null; var1 = var2) {
                  }

                  KeepAliveStream.cleanerThread = new Thread(var1, KeepAliveStream.queue, "Keep-Alive-SocketCleaner");
                  KeepAliveStream.cleanerThread.setDaemon(true);
                  KeepAliveStream.cleanerThread.setPriority(8);
                  KeepAliveStream.cleanerThread.setContextClassLoader((ClassLoader)null);
                  KeepAliveStream.cleanerThread.start();
                  return null;
               }
            });
         }

      }
   }

   protected long remainingToRead() {
      return this.expected - this.count;
   }

   protected void setClosed() {
      this.in = null;
      this.hc = null;
      this.closed = true;
   }
}
