package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class CatalogResolver implements EntityResolver, URIResolver {
   public boolean namespaceAware = true;
   public boolean validating = false;
   private Catalog catalog = null;
   private CatalogManager catalogManager = CatalogManager.getStaticManager();

   public CatalogResolver() {
      this.initializeCatalogs(false);
   }

   public CatalogResolver(boolean privateCatalog) {
      this.initializeCatalogs(privateCatalog);
   }

   public CatalogResolver(CatalogManager manager) {
      this.catalogManager = manager;
      this.initializeCatalogs(!this.catalogManager.getUseStaticCatalog());
   }

   private void initializeCatalogs(boolean privateCatalog) {
      this.catalog = this.catalogManager.getCatalog();
   }

   public Catalog getCatalog() {
      return this.catalog;
   }

   public String getResolvedEntity(String publicId, String systemId) {
      String resolved = null;
      if (this.catalog == null) {
         this.catalogManager.debug.message(1, "Catalog resolution attempted with null catalog; ignored");
         return null;
      } else {
         if (systemId != null) {
            try {
               resolved = this.catalog.resolveSystem(systemId);
            } catch (MalformedURLException var7) {
               this.catalogManager.debug.message(1, "Malformed URL exception trying to resolve", publicId);
               resolved = null;
            } catch (IOException var8) {
               this.catalogManager.debug.message(1, "I/O exception trying to resolve", publicId);
               resolved = null;
            }
         }

         if (resolved == null) {
            if (publicId != null) {
               try {
                  resolved = this.catalog.resolvePublic(publicId, systemId);
               } catch (MalformedURLException var5) {
                  this.catalogManager.debug.message(1, "Malformed URL exception trying to resolve", publicId);
               } catch (IOException var6) {
                  this.catalogManager.debug.message(1, "I/O exception trying to resolve", publicId);
               }
            }

            if (resolved != null) {
               this.catalogManager.debug.message(2, "Resolved public", publicId, resolved);
            }
         } else {
            this.catalogManager.debug.message(2, "Resolved system", systemId, resolved);
         }

         return resolved;
      }
   }

   public InputSource resolveEntity(String publicId, String systemId) {
      String resolved = this.getResolvedEntity(publicId, systemId);
      if (resolved != null) {
         try {
            InputSource iSource = new InputSource(resolved);
            iSource.setPublicId(publicId);
            URL url = new URL(resolved);
            InputStream iStream = url.openStream();
            iSource.setByteStream(iStream);
            return iSource;
         } catch (Exception var7) {
            this.catalogManager.debug.message(1, "Failed to create InputSource", resolved);
            return null;
         }
      } else {
         return null;
      }
   }

   public Source resolve(String href, String base) throws TransformerException {
      String uri = href;
      String fragment = null;
      int hashPos = href.indexOf("#");
      if (hashPos >= 0) {
         uri = href.substring(0, hashPos);
         href.substring(hashPos + 1);
      }

      String result = null;

      try {
         result = this.catalog.resolveURI(href);
      } catch (Exception var9) {
      }

      if (result == null) {
         try {
            URL url = null;
            if (base == null) {
               url = new URL(uri);
               result = url.toString();
            } else {
               URL baseURL = new URL(base);
               url = href.length() == 0 ? baseURL : new URL(baseURL, uri);
               result = url.toString();
            }
         } catch (MalformedURLException var10) {
            String absBase = this.makeAbsolute(base);
            if (!absBase.equals(base)) {
               return this.resolve(href, absBase);
            }

            throw new TransformerException("Malformed URL " + href + "(base " + base + ")", var10);
         }
      }

      this.catalogManager.debug.message(2, "Resolved URI", href, result);
      SAXSource source = new SAXSource();
      source.setInputSource(new InputSource(result));
      this.setEntityResolver(source);
      return source;
   }

   private void setEntityResolver(SAXSource source) throws TransformerException {
      XMLReader reader = source.getXMLReader();
      if (reader == null) {
         SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());

         try {
            reader = spf.newSAXParser().getXMLReader();
         } catch (ParserConfigurationException var5) {
            throw new TransformerException(var5);
         } catch (SAXException var6) {
            throw new TransformerException(var6);
         }
      }

      reader.setEntityResolver(this);
      source.setXMLReader(reader);
   }

   private String makeAbsolute(String uri) {
      if (uri == null) {
         uri = "";
      }

      try {
         URL url = new URL(uri);
         return url.toString();
      } catch (MalformedURLException var5) {
         try {
            URL fileURL = FileURL.makeURL(uri);
            return fileURL.toString();
         } catch (MalformedURLException var4) {
            return uri;
         }
      }
   }
}
