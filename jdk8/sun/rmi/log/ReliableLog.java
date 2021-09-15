package sun.rmi.log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class ReliableLog {
   public static final int PreferredMajorVersion = 0;
   public static final int PreferredMinorVersion = 2;
   private boolean Debug;
   private static String snapshotPrefix = "Snapshot.";
   private static String logfilePrefix = "Logfile.";
   private static String versionFile = "Version_Number";
   private static String newVersionFile = "New_Version_Number";
   private static int intBytes = 4;
   private static long diskPageSize = 512L;
   private File dir;
   private int version;
   private String logName;
   private ReliableLog.LogFile log;
   private long snapshotBytes;
   private long logBytes;
   private int logEntries;
   private long lastSnapshot;
   private long lastLog;
   private LogHandler handler;
   private final byte[] intBuf;
   private int majorFormatVersion;
   private int minorFormatVersion;
   private static final Constructor<? extends ReliableLog.LogFile> logClassConstructor = getLogClassConstructor();

   public ReliableLog(String var1, LogHandler var2, boolean var3) throws IOException {
      this.Debug = false;
      this.version = 0;
      this.logName = null;
      this.log = null;
      this.snapshotBytes = 0L;
      this.logBytes = 0L;
      this.logEntries = 0;
      this.lastSnapshot = 0L;
      this.lastLog = 0L;
      this.intBuf = new byte[4];
      this.majorFormatVersion = 0;
      this.minorFormatVersion = 0;
      this.Debug = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.rmi.log.debug")));
      this.dir = new File(var1);
      if ((!this.dir.exists() || !this.dir.isDirectory()) && !this.dir.mkdir()) {
         throw new IOException("could not create directory for log: " + var1);
      } else {
         this.handler = var2;
         this.lastSnapshot = 0L;
         this.lastLog = 0L;
         this.getVersion();
         if (this.version == 0) {
            try {
               this.snapshot(var2.initialSnapshot());
            } catch (IOException var5) {
               throw var5;
            } catch (Exception var6) {
               throw new IOException("initial snapshot failed with exception: " + var6);
            }
         }

      }
   }

   public ReliableLog(String var1, LogHandler var2) throws IOException {
      this(var1, var2, false);
   }

   public synchronized Object recover() throws IOException {
      if (this.Debug) {
         System.err.println("log.debug: recover()");
      }

      if (this.version == 0) {
         return null;
      } else {
         String var2 = this.versionName(snapshotPrefix);
         File var3 = new File(var2);
         BufferedInputStream var4 = new BufferedInputStream(new FileInputStream(var3));
         if (this.Debug) {
            System.err.println("log.debug: recovering from " + var2);
         }

         Object var1;
         try {
            try {
               var1 = this.handler.recover(var4);
            } catch (IOException var10) {
               throw var10;
            } catch (Exception var11) {
               if (this.Debug) {
                  System.err.println("log.debug: recovery failed: " + var11);
               }

               throw new IOException("log recover failed with exception: " + var11);
            }

            this.snapshotBytes = var3.length();
         } finally {
            var4.close();
         }

         return this.recoverUpdates(var1);
      }
   }

   public synchronized void update(Object var1) throws IOException {
      this.update(var1, true);
   }

   public synchronized void update(Object var1, boolean var2) throws IOException {
      if (this.log == null) {
         throw new IOException("log is inaccessible, it may have been corrupted or closed");
      } else {
         long var3 = this.log.getFilePointer();
         boolean var5 = this.log.checkSpansBoundary(var3);
         this.writeInt(this.log, var5 ? Integer.MIN_VALUE : 0);

         try {
            this.handler.writeUpdate(new LogOutputStream(this.log), var1);
         } catch (IOException var9) {
            throw var9;
         } catch (Exception var10) {
            throw (IOException)(new IOException("write update failed")).initCause(var10);
         }

         this.log.sync();
         long var6 = this.log.getFilePointer();
         int var8 = (int)(var6 - var3 - (long)intBytes);
         this.log.seek(var3);
         if (var5) {
            this.writeInt(this.log, var8 | Integer.MIN_VALUE);
            this.log.sync();
            this.log.seek(var3);
            this.log.writeByte(var8 >> 24);
            this.log.sync();
         } else {
            this.writeInt(this.log, var8);
            this.log.sync();
         }

         this.log.seek(var6);
         this.logBytes = var6;
         this.lastLog = System.currentTimeMillis();
         ++this.logEntries;
      }
   }

   private static Constructor<? extends ReliableLog.LogFile> getLogClassConstructor() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.log.class")));
      if (var0 != null) {
         try {
            ClassLoader var1 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
               public ClassLoader run() {
                  return ClassLoader.getSystemClassLoader();
               }
            });
            Class var2 = var1.loadClass(var0).asSubclass(ReliableLog.LogFile.class);
            return var2.getConstructor(String.class, String.class);
         } catch (Exception var3) {
            System.err.println("Exception occurred:");
            var3.printStackTrace();
         }
      }

      return null;
   }

   public synchronized void snapshot(Object var1) throws IOException {
      int var2 = this.version;
      this.incrVersion();
      String var3 = this.versionName(snapshotPrefix);
      File var4 = new File(var3);
      FileOutputStream var5 = new FileOutputStream(var4);

      try {
         try {
            this.handler.snapshot(var5, var1);
         } catch (IOException var11) {
            throw var11;
         } catch (Exception var12) {
            throw new IOException("snapshot failed", var12);
         }

         this.lastSnapshot = System.currentTimeMillis();
      } finally {
         var5.close();
         this.snapshotBytes = var4.length();
      }

      this.openLogFile(true);
      this.writeVersionFile(true);
      this.commitToNewVersion();
      this.deleteSnapshot(var2);
      this.deleteLogFile(var2);
   }

   public synchronized void close() throws IOException {
      if (this.log != null) {
         try {
            this.log.close();
         } finally {
            this.log = null;
         }

      }
   }

   public long snapshotSize() {
      return this.snapshotBytes;
   }

   public long logSize() {
      return this.logBytes;
   }

   private void writeInt(DataOutput var1, int var2) throws IOException {
      this.intBuf[0] = (byte)(var2 >> 24);
      this.intBuf[1] = (byte)(var2 >> 16);
      this.intBuf[2] = (byte)(var2 >> 8);
      this.intBuf[3] = (byte)var2;
      var1.write(this.intBuf);
   }

   private String fName(String var1) {
      return this.dir.getPath() + File.separator + var1;
   }

   private String versionName(String var1) {
      return this.versionName(var1, 0);
   }

   private String versionName(String var1, int var2) {
      var2 = var2 == 0 ? this.version : var2;
      return this.fName(var1) + String.valueOf(var2);
   }

   private void incrVersion() {
      do {
         ++this.version;
      } while(this.version == 0);

   }

   private void deleteFile(String var1) throws IOException {
      File var2 = new File(var1);
      if (!var2.delete()) {
         throw new IOException("couldn't remove file: " + var1);
      }
   }

   private void deleteNewVersionFile() throws IOException {
      this.deleteFile(this.fName(newVersionFile));
   }

   private void deleteSnapshot(int var1) throws IOException {
      if (var1 != 0) {
         this.deleteFile(this.versionName(snapshotPrefix, var1));
      }
   }

   private void deleteLogFile(int var1) throws IOException {
      if (var1 != 0) {
         this.deleteFile(this.versionName(logfilePrefix, var1));
      }
   }

   private void openLogFile(boolean var1) throws IOException {
      try {
         this.close();
      } catch (IOException var4) {
      }

      this.logName = this.versionName(logfilePrefix);

      try {
         this.log = logClassConstructor == null ? new ReliableLog.LogFile(this.logName, "rw") : (ReliableLog.LogFile)logClassConstructor.newInstance(this.logName, "rw");
      } catch (Exception var3) {
         throw (IOException)(new IOException("unable to construct LogFile instance")).initCause(var3);
      }

      if (var1) {
         this.initializeLogFile();
      }

   }

   private void initializeLogFile() throws IOException {
      this.log.setLength(0L);
      this.majorFormatVersion = 0;
      this.writeInt(this.log, 0);
      this.minorFormatVersion = 2;
      this.writeInt(this.log, 2);
      this.logBytes = (long)(intBytes * 2);
      this.logEntries = 0;
   }

   private void writeVersionFile(boolean var1) throws IOException {
      String var2;
      if (var1) {
         var2 = newVersionFile;
      } else {
         var2 = versionFile;
      }

      FileOutputStream var3 = new FileOutputStream(this.fName(var2));
      Throwable var4 = null;

      try {
         DataOutputStream var5 = new DataOutputStream(var3);
         Throwable var6 = null;

         try {
            this.writeInt(var5, this.version);
         } catch (Throwable var29) {
            var6 = var29;
            throw var29;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var28) {
                     var6.addSuppressed(var28);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (Throwable var31) {
         var4 = var31;
         throw var31;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var27) {
                  var4.addSuppressed(var27);
               }
            } else {
               var3.close();
            }
         }

      }

   }

   private void createFirstVersion() throws IOException {
      this.version = 0;
      this.writeVersionFile(false);
   }

   private void commitToNewVersion() throws IOException {
      this.writeVersionFile(false);
      this.deleteNewVersionFile();
   }

   private int readVersion(String var1) throws IOException {
      DataInputStream var2 = new DataInputStream(new FileInputStream(var1));
      Throwable var3 = null;

      int var4;
      try {
         var4 = var2.readInt();
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               var2.close();
            }
         }

      }

      return var4;
   }

   private void getVersion() throws IOException {
      try {
         this.version = this.readVersion(this.fName(newVersionFile));
         this.commitToNewVersion();
      } catch (IOException var5) {
         try {
            this.deleteNewVersionFile();
         } catch (IOException var4) {
         }

         try {
            this.version = this.readVersion(this.fName(versionFile));
         } catch (IOException var3) {
            this.createFirstVersion();
         }
      }

   }

   private Object recoverUpdates(Object var1) throws IOException {
      this.logBytes = 0L;
      this.logEntries = 0;
      if (this.version == 0) {
         return var1;
      } else {
         String var2 = this.versionName(logfilePrefix);
         BufferedInputStream var3 = new BufferedInputStream(new FileInputStream(var2));
         DataInputStream var4 = new DataInputStream(var3);
         if (this.Debug) {
            System.err.println("log.debug: reading updates from " + var2);
         }

         try {
            this.majorFormatVersion = var4.readInt();
            this.logBytes += (long)intBytes;
            this.minorFormatVersion = var4.readInt();
            this.logBytes += (long)intBytes;
         } catch (EOFException var15) {
            this.openLogFile(true);
            var3 = null;
         }

         if (this.majorFormatVersion != 0) {
            if (this.Debug) {
               System.err.println("log.debug: major version mismatch: " + this.majorFormatVersion + "." + this.minorFormatVersion);
            }

            throw new IOException("Log file " + this.logName + " has a version " + this.majorFormatVersion + "." + this.minorFormatVersion + " format, and this implementation  understands only version " + 0 + "." + 2);
         } else {
            try {
               while(var3 != null) {
                  boolean var5 = false;

                  int var18;
                  try {
                     var18 = var4.readInt();
                  } catch (EOFException var16) {
                     if (this.Debug) {
                        System.err.println("log.debug: log was sync'd cleanly");
                     }
                     break;
                  }

                  if (var18 <= 0) {
                     if (this.Debug) {
                        System.err.println("log.debug: last update incomplete, updateLen = 0x" + Integer.toHexString(var18));
                     }
                     break;
                  }

                  if (var3.available() < var18) {
                     if (this.Debug) {
                        System.err.println("log.debug: log was truncated");
                     }
                     break;
                  }

                  if (this.Debug) {
                     System.err.println("log.debug: rdUpdate size " + var18);
                  }

                  try {
                     var1 = this.handler.readUpdate(new LogInputStream(var3, var18), var1);
                  } catch (IOException var13) {
                     throw var13;
                  } catch (Exception var14) {
                     var14.printStackTrace();
                     throw new IOException("read update failed with exception: " + var14);
                  }

                  this.logBytes += (long)(intBytes + var18);
                  ++this.logEntries;
               }
            } finally {
               if (var3 != null) {
                  var3.close();
               }

            }

            if (this.Debug) {
               System.err.println("log.debug: recovered updates: " + this.logEntries);
            }

            this.openLogFile(false);
            if (this.log == null) {
               throw new IOException("rmid's log is inaccessible, it may have been corrupted or closed");
            } else {
               this.log.seek(this.logBytes);
               this.log.setLength(this.logBytes);
               return var1;
            }
         }
      }
   }

   public static class LogFile extends RandomAccessFile {
      private final FileDescriptor fd = this.getFD();

      public LogFile(String var1, String var2) throws FileNotFoundException, IOException {
         super(var1, var2);
      }

      protected void sync() throws IOException {
         this.fd.sync();
      }

      protected boolean checkSpansBoundary(long var1) {
         return var1 % 512L > 508L;
      }
   }
}
