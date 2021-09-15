package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

public class ElementNSImpl extends ElementImpl {
   static final long serialVersionUID = -9142310625494392642L;
   static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
   protected String namespaceURI;
   protected String localName;
   transient XSTypeDefinition type;

   protected ElementNSImpl() {
   }

   protected ElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName) throws DOMException {
      super(ownerDocument, qualifiedName);
      this.setName(namespaceURI, qualifiedName);
   }

   private void setName(String namespaceURI, String qname) {
      this.namespaceURI = namespaceURI;
      if (namespaceURI != null) {
         this.namespaceURI = namespaceURI.length() == 0 ? null : namespaceURI;
      }

      String msg;
      if (qname == null) {
         msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
         throw new DOMException((short)14, msg);
      } else {
         int colon1 = qname.indexOf(58);
         int colon2 = qname.lastIndexOf(58);
         this.ownerDocument.checkNamespaceWF(qname, colon1, colon2);
         if (colon1 < 0) {
            this.localName = qname;
            if (this.ownerDocument.errorChecking) {
               this.ownerDocument.checkQName((String)null, this.localName);
               if (qname.equals("xmlns") && (namespaceURI == null || !namespaceURI.equals(NamespaceContext.XMLNS_URI)) || namespaceURI != null && namespaceURI.equals(NamespaceContext.XMLNS_URI) && !qname.equals("xmlns")) {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
                  throw new DOMException((short)14, msg);
               }
            }
         } else {
            String prefix = qname.substring(0, colon1);
            this.localName = qname.substring(colon2 + 1);
            if (this.ownerDocument.errorChecking) {
               if (namespaceURI == null || prefix.equals("xml") && !namespaceURI.equals(NamespaceContext.XML_URI)) {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
                  throw new DOMException((short)14, msg);
               }

               this.ownerDocument.checkQName(prefix, this.localName);
               this.ownerDocument.checkDOMNSErr(prefix, namespaceURI);
            }
         }

      }
   }

   protected ElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) throws DOMException {
      super(ownerDocument, qualifiedName);
      this.localName = localName;
      this.namespaceURI = namespaceURI;
   }

   protected ElementNSImpl(CoreDocumentImpl ownerDocument, String value) {
      super(ownerDocument, value);
   }

   void rename(String namespaceURI, String qualifiedName) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.name = qualifiedName;
      this.setName(namespaceURI, qualifiedName);
      this.reconcileDefaultAttributes();
   }

   protected void setValues(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
      this.firstChild = null;
      this.previousSibling = null;
      this.nextSibling = null;
      this.fNodeListCache = null;
      this.attributes = null;
      super.flags = 0;
      this.setOwnerDocument(ownerDocument);
      this.needsSyncData(true);
      super.name = qualifiedName;
      this.localName = localName;
      this.namespaceURI = namespaceURI;
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

      if (this.ownerDocument.errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (prefix != null && prefix.length() != 0) {
            if (!CoreDocumentImpl.isXMLName(prefix, this.ownerDocument.isXML11Version())) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
               throw new DOMException((short)5, msg);
            }

            if (this.namespaceURI == null || prefix.indexOf(58) >= 0) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
               throw new DOMException((short)14, msg);
            }

            if (prefix.equals("xml") && !this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
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

   public String getBaseURI() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      String uri;
      if (this.attributes != null) {
         Attr attrNode = (Attr)this.attributes.getNamedItemNS("http://www.w3.org/XML/1998/namespace", "base");
         if (attrNode != null) {
            uri = attrNode.getNodeValue();
            if (uri.length() != 0) {
               try {
                  uri = (new URI(uri)).toString();
                  return uri;
               } catch (URI.MalformedURIException var10) {
                  NodeImpl parentOrOwner = this.parentNode() != null ? this.parentNode() : this.ownerNode;
                  String parentBaseURI = parentOrOwner != null ? parentOrOwner.getBaseURI() : null;
                  if (parentBaseURI != null) {
                     try {
                        uri = (new URI(new URI(parentBaseURI), uri)).toString();
                        return uri;
                     } catch (URI.MalformedURIException var7) {
                        return null;
                     }
                  }

                  return null;
               }
            }
         }
      }

      String parentElementBaseURI = this.parentNode() != null ? this.parentNode().getBaseURI() : null;
      if (parentElementBaseURI != null) {
         try {
            return (new URI(parentElementBaseURI)).toString();
         } catch (URI.MalformedURIException var8) {
            return null;
         }
      } else {
         uri = this.ownerNode != null ? this.ownerNode.getBaseURI() : null;
         if (uri != null) {
            try {
               return (new URI(uri)).toString();
            } catch (URI.MalformedURIException var9) {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public String getTypeName() {
      if (this.type != null) {
         if (this.type instanceof XSSimpleTypeDecl) {
            return ((XSSimpleTypeDecl)this.type).getTypeName();
         }

         if (this.type instanceof XSComplexTypeDecl) {
            return ((XSComplexTypeDecl)this.type).getTypeName();
         }
      }

      return null;
   }

   public String getTypeNamespace() {
      return this.type != null ? this.type.getNamespace() : null;
   }

   public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.type != null) {
         if (this.type instanceof XSSimpleTypeDecl) {
            return ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
         }

         if (this.type instanceof XSComplexTypeDecl) {
            return ((XSComplexTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
         }
      }

      return false;
   }

   public void setType(XSTypeDefinition type) {
      this.type = type;
   }
}
