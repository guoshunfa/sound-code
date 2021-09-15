package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public class W3CWsaClientTube extends WsaClientTube {
   public W3CWsaClientTube(WSDLPort wsdlPort, WSBinding binding, Tube next) {
      super(wsdlPort, binding, next);
   }

   public W3CWsaClientTube(WsaClientTube that, TubeCloner cloner) {
      super(that, cloner);
   }

   public W3CWsaClientTube copy(TubeCloner cloner) {
      return new W3CWsaClientTube(this, cloner);
   }

   protected void checkMandatoryHeaders(Packet packet, boolean foundAction, boolean foundTo, boolean foundReplyTo, boolean foundFaultTo, boolean foundMessageID, boolean foundRelatesTo) {
      super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageID, foundRelatesTo);
      if (this.expectReply && packet.getMessage() != null && !foundRelatesTo) {
         String action = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
         if (!packet.getMessage().isFault() || !action.equals(this.addressingVersion.getDefaultFaultAction())) {
            throw new MissingAddressingHeaderException(this.addressingVersion.relatesToTag, packet);
         }
      }

   }
}
