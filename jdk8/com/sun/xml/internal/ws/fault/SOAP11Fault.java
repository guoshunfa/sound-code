package com.sun.xml.internal.ws.fault;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"faultcode", "faultstring", "faultactor", "detail"}
)
@XmlRootElement(
   name = "Fault",
   namespace = "http://schemas.xmlsoap.org/soap/envelope/"
)
class SOAP11Fault extends SOAPFaultBuilder {
   @XmlElement(
      namespace = ""
   )
   private QName faultcode;
   @XmlElement(
      namespace = ""
   )
   private String faultstring;
   @XmlElement(
      namespace = ""
   )
   private String faultactor;
   @XmlElement(
      namespace = ""
   )
   private DetailType detail;

   SOAP11Fault() {
   }

   SOAP11Fault(QName code, String reason, String actor, Element detailObject) {
      this.faultcode = code;
      this.faultstring = reason;
      this.faultactor = actor;
      if (detailObject != null) {
         if ((detailObject.getNamespaceURI() == null || "".equals(detailObject.getNamespaceURI())) && "detail".equals(detailObject.getLocalName())) {
            this.detail = new DetailType();
            Iterator var5 = DOMUtil.getChildElements(detailObject).iterator();

            while(var5.hasNext()) {
               Element detailEntry = (Element)var5.next();
               this.detail.getDetails().add(detailEntry);
            }
         } else {
            this.detail = new DetailType(detailObject);
         }
      }

   }

   SOAP11Fault(SOAPFault fault) {
      this.faultcode = fault.getFaultCodeAsQName();
      this.faultstring = fault.getFaultString();
      this.faultactor = fault.getFaultActor();
      if (fault.getDetail() != null) {
         this.detail = new DetailType();
         Iterator iter = fault.getDetail().getDetailEntries();

         while(iter.hasNext()) {
            Element fd = (Element)iter.next();
            this.detail.getDetails().add(fd);
         }
      }

   }

   QName getFaultcode() {
      return this.faultcode;
   }

   void setFaultcode(QName faultcode) {
      this.faultcode = faultcode;
   }

   String getFaultString() {
      return this.faultstring;
   }

   void setFaultstring(String faultstring) {
      this.faultstring = faultstring;
   }

   String getFaultactor() {
      return this.faultactor;
   }

   void setFaultactor(String faultactor) {
      this.faultactor = faultactor;
   }

   DetailType getDetail() {
      return this.detail;
   }

   void setDetail(DetailType detail) {
      this.detail = detail;
   }

   protected Throwable getProtocolException() {
      try {
         SOAPFault fault = SOAPVersion.SOAP_11.getSOAPFactory().createFault(this.faultstring, this.faultcode);
         fault.setFaultActor(this.faultactor);
         if (this.detail != null) {
            Detail d = fault.addDetail();
            Iterator var3 = this.detail.getDetails().iterator();

            while(var3.hasNext()) {
               Element det = (Element)var3.next();
               Node n = fault.getOwnerDocument().importNode(det, true);
               d.appendChild(n);
            }
         }

         return new ServerSOAPFaultException(fault);
      } catch (SOAPException var6) {
         throw new WebServiceException(var6);
      }
   }
}
