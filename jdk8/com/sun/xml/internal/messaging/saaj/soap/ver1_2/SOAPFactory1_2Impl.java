package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

public class SOAPFactory1_2Impl extends SOAPFactoryImpl {
   protected SOAPDocumentImpl createDocument() {
      return (new SOAPPart1_2Impl()).getDocument();
   }

   public Detail createDetail() throws SOAPException {
      return new Detail1_2Impl(this.createDocument());
   }

   public SOAPFault createFault(String reasonText, QName faultCode) throws SOAPException {
      if (faultCode == null) {
         throw new IllegalArgumentException("faultCode argument for createFault was passed NULL");
      } else if (reasonText == null) {
         throw new IllegalArgumentException("reasonText argument for createFault was passed NULL");
      } else {
         Fault1_2Impl fault = new Fault1_2Impl(this.createDocument(), (String)null);
         fault.setFaultCode(faultCode);
         fault.setFaultString(reasonText);
         return fault;
      }
   }

   public SOAPFault createFault() throws SOAPException {
      Fault1_2Impl fault = new Fault1_2Impl(this.createDocument(), (String)null);
      fault.setFaultCode(fault.getDefaultFaultCode());
      fault.setFaultString("Fault string, and possibly fault code, not set");
      return fault;
   }
}
