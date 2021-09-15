package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.management.MBeanPermission;
import javax.management.ObjectName;
import javax.management.loading.PrivateClassLoader;
import sun.reflect.misc.ReflectUtil;

final class ClassLoaderRepositorySupport implements ModifiableClassLoaderRepository {
   private static final ClassLoaderRepositorySupport.LoaderEntry[] EMPTY_LOADER_ARRAY = new ClassLoaderRepositorySupport.LoaderEntry[0];
   private ClassLoaderRepositorySupport.LoaderEntry[] loaders;
   private final Map<String, List<ClassLoader>> search;
   private final Map<ObjectName, ClassLoader> loadersWithNames;

   ClassLoaderRepositorySupport() {
      this.loaders = EMPTY_LOADER_ARRAY;
      this.search = new Hashtable(10);
      this.loadersWithNames = new Hashtable(10);
   }

   private synchronized boolean add(ObjectName var1, ClassLoader var2) {
      ArrayList var3 = new ArrayList(Arrays.asList(this.loaders));
      var3.add(new ClassLoaderRepositorySupport.LoaderEntry(var1, var2));
      this.loaders = (ClassLoaderRepositorySupport.LoaderEntry[])var3.toArray(EMPTY_LOADER_ARRAY);
      return true;
   }

   private synchronized boolean remove(ObjectName var1, ClassLoader var2) {
      int var3 = this.loaders.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ClassLoaderRepositorySupport.LoaderEntry var5 = this.loaders[var4];
         boolean var6 = var1 == null ? var2 == var5.loader : var1.equals(var5.name);
         if (var6) {
            ClassLoaderRepositorySupport.LoaderEntry[] var7 = new ClassLoaderRepositorySupport.LoaderEntry[var3 - 1];
            System.arraycopy(this.loaders, 0, var7, 0, var4);
            System.arraycopy(this.loaders, var4 + 1, var7, var4, var3 - 1 - var4);
            this.loaders = var7;
            return true;
         }
      }

      return false;
   }

   public final Class<?> loadClass(String var1) throws ClassNotFoundException {
      return this.loadClass(this.loaders, var1, (ClassLoader)null, (ClassLoader)null);
   }

   public final Class<?> loadClassWithout(ClassLoader var1, String var2) throws ClassNotFoundException {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClassWithout", var2 + " without " + var1);
      }

      if (var1 == null) {
         return this.loadClass(this.loaders, var2, (ClassLoader)null, (ClassLoader)null);
      } else {
         this.startValidSearch(var1, var2);

         Class var3;
         try {
            var3 = this.loadClass(this.loaders, var2, var1, (ClassLoader)null);
         } finally {
            this.stopValidSearch(var1, var2);
         }

         return var3;
      }
   }

   public final Class<?> loadClassBefore(ClassLoader var1, String var2) throws ClassNotFoundException {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClassBefore", var2 + " before " + var1);
      }

      if (var1 == null) {
         return this.loadClass(this.loaders, var2, (ClassLoader)null, (ClassLoader)null);
      } else {
         this.startValidSearch(var1, var2);

         Class var3;
         try {
            var3 = this.loadClass(this.loaders, var2, (ClassLoader)null, var1);
         } finally {
            this.stopValidSearch(var1, var2);
         }

         return var3;
      }
   }

   private Class<?> loadClass(ClassLoaderRepositorySupport.LoaderEntry[] var1, String var2, ClassLoader var3, ClassLoader var4) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(var2);
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         try {
            ClassLoader var7 = var1[var6].loader;
            if (var7 == null) {
               return Class.forName(var2, false, (ClassLoader)null);
            }

            if (var7 != var3) {
               if (var7 != var4) {
                  if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                     JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClass", "Trying loader = " + var7);
                  }

                  return Class.forName(var2, false, var7);
               }
               break;
            }
         } catch (ClassNotFoundException var8) {
         }
      }

      throw new ClassNotFoundException(var2);
   }

   private synchronized void startValidSearch(ClassLoader var1, String var2) throws ClassNotFoundException {
      Object var3 = (List)this.search.get(var2);
      if (var3 != null && ((List)var3).contains(var1)) {
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "startValidSearch", "Already requested loader = " + var1 + " class = " + var2);
         }

         throw new ClassNotFoundException(var2);
      } else {
         if (var3 == null) {
            var3 = new ArrayList(1);
            this.search.put(var2, var3);
         }

         ((List)var3).add(var1);
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "startValidSearch", "loader = " + var1 + " class = " + var2);
         }

      }
   }

   private synchronized void stopValidSearch(ClassLoader var1, String var2) {
      List var3 = (List)this.search.get(var2);
      if (var3 != null) {
         var3.remove(var1);
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "stopValidSearch", "loader = " + var1 + " class = " + var2);
         }
      }

   }

   public final void addClassLoader(ClassLoader var1) {
      this.add((ObjectName)null, var1);
   }

   public final void removeClassLoader(ClassLoader var1) {
      this.remove((ObjectName)null, var1);
   }

   public final synchronized void addClassLoader(ObjectName var1, ClassLoader var2) {
      this.loadersWithNames.put(var1, var2);
      if (!(var2 instanceof PrivateClassLoader)) {
         this.add(var1, var2);
      }

   }

   public final synchronized void removeClassLoader(ObjectName var1) {
      ClassLoader var2 = (ClassLoader)this.loadersWithNames.remove(var1);
      if (!(var2 instanceof PrivateClassLoader)) {
         this.remove(var1, var2);
      }

   }

   public final ClassLoader getClassLoader(ObjectName var1) {
      ClassLoader var2 = (ClassLoader)this.loadersWithNames.get(var1);
      if (var2 != null) {
         SecurityManager var3 = System.getSecurityManager();
         if (var3 != null) {
            MBeanPermission var4 = new MBeanPermission(var2.getClass().getName(), (String)null, var1, "getClassLoader");
            var3.checkPermission(var4);
         }
      }

      return var2;
   }

   private static class LoaderEntry {
      ObjectName name;
      ClassLoader loader;

      LoaderEntry(ObjectName var1, ClassLoader var2) {
         this.name = var1;
         this.loader = var2;
      }
   }
}
