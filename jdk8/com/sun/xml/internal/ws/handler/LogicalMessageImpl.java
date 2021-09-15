package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.DOMMessage;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class LogicalMessageImpl implements LogicalMessage {
   private Packet packet;
   protected BindingContext defaultJaxbContext;
   private LogicalMessageImpl.ImmutableLM lm = null;

   public LogicalMessageImpl(BindingContext defaultJaxbContext, Packet packet) {
      this.packet = packet;
      this.defaultJaxbContext = defaultJaxbContext;
   }

   public Source getPayload() {
      if (this.lm == null) {
         Source payload = this.packet.getMessage().copy().readPayloadAsSource();
         if (payload instanceof DOMSource) {
            this.lm = this.createLogicalMessageImpl(payload);
         }

         return payload;
      } else {
         return this.lm.getPayload();
      }
   }

   public void setPayload(Source payload) {
      this.lm = this.createLogicalMessageImpl(payload);
   }

   private LogicalMessageImpl.ImmutableLM createLogicalMessageImpl(Source payload) {
      if (payload == null) {
         this.lm = new LogicalMessageImpl.EmptyLogicalMessageImpl();
      } else if (payload instanceof DOMSource) {
         this.lm = new LogicalMessageImpl.DOMLogicalMessageImpl((DOMSource)payload);
      } else {
         this.lm = new LogicalMessageImpl.SourceLogicalMessageImpl(payload);
      }

      return this.lm;
   }

   public Object getPayload(BindingContext context) {
      if (context == null) {
         context = this.defaultJaxbContext;
      }

      if (context == null) {
         throw new WebServiceException("JAXBContext parameter cannot be null");
      } else {
         Object o;
         if (this.lm == null) {
            try {
               o = this.packet.getMessage().copy().readPayloadAsJAXB(context.createUnmarshaller());
            } catch (JAXBException var4) {
               throw new WebServiceException(var4);
            }
         } else {
            o = this.lm.getPayload(context);
            this.lm = new LogicalMessageImpl.JAXBLogicalMessageImpl(context.getJAXBContext(), o);
         }

         return o;
      }
   }

   public Object getPayload(JAXBContext context) {
      if (context == null) {
         return this.getPayload(this.defaultJaxbContext);
      } else if (context == null) {
         throw new WebServiceException("JAXBContext parameter cannot be null");
      } else {
         Object o;
         if (this.lm == null) {
            try {
               o = this.packet.getMessage().copy().readPayloadAsJAXB(context.createUnmarshaller());
            } catch (JAXBException var4) {
               throw new WebServiceException(var4);
            }
         } else {
            o = this.lm.getPayload(context);
            this.lm = new LogicalMessageImpl.JAXBLogicalMessageImpl(context, o);
         }

         return o;
      }
   }

   public void setPayload(Object payload, BindingContext context) {
      if (context == null) {
         context = this.defaultJaxbContext;
      }

      if (payload == null) {
         this.lm = new LogicalMessageImpl.EmptyLogicalMessageImpl();
      } else {
         this.lm = new LogicalMessageImpl.JAXBLogicalMessageImpl(context.getJAXBContext(), payload);
      }

   }

   public void setPayload(Object payload, JAXBContext context) {
      if (context == null) {
         this.setPayload(payload, this.defaultJaxbContext);
      }

      if (payload == null) {
         this.lm = new LogicalMessageImpl.EmptyLogicalMessageImpl();
      } else {
         this.lm = new LogicalMessageImpl.JAXBLogicalMessageImpl(context, payload);
      }

   }

   public boolean isPayloadModifed() {
      return this.lm != null;
   }

   public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
      assert this.isPayloadModifed();

      return this.isPayloadModifed() ? this.lm.getMessage(headers, attachments, binding) : this.packet.getMessage();
   }

   private class SourceLogicalMessageImpl extends LogicalMessageImpl.ImmutableLM {
      private Source payloadSrc;

      public SourceLogicalMessageImpl(Source source) {
         super(null);
         this.payloadSrc = source;
      }

      public Source getPayload() {
         assert !(this.payloadSrc instanceof DOMSource);

         try {
            Transformer transformer = XmlUtil.newTransformer();
            DOMResult domResult = new DOMResult();
            transformer.transform(this.payloadSrc, domResult);
            DOMSource dom = new DOMSource(domResult.getNode());
            LogicalMessageImpl.this.lm = LogicalMessageImpl.this.new DOMLogicalMessageImpl(dom);
            this.payloadSrc = null;
            return dom;
         } catch (TransformerException var4) {
            throw new WebServiceException(var4);
         }
      }

      public Object getPayload(JAXBContext context) {
         try {
            Source payloadSrc = this.getPayload();
            if (payloadSrc == null) {
               return null;
            } else {
               Unmarshaller unmarshaller = context.createUnmarshaller();
               return unmarshaller.unmarshal(payloadSrc);
            }
         } catch (JAXBException var4) {
            throw new WebServiceException(var4);
         }
      }

      public Object getPayload(BindingContext context) {
         try {
            Source payloadSrc = this.getPayload();
            if (payloadSrc == null) {
               return null;
            } else {
               Unmarshaller unmarshaller = context.createUnmarshaller();
               return unmarshaller.unmarshal(payloadSrc);
            }
         } catch (JAXBException var4) {
            throw new WebServiceException(var4);
         }
      }

      public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
         assert this.payloadSrc != null;

         return new PayloadSourceMessage(headers, this.payloadSrc, attachments, binding.getSOAPVersion());
      }
   }

   private class JAXBLogicalMessageImpl extends LogicalMessageImpl.ImmutableLM {
      private JAXBContext ctxt;
      private Object o;

      public JAXBLogicalMessageImpl(JAXBContext ctxt, Object o) {
         super(null);
         this.ctxt = ctxt;
         this.o = o;
      }

      public Source getPayload() {
         JAXBContext context = this.ctxt;
         if (context == null) {
            context = LogicalMessageImpl.this.defaultJaxbContext.getJAXBContext();
         }

         try {
            return new JAXBSource(context, this.o);
         } catch (JAXBException var3) {
            throw new WebServiceException(var3);
         }
      }

      public Object getPayload(JAXBContext context) {
         try {
            Source payloadSrc = this.getPayload();
            if (payloadSrc == null) {
               return null;
            } else {
               Unmarshaller unmarshaller = context.createUnmarshaller();
               return unmarshaller.unmarshal(payloadSrc);
            }
         } catch (JAXBException var4) {
            throw new WebServiceException(var4);
         }
      }

      public Object getPayload(BindingContext context) {
         try {
            Source payloadSrc = this.getPayload();
            if (payloadSrc == null) {
               return null;
            } else {
               Unmarshaller unmarshaller = context.createUnmarshaller();
               return unmarshaller.unmarshal(payloadSrc);
            }
         } catch (JAXBException var4) {
            throw new WebServiceException(var4);
         }
      }

      public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
         return JAXBMessage.create(BindingContextFactory.create(this.ctxt), this.o, binding.getSOAPVersion(), headers, attachments);
      }
   }

   private class EmptyLogicalMessageImpl extends LogicalMessageImpl.ImmutableLM {
      public EmptyLogicalMessageImpl() {
         super(null);
      }

      public Source getPayload() {
         return null;
      }

      public Object getPayload(JAXBContext context) {
         return null;
      }

      public Object getPayload(BindingContext context) {
         return null;
      }

      public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
         return new EmptyMessageImpl(headers, attachments, binding.getSOAPVersion());
      }
   }

   private class DOMLogicalMessageImpl extends LogicalMessageImpl.SourceLogicalMessageImpl {
      private DOMSource dom;

      public DOMLogicalMessageImpl(DOMSource dom) {
         super(dom);
         this.dom = dom;
      }

      public Source getPayload() {
         return this.dom;
      }

      public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
         Node n = this.dom.getNode();
         if (((Node)n).getNodeType() == 9) {
            n = ((Document)n).getDocumentElement();
         }

         return new DOMMessage(binding.getSOAPVersion(), headers, (Element)n, attachments);
      }
   }

   private abstract class ImmutableLM {
      private ImmutableLM() {
      }

      public abstract Source getPayload();

      public abstract Object getPayload(BindingContext var1);

      public abstract Object getPayload(JAXBContext var1);

      public abstract Message getMessage(MessageHeaders var1, AttachmentSet var2, WSBinding var3);

      // $FF: synthetic method
      ImmutableLM(Object x1) {
         this();
      }
   }
}
