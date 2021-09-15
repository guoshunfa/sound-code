package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.ws.WebServiceException;

public class WsaClientTube extends WsaTube {
   protected boolean expectReply = true;

   public WsaClientTube(WSDLPort wsdlPort, WSBinding binding, Tube next) {
      super(wsdlPort, binding, next);
   }

   public WsaClientTube(WsaClientTube that, TubeCloner cloner) {
      super(that, cloner);
   }

   public WsaClientTube copy(TubeCloner cloner) {
      return new WsaClientTube(this, cloner);
   }

   @NotNull
   public NextAction processRequest(Packet request) {
      this.expectReply = request.expectReply;
      return this.doInvoke(this.next, request);
   }

   @NotNull
   public NextAction processResponse(Packet response) {
      if (response.getMessage() != null) {
         response = this.validateInboundHeaders(response);
         response.addSatellite(new WsaPropertyBag(this.addressingVersion, this.soapVersion, response));
         String msgId = AddressingUtils.getMessageID(response.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
         response.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest", msgId);
      }

      return this.doReturnWith(response);
   }

   protected void validateAction(Packet packet) {
      WSDLBoundOperation wbo = this.getWSDLBoundOperation(packet);
      if (wbo != null) {
         String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
         if (gotA == null) {
            throw new WebServiceException(AddressingMessages.VALIDATION_CLIENT_NULL_ACTION());
         } else {
            String expected = this.helper.getOutputAction(packet);
            if (expected != null && !gotA.equals(expected)) {
               throw new ActionNotSupportedException(gotA);
            }
         }
      }
   }
}
