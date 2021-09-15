package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class MarshallerBridge extends Bridge implements XMLBridge {
   protected MarshallerBridge(JAXBContextImpl context) {
      super(context);
   }

   public void marshal(Marshaller m, Object object, XMLStreamWriter output) throws JAXBException {
      m.setProperty("jaxb.fragment", true);

      try {
         m.marshal(object, output);
      } finally {
         m.setProperty("jaxb.fragment", false);
      }

   }

   public void marshal(Marshaller m, Object object, OutputStream output, NamespaceContext nsContext) throws JAXBException {
      m.setProperty("jaxb.fragment", true);

      try {
         ((MarshallerImpl)m).marshal(object, output, nsContext);
      } finally {
         m.setProperty("jaxb.fragment", false);
      }

   }

   public void marshal(Marshaller m, Object object, Node output) throws JAXBException {
      m.setProperty("jaxb.fragment", true);

      try {
         m.marshal(object, output);
      } finally {
         m.setProperty("jaxb.fragment", false);
      }

   }

   public void marshal(Marshaller m, Object object, ContentHandler contentHandler) throws JAXBException {
      m.setProperty("jaxb.fragment", true);

      try {
         m.marshal(object, contentHandler);
      } finally {
         m.setProperty("jaxb.fragment", false);
      }

   }

   public void marshal(Marshaller m, Object object, Result result) throws JAXBException {
      m.setProperty("jaxb.fragment", true);

      try {
         m.marshal(object, result);
      } finally {
         m.setProperty("jaxb.fragment", false);
      }

   }

   public Object unmarshal(Unmarshaller u, XMLStreamReader in) {
      throw new UnsupportedOperationException();
   }

   public Object unmarshal(Unmarshaller u, Source in) {
      throw new UnsupportedOperationException();
   }

   public Object unmarshal(Unmarshaller u, InputStream in) {
      throw new UnsupportedOperationException();
   }

   public Object unmarshal(Unmarshaller u, Node n) {
      throw new UnsupportedOperationException();
   }

   public TypeInfo getTypeInfo() {
      throw new UnsupportedOperationException();
   }

   public TypeReference getTypeReference() {
      throw new UnsupportedOperationException();
   }

   public BindingContext context() {
      throw new UnsupportedOperationException();
   }

   public boolean supportOutputStream() {
      return true;
   }
}
