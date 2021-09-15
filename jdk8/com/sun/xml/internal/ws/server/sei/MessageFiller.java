package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.message.ByteArrayAttachment;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.message.JAXBAttachment;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;

public abstract class MessageFiller {
   protected final int methodPos;

   protected MessageFiller(int methodPos) {
      this.methodPos = methodPos;
   }

   public abstract void fillIn(Object[] var1, Object var2, Message var3);

   private static boolean isXMLMimeType(String mimeType) {
      return mimeType.equals("text/xml") || mimeType.equals("application/xml");
   }

   public static final class Header extends MessageFiller {
      private final XMLBridge bridge;
      private final ValueGetter getter;

      public Header(int methodPos, XMLBridge bridge, ValueGetter getter) {
         super(methodPos);
         this.bridge = bridge;
         this.getter = getter;
      }

      public void fillIn(Object[] methodArgs, Object returnValue, Message msg) {
         Object value = this.methodPos == -1 ? returnValue : this.getter.get(methodArgs[this.methodPos]);
         msg.getHeaders().add(Headers.create(this.bridge, value));
      }
   }

   private static class JAXBFiller extends MessageFiller.AttachmentFiller {
      protected JAXBFiller(ParameterImpl param, ValueGetter getter) {
         super(param, getter);
      }

      public void fillIn(Object[] methodArgs, Object returnValue, Message msg) {
         String contentId = this.getContentId();
         Object obj = this.methodPos == -1 ? returnValue : this.getter.get(methodArgs[this.methodPos]);
         Attachment att = new JAXBAttachment(contentId, obj, this.param.getXMLBridge(), this.mimeType);
         msg.getAttachments().add(att);
      }
   }

   private static class DataHandlerFiller extends MessageFiller.AttachmentFiller {
      protected DataHandlerFiller(ParameterImpl param, ValueGetter getter) {
         super(param, getter);
      }

      public void fillIn(Object[] methodArgs, Object returnValue, Message msg) {
         String contentId = this.getContentId();
         Object obj = this.methodPos == -1 ? returnValue : this.getter.get(methodArgs[this.methodPos]);
         DataHandler dh = obj instanceof DataHandler ? (DataHandler)obj : new DataHandler(obj, this.mimeType);
         Attachment att = new DataHandlerAttachment(contentId, dh);
         msg.getAttachments().add(att);
      }
   }

   private static class ByteArrayFiller extends MessageFiller.AttachmentFiller {
      protected ByteArrayFiller(ParameterImpl param, ValueGetter getter) {
         super(param, getter);
      }

      public void fillIn(Object[] methodArgs, Object returnValue, Message msg) {
         String contentId = this.getContentId();
         Object obj = this.methodPos == -1 ? returnValue : this.getter.get(methodArgs[this.methodPos]);
         if (obj != null) {
            Attachment att = new ByteArrayAttachment(contentId, (byte[])((byte[])obj), this.mimeType);
            msg.getAttachments().add(att);
         }

      }
   }

   public abstract static class AttachmentFiller extends MessageFiller {
      protected final ParameterImpl param;
      protected final ValueGetter getter;
      protected final String mimeType;
      private final String contentIdPart;

      protected AttachmentFiller(ParameterImpl param, ValueGetter getter) {
         super(param.getIndex());
         this.param = param;
         this.getter = getter;
         this.mimeType = param.getBinding().getMimeType();

         try {
            this.contentIdPart = URLEncoder.encode(param.getPartName(), "UTF-8") + '=';
         } catch (UnsupportedEncodingException var4) {
            throw new WebServiceException(var4);
         }
      }

      public static MessageFiller createAttachmentFiller(ParameterImpl param, ValueGetter getter) {
         Class type = (Class)param.getTypeInfo().type;
         if (!DataHandler.class.isAssignableFrom(type) && !Source.class.isAssignableFrom(type)) {
            if (byte[].class == type) {
               return new MessageFiller.ByteArrayFiller(param, getter);
            } else {
               return (MessageFiller)(MessageFiller.isXMLMimeType(param.getBinding().getMimeType()) ? new MessageFiller.JAXBFiller(param, getter) : new MessageFiller.DataHandlerFiller(param, getter));
            }
         } else {
            return new MessageFiller.DataHandlerFiller(param, getter);
         }
      }

      String getContentId() {
         return this.contentIdPart + UUID.randomUUID() + "@jaxws.sun.com";
      }
   }
}
