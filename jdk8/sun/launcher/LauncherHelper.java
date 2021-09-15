package sun.launcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.misc.VM;

public enum LauncherHelper {
   INSTANCE;

   private static final String MAIN_CLASS = "Main-Class";
   private static StringBuilder outBuf = new StringBuilder();
   private static final String INDENT = "    ";
   private static final String VM_SETTINGS = "VM settings:";
   private static final String PROP_SETTINGS = "Property settings:";
   private static final String LOCALE_SETTINGS = "Locale settings:";
   private static final String diagprop = "sun.java.launcher.diag";
   static final boolean trace = VM.getSavedProperty("sun.java.launcher.diag") != null;
   private static final String defaultBundleName = "sun.launcher.resources.launcher";
   private static PrintStream ostream;
   private static final ClassLoader scloader = ClassLoader.getSystemClassLoader();
   private static Class<?> appClass;
   private static final int LM_UNKNOWN = 0;
   private static final int LM_CLASS = 1;
   private static final int LM_JAR = 2;
   private static final String encprop = "sun.jnu.encoding";
   private static String encoding = null;
   private static boolean isCharsetSupported = false;

   static void showSettings(boolean var0, String var1, long var2, long var4, long var6, boolean var8) {
      initOutput(var0);
      String[] var9 = var1.split(":");
      String var10 = var9.length > 1 && var9[1] != null ? var9[1].trim() : "all";
      byte var12 = -1;
      switch(var10.hashCode()) {
      case -1097462182:
         if (var10.equals("locale")) {
            var12 = 2;
         }
         break;
      case -926053069:
         if (var10.equals("properties")) {
            var12 = 1;
         }
         break;
      case 3767:
         if (var10.equals("vm")) {
            var12 = 0;
         }
      }

      switch(var12) {
      case 0:
         printVmSettings(var2, var4, var6, var8);
         break;
      case 1:
         printProperties();
         break;
      case 2:
         printLocale();
         break;
      default:
         printVmSettings(var2, var4, var6, var8);
         printProperties();
         printLocale();
      }

   }

   private static void printVmSettings(long var0, long var2, long var4, boolean var6) {
      ostream.println("VM settings:");
      if (var4 != 0L) {
         ostream.println("    Stack Size: " + LauncherHelper.SizePrefix.scaleValue(var4));
      }

      if (var0 != 0L) {
         ostream.println("    Min. Heap Size: " + LauncherHelper.SizePrefix.scaleValue(var0));
      }

      if (var2 != 0L) {
         ostream.println("    Max. Heap Size: " + LauncherHelper.SizePrefix.scaleValue(var2));
      } else {
         ostream.println("    Max. Heap Size (Estimated): " + LauncherHelper.SizePrefix.scaleValue(Runtime.getRuntime().maxMemory()));
      }

      ostream.println("    Ergonomics Machine Class: " + (var6 ? "server" : "client"));
      ostream.println("    Using VM: " + System.getProperty("java.vm.name"));
      ostream.println();
   }

   private static void printProperties() {
      Properties var0 = System.getProperties();
      ostream.println("Property settings:");
      ArrayList var1 = new ArrayList();
      var1.addAll(var0.stringPropertyNames());
      Collections.sort(var1);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         printPropertyValue(var3, var0.getProperty(var3));
      }

