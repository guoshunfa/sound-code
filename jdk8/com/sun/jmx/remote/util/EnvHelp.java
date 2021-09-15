package com.sun.jmx.remote.util;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.remote.security.NotificationAccessController;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class EnvHelp {
   public static final String CREDENTIAL_TYPES = "jmx.remote.rmi.server.credential.types";
   private static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
   private static final String DEFAULT_CLASS_LOADER_NAME = "jmx.remote.default.class.loader.name";
   public static final String BUFFER_SIZE_PROPERTY = "jmx.remote.x.notification.buffer.size";
   public static final String MAX_FETCH_NOTIFS = "jmx.remote.x.notification.fetch.max";
   public static final String FETCH_TIMEOUT = "jmx.remote.x.notification.fetch.timeout";
   public static final String NOTIF_ACCESS_CONTROLLER = "com.sun.jmx.remote.notification.access.controller";
   public static final String DEFAULT_ORB = "java.naming.corba.orb";
   public static final String HIDDEN_ATTRIBUTES = "jmx.remote.x.hidden.attributes";
   public static final String DEFAULT_HIDDEN_ATTRIBUTES = "java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
   private static final SortedSet<String> defaultHiddenStrings = new TreeSet();
   private static final SortedSet<String> defaultHiddenPrefixes = new TreeSet();
   public static final String SERVER_CONNECTION_TIMEOUT = "jmx.remote.x.server.connection.timeout";
   public static final String CLIENT_CONNECTION_CHECK_PERIOD = "jmx.remote.x.client.connection.check.period";
   public static final String JMX_SERVER_DAEMON = "jmx.remote.x.daemon";
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "EnvHelp");

   public static ClassLoader resolveServerClassLoader(Map<String, ?> var0, MBeanServer var1) throws InstanceNotFoundException {
      if (var0 == null) {
         return Thread.currentThread().getContextClassLoader();
      } else {
         Object var2 = var0.get("jmx.remote.default.class.loader");
         Object var3 = var0.get("jmx.remote.default.class.loader.name");
         if (var2 != null && var3 != null) {
            throw new IllegalArgumentException("Only one of jmx.remote.default.class.loader or jmx.remote.default.class.loader.name should be specified.");
         } else if (var2 == null && var3 == null) {
            return Thread.currentThread().getContextClassLoader();
         } else if (var2 != null) {
            if (var2 instanceof ClassLoader) {
               return (ClassLoader)var2;
            } else {
               String var6 = "ClassLoader object is not an instance of " + ClassLoader.class.getName() + " : " + var2.getClass().getName();
               throw new IllegalArgumentException(var6);
            }
         } else if (var3 instanceof ObjectName) {
            ObjectName var4 = (ObjectName)var3;
            if (var1 == null) {
               throw new IllegalArgumentException("Null MBeanServer object");
            } else {
               return var1.getClassLoader(var4);
            }
         } else {
            String var5 = "ClassLoader name is not an instance of " + ObjectName.class.getName() + " : " + var3.getClass().getName();
            throw new IllegalArgumentException(var5);
         }
      }
   }

   public static ClassLoader resolveClientClassLoader(Map<String, ?> var0) {
      if (var0 == null) {
         return Thread.currentThread().getContextClassLoader();
      } else {
         Object var1 = var0.get("jmx.remote.default.class.loader");
         if (var1 == null) {
            return Thread.currentThread().getContextClassLoader();
         } else if (var1 instanceof ClassLoader) {
            return (ClassLoader)var1;
         } else {
            String var2 = "ClassLoader object is not an instance of " + ClassLoader.class.getName() + " : " + var1.getClass().getName();
            throw new IllegalArgumentException(var2);
         }
      }
   }

   public static <T extends Throwable> T initCause(T var0, Throwable var1) {
      var0.initCause(var1);
      return var0;
   }

   public static Throwable getCause(Throwable var0) {
      Throwable var1 = var0;

      try {
         Method var2 = var0.getClass().getMethod("getCause", (Class[])null);
         var1 = (Throwable)var2.invoke(var0, (Object[])null);
      } catch (Exception var3) {
      }

      return var1 != null ? var1 : var0;
   }

   public static int getNotifBufferSize(Map<String, ?> var0) {
      int var1 = 1000;

      try {
         GetPropertyAction var3 = new GetPropertyAction("jmx.remote.x.notification.buffer.size");
         String var4 = (String)AccessController.doPrivileged((PrivilegedAction)var3);
         if (var4 != null) {
            var1 = Integer.parseInt(var4);
         } else {
            var3 = new GetPropertyAction("jmx.remote.x.buffer.size");
            var4 = (String)AccessController.doPrivileged((PrivilegedAction)var3);
            if (var4 != null) {
               var1 = Integer.parseInt(var4);
            }
         }
      } catch (RuntimeException var6) {
         logger.warning("getNotifBufferSize", "Can't use System property jmx.remote.x.notification.buffer.size: " + var6);
         logger.debug("getNotifBufferSize", (Throwable)var6);
      }

      int var7 = var1;

      try {
         if (var0.containsKey("jmx.remote.x.notification.buffer.size")) {
            var7 = (int)getIntegerAttribute(var0, "jmx.remote.x.notification.buffer.size", (long)var1, 0L, 2147483647L);
         } else {
            var7 = (int)getIntegerAttribute(var0, "jmx.remote.x.buffer.size", (long)var1, 0L, 2147483647L);
         }
      } catch (RuntimeException var5) {
         logger.warning("getNotifBufferSize", "Can't determine queuesize (using default): " + var5);
         logger.debug("getNotifBufferSize", (Throwable)var5);
      }

      return var7;
   }

   public static int getMaxFetchNotifNumber(Map<String, ?> var0) {
      return (int)getIntegerAttribute(var0, "jmx.remote.x.notification.fetch.max", 1000L, 1L, 2147483647L);
   }

   public static long getFetchTimeout(Map<String, ?> var0) {
      return getIntegerAttribute(var0, "jmx.remote.x.notification.fetch.timeout", 60000L, 0L, Long.MAX_VALUE);
   }

   public static NotificationAccessController getNotificationAccessController(Map<String, ?> var0) {
      return var0 == null ? null : (NotificationAccessController)var0.get("com.sun.jmx.remote.notification.access.controller");
   }

   public static long getIntegerAttribute(Map<String, ?> var0, String var1, long var2, long var4, long var6) {
      Object var8;
      if (var0 != null && (var8 = var0.get(var1)) != null) {
         long var9;
         String var11;
         if (var8 instanceof Number) {
            var9 = ((Number)var8).longValue();
         } else {
            if (!(var8 instanceof String)) {
               var11 = "Attribute " + var1 + " value must be Integer or String: " + var8;
               throw new IllegalArgumentException(var11);
            }

            var9 = Long.parseLong((String)var8);
         }

         if (var9 < var4) {
            var11 = "Attribute " + var1 + " value must be at least " + var4 + ": " + var9;
            throw new IllegalArgumentException(var11);
         } else if (var9 > var6) {
            var11 = "Attribute " + var1 + " value must be at most " + var6 + ": " + var9;
            throw new IllegalArgumentException(var11);
         } else {
            return var9;
         }
      } else {
         return var2;
      }
   }

   public static void checkAttributes(Map<?, ?> var0) {
      Iterator var1 = var0.keySet().iterator();

      Object var2;
      do {
         if (!var1.hasNext()) {
            return;
         }

         var2 = var1.next();
      } while(var2 instanceof String);

      String var3 = "Attributes contain key that is not a string: " + var2;
      throw new IllegalArgumentException(var3);
   }

   public static <V> Map<String, V> filterAttributes(Map<String, V> var0) {
      if (logger.traceOn()) {
         logger.trace("filterAttributes", "starts");
      }

      TreeMap var1 = new TreeMap(var0);
      purgeUnserializable(var1.values());
      hideAttributes(var1);
      return var1;
   }

   private static void purgeUnserializable(Collection<?> var0) {
      logger.trace("purgeUnserializable", "starts");
      ObjectOutputStream var1 = null;
      int var2 = 0;

      for(Iterator var3 = var0.iterator(); var3.hasNext(); ++var2) {
         Object var4 = var3.next();
         if (var4 != null && !(var4 instanceof String)) {
            try {
               if (var1 == null) {
                  var1 = new ObjectOutputStream(new EnvHelp.SinkOutputStream());
               }

               var1.writeObject(var4);
               if (logger.traceOn()) {
                  logger.trace("purgeUnserializable", "Value serializable: " + var4);
               }
            } catch (IOException var6) {
               if (logger.traceOn()) {
                  logger.trace("purgeUnserializable", "Value not serializable: " + var4 + ": " + var6);
               }

               var3.remove();
               var1 = null;
            }
         } else if (logger.traceOn()) {
            logger.trace("purgeUnserializable", "Value trivially serializable: " + var4);
         }
      }

   }

   private static void hideAttributes(SortedMap<String, ?> var0) {
      if (!var0.isEmpty()) {
         String var3 = (String)var0.get("jmx.remote.x.hidden.attributes");
         Object var1;
         Object var2;
         if (var3 != null) {
            if (var3.startsWith("=")) {
               var3 = var3.substring(1);
            } else {
               var3 = var3 + " java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
            }

            var1 = new TreeSet();
            var2 = new TreeSet();
            parseHiddenAttributes(var3, (SortedSet)var1, (SortedSet)var2);
         } else {
            var3 = "java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
            synchronized(defaultHiddenStrings) {
               if (defaultHiddenStrings.isEmpty()) {
                  parseHiddenAttributes(var3, defaultHiddenStrings, defaultHiddenPrefixes);
               }

               var1 = defaultHiddenStrings;
               var2 = defaultHiddenPrefixes;
            }
         }

         String var4 = (String)var0.lastKey() + "X";
         Iterator var5 = var0.keySet().iterator();
         Iterator var6 = ((SortedSet)var1).iterator();
         Iterator var7 = ((SortedSet)var2).iterator();
         String var8;
         if (var6.hasNext()) {
            var8 = (String)var6.next();
         } else {
            var8 = var4;
         }

         String var9;
         if (var7.hasNext()) {
            var9 = (String)var7.next();
         } else {
            var9 = var4;
         }

         while(true) {
            while(var5.hasNext()) {
               String var10 = (String)var5.next();
               boolean var11 = true;

               int var13;
               while((var13 = var8.compareTo(var10)) < 0) {
                  if (var6.hasNext()) {
                     var8 = (String)var6.next();
                  } else {
                     var8 = var4;
                  }
               }

               if (var13 == 0) {
                  var5.remove();
               } else {
                  while(var9.compareTo(var10) <= 0) {
                     if (var10.startsWith(var9)) {
                        var5.remove();
                        break;
                     }

                     if (var7.hasNext()) {
                        var9 = (String)var7.next();
                     } else {
                        var9 = var4;
                     }
                  }
               }
            }

            return;
         }
      }
   }

   private static void parseHiddenAttributes(String var0, SortedSet<String> var1, SortedSet<String> var2) {
      StringTokenizer var3 = new StringTokenizer(var0);

      while(var3.hasMoreTokens()) {
         String var4 = var3.nextToken();
         if (var4.endsWith("*")) {
            var2.add(var4.substring(0, var4.length() - 1));
         } else {
            var1.add(var4);
         }
      }

   }

   public static long getServerConnectionTimeout(Map<String, ?> var0) {
      return getIntegerAttribute(var0, "jmx.remote.x.server.connection.timeout", 120000L, 0L, Long.MAX_VALUE);
   }

   public static long getConnectionCheckPeriod(Map<String, ?> var0) {
      return getIntegerAttribute(var0, "jmx.remote.x.client.connection.check.period", 60000L, 0L, Long.MAX_VALUE);
   }

   public static boolean computeBooleanFromString(String var0) {
      return computeBooleanFromString(var0, false);
   }

   public static boolean computeBooleanFromString(String var0, boolean var1) {
      if (var0 == null) {
         return var1;
      } else if (var0.equalsIgnoreCase("true")) {
         return true;
      } else if (var0.equalsIgnoreCase("false")) {
         return false;
      } else {
         throw new IllegalArgumentException("Property value must be \"true\" or \"false\" instead of \"" + var0 + "\"");
      }
   }

   public static <K, V> Hashtable<K, V> mapToHashtable(Map<K, V> var0) {
      HashMap var1 = new HashMap(var0);
      if (var1.containsKey((Object)null)) {
         var1.remove((Object)null);
      }

      Iterator var2 = var1.values().iterator();

      while(var2.hasNext()) {
         if (var2.next() == null) {
            var2.remove();
         }
      }

      return new Hashtable(var1);
   }

   public static boolean isServerDaemon(Map<String, ?> var0) {
      return var0 != null && "true".equalsIgnoreCase((String)var0.get("jmx.remote.x.daemon"));
   }

   private static final class SinkOutputStream extends OutputStream {
      private SinkOutputStream() {
      }

      public void write(byte[] var1, int var2, int var3) {
      }

      public void write(int var1) {
      }

      // $FF: synthetic method
      SinkOutputStream(Object var1) {
         this();
      }
   }
}
