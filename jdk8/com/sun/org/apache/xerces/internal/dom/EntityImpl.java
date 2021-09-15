package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Entity;
import org.w3c.dom.Node;

public class EntityImpl extends ParentNode implements Entity {
   static final long serialVersionUID = -3575760943444303423L;
   protected String name;
   protected String publicId;
   protected String systemId;
   protected String encoding;
   protected String inputEncoding;
   protected String version;
   protected String notationName;
   protected String baseURI;

   public EntityImpl(CoreDocumentImpl ownerDoc, String name) {
      super(ownerDoc);
      this.name = name;
      this.isReadOnly(true);
   }

   public short getNodeType() {
      return 6;
   }

   public String getNodeName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public void setNodeValue(String x) throws DOMException {
      if (this.ownerDocument.errorChecking && this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      }
   }

   public void setPrefix(String prefix) throws DOMException {
      if (this.ownerDocument.errorChecking && this.isReadOnly()) {
         throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null));
      }
   }

   public Node cloneNode(boolean deep) {
      EntityImpl newentity = (EntityImpl)super.cloneNode(deep);
      newentity.setReadOnly(true, deep);
      return newentity;
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

   public String getXmlVersion() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.version;
   }

   public String getXmlEncoding() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.encoding;
   }

   public String getNotationName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.notationName;
   }

   public void setPublicId(String id) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.publicId = id;
   }

   public void setXmlEncoding(String value) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.encoding = value;
   }

   public String getInputEncoding() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.inputEncoding;
   }

   public void setInputEncoding(String inputEncoding) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.inputEncoding = inputEncoding;
   }

   public void setXmlVersion(String value) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.version = value;
   }

   public void setSystemId(String id) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.systemId = id;
   }

   public void setNotationName(String name) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.notationName = name;
   }

   public String getBaseURI() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.baseURI != null ? this.baseURI : ((CoreDocumentImpl)this.getOwnerDocument()).getBaseURI();
   }

   public void setBaseURI(String uri) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.baseURI = uri;
   }
}
