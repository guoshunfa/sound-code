package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class HttpserverMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.httpserver");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableUNEXPECTED_HTTP_METHOD(Object arg0) {
      return messageFactory.getMessage("unexpected.http.method", arg0);
   }

   public static String UNEXPECTED_HTTP_METHOD(Object arg0) {
      return localizer.localize(localizableUNEXPECTED_HTTP_METHOD(arg0));
   }
}
