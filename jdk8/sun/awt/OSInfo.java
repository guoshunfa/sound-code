package sun.awt;

import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

public class OSInfo {
   public static final OSInfo.WindowsVersion WINDOWS_UNKNOWN = new OSInfo.WindowsVersion(-1, -1);
   public static final OSInfo.WindowsVersion WINDOWS_95 = new OSInfo.WindowsVersion(4, 0);
   public static final OSInfo.WindowsVersion WINDOWS_98 = new OSInfo.WindowsVersion(4, 10);
   public static final OSInfo.WindowsVersion WINDOWS_ME = new OSInfo.WindowsVersion(4, 90);
   public static final OSInfo.WindowsVersion WINDOWS_2000 = new OSInfo.WindowsVersion(5, 0);
   public static final OSInfo.WindowsVersion WINDOWS_XP = new OSInfo.WindowsVersion(5, 1);
   public static final OSInfo.WindowsVersion WINDOWS_2003 = new OSInfo.WindowsVersion(5, 2);
   public static final OSInfo.WindowsVersion WINDOWS_VISTA = new OSInfo.WindowsVersion(6, 0);
   private static final String OS_NAME = "os.name";
   private static final String OS_VERSION = "os.version";
   private static final Map<String, OSInfo.WindowsVersion> windowsVersionMap = new HashMap();
   private static final PrivilegedAction<OSInfo.OSType> osTypeAction;

   private OSInfo() {
   }

   public static OSInfo.OSType getOSType() throws SecurityException {
      String var0 = System.getProperty("os.name");
      if (var0 != null) {
         if (var0.contains("Windows")) {
            return OSInfo.OSType.WINDOWS;
         }

         if (var0.contains("Linux")) {
            return OSInfo.OSType.LINUX;
         }

         if (var0.contains("Solaris") || var0.contains("SunOS")) {
            return OSInfo.OSType.SOLARIS;
         }

         if (var0.contains("OS X")) {
            return OSInfo.OSType.MACOSX;
         }
      }

      return OSInfo.OSType.UNKNOWN;
   }

   public static PrivilegedAction<OSInfo.OSType> getOSTypeAction() {
      return osTypeAction;
   }

   public static OSInfo.WindowsVersion getWindowsVersion() throws SecurityException {
      String var0 = System.getProperty("os.version");
      if (var0 == null) {
         return WINDOWS_UNKNOWN;
      } else {
         synchronized(windowsVersionMap) {
            OSInfo.WindowsVersion var2 = (OSInfo.WindowsVersion)windowsVersionMap.get(var0);
            if (var2 == null) {
               String[] var3 = var0.split("\\.");
               if (var3.length != 2) {
                  return WINDOWS_UNKNOWN;
               }

               try {
                  var2 = new OSInfo.WindowsVersion(Integer.parseInt(var3[0]), Integer.parseInt(var3[1]));
               } catch (NumberFormatException var6) {
                  return WINDOWS_UNKNOWN;
               }

               windowsVersionMap.put(var0, var2);
            }

            return var2;
         }
      }
   }

   static {
      windowsVersionMap.put(WINDOWS_95.toString(), WINDOWS_95);
      windowsVersionMap.put(WINDOWS_98.toString(), WINDOWS_98);
      windowsVersionMap.put(WINDOWS_ME.toString(), WINDOWS_ME);
      windowsVersionMap.put(WINDOWS_2000.toString(), WINDOWS_2000);
      windowsVersionMap.put(WINDOWS_XP.toString(), WINDOWS_XP);
      windowsVersionMap.put(WINDOWS_2003.toString(), WINDOWS_2003);
      windowsVersionMap.put(WINDOWS_VISTA.toString(), WINDOWS_VISTA);
      osTypeAction = new PrivilegedAction<OSInfo.OSType>() {
         public OSInfo.OSType run() {
            return OSInfo.getOSType();
         }
      };
   }

   public static class WindowsVersion implements Comparable<OSInfo.WindowsVersion> {
      private final int major;
      private final int minor;

      private WindowsVersion(int var1, int var2) {
         this.major = var1;
         this.minor = var2;
      }

      public int getMajor() {
         return this.major;
      }

      public int getMinor() {
         return this.minor;
      }

      public int compareTo(OSInfo.WindowsVersion var1) {
         int var2 = this.major - var1.getMajor();
         if (var2 == 0) {
            var2 = this.minor - var1.getMinor();
         }

         return var2;
      }

      public boolean equals(Object var1) {
         return var1 instanceof OSInfo.WindowsVersion && this.compareTo((OSInfo.WindowsVersion)var1) == 0;
      }

      public int hashCode() {
         return 31 * this.major + this.minor;
      }

      public String toString() {
         return this.major + "." + this.minor;
      }

      // $FF: synthetic method
      WindowsVersion(int var1, int var2, Object var3) {
         this(var1, var2);
      }
   }

   public static enum OSType {
      WINDOWS,
      LINUX,
      SOLARIS,
      MACOSX,
      UNKNOWN;
   }
}
