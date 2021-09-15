package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.handler.MessageContext;

public final class EndpointMessageContextImpl extends AbstractMap<String, Object> implements MessageContext {
   private Set<Map.Entry<String, Object>> entrySet;
   private final Packet packet;

   public EndpointMessageContextImpl(Packet packet) {
      this.packet = packet;
   }

   public Object get(Object key) {
      if (this.packet.supports(key)) {
         return this.packet.get(key);
      } else if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
         return null;
      } else {
         Object value = this.packet.invocationProperties.get(key);
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
               ((Map)atts).put(att.getContentId(), att.asDataHandler());
            }

            return atts;
         }
      }
   }

   public Object put(String key, Object value) {
      if (this.packet.supports(key)) {
         return this.packet.put(key, value);
      } else {
         Object old = this.packet.invocationProperties.get(key);
         if (old != null) {
            if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
               throw new IllegalArgumentException("Cannot overwrite property in HANDLER scope");
            } else {
               this.packet.invocationProperties.put(key, value);
               return old;
            }
         } else {
            this.packet.invocationProperties.put(key, value);
            return null;
         }
      }
   }

   public Object remove(Object key) {
      if (this.packet.supports(key)) {
         return this.packet.remove(key);
      } else {
         Object old = this.packet.invocationProperties.get(key);
         if (old != null) {
            if (this.packet.getHandlerScopePropertyNames(true).contains(key)) {
               throw new IllegalArgumentException("Cannot remove property in HANDLER scope");
            } else {
               this.packet.invocationProperties.remove(key);
               return old;
            }
         } else {
            return null;
         }
      }
   }

   public Set<Map.Entry<String, Object>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new EndpointMessageContextImpl.EntrySet();
      }

      return this.entrySet;
   }

   public void setScope(String name, MessageContext.Scope scope) {
      throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do setScope().");
   }

   public MessageContext.Scope getScope(String name) {
      throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do getScope().");
   }

   private Map<String, Object> createBackupMap() {
      Map<String, Object> backupMap = new HashMap();
      backupMap.putAll(this.packet.createMapView());
      Set<String> handlerProps = this.packet.getHandlerScopePropertyNames(true);
      Iterator var3 = this.packet.invocationProperties.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, Object> e = (Map.Entry)var3.next();
         if (!handlerProps.contains(e.getKey())) {
            backupMap.put(e.getKey(), e.getValue());
         }
      }

      return backupMap;
   }

   private class EntrySet extends AbstractSet<Map.Entry<String, Object>> {
      private EntrySet() {
      }

      public Iterator<Map.Entry<String, Object>> iterator() {
         final Iterator<Map.Entry<String, Object>> it = EndpointMessageContextImpl.this.createBackupMap().entrySet().iterator();
         return new Iterator<Map.Entry<String, Object>>() {
            Map.Entry<String, Object> cur;

            public boolean hasNext() {
               return it.hasNext();
            }

            public Map.Entry<String, Object> next() {
               this.cur = (Map.Entry)it.next();
               return this.cur;
            }

            public void remove() {
               it.remove();
               EndpointMessageContextImpl.this.remove(this.cur.getKey());
            }
         };
      }

      public int size() {
         return EndpointMessageContextImpl.this.createBackupMap().size();
      }

      // $FF: synthetic method
      EntrySet(Object x1) {
         this();
      }
   }
}
