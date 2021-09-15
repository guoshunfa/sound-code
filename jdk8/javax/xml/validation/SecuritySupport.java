package javax.xml.validation;

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
import java.util.Enumeration;

class SecuritySupport {
   ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader cl = null;
            cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
               cl = ClassLoader.getSystemClassLoader();
            }

            return cl;
         }
      });
   }

   String getSystemProperty(final String propName) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(propName);
         }
      });
   }

   FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
      try {
         return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws FileNotFoundException {
               return new FileInputStream(file);
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (FileNotFoundException)var3.getException();
      }
   }

   InputStream getURLInputStream(final URL url) throws IOException {
      try {
         return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws IOException {
               return url.openStream();
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getException();
      }
   }

   URL getResourceAsURL(final ClassLoader cl, final String name) {
      return (URL)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            URL url;
            if (cl == null) {
               url = Object.class.getResource(name);
            } else {
               url = cl.getResource(name);
            }

            return url;
         }
      });
   }

   Enumeration getResources(final ClassLoader cl, final String name) throws IOException {
      try {
         return (Enumeration)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws IOException {
               Enumeration enumeration;
               if (cl == null) {
                  enumeration = ClassLoader.getSystemResources(name);
               } else {
                  enumeration = cl.getResources(name);
               }

               return enumeration;
            }
         });
      } catch (PrivilegedActionException var4) {
         throw (IOException)var4.getException();
      }
   }

   InputStream getResourceAsStream(final ClassLoader cl, final String name) {
      return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            InputStream ris;
            if (cl == null) {
               ris = Object.class.getResourceAsStream(name);
            } else {
               ris = cl.getResourceAsStream(name);
            }

            return ris;
         }
      });
   }

   boolean doesFileExist(final File f) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return new Boolean(f.exists());
         }
      });
   }
}
