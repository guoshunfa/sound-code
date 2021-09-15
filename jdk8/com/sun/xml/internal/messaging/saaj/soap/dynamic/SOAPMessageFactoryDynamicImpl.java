package com.sun.xml.internal.messaging.saaj.soap.dynamic;

import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class SOAPMessageFactoryDynamicImpl extends MessageFactoryImpl {
   public SOAPMessage createMessage() throws SOAPException {
      throw new UnsupportedOperationException("createMessage() not supported for Dynamic Protocol");
   }
}
