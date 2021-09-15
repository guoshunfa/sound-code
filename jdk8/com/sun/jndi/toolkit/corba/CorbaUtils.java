package com.sun.jndi.toolkit.corba;

import com.sun.jndi.cosnaming.CNCtx;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.ConfigurationException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;

public class CorbaUtils {
   private static Method toStubMethod = null;
   private static Method connectMethod = null;
   private static Class<?> corbaStubClass = null;

   public static Object remoteToCorba(Remote var0, ORB var1) throws ClassNotFoundException, ConfigurationException {
      Class var2 = CorbaUtils.class;
      synchronized(CorbaUtils.class) {
         if (toStubMethod == null) {
            initMethodHandles();
         }
      }

      ConfigurationException var4;
      ConfigurationException var5;
      java.lang.Object var11;
      Throwable var12;
      try {
         var11 = toStubMethod.invoke((java.lang.Object)null, var0);
      } catch (InvocationTargetException var6) {
         var12 = var6.getTargetException();
         var5 = new ConfigurationException("Problem with PortableRemoteObject.toStub(); object not exported or stub not found");
         var5.setRootCause(var12);
         throw var5;
      } catch (IllegalAccessException var7) {
         var4 = new ConfigurationException("Cannot invoke javax.rmi.PortableRemoteObject.toStub(java.rmi.Remote)");
         var4.setRootCause(var7);
         throw var4;
      }

      if (!corbaStubClass.isInstance(var11)) {
         return null;
      } else {
         try {
            connectMethod.invoke(var11, var1);
         } catch (InvocationTargetException var8) {
            var12 = var8.getTargetException();
            if (!(var12 instanceof RemoteException)) {
               var5 = new ConfigurationException("Problem invoking javax.rmi.CORBA.Stub.connect()");
               var5.setRootCause(var12);
               throw var5;
            }
         } catch (IllegalAccessException var9) {
            var4 = new ConfigurationException("Cannot invoke javax.rmi.CORBA.Stub.connect()");
            var4.setRootCause(var9);
            throw var4;
         }

         return (Object)var11;
      }
   }

   public static ORB getOrb(String var0, int var1, Hashtable<?, ?> var2) {
      Properties var3;
      if (var2 != null) {
         if (var2 instanceof Properties) {
            var3 = (Properties)var2.clone();
         } else {
            var3 = new Properties();
            Enumeration var4 = var2.keys();

            while(var4.hasMoreElements()) {
               String var5 = (String)var4.nextElement();
               java.lang.Object var6 = var2.get(var5);
               if (var6 instanceof String) {
                  var3.put(var5, var6);
               }
            }
         }
      } else {
         var3 = new Properties();
      }

      if (var0 != null) {
         var3.put("org.omg.CORBA.ORBInitialHost", var0);
      }

      if (var1 >= 0) {
         var3.put("org.omg.CORBA.ORBInitialPort", "" + var1);
      }

      if (var2 != null) {
         java.lang.Object var7 = var2.get("java.naming.applet");
         if (var7 != null) {
            return initAppletORB(var7, var3);
         }
      }

      return ORB.init(new String[0], var3);
   }

   public static boolean isObjectFactoryTrusted(java.lang.Object var0) throws NamingException {
      Reference var1 = null;
      if (var0 instanceof Reference) {
         var1 = (Reference)var0;
      } else if (var0 instanceof Referenceable) {
         var1 = ((Referenceable)((Referenceable)var0)).getReference();
      }

      if (var1 != null && var1.getFactoryClassLocation() != null && !CNCtx.trustURLCodebase) {
         throw new ConfigurationException("The object factory is untrusted. Set the system property 'com.sun.jndi.cosnaming.object.trustURLCodebase' to 'true'.");
      } else {
         return true;
      }
   }

   private static ORB initAppletORB(java.lang.Object var0, Properties var1) {
      try {
         Class var2 = Class.forName("java.applet.Applet", true, (ClassLoader)null);
         if (!var2.isInstance(var0)) {
            throw new ClassCastException(var0.getClass().getName());
         } else {
            Method var8 = ORB.class.getMethod("init", var2, Properties.class);
            return (ORB)var8.invoke((java.lang.Object)null, var0, var1);
         }
      } catch (ClassNotFoundException var4) {
         throw new ClassCastException(var0.getClass().getName());
      } catch (NoSuchMethodException var5) {
         throw new AssertionError(var5);
      } catch (InvocationTargetException var6) {
         Throwable var3 = var6.getCause();
         if (var3 instanceof RuntimeException) {
            throw (RuntimeException)var3;
         } else if (var3 instanceof Error) {
            throw (Error)var3;
         } else {
            throw new AssertionError(var6);
         }
      } catch (IllegalAccessException var7) {
         throw new AssertionError(var7);
      }
   }

   private static void initMethodHandles() throws ClassNotFoundException {
      corbaStubClass = Class.forName("javax.rmi.CORBA.Stub");

      try {
         connectMethod = corbaStubClass.getMethod("connect", ORB.class);
      } catch (NoSuchMethodException var3) {
         throw new IllegalStateException("No method definition for javax.rmi.CORBA.Stub.connect(org.omg.CORBA.ORB)");
      }

      Class var0 = Class.forName("javax.rmi.PortableRemoteObject");

      try {
         toStubMethod = var0.getMethod("toStub", Remote.class);
      } catch (NoSuchMethodException var2) {
         throw new IllegalStateException("No method definition for javax.rmi.PortableRemoteObject.toStub(java.rmi.Remote)");
      }
   }
}
