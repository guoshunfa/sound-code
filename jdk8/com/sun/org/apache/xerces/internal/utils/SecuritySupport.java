package com.sun.org.apache.xerces.internal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class SecuritySupport {
   private static final SecuritySupport securitySupport = new SecuritySupport();
   static final Properties cacheProps = new Properties();
   static volatile boolean firstTime = true;

   public static SecuritySupport getInstance() {
      return securitySupport;
   }

   static ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader cl = null;

            try {
               cl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException var3) {
            }

            return cl;
         }
      });
   }

   static ClassLoader getSystemClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader cl = null;

            try {
               cl = ClassLoader.getSystemClassLoader();
            } catch (SecurityException var3) {
            }

            return cl;
         }
      });
   }

   static ClassLoader getParentClassLoader(final ClassLoader cl) {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader parent = null;

            try {
               parent = cl.getParent();
            } catch (SecurityException var3) {
            }

            return parent == cl ? null : parent;
         }
      });
   }

   public static String getSystemProperty(final String propName) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(propName);
         }
      });
   }

   static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
      try {
         return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws FileNotFoundException {
               return new FileInputStream(file);
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (FileNotFoundException)var2.getException();
      }
   }

   public static InputStream getResourceAsStream(String name) {
      return System.getSecurityManager() != null ? getResourceAsStream((ClassLoader)null, name) : getResourceAsStream(ObjectFactory.findClassLoader(), name);
   }

   public static InputStream getResourceAsStream(final ClassLoader cl, final String name) {
      return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            InputStream ris;
            if (cl == null) {
               ris = Object.class.getResourceAsStream("/" + name);
            } else {
               ris = cl.getResourceAsStream(name);
            }

            return ris;
         }
      });
   }

   public static ResourceBundle getResourceBundle(String bundle) {
      return getResourceBundle(bundle, Locale.getDefault());
   }

   public static ResourceBundle getResourceBundle(final String bundle, final Locale locale) {
      return (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
         public ResourceBundle run() {
            try {
               return PropertyResourceBundle.getBundle(bundle, locale);
            } catch (MissingResourceException var4) {
               try {
                  return PropertyResourceBundle.getBundle(bundle, new Locale("en", "US"));
               } catch (MissingResourceException var3) {
                  throw new MissingResourceException("Could not load any resource bundle by " + bundle, bundle, "");
               }
            }
         }
      });
   }

   static boolean getFileExists(final File f) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return f.exists() ? Boolean.TRUE : Boolean.FALSE;
         }
      });
   }

   static long getLastModified(final File f) {
      return (Long)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return new Long(f.lastModified());
         }
      });
   }

   public static String sanitizePath(String uri) {
      if (uri == null) {
         return "";
      } else {
         int i = uri.lastIndexOf("/");
         return i > 0 ? uri.substring(i + 1, uri.length()) : uri;
      }
   }

   public static String checkAccess(String systemId, String allowedProtocols, String accessAny) throws IOException {
      if (systemId == null || allowedProtocols != null && allowedProtocols.equalsIgnoreCase(accessAny)) {
         return null;
      } else {
         String protocol;
         if (systemId.indexOf(":") == -1) {
            protocol = "file";
         } else {
            URL url = new URL(systemId);
            protocol = url.getProtocol();
            if (protocol.equalsIgnoreCase("jar")) {
               String path = url.getPath();
               protocol = path.substring(0, path.indexOf(":"));
            }
         }

         return isProtocolAllowed(protocol, allowedProtocols) ? null : protocol;
      }
   }

   private static boolean isProtocolAllowed(String protocol, String allowedProtocols) {
      if (allowedProtocols == null) {
         return false;
      } else {
         String[] temp = allowedProtocols.split(",");
         String[] var3 = temp;
         int var4 = temp.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String t = var3[var5];
            t = t.trim();
            if (t.equalsIgnoreCase(protocol)) {
               return true;
            }
         }

         return false;
      }
   }

   public static String getJAXPSystemProperty(String sysPropertyId) {
      String accessExternal = getSystemProperty(sysPropertyId);
      if (accessExternal == null) {
         accessExternal = readJAXPProperty(sysPropertyId);
      }

      return accessExternal;
   }

   static String readJAXPProperty(String propertyId) {
      String value = null;
      FileInputStream is = null;

      try {
         if (firstTime) {
            synchronized(cacheProps) {
               if (firstTime) {
                  String configFile = getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
                  File f = new File(configFile);
                  if (getFileExists(f)) {
                     is = getFileInputStream(f);
                     cacheProps.load((InputStream)is);
                  }

                  firstTime = false;
               }
            }
         }

         value = cacheProps.getProperty(propertyId);
      } catch (Exception var17) {
      } finally {
         if (is != null) {
            try {
               is.close();
            } catch (IOException var15) {
            }
         }

      }

      return value;
   }

   private SecuritySupport() {
   }
}
