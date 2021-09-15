package javax.xml.ws.handler;

import javax.xml.namespace.QName;

public interface PortInfo {
   QName getServiceName();

   QName getPortName();

   String getBindingID();
}
