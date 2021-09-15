package com.sun.xml.internal.messaging.saaj.client.p2p;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;

public class HttpSOAPConnectionFactory extends SOAPConnectionFactory {
   public SOAPConnection createConnection() throws SOAPException {
      return new HttpSOAPConnection();
   }
}
