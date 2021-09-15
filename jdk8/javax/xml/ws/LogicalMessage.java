package javax.xml.ws;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;

public interface LogicalMessage {
   Source getPayload();

   void setPayload(Source var1);

   Object getPayload(JAXBContext var1);

   void setPayload(Object var1, JAXBContext var2);
}
