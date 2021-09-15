package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.handler.MessageContext;

class MessageContextImpl implements MessageContext {
   private final Set<String> handlerScopeProps;
   private final Packet packet;
   private final Map<String, Object> asMapIncludingInvocationProperties;

   public MessageContextImpl(Packet packet) {
      this.packet = packet;
      this.asMapIncludingInvocationProperties = packet.asMapIncludingInvocationProperties();
      this.handlerScopeProps = packet.getHandlerScopePropertyNames(false);
   }

   protected void updatePacket() {
      throw new UnsupportedOperationException("wrong call");
   }

   public void setScope(String name, MessageContext.Scope scope) {
      if (!this.containsKey(name)) {
         throw new IllegalArgumentException("Property " + name + " does not exist.");
      } else {
         if (scope == MessageContext.Scope.APPLICATION) {
            this.handlerScopeProps.remove(name);
         } else {
            this.handlerScopeProps.add(name);
         }

      }
   }

   public MessageContext.Scope getScope(String name) {
      if (!this.containsKey(name)) {
         throw new IllegalArgumentException("Property " + name + " does not exist.");
      } else {
         return this.handlerScopeProps.contains(name) ? MessageContext.Scope.HANDLER : MessageContext.Scope.APPLICATION;
      }
   }

   public int size() {
      return this.asMapIncludingInvocationProperties.size();
   }

   public boolean isEmpty() {
      return this.asMapIncludingInvocationProperties.isEmpty();
   }

   public boolean containsKey(Object key) {
      return this.asMapIncludingInvocationProperties.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.asMapIncludingInvocationProperties.containsValue(value);
   }

   public Object put(String key, Object value) {
      if (!this.asMapIncludingInvocationProperties.containsKey(key)) {
         this.handlerScopeProps.add(key);
      }

      return this.asMapIncludingInvocationProperties.put(key, value);
   }

   public Object get(Object key) {
      if (key == null) {
         return null;
      } else {
         Object value = this.asMapIncludingInvocationProperties.get(key);
         if (!key.equals("javax.xml.ws.binding.attachments.outbound") && !key.equals("javax.xml.ws.binding.attachments.inbound")) {
            return value;
         } else {
            Map<String, DataHandler> atts = (Map)value;
            if (atts == null) {
               atts = new HashMap();
            }

            AttachmentSet attSet = this.packet.getMessage().getAttachments();
            Iterator var5 = attSet.iterator();

            while(var5.hasNext()) {
               Attachment att = (Attachment)var5.next();
               String cid = att.getContentId();
               if (cid.indexOf("@jaxws.sun.com") == -1) {
                  Object a = ((Map)atts).get(cid);
                  if (a == null) {
                     a = ((Map)atts).get("<" + cid + ">");
                     if (a == null) {
                        ((Map)atts).put(att.getContentId(), att.asDataHandler());
                     }
                  }
               } else {
                  ((Map)atts).put(att.getContentId(), att.asDataHandler());
               }
            }

            return atts;
         }
      }
   }

   public void putAll(Map<? extends String, ? extends Object> t) {
      Iterator var2 = t.keySet().iterator();

      while(var2.hasNext()) {
         String key = (String)var2.next();
         if (!this.asMapIncludingInvocationProperties.containsKey(key)) {
            this.handlerScopeProps.add(key);
         }
      }

      this.asMapIncludingInvocationProperties.putAll(t);
   }

   public void clear() {
      this.asMapIncludingInvocationProperties.clear();
   }

   public Object remove(Object key) {
      this.handlerScopeProps.remove(key);
      return this.asMapIncludingInvocationProperties.remove(key);
   }

   public Set<String> keySet() {
      return this.asMapIncludingInvocationProperties.keySet();
   }

   public Set<Map.Entry<String, Object>> entrySet() {
      return this.asMapIncludingInvocationProperties.entrySet();
   }

   public Collection<Object> values() {
      return this.asMapIncludingInvocationProperties.values();
   }
}
