package javax.xml.ws;

import java.io.StringWriter;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;

@XmlTransient
public abstract class EndpointReference {
   protected EndpointReference() {
   }

   public static EndpointReference readFrom(Source eprInfoset) {
      return javax.xml.ws.spi.Provider.provider().readEndpointReference(eprInfoset);
   }

   public abstract void writeTo(Result var1);

   public <T> T getPort(Class<T> serviceEndpointInterface, WebServiceFeature... features) {
      return javax.xml.ws.spi.Provider.provider().getPort(this, serviceEndpointInterface, features);
   }

   public String toString() {
      StringWriter w = new StringWriter();
      this.writeTo(new StreamResult(w));
      return w.toString();
   }
}
