package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XSAnnotationImpl implements XSAnnotation {
   private String fData = null;
   private SchemaGrammar fGrammar = null;

   public XSAnnotationImpl(String contents, SchemaGrammar grammar) {
      this.fData = contents;
      this.fGrammar = grammar;
   }

   public boolean writeAnnotation(Object target, short targetType) {
      if (targetType != 1 && targetType != 3) {
         if (targetType == 2) {
            this.writeToSAX((ContentHandler)target);
            return true;
         } else {
            return false;
         }
      } else {
         this.writeToDOM((Node)target, targetType);
         return true;
      }
   }

   public String getAnnotationString() {
      return this.fData;
   }

   public short getType() {
      return 12;
   }

   public String getName() {
      return null;
   }

   public String getNamespace() {
      return null;
   }

   public XSNamespaceItem getNamespaceItem() {
      return null;
   }

   private synchronized void writeToSAX(ContentHandler handler) {
      SAXParser parser = this.fGrammar.getSAXParser();
      StringReader aReader = new StringReader(this.fData);
      InputSource aSource = new InputSource(aReader);
      parser.setContentHandler(handler);

      try {
         parser.parse(aSource);
      } catch (SAXException var6) {
      } catch (IOException var7) {
      }

      parser.setContentHandler((ContentHandler)null);
   }

   private synchronized void writeToDOM(Node target, short type) {
      Document futureOwner = type == 1 ? target.getOwnerDocument() : (Document)target;
      DOMParser parser = this.fGrammar.getDOMParser();
      StringReader aReader = new StringReader(this.fData);
      InputSource aSource = new InputSource(aReader);

      try {
         parser.parse(aSource);
      } catch (SAXException var10) {
      } catch (IOException var11) {
      }

      Document aDocument = parser.getDocument();
      parser.dropDocumentReferences();
      Element annotation = aDocument.getDocumentElement();
      Node newElem = null;
      if (futureOwner instanceof CoreDocumentImpl) {
         newElem = futureOwner.adoptNode(annotation);
         if (newElem == null) {
            newElem = futureOwner.importNode(annotation, true);
         }
      } else {
         newElem = futureOwner.importNode(annotation, true);
      }

      target.insertBefore(newElem, target.getFirstChild());
   }
}
