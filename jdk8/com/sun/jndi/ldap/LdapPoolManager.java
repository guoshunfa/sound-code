package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.Pool;
import com.sun.jndi.ldap.pool.PoolCleaner;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;

public final class LdapPoolManager {
   private static final String DEBUG = "com.sun.jndi.ldap.connect.pool.debug";
   public static final boolean debug = "all".equalsIgnoreCase(getProperty("com.sun.jndi.ldap.connect.pool.debug", (String)null));
   public static final boolean trace;
   private static final String POOL_AUTH = "com.sun.jndi.ldap.connect.pool.authentication";
   private static final String POOL_PROTOCOL = "com.sun.jndi.ldap.connect.pool.protocol";
   private static final String MAX_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.maxsize";
   private static final String PREF_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.prefsize";
   private static final String INIT_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.initsize";
   private static final String POOL_TIMEOUT = "com.sun.jndi.ldap.connect.pool.timeout";
   private static final String SASL_CALLBACK = "java.naming.security.sasl.callback";
   private static final int DEFAULT_MAX_POOL_SIZE = 0;
   private static final int DEFAULT_PREF_POOL_SIZE = 0;
   private static final int DEFAULT_INIT_POOL_SIZE = 1;
   private static final int DEFAULT_TIMEOUT = 0;
   private static final String DEFAULT_AUTH_MECHS = "none simple";
   private static final String DEFAULT_PROTOCOLS = "plain";
   private static final int NONE = 0;
   private static final int SIMPLE = 1;
   private static final int DIGEST = 2;
   private static final long idleTimeout;
   private static final int maxSize;
   private static final int prefSize;
   private static final int initSize;
   private static boolean supportPlainProtocol;
   private static boolean supportSslProtocol;
   private static final Pool[] pools;

   private LdapPoolManager() {
   }

   private static int findPool(String var0) {
      if ("none".equalsIgnoreCase(var0)) {
         return 0;
      } else if ("simple".equalsIgnoreCase(var0)) {
         return 1;
      } else {
         return "digest-md5".equalsIgnoreCase(var0) ? 2 : -1;
      }
   }

   static boolean isPoolingAllowed(String var0, OutputStream var1, String var2, String var3, Hashtable<?, ?> var4) throws NamingException {
      if ((var1 == null || debug) && (var3 != null || supportPlainProtocol) && (!"ssl".equalsIgnoreCase(var3) || supportSslProtocol)) {
         String var5 = "java.util.Comparator";
         boolean var6 = false;
         if (var0 != null && !var0.equals("javax.net.ssl.SSLSocketFactory")) {
            try {
               Class var7 = Obj.helper.loadClass(var0);
               Class[] var12 = var7.getInterfaces();

               for(int var9 = 0; var9 < var12.length; ++var9) {
                  if (var12[var9].getCanonicalName().equals(var5)) {
                     var6 = true;
                  }
               }
            } catch (Exception var10) {
               CommunicationException var8 = new CommunicationException("Loading the socket factory");
               var8.setRootCause(var10);
               throw var8;
            }

            if (!var6) {
               return false;
            }
         }

         int var11 = findPool(var2);
         if (var11 >= 0 && pools[var11] != null) {
            d("using authmech: ", var2);
            switch(var11) {
            case 0:
            case 1:
               return true;
            case 2:
               return var4 == null || var4.get("java.naming.security.sasl.callback") == null;
            default:
               return false;
            }
         } else {
            d("authmech not found: ", var2);
            return false;
         }
      } else {
         d("Pooling disallowed due to tracing or unsupported pooling of protocol");
         return false;
      }
   }

   static LdapClient getLdapClient(String var0, int var1, String var2, int var3, int var4, OutputStream var5, int var6, String var7, Control[] var8, String var9, String var10, Object var11, Hashtable<?, ?> var12) throws NamingException {
      Object var13 = null;
      int var15 = findPool(var7);
      Pool var14;
      if (var15 >= 0 && (var14 = pools[var15]) != null) {
         switch(var15) {
         case 0:
            var13 = new ClientId(var6, var0, var1, var9, var8, var5, var2);
            break;
         case 1:
            var13 = new SimpleClientId(var6, var0, var1, var9, var8, var5, var2, var10, var11);
            break;
         case 2:
            var13 = new DigestClientId(var6, var0, var1, var9, var8, var5, var2, var10, var11, var12);
         }

         return (LdapClient)var14.getPooledConnection(var13, (long)var3, new LdapClientFactory(var0, var1, var2, var3, var4, var5));
      } else {
         throw new IllegalArgumentException("Attempting to use pooling for an unsupported mechanism: " + var7);
      }
   }

