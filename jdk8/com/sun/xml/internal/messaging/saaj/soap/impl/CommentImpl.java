package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class CommentImpl extends com.sun.org.apache.xerces.internal.dom.CommentImpl implements Text, Comment {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
   protected static ResourceBundle rb;

   public CommentImpl(SOAPDocumentImpl ownerDoc, String text) {
      super(ownerDoc, text);
   }

   public String getValue() {
      String nodeValue = this.getNodeValue();
      return nodeValue.equals("") ? null : nodeValue;
   }

   public void setValue(String text) {
      this.setNodeValue(text);
   }

   public void setParentElement(SOAPElement element) throws SOAPException {
      if (element == null) {
         log.severe("SAAJ0112.impl.no.null.to.parent.elem");
         throw new SOAPException("Cannot pass NULL to setParentElement");
      } else {
         ((ElementImpl)element).addNode(this);
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
      return true;
   }

   public org.w3c.dom.Text splitText(int offset) throws DOMException {
      log.severe("SAAJ0113.impl.cannot.split.text.from.comment");
      throw new UnsupportedOperationException("Cannot split text from a Comment Node.");
   }

   public org.w3c.dom.Text replaceWholeText(String content) throws DOMException {
      log.severe("SAAJ0114.impl.cannot.replace.wholetext.from.comment");
      throw new UnsupportedOperationException("Cannot replace Whole Text from a Comment Node.");
   }

   public String getWholeText() {
      throw new UnsupportedOperationException("Not Supported");
   }

   public boolean isElementContentWhitespace() {
      throw new UnsupportedOperationException("Not Supported");
   }

   static {
      rb = log.getResourceBundle();
   }
}
