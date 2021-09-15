package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.DOMException;

public class AttrNSImpl extends AttrImpl {
   static final long serialVersionUID = -781906615369795414L;
   static final String xmlnsURI = "http://www.w3.org/2000/xmlns/";
   static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
   protected String namespaceURI;
   protected String localName;

   public AttrNSImpl() {
   }

   protected AttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName) {
      super(ownerDocument, qualifiedName);
      this.setName(namespaceURI, qualifiedName);
   }

   private void setName(String namespaceURI, String qname) {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      this.namespaceURI = namespaceURI;
      if (namespaceURI != null) {
         this.namespaceURI = namespaceURI.length() == 0 ? null : namespaceURI;
      }

      int colon1 = qname.indexOf(58);
      int colon2 = qname.lastIndexOf(58);
      ownerDocument.checkNamespaceWF(qname, colon1, colon2);
      if (colon1 < 0) {
         this.localName = qname;
         if (ownerDocument.errorChecking) {
            ownerDocument.checkQName((String)null, this.localName);
            if (qname.equals("xmlns") && (namespaceURI == null || !namespaceURI.equals(NamespaceContext.XMLNS_URI)) || namespaceURI != null && namespaceURI.equals(NamespaceContext.XMLNS_URI) && !qname.equals("xmlns")) {
               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
               throw new DOMException((short)14, msg);
            }
         }
      } else {
         String prefix = qname.substring(0, colon1);
         this.localName = qname.substring(colon2 + 1);
         ownerDocument.checkQName(prefix, this.localName);
         ownerDocument.checkDOMNSErr(prefix, namespaceURI);
      }

   }

   public AttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
      super(ownerDocument, qualifiedName);
      this.localName = localName;
      this.namespaceURI = namespaceURI;
   }

   protected AttrNSImpl(CoreDocumentImpl ownerDocument, String value) {
      super(ownerDocument, value);
   }

   void rename(String namespaceURI, String qualifiedName) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.name = qualifiedName;
      this.setName(namespaceURI, qualifiedName);
   }

   public void setValues(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
      super.textNode = null;
      super.flags = 0;
      this.isSpecified(true);
      this.hasStringValue(true);
      super.setOwnerDocument(ownerDocument);
      this.localName = localName;
      this.namespaceURI = namespaceURI;
      super.name = qualifiedName;
      super.value = null;
   }

   public String getNamespaceURI() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.namespaceURI;
   }

   public String getPrefix() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      int index = this.name.indexOf(58);
      return index < 0 ? null : this.name.substring(0, index);
   }

   public void setPrefix(String prefix) throws DOMException {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.ownerDocument().errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (prefix != null && prefix.length() != 0) {
            if (!CoreDocumentImpl.isXMLName(prefix, this.ownerDocument().isXML11Version())) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
               throw new DOMException((short)5, msg);
            }

            if (this.namespaceURI == null || prefix.indexOf(58) >= 0) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
               throw new DOMException((short)14, msg);
            }

            if (prefix.equals("xmlns")) {
               if (!this.namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
                  throw new DOMException((short)14, msg);
               }
            } else if (prefix.equals("xml")) {
               if (!this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
                  throw new DOMException((short)14, msg);
               }
            } else if (this.name.equals("xmlns")) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
               throw new DOMException((short)14, msg);
            }
         }
      }

      if (prefix != null && prefix.length() != 0) {
         this.name = prefix + ":" + this.localName;
      } else {
         this.name = this.localName;
      }

   }

   public String getLocalName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.localName;
   }

   public String getTypeName() {
      if (this.type != null) {
         return this.type instanceof XSSimpleTypeDecl ? ((XSSimpleTypeDecl)this.type).getName() : (String)this.type;
      } else {
         return null;
      }
   }

   public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
      return this.type != null && this.type instanceof XSSimpleTypeDecl ? ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod) : false;
   }

   public String getTypeNamespace() {
      if (this.type != null) {
         return this.type instanceof XSSimpleTypeDecl ? ((XSSimpleTypeDecl)this.type).getNamespace() : "http://www.w3.org/TR/REC-xml";
      } else {
         return null;
      }
   }
}
