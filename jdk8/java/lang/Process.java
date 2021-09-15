package java.lang;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public abstract class Process {
   public abstract OutputStream getOutputStream();

   public abstract InputStream getInputStream();

   public abstract InputStream getErrorStream();

   public abstract int waitFor() throws InterruptedException;

   public boolean waitFor(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = System.nanoTime();
      long var6 = var3.toNanos(var1);

      while(true) {
         try {
            this.exitValue();
            return true;
         } catch (IllegalThreadStateException var9) {
            if (var6 > 0L) {
               Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(var6) + 1L, 100L));
            }

            var6 = var3.toNanos(var1) - (System.nanoTime() - var4);
            if (var6 <= 0L) {
               return false;
            }
         }
      }
   }

   public abstract int exitValue();

   public abstract void destroy();

   public Process destroyForcibly() {
      this.destroy();
      return this;
   }

   public boolean isAlive() {
      try {
         this.exitValue();
         return false;
      } catch (IllegalThreadStateException var2) {
         return true;
      }
   }
}
