package com.sun.org.apache.xml.internal.resolver;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.helpers.BootstrapResolver;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.reflect.misc.ReflectUtil;

public class CatalogManager {
   private static String pFiles = "xml.catalog.files";
   private static String pVerbosity = "xml.catalog.verbosity";
   private static String pPrefer = "xml.catalog.prefer";
   private static String pStatic = "xml.catalog.staticCatalog";
   private static String pAllowPI = "xml.catalog.allowPI";
   private static String pClassname = "xml.catalog.className";
   private static String pIgnoreMissing = "xml.catalog.ignoreMissing";
   private static CatalogManager staticManager = new CatalogManager();
   private BootstrapResolver bResolver = new BootstrapResolver();
   private boolean ignoreMissingProperties;
   private ResourceBundle resources;
   private String propertyFile;
   private URL propertyFileURI;
   private String defaultCatalogFiles;
   private String catalogFiles;
   private boolean fromPropertiesFile;
   private int defaultVerbosity;
   private Integer verbosity;
   private boolean defaultPreferPublic;
   private Boolean preferPublic;
   private boolean defaultUseStaticCatalog;
   private Boolean useStaticCatalog;
   private static Catalog staticCatalog = null;
   private boolean defaultOasisXMLCatalogPI;
   private Boolean oasisXMLCatalogPI;
   private boolean defaultRelativeCatalogs;
   private Boolean relativeCatalogs;
   private String catalogClassName;
   private boolean overrideDefaultParser;
   public Debug debug;

   public CatalogManager() {
      this.ignoreMissingProperties = SecuritySupport.getSystemProperty(pIgnoreMissing) != null || SecuritySupport.getSystemProperty(pFiles) != null;
      this.propertyFile = "CatalogManager.properties";
      this.propertyFileURI = null;
      this.defaultCatalogFiles = "./xcatalog";
      this.catalogFiles = null;
      this.fromPropertiesFile = false;
      this.defaultVerbosity = 1;
      this.verbosity = null;
      this.defaultPreferPublic = true;
      this.preferPublic = null;
      this.defaultUseStaticCatalog = true;
      this.useStaticCatalog = null;
      this.defaultOasisXMLCatalogPI = true;
      this.oasisXMLCatalogPI = null;
      this.defaultRelativeCatalogs = true;
      this.relativeCatalogs = null;
      this.catalogClassName = null;
      this.debug = null;
      this.init();
   }

   public CatalogManager(String propertyFile) {
      this.ignoreMissingProperties = SecuritySupport.getSystemProperty(pIgnoreMissing) != null || SecuritySupport.getSystemProperty(pFiles) != null;
      this.propertyFile = "CatalogManager.properties";
      this.propertyFileURI = null;
      this.defaultCatalogFiles = "./xcatalog";
      this.catalogFiles = null;
      this.fromPropertiesFile = false;
      this.defaultVerbosity = 1;
      this.verbosity = null;
      this.defaultPreferPublic = true;
      this.preferPublic = null;
      this.defaultUseStaticCatalog = true;
      this.useStaticCatalog = null;
      this.defaultOasisXMLCatalogPI = true;
      this.oasisXMLCatalogPI = null;
      this.defaultRelativeCatalogs = true;
      this.relativeCatalogs = null;
      this.catalogClassName = null;
      this.debug = null;
      this.propertyFile = propertyFile;
      this.init();
   }

   private void init() {
      this.debug = new Debug();
      if (System.getSecurityManager() == null) {
         this.overrideDefaultParser = true;
      }

   }

   public void setBootstrapResolver(BootstrapResolver resolver) {
      this.bResolver = resolver;
   }

   public BootstrapResolver getBootstrapResolver() {
      return this.bResolver;
   }

   private synchronized void readProperties() {
      try {
         this.propertyFileURI = CatalogManager.class.getResource("/" + this.propertyFile);
         InputStream in = CatalogManager.class.getResourceAsStream("/" + this.propertyFile);
         if (in == null) {
            if (!this.ignoreMissingProperties) {
               System.err.println("Cannot find " + this.propertyFile);
               this.ignoreMissingProperties = true;
            }

            return;
         }

         this.resources = new PropertyResourceBundle(in);
      } catch (MissingResourceException var4) {
         if (!this.ignoreMissingProperties) {
            System.err.println("Cannot read " + this.propertyFile);
         }
      } catch (IOException var5) {
         if (!this.ignoreMissingProperties) {
            System.err.println("Failure trying to read " + this.propertyFile);
         }
      }

      if (this.verbosity == null) {
         try {
            String verbStr = this.resources.getString("verbosity");
            int verb = Integer.parseInt(verbStr.trim());
            this.debug.setDebug(verb);
            this.verbosity = new Integer(verb);
         } catch (Exception var3) {
         }
      }

   }

   public static CatalogManager getStaticManager() {
      return staticManager;
   }

   public boolean getIgnoreMissingProperties() {
      return this.ignoreMissingProperties;
   }

