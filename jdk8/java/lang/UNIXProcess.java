package java.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

final class UNIXProcess extends Process {
   private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
   private final int pid;
   private int exitcode;
   private boolean hasExited;
   private OutputStream stdin;
   private InputStream stdout;
   private InputStream stderr;
   private UNIXProcess.DeferredCloseInputStream stdout_inner_stream;
   private static final UNIXProcess.Platform platform = UNIXProcess.Platform.get();
   private static final UNIXProcess.LaunchMechanism launchMechanism;
   private static final byte[] helperpath;
   private static final Executor processReaperExecutor;

   private static byte[] toCString(String var0) {
      if (var0 == null) {
         return null;
      } else {
         byte[] var1 = var0.getBytes();
         byte[] var2 = new byte[var1.length + 1];
         System.arraycopy(var1, 0, var2, 0, var1.length);
         var2[var2.length - 1] = 0;
         return var2;
      }
   }

   private native int waitForProcessExit(int var1);

   private native int forkAndExec(int var1, byte[] var2, byte[] var3, byte[] var4, int var5, byte[] var6, int var7, byte[] var8, int[] var9, boolean var10) throws IOException;

   UNIXProcess(byte[] var1, byte[] var2, int var3, byte[] var4, int var5, byte[] var6, int[] var7, boolean var8) throws IOException {
      this.pid = this.forkAndExec(launchMechanism.ordinal() + 1, helperpath, var1, var2, var3, var4, var5, var6, var7, var8);

      try {
         AccessController.doPrivileged(() -> {
            this.initStreams(var7);
            return null;
         });
      } catch (PrivilegedActionException var10) {
         throw (IOException)var10.getException();
      }
   }

   static FileDescriptor newFileDescriptor(int var0) {
      FileDescriptor var1 = new FileDescriptor();
      fdAccess.set(var1, var0);
      return var1;
   }

   void initStreams(int[] var1) throws IOException {
      switch(platform) {
      case SOLARIS:
         this.stdin = (OutputStream)(var1[0] == -1 ? ProcessBuilder.NullOutputStream.INSTANCE : new BufferedOutputStream(new FileOutputStream(newFileDescriptor(var1[0]))));
         this.stdout = (InputStream)(var1[1] == -1 ? ProcessBuilder.NullInputStream.INSTANCE : new BufferedInputStream(this.stdout_inner_stream = new UNIXProcess.DeferredCloseInputStream(newFileDescriptor(var1[1]))));
         this.stderr = (InputStream)(var1[2] == -1 ? ProcessBuilder.NullInputStream.INSTANCE : new UNIXProcess.DeferredCloseInputStream(newFileDescriptor(var1[2])));
         processReaperExecutor.execute(() -> {
            int var1 = this.waitForProcessExit(this.pid);
            synchronized(this) {
               this.exitcode = var1;
               this.hasExited = true;
               this.notifyAll();
            }
         });
         break;
      case LINUX:
      case BSD:
         this.stdin = (OutputStream)(var1[0] == -1 ? ProcessBuilder.NullOutputStream.INSTANCE : new UNIXProcess.ProcessPipeOutputStream(var1[0]));
         this.stdout = (InputStream)(var1[1] == -1 ? ProcessBuilder.NullInputStream.INSTANCE : new UNIXProcess.ProcessPipeInputStream(var1[1]));
         this.stderr = (InputStream)(var1[2] == -1 ? ProcessBuilder.NullInputStream.INSTANCE : new UNIXProcess.ProcessPipeInputStream(var1[2]));
         processReaperExecutor.execute(() -> {
            int var1 = this.waitForProcessExit(this.pid);
            synchronized(this) {
               this.exitcode = var1;
               this.hasExited = true;
               this.notifyAll();
            }

            if (this.stdout instanceof UNIXProcess.ProcessPipeInputStream) {
               ((UNIXProcess.ProcessPipeInputStream)this.stdout).processExited();
            }

            if (this.stderr instanceof UNIXProcess.ProcessPipeInputStream) {
               ((UNIXProcess.ProcessPipeInputStream)this.stderr).processExited();
            }

            if (this.stdin instanceof UNIXProcess.ProcessPipeOutputStream) {
               ((UNIXProcess.ProcessPipeOutputStream)this.stdin).processExited();
            }

         });
         break;
      case AIX:
         this.stdin = (OutputStream)(var1[0] == -1 ? ProcessBuilder.NullOutputStream.INSTANCE : new UNIXProcess.ProcessPipeOutputStream(var1[0]));
         this.stdout = (InputStream)(var1[1] == -1 ? ProcessBuilder.NullInputStream.INSTANCE : new UNIXProcess.DeferredCloseProcessPipeInputStream(var1[1]));
         this.stderr = (InputStream)(var1[2] == -1 ? ProcessBuilder.NullInputStream.INSTANCE : new UNIXProcess.DeferredCloseProcessPipeInputStream(var1[2]));
         processReaperExecutor.execute(() -> {
            int var1 = this.waitForProcessExit(this.pid);
            synchronized(this) {
               this.exitcode = var1;
               this.hasExited = true;
               this.notifyAll();
            }

            if (this.stdout instanceof UNIXProcess.DeferredCloseProcessPipeInputStream) {
               ((UNIXProcess.DeferredCloseProcessPipeInputStream)this.stdout).processExited();
            }

            if (this.stderr instanceof UNIXProcess.DeferredCloseProcessPipeInputStream) {
               ((UNIXProcess.DeferredCloseProcessPipeInputStream)this.stderr).processExited();
            }

            if (this.stdin instanceof UNIXProcess.ProcessPipeOutputStream) {
               ((UNIXProcess.ProcessPipeOutputStream)this.stdin).processExited();
            }

         });
         break;
      default:
         throw new AssertionError("Unsupported platform: " + platform);
      }

   }

