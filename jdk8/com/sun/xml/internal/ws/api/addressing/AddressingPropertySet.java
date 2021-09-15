package com.sun.xml.internal.ws.api.addressing;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet;

public class AddressingPropertySet extends BasePropertySet {
   public static final String ADDRESSING_FAULT_TO = "com.sun.xml.internal.ws.api.addressing.fault.to";
   private String faultTo;
   public static final String ADDRESSING_MESSAGE_ID = "com.sun.xml.internal.ws.api.addressing.message.id";
   private String messageId;
   public static final String ADDRESSING_RELATES_TO = "com.sun.xml.internal.ws.api.addressing.relates.to";
   @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.relates.to"})
   private String relatesTo;
   public static final String ADDRESSING_REPLY_TO = "com.sun.xml.internal.ws.api.addressing.reply.to";
   @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.reply.to"})
   private String replyTo;
   private static final BasePropertySet.PropertyMap model = parse(AddressingPropertySet.class);

   @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.fault.to"})
   public String getFaultTo() {
      return this.faultTo;
   }

   public void setFaultTo(String x) {
      this.faultTo = x;
   }

   public String getMessageId() {
      return this.messageId;
   }

   public void setMessageId(String x) {
      this.messageId = x;
   }

   public String getRelatesTo() {
      return this.relatesTo;
   }

   public void setRelatesTo(String x) {
      this.relatesTo = x;
   }

   public String getReplyTo() {
      return this.replyTo;
   }

   public void setReplyTo(String x) {
      this.replyTo = x;
   }

   protected BasePropertySet.PropertyMap getPropertyMap() {
      return model;
   }
}
