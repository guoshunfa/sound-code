package java.util.logging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

public class FileHandler extends StreamHandler {
   private FileHandler.MeteredStream meter;
   private boolean append;
   private int limit;
   private int count;
   private String pattern;
   private String lockFileName;
   private FileChannel lockFileChannel;
   private File[] files;
   private static final int DEFAULT_MAX_LOCKS = 100;
   private static int maxLocks = (Integer)AccessController.doPrivileged(() -> {
      return Integer.getInteger("jdk.internal.FileHandlerLogging.maxLocks", 100);
   });
   private static final Set<String> locks = new HashSet();

   private void open(File var1, boolean var2) throws IOException {
      int var3 = 0;
      if (var2) {
         var3 = (int)var1.length();
      }

      FileOutputStream var4 = new FileOutputStream(var1.toString(), var2);
      BufferedOutputStream var5 = new BufferedOutputStream(var4);
      this.meter = new FileHandler.MeteredStream(var5, var3);
      this.setOutputStream(this.meter);
   }

   private void configure() {
      LogManager var1 = LogManager.getLogManager();
      String var2 = this.getClass().getName();
      this.pattern = var1.getStringProperty(var2 + ".pattern", "%h/java%u.log");
      this.limit = var1.getIntProperty(var2 + ".limit", 0);
      if (this.limit < 0) {
         this.limit = 0;
      }

      this.count = var1.getIntProperty(var2 + ".count", 1);
      if (this.count <= 0) {
         this.count = 1;
      }

      this.append = var1.getBooleanProperty(var2 + ".append", false);
      this.setLevel(var1.getLevelProperty(var2 + ".level", Level.ALL));
      this.setFilter(var1.getFilterProperty(var2 + ".filter", (Filter)null));
      this.setFormatter(var1.getFormatterProperty(var2 + ".formatter", new XMLFormatter()));

      try {
         this.setEncoding(var1.getStringProperty(var2 + ".encoding", (String)null));
      } catch (Exception var6) {
         try {
            this.setEncoding((String)null);
         } catch (Exception var5) {
         }
      }

   }

   public FileHandler() throws IOException, SecurityException {
      this.checkPermission();
      this.configure();
      this.openFiles();
   }

   public FileHandler(String var1) throws IOException, SecurityException {
      if (var1.length() < 1) {
         throw new IllegalArgumentException();
      } else {
         this.checkPermission();
         this.configure();
         this.pattern = var1;
         this.limit = 0;
         this.count = 1;
         this.openFiles();
      }
   }

   public FileHandler(String var1, boolean var2) throws IOException, SecurityException {
      if (var1.length() < 1) {
         throw new IllegalArgumentException();
      } else {
         this.checkPermission();
         this.configure();
         this.pattern = var1;
         this.limit = 0;
         this.count = 1;
         this.append = var2;
         this.openFiles();
      }
   }