   public static void showStats(PrintStream var0) {
      var0.println("***** start *****");
      var0.println("idle timeout: " + idleTimeout);
      var0.println("maximum pool size: " + maxSize);
      var0.println("preferred pool size: " + prefSize);
      var0.println("initial pool size: " + initSize);
      var0.println("protocol types: " + (supportPlainProtocol ? "plain " : "") + (supportSslProtocol ? "ssl" : ""));
      var0.println("authentication types: " + (pools[0] != null ? "none " : "") + (pools[1] != null ? "simple " : "") + (pools[2] != null ? "DIGEST-MD5 " : ""));

      for(int var1 = 0; var1 < pools.length; ++var1) {
         if (pools[var1] != null) {
            var0.println((var1 == 0 ? "anonymous pools" : (var1 == 1 ? "simple auth pools" : (var1 == 2 ? "digest pools" : ""))) + ":");
            pools[var1].showStats(var0);
         }
      }

      var0.println("***** end *****");
   }

   public static void expire(long var0) {
      for(int var2 = 0; var2 < pools.length; ++var2) {
         if (pools[var2] != null) {
            pools[var2].expire(var0);
         }
      }

   }

   private static void d(String var0) {
      if (debug) {
         System.err.println("LdapPoolManager: " + var0);
      }

   }

   private static void d(String var0, String var1) {
      if (debug) {
         System.err.println("LdapPoolManager: " + var0 + var1);
      }

   }

   private static final String getProperty(final String var0, final String var1) {
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            try {
               return System.getProperty(var0, var1);
            } catch (SecurityException var2) {
               return var1;
            }
         }
      });
   }

   private static final int getInteger(final String var0, final int var1) {
      Integer var2 = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
         public Integer run() {
            try {
               return Integer.getInteger(var0, var1);
            } catch (SecurityException var2) {
               return new Integer(var1);
            }
         }
      });
      return var2;
   }

   private static final long getLong(final String var0, final long var1) {
      Long var3 = (Long)AccessController.doPrivileged(new PrivilegedAction<Long>() {
         public Long run() {
            try {
               return Long.getLong(var0, var1);
            } catch (SecurityException var2) {
               return new Long(var1);
            }
         }
      });
      return var3;
   }

   static {
      trace = debug || "fine".equalsIgnoreCase(getProperty("com.sun.jndi.ldap.connect.pool.debug", (String)null));
      supportPlainProtocol = false;
      supportSslProtocol = false;
      pools = new Pool[3];
      maxSize = getInteger("com.sun.jndi.ldap.connect.pool.maxsize", 0);
      prefSize = getInteger("com.sun.jndi.ldap.connect.pool.prefsize", 0);
      initSize = getInteger("com.sun.jndi.ldap.connect.pool.initsize", 1);
      idleTimeout = getLong("com.sun.jndi.ldap.connect.pool.timeout", 0L);
      String var0 = getProperty("com.sun.jndi.ldap.connect.pool.authentication", "none simple");
      StringTokenizer var1 = new StringTokenizer(var0);
      int var2 = var1.countTokens();

      for(int var5 = 0; var5 < var2; ++var5) {
         String var3 = var1.nextToken().toLowerCase(Locale.ENGLISH);
         if (var3.equals("anonymous")) {
            var3 = "none";
         }

         int var4 = findPool(var3);
         if (var4 >= 0 && pools[var4] == null) {
            pools[var4] = new Pool(initSize, prefSize, maxSize);
         }
      }

      var0 = getProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain");
      var1 = new StringTokenizer(var0);
      var2 = var1.countTokens();

      for(int var6 = 0; var6 < var2; ++var6) {
         String var7 = var1.nextToken();
         if ("plain".equalsIgnoreCase(var7)) {
            supportPlainProtocol = true;
         } else if ("ssl".equalsIgnoreCase(var7)) {
            supportSslProtocol = true;
         }
      }

      if (idleTimeout > 0L) {
         (new PoolCleaner(idleTimeout, pools)).start();
      }

      if (debug) {
         showStats(System.err);
      }

   }
}
