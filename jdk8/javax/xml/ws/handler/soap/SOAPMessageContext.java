package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;

public interface SOAPMessageContext extends MessageContext {
   SOAPMessage getMessage();

   void setMessage(SOAPMessage var1);

   Object[] getHeaders(QName var1, JAXBContext var2, boolean var3);

   Set<String> getRoles();
}
