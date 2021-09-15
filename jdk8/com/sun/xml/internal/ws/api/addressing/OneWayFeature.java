package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.net.URL;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class OneWayFeature extends WebServiceFeature {
   public static final String ID = "http://java.sun.com/xml/ns/jaxws/addressing/oneway";
   private String messageId;
   private WSEndpointReference replyTo;
   private WSEndpointReference sslReplyTo;
   private WSEndpointReference from;
   private WSEndpointReference faultTo;
   private WSEndpointReference sslFaultTo;
   private String relatesToID;
   private boolean useAsyncWithSyncInvoke = false;

   public OneWayFeature() {
      this.enabled = true;
   }

   public OneWayFeature(boolean enabled) {
      this.enabled = enabled;
   }

   public OneWayFeature(boolean enabled, WSEndpointReference replyTo) {
      this.enabled = enabled;
      this.replyTo = replyTo;
   }

   @FeatureConstructor({"enabled", "replyTo", "from", "relatesTo"})
   public OneWayFeature(boolean enabled, WSEndpointReference replyTo, WSEndpointReference from, String relatesTo) {
      this.enabled = enabled;
      this.replyTo = replyTo;
      this.from = from;
      this.relatesToID = relatesTo;
   }

   public OneWayFeature(AddressingPropertySet a, AddressingVersion v) {
      this.enabled = true;
      this.messageId = a.getMessageId();
      this.relatesToID = a.getRelatesTo();
      this.replyTo = this.makeEPR(a.getReplyTo(), v);
      this.faultTo = this.makeEPR(a.getFaultTo(), v);
   }

   private WSEndpointReference makeEPR(String x, AddressingVersion v) {
      return x == null ? null : new WSEndpointReference(x, v);
   }

   public String getMessageId() {
      return this.messageId;
   }

   @ManagedAttribute
   public String getID() {
      return "http://java.sun.com/xml/ns/jaxws/addressing/oneway";
   }

   public boolean hasSslEprs() {
      return this.sslReplyTo != null || this.sslFaultTo != null;
   }

   @ManagedAttribute
   public WSEndpointReference getReplyTo() {
      return this.replyTo;
   }

   public WSEndpointReference getReplyTo(boolean ssl) {
      return ssl && this.sslReplyTo != null ? this.sslReplyTo : this.replyTo;
   }

   public void setReplyTo(WSEndpointReference address) {
      this.replyTo = address;
   }

   public WSEndpointReference getSslReplyTo() {
      return this.sslReplyTo;
   }

   public void setSslReplyTo(WSEndpointReference sslReplyTo) {
      this.sslReplyTo = sslReplyTo;
   }

   @ManagedAttribute
   public WSEndpointReference getFrom() {
      return this.from;
   }

   public void setFrom(WSEndpointReference address) {
      this.from = address;
   }

   @ManagedAttribute
   public String getRelatesToID() {
      return this.relatesToID;
   }

   public void setRelatesToID(String id) {
      this.relatesToID = id;
   }

   public WSEndpointReference getFaultTo() {
      return this.faultTo;
   }

   public WSEndpointReference getFaultTo(boolean ssl) {
      return ssl && this.sslFaultTo != null ? this.sslFaultTo : this.faultTo;
   }

   public void setFaultTo(WSEndpointReference address) {
      this.faultTo = address;
   }

   public WSEndpointReference getSslFaultTo() {
      return this.sslFaultTo;
   }

   public void setSslFaultTo(WSEndpointReference sslFaultTo) {
      this.sslFaultTo = sslFaultTo;
   }

   public boolean isUseAsyncWithSyncInvoke() {
      return this.useAsyncWithSyncInvoke;
   }

   public void setUseAsyncWithSyncInvoke(boolean useAsyncWithSyncInvoke) {
      this.useAsyncWithSyncInvoke = useAsyncWithSyncInvoke;
   }

   public static WSEndpointReference enableSslForEpr(@NotNull WSEndpointReference epr, @Nullable String sslHost, int sslPort) {
      if (!epr.isAnonymous()) {
         String address = epr.getAddress();

         URL url;
         try {
            url = new URL(address);
         } catch (Exception var10) {
            throw new RuntimeException(var10);
         }

         String protocol = url.getProtocol();
         if (!protocol.equalsIgnoreCase("https")) {
            protocol = "https";
            String host = url.getHost();
            if (sslHost != null) {
               host = sslHost;
            }

            int port = url.getPort();
            if (sslPort > 0) {
               port = sslPort;
            }

            try {
               url = new URL(protocol, host, port, url.getFile());
            } catch (Exception var9) {
               throw new RuntimeException(var9);
            }

            address = url.toExternalForm();
            return new WSEndpointReference(address, epr.getVersion());
         }
      }

      return epr;
   }
}
