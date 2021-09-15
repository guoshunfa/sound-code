package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;

public class ResponseContext extends AbstractMap<String, Object> {
   private final Packet packet;
   private Set<Map.Entry<String, Object>> entrySet;

   public ResponseContext(Packet packet) {
      this.packet = packet;
   }

   public boolean containsKey(Object key) {
      if (this.packet.supports(key)) {
         return this.packet.containsKey(key);
      } else if (this.packet.invocationProperties.containsKey(key)) {
         return !this.packet.getHandlerScopePropertyNames(true).contains(key);
      } else {
         return false;
      }
   }

   public Object get(Object key) {
      if (this.packet.supports(key)) {
         return this.packet.get(key);
      } else if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
         return null;
      } else {
         Object value = this.packet.invocationProperties.get(key);
         if (!key.equals("javax.xml.ws.binding.attachments.inbound")) {
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
               ((Map)atts).put(att.getContentId(), att.asDataHandler());
            }

            return atts;
         }
      }
   }

   public Object put(String key, Object value) {
      throw new UnsupportedOperationException();
   }

   public Object remove(Object key) {
      throw new UnsupportedOperationException();
   }

   public void putAll(Map<? extends String, ? extends Object> t) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Set<Map.Entry<String, Object>> entrySet() {
      if (this.entrySet == null) {
         Map<String, Object> r = new HashMap();
         r.putAll(this.packet.invocationProperties);
         r.keySet().removeAll(this.packet.getHandlerScopePropertyNames(true));
         r.putAll(this.packet.createMapView());
         this.entrySet = Collections.unmodifiableSet(r.entrySet());
      }

      return this.entrySet;
   }
}
