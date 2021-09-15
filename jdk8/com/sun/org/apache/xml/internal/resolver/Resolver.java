package com.sun.org.apache.xml.internal.resolver;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import jdk.xml.internal.JdkXmlUtils;

public class Resolver extends Catalog {
   public static final int URISUFFIX = CatalogEntry.addEntryType("URISUFFIX", 2);
   public static final int SYSTEMSUFFIX = CatalogEntry.addEntryType("SYSTEMSUFFIX", 2);
   public static final int RESOLVER = CatalogEntry.addEntryType("RESOLVER", 1);
   public static final int SYSTEMREVERSE = CatalogEntry.addEntryType("SYSTEMREVERSE", 1);

   public void setupReaders() {
      SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
      spf.setValidating(false);
      SAXCatalogReader saxReader = new SAXCatalogReader(spf);
      saxReader.setCatalogParser((String)null, "XMLCatalog", "com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader");
      saxReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader");
      this.addReader("application/xml", saxReader);
      TR9401CatalogReader textReader = new TR9401CatalogReader();
      this.addReader("text/plain", textReader);
   }

   public void addEntry(CatalogEntry entry) {
      int type = entry.getEntryType();
      String suffix;
      String fsi;
      if (type == URISUFFIX) {
         suffix = this.normalizeURI(entry.getEntryArg(0));
         fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
         entry.setEntryArg(1, fsi);
         this.catalogManager.debug.message(4, "URISUFFIX", suffix, fsi);
      } else if (type == SYSTEMSUFFIX) {
         suffix = this.normalizeURI(entry.getEntryArg(0));
         fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
         entry.setEntryArg(1, fsi);
         this.catalogManager.debug.message(4, "SYSTEMSUFFIX", suffix, fsi);
      }

      super.addEntry(entry);
   }

   public String resolveURI(String uri) throws MalformedURLException, IOException {
      String resolved = super.resolveURI(uri);
      if (resolved != null) {
         return resolved;
      } else {
         Enumeration en = this.catalogEntries.elements();

         while(en.hasMoreElements()) {
            CatalogEntry e = (CatalogEntry)en.nextElement();
            if (e.getEntryType() == RESOLVER) {
               resolved = this.resolveExternalSystem(uri, e.getEntryArg(0));
               if (resolved != null) {
                  return resolved;
               }
            } else if (e.getEntryType() == URISUFFIX) {
               String suffix = e.getEntryArg(0);
               String result = e.getEntryArg(1);
               if (suffix.length() <= uri.length() && uri.substring(uri.length() - suffix.length()).equals(suffix)) {
                  return result;
               }
            }
         }

         return this.resolveSubordinateCatalogs(Catalog.URI, (String)null, (String)null, uri);
      }
   }

   public String resolveSystem(String systemId) throws MalformedURLException, IOException {
      String resolved = super.resolveSystem(systemId);
      if (resolved != null) {
         return resolved;
      } else {
         Enumeration en = this.catalogEntries.elements();

         while(en.hasMoreElements()) {
            CatalogEntry e = (CatalogEntry)en.nextElement();
            if (e.getEntryType() == RESOLVER) {
               resolved = this.resolveExternalSystem(systemId, e.getEntryArg(0));
               if (resolved != null) {
                  return resolved;
               }
            } else if (e.getEntryType() == SYSTEMSUFFIX) {
               String suffix = e.getEntryArg(0);
               String result = e.getEntryArg(1);
               if (suffix.length() <= systemId.length() && systemId.substring(systemId.length() - suffix.length()).equals(suffix)) {
                  return result;
               }
            }
         }

         return this.resolveSubordinateCatalogs(Catalog.SYSTEM, (String)null, (String)null, systemId);
      }
   }

