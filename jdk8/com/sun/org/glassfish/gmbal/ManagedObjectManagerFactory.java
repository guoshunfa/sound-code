package com.sun.org.glassfish.gmbal;

import com.sun.org.glassfish.gmbal.util.GenericConstructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.ObjectName;

public final class ManagedObjectManagerFactory {
   private static GenericConstructor<ManagedObjectManager> objectNameCons = new GenericConstructor(ManagedObjectManager.class, "com.sun.org.glassfish.gmbal.impl.ManagedObjectManagerImpl", new Class[]{ObjectName.class});
   private static GenericConstructor<ManagedObjectManager> stringCons = new GenericConstructor(ManagedObjectManager.class, "com.sun.org.glassfish.gmbal.impl.ManagedObjectManagerImpl", new Class[]{String.class});

   private ManagedObjectManagerFactory() {
   }

   public static Method getMethod(final Class<?> cls, final String name, final Class<?>... types) {
      try {
         return (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return cls.getDeclaredMethod(name, types);
            }
         });
      } catch (PrivilegedActionException var4) {
         throw new GmbalException("Unexpected exception", var4);
      } catch (SecurityException var5) {
         throw new GmbalException("Unexpected exception", var5);
      }
   }

   public static ManagedObjectManager createStandalone(String domain) {
      ManagedObjectManager result = (ManagedObjectManager)stringCons.create(domain);
      return result == null ? ManagedObjectManagerNOPImpl.self : result;
   }

   public static ManagedObjectManager createFederated(ObjectName rootParentName) {
      ManagedObjectManager result = (ManagedObjectManager)objectNameCons.create(rootParentName);
      return result == null ? ManagedObjectManagerNOPImpl.self : result;
   }

   public static ManagedObjectManager createNOOP() {
      return ManagedObjectManagerNOPImpl.self;
   }
}
