package com.sun.xml.internal.ws.util.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.developer.ValidationErrorHandler;
import com.sun.xml.internal.ws.server.SDDocumentImpl;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.NamespaceSupport;

public abstract class AbstractSchemaValidationTube extends AbstractFilterTubeImpl {
   private static final Logger LOGGER = Logger.getLogger(AbstractSchemaValidationTube.class.getName());
   protected final WSBinding binding;
   protected final SchemaValidationFeature feature;
   protected final DocumentAddressResolver resolver = new AbstractSchemaValidationTube.ValidationDocumentAddressResolver();
   protected final SchemaFactory sf;

   public AbstractSchemaValidationTube(WSBinding binding, Tube next) {
      super(next);
      this.binding = binding;
      this.feature = (SchemaValidationFeature)binding.getFeature(SchemaValidationFeature.class);
      this.sf = XmlUtil.allowExternalAccess(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"), "file", false);
   }

   protected AbstractSchemaValidationTube(AbstractSchemaValidationTube that, TubeCloner cloner) {
      super(that, cloner);
      this.binding = that.binding;
      this.feature = that.feature;
      this.sf = that.sf;
   }

   protected abstract Validator getValidator();

   protected abstract boolean isNoValidation();

   private Document createDOM(SDDocument doc) {
      ByteArrayBuffer bab = new ByteArrayBuffer();

      try {
         doc.writeTo((PortAddressResolver)null, this.resolver, (OutputStream)bab);
      } catch (IOException var8) {
         throw new WebServiceException(var8);
      }

      Transformer trans = XmlUtil.newTransformer();
      Source source = new StreamSource(bab.newInputStream(), (String)null);
      DOMResult result = new DOMResult();

      try {
         trans.transform(source, result);
      } catch (TransformerException var7) {
         throw new WebServiceException(var7);
      }

      return (Document)result.getNode();
   }

   private void updateMultiSchemaForTns(String tns, String systemId, Map<String, List<String>> schemas) {
      List<String> docIdList = (List)schemas.get(tns);
      if (docIdList == null) {
         docIdList = new ArrayList();
         schemas.put(tns, docIdList);
      }

      ((List)docIdList).add(systemId);
   }

   protected Source[] getSchemaSources(Iterable<SDDocument> docs, AbstractSchemaValidationTube.MetadataResolverImpl mdresolver) {
      Map<String, DOMSource> inlinedSchemas = new HashMap();
      Map<String, List<String>> multiSchemaForTns = new HashMap();
      Iterator var5 = docs.iterator();

      while(var5.hasNext()) {
         SDDocument sdoc = (SDDocument)var5.next();
         if (sdoc.isWSDL()) {
            Document dom = this.createDOM(sdoc);
            this.addSchemaFragmentSource(dom, sdoc.getURL().toExternalForm(), inlinedSchemas);
         } else if (sdoc.isSchema()) {
            this.updateMultiSchemaForTns(((SDDocument.Schema)sdoc).getTargetNamespace(), sdoc.getURL().toExternalForm(), multiSchemaForTns);
         }
      }

      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, (String)"WSDL inlined schema fragment documents(these are used to create a pseudo schema) = {0}", (Object)inlinedSchemas.keySet());
      }

      var5 = inlinedSchemas.values().iterator();

      while(var5.hasNext()) {
         DOMSource src = (DOMSource)var5.next();
         String tns = this.getTargetNamespace(src);
         this.updateMultiSchemaForTns(tns, src.getSystemId(), multiSchemaForTns);
      }

