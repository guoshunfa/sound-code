package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public final class IIOPHelper {
   private static final String IMPL_CLASS = "com.sun.jmx.remote.protocol.iiop.IIOPProxyImpl";
   private static final IIOPProxy proxy = (IIOPProxy)AccessController.doPrivileged(new PrivilegedAction<IIOPProxy>() {
      public IIOPProxy run() {
         try {
            Class var1 = Class.forName("com.sun.jmx.remote.protocol.iiop.IIOPProxyImpl", true, IIOPHelper.class.getClassLoader());
            return (IIOPProxy)var1.newInstance();
         } catch (ClassNotFoundException var2) {
            return null;
         } catch (InstantiationException var3) {
            throw new AssertionError(var3);
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         }
      }
   });

   private IIOPHelper() {
   }

   public static boolean isAvailable() {
      return proxy != null;
   }

   private static void ensureAvailable() {
      if (proxy == null) {
         throw new AssertionError("Should not here");
      }
   }

   public static boolean isStub(Object var0) {
      return proxy == null ? false : proxy.isStub(var0);
   }

   public static Object getDelegate(Object var0) {
      ensureAvailable();
      return proxy.getDelegate(var0);
   }

   public static void setDelegate(Object var0, Object var1) {
      ensureAvailable();
      proxy.setDelegate(var0, var1);
   }

   public static Object getOrb(Object var0) {
      ensureAvailable();
      return proxy.getOrb(var0);
   }

   public static void connect(Object var0, Object var1) throws IOException {
      if (proxy == null) {
         throw new IOException("Connection to ORB failed, RMI/IIOP not available");
      } else {
         proxy.connect(var0, var1);
      }
   }

   public static boolean isOrb(Object var0) {
      return proxy == null ? false : proxy.isOrb(var0);
   }

   public static Object createOrb(String[] var0, Properties var1) throws IOException {
      if (proxy == null) {
         throw new IOException("ORB initialization failed, RMI/IIOP not available");
      } else {
         return proxy.createOrb(var0, var1);
      }
   }

   public static Object stringToObject(Object var0, String var1) {
      ensureAvailable();
      return proxy.stringToObject(var0, var1);
   }

   public static String objectToString(Object var0, Object var1) {
      ensureAvailable();
      return proxy.objectToString(var0, var1);
   }

   public static <T> T narrow(Object var0, Class<T> var1) {
      ensureAvailable();
      return proxy.narrow(var0, var1);
   }

   public static void exportObject(Remote var0) throws IOException {
      if (proxy == null) {
         throw new IOException("RMI object cannot be exported, RMI/IIOP not available");
      } else {
         proxy.exportObject(var0);
      }
   }

   public static void unexportObject(Remote var0) throws IOException {
      if (proxy == null) {
         throw new NoSuchObjectException("Object not exported");
      } else {
         proxy.unexportObject(var0);
      }
   }

   public static Remote toStub(Remote var0) throws IOException {
      if (proxy == null) {
         throw new NoSuchObjectException("Object not exported");
      } else {
         return proxy.toStub(var0);
      }
   }
}
