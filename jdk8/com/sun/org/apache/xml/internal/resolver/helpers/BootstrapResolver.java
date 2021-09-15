package com.sun.org.apache.xml.internal.resolver.helpers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class BootstrapResolver implements EntityResolver, URIResolver {
   public static final String xmlCatalogXSD = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd";
   public static final String xmlCatalogRNG = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng";
   public static final String xmlCatalogPubId = "-//OASIS//DTD XML Catalogs V1.0//EN";
   public static final String xmlCatalogSysId = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd";
   private final Map<String, String> publicMap = new HashMap();
   private final Map<String, String> systemMap = new HashMap();
   private final Map<String, String> uriMap = new HashMap();

   public BootstrapResolver() {
      URL url = this.getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.dtd");
      if (url != null) {
         this.publicMap.put("-//OASIS//DTD XML Catalogs V1.0//EN", url.toString());
         this.systemMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd", url.toString());
      }

      url = this.getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.rng");
      if (url != null) {
         this.uriMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng", url.toString());
      }

      url = this.getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.xsd");
      if (url != null) {
         this.uriMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd", url.toString());
      }

   }

   public InputSource resolveEntity(String publicId, String systemId) {
      String resolved = null;
      if (systemId != null && this.systemMap.containsKey(systemId)) {
         resolved = (String)this.systemMap.get(systemId);
      } else if (publicId != null && this.publicMap.containsKey(publicId)) {
         resolved = (String)this.publicMap.get(publicId);
      }

      if (resolved != null) {
         try {
            InputSource iSource = new InputSource(resolved);
            iSource.setPublicId(publicId);
            URL url = new URL(resolved);
            InputStream iStream = url.openStream();
            iSource.setByteStream(iStream);
            return iSource;
         } catch (Exception var7) {
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
      if (href != null && this.uriMap.containsKey(href)) {
         result = (String)this.uriMap.get(href);
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
         } catch (MalformedURLException var9) {
            String absBase = this.makeAbsolute(base);
            if (!absBase.equals(base)) {
               return this.resolve(href, absBase);
            }

            throw new TransformerException("Malformed URL " + href + "(base " + base + ")", var9);
         }
      }

      SAXSource source = new SAXSource();
      source.setInputSource(new InputSource(result));
      return source;
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
