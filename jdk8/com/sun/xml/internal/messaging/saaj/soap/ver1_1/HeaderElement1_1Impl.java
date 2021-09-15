package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class HeaderElement1_1Impl extends HeaderElementImpl {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");

   public HeaderElement1_1Impl(SOAPDocumentImpl ownerDoc, Name qname) {
      super(ownerDoc, qname);
   }

   public HeaderElement1_1Impl(SOAPDocumentImpl ownerDoc, QName qname) {
      super(ownerDoc, qname);
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      HeaderElementImpl copy = new HeaderElement1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
      return replaceElementWithSOAPElement(this, copy);
   }

   protected NameImpl getActorAttributeName() {
      return NameImpl.create("actor", (String)null, "http://schemas.xmlsoap.org/soap/envelope/");
   }

   protected NameImpl getRoleAttributeName() {
      log.log(Level.SEVERE, (String)"SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", (Object[])(new String[]{"Role"}));
      throw new UnsupportedOperationException("Role not supported by SOAP 1.1");
   }

   protected NameImpl getMustunderstandAttributeName() {
      return NameImpl.create("mustUnderstand", (String)null, "http://schemas.xmlsoap.org/soap/envelope/");
   }

   protected String getMustunderstandLiteralValue(boolean mustUnderstand) {
      return mustUnderstand ? "1" : "0";
   }

   protected boolean getMustunderstandAttributeValue(String mu) {
      return "1".equals(mu) || "true".equalsIgnoreCase(mu);
   }

   protected NameImpl getRelayAttributeName() {
      log.log(Level.SEVERE, (String)"SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", (Object[])(new String[]{"Relay"}));
      throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
   }

   protected String getRelayLiteralValue(boolean relayAttr) {
      log.log(Level.SEVERE, (String)"SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", (Object[])(new String[]{"Relay"}));
      throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
   }

   protected boolean getRelayAttributeValue(String mu) {
      log.log(Level.SEVERE, (String)"SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", (Object[])(new String[]{"Relay"}));
      throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
   }

   protected String getActorOrRole() {
      return this.getActor();
   }
}
