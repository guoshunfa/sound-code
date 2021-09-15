package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.PropertyParser;
import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class DataCollectorBase implements DataCollector {
   private PropertyParser parser;
   private Set propertyNames;
   private Set propertyPrefixes;
   private Set URLPropertyNames = new HashSet();
   protected String localHostName;
   protected String configurationHostName;
   private boolean setParserCalled;
   private Properties originalProps;
   private Properties resultProps;

   public DataCollectorBase(Properties var1, String var2, String var3) {
      this.URLPropertyNames.add("org.omg.CORBA.ORBInitialServices");
      this.propertyNames = new HashSet();
      this.propertyNames.add("org.omg.CORBA.ORBInitRef");
      this.propertyPrefixes = new HashSet();
      this.originalProps = var1;
      this.localHostName = var2;
      this.configurationHostName = var3;
      this.setParserCalled = false;
      this.resultProps = new Properties();
   }

   public boolean initialHostIsLocal() {
      this.checkSetParserCalled();
      return this.localHostName.equals(this.resultProps.getProperty("org.omg.CORBA.ORBInitialHost"));
   }

   public void setParser(PropertyParser var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ParserAction var3 = (ParserAction)((ParserAction)var2.next());
         if (var3.isPrefix()) {
            this.propertyPrefixes.add(var3.getPropertyName());
         } else {
            this.propertyNames.add(var3.getPropertyName());
         }
      }

      this.collect();
      this.setParserCalled = true;
   }

   public Properties getProperties() {
      this.checkSetParserCalled();
      return this.resultProps;
   }

   public abstract boolean isApplet();

   protected abstract void collect();

   protected void checkPropertyDefaults() {
      String var1 = this.resultProps.getProperty("org.omg.CORBA.ORBInitialHost");
      if (var1 == null || var1.equals("")) {
         this.setProperty("org.omg.CORBA.ORBInitialHost", this.configurationHostName);
      }

      String var2 = this.resultProps.getProperty("com.sun.CORBA.ORBServerHost");
      if (var2 == null || var2.equals("") || var2.equals("0.0.0.0") || var2.equals("::") || var2.toLowerCase().equals("::ffff:0.0.0.0")) {
         this.setProperty("com.sun.CORBA.ORBServerHost", this.localHostName);
         this.setProperty("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces");
      }

   }

   protected void findPropertiesFromArgs(String[] var1) {
      if (var1 != null) {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            String var3 = null;
            String var2 = null;
            if (var1[var4] != null && var1[var4].startsWith("-ORB")) {
               String var5 = var1[var4].substring(1);
               var2 = this.findMatchingPropertyName(this.propertyNames, var5);
               if (var2 != null && var4 + 1 < var1.length && var1[var4 + 1] != null) {
                  ++var4;
                  var3 = var1[var4];
               }
            }

            if (var3 != null) {
               this.setProperty(var2, var3);
            }
         }

      }
   }

   protected void findPropertiesFromApplet(final Applet var1) {
      if (var1 != null) {
         PropertyCallback var2 = new PropertyCallback() {
            public String get(String var1x) {
               return var1.getParameter(var1x);
            }
         };
         this.findPropertiesByName(this.propertyNames.iterator(), var2);
         PropertyCallback var3 = new PropertyCallback() {
            public String get(String var1x) {
               String var2 = DataCollectorBase.this.resultProps.getProperty(var1x);
               if (var2 == null) {
                  return null;
               } else {
                  try {
                     URL var3 = new URL(var1.getDocumentBase(), var2);
                     return var3.toExternalForm();
                  } catch (MalformedURLException var4) {
                     return var2;
                  }
               }
            }
         };
         this.findPropertiesByName(this.URLPropertyNames.iterator(), var3);
      }
   }

   private void doProperties(final Properties var1) {
      PropertyCallback var2 = new PropertyCallback() {
         public String get(String var1x) {
            return var1.getProperty(var1x);
         }
      };
      this.findPropertiesByName(this.propertyNames.iterator(), var2);
      this.findPropertiesByPrefix(this.propertyPrefixes, makeIterator(var1.propertyNames()), var2);
   }

   protected void findPropertiesFromFile() {
      Properties var1 = this.getFileProperties();
      if (var1 != null) {
         this.doProperties(var1);
      }
   }

   protected void findPropertiesFromProperties() {
      if (this.originalProps != null) {
         this.doProperties(this.originalProps);
      }
   }

   protected void findPropertiesFromSystem() {
      Set var1 = this.getCORBAPrefixes(this.propertyNames);
      Set var2 = this.getCORBAPrefixes(this.propertyPrefixes);
      PropertyCallback var3 = new PropertyCallback() {
         public String get(String var1) {
            return DataCollectorBase.getSystemProperty(var1);
         }
      };
      this.findPropertiesByName(var1.iterator(), var3);
      this.findPropertiesByPrefix(var2, getSystemPropertyNames(), var3);
   }

   private void setProperty(String var1, String var2) {
      if (var1.equals("org.omg.CORBA.ORBInitRef")) {
         StringTokenizer var3 = new StringTokenizer(var2, "=");
         if (var3.countTokens() != 2) {
            throw new IllegalArgumentException();
         }

         String var4 = var3.nextToken();
         String var5 = var3.nextToken();
         this.resultProps.setProperty(var1 + "." + var4, var5);
      } else {
         this.resultProps.setProperty(var1, var2);
      }

   }

   private void checkSetParserCalled() {
      if (!this.setParserCalled) {
         throw new IllegalStateException("setParser not called.");
      }
   }

   private void findPropertiesByPrefix(Set var1, Iterator var2, PropertyCallback var3) {
      label19:
      while(true) {
         if (var2.hasNext()) {
            String var4 = (String)((String)var2.next());
            Iterator var5 = var1.iterator();

            while(true) {
               if (!var5.hasNext()) {
                  continue label19;
               }

               String var6 = (String)((String)var5.next());
               if (var4.startsWith(var6)) {
                  String var7 = var3.get(var4);
                  this.setProperty(var4, var7);
               }
            }
         }

         return;
      }
   }

   private void findPropertiesByName(Iterator var1, PropertyCallback var2) {
      while(var1.hasNext()) {
         String var3 = (String)((String)var1.next());
         String var4 = var2.get(var3);
         if (var4 != null) {
            this.setProperty(var3, var4);
         }
      }

   }

   private static String getSystemProperty(String var0) {
      return (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0)));
   }

   private String findMatchingPropertyName(Set var1, String var2) {
      Iterator var3 = var1.iterator();

      String var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (String)((String)var3.next());
      } while(!var4.endsWith(var2));

      return var4;
   }

   private static Iterator makeIterator(final Enumeration var0) {
      return new Iterator() {
         public boolean hasNext() {
            return var0.hasMoreElements();
         }

         public Object next() {
            return var0.nextElement();
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   private static Iterator getSystemPropertyNames() {
      Enumeration var0 = (Enumeration)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperties().propertyNames();
         }
      });
      return makeIterator(var0);
   }

   private void getPropertiesFromFile(Properties var1, String var2) {
      try {
         File var3 = new File(var2);
         if (!var3.exists()) {
            return;
         }

         FileInputStream var4 = new FileInputStream(var3);

         try {
            var1.load((InputStream)var4);
         } finally {
            var4.close();
         }
      } catch (Exception var9) {
      }

   }

   private Properties getFileProperties() {
      Properties var1 = new Properties();
      String var2 = getSystemProperty("java.home");
      String var3 = var2 + File.separator + "lib" + File.separator + "orb.properties";
      this.getPropertiesFromFile(var1, var3);
      Properties var4 = new Properties(var1);
      String var5 = getSystemProperty("user.home");
      var3 = var5 + File.separator + "orb.properties";
      this.getPropertiesFromFile(var4, var3);
      return var4;
   }

   private boolean hasCORBAPrefix(String var1) {
      return var1.startsWith("org.omg.") || var1.startsWith("com.sun.CORBA.") || var1.startsWith("com.sun.corba.") || var1.startsWith("com.sun.corba.se.");
   }

   private Set getCORBAPrefixes(Set var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)((String)var3.next());
         if (this.hasCORBAPrefix(var4)) {
            var2.add(var4);
         }
      }

      return var2;
   }
}
