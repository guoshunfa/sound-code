package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

final class SchemaDOMImplementation implements DOMImplementation {
   private static final SchemaDOMImplementation singleton = new SchemaDOMImplementation();

   public static DOMImplementation getDOMImplementation() {
      return singleton;
   }

   private SchemaDOMImplementation() {
   }

   public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public Object getFeature(String feature, String version) {
      return singleton.hasFeature(feature, version) ? singleton : null;
   }

   public boolean hasFeature(String feature, String version) {
      boolean anyVersion = version == null || version.length() == 0;
      return (feature.equalsIgnoreCase("Core") || feature.equalsIgnoreCase("XML")) && (anyVersion || version.equals("1.0") || version.equals("2.0") || version.equals("3.0"));
   }
}
