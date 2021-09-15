package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class XMLCatalogResolver implements XMLEntityResolver, EntityResolver2, LSResourceResolver {
   private CatalogManager fResolverCatalogManager;
   private Catalog fCatalog;
   private String[] fCatalogsList;
   private boolean fCatalogsChanged;
   private boolean fPreferPublic;
   private boolean fUseLiteralSystemId;

   public XMLCatalogResolver() {
      this((String[])null, true);
   }

   public XMLCatalogResolver(String[] catalogs) {
      this(catalogs, true);
   }

   public XMLCatalogResolver(String[] catalogs, boolean preferPublic) {
      this.fResolverCatalogManager = null;
      this.fCatalog = null;
      this.fCatalogsList = null;
      this.fCatalogsChanged = true;
      this.fPreferPublic = true;
      this.fUseLiteralSystemId = true;
      this.init(catalogs, preferPublic);
   }

   public final synchronized String[] getCatalogList() {
      return this.fCatalogsList != null ? (String[])((String[])this.fCatalogsList.clone()) : null;
   }

   public final synchronized void setCatalogList(String[] catalogs) {
      this.fCatalogsChanged = true;
      this.fCatalogsList = catalogs != null ? (String[])((String[])catalogs.clone()) : null;
   }

   public final synchronized void clear() {
      this.fCatalog = null;
   }

   public final boolean getPreferPublic() {
      return this.fPreferPublic;
   }

   public final void setPreferPublic(boolean preferPublic) {
      this.fPreferPublic = preferPublic;
      this.fResolverCatalogManager.setPreferPublic(preferPublic);
   }

   public final boolean getUseLiteralSystemId() {
      return this.fUseLiteralSystemId;
   }

   public final void setUseLiteralSystemId(boolean useLiteralSystemId) {
      this.fUseLiteralSystemId = useLiteralSystemId;
   }

   public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
      String resolvedId = null;
      if (publicId != null && systemId != null) {
         resolvedId = this.resolvePublic(publicId, systemId);
      } else if (systemId != null) {
         resolvedId = this.resolveSystem(systemId);
      }

      if (resolvedId != null) {
         InputSource source = new InputSource(resolvedId);
         source.setPublicId(publicId);
         return source;
      } else {
         return null;
      }
   }

   public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
      String resolvedId = null;
      if (!this.getUseLiteralSystemId() && baseURI != null) {
         try {
            URI uri = new URI(new URI(baseURI), systemId);
            systemId = uri.toString();
         } catch (URI.MalformedURIException var7) {
         }
      }

      if (publicId != null && systemId != null) {
         resolvedId = this.resolvePublic(publicId, systemId);
      } else if (systemId != null) {
         resolvedId = this.resolveSystem(systemId);
      }

      if (resolvedId != null) {
         InputSource source = new InputSource(resolvedId);
         source.setPublicId(publicId);
         return source;
      } else {
         return null;
      }
   }

   public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
      return null;
   }

   public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
      String resolvedId = null;

      try {
         if (namespaceURI != null) {
            resolvedId = this.resolveURI(namespaceURI);
         }

         if (!this.getUseLiteralSystemId() && baseURI != null) {
            try {
               URI uri = new URI(new URI(baseURI), systemId);
               systemId = uri.toString();
            } catch (URI.MalformedURIException var8) {
            }
         }

         if (resolvedId == null) {
            if (publicId != null && systemId != null) {
               resolvedId = this.resolvePublic(publicId, systemId);
            } else if (systemId != null) {
               resolvedId = this.resolveSystem(systemId);
            }
         }
      } catch (IOException var9) {
      }

      return resolvedId != null ? new DOMInputImpl(publicId, resolvedId, baseURI) : null;
   }

   public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
      String resolvedId = this.resolveIdentifier(resourceIdentifier);
      return resolvedId != null ? new XMLInputSource(resourceIdentifier.getPublicId(), resolvedId, resourceIdentifier.getBaseSystemId()) : null;
   }

   public String resolveIdentifier(XMLResourceIdentifier resourceIdentifier) throws IOException, XNIException {
      String resolvedId = null;
      String namespace = resourceIdentifier.getNamespace();
      if (namespace != null) {
         resolvedId = this.resolveURI(namespace);
      }

      if (resolvedId == null) {
         String publicId = resourceIdentifier.getPublicId();
         String systemId = this.getUseLiteralSystemId() ? resourceIdentifier.getLiteralSystemId() : resourceIdentifier.getExpandedSystemId();
         if (publicId != null && systemId != null) {
            resolvedId = this.resolvePublic(publicId, systemId);
         } else if (systemId != null) {
            resolvedId = this.resolveSystem(systemId);
         }
      }

      return resolvedId;
   }

   public final synchronized String resolveSystem(String systemId) throws IOException {
      if (this.fCatalogsChanged) {
         this.parseCatalogs();
         this.fCatalogsChanged = false;
      }

      return this.fCatalog != null ? this.fCatalog.resolveSystem(systemId) : null;
   }

   public final synchronized String resolvePublic(String publicId, String systemId) throws IOException {
      if (this.fCatalogsChanged) {
         this.parseCatalogs();
         this.fCatalogsChanged = false;
      }

      return this.fCatalog != null ? this.fCatalog.resolvePublic(publicId, systemId) : null;
   }

   public final synchronized String resolveURI(String uri) throws IOException {
      if (this.fCatalogsChanged) {
         this.parseCatalogs();
         this.fCatalogsChanged = false;
      }

      return this.fCatalog != null ? this.fCatalog.resolveURI(uri) : null;
   }

   private void init(String[] catalogs, boolean preferPublic) {
      this.fCatalogsList = catalogs != null ? (String[])((String[])catalogs.clone()) : null;
      this.fPreferPublic = preferPublic;
      this.fResolverCatalogManager = new CatalogManager();
      this.fResolverCatalogManager.setAllowOasisXMLCatalogPI(false);
      this.fResolverCatalogManager.setCatalogClassName("com.sun.org.apache.xml.internal.resolver.Catalog");
      this.fResolverCatalogManager.setCatalogFiles("");
      this.fResolverCatalogManager.setIgnoreMissingProperties(true);
      this.fResolverCatalogManager.setPreferPublic(this.fPreferPublic);
      this.fResolverCatalogManager.setRelativeCatalogs(false);
      this.fResolverCatalogManager.setUseStaticCatalog(false);
      this.fResolverCatalogManager.setVerbosity(0);
   }

   private void parseCatalogs() throws IOException {
      if (this.fCatalogsList != null) {
         this.fCatalog = new Catalog(this.fResolverCatalogManager);
         this.attachReaderToCatalog(this.fCatalog);

         for(int i = 0; i < this.fCatalogsList.length; ++i) {
            String catalog = this.fCatalogsList[i];
            if (catalog != null && catalog.length() > 0) {
               this.fCatalog.parseCatalog(catalog);
            }
         }
      } else {
         this.fCatalog = null;
      }

   }

   private void attachReaderToCatalog(Catalog catalog) {
      SAXParserFactory spf = JdkXmlUtils.getSAXFactory(catalog.getCatalogManager().overrideDefaultParser());
      spf.setValidating(false);
      SAXCatalogReader saxReader = new SAXCatalogReader(spf);
      saxReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "com.sun.org.apache.xml.internal.resolver.readers.OASISXMLCatalogReader");
      catalog.addReader("application/xml", saxReader);
   }
}
