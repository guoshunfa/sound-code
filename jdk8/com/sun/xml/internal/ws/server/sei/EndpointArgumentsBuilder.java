package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.encoding.DataHandlerDataSource;
import com.sun.xml.internal.ws.encoding.StringDataContentHandler;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.jws.WebParam;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class EndpointArgumentsBuilder {
   public static final EndpointArgumentsBuilder NONE = new EndpointArgumentsBuilder.None();
   private static final Map<Class, Object> primitiveUninitializedValues = new HashMap();
   protected QName wrapperName;
   protected Map<QName, EndpointArgumentsBuilder.WrappedPartBuilder> wrappedParts = null;

   public abstract void readRequest(Message var1, Object[] var2) throws JAXBException, XMLStreamException;

   public static Object getVMUninitializedValue(Type type) {
      return primitiveUninitializedValues.get(type);
   }

   protected void readWrappedRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
      if (!msg.hasPayload()) {
         throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
      } else {
         XMLStreamReader reader = msg.readPayload();
         XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
         reader.nextTag();

         QName name;
         for(; reader.getEventType() == 1; XMLStreamReaderUtil.toNextTag(reader, name)) {
            name = reader.getName();
            EndpointArgumentsBuilder.WrappedPartBuilder part = (EndpointArgumentsBuilder.WrappedPartBuilder)this.wrappedParts.get(name);
            if (part == null) {
               XMLStreamReaderUtil.skipElement(reader);
               reader.nextTag();
            } else {
               part.readRequest(args, reader, msg.getAttachments());
            }
         }

         reader.close();
         XMLStreamReaderFactory.recycle(reader);
      }
   }

   public static final String getWSDLPartName(Attachment att) {
      String cId = att.getContentId();
      int index = cId.lastIndexOf(64, cId.length());
      if (index == -1) {
         return null;
      } else {
         String localPart = cId.substring(0, index);
         index = localPart.lastIndexOf(61, localPart.length());
         if (index == -1) {
            return null;
         } else {
            try {
               return URLDecoder.decode(localPart.substring(0, index), "UTF-8");
            } catch (UnsupportedEncodingException var5) {
               throw new WebServiceException(var5);
            }
         }
      }
   }

   private static boolean isXMLMimeType(String mimeType) {
      return mimeType.equals("text/xml") || mimeType.equals("application/xml");
   }

   static {
      Map<Class, Object> m = primitiveUninitializedValues;
      m.put(Integer.TYPE, 0);
      m.put(Character.TYPE, '\u0000');
      m.put(Byte.TYPE, (byte)0);
      m.put(Short.TYPE, Short.valueOf((short)0));
      m.put(Long.TYPE, 0L);
      m.put(Float.TYPE, 0.0F);
      m.put(Double.TYPE, 0.0D);
   }

   public static final class RpcLit extends EndpointArgumentsBuilder {
      public RpcLit(WrapperParameter wp) {
         assert wp.getTypeInfo().type == WrapperComposite.class;

         this.wrapperName = wp.getName();
         this.wrappedParts = new HashMap();
         List<ParameterImpl> children = wp.getWrapperChildren();
         Iterator var3 = children.iterator();

         ParameterImpl p;
         do {
            if (!var3.hasNext()) {
               return;
            }

            p = (ParameterImpl)var3.next();
            this.wrappedParts.put(p.getName(), new EndpointArgumentsBuilder.WrappedPartBuilder(p.getXMLBridge(), EndpointValueSetter.get(p)));
         } while($assertionsDisabled || p.getBinding() == ParameterBinding.BODY);

         throw new AssertionError();
      }

      public void readRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
         this.readWrappedRequest(msg, args);
      }
   }

   public static final class DocLit extends EndpointArgumentsBuilder {
      private final EndpointArgumentsBuilder.DocLit.PartBuilder[] parts;
      private final XMLBridge wrapper;
      private boolean dynamicWrapper;

      public DocLit(WrapperParameter wp, WebParam.Mode skipMode) {
         this.wrapperName = wp.getName();
         this.wrapper = wp.getXMLBridge();
         Class wrapperType = (Class)this.wrapper.getTypeInfo().type;
         this.dynamicWrapper = WrapperComposite.class.equals(wrapperType);
         List<EndpointArgumentsBuilder.DocLit.PartBuilder> parts = new ArrayList();
         List<ParameterImpl> children = wp.getWrapperChildren();
         Iterator var6 = children.iterator();

         while(var6.hasNext()) {
            ParameterImpl p = (ParameterImpl)var6.next();
            if (p.getMode() != skipMode) {
               QName name = p.getName();

               try {
                  if (this.dynamicWrapper) {
                     if (this.wrappedParts == null) {
                        this.wrappedParts = new HashMap();
                     }

                     XMLBridge xmlBridge = p.getInlinedRepeatedElementBridge();
                     if (xmlBridge == null) {
                        xmlBridge = p.getXMLBridge();
                     }

                     this.wrappedParts.put(p.getName(), new EndpointArgumentsBuilder.WrappedPartBuilder(xmlBridge, EndpointValueSetter.get(p)));
                  } else {
                     parts.add(new EndpointArgumentsBuilder.DocLit.PartBuilder(wp.getOwner().getBindingContext().getElementPropertyAccessor(wrapperType, name.getNamespaceURI(), p.getName().getLocalPart()), EndpointValueSetter.get(p)));

                     assert p.getBinding() == ParameterBinding.BODY;
                  }
               } catch (JAXBException var10) {
                  throw new WebServiceException(wrapperType + " do not have a property of the name " + name, var10);
               }
            }
         }

         this.parts = (EndpointArgumentsBuilder.DocLit.PartBuilder[])parts.toArray(new EndpointArgumentsBuilder.DocLit.PartBuilder[parts.size()]);
      }

      public void readRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
         if (this.dynamicWrapper) {
            this.readWrappedRequest(msg, args);
         } else if (this.parts.length > 0) {
            if (!msg.hasPayload()) {
               throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
            }

            XMLStreamReader reader = msg.readPayload();
            XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
            Object wrapperBean = this.wrapper.unmarshal((XMLStreamReader)reader, msg.getAttachments() != null ? new AttachmentUnmarshallerImpl(msg.getAttachments()) : null);

            try {
               EndpointArgumentsBuilder.DocLit.PartBuilder[] var5 = this.parts;
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  EndpointArgumentsBuilder.DocLit.PartBuilder part = var5[var7];
                  part.readRequest(args, wrapperBean);
               }
            } catch (DatabindingException var9) {
               throw new WebServiceException(var9);
            }

            reader.close();
            XMLStreamReaderFactory.recycle(reader);
         } else {
            msg.consume();
         }

      }

      static final class PartBuilder {
         private final PropertyAccessor accessor;
         private final EndpointValueSetter setter;

         public PartBuilder(PropertyAccessor accessor, EndpointValueSetter setter) {
            this.accessor = accessor;
            this.setter = setter;

            assert accessor != null && setter != null;
         }

         final void readRequest(Object[] args, Object wrapperBean) {
            Object obj = this.accessor.get(wrapperBean);
            this.setter.put(obj, args);
         }
      }
   }

   public static final class Body extends EndpointArgumentsBuilder {
      private final XMLBridge<?> bridge;
      private final EndpointValueSetter setter;

      public Body(XMLBridge<?> bridge, EndpointValueSetter setter) {
         this.bridge = bridge;
         this.setter = setter;
      }

      public void readRequest(Message msg, Object[] args) throws JAXBException {
         this.setter.put(msg.readPayloadAsJAXB(this.bridge), args);
      }
   }

   public static final class Header extends EndpointArgumentsBuilder {
      private final XMLBridge<?> bridge;
      private final EndpointValueSetter setter;
      private final QName headerName;
      private final SOAPVersion soapVersion;

      public Header(SOAPVersion soapVersion, QName name, XMLBridge<?> bridge, EndpointValueSetter setter) {
         this.soapVersion = soapVersion;
         this.headerName = name;
         this.bridge = bridge;
         this.setter = setter;
      }

      public Header(SOAPVersion soapVersion, ParameterImpl param, EndpointValueSetter setter) {
         this(soapVersion, param.getTypeInfo().tagName, param.getXMLBridge(), setter);

         assert param.getOutBinding() == ParameterBinding.HEADER;

      }

      private SOAPFaultException createDuplicateHeaderException() {
         try {
            SOAPFault fault = this.soapVersion.getSOAPFactory().createFault();
            fault.setFaultCode(this.soapVersion.faultCodeClient);
            fault.setFaultString(ServerMessages.DUPLICATE_PORT_KNOWN_HEADER(this.headerName));
            return new SOAPFaultException(fault);
         } catch (SOAPException var2) {
            throw new WebServiceException(var2);
         }
      }

      public void readRequest(Message msg, Object[] args) throws JAXBException {
         com.sun.xml.internal.ws.api.message.Header header = null;
         Iterator<com.sun.xml.internal.ws.api.message.Header> it = msg.getHeaders().getHeaders(this.headerName, true);
         if (it.hasNext()) {
            header = (com.sun.xml.internal.ws.api.message.Header)it.next();
            if (it.hasNext()) {
               throw this.createDuplicateHeaderException();
            }
         }

         if (header != null) {
            this.setter.put(header.readAsJAXB(this.bridge), args);
         }

      }
   }

   private static final class StringBuilder extends EndpointArgumentsBuilder.AttachmentBuilder {
      StringBuilder(ParameterImpl param, EndpointValueSetter setter) {
         super(param, setter);
      }

      void mapAttachment(Attachment att, Object[] args) {
         att.getContentType();
         StringDataContentHandler sdh = new StringDataContentHandler();

         try {
            String str = (String)sdh.getContent(new DataHandlerDataSource(att.asDataHandler()));
            this.setter.put(str, args);
         } catch (Exception var5) {
            throw new WebServiceException(var5);
         }
      }
   }

   private static final class JAXBBuilder extends EndpointArgumentsBuilder.AttachmentBuilder {
      JAXBBuilder(ParameterImpl param, EndpointValueSetter setter) {
         super(param, setter);
      }

      void mapAttachment(Attachment att, Object[] args) throws JAXBException {
         Object obj = this.param.getXMLBridge().unmarshal(att.asInputStream());
         this.setter.put(obj, args);
      }
   }

   private static final class InputStreamBuilder extends EndpointArgumentsBuilder.AttachmentBuilder {
      InputStreamBuilder(ParameterImpl param, EndpointValueSetter setter) {
         super(param, setter);
      }

      void mapAttachment(Attachment att, Object[] args) {
         this.setter.put(att.asInputStream(), args);
      }
   }

   private static final class ImageBuilder extends EndpointArgumentsBuilder.AttachmentBuilder {
      ImageBuilder(ParameterImpl param, EndpointValueSetter setter) {
         super(param, setter);
      }

      void mapAttachment(Attachment att, Object[] args) {
         InputStream is = null;

         BufferedImage image;
         try {
            is = att.asInputStream();
            image = ImageIO.read(is);
         } catch (IOException var13) {
            throw new WebServiceException(var13);
         } finally {
            if (is != null) {
               try {
                  is.close();
               } catch (IOException var12) {
                  throw new WebServiceException(var12);
               }
            }

         }

         this.setter.put(image, args);
      }
   }

   private static final class SourceBuilder extends EndpointArgumentsBuilder.AttachmentBuilder {
      SourceBuilder(ParameterImpl param, EndpointValueSetter setter) {
         super(param, setter);
      }

      void mapAttachment(Attachment att, Object[] args) {
         this.setter.put(att.asSource(), args);
      }
   }

   private static final class ByteArrayBuilder extends EndpointArgumentsBuilder.AttachmentBuilder {
      ByteArrayBuilder(ParameterImpl param, EndpointValueSetter setter) {
         super(param, setter);
      }

      void mapAttachment(Attachment att, Object[] args) {
         this.setter.put(att.asByteArray(), args);
      }
   }

   private static final class DataHandlerBuilder extends EndpointArgumentsBuilder.AttachmentBuilder {
      DataHandlerBuilder(ParameterImpl param, EndpointValueSetter setter) {
         super(param, setter);
      }

      void mapAttachment(Attachment att, Object[] args) {
         this.setter.put(att.asDataHandler(), args);
      }
   }

   public abstract static class AttachmentBuilder extends EndpointArgumentsBuilder {
      protected final EndpointValueSetter setter;
      protected final ParameterImpl param;
      protected final String pname;
      protected final String pname1;

      AttachmentBuilder(ParameterImpl param, EndpointValueSetter setter) {
         this.setter = setter;
         this.param = param;
         this.pname = param.getPartName();
         this.pname1 = "<" + this.pname;
      }

      public static EndpointArgumentsBuilder createAttachmentBuilder(ParameterImpl param, EndpointValueSetter setter) {
         Class type = (Class)param.getTypeInfo().type;
         if (DataHandler.class.isAssignableFrom(type)) {
            return new EndpointArgumentsBuilder.DataHandlerBuilder(param, setter);
         } else if (byte[].class == type) {
            return new EndpointArgumentsBuilder.ByteArrayBuilder(param, setter);
         } else if (Source.class.isAssignableFrom(type)) {
            return new EndpointArgumentsBuilder.SourceBuilder(param, setter);
         } else if (Image.class.isAssignableFrom(type)) {
            return new EndpointArgumentsBuilder.ImageBuilder(param, setter);
         } else if (InputStream.class == type) {
            return new EndpointArgumentsBuilder.InputStreamBuilder(param, setter);
         } else if (EndpointArgumentsBuilder.isXMLMimeType(param.getBinding().getMimeType())) {
            return new EndpointArgumentsBuilder.JAXBBuilder(param, setter);
         } else if (String.class.isAssignableFrom(type)) {
            return new EndpointArgumentsBuilder.StringBuilder(param, setter);
         } else {
            throw new UnsupportedOperationException("Unknown Type=" + type + " Attachment is not mapped.");
         }
      }

      public void readRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
         boolean foundAttachment = false;
         Iterator var4 = msg.getAttachments().iterator();

         while(var4.hasNext()) {
            Attachment att = (Attachment)var4.next();
            String part = getWSDLPartName(att);
            if (part != null && (part.equals(this.pname) || part.equals(this.pname1))) {
               foundAttachment = true;
               this.mapAttachment(att, args);
               break;
            }
         }

         if (!foundAttachment) {
            throw new WebServiceException("Missing Attachment for " + this.pname);
         }
      }

      abstract void mapAttachment(Attachment var1, Object[] var2) throws JAXBException;
   }

   public static final class Composite extends EndpointArgumentsBuilder {
      private final EndpointArgumentsBuilder[] builders;

      public Composite(EndpointArgumentsBuilder... builders) {
         this.builders = builders;
      }

      public Composite(Collection<? extends EndpointArgumentsBuilder> builders) {
         this((EndpointArgumentsBuilder[])builders.toArray(new EndpointArgumentsBuilder[builders.size()]));
      }

      public void readRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
         EndpointArgumentsBuilder[] var3 = this.builders;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EndpointArgumentsBuilder builder = var3[var5];
            builder.readRequest(msg, args);
         }

      }
   }

   public static final class NullSetter extends EndpointArgumentsBuilder {
      private final EndpointValueSetter setter;
      private final Object nullValue;

      public NullSetter(EndpointValueSetter setter, Object nullValue) {
         assert setter != null;

         this.nullValue = nullValue;
         this.setter = setter;
      }

      public void readRequest(Message msg, Object[] args) {
         this.setter.put(this.nullValue, args);
      }
   }

   static final class WrappedPartBuilder {
      private final XMLBridge bridge;
      private final EndpointValueSetter setter;

      public WrappedPartBuilder(XMLBridge bridge, EndpointValueSetter setter) {
         this.bridge = bridge;
         this.setter = setter;
      }

      void readRequest(Object[] args, XMLStreamReader r, AttachmentSet att) throws JAXBException {
         Object obj = null;
         AttachmentUnmarshallerImpl au = att != null ? new AttachmentUnmarshallerImpl(att) : null;
         if (this.bridge instanceof RepeatedElementBridge) {
            RepeatedElementBridge rbridge = (RepeatedElementBridge)this.bridge;
            ArrayList list = new ArrayList();
            QName name = r.getName();

            while(r.getEventType() == 1 && name.equals(r.getName())) {
               list.add(rbridge.unmarshal((XMLStreamReader)r, au));
               XMLStreamReaderUtil.toNextTag(r, name);
            }

            obj = rbridge.collectionHandler().convert(list);
         } else {
            obj = this.bridge.unmarshal((XMLStreamReader)r, au);
         }

         this.setter.put(obj, args);
      }
   }

   static final class None extends EndpointArgumentsBuilder {
      private None() {
      }

      public void readRequest(Message msg, Object[] args) {
         msg.consume();
      }

      // $FF: synthetic method
      None(Object x0) {
         this();
      }
   }
}
