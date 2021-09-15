package com.sun.xml.internal.ws.client.dispatch;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.ThrowableInPacketCompletionFeature;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class PacketDispatch extends DispatchImpl<Packet> {
   private final boolean isDeliverThrowableInPacket;

   /** @deprecated */
   @Deprecated
   public PacketDispatch(QName port, WSServiceDelegate owner, Tube pipe, BindingImpl binding, @Nullable WSEndpointReference epr) {
      super(port, Service.Mode.MESSAGE, owner, pipe, binding, epr);
      this.isDeliverThrowableInPacket = this.calculateIsDeliverThrowableInPacket(binding);
   }

   public PacketDispatch(WSPortInfo portInfo, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
      this(portInfo, pipe, binding, epr, true);
   }

   public PacketDispatch(WSPortInfo portInfo, Tube pipe, BindingImpl binding, WSEndpointReference epr, boolean allowFaultResponseMsg) {
      super(portInfo, Service.Mode.MESSAGE, pipe, binding, epr, allowFaultResponseMsg);
      this.isDeliverThrowableInPacket = this.calculateIsDeliverThrowableInPacket(binding);
   }

   public PacketDispatch(WSPortInfo portInfo, BindingImpl binding, WSEndpointReference epr) {
      super(portInfo, Service.Mode.MESSAGE, binding, epr, true);
      this.isDeliverThrowableInPacket = this.calculateIsDeliverThrowableInPacket(binding);
   }

   private boolean calculateIsDeliverThrowableInPacket(BindingImpl binding) {
      return binding.isFeatureEnabled(ThrowableInPacketCompletionFeature.class);
   }

   protected void configureFiber(Fiber fiber) {
      fiber.setDeliverThrowableInPacket(this.isDeliverThrowableInPacket);
   }

   Packet toReturnValue(Packet response) {
      return response;
   }

   Packet createPacket(Packet request) {
      return request;
   }
}
