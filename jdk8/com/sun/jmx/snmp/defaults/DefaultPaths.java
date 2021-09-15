package com.sun.jmx.snmp.defaults;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DefaultPaths {
   private static final String INSTALL_PATH_RESOURCE_NAME = "com/sun/jdmk/defaults/install.path";
   private static String etcDir;
   private static String tmpDir;
   private static String installDir;

   private DefaultPaths() {
   }

   public static String getInstallDir() {
      return installDir == null ? useRessourceFile() : installDir;
   }

   public static String getInstallDir(String var0) {
      if (installDir == null) {
         return var0 == null ? getInstallDir() : getInstallDir() + File.separator + var0;
      } else {
         return var0 == null ? installDir : installDir + File.separator + var0;
      }
   }

   public static void setInstallDir(String var0) {
      installDir = var0;
   }

   public static String getEtcDir() {
      return etcDir == null ? getInstallDir("etc") : etcDir;
   }

   public static String getEtcDir(String var0) {
      if (etcDir == null) {
         return var0 == null ? getEtcDir() : getEtcDir() + File.separator + var0;
      } else {
         return var0 == null ? etcDir : etcDir + File.separator + var0;
      }
   }

   public static void setEtcDir(String var0) {
      etcDir = var0;
   }

   public static String getTmpDir() {
      return tmpDir == null ? getInstallDir("tmp") : tmpDir;
   }

   public static String getTmpDir(String var0) {
      if (tmpDir == null) {
         return var0 == null ? getTmpDir() : getTmpDir() + File.separator + var0;
      } else {
         return var0 == null ? tmpDir : tmpDir + File.separator + var0;
      }
   }

   public static void setTmpDir(String var0) {
      tmpDir = var0;
   }

   private static String useRessourceFile() {
      InputStream var0 = null;
      BufferedReader var1 = null;

      try {
         var0 = DefaultPaths.class.getClassLoader().getResourceAsStream("com/sun/jdmk/defaults/install.path");
         if (var0 == null) {
            Object var2 = null;
            return (String)var2;
         }

         var1 = new BufferedReader(new InputStreamReader(var0));
         installDir = var1.readLine();
      } catch (Exception var13) {
      } finally {
         try {
            if (var0 != null) {
               var0.close();
            }

            if (var1 != null) {
               var1.close();
            }
         } catch (Exception var12) {
         }

      }

      return installDir;
   }
}
