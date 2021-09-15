package javax.xml.ws;

import java.security.Principal;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.Element;

public interface WebServiceContext {
   MessageContext getMessageContext();

   Principal getUserPrincipal();

   boolean isUserInRole(String var1);

   EndpointReference getEndpointReference(Element... var1);

   <T extends EndpointReference> T getEndpointReference(Class<T> var1, Element... var2);
}
