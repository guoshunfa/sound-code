package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class SenderMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.sender");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(Object arg0) {
      return messageFactory.getMessage("sender.request.illegalValueForContentNegotiation", arg0);
   }

   public static String SENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(Object arg0) {
      return localizer.localize(localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(arg0));
   }

   public static Localizable localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() {
      return messageFactory.getMessage("sender.response.cannotDecodeFaultDetail");
   }

   public static String SENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() {
      return localizer.localize(localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL());
   }

   public static Localizable localizableSENDER_NESTED_ERROR(Object arg0) {
      return messageFactory.getMessage("sender.nestedError", arg0);
   }

   public static String SENDER_NESTED_ERROR(Object arg0) {
      return localizer.localize(localizableSENDER_NESTED_ERROR(arg0));
   }

   public static Localizable localizableSENDER_REQUEST_MESSAGE_NOT_READY() {
      return messageFactory.getMessage("sender.request.messageNotReady");
   }

   public static String SENDER_REQUEST_MESSAGE_NOT_READY() {
      return localizer.localize(localizableSENDER_REQUEST_MESSAGE_NOT_READY());
   }
}