   public void setIgnoreMissingProperties(boolean ignore) {
      this.ignoreMissingProperties = ignore;
   }

   /** @deprecated */
   public void ignoreMissingProperties(boolean ignore) {
      this.setIgnoreMissingProperties(ignore);
   }

   private int queryVerbosity() {
      String defaultVerbStr = Integer.toString(this.defaultVerbosity);
      String verbStr = SecuritySupport.getSystemProperty(pVerbosity);
      if (verbStr == null) {
         if (this.resources == null) {
            this.readProperties();
         }

         if (this.resources != null) {
            try {
               verbStr = this.resources.getString("verbosity");
            } catch (MissingResourceException var6) {
               verbStr = defaultVerbStr;
            }
         } else {
            verbStr = defaultVerbStr;
         }
      }

      int verb = this.defaultVerbosity;

      try {
         verb = Integer.parseInt(verbStr.trim());
      } catch (Exception var5) {
         System.err.println("Cannot parse verbosity: \"" + verbStr + "\"");
      }

      if (this.verbosity == null) {
         this.debug.setDebug(verb);
         this.verbosity = new Integer(verb);
      }

      return verb;
   }

   public int getVerbosity() {
      if (this.verbosity == null) {
         this.verbosity = new Integer(this.queryVerbosity());
      }

      return this.verbosity;
   }

   public void setVerbosity(int verbosity) {
      this.verbosity = new Integer(verbosity);
      this.debug.setDebug(verbosity);
   }

   /** @deprecated */
   public int verbosity() {
      return this.getVerbosity();
   }

   private boolean queryRelativeCatalogs() {
      if (this.resources == null) {
         this.readProperties();
      }

      if (this.resources == null) {
         return this.defaultRelativeCatalogs;
      } else {
         try {
            String allow = this.resources.getString("relative-catalogs");
            return allow.equalsIgnoreCase("true") || allow.equalsIgnoreCase("yes") || allow.equalsIgnoreCase("1");
         } catch (MissingResourceException var2) {
            return this.defaultRelativeCatalogs;
         }
      }
   }

   public boolean getRelativeCatalogs() {
      if (this.relativeCatalogs == null) {
         this.relativeCatalogs = new Boolean(this.queryRelativeCatalogs());
      }

      return this.relativeCatalogs;
   }

   public void setRelativeCatalogs(boolean relative) {
      this.relativeCatalogs = new Boolean(relative);
   }

   /** @deprecated */
   public boolean relativeCatalogs() {
      return this.getRelativeCatalogs();
   }

   private String queryCatalogFiles() {
      String catalogList = SecuritySupport.getSystemProperty(pFiles);
      this.fromPropertiesFile = false;
      if (catalogList == null) {
         if (this.resources == null) {
            this.readProperties();
         }

         if (this.resources != null) {
            try {
               catalogList = this.resources.getString("catalogs");
               this.fromPropertiesFile = true;
            } catch (MissingResourceException var3) {
               System.err.println(this.propertyFile + ": catalogs not found.");
               catalogList = null;
            }
         }
      }

      if (catalogList == null) {
         catalogList = this.defaultCatalogFiles;
      }

      return catalogList;
   }

   public Vector getCatalogFiles() {
      if (this.catalogFiles == null) {
         this.catalogFiles = this.queryCatalogFiles();
      }

      StringTokenizer files = new StringTokenizer(this.catalogFiles, ";");

      Vector catalogs;
      String catalogFile;
      for(catalogs = new Vector(); files.hasMoreTokens(); catalogs.add(catalogFile)) {
         catalogFile = files.nextToken();
         URL absURI = null;
         if (this.fromPropertiesFile && !this.relativeCatalogs()) {
            try {
               absURI = new URL(this.propertyFileURI, catalogFile);
               catalogFile = absURI.toString();
            } catch (MalformedURLException var6) {
               absURI = null;
            }
         }
      }

      return catalogs;
   }

   public void setCatalogFiles(String fileList) {
      this.catalogFiles = fileList;
      this.fromPropertiesFile = false;
   }

   /** @deprecated */
   public Vector catalogFiles() {
      return this.getCatalogFiles();
   }

   private boolean queryPreferPublic() {
      String prefer = SecuritySupport.getSystemProperty(pPrefer);
      if (prefer == null) {
         if (this.resources == null) {
            this.readProperties();
         }

         if (this.resources == null) {
            return this.defaultPreferPublic;
         }

         try {
            prefer = this.resources.getString("prefer");
         } catch (MissingResourceException var3) {
            return this.defaultPreferPublic;
         }
      }

      return prefer == null ? this.defaultPreferPublic : prefer.equalsIgnoreCase("public");
   }

   public boolean getPreferPublic() {
      if (this.preferPublic == null) {
         this.preferPublic = new Boolean(this.queryPreferPublic());
      }

      return this.preferPublic;
   }

   public void setPreferPublic(boolean preferPublic) {
      this.preferPublic = new Boolean(preferPublic);
   }

   /** @deprecated */
   public boolean preferPublic() {
      return this.getPreferPublic();
   }

