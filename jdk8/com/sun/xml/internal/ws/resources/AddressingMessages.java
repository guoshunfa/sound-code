package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class AddressingMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.addressing");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableNON_ANONYMOUS_RESPONSE_ONEWAY() {
      return messageFactory.getMessage("nonAnonymous.response.oneway");
   }

   public static String NON_ANONYMOUS_RESPONSE_ONEWAY() {
      return localizer.localize(localizableNON_ANONYMOUS_RESPONSE_ONEWAY());
   }

   public static Localizable localizableNULL_WSA_HEADERS() {
      return messageFactory.getMessage("null.wsa.headers");
   }

   public static String NULL_WSA_HEADERS() {
      return localizer.localize(localizableNULL_WSA_HEADERS());
   }

   public static Localizable localizableUNKNOWN_WSA_HEADER() {
      return messageFactory.getMessage("unknown.wsa.header");
   }

   public static String UNKNOWN_WSA_HEADER() {
      return localizer.localize(localizableUNKNOWN_WSA_HEADER());
   }

   public static Localizable localizableNULL_ACTION() {
      return messageFactory.getMessage("null.action");
   }

   public static String NULL_ACTION() {
      return localizer.localize(localizableNULL_ACTION());
   }

   public static Localizable localizableINVALID_WSAW_ANONYMOUS(Object arg0) {
      return messageFactory.getMessage("invalid.wsaw.anonymous", arg0);
   }

   public static String INVALID_WSAW_ANONYMOUS(Object arg0) {
      return localizer.localize(localizableINVALID_WSAW_ANONYMOUS(arg0));
   }

   public static Localizable localizableNULL_SOAP_VERSION() {
      return messageFactory.getMessage("null.soap.version");
   }

   public static String NULL_SOAP_VERSION() {
      return localizer.localize(localizableNULL_SOAP_VERSION());
   }

   public static Localizable localizableWSDL_BOUND_OPERATION_NOT_FOUND(Object arg0) {
      return messageFactory.getMessage("wsdlBoundOperation.notFound", arg0);
   }

   public static String WSDL_BOUND_OPERATION_NOT_FOUND(Object arg0) {
      return localizer.localize(localizableWSDL_BOUND_OPERATION_NOT_FOUND(arg0));
   }

   public static Localizable localizableNON_UNIQUE_OPERATION_SIGNATURE(Object arg0, Object arg1, Object arg2, Object arg3) {
      return messageFactory.getMessage("non.unique.operation.signature", arg0, arg1, arg2, arg3);
   }

   public static String NON_UNIQUE_OPERATION_SIGNATURE(Object arg0, Object arg1, Object arg2, Object arg3) {
      return localizer.localize(localizableNON_UNIQUE_OPERATION_SIGNATURE(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizableNON_ANONYMOUS_RESPONSE() {
      return messageFactory.getMessage("nonAnonymous.response");
   }

   public static String NON_ANONYMOUS_RESPONSE() {
      return localizer.localize(localizableNON_ANONYMOUS_RESPONSE());
   }

   public static Localizable localizableVALIDATION_SERVER_NULL_ACTION() {
      return messageFactory.getMessage("validation.server.nullAction");
   }

   public static String VALIDATION_SERVER_NULL_ACTION() {
      return localizer.localize(localizableVALIDATION_SERVER_NULL_ACTION());
   }

   public static Localizable localizableFAULT_TO_CANNOT_PARSE() {
      return messageFactory.getMessage("faultTo.cannot.parse");
   }

   public static String FAULT_TO_CANNOT_PARSE() {
      return localizer.localize(localizableFAULT_TO_CANNOT_PARSE());
   }

   public static Localizable localizableVALIDATION_CLIENT_NULL_ACTION() {
      return messageFactory.getMessage("validation.client.nullAction");
   }

   public static String VALIDATION_CLIENT_NULL_ACTION() {
      return localizer.localize(localizableVALIDATION_CLIENT_NULL_ACTION());
   }

   public static Localizable localizableNULL_MESSAGE() {
      return messageFactory.getMessage("null.message");
   }

   public static String NULL_MESSAGE() {
      return localizer.localize(localizableNULL_MESSAGE());
   }

   public static Localizable localizableACTION_NOT_SUPPORTED_EXCEPTION(Object arg0) {
      return messageFactory.getMessage("action.not.supported.exception", arg0);
   }

   public static String ACTION_NOT_SUPPORTED_EXCEPTION(Object arg0) {
      return localizer.localize(localizableACTION_NOT_SUPPORTED_EXCEPTION(arg0));
   }

   public static Localizable localizableNON_ANONYMOUS_RESPONSE_NULL_HEADERS(Object arg0) {
      return messageFactory.getMessage("nonAnonymous.response.nullHeaders", arg0);
   }

   public static String NON_ANONYMOUS_RESPONSE_NULL_HEADERS(Object arg0) {
      return localizer.localize(localizableNON_ANONYMOUS_RESPONSE_NULL_HEADERS(arg0));
   }

   public static Localizable localizableNON_ANONYMOUS_RESPONSE_SENDING(Object arg0) {
      return messageFactory.getMessage("nonAnonymous.response.sending", arg0);
   }

   public static String NON_ANONYMOUS_RESPONSE_SENDING(Object arg0) {
      return localizer.localize(localizableNON_ANONYMOUS_RESPONSE_SENDING(arg0));
   }

   public static Localizable localizableREPLY_TO_CANNOT_PARSE() {
      return messageFactory.getMessage("replyTo.cannot.parse");
   }

   public static String REPLY_TO_CANNOT_PARSE() {
      return localizer.localize(localizableREPLY_TO_CANNOT_PARSE());
   }

   public static Localizable localizableINVALID_ADDRESSING_HEADER_EXCEPTION(Object arg0, Object arg1) {
      return messageFactory.getMessage("invalid.addressing.header.exception", arg0, arg1);
   }

   public static String INVALID_ADDRESSING_HEADER_EXCEPTION(Object arg0, Object arg1) {
      return localizer.localize(localizableINVALID_ADDRESSING_HEADER_EXCEPTION(arg0, arg1));
   }

   public static Localizable localizableWSAW_ANONYMOUS_PROHIBITED() {
      return messageFactory.getMessage("wsaw.anonymousProhibited");
   }

   public static String WSAW_ANONYMOUS_PROHIBITED() {
      return localizer.localize(localizableWSAW_ANONYMOUS_PROHIBITED());
   }

   public static Localizable localizableNULL_WSDL_PORT() {
      return messageFactory.getMessage("null.wsdlPort");
   }

   public static String NULL_WSDL_PORT() {
      return localizer.localize(localizableNULL_WSDL_PORT());
   }

   public static Localizable localizableADDRESSING_SHOULD_BE_ENABLED() {
      return messageFactory.getMessage("addressing.should.be.enabled.");
   }

   public static String ADDRESSING_SHOULD_BE_ENABLED() {
      return localizer.localize(localizableADDRESSING_SHOULD_BE_ENABLED());
   }

   public static Localizable localizableNULL_ADDRESSING_VERSION() {
      return messageFactory.getMessage("null.addressing.version");
   }

   public static String NULL_ADDRESSING_VERSION() {
      return localizer.localize(localizableNULL_ADDRESSING_VERSION());
   }

   public static Localizable localizableMISSING_HEADER_EXCEPTION(Object arg0) {
      return messageFactory.getMessage("missing.header.exception", arg0);
   }

   public static String MISSING_HEADER_EXCEPTION(Object arg0) {
      return localizer.localize(localizableMISSING_HEADER_EXCEPTION(arg0));
   }

   public static Localizable localizableNULL_PACKET() {
      return messageFactory.getMessage("null.packet");
   }

   public static String NULL_PACKET() {
      return localizer.localize(localizableNULL_PACKET());
   }

   public static Localizable localizableWRONG_ADDRESSING_VERSION(Object arg0, Object arg1) {
      return messageFactory.getMessage("wrong.addressing.version", arg0, arg1);
   }

   public static String WRONG_ADDRESSING_VERSION(Object arg0, Object arg1) {
      return localizer.localize(localizableWRONG_ADDRESSING_VERSION(arg0, arg1));
   }

   public static Localizable localizableADDRESSING_NOT_ENABLED(Object arg0) {
      return messageFactory.getMessage("addressing.notEnabled", arg0);
   }

   public static String ADDRESSING_NOT_ENABLED(Object arg0) {
      return localizer.localize(localizableADDRESSING_NOT_ENABLED(arg0));
   }

   public static Localizable localizableNON_ANONYMOUS_UNKNOWN_PROTOCOL(Object arg0) {
      return messageFactory.getMessage("nonAnonymous.unknown.protocol", arg0);
   }

   public static String NON_ANONYMOUS_UNKNOWN_PROTOCOL(Object arg0) {
      return localizer.localize(localizableNON_ANONYMOUS_UNKNOWN_PROTOCOL(arg0));
   }

   public static Localizable localizableNULL_HEADERS() {
      return messageFactory.getMessage("null.headers");
   }

   public static String NULL_HEADERS() {
      return localizer.localize(localizableNULL_HEADERS());
   }

   public static Localizable localizableNULL_BINDING() {
      return messageFactory.getMessage("null.binding");
   }

   public static String NULL_BINDING() {
      return localizer.localize(localizableNULL_BINDING());
   }
}
