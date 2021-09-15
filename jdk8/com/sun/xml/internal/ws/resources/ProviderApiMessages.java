package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class ProviderApiMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.providerApi");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableNULL_ADDRESS_SERVICE_ENDPOINT() {
      return messageFactory.getMessage("null.address.service.endpoint");
   }

   public static String NULL_ADDRESS_SERVICE_ENDPOINT() {
      return localizer.localize(localizableNULL_ADDRESS_SERVICE_ENDPOINT());
   }

   public static Localizable localizableNO_WSDL_NO_PORT(Object arg0) {
      return messageFactory.getMessage("no.wsdl.no.port", arg0);
   }

   public static String NO_WSDL_NO_PORT(Object arg0) {
      return localizer.localize(localizableNO_WSDL_NO_PORT(arg0));
   }

   public static Localizable localizableNULL_SERVICE() {
      return messageFactory.getMessage("null.service");
   }

   public static String NULL_SERVICE() {
      return localizer.localize(localizableNULL_SERVICE());
   }

   public static Localizable localizableNULL_ADDRESS() {
      return messageFactory.getMessage("null.address");
   }

   public static String NULL_ADDRESS() {
      return localizer.localize(localizableNULL_ADDRESS());
   }

   public static Localizable localizableNULL_PORTNAME() {
      return messageFactory.getMessage("null.portname");
   }

   public static String NULL_PORTNAME() {
      return localizer.localize(localizableNULL_PORTNAME());
   }

   public static Localizable localizableNOTFOUND_SERVICE_IN_WSDL(Object arg0, Object arg1) {
      return messageFactory.getMessage("notfound.service.in.wsdl", arg0, arg1);
   }

   public static String NOTFOUND_SERVICE_IN_WSDL(Object arg0, Object arg1) {
      return localizer.localize(localizableNOTFOUND_SERVICE_IN_WSDL(arg0, arg1));
   }

   public static Localizable localizableNULL_EPR() {
      return messageFactory.getMessage("null.epr");
   }

   public static String NULL_EPR() {
      return localizer.localize(localizableNULL_EPR());
   }

   public static Localizable localizableNULL_WSDL() {
      return messageFactory.getMessage("null.wsdl");
   }

   public static String NULL_WSDL() {
      return localizer.localize(localizableNULL_WSDL());
   }

   public static Localizable localizableNOTFOUND_PORT_IN_WSDL(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("notfound.port.in.wsdl", arg0, arg1, arg2);
   }

   public static String NOTFOUND_PORT_IN_WSDL(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableNOTFOUND_PORT_IN_WSDL(arg0, arg1, arg2));
   }

   public static Localizable localizableERROR_WSDL(Object arg0) {
      return messageFactory.getMessage("error.wsdl", arg0);
   }

   public static String ERROR_WSDL(Object arg0) {
      return localizer.localize(localizableERROR_WSDL(arg0));
   }
}
