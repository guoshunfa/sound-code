package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;
import org.w3c.dom.Node;

public class TextImpl extends com.sun.org.apache.xerces.internal.dom.TextImpl implements Text, org.w3c.dom.Text {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");

   public TextImpl(SOAPDocumentImpl ownerDoc, String text) {
      super(ownerDoc, text);
   }

   public String getValue() {
      String nodeValue = this.getNodeValue();
      return nodeValue.equals("") ? null : nodeValue;
   }

   public void setValue(String text) {
      this.setNodeValue(text);
   }

   public void setParentElement(SOAPElement parent) throws SOAPException {
      if (parent == null) {
         log.severe("SAAJ0126.impl.cannot.locate.ns");
         throw new SOAPException("Cannot pass NULL to setParentElement");
      } else {
         ((ElementImpl)parent).addNode(this);
      }
   }

   public SOAPElement getParentElement() {
      return (SOAPElement)this.getParentNode();
   }

   public void detachNode() {
      Node parent = this.getParentNode();
      if (parent != null) {
         parent.removeChild(this);
      }

   }

   public void recycleNode() {
      this.detachNode();
   }

   public boolean isComment() {
      String txt = this.getNodeValue();
      if (txt == null) {
         return false;
      } else {
         return txt.startsWith("<!--") && txt.endsWith("-->");
      }
   }
}
