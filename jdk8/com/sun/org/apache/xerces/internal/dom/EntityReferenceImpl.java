package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class EntityReferenceImpl extends ParentNode implements EntityReference {
   static final long serialVersionUID = -7381452955687102062L;
   protected String name;
   protected String baseURI;

   public EntityReferenceImpl(CoreDocumentImpl ownerDoc, String name) {
      super(ownerDoc);
      this.name = name;
      this.isReadOnly(true);
      this.needsSyncChildren(true);
   }

   public short getNodeType() {
      return 5;
   }

   public String getNodeName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public Node cloneNode(boolean deep) {
      EntityReferenceImpl er = (EntityReferenceImpl)super.cloneNode(deep);
      er.setReadOnly(true, deep);
      return er;
   }

   public String getBaseURI() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (this.baseURI == null) {
         DocumentType doctype;
         NamedNodeMap entities;
         if (null != (doctype = this.getOwnerDocument().getDoctype()) && null != (entities = doctype.getEntities())) {
            EntityImpl entDef = (EntityImpl)entities.getNamedItem(this.getNodeName());
            if (entDef != null) {
               return entDef.getBaseURI();
            }
         }
      } else if (this.baseURI != null && this.baseURI.length() != 0) {
         try {
            return (new URI(this.baseURI)).toString();
         } catch (URI.MalformedURIException var4) {
            return null;
         }
      }

      return this.baseURI;
   }

   public void setBaseURI(String uri) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.baseURI = uri;
   }

   protected String getEntityRefValue() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      String value = "";
      if (this.firstChild != null) {
         if (this.firstChild.getNodeType() == 5) {
            value = ((EntityReferenceImpl)this.firstChild).getEntityRefValue();
         } else {
            if (this.firstChild.getNodeType() != 3) {
               return null;
            }

            value = this.firstChild.getNodeValue();
         }

         if (this.firstChild.nextSibling == null) {
            return value;
         } else {
            StringBuffer buff = new StringBuffer(value);

            for(ChildNode next = this.firstChild.nextSibling; next != null; next = next.nextSibling) {
               if (next.getNodeType() == 5) {
                  value = ((EntityReferenceImpl)next).getEntityRefValue();
               } else {
                  if (next.getNodeType() != 3) {
                     return null;
                  }

                  value = next.getNodeValue();
               }

               buff.append(value);
            }

            return buff.toString();
         }
      } else {
         return "";
      }
   }

   protected void synchronizeChildren() {
      this.needsSyncChildren(false);
      DocumentType doctype;
      NamedNodeMap entities;
      if (null != (doctype = this.getOwnerDocument().getDoctype()) && null != (entities = doctype.getEntities())) {
         EntityImpl entDef = (EntityImpl)entities.getNamedItem(this.getNodeName());
         if (entDef == null) {
            return;
         }

         this.isReadOnly(false);

         for(Node defkid = entDef.getFirstChild(); defkid != null; defkid = defkid.getNextSibling()) {
            Node newkid = defkid.cloneNode(true);
            this.insertBefore(newkid, (Node)null);
         }

         this.setReadOnly(true, true);
      }

   }

   public void setReadOnly(boolean readOnly, boolean deep) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      if (deep) {
         if (this.needsSyncChildren()) {
            this.synchronizeChildren();
         }

         for(ChildNode mykid = this.firstChild; mykid != null; mykid = mykid.nextSibling) {
            mykid.setReadOnly(readOnly, true);
         }
      }

      this.isReadOnly(readOnly);
   }
}
