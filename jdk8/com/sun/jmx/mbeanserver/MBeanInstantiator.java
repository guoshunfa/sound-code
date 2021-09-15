package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.logging.Level;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import sun.reflect.misc.ConstructorUtil;
import sun.reflect.misc.ReflectUtil;

public class MBeanInstantiator {
   private final ModifiableClassLoaderRepository clr;
   private static final Map<String, Class<?>> primitiveClasses = Util.newMap();

   MBeanInstantiator(ModifiableClassLoaderRepository var1) {
      this.clr = var1;
   }

   public void testCreation(Class<?> var1) throws NotCompliantMBeanException {
      Introspector.testCreation(var1);
   }

   public Class<?> findClassWithDefaultLoaderRepository(String var1) throws ReflectionException {
      if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation");
      } else {
         ReflectUtil.checkPackageAccess(var1);

         try {
            if (this.clr == null) {
               throw new ClassNotFoundException(var1);
            } else {
               Class var2 = this.clr.loadClass(var1);
               return var2;
            }
         } catch (ClassNotFoundException var4) {
            throw new ReflectionException(var4, "The MBean class could not be loaded by the default loader repository");
         }
      }
   }

   public Class<?> findClass(String var1, ClassLoader var2) throws ReflectionException {
      return loadClass(var1, var2);
   }

   public Class<?> findClass(String var1, ObjectName var2) throws ReflectionException, InstanceNotFoundException {
      if (var2 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException(), "Null loader passed in parameter");
      } else {
         ClassLoader var3 = null;
         synchronized(this) {
            var3 = this.getClassLoader(var2);
         }

         if (var3 == null) {
            throw new InstanceNotFoundException("The loader named " + var2 + " is not registered in the MBeanServer");
         } else {
            return this.findClass(var1, var3);
         }
      }
   }

   public Class<?>[] findSignatureClasses(String[] var1, ClassLoader var2) throws ReflectionException {
      if (var1 == null) {
         return null;
      } else {
         ClassLoader var3 = var2;
         int var4 = var1.length;
         Class[] var5 = new Class[var4];
         if (var4 == 0) {
            return var5;
         } else {
            try {
               for(int var6 = 0; var6 < var4; ++var6) {
                  Class var7 = (Class)primitiveClasses.get(var1[var6]);
                  if (var7 != null) {
                     var5[var6] = var7;
                  } else {
                     ReflectUtil.checkPackageAccess(var1[var6]);
                     if (var3 != null) {
                        var5[var6] = Class.forName(var1[var6], false, var3);
                     } else {
                        var5[var6] = this.findClass(var1[var6], this.getClass().getClassLoader());
                     }
                  }
               }

               return var5;
            } catch (ClassNotFoundException var8) {
               if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", (String)"The parameter class could not be found", (Throwable)var8);
               }

               throw new ReflectionException(var8, "The parameter class could not be found");
            } catch (RuntimeException var9) {
               if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", (String)"Unexpected exception", (Throwable)var9);
               }

               throw var9;
            }
         }
      }
   }

   public Object instantiate(Class<?> var1) throws ReflectionException, MBeanException {
      checkMBeanPermission((Class)var1, (String)null, (ObjectName)null, "instantiate");
      Constructor var3 = this.findConstructor(var1, (Class[])null);
      if (var3 == null) {
         throw new ReflectionException(new NoSuchMethodException("No such constructor"));
      } else {
         try {
            ReflectUtil.checkPackageAccess(var1);
            ensureClassAccess(var1);
            Object var2 = var3.newInstance();
            return var2;
         } catch (InvocationTargetException var6) {
            Throwable var5 = var6.getTargetException();
            if (var5 instanceof RuntimeException) {
               throw new RuntimeMBeanException((RuntimeException)var5, "RuntimeException thrown in the MBean's empty constructor");
            } else if (var5 instanceof Error) {
               throw new RuntimeErrorException((Error)var5, "Error thrown in the MBean's empty constructor");
            } else {
               throw new MBeanException((Exception)var5, "Exception thrown in the MBean's empty constructor");
            }
         } catch (NoSuchMethodError var7) {
            throw new ReflectionException(new NoSuchMethodException("No constructor"), "No such constructor");
         } catch (InstantiationException var8) {
            throw new ReflectionException(var8, "Exception thrown trying to invoke the MBean's empty constructor");
         } catch (IllegalAccessException var9) {
            throw new ReflectionException(var9, "Exception thrown trying to invoke the MBean's empty constructor");
         } catch (IllegalArgumentException var10) {
            throw new ReflectionException(var10, "Exception thrown trying to invoke the MBean's empty constructor");
         }
      }
   }

   public Object instantiate(Class<?> var1, Object[] var2, String[] var3, ClassLoader var4) throws ReflectionException, MBeanException {
      checkMBeanPermission((Class)var1, (String)null, (ObjectName)null, "instantiate");

      Class[] var5;
      try {
         ClassLoader var7 = var1.getClassLoader();
         var5 = var3 == null ? null : this.findSignatureClasses(var3, var7);
      } catch (IllegalArgumentException var10) {
         throw new ReflectionException(var10, "The constructor parameter classes could not be loaded");
      }

      Constructor var15 = this.findConstructor(var1, var5);
      if (var15 == null) {
         throw new ReflectionException(new NoSuchMethodException("No such constructor"));
      } else {
         try {
            ReflectUtil.checkPackageAccess(var1);
            ensureClassAccess(var1);
            Object var6 = var15.newInstance(var2);
            return var6;
         } catch (NoSuchMethodError var11) {
            throw new ReflectionException(new NoSuchMethodException("No such constructor found"), "No such constructor");
         } catch (InstantiationException var12) {
            throw new ReflectionException(var12, "Exception thrown trying to invoke the MBean's constructor");
         } catch (IllegalAccessException var13) {
            throw new ReflectionException(var13, "Exception thrown trying to invoke the MBean's constructor");
         } catch (InvocationTargetException var14) {
            Throwable var9 = var14.getTargetException();
            if (var9 instanceof RuntimeException) {
               throw new RuntimeMBeanException((RuntimeException)var9, "RuntimeException thrown in the MBean's constructor");
            } else if (var9 instanceof Error) {
               throw new RuntimeErrorException((Error)var9, "Error thrown in the MBean's constructor");
            } else {
               throw new MBeanException((Exception)var9, "Exception thrown in the MBean's constructor");
            }
         }
      }
   }

   public ObjectInputStream deserialize(ClassLoader var1, byte[] var2) throws OperationsException {
      if (var2 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter");
      } else if (var2.length == 0) {
         throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter");
      } else {
         ByteArrayInputStream var3 = new ByteArrayInputStream(var2);

         try {
            ObjectInputStreamWithLoader var4 = new ObjectInputStreamWithLoader(var3, var1);
            return var4;
         } catch (IOException var6) {
            throw new OperationsException("An IOException occurred trying to de-serialize the data");
         }
      }
   }

   public ObjectInputStream deserialize(String var1, ObjectName var2, byte[] var3, ClassLoader var4) throws InstanceNotFoundException, OperationsException, ReflectionException {
      if (var3 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter");
      } else if (var3.length == 0) {
         throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter");
      } else if (var1 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter");
      } else {
         ReflectUtil.checkPackageAccess(var1);
         Class var5;
         if (var2 == null) {
            var5 = this.findClass(var1, var4);
         } else {
            try {
               ClassLoader var6 = null;
               var6 = this.getClassLoader(var2);
               if (var6 == null) {
                  throw new ClassNotFoundException(var1);
               }

               var5 = Class.forName(var1, false, var6);
            } catch (ClassNotFoundException var10) {
               throw new ReflectionException(var10, "The MBean class could not be loaded by the " + var2.toString() + " class loader");
            }
         }

         ByteArrayInputStream var11 = new ByteArrayInputStream(var3);

         try {
            ObjectInputStreamWithLoader var7 = new ObjectInputStreamWithLoader(var11, var5.getClassLoader());
            return var7;
         } catch (IOException var9) {
            throw new OperationsException("An IOException occurred trying to de-serialize the data");
         }
      }
   }

   public Object instantiate(String var1) throws ReflectionException, MBeanException {
      return this.instantiate((String)var1, (Object[])null, (String[])null, (ClassLoader)null);
   }

   public Object instantiate(String var1, ObjectName var2, ClassLoader var3) throws ReflectionException, MBeanException, InstanceNotFoundException {
      return this.instantiate(var1, var2, (Object[])null, (String[])null, var3);
   }

   public Object instantiate(String var1, Object[] var2, String[] var3, ClassLoader var4) throws ReflectionException, MBeanException {
      Class var5 = this.findClassWithDefaultLoaderRepository(var1);
      return this.instantiate(var5, var2, var3, var4);
   }

   public Object instantiate(String var1, ObjectName var2, Object[] var3, String[] var4, ClassLoader var5) throws ReflectionException, MBeanException, InstanceNotFoundException {
      Class var6;
      if (var2 == null) {
         var6 = this.findClass(var1, var5);
      } else {
         var6 = this.findClass(var1, var2);
      }

      return this.instantiate(var6, var3, var4, var5);
   }

   public ModifiableClassLoaderRepository getClassLoaderRepository() {
      checkMBeanPermission((String)((String)null), (String)null, (ObjectName)null, "getClassLoaderRepository");
      return this.clr;
   }

   static Class<?> loadClass(String var0, ClassLoader var1) throws ReflectionException {
      if (var0 == null) {
         throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation");
      } else {
         ReflectUtil.checkPackageAccess(var0);

         try {
            if (var1 == null) {
               var1 = MBeanInstantiator.class.getClassLoader();
            }

            Class var2;
            if (var1 != null) {
               var2 = Class.forName(var0, false, var1);
            } else {
               var2 = Class.forName(var0);
            }

            return var2;
         } catch (ClassNotFoundException var4) {
            throw new ReflectionException(var4, "The MBean class could not be loaded");
         }
      }
   }

   static Class<?>[] loadSignatureClasses(String[] var0, ClassLoader var1) throws ReflectionException {
      if (var0 == null) {
         return null;
      } else {
         ClassLoader var2 = var1 == null ? MBeanInstantiator.class.getClassLoader() : var1;
         int var3 = var0.length;
         Class[] var4 = new Class[var3];
         if (var3 == 0) {
            return var4;
         } else {
            try {
               for(int var5 = 0; var5 < var3; ++var5) {
                  Class var6 = (Class)primitiveClasses.get(var0[var5]);
                  if (var6 != null) {
                     var4[var5] = var6;
                  } else {
                     ReflectUtil.checkPackageAccess(var0[var5]);
                     var4[var5] = Class.forName(var0[var5], false, var2);
                  }
               }

               return var4;
            } catch (ClassNotFoundException var7) {
               if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", (String)"The parameter class could not be found", (Throwable)var7);
               }

               throw new ReflectionException(var7, "The parameter class could not be found");
            } catch (RuntimeException var8) {
               if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", (String)"Unexpected exception", (Throwable)var8);
               }

               throw var8;
            }
         }
      }
   }

   private Constructor<?> findConstructor(Class<?> var1, Class<?>[] var2) {
      try {
         return ConstructorUtil.getConstructor(var1, var2);
      } catch (Exception var4) {
         return null;
      }
   }

   private static void checkMBeanPermission(Class<?> var0, String var1, ObjectName var2, String var3) {
      if (var0 != null) {
         checkMBeanPermission(var0.getName(), var1, var2, var3);
      }

   }

   private static void checkMBeanPermission(String var0, String var1, ObjectName var2, String var3) throws SecurityException {
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         MBeanPermission var5 = new MBeanPermission(var0, var1, var2, var3);
         var4.checkPermission(var5);
      }

   }

   private static void ensureClassAccess(Class var0) throws IllegalAccessException {
      int var1 = var0.getModifiers();
      if (!Modifier.isPublic(var1)) {
         throw new IllegalAccessException("Class is not public and can't be instantiated");
      }
   }

   private ClassLoader getClassLoader(final ObjectName var1) {
      if (this.clr == null) {
         return null;
      } else {
         Permissions var2 = new Permissions();
         var2.add(new MBeanPermission("*", (String)null, var1, "getClassLoader"));
         ProtectionDomain var3 = new ProtectionDomain((CodeSource)null, var2);
         ProtectionDomain[] var4 = new ProtectionDomain[]{var3};
         AccessControlContext var5 = new AccessControlContext(var4);
         ClassLoader var6 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               return MBeanInstantiator.this.clr.getClassLoader(var1);
            }
         }, var5);
         return var6;
      }
   }

   static {
      Class[] var0 = new Class[]{Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Character.TYPE, Boolean.TYPE};
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Class var3 = var0[var2];
         primitiveClasses.put(var3.getName(), var3);
      }

   }
}
