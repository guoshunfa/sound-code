package sun.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;

public final class InetAddressCachePolicy {
   private static final String cachePolicyProp = "networkaddress.cache.ttl";
   private static final String cachePolicyPropFallback = "sun.net.inetaddr.ttl";
   private static final String negativeCachePolicyProp = "networkaddress.cache.negative.ttl";
   private static final String negativeCachePolicyPropFallback = "sun.net.inetaddr.negative.ttl";
   public static final int FOREVER = -1;
   public static final int NEVER = 0;
   public static final int DEFAULT_POSITIVE = 30;
   private static int cachePolicy = -1;
   private static int negativeCachePolicy = 0;
   private static boolean propertySet;
   private static boolean propertyNegativeSet;

   public static synchronized int get() {
      return cachePolicy;
   }

   public static synchronized int getNegative() {
      return negativeCachePolicy;
   }

   public static synchronized void setIfNotSet(int var0) {
      if (!propertySet) {
         checkValue(var0, cachePolicy);
         cachePolicy = var0;
      }

   }

   public static synchronized void setNegativeIfNotSet(int var0) {
      if (!propertyNegativeSet) {
         negativeCachePolicy = var0;
      }

   }

   private static void checkValue(int var0, int var1) {
      if (var0 != -1) {
         if (var1 == -1 || var0 < var1 || var0 < -1) {
            throw new SecurityException("can't make InetAddress cache more lax");
         }
      }
   }

   static {
      Integer var0 = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
         public Integer run() {
            String var1;
            try {
               var1 = Security.getProperty("networkaddress.cache.ttl");
               if (var1 != null) {
                  return Integer.valueOf(var1);
               }
            } catch (NumberFormatException var3) {
            }

            try {
               var1 = System.getProperty("sun.net.inetaddr.ttl");
               if (var1 != null) {
                  return Integer.decode(var1);
               }
            } catch (NumberFormatException var2) {
            }

            return null;
         }
      });
      if (var0 != null) {
         cachePolicy = var0;
         if (cachePolicy < 0) {
            cachePolicy = -1;
         }

         propertySet = true;
      } else if (System.getSecurityManager() == null) {
         cachePolicy = 30;
      }

      var0 = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
         public Integer run() {
            String var1;
            try {
               var1 = Security.getProperty("networkaddress.cache.negative.ttl");
               if (var1 != null) {
                  return Integer.valueOf(var1);
               }
            } catch (NumberFormatException var3) {
            }

            try {
               var1 = System.getProperty("sun.net.inetaddr.negative.ttl");
               if (var1 != null) {
                  return Integer.decode(var1);
               }
            } catch (NumberFormatException var2) {
            }

            return null;
         }
      });
      if (var0 != null) {
         negativeCachePolicy = var0;
         if (negativeCachePolicy < 0) {
            negativeCachePolicy = -1;
         }

         propertyNegativeSet = true;
      }

   }
}
