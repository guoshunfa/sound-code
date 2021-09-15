package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentEx;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceFinder<T> implements Iterable<T> {
   private static final String prefix = "META-INF/services/";
   private static WeakHashMap<ClassLoader, ConcurrentHashMap<String, ServiceFinder.ServiceName[]>> serviceNameCache = new WeakHashMap();
   private final Class<T> serviceClass;
   @Nullable
   private final ClassLoader classLoader;
   @Nullable
   private final ComponentEx component;

   public static <T> ServiceFinder<T> find(@NotNull Class<T> service, @Nullable ClassLoader loader, Component component) {
      return new ServiceFinder(service, loader, component);
   }

   public static <T> ServiceFinder<T> find(@NotNull Class<T> service, Component component) {
      return find(service, Thread.currentThread().getContextClassLoader(), component);
   }

   public static <T> ServiceFinder<T> find(@NotNull Class<T> service, @Nullable ClassLoader loader) {
      return find(service, loader, ContainerResolver.getInstance().getContainer());
   }

   public static <T> ServiceFinder<T> find(Class<T> service) {
      return find(service, Thread.currentThread().getContextClassLoader());
   }

   private ServiceFinder(Class<T> service, ClassLoader loader, Component component) {
      this.serviceClass = service;
      this.classLoader = loader;
      this.component = getComponentEx(component);
   }

   private static ServiceFinder.ServiceName[] serviceClassNames(Class serviceClass, ClassLoader classLoader) {
      ArrayList<ServiceFinder.ServiceName> l = new ArrayList();
      ServiceFinder.ServiceNameIterator it = new ServiceFinder.ServiceNameIterator(serviceClass, classLoader);

      while(it.hasNext()) {
         l.add(it.next());
      }

      return (ServiceFinder.ServiceName[])l.toArray(new ServiceFinder.ServiceName[l.size()]);
   }

   public Iterator<T> iterator() {
      Iterator<T> it = new ServiceFinder.LazyIterator(this.serviceClass, this.classLoader);
      return (Iterator)(this.component != null ? new ServiceFinder.CompositeIterator(new Iterator[]{this.component.getIterableSPI(this.serviceClass).iterator(), it}) : it);
   }

   public T[] toArray() {
      List<T> result = new ArrayList();
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         T t = var2.next();
         result.add(t);
      }

      return result.toArray((Object[])((Object[])Array.newInstance(this.serviceClass, result.size())));
   }

   private static void fail(Class service, String msg, Throwable cause) throws ServiceConfigurationError {
      ServiceConfigurationError sce = new ServiceConfigurationError(service.getName() + ": " + msg);
      sce.initCause(cause);
      throw sce;
   }

   private static void fail(Class service, String msg) throws ServiceConfigurationError {
      throw new ServiceConfigurationError(service.getName() + ": " + msg);
   }

   private static void fail(Class service, URL u, int line, String msg) throws ServiceConfigurationError {
      fail(service, u + ":" + line + ": " + msg);
   }

   private static int parseLine(Class service, URL u, BufferedReader r, int lc, List<String> names, Set<String> returned) throws IOException, ServiceConfigurationError {
      String ln = r.readLine();
      if (ln == null) {
         return -1;
      } else {
         int ci = ln.indexOf(35);
         if (ci >= 0) {
            ln = ln.substring(0, ci);
         }

         ln = ln.trim();
         int n = ln.length();
         if (n != 0) {
            if (ln.indexOf(32) >= 0 || ln.indexOf(9) >= 0) {
               fail(service, u, lc, "Illegal configuration-file syntax");
            }

            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
               fail(service, u, lc, "Illegal provider-class name: " + ln);
            }

            for(int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
               cp = ln.codePointAt(i);
               if (!Character.isJavaIdentifierPart(cp) && cp != 46) {
                  fail(service, u, lc, "Illegal provider-class name: " + ln);
               }
            }

            if (!returned.contains(ln)) {
               names.add(ln);
               returned.add(ln);
            }
         }

         return lc + 1;
      }
   }

   private static Iterator<String> parse(Class service, URL u, Set<String> returned) throws ServiceConfigurationError {
      InputStream in = null;
      BufferedReader r = null;
      ArrayList names = new ArrayList();

      try {
         in = u.openStream();
         r = new BufferedReader(new InputStreamReader(in, "utf-8"));
         int lc = 1;

         while(true) {
            if ((lc = parseLine(service, u, r, lc, names, returned)) >= 0) {
               continue;
            }
         }
      } catch (IOException var15) {
         fail(service, ": " + var15);
      } finally {
         try {
            if (r != null) {
               r.close();
            }

            if (in != null) {
               in.close();
            }
         } catch (IOException var14) {
            fail(service, ": " + var14);
         }

      }

      return names.iterator();
   }

   private static ComponentEx getComponentEx(Component component) {
      if (component instanceof ComponentEx) {
         return (ComponentEx)component;
      } else {
         return component != null ? new ServiceFinder.ComponentExWrapper(component) : null;
      }
   }

   private static class LazyIterator<T> implements Iterator<T> {
      Class<T> service;
      @Nullable
      ClassLoader loader;
      ServiceFinder.ServiceName[] names;
      int index;

      private LazyIterator(Class<T> service, ClassLoader loader) {
         this.service = service;
         this.loader = loader;
         this.names = null;
         this.index = 0;
      }

      public boolean hasNext() {
         if (this.names == null) {
            ConcurrentHashMap<String, ServiceFinder.ServiceName[]> nameMap = null;
            synchronized(ServiceFinder.serviceNameCache) {
               nameMap = (ConcurrentHashMap)ServiceFinder.serviceNameCache.get(this.loader);
            }

            this.names = nameMap != null ? (ServiceFinder.ServiceName[])nameMap.get(this.service.getName()) : null;
            if (this.names == null) {
               this.names = ServiceFinder.serviceClassNames(this.service, this.loader);
               if (nameMap == null) {
                  nameMap = new ConcurrentHashMap();
               }

               nameMap.put(this.service.getName(), this.names);
               synchronized(ServiceFinder.serviceNameCache) {
                  ServiceFinder.serviceNameCache.put(this.loader, nameMap);
               }
            }
         }

         return this.index < this.names.length;
      }

      public T next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            ServiceFinder.ServiceName sn = this.names[this.index++];
            String cn = sn.className;
            URL currentConfig = sn.config;

            try {
               return this.service.cast(Class.forName(cn, true, this.loader).newInstance());
            } catch (ClassNotFoundException var5) {
               ServiceFinder.fail(this.service, "Provider " + cn + " is specified in " + currentConfig + " but not found");
            } catch (Exception var6) {
               ServiceFinder.fail(this.service, "Provider " + cn + " is specified in " + currentConfig + "but could not be instantiated: " + var6, var6);
            }

            return null;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      LazyIterator(Class x0, ClassLoader x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class ServiceNameIterator implements Iterator<ServiceFinder.ServiceName> {
      Class service;
      @Nullable
      ClassLoader loader;
      Enumeration<URL> configs;
      Iterator<String> pending;
      Set<String> returned;
      String nextName;
      URL currentConfig;

      private ServiceNameIterator(Class service, ClassLoader loader) {
         this.configs = null;
         this.pending = null;
         this.returned = new TreeSet();
         this.nextName = null;
         this.currentConfig = null;
         this.service = service;
         this.loader = loader;
      }

      public boolean hasNext() throws ServiceConfigurationError {
         if (this.nextName != null) {
            return true;
         } else {
            if (this.configs == null) {
               try {
                  String fullName = "META-INF/services/" + this.service.getName();
                  if (this.loader == null) {
                     this.configs = ClassLoader.getSystemResources(fullName);
                  } else {
                     this.configs = this.loader.getResources(fullName);
                  }
               } catch (IOException var2) {
                  ServiceFinder.fail(this.service, ": " + var2);
               }
            }

            while(this.pending == null || !this.pending.hasNext()) {
               if (!this.configs.hasMoreElements()) {
                  return false;
               }

               this.currentConfig = (URL)this.configs.nextElement();
               this.pending = ServiceFinder.parse(this.service, this.currentConfig, this.returned);
            }

            this.nextName = (String)this.pending.next();
            return true;
         }
      }

      public ServiceFinder.ServiceName next() throws ServiceConfigurationError {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            String cn = this.nextName;
            this.nextName = null;
            return new ServiceFinder.ServiceName(cn, this.currentConfig);
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      ServiceNameIterator(Class x0, ClassLoader x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class CompositeIterator<T> implements Iterator<T> {
      private final Iterator<Iterator<T>> it;
      private Iterator<T> current = null;

      public CompositeIterator(Iterator<T>... iterators) {
         this.it = Arrays.asList(iterators).iterator();
      }

      public boolean hasNext() {
         if (this.current != null && this.current.hasNext()) {
            return true;
         } else {
            do {
               if (!this.it.hasNext()) {
                  return false;
               }

               this.current = (Iterator)this.it.next();
            } while(!this.current.hasNext());

            return true;
         }
      }

      public T next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.current.next();
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static class ComponentExWrapper implements ComponentEx {
      private final Component component;

      public ComponentExWrapper(Component component) {
         this.component = component;
      }

      public <S> S getSPI(Class<S> spiType) {
         return this.component.getSPI(spiType);
      }

      public <S> Iterable<S> getIterableSPI(Class<S> spiType) {
         S item = this.getSPI(spiType);
         if (item != null) {
            Collection<S> c = Collections.singletonList(item);
            return c;
         } else {
            return Collections.emptySet();
         }
      }
   }

   private static class ServiceName {
      final String className;
      final URL config;

      public ServiceName(String className, URL config) {
         this.className = className;
         this.config = config;
      }
   }
}
