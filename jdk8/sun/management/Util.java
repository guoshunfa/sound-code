package sun.management;

import java.lang.management.ManagementPermission;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Util {
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private static ManagementPermission monitorPermission = new ManagementPermission("monitor");
   private static ManagementPermission controlPermission = new ManagementPermission("control");

   private Util() {
   }

   static RuntimeException newException(Exception var0) {
      throw new RuntimeException(var0);
   }

   static String[] toStringArray(List<String> var0) {
      return (String[])var0.toArray(EMPTY_STRING_ARRAY);
   }

   public static ObjectName newObjectName(String var0, String var1) {
      return newObjectName(var0 + ",name=" + var1);
   }

   public static ObjectName newObjectName(String var0) {
      try {
         return ObjectName.getInstance(var0);
      } catch (MalformedObjectNameException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   static void checkAccess(ManagementPermission var0) throws SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(var0);
      }

   }

   static void checkMonitorAccess() throws SecurityException {
      checkAccess(monitorPermission);
   }

   static void checkControlAccess() throws SecurityException {
      checkAccess(controlPermission);
   }
}