   public OutputStream getOutputStream() {
      return this.stdin;
   }

   public InputStream getInputStream() {
      return this.stdout;
   }

   public InputStream getErrorStream() {
      return this.stderr;
   }

   public synchronized int waitFor() throws InterruptedException {
      while(!this.hasExited) {
         this.wait();
      }

      return this.exitcode;
   }

   public synchronized boolean waitFor(long var1, TimeUnit var3) throws InterruptedException {
      if (this.hasExited) {
         return true;
      } else if (var1 <= 0L) {
         return false;
      } else {
         long var4 = var3.toNanos(var1);
         long var6 = System.nanoTime() + var4;

         do {
            this.wait(TimeUnit.NANOSECONDS.toMillis(var4 + 999999L));
            if (this.hasExited) {
               return true;
            }

            var4 = var6 - System.nanoTime();
         } while(var4 > 0L);

         return this.hasExited;
      }
   }

   public synchronized int exitValue() {
      if (!this.hasExited) {
         throw new IllegalThreadStateException("process hasn't exited");
      } else {
         return this.exitcode;
      }
   }

   private static native void destroyProcess(int var0, boolean var1);

   private void destroy(boolean var1) {
      switch(platform) {
      case SOLARIS:
         synchronized(this) {
            if (!this.hasExited) {
               destroyProcess(this.pid, var1);
            }

            try {
               this.stdin.close();
               if (this.stdout_inner_stream != null) {
                  this.stdout_inner_stream.closeDeferred(this.stdout);
               }

               if (this.stderr instanceof UNIXProcess.DeferredCloseInputStream) {
                  ((UNIXProcess.DeferredCloseInputStream)this.stderr).closeDeferred(this.stderr);
               }
            } catch (IOException var5) {
            }
            break;
         }
      case LINUX:
      case AIX:
      case BSD:
         synchronized(this) {
            if (!this.hasExited) {
               destroyProcess(this.pid, var1);
            }
         }

         try {
            this.stdin.close();
         } catch (IOException var9) {
         }

         try {
            this.stdout.close();
         } catch (IOException var8) {
         }

         try {
            this.stderr.close();
         } catch (IOException var7) {
         }
         break;
      default:
         throw new AssertionError("Unsupported platform: " + platform);
      }

   }

   public void destroy() {
      this.destroy(false);
   }

   public Process destroyForcibly() {
      this.destroy(true);
      return this;
   }

   public synchronized boolean isAlive() {
      return !this.hasExited;
   }

   private static native void init();

   static {
      launchMechanism = platform.launchMechanism();
      helperpath = toCString(platform.helperPath());
      processReaperExecutor = (Executor)AccessController.doPrivileged(() -> {
         ThreadGroup var0;
         for(var0 = Thread.currentThread().getThreadGroup(); var0.getParent() != null; var0 = var0.getParent()) {
         }

         ThreadFactory var2 = (var1) -> {
            long var2 = Boolean.getBoolean("jdk.lang.processReaperUseDefaultStackSize") ? 0L : 32768L;
            Thread var4 = new Thread(var0, var1, "process reaper", var2);
            var4.setDaemon(true);
            var4.setPriority(10);
            return var4;
         };
         return Executors.newCachedThreadPool(var2);
      });
      init();
   }

   private static class DeferredCloseProcessPipeInputStream extends BufferedInputStream {
      private final Object closeLock = new Object();
      private int useCount = 0;
      private boolean closePending = false;

