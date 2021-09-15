package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.NonAnonymousResponseProcessor;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.message.FaultDetailHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;

public class WsaServerTube extends WsaTube {
   private WSEndpoint endpoint;
   private WSEndpointReference replyTo;
   private WSEndpointReference faultTo;
   private boolean isAnonymousRequired = false;
   protected boolean isEarlyBackchannelCloseAllowed = true;
   private WSDLBoundOperation wbo;
   /** @deprecated */
   public static final String REQUEST_MESSAGE_ID = "com.sun.xml.internal.ws.addressing.request.messageID";
   private static final Logger LOGGER = Logger.getLogger(WsaServerTube.class.getName());

   public WsaServerTube(WSEndpoint endpoint, @NotNull WSDLPort wsdlPort, WSBinding binding, Tube next) {
      super(wsdlPort, binding, next);
      this.endpoint = endpoint;
   }

   public WsaServerTube(WsaServerTube that, TubeCloner cloner) {
      super(that, cloner);
      this.endpoint = that.endpoint;
   }

   public WsaServerTube copy(TubeCloner cloner) {
      return new WsaServerTube(this, cloner);
   }

   @NotNull
   public NextAction processRequest(Packet request) {
      Message msg = request.getMessage();
      if (msg == null) {
         return this.doInvoke(this.next, request);
      } else {
         request.addSatellite(new WsaPropertyBag(this.addressingVersion, this.soapVersion, request));
         MessageHeaders hl = request.getMessage().getHeaders();

         String msgId;
         try {
            this.replyTo = AddressingUtils.getReplyTo(hl, this.addressingVersion, this.soapVersion);
            this.faultTo = AddressingUtils.getFaultTo(hl, this.addressingVersion, this.soapVersion);
            msgId = AddressingUtils.getMessageID(hl, this.addressingVersion, this.soapVersion);
         } catch (InvalidAddressingHeaderException var9) {
            LOGGER.log(Level.WARNING, (String)(this.addressingVersion.getInvalidMapText() + ", Problem header:" + var9.getProblemHeader() + ", Reason: " + var9.getSubsubcode()), (Throwable)var9);
            hl.remove(var9.getProblemHeader());
            SOAPFault soapFault = this.helper.createInvalidAddressingHeaderFault(var9, this.addressingVersion);
            if (this.wsdlPort != null && request.getMessage().isOneWay(this.wsdlPort)) {
               Packet response = request.createServerResponse((Message)null, (WSDLPort)this.wsdlPort, (SEIModel)null, (WSBinding)this.binding);
               return this.doReturnWith(response);
            }

            Message m = Messages.create(soapFault);
            if (this.soapVersion == SOAPVersion.SOAP_11) {
               FaultDetailHeader s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), var9.getProblemHeader());
               m.getHeaders().add(s11FaultDetailHeader);
            }

            Packet response = request.createServerResponse(m, (WSDLPort)this.wsdlPort, (SEIModel)null, (WSBinding)this.binding);
            return this.doReturnWith(response);
         }

         if (this.replyTo == null) {
            this.replyTo = this.addressingVersion.anonymousEpr;
         }

         if (this.faultTo == null) {
            this.faultTo = this.replyTo;
         }

