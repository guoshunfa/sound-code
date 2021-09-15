package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class HandlerMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.handler");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableHANDLER_MESSAGE_CONTEXT_INVALID_CLASS(Object arg0, Object arg1) {
      return messageFactory.getMessage("handler.messageContext.invalid.class", arg0, arg1);
   }

   public static String HANDLER_MESSAGE_CONTEXT_INVALID_CLASS(Object arg0, Object arg1) {
      return localizer.localize(localizableHANDLER_MESSAGE_CONTEXT_INVALID_CLASS(arg0, arg1));
   }

   public static Localizable localizableCANNOT_EXTEND_HANDLER_DIRECTLY(Object arg0) {
      return messageFactory.getMessage("cannot.extend.handler.directly", arg0);
   }

   public static String CANNOT_EXTEND_HANDLER_DIRECTLY(Object arg0) {
      return localizer.localize(localizableCANNOT_EXTEND_HANDLER_DIRECTLY(arg0));
   }

   public static Localizable localizableHANDLER_NOT_VALID_TYPE(Object arg0) {
      return messageFactory.getMessage("handler.not.valid.type", arg0);
   }

   public static String HANDLER_NOT_VALID_TYPE(Object arg0) {
      return localizer.localize(localizableHANDLER_NOT_VALID_TYPE(arg0));
   }

   public static Localizable localizableCANNOT_INSTANTIATE_HANDLER(Object arg0, Object arg1) {
      return messageFactory.getMessage("cannot.instantiate.handler", arg0, arg1);
   }

   public static String CANNOT_INSTANTIATE_HANDLER(Object arg0, Object arg1) {
      return localizer.localize(localizableCANNOT_INSTANTIATE_HANDLER(arg0, arg1));
   }

   public static Localizable localizableHANDLER_CHAIN_CONTAINS_HANDLER_ONLY(Object arg0) {
      return messageFactory.getMessage("handler.chain.contains.handler.only", arg0);
   }

   public static String HANDLER_CHAIN_CONTAINS_HANDLER_ONLY(Object arg0) {
      return localizer.localize(localizableHANDLER_CHAIN_CONTAINS_HANDLER_ONLY(arg0));
   }

   public static Localizable localizableHANDLER_NESTED_ERROR(Object arg0) {
      return messageFactory.getMessage("handler.nestedError", arg0);
   }

   public static String HANDLER_NESTED_ERROR(Object arg0) {
      return localizer.localize(localizableHANDLER_NESTED_ERROR(arg0));
   }

   public static Localizable localizableHANDLER_PREDESTROY_IGNORE(Object arg0) {
      return messageFactory.getMessage("handler.predestroy.ignore", arg0);
   }

   public static String HANDLER_PREDESTROY_IGNORE(Object arg0) {
      return localizer.localize(localizableHANDLER_PREDESTROY_IGNORE(arg0));
   }
}
