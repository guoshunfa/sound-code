package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.developer.JAXWSProperties;

public interface BindingProviderProperties extends JAXWSProperties {
   /** @deprecated */
   @Deprecated
   String HOSTNAME_VERIFICATION_PROPERTY = "com.sun.xml.internal.ws.client.http.HostnameVerificationProperty";
   String HTTP_COOKIE_JAR = "com.sun.xml.internal.ws.client.http.CookieJar";
   String REDIRECT_REQUEST_PROPERTY = "com.sun.xml.internal.ws.client.http.RedirectRequestProperty";
   String ONE_WAY_OPERATION = "com.sun.xml.internal.ws.server.OneWayOperation";
   String JAXWS_HANDLER_CONFIG = "com.sun.xml.internal.ws.handler.config";
   String JAXWS_CLIENT_HANDLE_PROPERTY = "com.sun.xml.internal.ws.client.handle";
}
