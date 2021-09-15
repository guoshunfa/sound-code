package com.sun.org.apache.bcel.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class SecuritySupport {
   private static final SecuritySupport securitySupport = new SecuritySupport();

   public static SecuritySupport getInstance() {
      return securitySupport;
   }

   static java.lang.ClassLoader getContextClassLoader() {
      return (java.lang.ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            java.lang.ClassLoader cl = null;

            try {
               cl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException var3) {
            }

            return cl;
         }
      });
   }

   static java.lang.ClassLoader getSystemClassLoader() {
      return (java.lang.ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            java.lang.ClassLoader cl = null;

            try {
               cl = java.lang.ClassLoader.getSystemClassLoader();
            } catch (SecurityException var3) {
            }

            return cl;
         }
      });
   }

   static java.lang.ClassLoader getParentClassLoader(final java.lang.ClassLoader cl) {
      return (java.lang.ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            java.lang.ClassLoader parent = null;

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
      return System.getSecurityManager() != null ? getResourceAsStream((java.lang.ClassLoader)null, name) : getResourceAsStream(findClassLoader(), name);
   }

   public static InputStream getResourceAsStream(final java.lang.ClassLoader cl, final String name) {
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

   public static ListResourceBundle getResourceBundle(String bundle) {
      return getResourceBundle(bundle, Locale.getDefault());
   }

   public static ListResourceBundle getResourceBundle(final String bundle, final Locale locale) {
      return (ListResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ListResourceBundle>() {
         public ListResourceBundle run() {
            try {
               return (ListResourceBundle)ResourceBundle.getBundle(bundle, locale);
            } catch (MissingResourceException var4) {
               try {
                  return (ListResourceBundle)ResourceBundle.getBundle(bundle, new Locale("en", "US"));
               } catch (MissingResourceException var3) {
                  throw new MissingResourceException("Could not load any resource bundle by " + bundle, bundle, "");
               }
            }
         }
      });
   }

   public static String[] getFileList(final File f, final FilenameFilter filter) {
      return (String[])((String[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return f.list(filter);
         }
      }));
   }

   public static boolean getFileExists(final File f) {
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

   public static java.lang.ClassLoader findClassLoader() {
      return System.getSecurityManager() != null ? null : SecuritySupport.class.getClassLoader();
   }

   private SecuritySupport() {
   }
}
