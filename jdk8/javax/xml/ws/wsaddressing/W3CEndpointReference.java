package javax.xml.ws.wsaddressing;

import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

@XmlRootElement(
   name = "EndpointReference",
   namespace = "http://www.w3.org/2005/08/addressing"
)
@XmlType(
   name = "EndpointReferenceType",
   namespace = "http://www.w3.org/2005/08/addressing"
)
public final class W3CEndpointReference extends EndpointReference {
   private final JAXBContext w3cjc = getW3CJaxbContext();
   protected static final String NS = "http://www.w3.org/2005/08/addressing";
   @XmlElement(
      name = "Address",
      namespace = "http://www.w3.org/2005/08/addressing"
   )
   private W3CEndpointReference.Address address;
   @XmlElement(
      name = "ReferenceParameters",
      namespace = "http://www.w3.org/2005/08/addressing"
   )
   private W3CEndpointReference.Elements referenceParameters;
   @XmlElement(
      name = "Metadata",
      namespace = "http://www.w3.org/2005/08/addressing"
   )
   private W3CEndpointReference.Elements metadata;
   @XmlAnyAttribute
   Map<QName, String> attributes;
   @XmlAnyElement
   List<Element> elements;

   protected W3CEndpointReference() {
   }

   public W3CEndpointReference(Source source) {
      try {
         W3CEndpointReference epr = (W3CEndpointReference)this.w3cjc.createUnmarshaller().unmarshal(source, W3CEndpointReference.class).getValue();
         this.address = epr.address;
         this.metadata = epr.metadata;
         this.referenceParameters = epr.referenceParameters;
         this.elements = epr.elements;
         this.attributes = epr.attributes;
      } catch (JAXBException var3) {
         throw new WebServiceException("Error unmarshalling W3CEndpointReference ", var3);
      } catch (ClassCastException var4) {
         throw new WebServiceException("Source did not contain W3CEndpointReference", var4);
      }
   }

   public void writeTo(Result result) {
      try {
         Marshaller marshaller = this.w3cjc.createMarshaller();
         marshaller.marshal(this, (Result)result);
      } catch (JAXBException var3) {
         throw new WebServiceException("Error marshalling W3CEndpointReference. ", var3);
      }
   }

   private static JAXBContext getW3CJaxbContext() {
      try {
         return JAXBContext.newInstance(W3CEndpointReference.class);
      } catch (JAXBException var1) {
         throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", var1);
      }
   }

   @XmlType(
      name = "elements",
      namespace = "http://www.w3.org/2005/08/addressing"
   )
   private static class Elements {
      @XmlAnyElement
      List<Element> elements;
      @XmlAnyAttribute
      Map<QName, String> attributes;

      protected Elements() {
      }
   }

   @XmlType(
      name = "address",
      namespace = "http://www.w3.org/2005/08/addressing"
   )
   private static class Address {
      @XmlValue
      String uri;
      @XmlAnyAttribute
      Map<QName, String> attributes;

      protected Address() {
      }
   }
}
