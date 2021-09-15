package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;

public interface SOAPHandler<T extends SOAPMessageContext> extends Handler<T> {
   Set<QName> getHeaders();
}
