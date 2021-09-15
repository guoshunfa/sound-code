package com.sun.corba.se.impl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ORBProperties {
   public static final String ORB_CLASS = "org.omg.CORBA.ORBClass=com.sun.corba.se.impl.orb.ORBImpl";
   public static final String ORB_SINGLETON_CLASS = "org.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton";

   public static void main(String[] var0) {
      try {
         String var1 = System.getProperty("java.home");
         File var2 = new File(var1 + File.separator + "lib" + File.separator + "orb.properties");
         if (var2.exists()) {
            return;
         }

         FileOutputStream var3 = new FileOutputStream(var2);
         PrintWriter var4 = new PrintWriter(var3);

         try {
            var4.println("org.omg.CORBA.ORBClass=com.sun.corba.se.impl.orb.ORBImpl");
            var4.println("org.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton");
         } finally {
            var4.close();
            var3.close();
         }
      } catch (Exception var9) {
      }

   }
}
