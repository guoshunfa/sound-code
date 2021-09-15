package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
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

public interface XMLBridge<T> {
   @NotNull
   BindingContext context();

   void marshal(T var1, XMLStreamWriter var2, AttachmentMarshaller var3) throws JAXBException;

   void marshal(T var1, OutputStream var2, NamespaceContext var3, AttachmentMarshaller var4) throws JAXBException;

   void marshal(T var1, Node var2) throws JAXBException;

   void marshal(T var1, ContentHandler var2, AttachmentMarshaller var3) throws JAXBException;

   void marshal(T var1, Result var2) throws JAXBException;

   @NotNull
   T unmarshal(@NotNull XMLStreamReader var1, @Nullable AttachmentUnmarshaller var2) throws JAXBException;

   @NotNull
   T unmarshal(@NotNull Source var1, @Nullable AttachmentUnmarshaller var2) throws JAXBException;

   @NotNull
   T unmarshal(@NotNull InputStream var1) throws JAXBException;

   @NotNull
   T unmarshal(@NotNull Node var1, @Nullable AttachmentUnmarshaller var2) throws JAXBException;

   TypeInfo getTypeInfo();

   boolean supportOutputStream();
}