   public FileHandler(String var1, int var2, int var3) throws IOException, SecurityException {
      if (var2 >= 0 && var3 >= 1 && var1.length() >= 1) {
         this.checkPermission();
         this.configure();
         this.pattern = var1;
         this.limit = var2;
         this.count = var3;
         this.openFiles();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public FileHandler(String var1, int var2, int var3, boolean var4) throws IOException, SecurityException {
      if (var2 >= 0 && var3 >= 1 && var1.length() >= 1) {
         this.checkPermission();
         this.configure();
         this.pattern = var1;
         this.limit = var2;
         this.count = var3;
         this.append = var4;
         this.openFiles();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private boolean isParentWritable(Path var1) {
      Path var2 = var1.getParent();
      if (var2 == null) {
         var2 = var1.toAbsolutePath().getParent();
      }

      return var2 != null && Files.isWritable(var2);
   }

   private void openFiles() throws IOException {
      LogManager var1 = LogManager.getLogManager();
      var1.checkPermission();
      if (this.count < 1) {
         throw new IllegalArgumentException("file count = " + this.count);
      } else {
         if (this.limit < 0) {
            this.limit = 0;
         }

         FileHandler.InitializationErrorManager var2 = new FileHandler.InitializationErrorManager();
         this.setErrorManager(var2);
         int var3 = -1;

         while(true) {
            ++var3;
            if (var3 > maxLocks) {
               throw new IOException("Couldn't get lock for " + this.pattern + ", maxLocks: " + maxLocks);
            }

            this.lockFileName = this.generate(this.pattern, 0, var3).toString() + ".lck";
            synchronized(locks) {
               if (locks.contains(this.lockFileName)) {
                  continue;
               }

               Path var5 = Paths.get(this.lockFileName);
               FileChannel var6 = null;
               int var7 = -1;
               boolean var8 = false;

               while(var6 == null && var7++ < 1) {
                  try {
                     var6 = FileChannel.open(var5, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                     var8 = true;
                  } catch (FileAlreadyExistsException var16) {
                     if (!Files.isRegularFile(var5, LinkOption.NOFOLLOW_LINKS) || !this.isParentWritable(var5)) {
                        break;
                     }

                     try {
                        var6 = FileChannel.open(var5, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                     } catch (NoSuchFileException var14) {
                     } catch (IOException var15) {
                        break;
                     }
                  }
               }

               if (var6 == null) {
                  continue;
               }

               this.lockFileChannel = var6;

               boolean var9;
               try {
                  var9 = this.lockFileChannel.tryLock() != null;
               } catch (IOException var12) {
                  var9 = var8;
               } catch (OverlappingFileLockException var13) {
                  var9 = false;
               }

               if (!var9) {
                  this.lockFileChannel.close();
                  continue;
               }

               locks.add(this.lockFileName);
            }

            this.files = new File[this.count];

            for(int var4 = 0; var4 < this.count; ++var4) {
               this.files[var4] = this.generate(this.pattern, var4, var3);
            }

            if (this.append) {
               this.open(this.files[0], true);
            } else {
               this.rotate();
            }

            Exception var18 = var2.lastException;
            if (var18 != null) {
               if (var18 instanceof IOException) {
                  throw (IOException)var18;
               }

               if (var18 instanceof SecurityException) {
                  throw (SecurityException)var18;
               }

               throw new IOException("Exception: " + var18);
            }

            this.setErrorManager(new ErrorManager());
            return;
         }
      }
   }

   private File generate(String var1, int var2, int var3) throws IOException {
      File var4 = null;
      String var5 = "";
      int var6 = 0;
      boolean var7 = false;
      boolean var8 = false;

      while(true) {
         while(var6 < var1.length()) {
            char var9 = var1.charAt(var6);
            ++var6;
            char var10 = 0;
            if (var6 < var1.length()) {
               var10 = Character.toLowerCase(var1.charAt(var6));
            }

            if (var9 == '/') {
               if (var4 == null) {
                  var4 = new File(var5);
               } else {
                  var4 = new File(var4, var5);
               }

               var5 = "";
            } else {
               if (var9 == '%') {
                  if (var10 == 't') {
                     String var11 = System.getProperty("java.io.tmpdir");
                     if (var11 == null) {
                        var11 = System.getProperty("user.home");
                     }

                     var4 = new File(var11);
                     ++var6;
                     var5 = "";
                     continue;
                  }

                  if (var10 == 'h') {
                     var4 = new File(System.getProperty("user.home"));
                     if (isSetUID()) {
                        throw new IOException("can't use %h in set UID program");
                     }

                     ++var6;
                     var5 = "";
                     continue;
                  }

                  if (var10 == 'g') {
                     var5 = var5 + var2;
                     var7 = true;
                     ++var6;
                     continue;
                  }

                  if (var10 == 'u') {
                     var5 = var5 + var3;
                     var8 = true;
                     ++var6;
                     continue;
                  }

                  if (var10 == '%') {
                     var5 = var5 + "%";
                     ++var6;
                     continue;
                  }
               }

               var5 = var5 + var9;
            }
         }

         if (this.count > 1 && !var7) {
            var5 = var5 + "." + var2;
         }

         if (var3 > 0 && !var8) {
            var5 = var5 + "." + var3;
         }

         if (var5.length() > 0) {
            if (var4 == null) {
               var4 = new File(var5);
            } else {
               var4 = new File(var4, var5);
            }
         }

         return var4;
      }
   }

   private synchronized void rotate() {
      Level var1 = this.getLevel();
      this.setLevel(Level.OFF);
      super.close();

      for(int var2 = this.count - 2; var2 >= 0; --var2) {
         File var3 = this.files[var2];
         File var4 = this.files[var2 + 1];
         if (var3.exists()) {
            if (var4.exists()) {
               var4.delete();
            }

            var3.renameTo(var4);
         }
      }

      try {
         this.open(this.files[0], false);
      } catch (IOException var5) {
         this.reportError((String)null, var5, 4);
      }

      this.setLevel(var1);
   }

   public synchronized void publish(LogRecord var1) {
      if (this.isLoggable(var1)) {
         super.publish(var1);
         this.flush();
         if (this.limit > 0 && this.meter.written >= this.limit) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  FileHandler.this.rotate();
                  return null;
               }
            });
         }

      }
   }

   public synchronized void close() throws SecurityException {
      super.close();
      if (this.lockFileName != null) {
         try {
            this.lockFileChannel.close();
         } catch (Exception var4) {
         }

         synchronized(locks) {
            locks.remove(this.lockFileName);
         }

         (new File(this.lockFileName)).delete();
         this.lockFileName = null;
         this.lockFileChannel = null;
      }
   }

   private static native boolean isSetUID();

   static {
      if (maxLocks <= 0) {
         maxLocks = 100;
      }

   }

   private static class InitializationErrorManager extends ErrorManager {
      Exception lastException;

      private InitializationErrorManager() {
      }

      public void error(String var1, Exception var2, int var3) {
         this.lastException = var2;
      }

      // $FF: synthetic method
      InitializationErrorManager(Object var1) {
         this();
      }
   }

   private class MeteredStream extends OutputStream {
      final OutputStream out;
      int written;

      MeteredStream(OutputStream var2, int var3) {
         this.out = var2;
         this.written = var3;
      }

      public void write(int var1) throws IOException {
         this.out.write(var1);
         ++this.written;
      }

      public void write(byte[] var1) throws IOException {
         this.out.write(var1);
         this.written += var1.length;
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.out.write(var1, var2, var3);
         this.written += var3;
      }

      public void flush() throws IOException {
         this.out.flush();
      }

      public void close() throws IOException {
         this.out.close();
      }
   }
}
