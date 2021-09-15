package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

public class DocumentTypeImpl extends ParentNode implements DocumentType {
   static final long serialVersionUID = 7751299192316526485L;
   protected String name;
   protected NamedNodeMapImpl entities;
   protected NamedNodeMapImpl notations;
   protected NamedNodeMapImpl elements;
   protected String publicID;
   protected String systemID;
   protected String internalSubset;
   private int doctypeNumber;
   private Map<String, ParentNode.UserDataRecord> userData;
   private static final ObjectStreamField[] serialPersistentFields;

   public DocumentTypeImpl(CoreDocumentImpl ownerDocument, String name) {
      super(ownerDocument);
      this.doctypeNumber = 0;
      this.userData = null;
      this.name = name;
      this.entities = new NamedNodeMapImpl(this);
      this.notations = new NamedNodeMapImpl(this);
      this.elements = new NamedNodeMapImpl(this);
   }

   public DocumentTypeImpl(CoreDocumentImpl ownerDocument, String qualifiedName, String publicID, String systemID) {
      this(ownerDocument, qualifiedName);
      this.publicID = publicID;
      this.systemID = systemID;
   }

   public String getPublicId() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.publicID;
   }

   public String getSystemId() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.systemID;
   }

   public void setInternalSubset(String internalSubset) {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      this.internalSubset = internalSubset;
   }

   public String getInternalSubset() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.internalSubset;
   }

   public short getNodeType() {
      return 10;
   }

   public String getNodeName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public Node cloneNode(boolean deep) {
      DocumentTypeImpl newnode = (DocumentTypeImpl)super.cloneNode(deep);
      newnode.entities = this.entities.cloneMap((NodeImpl)newnode);
      newnode.notations = this.notations.cloneMap((NodeImpl)newnode);
      newnode.elements = this.elements.cloneMap((NodeImpl)newnode);
      return newnode;
   }

   public String getTextContent() throws DOMException {
      return null;
   }

   public void setTextContent(String textContent) throws DOMException {
   }

   public boolean isEqualNode(Node arg) {
      if (!super.isEqualNode(arg)) {
         return false;
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         DocumentTypeImpl argDocType = (DocumentTypeImpl)arg;
         if ((this.getPublicId() != null || argDocType.getPublicId() == null) && (this.getPublicId() == null || argDocType.getPublicId() != null) && (this.getSystemId() != null || argDocType.getSystemId() == null) && (this.getSystemId() == null || argDocType.getSystemId() != null) && (this.getInternalSubset() != null || argDocType.getInternalSubset() == null) && (this.getInternalSubset() == null || argDocType.getInternalSubset() != null)) {
            if (this.getPublicId() != null && !this.getPublicId().equals(argDocType.getPublicId())) {
               return false;
            } else if (this.getSystemId() != null && !this.getSystemId().equals(argDocType.getSystemId())) {
               return false;
            } else if (this.getInternalSubset() != null && !this.getInternalSubset().equals(argDocType.getInternalSubset())) {
               return false;
            } else {
               NamedNodeMapImpl argEntities = argDocType.entities;
               if (this.entities == null && argEntities != null || this.entities != null && argEntities == null) {
                  return false;
               } else {
                  Node noteNode1;
                  if (this.entities != null && argEntities != null) {
                     if (this.entities.getLength() != argEntities.getLength()) {
                        return false;
                     }

                     for(int index = 0; this.entities.item(index) != null; ++index) {
                        Node entNode1 = this.entities.item(index);
                        noteNode1 = argEntities.getNamedItem(entNode1.getNodeName());
                        if (!((NodeImpl)entNode1).isEqualNode((NodeImpl)noteNode1)) {
                           return false;
                        }
                     }
                  }

                  NamedNodeMapImpl argNotations = argDocType.notations;
                  if (this.notations == null && argNotations != null || this.notations != null && argNotations == null) {
                     return false;
                  } else {
                     if (this.notations != null && argNotations != null) {
                        if (this.notations.getLength() != argNotations.getLength()) {
                           return false;
                        }

                        for(int index = 0; this.notations.item(index) != null; ++index) {
                           noteNode1 = this.notations.item(index);
                           Node noteNode2 = argNotations.getNamedItem(noteNode1.getNodeName());
                           if (!((NodeImpl)noteNode1).isEqualNode((NodeImpl)noteNode2)) {
                              return false;
                           }
                        }
                     }

                     return true;
                  }
               }
            }
         } else {
            return false;
         }
      }
   }

   void setOwnerDocument(CoreDocumentImpl doc) {
      super.setOwnerDocument(doc);
      this.entities.setOwnerDocument(doc);
      this.notations.setOwnerDocument(doc);
      this.elements.setOwnerDocument(doc);
   }

   protected int getNodeNumber() {
      if (this.getOwnerDocument() != null) {
         return super.getNodeNumber();
      } else {
         if (this.doctypeNumber == 0) {
            CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            this.doctypeNumber = cd.assignDocTypeNumber();
         }

         return this.doctypeNumber;
      }
   }

   public String getName() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.name;
   }

   public NamedNodeMap getEntities() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.entities;
   }

   public NamedNodeMap getNotations() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.notations;
   }

   public void setReadOnly(boolean readOnly, boolean deep) {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      super.setReadOnly(readOnly, deep);
      this.elements.setReadOnly(readOnly, true);
      this.entities.setReadOnly(readOnly, true);
      this.notations.setReadOnly(readOnly, true);
   }

   public NamedNodeMap getElements() {
      if (this.needsSyncChildren()) {
         this.synchronizeChildren();
      }

      return this.elements;
   }

   public Object setUserData(String key, Object data, UserDataHandler handler) {
      if (this.userData == null) {
         this.userData = new HashMap();
      }

      ParentNode.UserDataRecord udr;
      if (data == null) {
         if (this.userData != null) {
            udr = (ParentNode.UserDataRecord)this.userData.remove(key);
            if (udr != null) {
               return udr.fData;
            }
         }

         return null;
      } else {
         udr = (ParentNode.UserDataRecord)this.userData.put(key, new ParentNode.UserDataRecord(data, handler));
         return udr != null ? udr.fData : null;
      }
   }

   public Object getUserData(String key) {
      if (this.userData == null) {
         return null;
      } else {
         ParentNode.UserDataRecord udr = (ParentNode.UserDataRecord)this.userData.get(key);
         return udr != null ? udr.fData : null;
      }
   }

   protected Map<String, ParentNode.UserDataRecord> getUserDataRecord() {
      return this.userData;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      Hashtable<String, ParentNode.UserDataRecord> ud = this.userData == null ? null : new Hashtable(this.userData);
      ObjectOutputStream.PutField pf = out.putFields();
      pf.put("name", this.name);
      pf.put("entities", this.entities);
      pf.put("notations", this.notations);
      pf.put("elements", this.elements);
      pf.put("publicID", this.publicID);
      pf.put("systemID", this.systemID);
      pf.put("internalSubset", this.internalSubset);
      pf.put("doctypeNumber", this.doctypeNumber);
      pf.put("userData", ud);
      out.writeFields();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField gf = in.readFields();
      this.name = (String)gf.get("name", (Object)null);
      this.entities = (NamedNodeMapImpl)gf.get("entities", (Object)null);
      this.notations = (NamedNodeMapImpl)gf.get("notations", (Object)null);
      this.elements = (NamedNodeMapImpl)gf.get("elements", (Object)null);
      this.publicID = (String)gf.get("publicID", (Object)null);
      this.systemID = (String)gf.get("systemID", (Object)null);
      this.internalSubset = (String)gf.get("internalSubset", (Object)null);
      this.doctypeNumber = gf.get("doctypeNumber", (int)0);
      Hashtable<String, ParentNode.UserDataRecord> ud = (Hashtable)gf.get("userData", (Object)null);
      if (ud != null) {
         this.userData = new HashMap(ud);
      }

   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("name", String.class), new ObjectStreamField("entities", NamedNodeMapImpl.class), new ObjectStreamField("notations", NamedNodeMapImpl.class), new ObjectStreamField("elements", NamedNodeMapImpl.class), new ObjectStreamField("publicID", String.class), new ObjectStreamField("systemID", String.class), new ObjectStreamField("internalSubset", String.class), new ObjectStreamField("doctypeNumber", Integer.TYPE), new ObjectStreamField("userData", Hashtable.class)};
   }
}
