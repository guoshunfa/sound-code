package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

public class SOAPFactory1_1Impl extends SOAPFactoryImpl {
   protected SOAPDocumentImpl createDocument() {
      return (new SOAPPart1_1Impl()).getDocument();
   }

   public Detail createDetail() throws SOAPException {
      return new Detail1_1Impl(this.createDocument());
   }

   public SOAPFault createFault(String reasonText, QName faultCode) throws SOAPException {
      if (faultCode == null) {
         throw new IllegalArgumentException("faultCode argument for createFault was passed NULL");
      } else if (reasonText == null) {
         throw new IllegalArgumentException("reasonText argument for createFault was passed NULL");
      } else {
         Fault1_1Impl fault = new Fault1_1Impl(this.createDocument(), (String)null);
         fault.setFaultCode(faultCode);
         fault.setFaultString(reasonText);
         return fault;
      }
   }

   public SOAPFault createFault() throws SOAPException {
      Fault1_1Impl fault = new Fault1_1Impl(this.createDocument(), (String)null);
      fault.setFaultCode(fault.getDefaultFaultCode());
      fault.setFaultString("Fault string, and possibly fault code, not set");
      return fault;
   }
}
