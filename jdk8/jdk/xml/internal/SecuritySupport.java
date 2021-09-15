package jdk.xml.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;

class SecuritySupport {
   static final Properties cacheProps = new Properties();
   static volatile boolean firstTime = true;

   private SecuritySupport() {
   }

   public static String getSystemProperty(final String propName) {
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(propName);
         }
      });
   }

   public static <T> T getJAXPSystemProperty(Class<T> type, String propName, String defValue) {
      String value = getJAXPSystemProperty(propName);
      if (value == null) {
         value = defValue;
      }

      if (Integer.class.isAssignableFrom(type)) {
         return type.cast(Integer.parseInt(value));
      } else {
         return Boolean.class.isAssignableFrom(type) ? type.cast(Boolean.parseBoolean(value)) : type.cast(value);
      }
   }

   public static String getJAXPSystemProperty(String propName) {
      String value = getSystemProperty(propName);
      if (value == null) {
         value = readJAXPProperty(propName);
      }

      return value;
   }

   public static String readJAXPProperty(String propName) {
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

         value = cacheProps.getProperty(propName);
      } catch (IOException var17) {
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

   static boolean getFileExists(final File f) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return f.exists() ? Boolean.TRUE : Boolean.FALSE;
         }
      });
   }

   static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
      try {
         return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
            public FileInputStream run() throws Exception {
               return new FileInputStream(file);
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (FileNotFoundException)var2.getException();
      }
   }
}
