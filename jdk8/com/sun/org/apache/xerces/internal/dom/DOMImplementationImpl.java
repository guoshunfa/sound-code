package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class DOMImplementationImpl extends CoreDOMImplementationImpl implements DOMImplementation {
   static DOMImplementationImpl singleton = new DOMImplementationImpl();

   public static DOMImplementation getDOMImplementation() {
      return singleton;
   }

   public boolean hasFeature(String feature, String version) {
      boolean result = super.hasFeature(feature, version);
      if (result) {
         return result;
      } else {
         boolean anyVersion = version == null || version.length() == 0;
         if (feature.startsWith("+")) {
            feature = feature.substring(1);
         }

         return feature.equalsIgnoreCase("Events") && (anyVersion || version.equals("2.0")) || feature.equalsIgnoreCase("MutationEvents") && (anyVersion || version.equals("2.0")) || feature.equalsIgnoreCase("Traversal") && (anyVersion || version.equals("2.0")) || feature.equalsIgnoreCase("Range") && (anyVersion || version.equals("2.0")) || feature.equalsIgnoreCase("MutationEvents") && (anyVersion || version.equals("2.0"));
      }
   }

   public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
      if (namespaceURI == null && qualifiedName == null && doctype == null) {
         return new DocumentImpl();
      } else if (doctype != null && doctype.getOwnerDocument() != null) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
         throw new DOMException((short)4, msg);
      } else {
         DocumentImpl doc = new DocumentImpl(doctype);
         Element e = doc.createElementNS(namespaceURI, qualifiedName);
         doc.appendChild(e);
         return doc;
      }
   }
}
