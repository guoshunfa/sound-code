package com.sun.xml.internal.ws.client.sei;

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
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class ResponseBuilder {
   protected Map<QName, ResponseBuilder.WrappedPartBuilder> wrappedParts = null;
   protected QName wrapperName;
   public static final ResponseBuilder NONE = new ResponseBuilder.None();
   private static final Map<Class, Object> primitiveUninitializedValues = new HashMap();

   public abstract Object readResponse(Message var1, Object[] var2) throws JAXBException, XMLStreamException;

   protected Object readWrappedResponse(Message msg, Object[] args) throws JAXBException, XMLStreamException {
      Object retVal = null;
      if (!msg.hasPayload()) {
         throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
      } else {
         XMLStreamReader reader = msg.readPayload();
         XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
         reader.nextTag();

         while(reader.getEventType() == 1) {
            ResponseBuilder.WrappedPartBuilder part = (ResponseBuilder.WrappedPartBuilder)this.wrappedParts.get(reader.getName());
            if (part == null) {
               XMLStreamReaderUtil.skipElement(reader);
               reader.nextTag();
            } else {
               Object o = part.readResponse(args, reader, msg.getAttachments());
               if (o != null) {
                  assert retVal == null;

                  retVal = o;
               }
            }

            if (reader.getEventType() != 1 && reader.getEventType() != 2) {
               XMLStreamReaderUtil.nextElementContent(reader);
            }
         }

         reader.close();
         XMLStreamReaderFactory.recycle(reader);
         return retVal;
      }
   }

   public static Object getVMUninitializedValue(Type type) {
      return primitiveUninitializedValues.get(type);
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

   public static final class RpcLit extends ResponseBuilder {
      public RpcLit(WrapperParameter wp, ValueSetterFactory setterFactory) {
         assert wp.getTypeInfo().type == WrapperComposite.class;

         this.wrapperName = wp.getName();
         this.wrappedParts = new HashMap();
         List<ParameterImpl> children = wp.getWrapperChildren();
         Iterator var4 = children.iterator();

         ParameterImpl p;
         do {
            if (!var4.hasNext()) {
               return;
            }

            p = (ParameterImpl)var4.next();
            this.wrappedParts.put(p.getName(), new ResponseBuilder.WrappedPartBuilder(p.getXMLBridge(), setterFactory.get(p)));
         } while($assertionsDisabled || p.getBinding() == ParameterBinding.BODY);

         throw new AssertionError();
      }

      public Object readResponse(Message msg, Object[] args) throws JAXBException, XMLStreamException {
         return this.readWrappedResponse(msg, args);
      }
   }

   public static final class DocLit extends ResponseBuilder {
      private final ResponseBuilder.DocLit.PartBuilder[] parts;
      private final XMLBridge wrapper;
      private boolean dynamicWrapper;

      public DocLit(WrapperParameter wp, ValueSetterFactory setterFactory) {
         this.wrapperName = wp.getName();
         this.wrapper = wp.getXMLBridge();
         Class wrapperType = (Class)this.wrapper.getTypeInfo().type;
         this.dynamicWrapper = WrapperComposite.class.equals(wrapperType);
         List<ResponseBuilder.DocLit.PartBuilder> tempParts = new ArrayList();
         List<ParameterImpl> children = wp.getWrapperChildren();
         Iterator var6 = children.iterator();

         while(var6.hasNext()) {
            ParameterImpl p = (ParameterImpl)var6.next();
            if (!p.isIN()) {
               QName name = p.getName();
               if (this.dynamicWrapper) {
                  if (this.wrappedParts == null) {
                     this.wrappedParts = new HashMap();
                  }

                  XMLBridge xmlBridge = p.getInlinedRepeatedElementBridge();
                  if (xmlBridge == null) {
                     xmlBridge = p.getXMLBridge();
                  }

                  this.wrappedParts.put(p.getName(), new ResponseBuilder.WrappedPartBuilder(xmlBridge, setterFactory.get(p)));
               } else {
                  try {
                     tempParts.add(new ResponseBuilder.DocLit.PartBuilder(wp.getOwner().getBindingContext().getElementPropertyAccessor(wrapperType, name.getNamespaceURI(), p.getName().getLocalPart()), setterFactory.get(p)));

                     assert p.getBinding() == ParameterBinding.BODY;
                  } catch (JAXBException var10) {
                     throw new WebServiceException(wrapperType + " do not have a property of the name " + name, var10);
                  }
               }
            }
         }

         this.parts = (ResponseBuilder.DocLit.PartBuilder[])tempParts.toArray(new ResponseBuilder.DocLit.PartBuilder[tempParts.size()]);
      }

      public Object readResponse(Message msg, Object[] args) throws JAXBException, XMLStreamException {
         if (this.dynamicWrapper) {
            return this.readWrappedResponse(msg, args);
         } else {
            Object retVal = null;
            if (this.parts.length > 0) {
               if (!msg.hasPayload()) {
                  throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
               }

               XMLStreamReader reader = msg.readPayload();
               XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
               Object wrapperBean = this.wrapper.unmarshal((XMLStreamReader)reader, msg.getAttachments() != null ? new AttachmentUnmarshallerImpl(msg.getAttachments()) : null);

               try {
                  ResponseBuilder.DocLit.PartBuilder[] var6 = this.parts;
                  int var7 = var6.length;

                  for(int var8 = 0; var8 < var7; ++var8) {
                     ResponseBuilder.DocLit.PartBuilder part = var6[var8];
                     Object o = part.readResponse(args, wrapperBean);
                     if (o != null) {
                        assert retVal == null;

                        retVal = o;
                     }
                  }
               } catch (DatabindingException var11) {
                  throw new WebServiceException(var11);
               }

               reader.close();
               XMLStreamReaderFactory.recycle(reader);
            } else {
               msg.consume();
            }

            return retVal;
         }
      }

      static final class PartBuilder {
         private final PropertyAccessor accessor;
         private final ValueSetter setter;

         public PartBuilder(PropertyAccessor accessor, ValueSetter setter) {
            this.accessor = accessor;
            this.setter = setter;

            assert accessor != null && setter != null;
         }

         final Object readResponse(Object[] args, Object wrapperBean) {
            Object obj = this.accessor.get(wrapperBean);
            return this.setter.put(obj, args);
         }
      }
   }

   public static final class Body extends ResponseBuilder {
      private final XMLBridge<?> bridge;
      private final ValueSetter setter;

      public Body(XMLBridge<?> bridge, ValueSetter setter) {
         this.bridge = bridge;
         this.setter = setter;
      }

      public Object readResponse(Message msg, Object[] args) throws JAXBException {
         return this.setter.put(msg.readPayloadAsJAXB(this.bridge), args);
      }
   }

   public static final class Header extends ResponseBuilder {
      private final XMLBridge<?> bridge;
      private final ValueSetter setter;
      private final QName headerName;
      private final SOAPVersion soapVersion;

      public Header(SOAPVersion soapVersion, QName name, XMLBridge<?> bridge, ValueSetter setter) {
         this.soapVersion = soapVersion;
         this.headerName = name;
         this.bridge = bridge;
         this.setter = setter;
      }

      public Header(SOAPVersion soapVersion, ParameterImpl param, ValueSetter setter) {
         this(soapVersion, param.getTypeInfo().tagName, param.getXMLBridge(), setter);

         assert param.getOutBinding() == ParameterBinding.HEADER;

      }

      private SOAPFaultException createDuplicateHeaderException() {
         try {
            SOAPFault fault = this.soapVersion.getSOAPFactory().createFault();
            fault.setFaultCode(this.soapVersion.faultCodeServer);
            fault.setFaultString(ServerMessages.DUPLICATE_PORT_KNOWN_HEADER(this.headerName));
            return new SOAPFaultException(fault);
         } catch (SOAPException var2) {
            throw new WebServiceException(var2);
         }
      }

      public Object readResponse(Message msg, Object[] args) throws JAXBException {
         com.sun.xml.internal.ws.api.message.Header header = null;
         Iterator<com.sun.xml.internal.ws.api.message.Header> it = msg.getHeaders().getHeaders(this.headerName, true);
         if (it.hasNext()) {
            header = (com.sun.xml.internal.ws.api.message.Header)it.next();
            if (it.hasNext()) {
               throw this.createDuplicateHeaderException();
            }
         }

         return header != null ? this.setter.put(header.readAsJAXB(this.bridge), args) : null;
      }
   }

   private static final class JAXBBuilder extends ResponseBuilder.AttachmentBuilder {
      JAXBBuilder(ParameterImpl param, ValueSetter setter) {
         super(param, setter);
      }

      Object mapAttachment(Attachment att, Object[] args) throws JAXBException {
         Object obj = this.param.getXMLBridge().unmarshal(att.asInputStream());
         return this.setter.put(obj, args);
      }
   }

   private static final class InputStreamBuilder extends ResponseBuilder.AttachmentBuilder {
      InputStreamBuilder(ParameterImpl param, ValueSetter setter) {
         super(param, setter);
      }

      Object mapAttachment(Attachment att, Object[] args) {
         return this.setter.put(att.asInputStream(), args);
      }
   }

   private static final class ImageBuilder extends ResponseBuilder.AttachmentBuilder {
      ImageBuilder(ParameterImpl param, ValueSetter setter) {
         super(param, setter);
      }

      Object mapAttachment(Attachment att, Object[] args) {
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

         return this.setter.put(image, args);
      }
   }

   private static final class SourceBuilder extends ResponseBuilder.AttachmentBuilder {
      SourceBuilder(ParameterImpl param, ValueSetter setter) {
         super(param, setter);
      }

      Object mapAttachment(Attachment att, Object[] args) {
         return this.setter.put(att.asSource(), args);
      }
   }

   private static final class ByteArrayBuilder extends ResponseBuilder.AttachmentBuilder {
      ByteArrayBuilder(ParameterImpl param, ValueSetter setter) {
         super(param, setter);
      }

      Object mapAttachment(Attachment att, Object[] args) {
         return this.setter.put(att.asByteArray(), args);
      }
   }

   private static final class StringBuilder extends ResponseBuilder.AttachmentBuilder {
      StringBuilder(ParameterImpl param, ValueSetter setter) {
         super(param, setter);
      }

      Object mapAttachment(Attachment att, Object[] args) {
         att.getContentType();
         StringDataContentHandler sdh = new StringDataContentHandler();

         try {
            String str = (String)sdh.getContent(new DataHandlerDataSource(att.asDataHandler()));
            return this.setter.put(str, args);
         } catch (Exception var5) {
            throw new WebServiceException(var5);
         }
      }
   }

   private static final class DataHandlerBuilder extends ResponseBuilder.AttachmentBuilder {
      DataHandlerBuilder(ParameterImpl param, ValueSetter setter) {
         super(param, setter);
      }

      Object mapAttachment(Attachment att, Object[] args) {
         return this.setter.put(att.asDataHandler(), args);
      }
   }

   public abstract static class AttachmentBuilder extends ResponseBuilder {
      protected final ValueSetter setter;
      protected final ParameterImpl param;
      private final String pname;
      private final String pname1;

      AttachmentBuilder(ParameterImpl param, ValueSetter setter) {
         this.setter = setter;
         this.param = param;
         this.pname = param.getPartName();
         this.pname1 = "<" + this.pname;
      }

      public static ResponseBuilder createAttachmentBuilder(ParameterImpl param, ValueSetter setter) {
         Class type = (Class)param.getTypeInfo().type;
         if (DataHandler.class.isAssignableFrom(type)) {
            return new ResponseBuilder.DataHandlerBuilder(param, setter);
         } else if (byte[].class == type) {
            return new ResponseBuilder.ByteArrayBuilder(param, setter);
         } else if (Source.class.isAssignableFrom(type)) {
            return new ResponseBuilder.SourceBuilder(param, setter);
         } else if (Image.class.isAssignableFrom(type)) {
            return new ResponseBuilder.ImageBuilder(param, setter);
         } else if (InputStream.class == type) {
            return new ResponseBuilder.InputStreamBuilder(param, setter);
         } else if (ResponseBuilder.isXMLMimeType(param.getBinding().getMimeType())) {
            return new ResponseBuilder.JAXBBuilder(param, setter);
         } else if (String.class.isAssignableFrom(type)) {
            return new ResponseBuilder.StringBuilder(param, setter);
         } else {
            throw new UnsupportedOperationException("Unexpected Attachment type =" + type);
         }
      }

      public Object readResponse(Message msg, Object[] args) throws JAXBException, XMLStreamException {
         Iterator var3 = msg.getAttachments().iterator();

         Attachment att;
         String part;
         do {
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               att = (Attachment)var3.next();
               part = getWSDLPartName(att);
            } while(part == null);
         } while(!part.equals(this.pname) && !part.equals(this.pname1));

         return this.mapAttachment(att, args);
      }

      abstract Object mapAttachment(Attachment var1, Object[] var2) throws JAXBException;
   }

   public static final class Composite extends ResponseBuilder {
      private final ResponseBuilder[] builders;

      public Composite(ResponseBuilder... builders) {
         this.builders = builders;
      }

      public Composite(Collection<? extends ResponseBuilder> builders) {
         this((ResponseBuilder[])builders.toArray(new ResponseBuilder[builders.size()]));
      }

      public Object readResponse(Message msg, Object[] args) throws JAXBException, XMLStreamException {
         Object retVal = null;
         ResponseBuilder[] var4 = this.builders;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ResponseBuilder builder = var4[var6];
            Object r = builder.readResponse(msg, args);
            if (r != null) {
               assert retVal == null;

               retVal = r;
            }
         }

         return retVal;
      }
   }

   public static final class NullSetter extends ResponseBuilder {
      private final ValueSetter setter;
      private final Object nullValue;

      public NullSetter(ValueSetter setter, Object nullValue) {
         assert setter != null;

         this.nullValue = nullValue;
         this.setter = setter;
      }

      public Object readResponse(Message msg, Object[] args) {
         return this.setter.put(this.nullValue, args);
      }
   }

   static final class None extends ResponseBuilder {
      private None() {
      }

      public Object readResponse(Message msg, Object[] args) {
         msg.consume();
         return null;
      }

      // $FF: synthetic method
      None(Object x0) {
         this();
      }
   }

   static final class WrappedPartBuilder {
      private final XMLBridge bridge;
      private final ValueSetter setter;

      public WrappedPartBuilder(XMLBridge bridge, ValueSetter setter) {
         this.bridge = bridge;
         this.setter = setter;
      }

      final Object readResponse(Object[] args, XMLStreamReader r, AttachmentSet att) throws JAXBException {
         AttachmentUnmarshallerImpl au = att != null ? new AttachmentUnmarshallerImpl(att) : null;
         Object obj;
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

         return this.setter.put(obj, args);
      }
   }
}
