package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Properties;
import org.omg.CORBA.ORB;

public class ServerMain {
   public static final int OK = 0;
   public static final int MAIN_CLASS_NOT_FOUND = 1;
   public static final int NO_MAIN_METHOD = 2;
   public static final int APPLICATION_ERROR = 3;
   public static final int UNKNOWN_ERROR = 4;
   public static final int NO_SERVER_ID = 5;
   public static final int REGISTRATION_FAILED = 6;
   private static final boolean debug = false;

   public static String printResult(int var0) {
      switch(var0) {
      case 0:
         return "Server terminated normally";
      case 1:
         return "main class not found";
      case 2:
         return "no main method";
      case 3:
         return "application error";
      case 4:
      default:
         return "unknown error";
      case 5:
         return "server ID not defined";
      case 6:
         return "server registration failed";
      }
   }

   private void redirectIOStreams() {
      try {
         String var1 = System.getProperty("com.sun.CORBA.activation.DbDir") + System.getProperty("file.separator") + "logs" + System.getProperty("file.separator");
         new File(var1);
         String var3 = System.getProperty("com.sun.CORBA.POA.ORBServerId");
         FileOutputStream var4 = new FileOutputStream(var1 + var3 + ".out", true);
         FileOutputStream var5 = new FileOutputStream(var1 + var3 + ".err", true);
         PrintStream var6 = new PrintStream(var4, true);
         PrintStream var7 = new PrintStream(var5, true);
         System.setOut(var6);
         System.setErr(var7);
         logInformation("Server started");
      } catch (Exception var8) {
      }

   }

   private static void writeLogMessage(PrintStream var0, String var1) {
      Date var2 = new Date();
      var0.print("[" + var2.toString() + "] " + var1 + "\n");
   }

   public static void logInformation(String var0) {
      writeLogMessage(System.out, "        " + var0);
   }

   public static void logError(String var0) {
      writeLogMessage(System.out, "ERROR:  " + var0);
      writeLogMessage(System.err, "ERROR:  " + var0);
   }

   public static void logTerminal(String var0, int var1) {
      if (var1 == 0) {
         writeLogMessage(System.out, "        " + var0);
      } else {
         writeLogMessage(System.out, "FATAL:  " + printResult(var1) + ": " + var0);
         writeLogMessage(System.err, "FATAL:  " + printResult(var1) + ": " + var0);
      }

      System.exit(var1);
   }

   private Method getMainMethod(Class var1) {
      Class[] var2 = new Class[]{String[].class};
      Method var3 = null;

      try {
         var3 = var1.getDeclaredMethod("main", var2);
      } catch (Exception var5) {
         logTerminal(var5.getMessage(), 2);
      }

      if (!this.isPublicStaticVoid(var3)) {
         logTerminal("", 2);
      }

      return var3;
   }

   private boolean isPublicStaticVoid(Method var1) {
      int var2 = var1.getModifiers();
      if (Modifier.isPublic(var2) && Modifier.isStatic(var2)) {
         if (var1.getExceptionTypes().length != 0) {
            logError(var1.getName() + " declares exceptions");
            return false;
         } else if (!var1.getReturnType().equals(Void.TYPE)) {
            logError(var1.getName() + " does not have a void return type");
            return false;
         } else {
            return true;
         }
      } else {
         logError(var1.getName() + " is not public static");
         return false;
      }
   }

   private Method getNamedMethod(Class var1, String var2) {
      Class[] var3 = new Class[]{ORB.class};
      Method var4 = null;

      try {
         var4 = var1.getDeclaredMethod(var2, var3);
      } catch (Exception var6) {
         return null;
      }

      return !this.isPublicStaticVoid(var4) ? null : var4;
   }

   private void run(String[] var1) {
      try {
         this.redirectIOStreams();
         String var2 = System.getProperty("com.sun.CORBA.POA.ORBServerName");
         ClassLoader var3 = Thread.currentThread().getContextClassLoader();
         if (var3 == null) {
            var3 = ClassLoader.getSystemClassLoader();
         }

         Class var4 = null;

         try {
            var4 = Class.forName(var2);
         } catch (ClassNotFoundException var8) {
            var4 = Class.forName(var2, true, var3);
         }

         Method var5 = this.getMainMethod(var4);
         boolean var6 = Boolean.getBoolean("com.sun.CORBA.activation.ORBServerVerify");
         if (var6) {
            if (var5 == null) {
               logTerminal("", 2);
            } else {
               logTerminal("", 0);
            }
         }

         this.registerCallback(var4);
         Object[] var7 = new Object[]{var1};
         var5.invoke((Object)null, var7);
      } catch (ClassNotFoundException var9) {
         logTerminal("ClassNotFound exception: " + var9.getMessage(), 1);
      } catch (Exception var10) {
         logTerminal("Exception: " + var10.getMessage(), 3);
      }

   }

   public static void main(String[] var0) {
      ServerMain var1 = new ServerMain();
      var1.run(var0);
   }

   private int getServerId() {
      Integer var1 = Integer.getInteger("com.sun.CORBA.POA.ORBServerId");
      if (var1 == null) {
         logTerminal("", 5);
      }

      return var1;
   }

   private void registerCallback(Class var1) {
      Method var2 = this.getNamedMethod(var1, "install");
      Method var3 = this.getNamedMethod(var1, "uninstall");
      Method var4 = this.getNamedMethod(var1, "shutdown");
      Properties var5 = new Properties();
      var5.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
      var5.put("com.sun.CORBA.POA.ORBActivated", "false");
      Object var6 = null;
      ORB var7 = ORB.init((String[])var6, var5);
      ServerCallback var8 = new ServerCallback(var7, var2, var3, var4);
      int var9 = this.getServerId();

      try {
         Activator var10 = ActivatorHelper.narrow(var7.resolve_initial_references("ServerActivator"));
         var10.active(var9, var8);
      } catch (Exception var11) {
         logTerminal("exception " + var11.getMessage(), 6);
      }

   }
}
