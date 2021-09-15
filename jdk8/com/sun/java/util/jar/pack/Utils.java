package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import sun.util.logging.PlatformLogger;

class Utils {
   static final String COM_PREFIX = "com.sun.java.util.jar.pack.";
   static final String METAINF = "META-INF";
   static final String DEBUG_VERBOSE = "com.sun.java.util.jar.pack.verbose";
   static final String DEBUG_DISABLE_NATIVE = "com.sun.java.util.jar.pack.disable.native";
   static final String PACK_DEFAULT_TIMEZONE = "com.sun.java.util.jar.pack.default.timezone";
   static final String UNPACK_MODIFICATION_TIME = "com.sun.java.util.jar.pack.unpack.modification.time";
   static final String UNPACK_STRIP_DEBUG = "com.sun.java.util.jar.pack.unpack.strip.debug";
   static final String UNPACK_REMOVE_PACKFILE = "com.sun.java.util.jar.pack.unpack.remove.packfile";
   static final String NOW = "now";
   static final String PACK_KEEP_CLASS_ORDER = "com.sun.java.util.jar.pack.keep.class.order";
   static final String PACK_ZIP_ARCHIVE_MARKER_COMMENT = "PACK200";
   static final String CLASS_FORMAT_ERROR = "com.sun.java.util.jar.pack.class.format.error";
   static final ThreadLocal<TLGlobals> currentInstance = new ThreadLocal();
   private static TimeZone tz;
   private static int workingPackerCount = 0;
   static final boolean nolog = Boolean.getBoolean("com.sun.java.util.jar.pack.nolog");
   static final boolean SORT_MEMBERS_DESCR_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.members.descr.major");
   static final boolean SORT_HANDLES_KIND_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.handles.kind.major");
   static final boolean SORT_INDY_BSS_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.indy.bss.major");
   static final boolean SORT_BSS_BSM_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.bss.bsm.major");
   static final Utils.Pack200Logger log = new Utils.Pack200Logger("java.util.jar.Pack200");

   static TLGlobals getTLGlobals() {
      return (TLGlobals)currentInstance.get();
   }

   static PropMap currentPropMap() {
      Object var0 = currentInstance.get();
      if (var0 instanceof PackerImpl) {
         return ((PackerImpl)var0).props;
      } else {
         return var0 instanceof UnpackerImpl ? ((UnpackerImpl)var0).props : null;
      }
   }

   static synchronized void changeDefaultTimeZoneToUtc() {
      if (workingPackerCount++ == 0) {
         tz = TimeZone.getDefault();
         TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
      }

   }

   static synchronized void restoreDefaultTimeZone() {
      if (--workingPackerCount == 0) {
         if (tz != null) {
            TimeZone.setDefault(tz);
         }

         tz = null;
      }

   }

   static String getVersionString() {
      return "Pack200, Vendor: " + System.getProperty("java.vendor") + ", Version: " + Constants.MAX_PACKAGE_VERSION;
   }

   static void markJarFile(JarOutputStream var0) throws IOException {
      var0.setComment("PACK200");
   }

   static void copyJarFile(JarInputStream var0, JarOutputStream var1) throws IOException {
      if (var0.getManifest() != null) {
         ZipEntry var2 = new ZipEntry("META-INF/MANIFEST.MF");
         var1.putNextEntry(var2);
         var0.getManifest().write(var1);
         var1.closeEntry();
      }

      byte[] var5 = new byte[16384];

      JarEntry var3;
      while((var3 = var0.getNextJarEntry()) != null) {
         var1.putNextEntry(var3);

         int var4;
         while(0 < (var4 = var0.read(var5))) {
            var1.write(var5, 0, var4);
         }
      }

      var0.close();
      markJarFile(var1);
   }

   static void copyJarFile(JarFile var0, JarOutputStream var1) throws IOException {
      byte[] var2 = new byte[16384];
      Iterator var3 = Collections.list(var0.entries()).iterator();

      while(var3.hasNext()) {
         JarEntry var4 = (JarEntry)var3.next();
         var1.putNextEntry(var4);
         InputStream var5 = var0.getInputStream(var4);

         int var6;
         while(0 < (var6 = var5.read(var2))) {
            var1.write(var2, 0, var6);
         }
      }

      var0.close();
      markJarFile(var1);
   }

   static void copyJarFile(JarInputStream var0, OutputStream var1) throws IOException {
      BufferedOutputStream var14 = new BufferedOutputStream(var1);
      Utils.NonCloser var15 = new Utils.NonCloser(var14);
      JarOutputStream var2 = new JarOutputStream(var15);
      Throwable var3 = null;

      try {
         copyJarFile(var0, var2);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   static void copyJarFile(JarFile var0, OutputStream var1) throws IOException {
      BufferedOutputStream var14 = new BufferedOutputStream(var1);
      Utils.NonCloser var15 = new Utils.NonCloser(var14);
      JarOutputStream var2 = new JarOutputStream(var15);
      Throwable var3 = null;

      try {
         copyJarFile(var0, var2);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   static String getJarEntryName(String var0) {
      return var0 == null ? null : var0.replace(File.separatorChar, '/');
   }

   static String zeString(ZipEntry var0) {
      int var1 = var0.getCompressedSize() > 0L ? (int)((1.0D - (double)var0.getCompressedSize() / (double)var0.getSize()) * 100.0D) : 0;
      return var0.getSize() + "\t" + var0.getMethod() + "\t" + var0.getCompressedSize() + "\t" + var1 + "%\t" + new Date(var0.getTime()) + "\t" + Long.toHexString(var0.getCrc()) + "\t" + var0.getName();
   }

   static byte[] readMagic(BufferedInputStream var0) throws IOException {
      var0.mark(4);
      byte[] var1 = new byte[4];

      for(int var2 = 0; var2 < var1.length && 1 == var0.read(var1, var2, 1); ++var2) {
      }

      var0.reset();
      return var1;
   }

   static boolean isJarMagic(byte[] var0) {
      return var0[0] == 80 && var0[1] == 75 && var0[2] >= 1 && var0[2] < 8 && var0[3] == var0[2] + 1;
   }

   static boolean isPackMagic(byte[] var0) {
      return var0[0] == -54 && var0[1] == -2 && var0[2] == -48 && var0[3] == 13;
   }

   static boolean isGZIPMagic(byte[] var0) {
      return var0[0] == 31 && var0[1] == -117 && var0[2] == 8;
   }

   private Utils() {
   }

   private static class NonCloser extends FilterOutputStream {
      NonCloser(OutputStream var1) {
         super(var1);
      }

      public void close() throws IOException {
         this.flush();
      }
   }

   static class Pack200Logger {
      private final String name;
      private PlatformLogger log;

      Pack200Logger(String var1) {
         this.name = var1;
      }

      private synchronized PlatformLogger getLogger() {
         if (this.log == null) {
            this.log = PlatformLogger.getLogger(this.name);
         }

         return this.log;
      }

      public void warning(String var1, Object var2) {
         this.getLogger().warning(var1, var2);
      }

      public void warning(String var1) {
         this.warning(var1, (Object)null);
      }

      public void info(String var1) {
         int var2 = Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
         if (var2 > 0) {
            if (Utils.nolog) {
               System.out.println(var1);
            } else {
               this.getLogger().info(var1);
            }
         }

      }

      public void fine(String var1) {
         int var2 = Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
         if (var2 > 0) {
            System.out.println(var1);
         }

      }
   }
}