   private boolean queryUseStaticCatalog() {
      String staticCatalog = SecuritySupport.getSystemProperty(pStatic);
      if (staticCatalog == null) {
         if (this.resources == null) {
            this.readProperties();
         }

         if (this.resources == null) {
            return this.defaultUseStaticCatalog;
         }

         try {
            staticCatalog = this.resources.getString("static-catalog");
         } catch (MissingResourceException var3) {
            return this.defaultUseStaticCatalog;
         }
      }

      if (staticCatalog == null) {
         return this.defaultUseStaticCatalog;
      } else {
         return staticCatalog.equalsIgnoreCase("true") || staticCatalog.equalsIgnoreCase("yes") || staticCatalog.equalsIgnoreCase("1");
      }
   }

   public boolean getUseStaticCatalog() {
      if (this.useStaticCatalog == null) {
         this.useStaticCatalog = new Boolean(this.queryUseStaticCatalog());
      }

      return this.useStaticCatalog;
   }

   public void setUseStaticCatalog(boolean useStatic) {
      this.useStaticCatalog = new Boolean(useStatic);
   }

   /** @deprecated */
   public boolean staticCatalog() {
      return this.getUseStaticCatalog();
   }

   public Catalog getPrivateCatalog() {
      Catalog catalog = staticCatalog;
      if (this.useStaticCatalog == null) {
         this.useStaticCatalog = new Boolean(this.getUseStaticCatalog());
      }

      if (catalog == null || !this.useStaticCatalog) {
         try {
            String catalogClassName = this.getCatalogClassName();
            if (catalogClassName == null) {
               catalog = new Catalog();
            } else {
               try {
                  catalog = (Catalog)ReflectUtil.forName(catalogClassName).newInstance();
               } catch (ClassNotFoundException var4) {
                  this.debug.message(1, "Catalog class named '" + catalogClassName + "' could not be found. Using default.");
                  catalog = new Catalog();
               } catch (ClassCastException var5) {
                  this.debug.message(1, "Class named '" + catalogClassName + "' is not a Catalog. Using default.");
                  catalog = new Catalog();
               }
            }

            catalog.setCatalogManager(this);
            catalog.setupReaders();
            catalog.loadSystemCatalogs();
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         if (this.useStaticCatalog) {
            staticCatalog = catalog;
         }
      }

      return catalog;
   }

   public Catalog getCatalog() {
      Catalog catalog = staticCatalog;
      if (this.useStaticCatalog == null) {
         this.useStaticCatalog = new Boolean(this.getUseStaticCatalog());
      }

      if (catalog == null || !this.useStaticCatalog) {
         catalog = this.getPrivateCatalog();
         if (this.useStaticCatalog) {
            staticCatalog = catalog;
         }
      }

      return catalog;
   }

   public boolean queryAllowOasisXMLCatalogPI() {
      String allow = SecuritySupport.getSystemProperty(pAllowPI);
      if (allow == null) {
         if (this.resources == null) {
            this.readProperties();
         }

         if (this.resources == null) {
            return this.defaultOasisXMLCatalogPI;
         }

         try {
            allow = this.resources.getString("allow-oasis-xml-catalog-pi");
         } catch (MissingResourceException var3) {
            return this.defaultOasisXMLCatalogPI;
         }
      }

      if (allow == null) {
         return this.defaultOasisXMLCatalogPI;
      } else {
         return allow.equalsIgnoreCase("true") || allow.equalsIgnoreCase("yes") || allow.equalsIgnoreCase("1");
      }
   }

   public boolean getAllowOasisXMLCatalogPI() {
      if (this.oasisXMLCatalogPI == null) {
         this.oasisXMLCatalogPI = new Boolean(this.queryAllowOasisXMLCatalogPI());
      }

      return this.oasisXMLCatalogPI;
   }

   public boolean overrideDefaultParser() {
      return this.overrideDefaultParser;
   }

   public void setAllowOasisXMLCatalogPI(boolean allowPI) {
      this.oasisXMLCatalogPI = new Boolean(allowPI);
   }

   /** @deprecated */
   public boolean allowOasisXMLCatalogPI() {
      return this.getAllowOasisXMLCatalogPI();
   }

   public String queryCatalogClassName() {
      String className = SecuritySupport.getSystemProperty(pClassname);
      if (className == null) {
         if (this.resources == null) {
            this.readProperties();
         }

         if (this.resources == null) {
            return null;
         } else {
            try {
               return this.resources.getString("catalog-class-name");
            } catch (MissingResourceException var3) {
               return null;
            }
         }
      } else {
         return className;
      }
   }

   public String getCatalogClassName() {
      if (this.catalogClassName == null) {
         this.catalogClassName = this.queryCatalogClassName();
      }

      return this.catalogClassName;
   }

   public void setCatalogClassName(String className) {
      this.catalogClassName = className;
   }

   /** @deprecated */
   public String catalogClassName() {
      return this.getCatalogClassName();
   }
}
