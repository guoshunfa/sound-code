package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.org.apache.xerces.internal.dom.CDATASectionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;
import org.w3c.dom.Node;

public class CDATAImpl extends CDATASectionImpl implements Text {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
   static final String cdataUC = "<![CDATA[";
   static final String cdataLC = "<![cdata[";

   public CDATAImpl(SOAPDocumentImpl ownerDoc, String text) {
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
         log.severe("SAAJ0145.impl.no.null.to.parent.elem");
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
      return false;
   }
}
