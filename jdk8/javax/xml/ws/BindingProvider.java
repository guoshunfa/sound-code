package javax.xml.ws;

import java.util.Map;

public interface BindingProvider {
   String USERNAME_PROPERTY = "javax.xml.ws.security.auth.username";
   String PASSWORD_PROPERTY = "javax.xml.ws.security.auth.password";
   String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.ws.service.endpoint.address";
   String SESSION_MAINTAIN_PROPERTY = "javax.xml.ws.session.maintain";
   String SOAPACTION_USE_PROPERTY = "javax.xml.ws.soap.http.soapaction.use";
   String SOAPACTION_URI_PROPERTY = "javax.xml.ws.soap.http.soapaction.uri";

   Map<String, Object> getRequestContext();

   Map<String, Object> getResponseContext();

   Binding getBinding();

   EndpointReference getEndpointReference();

   <T extends EndpointReference> T getEndpointReference(Class<T> var1);
}
