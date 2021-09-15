package sun.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public final class Service<S> {
   private static final String prefix = "META-INF/services/";

   private Service() {
   }

   private static void fail(Class<?> var0, String var1, Throwable var2) throws ServiceConfigurationError {
      ServiceConfigurationError var3 = new ServiceConfigurationError(var0.getName() + ": " + var1);
      var3.initCause(var2);
      throw var3;
   }

   private static void fail(Class<?> var0, String var1) throws ServiceConfigurationError {
      throw new ServiceConfigurationError(var0.getName() + ": " + var1);
   }

   private static void fail(Class<?> var0, URL var1, int var2, String var3) throws ServiceConfigurationError {
      fail(var0, var1 + ":" + var2 + ": " + var3);
   }

   private static int parseLine(Class<?> var0, URL var1, BufferedReader var2, int var3, List<String> var4, Set<String> var5) throws IOException, ServiceConfigurationError {
      String var6 = var2.readLine();
      if (var6 == null) {
         return -1;
      } else {
         int var7 = var6.indexOf(35);
         if (var7 >= 0) {
            var6 = var6.substring(0, var7);
         }

         var6 = var6.trim();
         int var8 = var6.length();
         if (var8 != 0) {
            if (var6.indexOf(32) >= 0 || var6.indexOf(9) >= 0) {
               fail(var0, var1, var3, "Illegal configuration-file syntax");
            }

            int var9 = var6.codePointAt(0);
            if (!Character.isJavaIdentifierStart(var9)) {
               fail(var0, var1, var3, "Illegal provider-class name: " + var6);
            }

            for(int var10 = Character.charCount(var9); var10 < var8; var10 += Character.charCount(var9)) {
               var9 = var6.codePointAt(var10);
               if (!Character.isJavaIdentifierPart(var9) && var9 != 46) {
                  fail(var0, var1, var3, "Illegal provider-class name: " + var6);
               }
            }

            if (!var5.contains(var6)) {
               var4.add(var6);
               var5.add(var6);
            }
         }

         return var3 + 1;
      }
   }

   private static Iterator<String> parse(Class<?> var0, URL var1, Set<String> var2) throws ServiceConfigurationError {
      InputStream var3 = null;
      BufferedReader var4 = null;
      ArrayList var5 = new ArrayList();

      try {
         var3 = var1.openStream();
         var4 = new BufferedReader(new InputStreamReader(var3, "utf-8"));
         int var6 = 1;

         while(true) {
            if ((var6 = parseLine(var0, var1, var4, var6, var5, var2)) >= 0) {
               continue;
            }
         }
      } catch (IOException var15) {
         fail(var0, ": " + var15);
      } finally {
         try {
            if (var4 != null) {
               var4.close();
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var14) {
            fail(var0, ": " + var14);
         }

      }

      return var5.iterator();
   }

   public static <S> Iterator<S> providers(Class<S> var0, ClassLoader var1) throws ServiceConfigurationError {
      return new Service.LazyIterator(var0, var1);
   }

   public static <S> Iterator<S> providers(Class<S> var0) throws ServiceConfigurationError {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();
      return providers(var0, var1);
   }

   public static <S> Iterator<S> installedProviders(Class<S> var0) throws ServiceConfigurationError {
      ClassLoader var1 = ClassLoader.getSystemClassLoader();

      ClassLoader var2;
      for(var2 = null; var1 != null; var1 = var1.getParent()) {
         var2 = var1;
      }

      return providers(var0, var2);
   }

   private static class LazyIterator<S> implements Iterator<S> {
      Class<S> service;
      ClassLoader loader;
      Enumeration<URL> configs;
      Iterator<String> pending;
      Set<String> returned;
      String nextName;

      private LazyIterator(Class<S> var1, ClassLoader var2) {
         this.configs = null;
         this.pending = null;
         this.returned = new TreeSet();
         this.nextName = null;
         this.service = var1;
         this.loader = var2;
      }

      public boolean hasNext() throws ServiceConfigurationError {
         if (this.nextName != null) {
            return true;
         } else {
            if (this.configs == null) {
               try {
                  String var1 = "META-INF/services/" + this.service.getName();
                  if (this.loader == null) {
                     this.configs = ClassLoader.getSystemResources(var1);
                  } else {
                     this.configs = this.loader.getResources(var1);
                  }
               } catch (IOException var2) {
                  Service.fail(this.service, ": " + var2);
               }
            }

            while(this.pending == null || !this.pending.hasNext()) {
               if (!this.configs.hasMoreElements()) {
                  return false;
               }

               this.pending = Service.parse(this.service, (URL)this.configs.nextElement(), this.returned);
            }

            this.nextName = (String)this.pending.next();
            return true;
         }
      }

      public S next() throws ServiceConfigurationError {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            String var1 = this.nextName;
            this.nextName = null;
            Class var2 = null;

            try {
               var2 = Class.forName(var1, false, this.loader);
            } catch (ClassNotFoundException var5) {
               Service.fail(this.service, "Provider " + var1 + " not found");
            }

            if (!this.service.isAssignableFrom(var2)) {
               Service.fail(this.service, "Provider " + var1 + " not a subtype");
            }

            try {
               return this.service.cast(var2.newInstance());
            } catch (Throwable var4) {
               Service.fail(this.service, "Provider " + var1 + " could not be instantiated", var4);
               return null;
            }
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      LazyIterator(Class var1, ClassLoader var2, Object var3) {
         this(var1, var2);
      }
   }
}
