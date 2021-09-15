package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class EncodingMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.encoding");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableFAILED_TO_READ_RESPONSE(Object arg0) {
      return messageFactory.getMessage("failed.to.read.response", arg0);
   }

   public static String FAILED_TO_READ_RESPONSE(Object arg0) {
      return localizer.localize(localizableFAILED_TO_READ_RESPONSE(arg0));
   }

   public static Localizable localizableEXCEPTION_INCORRECT_TYPE(Object arg0) {
      return messageFactory.getMessage("exception.incorrectType", arg0);
   }

   public static String EXCEPTION_INCORRECT_TYPE(Object arg0) {
      return localizer.localize(localizableEXCEPTION_INCORRECT_TYPE(arg0));
   }

   public static Localizable localizableEXCEPTION_NOTFOUND(Object arg0) {
      return messageFactory.getMessage("exception.notfound", arg0);
   }

   public static String EXCEPTION_NOTFOUND(Object arg0) {
      return localizer.localize(localizableEXCEPTION_NOTFOUND(arg0));
   }

   public static Localizable localizableXSD_UNEXPECTED_ELEMENT_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("xsd.unexpectedElementName", arg0, arg1);
   }

   public static String XSD_UNEXPECTED_ELEMENT_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableXSD_UNEXPECTED_ELEMENT_NAME(arg0, arg1));
   }

   public static Localizable localizableNESTED_DESERIALIZATION_ERROR(Object arg0) {
      return messageFactory.getMessage("nestedDeserializationError", arg0);
   }

   public static String NESTED_DESERIALIZATION_ERROR(Object arg0) {
      return localizer.localize(localizableNESTED_DESERIALIZATION_ERROR(arg0));
   }

   public static Localizable localizableNESTED_ENCODING_ERROR(Object arg0) {
      return messageFactory.getMessage("nestedEncodingError", arg0);
   }

   public static String NESTED_ENCODING_ERROR(Object arg0) {
      return localizer.localize(localizableNESTED_ENCODING_ERROR(arg0));
   }

   public static Localizable localizableXSD_UNKNOWN_PREFIX(Object arg0) {
      return messageFactory.getMessage("xsd.unknownPrefix", arg0);
   }

   public static String XSD_UNKNOWN_PREFIX(Object arg0) {
      return localizer.localize(localizableXSD_UNKNOWN_PREFIX(arg0));
   }

   public static Localizable localizableNESTED_SERIALIZATION_ERROR(Object arg0) {
      return messageFactory.getMessage("nestedSerializationError", arg0);
   }

   public static String NESTED_SERIALIZATION_ERROR(Object arg0) {
      return localizer.localize(localizableNESTED_SERIALIZATION_ERROR(arg0));
   }

   public static Localizable localizableNO_SUCH_CONTENT_ID(Object arg0) {
      return messageFactory.getMessage("noSuchContentId", arg0);
   }

   public static String NO_SUCH_CONTENT_ID(Object arg0) {
      return localizer.localize(localizableNO_SUCH_CONTENT_ID(arg0));
   }
}
