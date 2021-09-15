package com.sun.xml.internal.ws.client;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.PropertySet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.transport.Headers;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class RequestContext extends BaseDistributedPropertySet {
   private static final Logger LOGGER = Logger.getLogger(RequestContext.class.getName());
   private static ContentNegotiation defaultContentNegotiation = ContentNegotiation.obtainFromSystemProperty();
   @NotNull
   private EndpointAddress endpointAddress;
   public ContentNegotiation contentNegotiation;
   private String soapAction;
   private Boolean soapActionUse;
   private static final BasePropertySet.PropertyMap propMap = parse(RequestContext.class);

   /** @deprecated */
   public void addSatellite(@NotNull PropertySet satellite) {
      super.addSatellite(satellite);
   }

   /** @deprecated */
   @com.oracle.webservices.internal.api.message.PropertySet.Property({"javax.xml.ws.service.endpoint.address"})
   public String getEndPointAddressString() {
      return this.endpointAddress != null ? this.endpointAddress.toString() : null;
   }

   public void setEndPointAddressString(String s) {
      if (s == null) {
         throw new IllegalArgumentException();
      } else {
         this.endpointAddress = EndpointAddress.create(s);
      }
   }

   public void setEndpointAddress(@NotNull EndpointAddress epa) {
      this.endpointAddress = epa;
   }

   @NotNull
   public EndpointAddress getEndpointAddress() {
      return this.endpointAddress;
   }

   @com.oracle.webservices.internal.api.message.PropertySet.Property({"com.sun.xml.internal.ws.client.ContentNegotiation"})
   public String getContentNegotiationString() {
      return this.contentNegotiation.toString();
   }

   public void setContentNegotiationString(String s) {
      if (s == null) {
         this.contentNegotiation = ContentNegotiation.none;
      } else {
         try {
            this.contentNegotiation = ContentNegotiation.valueOf(s);
         } catch (IllegalArgumentException var3) {
            this.contentNegotiation = ContentNegotiation.none;
         }
      }

   }

   @com.oracle.webservices.internal.api.message.PropertySet.Property({"javax.xml.ws.soap.http.soapaction.uri"})
   public String getSoapAction() {
      return this.soapAction;
   }

   public void setSoapAction(String sAction) {
      this.soapAction = sAction;
   }

   @com.oracle.webservices.internal.api.message.PropertySet.Property({"javax.xml.ws.soap.http.soapaction.use"})
   public Boolean getSoapActionUse() {
      return this.soapActionUse;
   }

   public void setSoapActionUse(Boolean sActionUse) {
      this.soapActionUse = sActionUse;
   }

   RequestContext() {
      this.contentNegotiation = defaultContentNegotiation;
   }

   private RequestContext(RequestContext that) {
      this.contentNegotiation = defaultContentNegotiation;
      Iterator var2 = that.asMapLocal().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, Object> entry = (Map.Entry)var2.next();
         if (!propMap.containsKey(entry.getKey())) {
            this.asMap().put(entry.getKey(), entry.getValue());
         }
      }

      this.endpointAddress = that.endpointAddress;
      this.soapAction = that.soapAction;
      this.soapActionUse = that.soapActionUse;
      this.contentNegotiation = that.contentNegotiation;
      that.copySatelliteInto(this);
   }

   public Object get(Object key) {
      return this.supports(key) ? super.get(key) : this.asMap().get(key);
   }

   public Object put(String key, Object value) {
      return this.supports(key) ? super.put(key, value) : this.asMap().put(key, value);
   }

   public void fill(Packet packet, boolean isAddressingEnabled) {
      if (this.endpointAddress != null) {
         packet.endpointAddress = this.endpointAddress;
      }

      packet.contentNegotiation = this.contentNegotiation;
      this.fillSOAPAction(packet, isAddressingEnabled);
      this.mergeRequestHeaders(packet);
      Set<String> handlerScopeNames = new HashSet();
      this.copySatelliteInto(packet);
      Iterator var4 = this.asMapLocal().keySet().iterator();

      while(var4.hasNext()) {
         String key = (String)var4.next();
         if (!this.supportsLocal(key)) {
            handlerScopeNames.add(key);
         }

         if (!propMap.containsKey(key)) {
            Object value = this.asMapLocal().get(key);
            if (packet.supports(key)) {
               packet.put(key, value);
            } else {
               packet.invocationProperties.put(key, value);
            }
         }
      }

      if (!handlerScopeNames.isEmpty()) {
         packet.getHandlerScopePropertyNames(false).addAll(handlerScopeNames);
      }

   }

   private void mergeRequestHeaders(Packet packet) {
      Headers packetHeaders = (Headers)packet.invocationProperties.get("javax.xml.ws.http.request.headers");
      Map<String, List<String>> myHeaders = (Map)this.asMap().get("javax.xml.ws.http.request.headers");
      if (packetHeaders != null && myHeaders != null) {
         Iterator var4 = myHeaders.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)var4.next();
            String key = (String)entry.getKey();
            if (key != null && key.trim().length() != 0) {
               List<String> listFromPacket = (List)packetHeaders.get(key);
               if (listFromPacket != null) {
                  listFromPacket.addAll((Collection)entry.getValue());
               } else {
                  packetHeaders.put(key, myHeaders.get(key));
               }
            }
         }

         this.asMap().put("javax.xml.ws.http.request.headers", packetHeaders);
      }

   }

   private void fillSOAPAction(Packet packet, boolean isAddressingEnabled) {
      boolean p = packet.packetTakesPriorityOverRequestContext;
      String localSoapAction = p ? packet.soapAction : this.soapAction;
      Boolean localSoapActionUse = p ? (Boolean)packet.invocationProperties.get("javax.xml.ws.soap.http.soapaction.use") : this.soapActionUse;
      if ((localSoapActionUse != null && localSoapActionUse || localSoapActionUse == null && isAddressingEnabled) && localSoapAction != null) {
         packet.soapAction = localSoapAction;
      }

      if (!isAddressingEnabled && (localSoapActionUse == null || !localSoapActionUse) && localSoapAction != null) {
         LOGGER.warning("BindingProvider.SOAPACTION_URI_PROPERTY is set in the RequestContext but is ineffective, Either set BindingProvider.SOAPACTION_USE_PROPERTY to true or enable AddressingFeature");
      }

   }

   public RequestContext copy() {
      return new RequestContext(this);
   }

   protected BasePropertySet.PropertyMap getPropertyMap() {
      return propMap;
   }

   protected boolean mapAllowsAdditionalProperties() {
      return true;
   }
}