      if (multiSchemaForTns.isEmpty()) {
         return new Source[0];
      } else if (multiSchemaForTns.size() == 1 && ((List)multiSchemaForTns.values().iterator().next()).size() == 1) {
         String systemId = (String)((List)multiSchemaForTns.values().iterator().next()).get(0);
         return new Source[]{(Source)inlinedSchemas.get(systemId)};
      } else {
         mdresolver.addSchemas(inlinedSchemas.values());
         Map<String, String> oneSchemaForTns = new HashMap();
         int i = 0;

         Map.Entry e;
         String systemId;
         for(Iterator var17 = multiSchemaForTns.entrySet().iterator(); var17.hasNext(); oneSchemaForTns.put(e.getKey(), systemId)) {
            e = (Map.Entry)var17.next();
            List<String> sameTnsSchemas = (List)e.getValue();
            if (sameTnsSchemas.size() > 1) {
               systemId = "file:x-jax-ws-include-" + i++;
               Source src = this.createSameTnsPseudoSchema((String)e.getKey(), sameTnsSchemas, systemId);
               mdresolver.addSchema(src);
            } else {
               systemId = (String)sameTnsSchemas.get(0);
            }
         }

         Source pseudoSchema = this.createMasterPseudoSchema(oneSchemaForTns);
         return new Source[]{pseudoSchema};
      }
   }

   @Nullable
   private void addSchemaFragmentSource(Document doc, String systemId, Map<String, DOMSource> map) {
      Element e = doc.getDocumentElement();

      assert e.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/");

      assert e.getLocalName().equals("definitions");

      NodeList typesList = e.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "types");

      for(int i = 0; i < typesList.getLength(); ++i) {
         NodeList schemaList = ((Element)typesList.item(i)).getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");

         for(int j = 0; j < schemaList.getLength(); ++j) {
            Element elem = (Element)schemaList.item(j);
            NamespaceSupport nss = new NamespaceSupport();
            this.buildNamespaceSupport(nss, elem);
            this.patchDOMFragment(nss, elem);
            String docId = systemId + "#schema" + j;
            map.put(docId, new DOMSource(elem, docId));
         }
      }

   }

   private void buildNamespaceSupport(NamespaceSupport nss, Node node) {
      if (node != null && node.getNodeType() == 1) {
         this.buildNamespaceSupport(nss, node.getParentNode());
         nss.pushContext();
         NamedNodeMap atts = node.getAttributes();

         for(int i = 0; i < atts.getLength(); ++i) {
            Attr a = (Attr)atts.item(i);
            if ("xmlns".equals(a.getPrefix())) {
               nss.declarePrefix(a.getLocalName(), a.getValue());
            } else if ("xmlns".equals(a.getName())) {
               nss.declarePrefix("", a.getValue());
            }
         }

      }
   }

   @Nullable
   private void patchDOMFragment(NamespaceSupport nss, Element elem) {
      NamedNodeMap atts = elem.getAttributes();
      Enumeration en = nss.getPrefixes();

      while(en.hasMoreElements()) {
         String prefix = (String)en.nextElement();

         for(int i = 0; i < atts.getLength(); ++i) {
            Attr a = (Attr)atts.item(i);
            if (!"xmlns".equals(a.getPrefix()) || !a.getLocalName().equals(prefix)) {
               if (LOGGER.isLoggable(Level.FINE)) {
                  LOGGER.log(Level.FINE, "Patching with xmlns:{0}={1}", new Object[]{prefix, nss.getURI(prefix)});
               }

               elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, nss.getURI(prefix));
            }
         }
      }

   }

   @Nullable
   private Source createSameTnsPseudoSchema(String tns, Collection<String> docs, String pseudoSystemId) {
      assert docs.size() > 1;

      final StringBuilder sb = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'");
      if (!tns.equals("")) {
         sb.append(" targetNamespace='").append(tns).append("'");
      }

      sb.append(">\n");
      Iterator var5 = docs.iterator();

      while(var5.hasNext()) {
         String systemId = (String)var5.next();
         sb.append("<xsd:include schemaLocation='").append(systemId).append("'/>\n");
      }

      sb.append("</xsd:schema>\n");
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, "Pseudo Schema for the same tns={0}is {1}", new Object[]{tns, sb});
      }

      return new StreamSource(pseudoSystemId) {
         public Reader getReader() {
            return new StringReader(sb.toString());
         }
      };
   }

   private Source createMasterPseudoSchema(Map<String, String> docs) {
      final StringBuilder sb = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:x-jax-ws-master'>\n");

      for(Iterator var3 = docs.entrySet().iterator(); var3.hasNext(); sb.append("/>\n")) {
         Map.Entry<String, String> e = (Map.Entry)var3.next();
         String systemId = (String)e.getValue();
         String ns = (String)e.getKey();
         sb.append("<xsd:import schemaLocation='").append(systemId).append("'");
         if (!ns.equals("")) {
            sb.append(" namespace='").append(ns).append("'");
         }
      }

      sb.append("</xsd:schema>");
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, (String)"Master Pseudo Schema = {0}", (Object)sb);
      }

      return new StreamSource("file:x-jax-ws-master-doc") {
         public Reader getReader() {
            return new StringReader(sb.toString());
         }
      };
   }

   protected void doProcess(Packet packet) throws SAXException {
      this.getValidator().reset();
      Class handlerClass = this.feature.getErrorHandler();

      ValidationErrorHandler handler;
      try {
         handler = (ValidationErrorHandler)handlerClass.newInstance();
      } catch (Exception var8) {
         throw new WebServiceException(var8);
      }

      handler.setPacket(packet);
      this.getValidator().setErrorHandler(handler);
      Message msg = packet.getMessage().copy();
      Source source = msg.readPayloadAsSource();

      try {
         this.getValidator().validate(source);
      } catch (IOException var7) {
         throw new WebServiceException(var7);
      }
   }

   private String getTargetNamespace(DOMSource src) {
      Element elem = (Element)src.getNode();
      return elem.getAttribute("targetNamespace");
   }

   protected class MetadataResolverImpl implements SDDocumentResolver, LSResourceResolver {
      final Map<String, SDDocument> docs = new HashMap();
      final Map<String, SDDocument> nsMapping = new HashMap();

      public MetadataResolverImpl() {
      }

      public MetadataResolverImpl(Iterable<SDDocument> it) {
         Iterator var3 = it.iterator();

         while(var3.hasNext()) {
            SDDocument doc = (SDDocument)var3.next();
            if (doc.isSchema()) {
               this.docs.put(doc.getURL().toExternalForm(), doc);
               this.nsMapping.put(((SDDocument.Schema)doc).getTargetNamespace(), doc);
            }
         }

      }

      void addSchema(Source schema) {
         assert schema.getSystemId() != null;

         String systemId = schema.getSystemId();

         try {
            XMLStreamBufferResult xsbr = (XMLStreamBufferResult)XmlUtil.identityTransform(schema, new XMLStreamBufferResult());
            SDDocumentSource sds = SDDocumentSource.create(new URL(systemId), xsbr.getXMLStreamBuffer());
            SDDocument sdoc = SDDocumentImpl.create(sds, new QName(""), new QName(""));
            this.docs.put(systemId, sdoc);
            this.nsMapping.put(((SDDocument.Schema)sdoc).getTargetNamespace(), sdoc);
         } catch (Exception var6) {
            AbstractSchemaValidationTube.LOGGER.log(Level.WARNING, (String)"Exception in adding schemas to resolver", (Throwable)var6);
         }

      }

      void addSchemas(Collection<? extends Source> schemas) {
         Iterator var2 = schemas.iterator();

         while(var2.hasNext()) {
            Source src = (Source)var2.next();
            this.addSchema(src);
         }

      }

      public SDDocument resolve(String systemId) {
         SDDocument sdi = (SDDocument)this.docs.get(systemId);
         if (sdi == null) {
            SDDocumentSource sds;
            try {
               sds = SDDocumentSource.create(new URL(systemId));
            } catch (MalformedURLException var5) {
               throw new WebServiceException(var5);
            }

            sdi = SDDocumentImpl.create(sds, new QName(""), new QName(""));
            this.docs.put(systemId, sdi);
         }

         return (SDDocument)sdi;
      }

      public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
         if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
            AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "type={0} namespaceURI={1} publicId={2} systemId={3} baseURI={4}", new Object[]{type, namespaceURI, publicId, systemId, baseURI});
         }

         try {
            final SDDocument doc;
            if (systemId == null) {
               doc = (SDDocument)this.nsMapping.get(namespaceURI);
            } else {
               URI rel = baseURI != null ? (new URI(baseURI)).resolve(systemId) : new URI(systemId);
               doc = (SDDocument)this.docs.get(rel.toString());
            }

            if (doc != null) {
               return new LSInput() {
                  public Reader getCharacterStream() {
                     return null;
                  }

                  public void setCharacterStream(Reader characterStream) {
                     throw new UnsupportedOperationException();
                  }

                  public InputStream getByteStream() {
                     ByteArrayBuffer bab = new ByteArrayBuffer();

                     try {
                        doc.writeTo((PortAddressResolver)null, AbstractSchemaValidationTube.this.resolver, (OutputStream)bab);
                     } catch (IOException var3) {
                        throw new WebServiceException(var3);
                     }

                     return bab.newInputStream();
                  }

                  public void setByteStream(InputStream byteStream) {
                     throw new UnsupportedOperationException();
                  }

                  public String getStringData() {
                     return null;
                  }

                  public void setStringData(String stringData) {
                     throw new UnsupportedOperationException();
                  }

                  public String getSystemId() {
                     return doc.getURL().toExternalForm();
                  }

                  public void setSystemId(String systemId) {
                     throw new UnsupportedOperationException();
                  }

                  public String getPublicId() {
                     return null;
                  }

                  public void setPublicId(String publicId) {
                     throw new UnsupportedOperationException();
                  }

                  public String getBaseURI() {
                     return doc.getURL().toExternalForm();
                  }

                  public void setBaseURI(String baseURI) {
                     throw new UnsupportedOperationException();
                  }

                  public String getEncoding() {
                     return null;
                  }

                  public void setEncoding(String encoding) {
                     throw new UnsupportedOperationException();
                  }

                  public boolean getCertifiedText() {
                     return false;
                  }

                  public void setCertifiedText(boolean certifiedText) {
                     throw new UnsupportedOperationException();
                  }
               };
            }
         } catch (Exception var8) {
            AbstractSchemaValidationTube.LOGGER.log(Level.WARNING, (String)"Exception in LSResourceResolver impl", (Throwable)var8);
         }

         if (AbstractSchemaValidationTube.LOGGER.isLoggable(Level.FINE)) {
            AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Don''t know about systemId={0} baseURI={1}", new Object[]{systemId, baseURI});
         }

         return null;
      }
   }

   private static class ValidationDocumentAddressResolver implements DocumentAddressResolver {
      private ValidationDocumentAddressResolver() {
      }

      @Nullable
      public String getRelativeAddressFor(@NotNull SDDocument current, @NotNull SDDocument referenced) {
         AbstractSchemaValidationTube.LOGGER.log(Level.FINE, "Current = {0} resolved relative={1}", new Object[]{current.getURL(), referenced.getURL()});
         return referenced.getURL().toExternalForm();
      }

      // $FF: synthetic method
      ValidationDocumentAddressResolver(Object x0) {
         this();
      }
   }
}
