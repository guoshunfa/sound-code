package java.util.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

class FileSystemPreferences extends AbstractPreferences {
   private static final int SYNC_INTERVAL = Math.max(1, Integer.parseInt((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.util.prefs.syncInterval", "30")))));
   private static File systemRootDir;
   private static boolean isSystemRootWritable;
   private static File userRootDir;
   private static boolean isUserRootWritable;
   static Preferences userRoot = null;
   static Preferences systemRoot;
   private static final int USER_READ_WRITE = 384;
   private static final int USER_RW_ALL_READ = 420;
   private static final int USER_RWX_ALL_RX = 493;
   private static final int USER_RWX = 448;
   static File userLockFile;
   static File systemLockFile;
   private static int userRootLockHandle = 0;
   private static int systemRootLockHandle = 0;
   private final File dir;
   private final File prefsFile;
   private final File tmpFile;
   private static File userRootModFile;
   private static boolean isUserRootModified = false;
   private static long userRootModTime;
   private static File systemRootModFile;
   private static boolean isSystemRootModified = false;
   private static long systemRootModTime;
   private Map<String, String> prefsCache = null;
   private long lastSyncTime = 0L;
   private static final int EAGAIN = 11;
   private static final int EACCES = 13;
   private static final int LOCK_HANDLE = 0;
   private static final int ERROR_CODE = 1;
   final List<FileSystemPreferences.Change> changeLog = new ArrayList();
   FileSystemPreferences.NodeCreate nodeCreate = null;
   private static Timer syncTimer = new Timer(true);
   private final boolean isUserNode;
   private static final String[] EMPTY_STRING_ARRAY;
   private static int INIT_SLEEP_TIME;
   private static int MAX_ATTEMPTS;

   private static PlatformLogger getLogger() {
      return PlatformLogger.getLogger("java.util.prefs");
   }

   static synchronized Preferences getUserRoot() {
      if (userRoot == null) {
         setupUserRoot();
         userRoot = new FileSystemPreferences(true);
      }

      return userRoot;
   }

   private static void setupUserRoot() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            FileSystemPreferences.userRootDir = new File(System.getProperty("java.util.prefs.userRoot", System.getProperty("user.home")), ".java/.userPrefs");
            if (!FileSystemPreferences.userRootDir.exists()) {
               if (FileSystemPreferences.userRootDir.mkdirs()) {
                  try {
                     FileSystemPreferences.chmod(FileSystemPreferences.userRootDir.getCanonicalPath(), 448);
                  } catch (IOException var4) {
                     FileSystemPreferences.getLogger().warning("Could not change permissions on userRoot directory. ");
                  }

                  FileSystemPreferences.getLogger().info("Created user preferences directory.");
               } else {
                  FileSystemPreferences.getLogger().warning("Couldn't create user preferences directory. User preferences are unusable.");
               }
            }

            FileSystemPreferences.isUserRootWritable = FileSystemPreferences.userRootDir.canWrite();
            String var1 = System.getProperty("user.name");
            FileSystemPreferences.userLockFile = new File(FileSystemPreferences.userRootDir, ".user.lock." + var1);
            FileSystemPreferences.userRootModFile = new File(FileSystemPreferences.userRootDir, ".userRootModFile." + var1);
            if (!FileSystemPreferences.userRootModFile.exists()) {
               try {
                  FileSystemPreferences.userRootModFile.createNewFile();
                  int var2 = FileSystemPreferences.chmod(FileSystemPreferences.userRootModFile.getCanonicalPath(), 384);
                  if (var2 != 0) {
                     FileSystemPreferences.getLogger().warning("Problem creating userRoot mod file. Chmod failed on " + FileSystemPreferences.userRootModFile.getCanonicalPath() + " Unix error code " + var2);
                  }
               } catch (IOException var3) {
                  FileSystemPreferences.getLogger().warning(var3.toString());
               }
            }

            FileSystemPreferences.userRootModTime = FileSystemPreferences.userRootModFile.lastModified();
            return null;
         }
      });
   }

   static synchronized Preferences getSystemRoot() {
      if (systemRoot == null) {
         setupSystemRoot();
         systemRoot = new FileSystemPreferences(false);
      }

      return systemRoot;
   }

   private static void setupSystemRoot() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            String var1 = System.getProperty("java.util.prefs.systemRoot", "/etc/.java");
            FileSystemPreferences.systemRootDir = new File(var1, ".systemPrefs");
            if (!FileSystemPreferences.systemRootDir.exists()) {
               FileSystemPreferences.systemRootDir = new File(System.getProperty("java.home"), ".systemPrefs");
               if (!FileSystemPreferences.systemRootDir.exists()) {
                  if (FileSystemPreferences.systemRootDir.mkdirs()) {
                     FileSystemPreferences.getLogger().info("Created system preferences directory in java.home.");

                     try {
                        FileSystemPreferences.chmod(FileSystemPreferences.systemRootDir.getCanonicalPath(), 493);
                     } catch (IOException var4) {
                     }
                  } else {
                     FileSystemPreferences.getLogger().warning("Could not create system preferences directory. System preferences are unusable.");
                  }
               }
            }

            FileSystemPreferences.isSystemRootWritable = FileSystemPreferences.systemRootDir.canWrite();
            FileSystemPreferences.systemLockFile = new File(FileSystemPreferences.systemRootDir, ".system.lock");
            FileSystemPreferences.systemRootModFile = new File(FileSystemPreferences.systemRootDir, ".systemRootModFile");
            if (!FileSystemPreferences.systemRootModFile.exists() && FileSystemPreferences.isSystemRootWritable) {
               try {
                  FileSystemPreferences.systemRootModFile.createNewFile();
                  int var2 = FileSystemPreferences.chmod(FileSystemPreferences.systemRootModFile.getCanonicalPath(), 420);
                  if (var2 != 0) {
                     FileSystemPreferences.getLogger().warning("Chmod failed on " + FileSystemPreferences.systemRootModFile.getCanonicalPath() + " Unix error code " + var2);
                  }
               } catch (IOException var3) {
                  FileSystemPreferences.getLogger().warning(var3.toString());
               }
            }

            FileSystemPreferences.systemRootModTime = FileSystemPreferences.systemRootModFile.lastModified();
            return null;
         }
      });
   }

   private void replayChanges() {
      int var1 = 0;

      for(int var2 = this.changeLog.size(); var1 < var2; ++var1) {
         ((FileSystemPreferences.Change)this.changeLog.get(var1)).replay();
      }

   }

   private static void syncWorld() {
      Class var2 = FileSystemPreferences.class;
      Preferences var0;
      Preferences var1;
      synchronized(FileSystemPreferences.class) {
         var0 = userRoot;
         var1 = systemRoot;
      }

      try {
         if (var0 != null) {
            var0.flush();
         }
      } catch (BackingStoreException var5) {
         getLogger().warning("Couldn't flush user prefs: " + var5);
      }

      try {
         if (var1 != null) {
            var1.flush();
         }
      } catch (BackingStoreException var4) {
         getLogger().warning("Couldn't flush system prefs: " + var4);
      }

   }

   private FileSystemPreferences(boolean var1) {
      super((AbstractPreferences)null, "");
      this.isUserNode = var1;
      this.dir = var1 ? userRootDir : systemRootDir;
      this.prefsFile = new File(this.dir, "prefs.xml");
      this.tmpFile = new File(this.dir, "prefs.tmp");
   }

   private FileSystemPreferences(FileSystemPreferences var1, String var2) {
      super(var1, var2);
      this.isUserNode = var1.isUserNode;
      this.dir = new File(var1.dir, dirName(var2));
      this.prefsFile = new File(this.dir, "prefs.xml");
      this.tmpFile = new File(this.dir, "prefs.tmp");
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            FileSystemPreferences.this.newNode = !FileSystemPreferences.this.dir.exists();
            return null;
         }
      });
      if (this.newNode) {
         this.prefsCache = new TreeMap();
         this.nodeCreate = new FileSystemPreferences.NodeCreate();
         this.changeLog.add(this.nodeCreate);
      }

   }

   public boolean isUserNode() {
      return this.isUserNode;
   }

   protected void putSpi(String var1, String var2) {
      this.initCacheIfNecessary();
      this.changeLog.add(new FileSystemPreferences.Put(var1, var2));
      this.prefsCache.put(var1, var2);
   }

   protected String getSpi(String var1) {
      this.initCacheIfNecessary();
      return (String)this.prefsCache.get(var1);
   }

   protected void removeSpi(String var1) {
      this.initCacheIfNecessary();
      this.changeLog.add(new FileSystemPreferences.Remove(var1));
      this.prefsCache.remove(var1);
   }

   private void initCacheIfNecessary() {
      if (this.prefsCache == null) {
         try {
            this.loadCache();
         } catch (Exception var2) {
            this.prefsCache = new TreeMap();
         }

      }
   }

   private void loadCache() throws BackingStoreException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws BackingStoreException {
               TreeMap var1 = new TreeMap();
               long var2 = 0L;

               try {
                  var2 = FileSystemPreferences.this.prefsFile.lastModified();
                  FileInputStream var4 = new FileInputStream(FileSystemPreferences.this.prefsFile);
                  Throwable var5 = null;

                  try {
                     XmlSupport.importMap(var4, var1);
                  } catch (Throwable var15) {
                     var5 = var15;
                     throw var15;
                  } finally {
                     if (var4 != null) {
                        if (var5 != null) {
                           try {
                              var4.close();
                           } catch (Throwable var14) {
                              var5.addSuppressed(var14);
                           }
                        } else {
                           var4.close();
                        }
                     }

                  }
               } catch (Exception var17) {
                  if (var17 instanceof InvalidPreferencesFormatException) {
                     FileSystemPreferences.getLogger().warning("Invalid preferences format in " + FileSystemPreferences.this.prefsFile.getPath());
                     FileSystemPreferences.this.prefsFile.renameTo(new File(FileSystemPreferences.this.prefsFile.getParentFile(), "IncorrectFormatPrefs.xml"));
                     var1 = new TreeMap();
                  } else {
                     if (!(var17 instanceof FileNotFoundException)) {
                        throw new BackingStoreException(var17);
                     }

                     FileSystemPreferences.getLogger().warning("Prefs file removed in background " + FileSystemPreferences.this.prefsFile.getPath());
                  }
               }

               FileSystemPreferences.this.prefsCache = var1;
               FileSystemPreferences.this.lastSyncTime = var2;
               return null;
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (BackingStoreException)var2.getException();
      }
   }

   private void writeBackCache() throws BackingStoreException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws BackingStoreException {
               try {
                  if (!FileSystemPreferences.this.dir.exists() && !FileSystemPreferences.this.dir.mkdirs()) {
                     throw new BackingStoreException(FileSystemPreferences.this.dir + " create failed.");
                  } else {
                     FileOutputStream var1 = new FileOutputStream(FileSystemPreferences.this.tmpFile);
                     Throwable var2 = null;

                     try {
                        XmlSupport.exportMap(var1, FileSystemPreferences.this.prefsCache);
                     } catch (Throwable var12) {
                        var2 = var12;
                        throw var12;
                     } finally {
                        if (var1 != null) {
                           if (var2 != null) {
                              try {
                                 var1.close();
                              } catch (Throwable var11) {
                                 var2.addSuppressed(var11);
                              }
                           } else {
                              var1.close();
                           }
                        }

                     }

                     if (!FileSystemPreferences.this.tmpFile.renameTo(FileSystemPreferences.this.prefsFile)) {
                        throw new BackingStoreException("Can't rename " + FileSystemPreferences.this.tmpFile + " to " + FileSystemPreferences.this.prefsFile);
                     } else {
                        return null;
                     }
                  }
               } catch (Exception var14) {
                  if (var14 instanceof BackingStoreException) {
                     throw (BackingStoreException)var14;
                  } else {
                     throw new BackingStoreException(var14);
                  }
               }
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (BackingStoreException)var2.getException();
      }
   }

   protected String[] keysSpi() {
      this.initCacheIfNecessary();
      return (String[])this.prefsCache.keySet().toArray(new String[this.prefsCache.size()]);
   }

   protected String[] childrenNamesSpi() {
      return (String[])AccessController.doPrivileged(new PrivilegedAction<String[]>() {
         public String[] run() {
            ArrayList var1 = new ArrayList();
            File[] var2 = FileSystemPreferences.this.dir.listFiles();
            if (var2 != null) {
               for(int var3 = 0; var3 < var2.length; ++var3) {
                  if (var2[var3].isDirectory()) {
                     var1.add(FileSystemPreferences.nodeName(var2[var3].getName()));
                  }
               }
            }

            return (String[])var1.toArray(FileSystemPreferences.EMPTY_STRING_ARRAY);
         }
      });
   }

   protected AbstractPreferences childSpi(String var1) {
      return new FileSystemPreferences(this, var1);
   }

   public void removeNode() throws BackingStoreException {
      synchronized(this.isUserNode() ? userLockFile : systemLockFile) {
         if (!this.lockFile(false)) {
            throw new BackingStoreException("Couldn't get file lock.");
         } else {
            try {
               super.removeNode();
            } finally {
               this.unlockFile();
            }

         }
      }
   }

   protected void removeNodeSpi() throws BackingStoreException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws BackingStoreException {
               if (FileSystemPreferences.this.changeLog.contains(FileSystemPreferences.this.nodeCreate)) {
                  FileSystemPreferences.this.changeLog.remove(FileSystemPreferences.this.nodeCreate);
                  FileSystemPreferences.this.nodeCreate = null;
                  return null;
               } else if (!FileSystemPreferences.this.dir.exists()) {
                  return null;
               } else {
                  FileSystemPreferences.this.prefsFile.delete();
                  FileSystemPreferences.this.tmpFile.delete();
                  File[] var1 = FileSystemPreferences.this.dir.listFiles();
                  if (var1.length != 0) {
                     FileSystemPreferences.getLogger().warning("Found extraneous files when removing node: " + Arrays.asList(var1));

                     for(int var2 = 0; var2 < var1.length; ++var2) {
                        var1[var2].delete();
                     }
                  }

                  if (!FileSystemPreferences.this.dir.delete()) {
                     throw new BackingStoreException("Couldn't delete dir: " + FileSystemPreferences.this.dir);
                  } else {
                     return null;
                  }
               }
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (BackingStoreException)var2.getException();
      }
   }

   public synchronized void sync() throws BackingStoreException {
      boolean var1 = this.isUserNode();
      boolean var2;
      if (var1) {
         var2 = false;
      } else {
         var2 = !isSystemRootWritable;
      }

      synchronized(this.isUserNode() ? userLockFile : systemLockFile) {
         if (!this.lockFile(var2)) {
            throw new BackingStoreException("Couldn't get file lock.");
         } else {
            final Long var4 = (Long)AccessController.doPrivileged(new PrivilegedAction<Long>() {
               public Long run() {
                  long var1;
                  if (FileSystemPreferences.this.isUserNode()) {
                     var1 = FileSystemPreferences.userRootModFile.lastModified();
                     FileSystemPreferences.isUserRootModified = FileSystemPreferences.userRootModTime == var1;
                  } else {
                     var1 = FileSystemPreferences.systemRootModFile.lastModified();
                     FileSystemPreferences.isSystemRootModified = FileSystemPreferences.systemRootModTime == var1;
                  }

                  return new Long(var1);
               }
            });

            try {
               super.sync();
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     if (FileSystemPreferences.this.isUserNode()) {
                        FileSystemPreferences.userRootModTime = var4 + 1000L;
                        FileSystemPreferences.userRootModFile.setLastModified(FileSystemPreferences.userRootModTime);
                     } else {
                        FileSystemPreferences.systemRootModTime = var4 + 1000L;
                        FileSystemPreferences.systemRootModFile.setLastModified(FileSystemPreferences.systemRootModTime);
                     }

                     return null;
                  }
               });
            } finally {
               this.unlockFile();
            }

         }
      }
   }

   protected void syncSpi() throws BackingStoreException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws BackingStoreException {
               FileSystemPreferences.this.syncSpiPrivileged();
               return null;
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (BackingStoreException)var2.getException();
      }
   }

   private void syncSpiPrivileged() throws BackingStoreException {
      if (this.isRemoved()) {
         throw new IllegalStateException("Node has been removed");
      } else if (this.prefsCache != null) {
         long var1;
         label36: {
            label35: {
               if (this.isUserNode()) {
                  if (!isUserRootModified) {
                     break label35;
                  }
               } else if (!isSystemRootModified) {
                  break label35;
               }

               var1 = this.prefsFile.lastModified();
               if (var1 != this.lastSyncTime) {
                  this.loadCache();
                  this.replayChanges();
                  this.lastSyncTime = var1;
               }
               break label36;
            }

            if (this.lastSyncTime != 0L && !this.dir.exists()) {
               this.prefsCache = new TreeMap();
               this.replayChanges();
            }
         }

         if (!this.changeLog.isEmpty()) {
            this.writeBackCache();
            var1 = this.prefsFile.lastModified();
            if (this.lastSyncTime <= var1) {
               this.lastSyncTime = var1 + 1000L;
               this.prefsFile.setLastModified(this.lastSyncTime);
            }

            this.changeLog.clear();
         }

      }
   }

   public void flush() throws BackingStoreException {
      if (!this.isRemoved()) {
         this.sync();
      }
   }

   protected void flushSpi() throws BackingStoreException {
   }

   private static boolean isDirChar(char var0) {
      return var0 > 31 && var0 < 127 && var0 != '/' && var0 != '.' && var0 != '_';
   }

   private static String dirName(String var0) {
      int var1 = 0;

      for(int var2 = var0.length(); var1 < var2; ++var1) {
         if (!isDirChar(var0.charAt(var1))) {
            return "_" + Base64.byteArrayToAltBase64(byteArray(var0));
         }
      }

      return var0;
   }

   private static byte[] byteArray(String var0) {
      int var1 = var0.length();
      byte[] var2 = new byte[2 * var1];
      int var3 = 0;

      for(int var4 = 0; var3 < var1; ++var3) {
         char var5 = var0.charAt(var3);
         var2[var4++] = (byte)(var5 >> 8);
         var2[var4++] = (byte)var5;
      }

      return var2;
   }

   private static String nodeName(String var0) {
      if (var0.charAt(0) != '_') {
         return var0;
      } else {
         byte[] var1 = Base64.altBase64ToByteArray(var0.substring(1));
         StringBuffer var2 = new StringBuffer(var1.length / 2);
         int var3 = 0;

         while(var3 < var1.length) {
            int var4 = var1[var3++] & 255;
            int var5 = var1[var3++] & 255;
            var2.append((char)(var4 << 8 | var5));
         }

         return var2.toString();
      }
   }

   private boolean lockFile(boolean var1) throws SecurityException {
      boolean var2 = this.isUserNode();
      int var4 = 0;
      File var5 = var2 ? userLockFile : systemLockFile;
      long var6 = (long)INIT_SLEEP_TIME;

      for(int var8 = 0; var8 < MAX_ATTEMPTS; ++var8) {
         try {
            int var9 = var2 ? 384 : 420;
            int[] var3 = lockFile0(var5.getCanonicalPath(), var9, var1);
            var4 = var3[1];
            if (var3[0] != 0) {
               if (var2) {
                  userRootLockHandle = var3[0];
               } else {
                  systemRootLockHandle = var3[0];
               }

               return true;
            }
         } catch (IOException var11) {
         }

         try {
            Thread.sleep(var6);
         } catch (InterruptedException var10) {
            this.checkLockFile0ErrorCode(var4);
            return false;
         }

         var6 *= 2L;
      }

      this.checkLockFile0ErrorCode(var4);
      return false;
   }

   private void checkLockFile0ErrorCode(int var1) throws SecurityException {
      if (var1 == 13) {
         throw new SecurityException("Could not lock " + (this.isUserNode() ? "User prefs." : "System prefs.") + " Lock file access denied.");
      } else {
         if (var1 != 11) {
            getLogger().warning("Could not lock " + (this.isUserNode() ? "User prefs. " : "System prefs.") + " Unix error code " + var1 + ".");
         }

      }
   }

   private static native int[] lockFile0(String var0, int var1, boolean var2);

   private static native int unlockFile0(int var0);

   private static native int chmod(String var0, int var1);

   private void unlockFile() {
      boolean var2 = this.isUserNode();
      File var3 = var2 ? userLockFile : systemLockFile;
      int var4 = var2 ? userRootLockHandle : systemRootLockHandle;
      if (var4 == 0) {
         getLogger().warning("Unlock: zero lockHandle for " + (var2 ? "user" : "system") + " preferences.)");
      } else {
         int var1 = unlockFile0(var4);
         if (var1 != 0) {
            getLogger().warning("Could not drop file-lock on " + (this.isUserNode() ? "user" : "system") + " preferences. Unix error code " + var1 + ".");
            if (var1 == 13) {
               throw new SecurityException("Could not unlock" + (this.isUserNode() ? "User prefs." : "System prefs.") + " Lock file access denied.");
            }
         }

         if (this.isUserNode()) {
            userRootLockHandle = 0;
         } else {
            systemRootLockHandle = 0;
         }

      }
   }

   static {
      syncTimer.schedule(new TimerTask() {
         public void run() {
            FileSystemPreferences.syncWorld();
         }
      }, (long)(SYNC_INTERVAL * 1000), (long)(SYNC_INTERVAL * 1000));
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            Runtime.getRuntime().addShutdownHook(new Thread() {
               public void run() {
                  FileSystemPreferences.syncTimer.cancel();
                  FileSystemPreferences.syncWorld();
               }
            });
            return null;
         }
      });
      EMPTY_STRING_ARRAY = new String[0];
      INIT_SLEEP_TIME = 50;
      MAX_ATTEMPTS = 5;
   }

   private class NodeCreate extends FileSystemPreferences.Change {
      private NodeCreate() {
         super(null);
      }

      void replay() {
      }

      // $FF: synthetic method
      NodeCreate(Object var2) {
         this();
      }
   }

   private class Remove extends FileSystemPreferences.Change {
      String key;

      Remove(String var2) {
         super(null);
         this.key = var2;
      }

      void replay() {
         FileSystemPreferences.this.prefsCache.remove(this.key);
      }
   }

   private class Put extends FileSystemPreferences.Change {
      String key;
      String value;

      Put(String var2, String var3) {
         super(null);
         this.key = var2;
         this.value = var3;
      }

      void replay() {
         FileSystemPreferences.this.prefsCache.put(this.key, this.value);
      }
   }

   private abstract class Change {
      private Change() {
      }

      abstract void replay();

      // $FF: synthetic method
      Change(Object var2) {
         this();
      }
   }
}
