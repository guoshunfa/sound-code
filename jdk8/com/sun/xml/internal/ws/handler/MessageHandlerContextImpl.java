package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.handler.MessageHandlerContext;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.Set;

public class MessageHandlerContextImpl extends MessageUpdatableContext implements MessageHandlerContext {
   @Nullable
   private SEIModel seiModel;
   private Set<String> roles;
   private WSBinding binding;
   @Nullable
   private WSDLPort wsdlModel;

   public MessageHandlerContextImpl(@Nullable SEIModel seiModel, WSBinding binding, @Nullable WSDLPort wsdlModel, Packet packet, Set<String> roles) {
      super(packet);
      this.seiModel = seiModel;
      this.binding = binding;
      this.wsdlModel = wsdlModel;
      this.roles = roles;
   }

   public Message getMessage() {
      return this.packet.getMessage();
   }

   public void setMessage(Message message) {
      this.packet.setMessage(message);
   }

   public Set<String> getRoles() {
      return this.roles;
   }

   public WSBinding getWSBinding() {
      return this.binding;
   }

   @Nullable
   public SEIModel getSEIModel() {
      return this.seiModel;
   }

   @Nullable
   public WSDLPort getPort() {
      return this.wsdlModel;
   }

   void updateMessage() {
   }

   void setPacketMessage(Message newMessage) {
      this.setMessage(newMessage);
   }
}
