package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class UtilMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.util");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableUTIL_LOCATION(Object arg0, Object arg1) {
      return messageFactory.getMessage("util.location", arg0, arg1);
   }

   public static String UTIL_LOCATION(Object arg0, Object arg1) {
      return localizer.localize(localizableUTIL_LOCATION(arg0, arg1));
   }

   public static Localizable localizableUTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(Object arg0, Object arg1) {
      return messageFactory.getMessage("util.failed.to.parse.handlerchain.file", arg0, arg1);
   }

   public static String UTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(Object arg0, Object arg1) {
      return localizer.localize(localizableUTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(arg0, arg1));
   }

   public static Localizable localizableUTIL_PARSER_WRONG_ELEMENT(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("util.parser.wrong.element", arg0, arg1, arg2);
   }

   public static String UTIL_PARSER_WRONG_ELEMENT(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableUTIL_PARSER_WRONG_ELEMENT(arg0, arg1, arg2));
   }

   public static Localizable localizableUTIL_HANDLER_CLASS_NOT_FOUND(Object arg0) {
      return messageFactory.getMessage("util.handler.class.not.found", arg0);
   }

   public static String UTIL_HANDLER_CLASS_NOT_FOUND(Object arg0) {
      return localizer.localize(localizableUTIL_HANDLER_CLASS_NOT_FOUND(arg0));
   }

   public static Localizable localizableUTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(Object arg0) {
      return messageFactory.getMessage("util.handler.endpoint.interface.no.webservice", arg0);
   }

   public static String UTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(Object arg0) {
      return localizer.localize(localizableUTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(arg0));
   }

   public static Localizable localizableUTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(Object arg0) {
      return messageFactory.getMessage("util.handler.no.webservice.annotation", arg0);
   }

   public static String UTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(Object arg0) {
      return localizer.localize(localizableUTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(arg0));
   }

   public static Localizable localizableUTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(Object arg0, Object arg1) {
      return messageFactory.getMessage("util.failed.to.find.handlerchain.file", arg0, arg1);
   }

   public static String UTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(Object arg0, Object arg1) {
      return localizer.localize(localizableUTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(arg0, arg1));
   }

   public static Localizable localizableUTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS() {
      return messageFactory.getMessage("util.handler.cannot.combine.soapmessagehandlers");
   }

   public static String UTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS() {
      return localizer.localize(localizableUTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS());
   }
}