         request.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest", this.replyTo);
         request.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest", this.faultTo);
         request.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest", msgId);
         this.wbo = this.getWSDLBoundOperation(request);
         this.isAnonymousRequired = this.isAnonymousRequired(this.wbo);
         Packet p = this.validateInboundHeaders(request);
         if (p.getMessage() == null) {
            return this.doReturnWith(p);
         } else if (p.getMessage().isFault()) {
            if (this.isEarlyBackchannelCloseAllowed && !this.isAnonymousRequired && !this.faultTo.isAnonymous() && request.transportBackChannel != null) {
               request.transportBackChannel.close();
            }

            return this.processResponse(p);
         } else {
            if (this.isEarlyBackchannelCloseAllowed && !this.isAnonymousRequired && !this.replyTo.isAnonymous() && !this.faultTo.isAnonymous() && request.transportBackChannel != null) {
               request.transportBackChannel.close();
            }

            return this.doInvoke(this.next, p);
         }
      }
   }

   protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation wbo) {
      return false;
   }

   protected void checkAnonymousSemantics(WSDLBoundOperation wbo, WSEndpointReference replyTo, WSEndpointReference faultTo) {
   }

   @NotNull
   public NextAction processException(Throwable t) {
      Packet response = Fiber.current().getPacket();
      ThrowableContainerPropertySet tc = (ThrowableContainerPropertySet)response.getSatellite(ThrowableContainerPropertySet.class);
      if (tc == null) {
         tc = new ThrowableContainerPropertySet(t);
         response.addSatellite(tc);
      } else if (t != tc.getThrowable()) {
         tc.setThrowable(t);
      }

      return this.processResponse(response.endpoint.createServiceResponseForException(tc, response, this.soapVersion, this.wsdlPort, response.endpoint.getSEIModel(), this.binding));
   }

   @NotNull
   public NextAction processResponse(Packet response) {
      Message msg = response.getMessage();
      if (msg == null) {
         return this.doReturnWith(response);
      } else {
         String to = AddressingUtils.getTo(msg.getHeaders(), this.addressingVersion, this.soapVersion);
         if (to != null) {
            this.replyTo = this.faultTo = new WSEndpointReference(to, this.addressingVersion);
         }

         if (this.replyTo == null) {
            this.replyTo = (WSEndpointReference)response.get("com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest");
         }

         if (this.faultTo == null) {
            this.faultTo = (WSEndpointReference)response.get("com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest");
         }

         WSEndpointReference target = msg.isFault() ? this.faultTo : this.replyTo;
         if (target == null && response.proxy instanceof Stub) {
            target = ((Stub)response.proxy).getWSEndpointReference();
         }

         if (target != null && !target.isAnonymous() && !this.isAnonymousRequired) {
            if (target.isNone()) {
               response.setMessage((Message)null);
               return this.doReturnWith(response);
            } else if (this.wsdlPort != null && response.getMessage().isOneWay(this.wsdlPort)) {
               LOGGER.fine(AddressingMessages.NON_ANONYMOUS_RESPONSE_ONEWAY());
               return this.doReturnWith(response);
            } else {
               if (this.wbo != null || response.soapAction == null) {
                  String action = response.getMessage().isFault() ? this.helper.getFaultAction(this.wbo, response) : this.helper.getOutputAction(this.wbo);
                  if (response.soapAction == null || action != null && !action.equals("http://jax-ws.dev.java.net/addressing/output-action-not-set")) {
                     response.soapAction = action;
                  }
               }

               response.expectReply = false;

               EndpointAddress adrs;
               try {
                  adrs = new EndpointAddress(URI.create(target.getAddress()));
               } catch (NullPointerException var7) {
                  throw new WebServiceException(var7);
               } catch (IllegalArgumentException var8) {
                  throw new WebServiceException(var8);
               }

               response.endpointAddress = adrs;
               return response.isAdapterDeliversNonAnonymousResponse ? this.doReturnWith(response) : this.doReturnWith(NonAnonymousResponseProcessor.getDefault().process(response));
            }
         } else {
            return this.doReturnWith(response);
         }
      }
   }

   protected void validateAction(Packet packet) {
      WSDLBoundOperation wsdlBoundOperation = this.getWSDLBoundOperation(packet);
      if (wsdlBoundOperation != null) {
         String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
         if (gotA == null) {
            throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION());
         } else {
            String expected = this.helper.getInputAction(packet);
            String soapAction = this.helper.getSOAPAction(packet);
            if (this.helper.isInputActionDefault(packet) && soapAction != null && !soapAction.equals("")) {
               expected = soapAction;
            }

            if (expected != null && !gotA.equals(expected)) {
               throw new ActionNotSupportedException(gotA);
            }
         }
      }
   }

   protected void checkMessageAddressingProperties(Packet packet) {
      super.checkMessageAddressingProperties(packet);
      WSDLBoundOperation wsdlBoundOperation = this.getWSDLBoundOperation(packet);
      this.checkAnonymousSemantics(wsdlBoundOperation, this.replyTo, this.faultTo);
      this.checkNonAnonymousAddresses(this.replyTo, this.faultTo);
   }

   private void checkNonAnonymousAddresses(WSEndpointReference replyTo, WSEndpointReference faultTo) {
      if (!replyTo.isAnonymous()) {
         try {
            new EndpointAddress(URI.create(replyTo.getAddress()));
         } catch (Exception var4) {
            throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, this.addressingVersion.invalidAddressTag);
         }
      }

   }
}
