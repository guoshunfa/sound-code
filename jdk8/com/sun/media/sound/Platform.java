package com.sun.media.sound;

import java.security.AccessController;
import java.util.StringTokenizer;

final class Platform {
   private static final String libNameMain = "jsound";
   private static final String libNameALSA = "jsoundalsa";
   private static final String libNameDSound = "jsoundds";
   public static final int LIB_MAIN = 1;
   public static final int LIB_ALSA = 2;
   public static final int LIB_DSOUND = 4;
   private static int loadedLibs = 0;
   public static final int FEATURE_MIDIIO = 1;
   public static final int FEATURE_PORTS = 2;
   public static final int FEATURE_DIRECT_AUDIO = 3;
   private static boolean signed8;
   private static boolean bigEndian;

   private Platform() {
   }

   static void initialize() {
   }

   static boolean isBigEndian() {
      return bigEndian;
   }

   static boolean isSigned8() {
      return signed8;
   }

   private static void loadLibraries() {
      AccessController.doPrivileged(() -> {
         System.loadLibrary("jsound");
         return null;
      });
      loadedLibs |= 1;
      String var0 = nGetExtraLibraries();
      StringTokenizer var1 = new StringTokenizer(var0);

      while(var1.hasMoreTokens()) {
         String var2 = var1.nextToken();

         try {
            AccessController.doPrivileged(() -> {
               System.loadLibrary(var2);
               return null;
            });
            if (var2.equals("jsoundalsa")) {
               loadedLibs |= 2;
            } else if (var2.equals("jsoundds")) {
               loadedLibs |= 4;
            }
         } catch (Throwable var4) {
         }
      }

   }

   static boolean isMidiIOEnabled() {
      return isFeatureLibLoaded(1);
   }

   static boolean isPortsEnabled() {
      return isFeatureLibLoaded(2);
   }

   static boolean isDirectAudioEnabled() {
      return isFeatureLibLoaded(3);
   }

   private static boolean isFeatureLibLoaded(int var0) {
      int var1 = nGetLibraryForFeature(var0);
      boolean var2 = var1 != 0 && (loadedLibs & var1) == var1;
      return var2;
   }

   private static native boolean nIsBigEndian();

   private static native boolean nIsSigned8();

   private static native String nGetExtraLibraries();

   private static native int nGetLibraryForFeature(int var0);

   private static void readProperties() {
      bigEndian = nIsBigEndian();
      signed8 = nIsSigned8();
   }

   static {
      loadLibraries();
      readProperties();
   }
}
