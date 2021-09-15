package com.sun.naming.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.naming.NamingEnumeration;

final class VersionHelper12 extends VersionHelper {
   public Class<?> loadClass(String var1) throws ClassNotFoundException {
      return this.loadClass(var1, this.getContextClassLoader());
   }

   Class<?> loadClass(String var1, ClassLoader var2) throws ClassNotFoundException {
      Class var3 = Class.forName(var1, true, var2);
      return var3;
   }

   public Class<?> loadClass(String var1, String var2) throws ClassNotFoundException, MalformedURLException {
      ClassLoader var3 = this.getContextClassLoader();
      URLClassLoader var4 = URLClassLoader.newInstance(getUrlArray(var2), var3);
      return this.loadClass(var1, (ClassLoader)var4);
   }

   String getJndiProperty(final int var1) {
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            try {
               return System.getProperty(VersionHelper.PROPS[var1]);
            } catch (SecurityException var2) {
               return null;
            }
         }
      });
   }

   String[] getJndiProperties() {
      Properties var1 = (Properties)AccessController.doPrivileged(new PrivilegedAction<Properties>() {
         public Properties run() {
            try {
               return System.getProperties();
            } catch (SecurityException var2) {
               return null;
            }
         }
      });
      if (var1 == null) {
         return null;
      } else {
         String[] var2 = new String[PROPS.length];

         for(int var3 = 0; var3 < PROPS.length; ++var3) {
            var2[var3] = var1.getProperty(PROPS[var3]);
         }

         return var2;
      }
   }

   InputStream getResourceAsStream(final Class<?> var1, final String var2) {
      return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
         public InputStream run() {
            return var1.getResourceAsStream(var2);
         }
      });
   }

   InputStream getJavaHomeLibStream(final String var1) {
      return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
         public InputStream run() {
            try {
               String var1x = System.getProperty("java.home");
               if (var1x == null) {
                  return null;
               } else {
                  String var2 = var1x + File.separator + "lib" + File.separator + var1;
                  return new FileInputStream(var2);
               }
            } catch (Exception var3) {
               return null;
            }
         }
      });
   }

   NamingEnumeration<InputStream> getResources(final ClassLoader var1, final String var2) throws IOException {
      Enumeration var3;
      try {
         var3 = (Enumeration)AccessController.doPrivileged(new PrivilegedExceptionAction<Enumeration<URL>>() {
            public Enumeration<URL> run() throws IOException {
               return var1 == null ? ClassLoader.getSystemResources(var2) : var1.getResources(var2);
            }
         });
      } catch (PrivilegedActionException var5) {
         throw (IOException)var5.getException();
      }

      return new VersionHelper12.InputStreamEnumeration(var3);
   }

   ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            ClassLoader var1 = Thread.currentThread().getContextClassLoader();
            if (var1 == null) {
               var1 = ClassLoader.getSystemClassLoader();
            }

            return var1;
         }
      });
   }

   class InputStreamEnumeration implements NamingEnumeration<InputStream> {
      private final Enumeration<URL> urls;
      private InputStream nextElement = null;

      InputStreamEnumeration(Enumeration<URL> var2) {
         this.urls = var2;
      }

      private InputStream getNextElement() {
         return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() {
               while(InputStreamEnumeration.this.urls.hasMoreElements()) {
                  try {
                     return ((URL)InputStreamEnumeration.this.urls.nextElement()).openStream();
                  } catch (IOException var2) {
                  }
               }

               return null;
            }
         });
      }

      public boolean hasMore() {
         if (this.nextElement != null) {
            return true;
         } else {
            this.nextElement = this.getNextElement();
            return this.nextElement != null;
         }
      }

      public boolean hasMoreElements() {
         return this.hasMore();
      }

      public InputStream next() {
         if (this.hasMore()) {
            InputStream var1 = this.nextElement;
            this.nextElement = null;
            return var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      public InputStream nextElement() {
         return this.next();
      }

      public void close() {
      }
   }
}
