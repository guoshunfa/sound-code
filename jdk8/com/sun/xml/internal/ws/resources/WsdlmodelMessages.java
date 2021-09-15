package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class WsdlmodelMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.wsdlmodel");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdl.portaddress.epraddress.not.match", arg0, arg1, arg2);
   }

   public static String WSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDL_IMPORT_SHOULD_BE_WSDL(Object arg0) {
      return messageFactory.getMessage("wsdl.import.should.be.wsdl", arg0);
   }

   public static String WSDL_IMPORT_SHOULD_BE_WSDL(Object arg0) {
      return localizer.localize(localizableWSDL_IMPORT_SHOULD_BE_WSDL(arg0));
   }

   public static Localizable localizableMEX_METADATA_SYSTEMID_NULL() {
      return messageFactory.getMessage("Mex.metadata.systemid.null");
   }

   public static String MEX_METADATA_SYSTEMID_NULL() {
      return localizer.localize(localizableMEX_METADATA_SYSTEMID_NULL());
   }
}
