package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalMessageContext;

class LogicalMessageContextImpl extends MessageUpdatableContext implements LogicalMessageContext {
   private LogicalMessageImpl lm;
   private WSBinding binding;
   private BindingContext defaultJaxbContext;

   public LogicalMessageContextImpl(WSBinding binding, BindingContext defaultJAXBContext, Packet packet) {
      super(packet);
      this.binding = binding;
      this.defaultJaxbContext = defaultJAXBContext;
   }

   public LogicalMessage getMessage() {
      if (this.lm == null) {
         this.lm = new LogicalMessageImpl(this.defaultJaxbContext, this.packet);
      }

      return this.lm;
   }

   void setPacketMessage(Message newMessage) {
      if (newMessage != null) {
         this.packet.setMessage(newMessage);
         this.lm = null;
      }

   }

   protected void updateMessage() {
      if (this.lm != null) {
         if (this.lm.isPayloadModifed()) {
            Message msg = this.packet.getMessage();
            Message updatedMsg = this.lm.getMessage(msg.getHeaders(), msg.getAttachments(), this.binding);
            this.packet.setMessage(updatedMsg);
         }

         this.lm = null;
      }

   }
}
