package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class HeaderElement1_2Impl extends HeaderElementImpl {
   private static final Logger log = Logger.getLogger(HeaderElement1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");

   public HeaderElement1_2Impl(SOAPDocumentImpl ownerDoc, Name qname) {
      super(ownerDoc, qname);
   }

   public HeaderElement1_2Impl(SOAPDocumentImpl ownerDoc, QName qname) {
      super(ownerDoc, qname);
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      HeaderElementImpl copy = new HeaderElement1_2Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
      return replaceElementWithSOAPElement(this, copy);
   }

   protected NameImpl getRoleAttributeName() {
      return NameImpl.create("role", (String)null, "http://www.w3.org/2003/05/soap-envelope");
   }

   protected NameImpl getActorAttributeName() {
      return this.getRoleAttributeName();
   }

   protected NameImpl getMustunderstandAttributeName() {
      return NameImpl.create("mustUnderstand", (String)null, "http://www.w3.org/2003/05/soap-envelope");
   }

   protected String getMustunderstandLiteralValue(boolean mustUnderstand) {
      return mustUnderstand ? "true" : "false";
   }

   protected boolean getMustunderstandAttributeValue(String mu) {
      return mu.equals("true") || mu.equals("1");
   }

   protected NameImpl getRelayAttributeName() {
      return NameImpl.create("relay", (String)null, "http://www.w3.org/2003/05/soap-envelope");
   }

   protected String getRelayLiteralValue(boolean relay) {
      return relay ? "true" : "false";
   }

   protected boolean getRelayAttributeValue(String relay) {
      return relay.equals("true") || relay.equals("1");
   }

   protected String getActorOrRole() {
      return this.getRole();
   }
}
