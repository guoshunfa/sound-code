package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.runtime.output.DOMOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.InterningXmlVisitor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.SAXConnector;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BinderImpl<XmlNode> extends Binder<XmlNode> {
   private final JAXBContextImpl context;
   private UnmarshallerImpl unmarshaller;
   private MarshallerImpl marshaller;
   private final InfosetScanner<XmlNode> scanner;
   private final AssociationMap<XmlNode> assoc = new AssociationMap();

   BinderImpl(JAXBContextImpl _context, InfosetScanner<XmlNode> scanner) {
      this.context = _context;
      this.scanner = scanner;
   }

   private UnmarshallerImpl getUnmarshaller() {
      if (this.unmarshaller == null) {
         this.unmarshaller = new UnmarshallerImpl(this.context, this.assoc);
      }

      return this.unmarshaller;
   }

   private MarshallerImpl getMarshaller() {
      if (this.marshaller == null) {
         this.marshaller = new MarshallerImpl(this.context, this.assoc);
      }

      return this.marshaller;
   }

   public void marshal(Object jaxbObject, XmlNode xmlNode) throws JAXBException {
      if (xmlNode != null && jaxbObject != null) {
         this.getMarshaller().marshal(jaxbObject, (XmlOutput)this.createOutput(xmlNode));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private DOMOutput createOutput(XmlNode xmlNode) {
      return new DOMOutput((Node)xmlNode, this.assoc);
   }

   public Object updateJAXB(XmlNode xmlNode) throws JAXBException {
      return this.associativeUnmarshal(xmlNode, true, (Class)null);
   }

   public Object unmarshal(XmlNode xmlNode) throws JAXBException {
      return this.associativeUnmarshal(xmlNode, false, (Class)null);
   }

   public <T> JAXBElement<T> unmarshal(XmlNode xmlNode, Class<T> expectedType) throws JAXBException {
      if (expectedType == null) {
         throw new IllegalArgumentException();
      } else {
         return (JAXBElement)this.associativeUnmarshal(xmlNode, true, expectedType);
      }
   }

   public void setSchema(Schema schema) {
      this.getMarshaller().setSchema(schema);
      this.getUnmarshaller().setSchema(schema);
   }

   public Schema getSchema() {
      return this.getUnmarshaller().getSchema();
   }

   private Object associativeUnmarshal(XmlNode xmlNode, boolean inplace, Class expectedType) throws JAXBException {
      if (xmlNode == null) {
         throw new IllegalArgumentException();
      } else {
         JaxBeanInfo bi = null;
         if (expectedType != null) {
            bi = this.context.getBeanInfo(expectedType, true);
         }

         InterningXmlVisitor handler = new InterningXmlVisitor(this.getUnmarshaller().createUnmarshallerHandler(this.scanner, inplace, bi));
         this.scanner.setContentHandler(new SAXConnector(handler, this.scanner.getLocator()));

         try {
            this.scanner.scan(xmlNode);
         } catch (SAXException var7) {
            throw this.unmarshaller.createUnmarshalException(var7);
         }

         return handler.getContext().getResult();
      }
   }

   public XmlNode getXMLNode(Object jaxbObject) {
      if (jaxbObject == null) {
         throw new IllegalArgumentException();
      } else {
         AssociationMap.Entry<XmlNode> e = this.assoc.byPeer(jaxbObject);
         return e == null ? null : e.element();
      }
   }

   public Object getJAXBNode(XmlNode xmlNode) {
      if (xmlNode == null) {
         throw new IllegalArgumentException();
      } else {
         AssociationMap.Entry e = this.assoc.byElement(xmlNode);
         if (e == null) {
            return null;
         } else {
            return e.outer() != null ? e.outer() : e.inner();
         }
      }
   }

   public XmlNode updateXML(Object jaxbObject) throws JAXBException {
      return this.updateXML(jaxbObject, this.getXMLNode(jaxbObject));
   }

   public XmlNode updateXML(Object jaxbObject, XmlNode xmlNode) throws JAXBException {
      if (jaxbObject != null && xmlNode != null) {
         Element e = (Element)xmlNode;
         Node ns = e.getNextSibling();
         Node p = e.getParentNode();
         p.removeChild(e);
         JaxBeanInfo bi = this.context.getBeanInfo(jaxbObject, true);
         if (!bi.isElement()) {
            jaxbObject = new JAXBElement(new QName(e.getNamespaceURI(), e.getLocalName()), bi.jaxbType, jaxbObject);
         }

         this.getMarshaller().marshal(jaxbObject, (Node)p);
         Node newNode = p.getLastChild();
         p.removeChild(newNode);
         p.insertBefore(newNode, ns);
         return newNode;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
      this.getUnmarshaller().setEventHandler(handler);
      this.getMarshaller().setEventHandler(handler);
   }

   public ValidationEventHandler getEventHandler() {
      return this.getUnmarshaller().getEventHandler();
   }

   public Object getProperty(String name) throws PropertyException {
      if (name == null) {
         throw new IllegalArgumentException(Messages.NULL_PROPERTY_NAME.format());
      } else if (this.excludeProperty(name)) {
         throw new PropertyException(name);
      } else {
         Object prop = null;
         Object var3 = null;

         try {
            prop = this.getMarshaller().getProperty(name);
            return prop;
         } catch (PropertyException var6) {
            try {
               prop = this.getUnmarshaller().getProperty(name);
               return prop;
            } catch (PropertyException var5) {
               var5.setStackTrace(Thread.currentThread().getStackTrace());
               throw var5;
            }
         }
      }
   }

   public void setProperty(String name, Object value) throws PropertyException {
      if (name == null) {
         throw new IllegalArgumentException(Messages.NULL_PROPERTY_NAME.format());
      } else if (this.excludeProperty(name)) {
         throw new PropertyException(name, value);
      } else {
         Object var3 = null;

         try {
            this.getMarshaller().setProperty(name, value);
         } catch (PropertyException var6) {
            try {
               this.getUnmarshaller().setProperty(name, value);
            } catch (PropertyException var5) {
               var5.setStackTrace(Thread.currentThread().getStackTrace());
               throw var5;
            }
         }
      }
   }

   private boolean excludeProperty(String name) {
      return name.equals("com.sun.xml.internal.bind.characterEscapeHandler") || name.equals("com.sun.xml.internal.bind.xmlDeclaration") || name.equals("com.sun.xml.internal.bind.xmlHeaders");
   }
}
