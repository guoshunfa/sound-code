package apple.launcher;

import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.JOptionPane;
import sun.misc.Launcher;

class JavaAppLauncher implements Runnable {
   static final String kJavaFailureMainClassNotSpecified = "MainClassNotSpecified";
   static final String kJavaFailureMainClassNotFound = "CannotLoadMainClass";
   static final String kJavaFailureMainClassHasNoMain = "NoMainMethod";
   static final String kJavaFailureMainClassMainNotStatic = "MainNotStatic";
   static final String kJavaFailureMainThrewException = "MainThrewException";
   static final String kJavaFailureMainInitializerException = "MainInitializerException";
   final boolean verbose;
   final Map<String, ?> javaDictionary;

   private static native <T> T nativeConvertAndRelease(long var0);

   private static native void nativeInvokeNonPublic(Class<? extends Method> var0, Method var1, String[] var2);

   static void launch(long var0, boolean var2) {
      Map var3 = (Map)nativeConvertAndRelease(var0);
      (new JavaAppLauncher(var3, var2)).run();
   }

   JavaAppLauncher(Map<String, ?> var1, boolean var2) {
      this.verbose = var2;
      this.javaDictionary = var1;
   }

   public void run() {
      Method var1 = this.loadMainMethod(this.getMainMethod());
      String var2 = var1.getDeclaringClass().getName() + ".main(String[])";

      try {
         this.log("Calling " + var2 + " method");
         var1.invoke((Object)null, this.getArguments());
         this.log(var2 + " has returned");
      } catch (IllegalAccessException var6) {
         try {
            nativeInvokeNonPublic(var1.getClass(), var1, this.getArguments());
         } catch (Throwable var5) {
            logError(var2 + " threw an exception:");
            if (var5 instanceof UnsatisfiedLinkError && var5.getMessage().equals("nativeInvokeNonPublic")) {
               showFailureAlertAndKill("MainThrewException", "nativeInvokeNonPublic not registered");
            } else {
               var5.printStackTrace();
               showFailureAlertAndKill("MainThrewException", var5.toString());
            }
         }
      } catch (InvocationTargetException var7) {
         logError(var2 + " threw an exception:");
         var7.getTargetException().printStackTrace();
         showFailureAlertAndKill("MainThrewException", var7.getTargetException().toString());
      }

   }

   Method loadMainMethod(String var1) {
      try {
         Class var2 = Class.forName(var1, true, Launcher.getLauncher().getClassLoader());
         Method var3 = var2.getDeclaredMethod("main", String[].class);
         if ((var3.getModifiers() & 8) == 0) {
            logError("The main(String[]) method of class " + var1 + " is not static!");
            showFailureAlertAndKill("MainNotStatic", var1);
         }

         return var3;
      } catch (ExceptionInInitializerError var4) {
         logError("The main class \"" + var1 + "\" had a static initializer throw an exception.");
         var4.getException().printStackTrace();
         showFailureAlertAndKill("MainInitializerException", var4.getException().toString());
      } catch (ClassNotFoundException var5) {
         logError("The main class \"" + var1 + "\" could not be found.");
         showFailureAlertAndKill("CannotLoadMainClass", var1);
      } catch (NoSuchMethodException var6) {
         logError("The main class \"" + var1 + "\" has no static main(String[]) method.");
         showFailureAlertAndKill("NoMainMethod", var1);
      } catch (NullPointerException var7) {
         logError("No main class specified");
         showFailureAlertAndKill("MainClassNotSpecified", (String)null);
      }

      return null;
   }

   String getMainMethod() {
      Object var1 = this.javaDictionary.get("Jar");
      String var3;
      if (var1 != null) {
         if (!(var1 instanceof String)) {
            logError("'Jar' key in 'Java' sub-dictionary of Info.plist requires a string value");
            return null;
         }

         String var2 = (String)var1;
         if (var2.length() != 0) {
            var3 = this.getMainFromManifest(var2);
            if (var3 == null) {
               logError("jar file '" + var2 + "' does not have Main-Class: attribute in its manifest");
               return null;
            }

            this.log("Main class " + var3 + " found in jar manifest");
            return var3;
         }

         this.log("'Jar' key of sub-dictionary 'Java' of Info.plist key is empty");
      }

      Object var4 = this.javaDictionary.get("MainClass");
      if (!(var4 instanceof String)) {
         logError("'MainClass' key in 'Java' sub-dictionary of Info.plist requires a string value");
         return null;
      } else {
         var3 = (String)var4;
         if (var3.length() == 0) {
            this.log("'MainClass' key of sub-dictionary 'Java' of Info.plist key is empty");
            return null;
         } else {
            this.log("Main class " + (String)var4 + " found via 'MainClass' key of sub-dictionary 'Java' of Info.plist key");
            return (String)var4;
         }
      }
   }

   String[] getArguments() {
      Object var1 = this.javaDictionary.get("Arguments");
      if (var1 == null) {
         this.log("No arguments for main(String[]) specified");
         return new String[0];
      } else if (var1 instanceof List) {
         List var2 = (List)var1;
         int var3 = var2.size();
         this.log("Arguments to main(String[" + var3 + "]):");
         String[] var4 = new String[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            Object var6 = var2.get(var5);
            if (var6 instanceof String) {
               var4[var5] = (String)var6;
            } else {
               logError("Found non-string in array");
            }

            this.log("   arg[" + var5 + "]=" + var4[var5]);
         }

         return var4;
      } else {
         logError("'Arguments' key in 'Java' sub-dictionary of Info.plist requires a string value or an array of strings");
         return new String[0];
      }
   }

   String getMainFromManifest(String var1) {
      JarFile var2 = null;

      try {
         var2 = new JarFile(var1);
         Manifest var3 = var2.getManifest();
         Attributes var4 = var3.getMainAttributes();
         String var5 = var4.getValue("Main-Class");
         return var5;
      } catch (IOException var15) {
      } finally {
         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var14) {
            }
         }

      }

      return null;
   }

   void log(String var1) {
      if (this.verbose) {
         System.out.println("[LaunchRunner] " + var1);
      }
   }

   static void logError(String var0) {
      System.err.println("[LaunchRunner Error] " + var0);
   }

   static void showFailureAlertAndKill(String var0, String var1) {
      if (var1 == null) {
         var1 = "<<null>>";
      }

      JOptionPane.showMessageDialog((Component)null, getMessage(var0, var1), "", 0);
      System.exit(-1);
   }

   static String getMessage(String var0, Object... var1) {
      String var2 = ResourceBundle.getBundle("appLauncherErrors").getString(var0);
      return MessageFormat.format(var2, var1);
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osx");
            return null;
         }
      });
   }
}
