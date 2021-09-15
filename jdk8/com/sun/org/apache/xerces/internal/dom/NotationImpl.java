package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Notation;

public class NotationImpl extends NodeImpl implements Notation {
   static final long serialVersionUID = -764632195890658402L;
   protected String name;
   protected String publicId;
   protected String systemId;
   protected String baseURI;

   public NotationImpl(CoreDocumentImpl ownerDoc, String name) {
      super(ownerDoc);
      this.name = name;
   }

   public short getNodeType() {
      return 12;
   }

   public String getNodeName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public String getPublicId() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.publicId;
   }

   public String getSystemId() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.systemId;
   }

   public void setPublicId(String id) {
      if (this.isReadOnly()) {
         throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null));
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         this.publicId = id;
      }
   }

   public void setSystemId(String id) {
      if (this.isReadOnly()) {
         throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null));
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         this.systemId = id;
      }
   }

   public String getBaseURI() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.baseURI != null && this.baseURI.length() != 0) {
         try {
            return (new URI(this.baseURI)).toString();
         } catch (URI.MalformedURIException var2) {
            return null;
         }
      } else {
         return this.baseURI;
      }
   }

   public void setBaseURI(String uri) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.baseURI = uri;
   }
}
