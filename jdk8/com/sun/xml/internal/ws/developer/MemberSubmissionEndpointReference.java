package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

@XmlRootElement(
   name = "EndpointReference",
   namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
)
@XmlType(
   name = "EndpointReferenceType",
   namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
)
public final class MemberSubmissionEndpointReference extends EndpointReference implements MemberSubmissionAddressingConstants {
   private static final ContextClassloaderLocal<JAXBContext> msjc = new ContextClassloaderLocal<JAXBContext>() {
      protected JAXBContext initialValue() throws Exception {
         return MemberSubmissionEndpointReference.getMSJaxbContext();
      }
   };
   @XmlElement(
      name = "Address",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   public MemberSubmissionEndpointReference.Address addr;
   @XmlElement(
      name = "ReferenceProperties",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   public MemberSubmissionEndpointReference.Elements referenceProperties;
   @XmlElement(
      name = "ReferenceParameters",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   public MemberSubmissionEndpointReference.Elements referenceParameters;
   @XmlElement(
      name = "PortType",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   public MemberSubmissionEndpointReference.AttributedQName portTypeName;
   @XmlElement(
      name = "ServiceName",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   public MemberSubmissionEndpointReference.ServiceNameType serviceName;
   @XmlAnyAttribute
   public Map<QName, String> attributes;
   @XmlAnyElement
   public List<Element> elements;
   protected static final String MSNS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";

   public MemberSubmissionEndpointReference() {
   }

   public MemberSubmissionEndpointReference(@NotNull Source source) {
      if (source == null) {
         throw new WebServiceException("Source parameter can not be null on constructor");
      } else {
         try {
            Unmarshaller unmarshaller = ((JAXBContext)msjc.get()).createUnmarshaller();
            MemberSubmissionEndpointReference epr = (MemberSubmissionEndpointReference)unmarshaller.unmarshal(source, MemberSubmissionEndpointReference.class).getValue();
            this.addr = epr.addr;
            this.referenceProperties = epr.referenceProperties;
            this.referenceParameters = epr.referenceParameters;
            this.portTypeName = epr.portTypeName;
            this.serviceName = epr.serviceName;
            this.attributes = epr.attributes;
            this.elements = epr.elements;
         } catch (JAXBException var4) {
            throw new WebServiceException("Error unmarshalling MemberSubmissionEndpointReference ", var4);
         } catch (ClassCastException var5) {
            throw new WebServiceException("Source did not contain MemberSubmissionEndpointReference", var5);
         }
      }
   }

   public void writeTo(Result result) {
      try {
         Marshaller marshaller = ((JAXBContext)msjc.get()).createMarshaller();
         marshaller.marshal(this, (Result)result);
      } catch (JAXBException var3) {
         throw new WebServiceException("Error marshalling W3CEndpointReference. ", var3);
      }
   }

   public Source toWSDLSource() {
      Element wsdlElement = null;
      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         Element elem = (Element)var2.next();
         if (elem.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && elem.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
            wsdlElement = elem;
         }
      }

      return new DOMSource(wsdlElement);
   }

   private static JAXBContext getMSJaxbContext() {
      try {
         return JAXBContext.newInstance(MemberSubmissionEndpointReference.class);
      } catch (JAXBException var1) {
         throw new WebServiceException("Error creating JAXBContext for MemberSubmissionEndpointReference. ", var1);
      }
   }

   public static class ServiceNameType extends MemberSubmissionEndpointReference.AttributedQName {
      @XmlAttribute(
         name = "PortName"
      )
      public String portName;
   }

   public static class AttributedQName {
      @XmlValue
      public QName name;
      @XmlAnyAttribute
      public Map<QName, String> attributes;
   }

   @XmlType(
      name = "elements",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   public static class Elements {
      @XmlAnyElement
      public List<Element> elements;
   }

   @XmlType(
      name = "address",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   public static class Address {
      @XmlValue
      public String uri;
      @XmlAnyAttribute
      public Map<QName, String> attributes;
   }
}