   public String resolvePublic(String publicId, String systemId) throws MalformedURLException, IOException {
      String resolved = super.resolvePublic(publicId, systemId);
      if (resolved != null) {
         return resolved;
      } else {
         Enumeration en = this.catalogEntries.elements();

         while(en.hasMoreElements()) {
            CatalogEntry e = (CatalogEntry)en.nextElement();
            if (e.getEntryType() == RESOLVER) {
               if (systemId != null) {
                  resolved = this.resolveExternalSystem(systemId, e.getEntryArg(0));
                  if (resolved != null) {
                     return resolved;
                  }
               }

               resolved = this.resolveExternalPublic(publicId, e.getEntryArg(0));
               if (resolved != null) {
                  return resolved;
               }
            }
         }

         return this.resolveSubordinateCatalogs(Catalog.PUBLIC, (String)null, publicId, systemId);
      }
   }

   protected String resolveExternalSystem(String systemId, String resolver) throws MalformedURLException, IOException {
      Resolver r = this.queryResolver(resolver, "i2l", systemId, (String)null);
      return r != null ? r.resolveSystem(systemId) : null;
   }

   protected String resolveExternalPublic(String publicId, String resolver) throws MalformedURLException, IOException {
      Resolver r = this.queryResolver(resolver, "fpi2l", publicId, (String)null);
      return r != null ? r.resolvePublic(publicId, (String)null) : null;
   }

   protected Resolver queryResolver(String resolver, String command, String arg1, String arg2) {
      InputStream iStream = null;
      String RFC2483 = resolver + "?command=" + command + "&format=tr9401&uri=" + arg1 + "&uri2=" + arg2;
      Object var7 = null;

      try {
         URL url = new URL(RFC2483);
         URLConnection urlCon = url.openConnection();
         urlCon.setUseCaches(false);
         Resolver r = (Resolver)this.newCatalog();
         String cType = urlCon.getContentType();
         if (cType.indexOf(";") > 0) {
            cType = cType.substring(0, cType.indexOf(";"));
         }

         r.parseCatalog(cType, urlCon.getInputStream());
         return r;
      } catch (CatalogException var12) {
         if (var12.getExceptionType() == 6) {
            this.catalogManager.debug.message(1, "Unparseable catalog: " + RFC2483);
         } else if (var12.getExceptionType() == 5) {
            this.catalogManager.debug.message(1, "Unknown catalog format: " + RFC2483);
         }

         return null;
      } catch (MalformedURLException var13) {
         this.catalogManager.debug.message(1, "Malformed resolver URL: " + RFC2483);
         return null;
      } catch (IOException var14) {
         this.catalogManager.debug.message(1, "I/O Exception opening resolver: " + RFC2483);
         return null;
      }
   }

   private Vector appendVector(Vector vec, Vector appvec) {
      if (appvec != null) {
         for(int count = 0; count < appvec.size(); ++count) {
            vec.addElement(appvec.elementAt(count));
         }
      }

      return vec;
   }

   public Vector resolveAllSystemReverse(String systemId) throws MalformedURLException, IOException {
      Vector resolved = new Vector();
      Vector subResolved;
      if (systemId != null) {
         subResolved = this.resolveLocalSystemReverse(systemId);
         resolved = this.appendVector(resolved, subResolved);
      }

      subResolved = this.resolveAllSubordinateCatalogs(SYSTEMREVERSE, (String)null, (String)null, systemId);
      return this.appendVector(resolved, subResolved);
   }

   public String resolveSystemReverse(String systemId) throws MalformedURLException, IOException {
      Vector resolved = this.resolveAllSystemReverse(systemId);
      return resolved != null && resolved.size() > 0 ? (String)resolved.elementAt(0) : null;
   }

   public Vector resolveAllSystem(String systemId) throws MalformedURLException, IOException {
      Vector resolutions = new Vector();
      Vector subResolutions;
      if (systemId != null) {
         subResolutions = this.resolveAllLocalSystem(systemId);
         resolutions = this.appendVector(resolutions, subResolutions);
      }

      subResolutions = this.resolveAllSubordinateCatalogs(SYSTEM, (String)null, (String)null, systemId);
      resolutions = this.appendVector(resolutions, subResolutions);
      return resolutions.size() > 0 ? resolutions : null;
   }

