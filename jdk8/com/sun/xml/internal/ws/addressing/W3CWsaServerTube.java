package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import javax.xml.ws.soap.AddressingFeature;

public class W3CWsaServerTube extends WsaServerTube {
   private final AddressingFeature af;

   public W3CWsaServerTube(WSEndpoint endpoint, @NotNull WSDLPort wsdlPort, WSBinding binding, Tube next) {
      super(endpoint, wsdlPort, binding, next);
      this.af = (AddressingFeature)binding.getFeature(AddressingFeature.class);
   }

   public W3CWsaServerTube(W3CWsaServerTube that, TubeCloner cloner) {
      super(that, cloner);
      this.af = that.af;
   }

   public W3CWsaServerTube copy(TubeCloner cloner) {
      return new W3CWsaServerTube(this, cloner);
   }

   protected void checkMandatoryHeaders(Packet packet, boolean foundAction, boolean foundTo, boolean foundReplyTo, boolean foundFaultTo, boolean foundMessageId, boolean foundRelatesTo) {
      super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageId, foundRelatesTo);
      WSDLBoundOperation wbo = this.getWSDLBoundOperation(packet);
      if (wbo != null && !wbo.getOperation().isOneWay() && !foundMessageId) {
         throw new MissingAddressingHeaderException(this.addressingVersion.messageIDTag, packet);
      }
   }

   protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation wbo) {
      return this.getResponseRequirement(wbo) == WSDLBoundOperation.ANONYMOUS.required;
   }

   private WSDLBoundOperation.ANONYMOUS getResponseRequirement(@Nullable WSDLBoundOperation wbo) {
      try {
         if (this.af.getResponses() == AddressingFeature.Responses.ANONYMOUS) {
            return WSDLBoundOperation.ANONYMOUS.required;
         }

         if (this.af.getResponses() == AddressingFeature.Responses.NON_ANONYMOUS) {
            return WSDLBoundOperation.ANONYMOUS.prohibited;
         }
      } catch (NoSuchMethodError var3) {
      }

      return wbo != null ? wbo.getAnonymous() : WSDLBoundOperation.ANONYMOUS.optional;
   }

   protected void checkAnonymousSemantics(WSDLBoundOperation wbo, WSEndpointReference replyTo, WSEndpointReference faultTo) {
      String replyToValue = null;
      String faultToValue = null;
      if (replyTo != null) {
         replyToValue = replyTo.getAddress();
      }

      if (faultTo != null) {
         faultToValue = faultTo.getAddress();
      }

      WSDLBoundOperation.ANONYMOUS responseRequirement = this.getResponseRequirement(wbo);
      switch(responseRequirement) {
      case prohibited:
         if (replyToValue != null && replyToValue.equals(this.addressingVersion.anonymousUri)) {
            throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
         }

         if (faultToValue != null && faultToValue.equals(this.addressingVersion.anonymousUri)) {
            throw new InvalidAddressingHeaderException(this.addressingVersion.faultToTag, W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
         }
         break;
      case required:
         if (replyToValue != null && !replyToValue.equals(this.addressingVersion.anonymousUri)) {
            throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
         }

         if (faultToValue != null && !faultToValue.equals(this.addressingVersion.anonymousUri)) {
            throw new InvalidAddressingHeaderException(this.addressingVersion.faultToTag, W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
         }
      }

   }
}
