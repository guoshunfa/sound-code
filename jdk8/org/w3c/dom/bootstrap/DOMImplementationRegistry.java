package org.w3c.dom.bootstrap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementationSource;

public final class DOMImplementationRegistry {
   public static final String PROPERTY = "org.w3c.dom.DOMImplementationSourceList";
   private static final int DEFAULT_LINE_LENGTH = 80;
   private Vector sources;
   private static final String FALLBACK_CLASS = "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl";
   private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal.dom";

   private DOMImplementationRegistry(Vector srcs) {
      this.sources = srcs;
   }

   public static DOMImplementationRegistry newInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException {
      Vector sources = new Vector();
      ClassLoader classLoader = getClassLoader();
      String p = getSystemProperty("org.w3c.dom.DOMImplementationSourceList");
      if (p == null) {
         p = getServiceValue(classLoader);
      }

      if (p == null) {
         p = "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl";
      }

      if (p != null) {
         StringTokenizer st = new StringTokenizer(p);

         while(st.hasMoreTokens()) {
            String sourceName = st.nextToken();
            boolean internal = false;
            if (System.getSecurityManager() != null && sourceName != null && sourceName.startsWith("com.sun.org.apache.xerces.internal.dom")) {
               internal = true;
            }

            Class sourceClass = null;
            if (classLoader != null && !internal) {
               sourceClass = classLoader.loadClass(sourceName);
            } else {
               sourceClass = Class.forName(sourceName);
            }

            DOMImplementationSource source = (DOMImplementationSource)sourceClass.newInstance();
            sources.addElement(source);
         }
      }

      return new DOMImplementationRegistry(sources);
   }

   public DOMImplementation getDOMImplementation(String features) {
      int size = this.sources.size();
      String name = null;

      for(int i = 0; i < size; ++i) {
         DOMImplementationSource source = (DOMImplementationSource)this.sources.elementAt(i);
         DOMImplementation impl = source.getDOMImplementation(features);
         if (impl != null) {
            return impl;
         }
      }

      return null;
   }

   public DOMImplementationList getDOMImplementationList(String features) {
      final Vector implementations = new Vector();
      int size = this.sources.size();

      for(int i = 0; i < size; ++i) {
         DOMImplementationSource source = (DOMImplementationSource)this.sources.elementAt(i);
         DOMImplementationList impls = source.getDOMImplementationList(features);

         for(int j = 0; j < impls.getLength(); ++j) {
            DOMImplementation impl = impls.item(j);
            implementations.addElement(impl);
         }
      }

      return new DOMImplementationList() {
         public DOMImplementation item(int index) {
            if (index >= 0 && index < implementations.size()) {
               try {
                  return (DOMImplementation)implementations.elementAt(index);
               } catch (ArrayIndexOutOfBoundsException var3) {
                  return null;
               }
            } else {
               return null;
            }
         }

         public int getLength() {
            return implementations.size();
         }
      };
   }

   public void addSource(DOMImplementationSource s) {
      if (s == null) {
         throw new NullPointerException();
      } else {
         if (!this.sources.contains(s)) {
            this.sources.addElement(s);
         }

      }
   }

   private static ClassLoader getClassLoader() {
      try {
         ClassLoader contextClassLoader = getContextClassLoader();
         if (contextClassLoader != null) {
            return contextClassLoader;
         }
      } catch (Exception var1) {
         return DOMImplementationRegistry.class.getClassLoader();
      }

      return DOMImplementationRegistry.class.getClassLoader();
   }

   private static String getServiceValue(ClassLoader classLoader) {
      String serviceId = "META-INF/services/org.w3c.dom.DOMImplementationSourceList";

      try {
         InputStream is = getResourceAsStream(classLoader, serviceId);
         if (is != null) {
            BufferedReader rd;
            try {
               rd = new BufferedReader(new InputStreamReader(is, "UTF-8"), 80);
            } catch (UnsupportedEncodingException var5) {
               rd = new BufferedReader(new InputStreamReader(is), 80);
            }

            String serviceValue = rd.readLine();
            rd.close();
            if (serviceValue != null && serviceValue.length() > 0) {
               return serviceValue;
            }
         }

         return null;
      } catch (Exception var6) {
         return null;
      }
   }

   private static boolean isJRE11() {
      try {
         Class c = Class.forName("java.security.AccessController");
         return false;
      } catch (Exception var1) {
         return true;
      }
   }

   private static ClassLoader getContextClassLoader() {
      return isJRE11() ? null : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader classLoader = null;

            try {
               classLoader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException var3) {
            }

            return classLoader;
         }
      });
   }

   private static String getSystemProperty(final String name) {
      return isJRE11() ? System.getProperty(name) : (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(name);
         }
      });
   }

   private static InputStream getResourceAsStream(final ClassLoader classLoader, final String name) {
      if (isJRE11()) {
         InputStream ris;
         if (classLoader == null) {
            ris = ClassLoader.getSystemResourceAsStream(name);
         } else {
            ris = classLoader.getResourceAsStream(name);
         }

         return ris;
      } else {
         return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               InputStream ris;
               if (classLoader == null) {
                  ris = ClassLoader.getSystemResourceAsStream(name);
               } else {
                  ris = classLoader.getResourceAsStream(name);
               }

               return ris;
            }
         });
      }
   }
}
