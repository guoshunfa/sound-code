package sun.misc;

import java.io.PrintStream;

public class Version {
   private static final String launcher_name = "java";
   private static final String java_version = "1.8.0_181";
   private static final String java_runtime_name = "Java(TM) SE Runtime Environment";
   private static final String java_profile_name = "";
   private static final String java_runtime_version = "1.8.0_181-b13";
   private static boolean versionsInitialized;
   private static int jvm_major_version;
   private static int jvm_minor_version;
   private static int jvm_micro_version;
   private static int jvm_update_version;
   private static int jvm_build_number;
   private static String jvm_special_version;
   private static int jdk_major_version;
   private static int jdk_minor_version;
   private static int jdk_micro_version;
   private static int jdk_update_version;
   private static int jdk_build_number;
   private static String jdk_special_version;
   private static boolean jvmVersionInfoAvailable;

   public static void init() {
      System.setProperty("java.version", "1.8.0_181");
      System.setProperty("java.runtime.version", "1.8.0_181-b13");
      System.setProperty("java.runtime.name", "Java(TM) SE Runtime Environment");
   }

   public static void print() {
      print(System.err);
   }

   public static void println() {
      print(System.err);
      System.err.println();
   }

   public static void print(PrintStream var0) {
      boolean var1 = false;
      String var2 = System.getProperty("java.awt.headless");
      if (var2 != null && var2.equalsIgnoreCase("true")) {
         var1 = true;
      }

      var0.println("java version \"1.8.0_181\"");
      var0.print("Java(TM) SE Runtime Environment (build 1.8.0_181-b13");
      if ("".length() > 0) {
         var0.print(", profile ");
      }

      if ("Java(TM) SE Runtime Environment".indexOf("Embedded") != -1 && var1) {
         var0.print(", headless");
      }

      var0.println(')');
      String var3 = System.getProperty("java.vm.name");
      String var4 = System.getProperty("java.vm.version");
      String var5 = System.getProperty("java.vm.info");
      var0.println(var3 + " (build " + var4 + ", " + var5 + ")");
   }

   public static synchronized int jvmMajorVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jvm_major_version;
   }

   public static synchronized int jvmMinorVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jvm_minor_version;
   }

   public static synchronized int jvmMicroVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jvm_micro_version;
   }

   public static synchronized int jvmUpdateVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jvm_update_version;
   }

   public static synchronized String jvmSpecialVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      if (jvm_special_version == null) {
         jvm_special_version = getJvmSpecialVersion();
      }

      return jvm_special_version;
   }

   public static native String getJvmSpecialVersion();

   public static synchronized int jvmBuildNumber() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jvm_build_number;
   }

   public static synchronized int jdkMajorVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jdk_major_version;
   }

   public static synchronized int jdkMinorVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jdk_minor_version;
   }

   public static synchronized int jdkMicroVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jdk_micro_version;
   }

   public static synchronized int jdkUpdateVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jdk_update_version;
   }

   public static synchronized String jdkSpecialVersion() {
      if (!versionsInitialized) {
         initVersions();
      }

      if (jdk_special_version == null) {
         jdk_special_version = getJdkSpecialVersion();
      }

      return jdk_special_version;
   }

   public static native String getJdkSpecialVersion();

   public static synchronized int jdkBuildNumber() {
      if (!versionsInitialized) {
         initVersions();
      }

      return jdk_build_number;
   }

   private static synchronized void initVersions() {
      if (!versionsInitialized) {
         jvmVersionInfoAvailable = getJvmVersionInfo();
         if (!jvmVersionInfoAvailable) {
            String var0 = System.getProperty("java.vm.version");
            if (var0.length() >= 5 && Character.isDigit(var0.charAt(0)) && var0.charAt(1) == '.' && Character.isDigit(var0.charAt(2)) && var0.charAt(3) == '.' && Character.isDigit(var0.charAt(4))) {
               jvm_major_version = Character.digit((char)var0.charAt(0), 10);
               jvm_minor_version = Character.digit((char)var0.charAt(2), 10);
               jvm_micro_version = Character.digit((char)var0.charAt(4), 10);
               CharSequence var7 = var0.subSequence(5, var0.length());
               if (var7.charAt(0) == '_' && var7.length() >= 3) {
                  int var1 = 0;
                  if (Character.isDigit(var7.charAt(1)) && Character.isDigit(var7.charAt(2)) && Character.isDigit(var7.charAt(3))) {
                     var1 = 4;
                  } else if (Character.isDigit(var7.charAt(1)) && Character.isDigit(var7.charAt(2))) {
                     var1 = 3;
                  }

                  try {
                     String var2 = var7.subSequence(1, var1).toString();
                     jvm_update_version = Integer.valueOf(var2);
                     if (var7.length() >= var1 + 1) {
                        char var3 = var7.charAt(var1);
                        if (var3 >= 'a' && var3 <= 'z') {
                           jvm_special_version = Character.toString(var3);
                           ++var1;
                        }
                     }
                  } catch (NumberFormatException var6) {
                     return;
                  }

                  var7 = var7.subSequence(var1, var7.length());
               }

               if (var7.charAt(0) == '-') {
                  var7 = var7.subSequence(1, var7.length());
                  String[] var9 = var7.toString().split("-");
                  String[] var8 = var9;
                  int var10 = var9.length;

                  for(int var4 = 0; var4 < var10; ++var4) {
                     String var5 = var8[var4];
                     if (var5.charAt(0) == 'b' && var5.length() == 3 && Character.isDigit(var5.charAt(1)) && Character.isDigit(var5.charAt(2))) {
                        jvm_build_number = Integer.valueOf(var5.substring(1, 3));
                        break;
                     }
                  }
               }
            }
         }

         getJdkVersionInfo();
         versionsInitialized = true;
      }
   }

   private static native boolean getJvmVersionInfo();

   private static native void getJdkVersionInfo();

   static {
      init();
      versionsInitialized = false;
      jvm_major_version = 0;
      jvm_minor_version = 0;
      jvm_micro_version = 0;
      jvm_update_version = 0;
      jvm_build_number = 0;
      jvm_special_version = null;
      jdk_major_version = 0;
      jdk_minor_version = 0;
      jdk_micro_version = 0;
      jdk_update_version = 0;
      jdk_build_number = 0;
      jdk_special_version = null;
   }
}
