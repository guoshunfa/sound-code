package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;

public abstract class MessageUpdatableContext implements MessageContext {
   final Packet packet;
   private MessageContextImpl ctxt;

   public MessageUpdatableContext(Packet packet) {
      this.ctxt = new MessageContextImpl(packet);
      this.packet = packet;
   }

   abstract void updateMessage();

   Message getPacketMessage() {
      this.updateMessage();
      return this.packet.getMessage();
   }

   abstract void setPacketMessage(Message var1);

   public final void updatePacket() {
      this.updateMessage();
   }

   MessageContextImpl getMessageContext() {
      return this.ctxt;
   }

   public void setScope(String name, MessageContext.Scope scope) {
      this.ctxt.setScope(name, scope);
   }

   public MessageContext.Scope getScope(String name) {
      return this.ctxt.getScope(name);
   }

   public void clear() {
      this.ctxt.clear();
   }

   public boolean containsKey(Object obj) {
      return this.ctxt.containsKey(obj);
   }

   public boolean containsValue(Object obj) {
      return this.ctxt.containsValue(obj);
   }

   public Set<Map.Entry<String, Object>> entrySet() {
      return this.ctxt.entrySet();
   }

   public Object get(Object obj) {
      return this.ctxt.get(obj);
   }

   public boolean isEmpty() {
      return this.ctxt.isEmpty();
   }

   public Set<String> keySet() {
      return this.ctxt.keySet();
   }

   public Object put(String str, Object obj) {
      return this.ctxt.put(str, obj);
   }

   public void putAll(Map<? extends String, ? extends Object> map) {
      this.ctxt.putAll(map);
   }

   public Object remove(Object obj) {
      return this.ctxt.remove(obj);
   }

   public int size() {
      return this.ctxt.size();
   }

   public Collection<Object> values() {
      return this.ctxt.values();
   }
}
