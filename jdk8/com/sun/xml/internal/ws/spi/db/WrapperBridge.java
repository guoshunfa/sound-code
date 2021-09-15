package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class WrapperBridge<T> implements XMLBridge<T> {
   BindingContext parent;
   TypeInfo typeInfo;
   static final String WrapperPrefix = "w";
   static final String WrapperPrefixColon = "w:";

   public WrapperBridge(BindingContext p, TypeInfo ti) {
      this.parent = p;
      this.typeInfo = ti;
   }

   public BindingContext context() {
      return this.parent;
   }

   public TypeInfo getTypeInfo() {
      return this.typeInfo;
   }

   public final void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
      WrapperComposite w = (WrapperComposite)object;
      Attributes att = new Attributes() {
         public int getLength() {
            return 0;
         }

         public String getURI(int index) {
            return null;
         }

         public String getLocalName(int index) {
            return null;
         }

         public String getQName(int index) {
            return null;
         }

         public String getType(int index) {
            return null;
         }

         public String getValue(int index) {
            return null;
         }

         public int getIndex(String uri, String localName) {
            return 0;
         }

         public int getIndex(String qName) {
            return 0;
         }

         public String getType(String uri, String localName) {
            return null;
         }

         public String getType(String qName) {
            return null;
         }

         public String getValue(String uri, String localName) {
            return null;
         }

         public String getValue(String qName) {
            return null;
         }
      };

      try {
         contentHandler.startPrefixMapping("w", this.typeInfo.tagName.getNamespaceURI());
         contentHandler.startElement(this.typeInfo.tagName.getNamespaceURI(), this.typeInfo.tagName.getLocalPart(), "w:" + this.typeInfo.tagName.getLocalPart(), att);
      } catch (SAXException var10) {
         throw new JAXBException(var10);
      }

      if (w.bridges != null) {
         for(int i = 0; i < w.bridges.length; ++i) {
            if (w.bridges[i] instanceof RepeatedElementBridge) {
               RepeatedElementBridge rbridge = (RepeatedElementBridge)w.bridges[i];
               Iterator itr = rbridge.collectionHandler().iterator(w.values[i]);

               while(itr.hasNext()) {
                  rbridge.marshal(itr.next(), contentHandler, am);
               }
            } else {
               w.bridges[i].marshal(w.values[i], contentHandler, am);
            }
         }
      }

      try {
         contentHandler.endElement(this.typeInfo.tagName.getNamespaceURI(), this.typeInfo.tagName.getLocalPart(), (String)null);
         contentHandler.endPrefixMapping("w");
      } catch (SAXException var9) {
         throw new JAXBException(var9);
      }
   }

   public void marshal(T object, Node output) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public void marshal(T object, OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
   }

   public final void marshal(T object, Result result) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public final void marshal(T object, XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
      WrapperComposite w = (WrapperComposite)object;

      try {
         String prefix = output.getPrefix(this.typeInfo.tagName.getNamespaceURI());
         if (prefix == null) {
            prefix = "w";
         }

         output.writeStartElement(prefix, this.typeInfo.tagName.getLocalPart(), this.typeInfo.tagName.getNamespaceURI());
         output.writeNamespace(prefix, this.typeInfo.tagName.getNamespaceURI());
      } catch (XMLStreamException var9) {
         var9.printStackTrace();
         throw new DatabindingException(var9);
      }

      if (w.bridges != null) {
         for(int i = 0; i < w.bridges.length; ++i) {
            if (w.bridges[i] instanceof RepeatedElementBridge) {
               RepeatedElementBridge rbridge = (RepeatedElementBridge)w.bridges[i];
               Iterator itr = rbridge.collectionHandler().iterator(w.values[i]);

               while(itr.hasNext()) {
                  rbridge.marshal(itr.next(), output, am);
               }
            } else {
               w.bridges[i].marshal(w.values[i], output, am);
            }
         }
      }

      try {
         output.writeEndElement();
      } catch (XMLStreamException var8) {
         throw new DatabindingException(var8);
      }
   }

   public final T unmarshal(InputStream in) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public final T unmarshal(Node n, AttachmentUnmarshaller au) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public final T unmarshal(Source in, AttachmentUnmarshaller au) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public final T unmarshal(XMLStreamReader in, AttachmentUnmarshaller au) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public boolean supportOutputStream() {
      return false;
   }
}
