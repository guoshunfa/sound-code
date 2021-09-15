package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.v2.runtime.BridgeContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public abstract class OldBridge<T> {
   protected final JAXBContextImpl context;

   protected OldBridge(JAXBContextImpl context) {
      this.context = context;
   }

   @NotNull
   public BindingContext getContext() {
      return null;
   }

   public final void marshal(T object, XMLStreamWriter output) throws JAXBException {
      this.marshal((Object)object, (XMLStreamWriter)output, (AttachmentMarshaller)null);
   }

   public final void marshal(T object, XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
      Marshaller m = (Marshaller)this.context.marshallerPool.take();
      m.setAttachmentMarshaller(am);
      this.marshal(m, object, output);
      m.setAttachmentMarshaller((AttachmentMarshaller)null);
      this.context.marshallerPool.recycle(m);
   }

   public final void marshal(@NotNull BridgeContext context, T object, XMLStreamWriter output) throws JAXBException {
      this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, (Object)object, (XMLStreamWriter)output);
   }

   public abstract void marshal(@NotNull Marshaller var1, T var2, XMLStreamWriter var3) throws JAXBException;

   public void marshal(T object, OutputStream output, NamespaceContext nsContext) throws JAXBException {
      this.marshal((Object)object, (OutputStream)output, (NamespaceContext)nsContext, (AttachmentMarshaller)null);
   }

   public void marshal(T object, OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
      Marshaller m = (Marshaller)this.context.marshallerPool.take();
      m.setAttachmentMarshaller(am);
      this.marshal(m, object, output, nsContext);
      m.setAttachmentMarshaller((AttachmentMarshaller)null);
      this.context.marshallerPool.recycle(m);
   }

   public final void marshal(@NotNull BridgeContext context, T object, OutputStream output, NamespaceContext nsContext) throws JAXBException {
      this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, (Object)object, (OutputStream)output, (NamespaceContext)nsContext);
   }

   public abstract void marshal(@NotNull Marshaller var1, T var2, OutputStream var3, NamespaceContext var4) throws JAXBException;

   public final void marshal(T object, Node output) throws JAXBException {
      Marshaller m = (Marshaller)this.context.marshallerPool.take();
      this.marshal(m, object, output);
      this.context.marshallerPool.recycle(m);
   }

   public final void marshal(@NotNull BridgeContext context, T object, Node output) throws JAXBException {
      this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, (Object)object, (Node)output);
   }

   public abstract void marshal(@NotNull Marshaller var1, T var2, Node var3) throws JAXBException;

   public final void marshal(T object, ContentHandler contentHandler) throws JAXBException {
      this.marshal((Object)object, (ContentHandler)contentHandler, (AttachmentMarshaller)null);
   }

   public final void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
      Marshaller m = (Marshaller)this.context.marshallerPool.take();
      m.setAttachmentMarshaller(am);
      this.marshal(m, object, contentHandler);
      m.setAttachmentMarshaller((AttachmentMarshaller)null);
      this.context.marshallerPool.recycle(m);
   }

   public final void marshal(@NotNull BridgeContext context, T object, ContentHandler contentHandler) throws JAXBException {
      this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, (Object)object, (ContentHandler)contentHandler);
   }

   public abstract void marshal(@NotNull Marshaller var1, T var2, ContentHandler var3) throws JAXBException;

   public final void marshal(T object, Result result) throws JAXBException {
      Marshaller m = (Marshaller)this.context.marshallerPool.take();
      this.marshal(m, object, result);
      this.context.marshallerPool.recycle(m);
   }

   public final void marshal(@NotNull BridgeContext context, T object, Result result) throws JAXBException {
      this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, (Object)object, (Result)result);
   }

   public abstract void marshal(@NotNull Marshaller var1, T var2, Result var3) throws JAXBException;

   private T exit(T r, Unmarshaller u) {
      u.setAttachmentUnmarshaller((AttachmentUnmarshaller)null);
      this.context.unmarshallerPool.recycle(u);
      return r;
   }

   @NotNull
   public final T unmarshal(@NotNull XMLStreamReader in) throws JAXBException {
      return this.unmarshal((XMLStreamReader)in, (AttachmentUnmarshaller)null);
   }

   @NotNull
   public final T unmarshal(@NotNull XMLStreamReader in, @Nullable AttachmentUnmarshaller au) throws JAXBException {
      Unmarshaller u = (Unmarshaller)this.context.unmarshallerPool.take();
      u.setAttachmentUnmarshaller(au);
      return this.exit(this.unmarshal(u, in), u);
   }

   @NotNull
   public final T unmarshal(@NotNull BridgeContext context, @NotNull XMLStreamReader in) throws JAXBException {
      return this.unmarshal((Unmarshaller)((BridgeContextImpl)context).unmarshaller, (XMLStreamReader)in);
   }

   @NotNull
   public abstract T unmarshal(@NotNull Unmarshaller var1, @NotNull XMLStreamReader var2) throws JAXBException;

   @NotNull
   public final T unmarshal(@NotNull Source in) throws JAXBException {
      return this.unmarshal((Source)in, (AttachmentUnmarshaller)null);
   }

   @NotNull
   public final T unmarshal(@NotNull Source in, @Nullable AttachmentUnmarshaller au) throws JAXBException {
      Unmarshaller u = (Unmarshaller)this.context.unmarshallerPool.take();
      u.setAttachmentUnmarshaller(au);
      return this.exit(this.unmarshal(u, in), u);
   }

   @NotNull
   public final T unmarshal(@NotNull BridgeContext context, @NotNull Source in) throws JAXBException {
      return this.unmarshal((Unmarshaller)((BridgeContextImpl)context).unmarshaller, (Source)in);
   }

   @NotNull
   public abstract T unmarshal(@NotNull Unmarshaller var1, @NotNull Source var2) throws JAXBException;

   @NotNull
   public final T unmarshal(@NotNull InputStream in) throws JAXBException {
      Unmarshaller u = (Unmarshaller)this.context.unmarshallerPool.take();
      return this.exit(this.unmarshal(u, in), u);
   }

   @NotNull
   public final T unmarshal(@NotNull BridgeContext context, @NotNull InputStream in) throws JAXBException {
      return this.unmarshal((Unmarshaller)((BridgeContextImpl)context).unmarshaller, (InputStream)in);
   }

   @NotNull
   public abstract T unmarshal(@NotNull Unmarshaller var1, @NotNull InputStream var2) throws JAXBException;

   @NotNull
   public final T unmarshal(@NotNull Node n) throws JAXBException {
      return this.unmarshal((Node)n, (AttachmentUnmarshaller)null);
   }

   @NotNull
   public final T unmarshal(@NotNull Node n, @Nullable AttachmentUnmarshaller au) throws JAXBException {
      Unmarshaller u = (Unmarshaller)this.context.unmarshallerPool.take();
      u.setAttachmentUnmarshaller(au);
      return this.exit(this.unmarshal(u, n), u);
   }

   @NotNull
   public final T unmarshal(@NotNull BridgeContext context, @NotNull Node n) throws JAXBException {
      return this.unmarshal((Unmarshaller)((BridgeContextImpl)context).unmarshaller, (Node)n);
   }

   @NotNull
   public abstract T unmarshal(@NotNull Unmarshaller var1, @NotNull Node var2) throws JAXBException;

   public abstract TypeInfo getTypeReference();
}