      DeferredCloseProcessPipeInputStream(int var1) {
         super(new FileInputStream(UNIXProcess.newFileDescriptor(var1)));
      }

      private InputStream drainInputStream(InputStream var1) throws IOException {
         int var2 = 0;
         byte[] var4 = null;
         int var3;
         synchronized(this.closeLock) {
            if (this.buf == null) {
               return null;
            }

            var3 = var1.available();
         }

         while(var3 > 0) {
            var4 = var4 == null ? new byte[var3] : Arrays.copyOf(var4, var2 + var3);
            synchronized(this.closeLock) {
               if (this.buf == null) {
                  return null;
               }

               var2 += var1.read(var4, var2, var3);
               var3 = var1.available();
            }
         }

         return (InputStream)(var4 == null ? ProcessBuilder.NullInputStream.INSTANCE : new ByteArrayInputStream(var2 == var4.length ? var4 : Arrays.copyOf(var4, var2)));
      }

      synchronized void processExited() {
         try {
            InputStream var1 = this.in;
            if (var1 != null) {
               InputStream var2 = this.drainInputStream(var1);
               var1.close();
               this.in = var2;
            }
         } catch (IOException var3) {
         }

      }

      private void raise() {
         synchronized(this.closeLock) {
            ++this.useCount;
         }
      }

      private void lower() throws IOException {
         synchronized(this.closeLock) {
            --this.useCount;
            if (this.useCount == 0 && this.closePending) {
               this.closePending = false;
               super.close();
            }

         }
      }

      public int read() throws IOException {
         this.raise();

         int var1;
         try {
            var1 = super.read();
         } finally {
            this.lower();
         }

         return var1;
      }

      public int read(byte[] var1) throws IOException {
         this.raise();

         int var2;
         try {
            var2 = super.read(var1);
         } finally {
            this.lower();
         }

         return var2;
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         this.raise();

         int var4;
         try {
            var4 = super.read(var1, var2, var3);
         } finally {
            this.lower();
         }

         return var4;
      }

      public long skip(long var1) throws IOException {
         this.raise();

         long var3;
         try {
            var3 = super.skip(var1);
         } finally {
            this.lower();
         }

         return var3;
      }

      public int available() throws IOException {
         this.raise();

         int var1;
         try {
            var1 = super.available();
         } finally {
            this.lower();
         }

         return var1;
      }

      public void close() throws IOException {
         synchronized(this.closeLock) {
            if (this.useCount == 0) {
               super.close();
            } else {
               this.closePending = true;
            }

         }
      }
   }

   private static class DeferredCloseInputStream extends FileInputStream {
      private Object lock = new Object();
      private boolean closePending = false;
      private int useCount = 0;
      private InputStream streamToClose;

      DeferredCloseInputStream(FileDescriptor var1) {
         super(var1);
      }

      private void raise() {
         synchronized(this.lock) {
            ++this.useCount;
         }
      }

      private void lower() throws IOException {
         synchronized(this.lock) {
            --this.useCount;
            if (this.useCount == 0 && this.closePending) {
               this.streamToClose.close();
            }

         }
      }

      private void closeDeferred(InputStream var1) throws IOException {
         synchronized(this.lock) {
            if (this.useCount == 0) {
               var1.close();
            } else {
               this.closePending = true;
               this.streamToClose = var1;
            }

         }
      }

      public void close() throws IOException {
         synchronized(this.lock) {
            this.useCount = 0;
            this.closePending = false;
         }

         super.close();
      }

      public int read() throws IOException {
         this.raise();

         int var1;
         try {
            var1 = super.read();
         } finally {
            this.lower();
         }

         return var1;
      }

      public int read(byte[] var1) throws IOException {
         this.raise();

         int var2;
         try {
            var2 = super.read(var1);
         } finally {
            this.lower();
         }

         return var2;
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         this.raise();

         int var4;
         try {
            var4 = super.read(var1, var2, var3);
         } finally {
            this.lower();
         }

         return var4;
      }

      public long skip(long var1) throws IOException {
         this.raise();

         long var3;
         try {
            var3 = super.skip(var1);
         } finally {
            this.lower();
         }

         return var3;
      }

      public int available() throws IOException {
         this.raise();

         int var1;
         try {
            var1 = super.available();
         } finally {
            this.lower();
         }

         return var1;
      }
   }

   private static class ProcessPipeOutputStream extends BufferedOutputStream {
      ProcessPipeOutputStream(int var1) {
         super(new FileOutputStream(UNIXProcess.newFileDescriptor(var1)));
      }

      synchronized void processExited() {
         OutputStream var1 = this.out;
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var3) {
            }

