package com.sun.corba.se.impl.naming.cosnaming;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.omg.CosNaming.NameComponent;

public class NamingUtils {
   public static boolean debug = false;
   public static PrintStream debugStream;
   public static PrintStream errStream;

   private NamingUtils() {
   }

   public static void dprint(String var0) {
      if (debug && debugStream != null) {
         debugStream.println(var0);
      }

   }

   public static void errprint(String var0) {
      if (errStream != null) {
         errStream.println(var0);
      } else {
         System.err.println(var0);
      }

   }

   public static void printException(Exception var0) {
      if (errStream != null) {
         var0.printStackTrace(errStream);
      } else {
         var0.printStackTrace();
      }

   }

   public static void makeDebugStream(File var0) throws IOException {
      FileOutputStream var1 = new FileOutputStream(var0);
      DataOutputStream var2 = new DataOutputStream(var1);
      debugStream = new PrintStream(var2);
      debugStream.println("Debug Stream Enabled.");
   }

   public static void makeErrStream(File var0) throws IOException {
      if (debug) {
         FileOutputStream var1 = new FileOutputStream(var0);
         DataOutputStream var2 = new DataOutputStream(var1);
         errStream = new PrintStream(var2);
         dprint("Error stream setup completed.");
      }

   }

   static String getDirectoryStructuredName(NameComponent[] var0) {
      StringBuffer var1 = new StringBuffer("/");

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1.append(var0[var2].id + "." + var0[var2].kind);
      }

      return var1.toString();
   }
}
