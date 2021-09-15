package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final class BridgeAdapter<OnWire, InMemory> extends InternalBridge<InMemory> {
   private final InternalBridge<OnWire> core;
   private final Class<? extends XmlAdapter<OnWire, InMemory>> adapter;

   public BridgeAdapter(InternalBridge<OnWire> core, Class<? extends XmlAdapter<OnWire, InMemory>> adapter) {
      super(core.getContext());
      this.core = core;
      this.adapter = adapter;
   }

   public void marshal(Marshaller m, InMemory inMemory, XMLStreamWriter output) throws JAXBException {
      this.core.marshal(m, this.adaptM(m, inMemory), output);
   }

   public void marshal(Marshaller m, InMemory inMemory, OutputStream output, NamespaceContext nsc) throws JAXBException {
      this.core.marshal(m, this.adaptM(m, inMemory), output, nsc);
   }

   public void marshal(Marshaller m, InMemory inMemory, Node output) throws JAXBException {
      this.core.marshal(m, this.adaptM(m, inMemory), output);
   }

   public void marshal(Marshaller context, InMemory inMemory, ContentHandler contentHandler) throws JAXBException {
      this.core.marshal(context, this.adaptM(context, inMemory), contentHandler);
   }

   public void marshal(Marshaller context, InMemory inMemory, Result result) throws JAXBException {
      this.core.marshal(context, this.adaptM(context, inMemory), result);
   }

   private OnWire adaptM(Marshaller m, InMemory v) throws JAXBException {
      XMLSerializer serializer = ((MarshallerImpl)m).serializer;
      serializer.pushCoordinator();

      Object var4;
      try {
         var4 = this._adaptM(serializer, v);
      } finally {
         serializer.popCoordinator();
      }

      return var4;
   }

   private OnWire _adaptM(XMLSerializer serializer, InMemory v) throws MarshalException {
      XmlAdapter a = serializer.getAdapter(this.adapter);

      try {
         return a.marshal(v);
      } catch (Exception var5) {
         serializer.handleError(var5, v, (String)null);
         throw new MarshalException(var5);
      }
   }

   @NotNull
   public InMemory unmarshal(Unmarshaller u, XMLStreamReader in) throws JAXBException {
      return this.adaptU(u, this.core.unmarshal(u, in));
   }

   @NotNull
   public InMemory unmarshal(Unmarshaller u, Source in) throws JAXBException {
      return this.adaptU(u, this.core.unmarshal(u, in));
   }

   @NotNull
   public InMemory unmarshal(Unmarshaller u, InputStream in) throws JAXBException {
      return this.adaptU(u, this.core.unmarshal(u, in));
   }

   @NotNull
   public InMemory unmarshal(Unmarshaller u, Node n) throws JAXBException {
      return this.adaptU(u, this.core.unmarshal(u, n));
   }

   public TypeReference getTypeReference() {
      return this.core.getTypeReference();
   }

   @NotNull
   private InMemory adaptU(Unmarshaller _u, OnWire v) throws JAXBException {
      UnmarshallerImpl u = (UnmarshallerImpl)_u;
      XmlAdapter<OnWire, InMemory> a = u.coordinator.getAdapter(this.adapter);
      u.coordinator.pushCoordinator();

      Object var5;
      try {
         var5 = a.unmarshal(v);
      } catch (Exception var9) {
         throw new UnmarshalException(var9);
      } finally {
         u.coordinator.popCoordinator();
      }

      return var5;
   }

   void marshal(InMemory o, XMLSerializer out) throws IOException, SAXException, XMLStreamException {
      try {
         this.core.marshal(this._adaptM(XMLSerializer.getInstance(), o), out);
      } catch (MarshalException var4) {
      }

   }
}