            this.out = ProcessBuilder.NullOutputStream.INSTANCE;
         }

      }
   }

   private static class ProcessPipeInputStream extends BufferedInputStream {
      private final Object closeLock = new Object();

      ProcessPipeInputStream(int var1) {
         super(new FileInputStream(UNIXProcess.newFileDescriptor(var1)));
      }

      private static byte[] drainInputStream(InputStream var0) throws IOException {
         int var1 = 0;

         int var2;
         byte[] var3;
         for(var3 = null; (var2 = var0.available()) > 0; var1 += var0.read(var3, var1, var2)) {
            var3 = var3 == null ? new byte[var2] : Arrays.copyOf(var3, var1 + var2);
         }

         return var3 != null && var1 != var3.length ? Arrays.copyOf(var3, var1) : var3;
      }

      synchronized void processExited() {
         synchronized(this.closeLock) {
            try {
               InputStream var2 = this.in;
               if (var2 != null) {
                  byte[] var3 = drainInputStream(var2);
                  var2.close();
                  this.in = (InputStream)(var3 == null ? ProcessBuilder.NullInputStream.INSTANCE : new ByteArrayInputStream(var3));
               }
            } catch (IOException var5) {
            }

         }
      }

      public void close() throws IOException {
         synchronized(this.closeLock) {
            super.close();
         }
      }
   }

   private static enum Platform {
      LINUX(new UNIXProcess.LaunchMechanism[]{UNIXProcess.LaunchMechanism.VFORK, UNIXProcess.LaunchMechanism.FORK}),
      BSD(new UNIXProcess.LaunchMechanism[]{UNIXProcess.LaunchMechanism.POSIX_SPAWN, UNIXProcess.LaunchMechanism.FORK}),
      SOLARIS(new UNIXProcess.LaunchMechanism[]{UNIXProcess.LaunchMechanism.POSIX_SPAWN, UNIXProcess.LaunchMechanism.FORK}),
      AIX(new UNIXProcess.LaunchMechanism[]{UNIXProcess.LaunchMechanism.POSIX_SPAWN, UNIXProcess.LaunchMechanism.FORK});

      final UNIXProcess.LaunchMechanism defaultLaunchMechanism;
      final Set<UNIXProcess.LaunchMechanism> validLaunchMechanisms;

      private Platform(UNIXProcess.LaunchMechanism... var3) {
         this.defaultLaunchMechanism = var3[0];
         this.validLaunchMechanisms = EnumSet.copyOf((Collection)Arrays.asList(var3));
      }

      private String helperPath(String var1, String var2) {
         switch(this) {
         case SOLARIS:
            if (var2.equals("x86")) {
               var2 = "i386";
            } else if (var2.equals("x86_64")) {
               var2 = "amd64";
            }
         case LINUX:
         case AIX:
            return var1 + "/lib/" + var2 + "/jspawnhelper";
         case BSD:
            return var1 + "/lib/jspawnhelper";
         default:
            throw new AssertionError("Unsupported platform: " + this);
         }
      }

      String helperPath() {
         return (String)AccessController.doPrivileged(() -> {
            return this.helperPath(System.getProperty("java.home"), System.getProperty("os.arch"));
         });
      }

      UNIXProcess.LaunchMechanism launchMechanism() {
         return (UNIXProcess.LaunchMechanism)AccessController.doPrivileged(() -> {
            String var1 = System.getProperty("jdk.lang.Process.launchMechanism");
            UNIXProcess.LaunchMechanism var2;
            if (var1 == null) {
               var2 = this.defaultLaunchMechanism;
               var1 = var2.name().toLowerCase(Locale.ENGLISH);
            } else {
               try {
                  var2 = UNIXProcess.LaunchMechanism.valueOf(var1.toUpperCase(Locale.ENGLISH));
               } catch (IllegalArgumentException var4) {
                  var2 = null;
               }
            }

            if (var2 != null && this.validLaunchMechanisms.contains(var2)) {
               return var2;
            } else {
               throw new Error(var1 + " is not a supported process launch mechanism on this platform.");
            }
         });
      }

      static UNIXProcess.Platform get() {
         String var0 = (String)AccessController.doPrivileged(() -> {
            return System.getProperty("os.name");
         });
         if (var0.equals("Linux")) {
            return LINUX;
         } else if (var0.contains("OS X")) {
            return BSD;
         } else if (var0.equals("SunOS")) {
            return SOLARIS;
         } else if (var0.equals("AIX")) {
            return AIX;
         } else {
            throw new Error(var0 + " is not a supported OS platform.");
         }
      }
   }

   private static enum LaunchMechanism {
      FORK,
      POSIX_SPAWN,
      VFORK;
   }
}
