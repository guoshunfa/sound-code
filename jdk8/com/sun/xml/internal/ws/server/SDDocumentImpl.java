package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import com.sun.xml.internal.ws.wsdl.parser.ParserUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.internal.ws.wsdl.writer.DocumentLocationResolver;
import com.sun.xml.internal.ws.wsdl.writer.WSDLPatcher;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public class SDDocumentImpl extends SDDocumentSource implements SDDocument {
   private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
   private static final QName SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
   private static final QName SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
   private static final QName SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
   private static final String VERSION_COMMENT;
   private final QName rootName;
   private final SDDocumentSource source;
   @Nullable
   List<SDDocumentFilter> filters;
   @Nullable
   SDDocumentResolver sddocResolver;
   private final URL url;
   private final Set<String> imports;

   public static SDDocumentImpl create(SDDocumentSource src, QName serviceName, QName portTypeName) {
      URL systemId = src.getSystemId();

      try {
         XMLStreamReader reader = src.read();

         SDDocumentImpl var6;
         try {
            XMLStreamReaderUtil.nextElementContent(reader);
            QName rootName = reader.getName();
            String tns;
            if (rootName.equals(WSDLConstants.QNAME_SCHEMA)) {
               tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
               HashSet importedDocs = new HashSet();

               while(XMLStreamReaderUtil.nextContent(reader) != 8) {
                  if (reader.getEventType() == 1) {
                     QName name = reader.getName();
                     if (SCHEMA_INCLUDE_QNAME.equals(name) || SCHEMA_IMPORT_QNAME.equals(name) || SCHEMA_REDEFINE_QNAME.equals(name)) {
                        String importedDoc = reader.getAttributeValue((String)null, "schemaLocation");
                        if (importedDoc != null) {
                           importedDocs.add((new URL(src.getSystemId(), importedDoc)).toString());
                        }
                     }
                  }
               }

               SDDocumentImpl.SchemaImpl var26 = new SDDocumentImpl.SchemaImpl(rootName, systemId, src, tns, importedDocs);
               return var26;
            }

            if (rootName.equals(WSDLConstants.QNAME_DEFINITIONS)) {
               tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
               boolean hasPortType = false;
               boolean hasService = false;
               Set<String> importedDocs = new HashSet();
               HashSet allServices = new HashSet();

               while(XMLStreamReaderUtil.nextContent(reader) != 8) {
                  if (reader.getEventType() == 1) {
                     QName name = reader.getName();
                     String importedDoc;
                     if (WSDLConstants.QNAME_PORT_TYPE.equals(name)) {
                        importedDoc = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                        if (portTypeName != null && portTypeName.getLocalPart().equals(importedDoc) && portTypeName.getNamespaceURI().equals(tns)) {
                           hasPortType = true;
                        }
                     } else if (WSDLConstants.QNAME_SERVICE.equals(name)) {
                        importedDoc = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                        QName sqn = new QName(tns, importedDoc);
                        allServices.add(sqn);
                        if (serviceName.equals(sqn)) {
                           hasService = true;
                        }
                     } else if (WSDLConstants.QNAME_IMPORT.equals(name)) {
                        importedDoc = reader.getAttributeValue((String)null, "location");
                        if (importedDoc != null) {
                           importedDocs.add((new URL(src.getSystemId(), importedDoc)).toString());
                        }
                     } else if (SCHEMA_INCLUDE_QNAME.equals(name) || SCHEMA_IMPORT_QNAME.equals(name) || SCHEMA_REDEFINE_QNAME.equals(name)) {
                        importedDoc = reader.getAttributeValue((String)null, "schemaLocation");
                        if (importedDoc != null) {
                           importedDocs.add((new URL(src.getSystemId(), importedDoc)).toString());
                        }
                     }
                  }
               }

               SDDocumentImpl.WSDLImpl var28 = new SDDocumentImpl.WSDLImpl(rootName, systemId, src, tns, hasPortType, hasService, importedDocs, allServices);
               return var28;
            }

            var6 = new SDDocumentImpl(rootName, systemId, src);
         } finally {
            reader.close();
         }

         return var6;
      } catch (WebServiceException var20) {
         throw new ServerRtException("runtime.parser.wsdl", new Object[]{systemId, var20});
      } catch (IOException var21) {
         throw new ServerRtException("runtime.parser.wsdl", new Object[]{systemId, var21});
      } catch (XMLStreamException var22) {
         throw new ServerRtException("runtime.parser.wsdl", new Object[]{systemId, var22});
      }
   }

   protected SDDocumentImpl(QName rootName, URL url, SDDocumentSource source) {
      this(rootName, url, source, new HashSet());
   }

   protected SDDocumentImpl(QName rootName, URL url, SDDocumentSource source, Set<String> imports) {
      if (url == null) {
         throw new IllegalArgumentException("Cannot construct SDDocument with null URL.");
      } else {
         this.rootName = rootName;
         this.source = source;
         this.url = url;
         this.imports = imports;
      }
   }

   void setFilters(List<SDDocumentFilter> filters) {
      this.filters = filters;
   }

   void setResolver(SDDocumentResolver sddocResolver) {
      this.sddocResolver = sddocResolver;
   }

   public QName getRootName() {
      return this.rootName;
   }

   public boolean isWSDL() {
      return false;
   }

   public boolean isSchema() {
      return false;
   }

   public URL getURL() {
      return this.url;
   }

   public XMLStreamReader read(XMLInputFactory xif) throws IOException, XMLStreamException {
      return this.source.read(xif);
   }

   public XMLStreamReader read() throws IOException, XMLStreamException {
      return this.source.read();
   }

   public URL getSystemId() {
      return this.url;
   }

   public Set<String> getImports() {
      return this.imports;
   }

   public void writeTo(OutputStream os) throws IOException {
      XMLStreamWriter w = null;
      boolean var12 = false;

      IOException ioe;
      try {
         var12 = true;
         w = XMLStreamWriterFactory.create(os, "UTF-8");
         w.writeStartDocument("UTF-8", "1.0");
         (new XMLStreamReaderToXMLStreamWriter()).bridge(this.source.read(), w);
         w.writeEndDocument();
         var12 = false;
      } catch (XMLStreamException var14) {
         ioe = new IOException(var14.getMessage());
         ioe.initCause(var14);
         throw ioe;
      } finally {
         if (var12) {
            try {
               if (w != null) {
                  w.close();
               }
            } catch (XMLStreamException var15) {
               IOException ioe = new IOException(var15.getMessage());
               ioe.initCause(var15);
               throw ioe;
            }

         }
      }

      try {
         if (w != null) {
            w.close();
         }

      } catch (XMLStreamException var13) {
         ioe = new IOException(var13.getMessage());
         ioe.initCause(var13);
         throw ioe;
      }
   }

   public void writeTo(PortAddressResolver portAddressResolver, DocumentAddressResolver resolver, OutputStream os) throws IOException {
      XMLStreamWriter w = null;
      boolean var14 = false;

      IOException ioe;
      try {
         var14 = true;
         w = XMLStreamWriterFactory.create(os, "UTF-8");
         w.writeStartDocument("UTF-8", "1.0");
         this.writeTo(portAddressResolver, resolver, w);
         w.writeEndDocument();
         var14 = false;
      } catch (XMLStreamException var16) {
         ioe = new IOException(var16.getMessage());
         ioe.initCause(var16);
         throw ioe;
      } finally {
         if (var14) {
            try {
               if (w != null) {
                  w.close();
               }
            } catch (XMLStreamException var17) {
               IOException ioe = new IOException(var17.getMessage());
               ioe.initCause(var17);
               throw ioe;
            }

         }
      }

      try {
         if (w != null) {
            w.close();
         }

      } catch (XMLStreamException var15) {
         ioe = new IOException(var15.getMessage());
         ioe.initCause(var15);
         throw ioe;
      }
   }

   public void writeTo(PortAddressResolver portAddressResolver, DocumentAddressResolver resolver, XMLStreamWriter out) throws XMLStreamException, IOException {
      SDDocumentFilter f;
      if (this.filters != null) {
         for(Iterator var4 = this.filters.iterator(); var4.hasNext(); out = f.filter(this, out)) {
            f = (SDDocumentFilter)var4.next();
         }
      }

      XMLStreamReader xsr = this.source.read();

      try {
         out.writeComment(VERSION_COMMENT);
         (new WSDLPatcher(portAddressResolver, new SDDocumentImpl.DocumentLocationResolverImpl(resolver))).bridge(xsr, out);
      } finally {
         xsr.close();
      }

   }

   static {
      VERSION_COMMENT = " Published by JAX-WS RI (http://jax-ws.java.net). RI's version is " + RuntimeVersion.VERSION + ". ";
   }

   private class DocumentLocationResolverImpl implements DocumentLocationResolver {
      private DocumentAddressResolver delegate;

      DocumentLocationResolverImpl(DocumentAddressResolver delegate) {
         this.delegate = delegate;
      }

      public String getLocationFor(String namespaceURI, String systemId) {
         if (SDDocumentImpl.this.sddocResolver == null) {
            return systemId;
         } else {
            try {
               URL ref = new URL(SDDocumentImpl.this.getURL(), systemId);
               SDDocument refDoc = SDDocumentImpl.this.sddocResolver.resolve(ref.toExternalForm());
               return refDoc == null ? systemId : this.delegate.getRelativeAddressFor(SDDocumentImpl.this, refDoc);
            } catch (MalformedURLException var5) {
               return null;
            }
         }
      }
   }

   private static final class WSDLImpl extends SDDocumentImpl implements SDDocument.WSDL {
      private final String targetNamespace;
      private final boolean hasPortType;
      private final boolean hasService;
      private final Set<QName> allServices;

      public WSDLImpl(QName rootName, URL url, SDDocumentSource source, String targetNamespace, boolean hasPortType, boolean hasService, Set<String> imports, Set<QName> allServices) {
         super(rootName, url, source, imports);
         this.targetNamespace = targetNamespace;
         this.hasPortType = hasPortType;
         this.hasService = hasService;
         this.allServices = allServices;
      }

      public String getTargetNamespace() {
         return this.targetNamespace;
      }

      public boolean hasPortType() {
         return this.hasPortType;
      }

      public boolean hasService() {
         return this.hasService;
      }

      public Set<QName> getAllServices() {
         return this.allServices;
      }

      public boolean isWSDL() {
         return true;
      }
   }

   private static final class SchemaImpl extends SDDocumentImpl implements SDDocument.Schema {
      private final String targetNamespace;

      public SchemaImpl(QName rootName, URL url, SDDocumentSource source, String targetNamespace, Set<String> imports) {
         super(rootName, url, source, imports);
         this.targetNamespace = targetNamespace;
      }

      public String getTargetNamespace() {
         return this.targetNamespace;
      }

      public boolean isSchema() {
         return true;
      }
   }
}
