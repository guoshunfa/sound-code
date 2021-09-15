package com.sun.xml.internal.ws.server;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

final class WSDLGenResolver implements WSDLResolver {
   private final List<SDDocumentImpl> docs;
   private final List<SDDocumentSource> newDocs = new ArrayList();
   private SDDocumentSource concreteWsdlSource;
   private SDDocumentImpl abstractWsdl;
   private SDDocumentImpl concreteWsdl;
   private final Map<String, List<SDDocumentImpl>> nsMapping = new HashMap();
   private final QName serviceName;
   private final QName portTypeName;

   public WSDLGenResolver(@NotNull List<SDDocumentImpl> docs, QName serviceName, QName portTypeName) {
      this.docs = docs;
      this.serviceName = serviceName;
      this.portTypeName = portTypeName;
      Iterator var4 = docs.iterator();

      while(var4.hasNext()) {
         SDDocumentImpl doc = (SDDocumentImpl)var4.next();
         if (doc.isWSDL()) {
            SDDocument.WSDL wsdl = (SDDocument.WSDL)doc;
            if (wsdl.hasPortType()) {
               this.abstractWsdl = doc;
            }
         }

         if (doc.isSchema()) {
            SDDocument.Schema schema = (SDDocument.Schema)doc;
            List<SDDocumentImpl> sysIds = (List)this.nsMapping.get(schema.getTargetNamespace());
            if (sysIds == null) {
               sysIds = new ArrayList();
               this.nsMapping.put(schema.getTargetNamespace(), sysIds);
            }

            ((List)sysIds).add(doc);
         }
      }

   }

   public Result getWSDL(String filename) {
      URL url = this.createURL(filename);
      MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
      xsb.setSystemId(url.toExternalForm());
      this.concreteWsdlSource = SDDocumentSource.create(url, xsb);
      this.newDocs.add(this.concreteWsdlSource);
      XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
      r.setSystemId(filename);
      return r;
   }

   private URL createURL(String filename) {
      try {
         return new URL("file:///" + filename);
      } catch (MalformedURLException var3) {
         throw new WebServiceException(var3);
      }
   }

   public Result getAbstractWSDL(Holder<String> filename) {
      if (this.abstractWsdl != null) {
         filename.value = this.abstractWsdl.getURL().toString();
         return null;
      } else {
         URL url = this.createURL((String)filename.value);
         MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
         xsb.setSystemId(url.toExternalForm());
         SDDocumentSource abstractWsdlSource = SDDocumentSource.create(url, xsb);
         this.newDocs.add(abstractWsdlSource);
         XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
         r.setSystemId((String)filename.value);
         return r;
      }
   }

   public Result getSchemaOutput(String namespace, Holder<String> filename) {
      List<SDDocumentImpl> schemas = (List)this.nsMapping.get(namespace);
      if (schemas != null) {
         if (schemas.size() > 1) {
            throw new ServerRtException("server.rt.err", new Object[]{"More than one schema for the target namespace " + namespace});
         } else {
            filename.value = ((SDDocumentImpl)schemas.get(0)).getURL().toExternalForm();
            return null;
         }
      } else {
         URL url = this.createURL((String)filename.value);
         MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
         xsb.setSystemId(url.toExternalForm());
         SDDocumentSource sd = SDDocumentSource.create(url, xsb);
         this.newDocs.add(sd);
         XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
         r.setSystemId((String)filename.value);
         return r;
      }
   }

   public SDDocumentImpl updateDocs() {
      SDDocumentImpl docImpl;
      for(Iterator var1 = this.newDocs.iterator(); var1.hasNext(); this.docs.add(docImpl)) {
         SDDocumentSource doc = (SDDocumentSource)var1.next();
         docImpl = SDDocumentImpl.create(doc, this.serviceName, this.portTypeName);
         if (doc == this.concreteWsdlSource) {
            this.concreteWsdl = docImpl;
         }
      }

      return this.concreteWsdl;
   }
}
