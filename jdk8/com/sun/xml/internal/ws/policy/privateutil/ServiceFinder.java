package com.sun.xml.internal.ws.policy.privateutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

final class ServiceFinder<T> implements Iterable<T> {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ServiceFinder.class);
   private static final String prefix = "META-INF/services/";
   private final Class<T> serviceClass;
   private final ClassLoader classLoader;

   static <T> ServiceFinder<T> find(Class<T> service, ClassLoader loader) {
      if (null == service) {
         throw (NullPointerException)LOGGER.logSevereException(new NullPointerException(LocalizationMessages.WSP_0032_SERVICE_CAN_NOT_BE_NULL()));
      } else {
         return new ServiceFinder(service, loader);
      }
   }

   public static <T> ServiceFinder<T> find(Class<T> service) {
      return find(service, Thread.currentThread().getContextClassLoader());
   }

   private ServiceFinder(Class<T> service, ClassLoader loader) {
      this.serviceClass = service;
      this.classLoader = loader;
   }

   public Iterator<T> iterator() {
      return new ServiceFinder.LazyIterator(this.serviceClass, this.classLoader);
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
      ServiceConfigurationError sce = new ServiceConfigurationError(LocalizationMessages.WSP_0025_SPI_FAIL_SERVICE_MSG(service.getName(), msg));
      if (null != cause) {
         sce.initCause(cause);
      }

      throw (ServiceConfigurationError)LOGGER.logSevereException(sce);
   }

   private static void fail(Class service, URL u, int line, String msg, Throwable cause) throws ServiceConfigurationError {
      fail(service, LocalizationMessages.WSP_0024_SPI_FAIL_SERVICE_URL_LINE_MSG(u, line, msg), cause);
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
               fail(service, u, lc, LocalizationMessages.WSP_0067_ILLEGAL_CFG_FILE_SYNTAX(), (Throwable)null);
            }

            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
               fail(service, u, lc, LocalizationMessages.WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(ln), (Throwable)null);
            }

            for(int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
               cp = ln.codePointAt(i);
               if (!Character.isJavaIdentifierPart(cp) && cp != 46) {
                  fail(service, u, lc, LocalizationMessages.WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(ln), (Throwable)null);
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
         fail(service, ": " + var15, var15);
      } finally {
         try {
            if (r != null) {
               r.close();
            }

            if (in != null) {
               in.close();
            }
         } catch (IOException var14) {
            fail(service, ": " + var14, var14);
         }

      }

      return names.iterator();
   }

   private static class LazyIterator<T> implements Iterator<T> {
      Class<T> service;
      ClassLoader loader;
      Enumeration<URL> configs;
      Iterator<String> pending;
      Set<String> returned;
      String nextName;

      private LazyIterator(Class<T> service, ClassLoader loader) {
         this.configs = null;
         this.pending = null;
         this.returned = new TreeSet();
         this.nextName = null;
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
                  ServiceFinder.fail(this.service, ": " + var2, var2);
               }
            }

            while(this.pending == null || !this.pending.hasNext()) {
               if (!this.configs.hasMoreElements()) {
                  return false;
               }

               this.pending = ServiceFinder.parse(this.service, (URL)this.configs.nextElement(), this.returned);
            }

            this.nextName = (String)this.pending.next();
            return true;
         }
      }

      public T next() throws ServiceConfigurationError {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            String cn = this.nextName;
            this.nextName = null;

            try {
               return this.service.cast(Class.forName(cn, true, this.loader).newInstance());
            } catch (ClassNotFoundException var3) {
               ServiceFinder.fail(this.service, LocalizationMessages.WSP_0027_SERVICE_PROVIDER_NOT_FOUND(cn), var3);
            } catch (Exception var4) {
               ServiceFinder.fail(this.service, LocalizationMessages.WSP_0028_SERVICE_PROVIDER_COULD_NOT_BE_INSTANTIATED(cn), var4);
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
}
