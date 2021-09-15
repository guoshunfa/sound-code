package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.ws.api.message.Packet;
import org.xml.sax.ErrorHandler;

public abstract class ValidationErrorHandler implements ErrorHandler {
   protected Packet packet;

   public void setPacket(Packet packet) {
      this.packet = packet;
   }
}
