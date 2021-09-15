package javax.xml.ws.soap;

import java.util.Set;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.Binding;

public interface SOAPBinding extends Binding {
   String SOAP11HTTP_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http";
   String SOAP12HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
   String SOAP11HTTP_MTOM_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
   String SOAP12HTTP_MTOM_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";

   Set<String> getRoles();

   void setRoles(Set<String> var1);

   boolean isMTOMEnabled();

   void setMTOMEnabled(boolean var1);

   SOAPFactory getSOAPFactory();

   MessageFactory getMessageFactory();
}