      ostream.println();
   }

   private static boolean isPath(String var0) {
      return var0.endsWith(".dirs") || var0.endsWith(".path");
   }

   private static void printPropertyValue(String var0, String var1) {
      ostream.print("    " + var0 + " = ");
      if (var0.equals("line.separator")) {
         byte[] var8 = var1.getBytes();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            byte var11 = var8[var10];
            switch(var11) {
            case 10:
               ostream.print("\\n ");
               break;
            case 13:
               ostream.print("\\r ");
               break;
            default:
               ostream.printf("0x%02X", var11 & 255);
            }
         }

         ostream.println();
      } else if (!isPath(var0)) {
         ostream.println(var1);
      } else {
         String[] var2 = var1.split(System.getProperty("path.separator"));
         boolean var3 = true;
         String[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            if (var3) {
               ostream.println(var7);
               var3 = false;
            } else {
               ostream.println("        " + var7);
            }
         }

      }
   }

   private static void printLocale() {
      Locale var0 = Locale.getDefault();
      ostream.println("Locale settings:");
      ostream.println("    default locale = " + var0.getDisplayLanguage());
      ostream.println("    default display locale = " + Locale.getDefault(Locale.Category.DISPLAY).getDisplayName());
      ostream.println("    default format locale = " + Locale.getDefault(Locale.Category.FORMAT).getDisplayName());
      printLocales();
      ostream.println();
   }

   private static void printLocales() {
      Locale[] var0 = Locale.getAvailableLocales();
      int var1 = var0 == null ? 0 : var0.length;
      if (var1 >= 1) {
         TreeSet var2 = new TreeSet();
         Locale[] var3 = var0;
         int var4 = var0.length;

         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            Locale var6 = var3[var5];
            var2.add(var6.toString());
         }

         ostream.print("    available locales = ");
         Iterator var7 = var2.iterator();
         var4 = var1 - 1;

         for(var5 = 0; var7.hasNext(); ++var5) {
            String var8 = (String)var7.next();
            ostream.print(var8);
            if (var5 != var4) {
               ostream.print(", ");
            }

            if ((var5 + 1) % 8 == 0) {
               ostream.println();
               ostream.print("        ");
            }
         }

      }
   }

   private static String getLocalizedMessage(String var0, Object... var1) {
      String var2 = LauncherHelper.ResourceBundleHolder.RB.getString(var0);
      return var1 != null ? MessageFormat.format(var2, var1) : var2;
   }

   static void initHelpMessage(String var0) {
      outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.header", var0 == null ? "java" : var0));
      outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", 32));
      outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", 64));
   }

   static void appendVmSelectMessage(String var0, String var1) {
      outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.vmselect", var0, var1));
   }

   static void appendVmSynonymMessage(String var0, String var1) {
      outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.hotspot", var0, var1));
   }

   static void appendVmErgoMessage(boolean var0, String var1) {
      outBuf = outBuf.append(getLocalizedMessage("java.launcher.ergo.message1", var1));
      outBuf = var0 ? outBuf.append(",\n" + getLocalizedMessage("java.launcher.ergo.message2") + "\n\n") : outBuf.append(".\n\n");
   }

   static void printHelpMessage(boolean var0) {
      initOutput(var0);
      outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.footer", File.pathSeparator));
      ostream.println(outBuf.toString());
   }

   static void printXUsageMessage(boolean var0) {
      initOutput(var0);
      ostream.println(getLocalizedMessage("java.launcher.X.usage", File.pathSeparator));
      if (System.getProperty("os.name").contains("OS X")) {
         ostream.println(getLocalizedMessage("java.launcher.X.macosx.usage", File.pathSeparator));
      }

   }

   static void initOutput(boolean var0) {
      ostream = var0 ? System.err : System.out;
   }

   static String getMainClassFromJar(String var0) {
      String var1 = null;

      try {
         JarFile var2 = new JarFile(var0);
         Throwable var3 = null;

         String var6;
         try {
            Manifest var4 = var2.getManifest();
            if (var4 == null) {
               abort((Throwable)null, "java.launcher.jar.error2", var0);
            }

            Attributes var5 = var4.getMainAttributes();
            if (var5 == null) {
               abort((Throwable)null, "java.launcher.jar.error3", var0);
            }

            var1 = var5.getValue("Main-Class");
            if (var1 == null) {
               abort((Throwable)null, "java.launcher.jar.error3", var0);
            }

            if (!var5.containsKey(new Attributes.Name("JavaFX-Application-Class"))) {
               var6 = var1.trim();
               return var6;
            }

            var6 = LauncherHelper.FXHelper.class.getName();
         } catch (Throwable var17) {
            var3 = var17;
            throw var17;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var16) {
                     var3.addSuppressed(var16);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var6;
      } catch (IOException var19) {
         abort(var19, "java.launcher.jar.error1", var0);
         return null;
      }
   }

   static void abort(Throwable var0, String var1, Object... var2) {
      if (var1 != null) {
         ostream.println(getLocalizedMessage(var1, var2));
      }

      if (trace) {
         if (var0 != null) {
            var0.printStackTrace();
         } else {
            Thread.dumpStack();
         }
      }

      System.exit(1);
   }

   public static Class<?> checkAndLoadMain(boolean var0, int var1, String var2) {
      initOutput(var0);
      String var3 = null;
      switch(var1) {
      case 1:
         var3 = var2;
         break;
      case 2:
         var3 = getMainClassFromJar(var2);
         break;
      default:
         throw new InternalError("" + var1 + ": Unknown launch mode");
      }

      var3 = var3.replace('/', '.');
      Class var4 = null;

      try {
         var4 = scloader.loadClass(var3);
      } catch (ClassNotFoundException | NoClassDefFoundError var8) {
         if (System.getProperty("os.name", "").contains("OS X") && Normalizer.isNormalized(var3, Normalizer.Form.NFD)) {
            try {
               var4 = scloader.loadClass(Normalizer.normalize(var3, Normalizer.Form.NFC));
            } catch (ClassNotFoundException | NoClassDefFoundError var7) {
               abort(var8, "java.launcher.cls.error1", var3);
            }
         } else {
            abort(var8, "java.launcher.cls.error1", var3);
         }
      }

      appClass = var4;
      if (!var4.equals(LauncherHelper.FXHelper.class) && !LauncherHelper.FXHelper.doesExtendFXApplication(var4)) {
         validateMainClass(var4);
         return var4;
      } else {
         LauncherHelper.FXHelper.setFXLaunchParameters(var2, var1);
         return LauncherHelper.FXHelper.class;
      }
   }

   public static Class<?> getApplicationClass() {
      return appClass;
   }

   static void validateMainClass(Class<?> var0) {
      Method var1;
      try {
         var1 = var0.getMethod("main", String[].class);
      } catch (NoSuchMethodException var3) {
         abort((Throwable)null, "java.launcher.cls.error4", var0.getName(), "javafx.application.Application");
         return;
      }

      int var2 = var1.getModifiers();
      if (!Modifier.isStatic(var2)) {
         abort((Throwable)null, "java.launcher.cls.error2", "static", var1.getDeclaringClass().getName());
      }

      if (var1.getReturnType() != Void.TYPE) {
         abort((Throwable)null, "java.launcher.cls.error3", var1.getDeclaringClass().getName());
      }

   }

   static String makePlatformString(boolean var0, byte[] var1) {
      initOutput(var0);
      if (encoding == null) {
         encoding = System.getProperty("sun.jnu.encoding");
         isCharsetSupported = Charset.isSupported(encoding);
      }

      try {
         String var2 = isCharsetSupported ? new String(var1, encoding) : new String(var1);
         return var2;
      } catch (UnsupportedEncodingException var3) {
         abort(var3, (String)null);
         return null;
      }
   }

   static String[] expandArgs(String[] var0) {
      ArrayList var1 = new ArrayList();
      String[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         var1.add(new LauncherHelper.StdArg(var5));
      }

      return expandArgs((List)var1);
   }

   static String[] expandArgs(List<LauncherHelper.StdArg> var0) {
      ArrayList var1 = new ArrayList();
      if (trace) {
         System.err.println("Incoming arguments:");
      }

      Iterator var2 = var0.iterator();

      String var6;
      while(var2.hasNext()) {
         LauncherHelper.StdArg var3 = (LauncherHelper.StdArg)var2.next();
         if (trace) {
            System.err.println((Object)var3);
         }

         if (!var3.needsExpansion) {
            var1.add(var3.arg);
         } else {
            File var4 = new File(var3.arg);
            File var5 = var4.getParentFile();
            var6 = var4.getName();
            if (var5 == null) {
               var5 = new File(".");
            }

            try {
               DirectoryStream var7 = Files.newDirectoryStream(var5.toPath(), var6);
               Throwable var8 = null;

               try {
                  int var9 = 0;

                  for(Iterator var10 = var7.iterator(); var10.hasNext(); ++var9) {
                     Path var11 = (Path)var10.next();
                     var1.add(var11.normalize().toString());
                  }

                  if (var9 == 0) {
                     var1.add(var3.arg);
                  }
               } catch (Throwable var20) {
                  var8 = var20;
                  throw var20;
               } finally {
                  if (var7 != null) {
                     if (var8 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var19) {
                           var8.addSuppressed(var19);
                        }
                     } else {
                        var7.close();
                     }
                  }

               }
            } catch (Exception var22) {
               var1.add(var3.arg);
               if (trace) {
                  System.err.println("Warning: passing argument as-is " + var3);
                  System.err.print((Object)var22);
               }
            }
         }
      }

      String[] var23 = new String[var1.size()];
      var1.toArray(var23);
      if (trace) {
         System.err.println("Expanded arguments:");
         String[] var24 = var23;
         int var25 = var23.length;

         for(int var26 = 0; var26 < var25; ++var26) {
            var6 = var24[var26];
            System.err.println(var6);
         }
      }

      return var23;
   }

   static final class FXHelper {
      private static final String JAVAFX_APPLICATION_MARKER = "JavaFX-Application-Class";
      private static final String JAVAFX_APPLICATION_CLASS_NAME = "javafx.application.Application";
      private static final String JAVAFX_LAUNCHER_CLASS_NAME = "com.sun.javafx.application.LauncherImpl";
      private static final String JAVAFX_LAUNCH_MODE_CLASS = "LM_CLASS";
      private static final String JAVAFX_LAUNCH_MODE_JAR = "LM_JAR";
      private static String fxLaunchName = null;
      private static String fxLaunchMode = null;
      private static Class<?> fxLauncherClass = null;
      private static Method fxLauncherMethod = null;

      private static void setFXLaunchParameters(String var0, int var1) {
         try {
            fxLauncherClass = LauncherHelper.scloader.loadClass("com.sun.javafx.application.LauncherImpl");
            fxLauncherMethod = fxLauncherClass.getMethod("launchApplication", String.class, String.class, String[].class);
            int var2 = fxLauncherMethod.getModifiers();
            if (!Modifier.isStatic(var2)) {
               LauncherHelper.abort((Throwable)null, "java.launcher.javafx.error1");
            }

            if (fxLauncherMethod.getReturnType() != Void.TYPE) {
               LauncherHelper.abort((Throwable)null, "java.launcher.javafx.error1");
            }
         } catch (NoSuchMethodException | ClassNotFoundException var3) {
            LauncherHelper.abort(var3, "java.launcher.cls.error5", var3);
         }

         fxLaunchName = var0;
         switch(var1) {
         case 1:
            fxLaunchMode = "LM_CLASS";
            break;
         case 2:
            fxLaunchMode = "LM_JAR";
            break;
         default:
            throw new InternalError(var1 + ": Unknown launch mode");
         }

      }

      private static boolean doesExtendFXApplication(Class<?> var0) {
         for(Class var1 = var0.getSuperclass(); var1 != null; var1 = var1.getSuperclass()) {
            if (var1.getName().equals("javafx.application.Application")) {
               return true;
            }
         }

         return false;
      }

      public static void main(String... var0) throws Exception {
         if (fxLauncherMethod != null && fxLaunchMode != null && fxLaunchName != null) {
            fxLauncherMethod.invoke((Object)null, fxLaunchName, fxLaunchMode, var0);
         } else {
            throw new RuntimeException("Invalid JavaFX launch parameters");
         }
      }
   }

   private static class StdArg {
      final String arg;
      final boolean needsExpansion;

      StdArg(String var1, boolean var2) {
         this.arg = var1;
         this.needsExpansion = var2;
      }

      StdArg(String var1) {
         this.arg = var1.substring(1);
         this.needsExpansion = var1.charAt(0) == 'T';
      }

      public String toString() {
         return "StdArg{arg=" + this.arg + ", needsExpansion=" + this.needsExpansion + '}';
      }
   }

   private static enum SizePrefix {
      KILO(1024L, "K"),
      MEGA(1048576L, "M"),
      GIGA(1073741824L, "G"),
      TERA(1099511627776L, "T");

      long size;
      String abbrev;

      private SizePrefix(long var3, String var5) {
         this.size = var3;
         this.abbrev = var5;
      }

      private static String scale(long var0, LauncherHelper.SizePrefix var2) {
         return BigDecimal.valueOf(var0).divide(BigDecimal.valueOf(var2.size), 2, RoundingMode.HALF_EVEN).toPlainString() + var2.abbrev;
      }

      static String scaleValue(long var0) {
         if (var0 < MEGA.size) {
            return scale(var0, KILO);
         } else if (var0 < GIGA.size) {
            return scale(var0, MEGA);
         } else {
            return var0 < TERA.size ? scale(var0, GIGA) : scale(var0, TERA);
         }
      }
   }

   private static class ResourceBundleHolder {
      private static final ResourceBundle RB = ResourceBundle.getBundle("sun.launcher.resources.launcher");
   }
}
