package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.ContentType;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.ws.addressing.WsaPropertyBag;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.DistributedPropertySet;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.message.RelatesToHeader;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public final class Packet extends BaseDistributedPropertySet implements MessageContext, MessageMetadata {
   private Message message;
   private WSDLOperationMapping wsdlOperationMapping;
   private QName wsdlOperation;
   public boolean wasTransportSecure;
   public static final String INBOUND_TRANSPORT_HEADERS = "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers";
   public static final String OUTBOUND_TRANSPORT_HEADERS = "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers";
   public static final String HA_INFO = "com.sun.xml.internal.ws.api.message.packet.hainfo";
   @PropertySet.Property({"com.sun.xml.internal.ws.handler.config"})
   public HandlerConfiguration handlerConfig;
   @PropertySet.Property({"com.sun.xml.internal.ws.client.handle"})
   public BindingProvider proxy;
   public boolean isAdapterDeliversNonAnonymousResponse;
   public boolean packetTakesPriorityOverRequestContext;
   public EndpointAddress endpointAddress;
   public ContentNegotiation contentNegotiation;
   public String acceptableMimeTypes;
   public WebServiceContextDelegate webServiceContextDelegate;
   @Nullable
   public TransportBackChannel transportBackChannel;
   public Component component;
   @PropertySet.Property({"com.sun.xml.internal.ws.api.server.WSEndpoint"})
   public WSEndpoint endpoint;
   @PropertySet.Property({"javax.xml.ws.soap.http.soapaction.uri"})
   public String soapAction;
   @PropertySet.Property({"com.sun.xml.internal.ws.server.OneWayOperation"})
   public Boolean expectReply;
   /** @deprecated */
   @Deprecated
   public Boolean isOneWay;
   public Boolean isSynchronousMEP;
   public Boolean nonNullAsyncHandlerGiven;
   private Boolean isRequestReplyMEP;
   private Set<String> handlerScopePropertyNames;
   public final Map<String, Object> invocationProperties;
   private static final BasePropertySet.PropertyMap model = parse(Packet.class);
   private static final Logger LOGGER = Logger.getLogger(Packet.class.getName());
   public Codec codec;
   private ContentType contentType;
   private Boolean mtomRequest;
   private Boolean mtomAcceptable;
   private MTOMFeature mtomFeature;
   Boolean checkMtomAcceptable;
   private Boolean fastInfosetAcceptable;
   private Packet.State state;
   private boolean isFastInfosetDisabled;

   public Packet(Message request) {
      this();
      this.message = request;
      if (this.message != null) {
         this.message.setMessageMedadata(this);
      }

   }

   public Packet() {
      this.wsdlOperationMapping = null;
      this.packetTakesPriorityOverRequestContext = false;
      this.codec = null;
      this.state = Packet.State.ServerRequest;
      this.invocationProperties = new HashMap();
   }

   private Packet(Packet that) {
      this.wsdlOperationMapping = null;
      this.packetTakesPriorityOverRequestContext = false;
      this.codec = null;
      this.state = Packet.State.ServerRequest;
      this.relatePackets(that, true);
      this.invocationProperties = that.invocationProperties;
   }

   public Packet copy(boolean copyMessage) {
      Packet copy = new Packet(this);
      if (copyMessage && this.message != null) {
         copy.message = this.message.copy();
      }

      if (copy.message != null) {
         copy.message.setMessageMedadata(copy);
      }

      return copy;
   }

   public Message getMessage() {
      if (this.message != null && !(this.message instanceof MessageWrapper)) {
         this.message = new MessageWrapper(this, this.message);
      }

      return this.message;
   }

   public Message getInternalMessage() {
      return this.message instanceof MessageWrapper ? ((MessageWrapper)this.message).delegate : this.message;
   }

   public WSBinding getBinding() {
      if (this.endpoint != null) {
         return this.endpoint.getBinding();
      } else {
         return this.proxy != null ? (WSBinding)this.proxy.getBinding() : null;
      }
   }

   public void setMessage(Message message) {
      this.message = message;
      if (message != null) {
         this.message.setMessageMedadata(this);
      }

   }

   @PropertySet.Property({"javax.xml.ws.wsdl.operation"})
   @Nullable
   public final QName getWSDLOperation() {
      if (this.wsdlOperation != null) {
         return this.wsdlOperation;
      } else {
         if (this.wsdlOperationMapping == null) {
            this.wsdlOperationMapping = this.getWSDLOperationMapping();
         }

         if (this.wsdlOperationMapping != null) {
            this.wsdlOperation = this.wsdlOperationMapping.getOperationName();
         }

         return this.wsdlOperation;
      }
   }

   public WSDLOperationMapping getWSDLOperationMapping() {
      if (this.wsdlOperationMapping != null) {
         return this.wsdlOperationMapping;
      } else {
         OperationDispatcher opDispatcher = null;
         if (this.endpoint != null) {
            opDispatcher = this.endpoint.getOperationDispatcher();
         } else if (this.proxy != null) {
            opDispatcher = ((Stub)this.proxy).getOperationDispatcher();
         }

         if (opDispatcher != null) {
            try {
               this.wsdlOperationMapping = opDispatcher.getWSDLOperationMapping(this);
            } catch (DispatchException var3) {
            }
         }

         return this.wsdlOperationMapping;
      }
   }

   public void setWSDLOperation(QName wsdlOp) {
      this.wsdlOperation = wsdlOp;
   }

   /** @deprecated */
   @PropertySet.Property({"javax.xml.ws.service.endpoint.address"})
   public String getEndPointAddressString() {
      return this.endpointAddress == null ? null : this.endpointAddress.toString();
   }

   public void setEndPointAddressString(String s) {
      if (s == null) {
         this.endpointAddress = null;
      } else {
         this.endpointAddress = EndpointAddress.create(s);
      }

   }

   @PropertySet.Property({"com.sun.xml.internal.ws.client.ContentNegotiation"})
   public String getContentNegotiationString() {
      return this.contentNegotiation != null ? this.contentNegotiation.toString() : null;
   }

   public void setContentNegotiationString(String s) {
      if (s == null) {
         this.contentNegotiation = null;
      } else {
         try {
            this.contentNegotiation = ContentNegotiation.valueOf(s);
         } catch (IllegalArgumentException var3) {
            this.contentNegotiation = ContentNegotiation.none;
         }
      }

   }

   @PropertySet.Property({"javax.xml.ws.reference.parameters"})
   @NotNull
   public List<Element> getReferenceParameters() {
      Message msg = this.getMessage();
      List<Element> refParams = new ArrayList();
      if (msg == null) {
         return refParams;
      } else {
         MessageHeaders hl = msg.getHeaders();
         Iterator var4 = hl.asList().iterator();

         while(true) {
            Header h;
            String attr;
            do {
               do {
                  if (!var4.hasNext()) {
                     return refParams;
                  }

                  h = (Header)var4.next();
                  attr = h.getAttribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter");
               } while(attr == null);
            } while(!attr.equals("true") && !attr.equals("1"));

            Document d = DOMUtil.createDom();
            SAX2DOMEx s2d = new SAX2DOMEx(d);

            try {
               h.writeTo(s2d, XmlUtil.DRACONIAN_ERROR_HANDLER);
               refParams.add((Element)d.getLastChild());
            } catch (SAXException var10) {
               throw new WebServiceException(var10);
            }
         }
      }
   }

   @PropertySet.Property({"com.sun.xml.internal.ws.api.message.HeaderList"})
   MessageHeaders getHeaderList() {
      Message msg = this.getMessage();
      return msg == null ? null : msg.getHeaders();
   }

   public TransportBackChannel keepTransportBackChannelOpen() {
      TransportBackChannel r = this.transportBackChannel;
      this.transportBackChannel = null;
      return r;
   }

   public Boolean isRequestReplyMEP() {
      return this.isRequestReplyMEP;
   }

   public void setRequestReplyMEP(Boolean x) {
      this.isRequestReplyMEP = x;
   }

   public final Set<String> getHandlerScopePropertyNames(boolean readOnly) {
      Set<String> o = this.handlerScopePropertyNames;
      if (o == null) {
         if (readOnly) {
            return Collections.emptySet();
         }

         o = new HashSet();
         this.handlerScopePropertyNames = (Set)o;
      }

      return (Set)o;
   }

   /** @deprecated */
   public final Set<String> getApplicationScopePropertyNames(boolean readOnly) {
      assert false;

      return new HashSet();
   }

   /** @deprecated */
   @Deprecated
   public Packet createResponse(Message msg) {
      Packet response = new Packet(this);
      response.setMessage(msg);
      return response;
   }

   public Packet createClientResponse(Message msg) {
      Packet response = new Packet(this);
      response.setMessage(msg);
      this.finishCreateRelateClientResponse(response);
      return response;
   }

   public Packet relateClientResponse(Packet response) {
      response.relatePackets(this, true);
      this.finishCreateRelateClientResponse(response);
      return response;
   }

   private void finishCreateRelateClientResponse(Packet response) {
      response.soapAction = null;
      response.setState(Packet.State.ClientResponse);
   }

   public Packet createServerResponse(@Nullable Message responseMessage, @Nullable WSDLPort wsdlPort, @Nullable SEIModel seiModel, @NotNull WSBinding binding) {
      Packet r = this.createClientResponse(responseMessage);
      return this.relateServerResponse(r, wsdlPort, seiModel, binding);
   }

   public void copyPropertiesTo(@Nullable Packet response) {
      this.relatePackets(response, false);
   }

   private void relatePackets(@Nullable Packet packet, boolean isCopy) {
      Packet request;
      Packet response;
      if (!isCopy) {
         request = this;
         response = packet;
         packet.soapAction = null;
         packet.invocationProperties.putAll(this.invocationProperties);
         if (this.getState().equals(Packet.State.ServerRequest)) {
            packet.setState(Packet.State.ServerResponse);
         }
      } else {
         request = packet;
         response = this;
         this.soapAction = packet.soapAction;
         this.setState(packet.getState());
      }

      request.copySatelliteInto(response);
      response.isAdapterDeliversNonAnonymousResponse = request.isAdapterDeliversNonAnonymousResponse;
      response.handlerConfig = request.handlerConfig;
      response.handlerScopePropertyNames = request.handlerScopePropertyNames;
      response.contentNegotiation = request.contentNegotiation;
      response.wasTransportSecure = request.wasTransportSecure;
      response.transportBackChannel = request.transportBackChannel;
      response.endpointAddress = request.endpointAddress;
      response.wsdlOperation = request.wsdlOperation;
      response.wsdlOperationMapping = request.wsdlOperationMapping;
      response.acceptableMimeTypes = request.acceptableMimeTypes;
      response.endpoint = request.endpoint;
      response.proxy = request.proxy;
      response.webServiceContextDelegate = request.webServiceContextDelegate;
      response.expectReply = request.expectReply;
      response.component = request.component;
      response.mtomAcceptable = request.mtomAcceptable;
      response.mtomRequest = request.mtomRequest;
   }

   public Packet relateServerResponse(@Nullable Packet r, @Nullable WSDLPort wsdlPort, @Nullable SEIModel seiModel, @NotNull WSBinding binding) {
      this.relatePackets(r, false);
      r.setState(Packet.State.ServerResponse);
      AddressingVersion av = binding.getAddressingVersion();
      if (av == null) {
         return r;
      } else if (this.getMessage() == null) {
         return r;
      } else {
         String inputAction = AddressingUtils.getAction(this.getMessage().getHeaders(), av, binding.getSOAPVersion());
         if (inputAction == null) {
            return r;
         } else if (r.getMessage() != null && (wsdlPort == null || !this.getMessage().isOneWay(wsdlPort))) {
            this.populateAddressingHeaders(binding, r, wsdlPort, seiModel);
            return r;
         } else {
            return r;
         }
      }
   }

   public Packet createServerResponse(@Nullable Message responseMessage, @NotNull AddressingVersion addressingVersion, @NotNull SOAPVersion soapVersion, @NotNull String action) {
      Packet responsePacket = this.createClientResponse(responseMessage);
      responsePacket.setState(Packet.State.ServerResponse);
      if (addressingVersion == null) {
         return responsePacket;
      } else {
         String inputAction = AddressingUtils.getAction(this.getMessage().getHeaders(), addressingVersion, soapVersion);
         if (inputAction == null) {
            return responsePacket;
         } else {
            this.populateAddressingHeaders(responsePacket, addressingVersion, soapVersion, action, false);
            return responsePacket;
         }
      }
   }

   public void setResponseMessage(@NotNull Packet request, @Nullable Message responseMessage, @NotNull AddressingVersion addressingVersion, @NotNull SOAPVersion soapVersion, @NotNull String action) {
      Packet temp = request.createServerResponse(responseMessage, addressingVersion, soapVersion, action);
      this.setMessage(temp.getMessage());
   }

   private void populateAddressingHeaders(Packet responsePacket, AddressingVersion av, SOAPVersion sv, String action, boolean mustUnderstand) {
      if (av != null) {
         if (responsePacket.getMessage() != null) {
            MessageHeaders hl = responsePacket.getMessage().getHeaders();
            WsaPropertyBag wpb = (WsaPropertyBag)this.getSatellite(WsaPropertyBag.class);
            Message msg = this.getMessage();
            WSEndpointReference replyTo = null;
            Header replyToFromRequestMsg = AddressingUtils.getFirstHeader(msg.getHeaders(), av.replyToTag, true, sv);
            Header replyToFromResponseMsg = hl.get(av.toTag, false);
            boolean replaceToTag = true;

            try {
               if (replyToFromRequestMsg != null) {
                  replyTo = replyToFromRequestMsg.readAsEPR(av);
               }

               if (replyToFromResponseMsg != null && replyTo == null) {
                  replaceToTag = false;
               }
            } catch (XMLStreamException var15) {
               throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), var15);
            }

            if (replyTo == null) {
               replyTo = AddressingUtils.getReplyTo(msg.getHeaders(), av, sv);
            }

            if (AddressingUtils.getAction(responsePacket.getMessage().getHeaders(), av, sv) == null) {
               hl.add(new StringHeader(av.actionTag, action, sv, mustUnderstand));
            }

            String mid;
            if (responsePacket.getMessage().getHeaders().get(av.messageIDTag, false) == null) {
               mid = Message.generateMessageID();
               hl.add(new StringHeader(av.messageIDTag, mid));
            }

            mid = null;
            if (wpb != null) {
               mid = wpb.getMessageID();
            }

            if (mid == null) {
               mid = AddressingUtils.getMessageID(msg.getHeaders(), av, sv);
            }

            if (mid != null) {
               hl.addOrReplace(new RelatesToHeader(av.relatesToTag, mid));
            }

            WSEndpointReference refpEPR = null;
            if (responsePacket.getMessage().isFault()) {
               if (wpb != null) {
                  refpEPR = wpb.getFaultToFromRequest();
               }

               if (refpEPR == null) {
                  refpEPR = AddressingUtils.getFaultTo(msg.getHeaders(), av, sv);
               }

               if (refpEPR == null) {
                  refpEPR = replyTo;
               }
            } else {
               refpEPR = replyTo;
            }

            if (replaceToTag && refpEPR != null) {
               hl.addOrReplace(new StringHeader(av.toTag, refpEPR.getAddress()));
               refpEPR.addReferenceParametersToList(hl);
            }

         }
      }
   }

   private void populateAddressingHeaders(WSBinding binding, Packet responsePacket, WSDLPort wsdlPort, SEIModel seiModel) {
      AddressingVersion addressingVersion = binding.getAddressingVersion();
      if (addressingVersion != null) {
         WsaTubeHelper wsaHelper = addressingVersion.getWsaHelper(wsdlPort, seiModel, binding);
         String action = responsePacket.getMessage().isFault() ? wsaHelper.getFaultAction(this, responsePacket) : wsaHelper.getOutputAction(this);
         if (action == null) {
            LOGGER.info("WSA headers are not added as value for wsa:Action cannot be resolved for this message");
         } else {
            this.populateAddressingHeaders(responsePacket, addressingVersion, binding.getSOAPVersion(), action, AddressingVersion.isRequired(binding));
         }
      }
   }

   public String toShortString() {
      return super.toString();
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(super.toString());

      String content;
      try {
         Message msg = this.getMessage();
         if (msg != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLStreamWriter xmlWriter = XMLStreamWriterFactory.create(baos, "UTF-8");
            msg.copy().writeTo(xmlWriter);
            xmlWriter.flush();
            xmlWriter.close();
            baos.flush();
            XMLStreamWriterFactory.recycle(xmlWriter);
            byte[] bytes = baos.toByteArray();
            content = new String(bytes, "UTF-8");
         } else {
            content = "<none>";
         }
      } catch (Throwable var7) {
         throw new WebServiceException(var7);
      }

      buf.append(" Content: ").append(content);
      return buf.toString();
   }

   protected BasePropertySet.PropertyMap getPropertyMap() {
      return model;
   }

   public Map<String, Object> asMapIncludingInvocationProperties() {
      final Map<String, Object> asMap = this.asMap();
      return new AbstractMap<String, Object>() {
         public Object get(Object key) {
            Object o = asMap.get(key);
            return o != null ? o : Packet.this.invocationProperties.get(key);
         }

         public int size() {
            return asMap.size() + Packet.this.invocationProperties.size();
         }

         public boolean containsKey(Object key) {
            return asMap.containsKey(key) ? true : Packet.this.invocationProperties.containsKey(key);
         }

         public Set<Map.Entry<String, Object>> entrySet() {
            final Set<Map.Entry<String, Object>> asMapEntries = asMap.entrySet();
            final Set<Map.Entry<String, Object>> ipEntries = Packet.this.invocationProperties.entrySet();
            return new AbstractSet<Map.Entry<String, Object>>() {
               public Iterator<Map.Entry<String, Object>> iterator() {
                  final Iterator<Map.Entry<String, Object>> asMapIt = asMapEntries.iterator();
                  final Iterator<Map.Entry<String, Object>> ipIt = ipEntries.iterator();
                  return new Iterator<Map.Entry<String, Object>>() {
                     public boolean hasNext() {
                        return asMapIt.hasNext() || ipIt.hasNext();
                     }

                     public Map.Entry<String, Object> next() {
                        return asMapIt.hasNext() ? (Map.Entry)asMapIt.next() : (Map.Entry)ipIt.next();
                     }

                     public void remove() {
                        throw new UnsupportedOperationException();
                     }
                  };
               }

               public int size() {
                  return asMap.size() + Packet.this.invocationProperties.size();
               }
            };
         }

         public Object put(String key, Object value) {
            return Packet.this.supports(key) ? asMap.put(key, value) : Packet.this.invocationProperties.put(key, value);
         }

         public void clear() {
            asMap.clear();
            Packet.this.invocationProperties.clear();
         }

         public Object remove(Object key) {
            return Packet.this.supports(key) ? asMap.remove(key) : Packet.this.invocationProperties.remove(key);
         }
      };
   }

   public SOAPMessage getSOAPMessage() throws SOAPException {
      return this.getAsSOAPMessage();
   }

   public SOAPMessage getAsSOAPMessage() throws SOAPException {
      Message msg = this.getMessage();
      if (msg == null) {
         return null;
      } else {
         if (msg instanceof MessageWritable) {
            ((MessageWritable)msg).setMTOMConfiguration(this.mtomFeature);
         }

         return msg.readAsSOAPMessage(this, this.getState().isInbound());
      }
   }

   public Codec getCodec() {
      if (this.codec != null) {
         return this.codec;
      } else {
         if (this.endpoint != null) {
            this.codec = this.endpoint.createCodec();
         }

         WSBinding wsb = this.getBinding();
         if (wsb != null) {
            this.codec = wsb.getBindingId().createEncoder(wsb);
         }

         return this.codec;
      }
   }

   public ContentType writeTo(OutputStream out) throws IOException {
      Message msg = this.getInternalMessage();
      if (msg instanceof MessageWritable) {
         ((MessageWritable)msg).setMTOMConfiguration(this.mtomFeature);
         return ((MessageWritable)msg).writeTo(out);
      } else {
         return this.getCodec().encode(this, out);
      }
   }

   public ContentType writeTo(WritableByteChannel buffer) {
      return this.getCodec().encode(this, buffer);
   }

   public Boolean getMtomRequest() {
      return this.mtomRequest;
   }

   public void setMtomRequest(Boolean mtomRequest) {
      this.mtomRequest = mtomRequest;
   }

   public Boolean getMtomAcceptable() {
      return this.mtomAcceptable;
   }

   public void checkMtomAcceptable() {
      if (this.checkMtomAcceptable == null) {
         if (this.acceptableMimeTypes != null && !this.isFastInfosetDisabled) {
            this.checkMtomAcceptable = this.acceptableMimeTypes.indexOf("application/xop+xml") != -1;
         } else {
            this.checkMtomAcceptable = false;
         }
      }

      this.mtomAcceptable = this.checkMtomAcceptable;
   }

   public Boolean getFastInfosetAcceptable(String fiMimeType) {
      if (this.fastInfosetAcceptable == null) {
         if (this.acceptableMimeTypes != null && !this.isFastInfosetDisabled) {
            this.fastInfosetAcceptable = this.acceptableMimeTypes.indexOf(fiMimeType) != -1;
         } else {
            this.fastInfosetAcceptable = false;
         }
      }

      return this.fastInfosetAcceptable;
   }

   public void setMtomFeature(MTOMFeature mtomFeature) {
      this.mtomFeature = mtomFeature;
   }

   public MTOMFeature getMtomFeature() {
      WSBinding binding = this.getBinding();
      return binding != null ? (MTOMFeature)binding.getFeature(MTOMFeature.class) : this.mtomFeature;
   }

   public ContentType getContentType() {
      if (this.contentType == null) {
         this.contentType = this.getInternalContentType();
      }

      if (this.contentType == null) {
         this.contentType = this.getCodec().getStaticContentType(this);
      }

      if (this.contentType == null) {
      }

      return this.contentType;
   }

   public ContentType getInternalContentType() {
      Message msg = this.getInternalMessage();
      return msg instanceof MessageWritable ? ((MessageWritable)msg).getContentType() : this.contentType;
   }

   public void setContentType(ContentType contentType) {
      this.contentType = contentType;
   }

   public Packet.State getState() {
      return this.state;
   }

   public void setState(Packet.State state) {
      this.state = state;
   }

   public boolean shouldUseMtom() {
      return this.getState().isInbound() ? this.isMtomContentType() : this.shouldUseMtomOutbound();
   }

   private boolean shouldUseMtomOutbound() {
      MTOMFeature myMtomFeature = this.getMtomFeature();
      if (myMtomFeature != null && myMtomFeature.isEnabled()) {
         if (this.getMtomAcceptable() == null && this.getMtomRequest() == null) {
            return true;
         }

         if (this.getMtomAcceptable() != null && this.getMtomAcceptable() && this.getState().equals(Packet.State.ServerResponse)) {
            return true;
         }

         if (this.getMtomRequest() != null && this.getMtomRequest() && this.getState().equals(Packet.State.ServerResponse)) {
            return true;
         }

         if (this.getMtomRequest() != null && this.getMtomRequest() && this.getState().equals(Packet.State.ClientRequest)) {
            return true;
         }
      }

      return false;
   }

   private boolean isMtomContentType() {
      return this.getInternalContentType() != null && this.getInternalContentType().getContentType().contains("application/xop+xml");
   }

   /** @deprecated */
   public void addSatellite(@NotNull com.sun.xml.internal.ws.api.PropertySet satellite) {
      super.addSatellite(satellite);
   }

   /** @deprecated */
   public void addSatellite(@NotNull Class keyClass, @NotNull com.sun.xml.internal.ws.api.PropertySet satellite) {
      super.addSatellite(keyClass, satellite);
   }

   /** @deprecated */
   public void copySatelliteInto(@NotNull DistributedPropertySet r) {
      super.copySatelliteInto((com.oracle.webservices.internal.api.message.DistributedPropertySet)r);
   }

   /** @deprecated */
   public void removeSatellite(com.sun.xml.internal.ws.api.PropertySet satellite) {
      super.removeSatellite(satellite);
   }

   public void setFastInfosetDisabled(boolean b) {
      this.isFastInfosetDisabled = b;
   }

   public static enum State {
      ServerRequest(true),
      ClientRequest(false),
      ServerResponse(false),
      ClientResponse(true);

      private boolean inbound;

      private State(boolean inbound) {
         this.inbound = inbound;
      }

      public boolean isInbound() {
         return this.inbound;
      }
   }

   public static enum Status {
      Request,
      Response,
      Unknown;

      public boolean isRequest() {
         return Request.equals(this);
      }

      public boolean isResponse() {
         return Response.equals(this);
      }
   }
}
