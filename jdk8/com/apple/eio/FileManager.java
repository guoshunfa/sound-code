package com.apple.eio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class FileManager {
   public static final short kOnAppropriateDisk = -32767;
   public static final short kSystemDomain = -32766;
   public static final short kLocalDomain = -32765;
   public static final short kNetworkDomain = -32764;
   public static final short kUserDomain = -32763;

   public static int OSTypeToInt(String var0) {
      int var1 = 0;
      byte[] var2 = new byte[]{0, 0, 0, 0};
      int var3 = var0.length();
      if (var3 > 0) {
         if (var3 > 4) {
            var3 = 4;
         }

         var0.getBytes(0, var3, var2, 4 - var3);
      }

      for(int var4 = 0; var4 < var3; ++var4) {
         if (var4 > 0) {
            var1 <<= 8;
         }

         var1 |= var2[var4] & 255;
      }

      return var1;
   }

   public static void setFileTypeAndCreator(String var0, int var1, int var2) throws IOException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkWrite(var0);
      }

      _setFileTypeAndCreator(var0, var1, var2);
   }

   private static native void _setFileTypeAndCreator(String var0, int var1, int var2) throws IOException;

   public static void setFileType(String var0, int var1) throws IOException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkWrite(var0);
      }

      _setFileType(var0, var1);
   }

   private static native void _setFileType(String var0, int var1) throws IOException;

   public static void setFileCreator(String var0, int var1) throws IOException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkWrite(var0);
      }

      _setFileCreator(var0, var1);
   }

   private static native void _setFileCreator(String var0, int var1) throws IOException;

   public static int getFileType(String var0) throws IOException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(var0);
      }

      return _getFileType(var0);
   }

   private static native int _getFileType(String var0) throws IOException;

   public static int getFileCreator(String var0) throws IOException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(var0);
      }

      return _getFileCreator(var0);
   }

   private static native int _getFileCreator(String var0) throws IOException;

   public static String findFolder(int var0) throws FileNotFoundException {
      return findFolder((short)-32767, var0);
   }

   public static String findFolder(short var0, int var1) throws FileNotFoundException {
      return findFolder(var0, var1, false);
   }

   public static String findFolder(short var0, int var1, boolean var2) throws FileNotFoundException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(new RuntimePermission("canExamineFileSystem"));
      }

      String var4 = _findFolder(var0, var1, var2);
      if (var4 == null) {
         throw new FileNotFoundException("Can't find folder: " + Integer.toHexString(var1));
      } else {
         return var4;
      }
   }

   private static native String _findFolder(short var0, int var1, boolean var2);

   /** @deprecated */
   @Deprecated
   public static void openURL(String var0) throws IOException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("canOpenURLs"));
      }

      _openURL(var0);
   }

   private static native void _openURL(String var0) throws IOException;

   public static String getResource(String var0) throws FileNotFoundException {
      return getResourceFromBundle(var0, (String)null, (String)null);
   }

   public static String getResource(String var0, String var1) throws FileNotFoundException {
      return getResourceFromBundle(var0, var1, (String)null);
   }

   public static String getResource(String var0, String var1, String var2) throws FileNotFoundException {
      return getResourceFromBundle(var0, var1, var2);
   }

   private static native String getNativeResourceFromBundle(String var0, String var1, String var2) throws FileNotFoundException;

   private static String getResourceFromBundle(String var0, String var1, String var2) throws FileNotFoundException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(new RuntimePermission("canReadBundle"));
      }

      String var4 = getNativeResourceFromBundle(var0, var1, var2);
      if (var4 == null) {
         throw new FileNotFoundException(var0);
      } else {
         return var4;
      }
   }

   public static String getPathToApplicationBundle() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("canReadBundle"));
      }

      return getNativePathToApplicationBundle();
   }

   private static native String getNativePathToApplicationBundle();

   public static boolean moveToTrash(File var0) throws FileNotFoundException {
      if (var0 != null && var0.exists()) {
         String var1 = var0.getAbsolutePath();
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkWrite(var1);
         }

         return _moveToTrash(var1);
      } else {
         throw new FileNotFoundException();
      }
   }

   private static native boolean _moveToTrash(String var0);

   public static boolean revealInFinder(File var0) throws FileNotFoundException {
      if (var0 != null && var0.exists()) {
         String var1 = var0.getAbsolutePath();
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkRead(var1);
         }

         return _revealInFinder(var1);
      } else {
         throw new FileNotFoundException();
      }
   }

   private static native boolean _revealInFinder(String var0);

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osx");
            return null;
         }
      });
   }
}