   private Vector resolveAllLocalSystem(String systemId) {
      Vector map = new Vector();
      String osname = SecuritySupport.getSystemProperty("os.name");
      boolean windows = osname.indexOf("Windows") >= 0;
      Enumeration en = this.catalogEntries.elements();

      while(true) {
         CatalogEntry e;
         do {
            do {
               if (!en.hasMoreElements()) {
                  if (map.size() == 0) {
                     return null;
                  }

                  return map;
               }

               e = (CatalogEntry)en.nextElement();
            } while(e.getEntryType() != SYSTEM);
         } while(!e.getEntryArg(0).equals(systemId) && (!windows || !e.getEntryArg(0).equalsIgnoreCase(systemId)));

         map.addElement(e.getEntryArg(1));
      }
   }

   private Vector resolveLocalSystemReverse(String systemId) {
      Vector map = new Vector();
      String osname = SecuritySupport.getSystemProperty("os.name");
      boolean windows = osname.indexOf("Windows") >= 0;
      Enumeration en = this.catalogEntries.elements();

      while(true) {
         CatalogEntry e;
         do {
            do {
               if (!en.hasMoreElements()) {
                  if (map.size() == 0) {
                     return null;
                  }

                  return map;
               }

               e = (CatalogEntry)en.nextElement();
            } while(e.getEntryType() != SYSTEM);
         } while(!e.getEntryArg(1).equals(systemId) && (!windows || !e.getEntryArg(1).equalsIgnoreCase(systemId)));

         map.addElement(e.getEntryArg(0));
      }
   }

   private synchronized Vector resolveAllSubordinateCatalogs(int entityType, String entityName, String publicId, String systemId) throws MalformedURLException, IOException {
      Vector resolutions = new Vector();

      for(int catPos = 0; catPos < this.catalogs.size(); ++catPos) {
         Resolver c = null;

         try {
            c = (Resolver)this.catalogs.elementAt(catPos);
         } catch (ClassCastException var14) {
            String catfile = (String)this.catalogs.elementAt(catPos);
            c = (Resolver)this.newCatalog();

            try {
               c.parseCatalog(catfile);
            } catch (MalformedURLException var11) {
               this.catalogManager.debug.message(1, "Malformed Catalog URL", catfile);
            } catch (FileNotFoundException var12) {
               this.catalogManager.debug.message(1, "Failed to load catalog, file not found", catfile);
            } catch (IOException var13) {
               this.catalogManager.debug.message(1, "Failed to load catalog, I/O error", catfile);
            }

            this.catalogs.setElementAt(c, catPos);
         }

         String resolved = null;
         if (entityType == DOCTYPE) {
            resolved = c.resolveDoctype(entityName, publicId, systemId);
            if (resolved != null) {
               resolutions.addElement(resolved);
               return resolutions;
            }
         } else if (entityType == DOCUMENT) {
            resolved = c.resolveDocument();
            if (resolved != null) {
               resolutions.addElement(resolved);
               return resolutions;
            }
         } else if (entityType == ENTITY) {
            resolved = c.resolveEntity(entityName, publicId, systemId);
            if (resolved != null) {
               resolutions.addElement(resolved);
               return resolutions;
            }
         } else if (entityType == NOTATION) {
            resolved = c.resolveNotation(entityName, publicId, systemId);
            if (resolved != null) {
               resolutions.addElement(resolved);
               return resolutions;
            }
         } else if (entityType == PUBLIC) {
            resolved = c.resolvePublic(publicId, systemId);
            if (resolved != null) {
               resolutions.addElement(resolved);
               return resolutions;
            }
         } else {
            Vector localResolutions;
            if (entityType == SYSTEM) {
               localResolutions = c.resolveAllSystem(systemId);
               resolutions = this.appendVector(resolutions, localResolutions);
               break;
            }

            if (entityType == SYSTEMREVERSE) {
               localResolutions = c.resolveAllSystemReverse(systemId);
               resolutions = this.appendVector(resolutions, localResolutions);
            }
         }
      }

      return resolutions != null ? resolutions : null;
   }
}
