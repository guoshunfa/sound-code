package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class WrapperBridge<T> implements XMLBridge<T> {
   private JAXBRIContextWrapper parent;
   private Bridge<T> bridge;

   public WrapperBridge(JAXBRIContextWrapper p, Bridge<T> b) {
      this.parent = p;
      this.bridge = b;
   }

   public BindingContext context() {
      return this.parent;
   }

   public boolean equals(Object obj) {
      return this.bridge.equals(obj);
   }

   public TypeInfo getTypeInfo() {
      return this.parent.typeInfo(this.bridge.getTypeReference());
   }

   public int hashCode() {
      return this.bridge.hashCode();
   }

   static CompositeStructure convert(Object o) {
      WrapperComposite w = (WrapperComposite)o;
      CompositeStructure cs = new CompositeStructure();
      cs.values = w.values;
      cs.bridges = new Bridge[w.bridges.length];

      for(int i = 0; i < cs.bridges.length; ++i) {
         cs.bridges[i] = ((BridgeWrapper)w.bridges[i]).getBridge();
      }

      return cs;
   }

   public final void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
      this.bridge.marshal((Object)convert(object), (ContentHandler)contentHandler, (AttachmentMarshaller)am);
   }

   public void marshal(T object, Node output) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public void marshal(T object, OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
      this.bridge.marshal((Object)convert(object), (OutputStream)output, (NamespaceContext)nsContext, (AttachmentMarshaller)am);
   }

   public final void marshal(T object, Result result) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   public final void marshal(T object, XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
      this.bridge.marshal((Object)convert(object), (XMLStreamWriter)output, (AttachmentMarshaller)am);
   }

   public String toString() {
      return BridgeWrapper.class.getName() + " : " + this.bridge.toString();
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
      return true;
   }
}
