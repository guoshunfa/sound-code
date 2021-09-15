package com.sun.xml.internal.messaging.saaj.soap.dynamic;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;

public class SOAPFactoryDynamicImpl extends SOAPFactoryImpl {
   protected SOAPDocumentImpl createDocument() {
      return null;
   }

   public Detail createDetail() throws SOAPException {
      throw new UnsupportedOperationException("createDetail() not supported for Dynamic Protocol");
   }
}
