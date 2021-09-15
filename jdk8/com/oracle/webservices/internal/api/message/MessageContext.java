package com.oracle.webservices.internal.api.message;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public interface MessageContext extends DistributedPropertySet {
   SOAPMessage getAsSOAPMessage() throws SOAPException;

   /** @deprecated */
   SOAPMessage getSOAPMessage() throws SOAPException;

   ContentType writeTo(OutputStream var1) throws IOException;

   ContentType getContentType();
}
