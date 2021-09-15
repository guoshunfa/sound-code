package com.sun.org.apache.xml.internal.security.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecurityPermission;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtils {
   private static Logger log = Logger.getLogger(JavaUtils.class.getName());
   private static final SecurityPermission REGISTER_PERMISSION = new SecurityPermission("com.sun.org.apache.xml.internal.security.register");

   private JavaUtils() {
   }

   public static byte[] getBytesFromFile(String var0) throws FileNotFoundException, IOException {
      Object var1 = null;
      FileInputStream var2 = null;
      UnsyncByteArrayOutputStream var3 = null;

      try {
         var2 = new FileInputStream(var0);
         var3 = new UnsyncByteArrayOutputStream();
         byte[] var4 = new byte[1024];

         int var5;
         while((var5 = var2.read(var4)) > 0) {
            var3.write(var4, 0, var5);
         }

         byte[] var9 = var3.toByteArray();
         return var9;
      } finally {
         if (var3 != null) {
            var3.close();
         }

         if (var2 != null) {
            var2.close();
         }

      }
   }

   public static void writeBytesToFilename(String var0, byte[] var1) {
      FileOutputStream var2 = null;

      try {
         if (var0 != null && var1 != null) {
            File var3 = new File(var0);
            var2 = new FileOutputStream(var3);
            var2.write(var1);
            var2.close();
         } else if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "writeBytesToFilename got null byte[] pointed");
         }
      } catch (IOException var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var5) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)var5.getMessage(), (Throwable)var5);
               }
            }
         }
      }

   }

   public static byte[] getBytesFromStream(InputStream var0) throws IOException {
      UnsyncByteArrayOutputStream var1 = null;
      Object var2 = null;

      try {
         var1 = new UnsyncByteArrayOutputStream();
         byte[] var3 = new byte[4096];

         int var4;
         while((var4 = var0.read(var3)) > 0) {
            var1.write(var3, 0, var4);
         }

         byte[] var8 = var1.toByteArray();
         return var8;
      } finally {
         var1.close();
      }
   }

   public static byte[] convertDsaASN1toXMLDSIG(byte[] var0, int var1) throws IOException {
      if (var0[0] == 48 && var0[1] == var0.length - 2 && var0[2] == 2) {
         byte var2 = var0[3];

         int var3;
         for(var3 = var2; var3 > 0 && var0[4 + var2 - var3] == 0; --var3) {
         }

         byte var4 = var0[5 + var2];

         int var5;
         for(var5 = var4; var5 > 0 && var0[6 + var2 + var4 - var5] == 0; --var5) {
         }

         if (var3 <= var1 && var0[4 + var2] == 2 && var5 <= var1) {
            byte[] var6 = new byte[var1 * 2];
            System.arraycopy(var0, 4 + var2 - var3, var6, var1 - var3, var3);
            System.arraycopy(var0, 6 + var2 + var4 - var5, var6, var1 * 2 - var5, var5);
            return var6;
         } else {
            throw new IOException("Invalid ASN.1 format of DSA signature");
         }
      } else {
         throw new IOException("Invalid ASN.1 format of DSA signature");
      }
   }

   public static byte[] convertDsaXMLDSIGtoASN1(byte[] var0, int var1) throws IOException {
      int var2 = var1 * 2;
      if (var0.length != var2) {
         throw new IOException("Invalid XMLDSIG format of DSA signature");
      } else {
         int var3;
         for(var3 = var1; var3 > 0 && var0[var1 - var3] == 0; --var3) {
         }

         int var4 = var3;
         if (var0[var1 - var3] < 0) {
            var4 = var3 + 1;
         }

         int var5;
         for(var5 = var1; var5 > 0 && var0[var2 - var5] == 0; --var5) {
         }

         int var6 = var5;
         if (var0[var2 - var5] < 0) {
            var6 = var5 + 1;
         }

         byte[] var7 = new byte[6 + var4 + var6];
         var7[0] = 48;
         var7[1] = (byte)(4 + var4 + var6);
         var7[2] = 2;
         var7[3] = (byte)var4;
         System.arraycopy(var0, var1 - var3, var7, 4 + var4 - var3, var3);
         var7[4 + var4] = 2;
         var7[5 + var4] = (byte)var6;
         System.arraycopy(var0, var2 - var5, var7, 6 + var4 + var6 - var5, var5);
         return var7;
      }
   }

   public static void checkRegisterPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(REGISTER_PERMISSION);
      }

   }
}
