package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class CharacterDataImpl extends ChildNode {
   static final long serialVersionUID = 7931170150428474230L;
   protected String data;
   private static transient NodeList singletonNodeList = new NodeList() {
      public Node item(int index) {
         return null;
      }

      public int getLength() {
         return 0;
      }
   };

   public CharacterDataImpl() {
   }

   protected CharacterDataImpl(CoreDocumentImpl ownerDocument, String data) {
      super(ownerDocument);
      this.data = data;
   }

   public NodeList getChildNodes() {
      return singletonNodeList;
   }

   public String getNodeValue() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.data;
   }

   protected void setNodeValueInternal(String value) {
      this.setNodeValueInternal(value, false);
   }

   protected void setNodeValueInternal(String value, boolean replace) {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      String oldvalue;
      if (ownerDocument.errorChecking && this.isReadOnly()) {
         oldvalue = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, oldvalue);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         oldvalue = this.data;
         ownerDocument.modifyingCharacterData(this, replace);
         this.data = value;
         ownerDocument.modifiedCharacterData(this, oldvalue, value, replace);
      }
   }

   public void setNodeValue(String value) {
      this.setNodeValueInternal(value);
      this.ownerDocument().replacedText(this);
   }

   public String getData() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.data;
   }

   public int getLength() {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      return this.data.length();
   }

   public void appendData(String data) {
      if (this.isReadOnly()) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, msg);
      } else if (data != null) {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         this.setNodeValue(this.data + data);
      }
   }

   public void deleteData(int offset, int count) throws DOMException {
      this.internalDeleteData(offset, count, false);
   }

   void internalDeleteData(int offset, int count, boolean replace) throws DOMException {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      if (ownerDocument.errorChecking) {
         String msg;
         if (this.isReadOnly()) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
            throw new DOMException((short)7, msg);
         }

         if (count < 0) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", (Object[])null);
            throw new DOMException((short)1, msg);
         }
      }

      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      int tailLength = Math.max(this.data.length() - count - offset, 0);

      try {
         String value = this.data.substring(0, offset) + (tailLength > 0 ? this.data.substring(offset + count, offset + count + tailLength) : "");
         this.setNodeValueInternal(value, replace);
         ownerDocument.deletedText(this, offset, count);
      } catch (StringIndexOutOfBoundsException var8) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", (Object[])null);
         throw new DOMException((short)1, msg);
      }
   }

   public void insertData(int offset, String data) throws DOMException {
      this.internalInsertData(offset, data, false);
   }

   void internalInsertData(int offset, String data, boolean replace) throws DOMException {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      String value;
      if (ownerDocument.errorChecking && this.isReadOnly()) {
         value = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, value);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         try {
            value = (new StringBuffer(this.data)).insert(offset, data).toString();
            this.setNodeValueInternal(value, replace);
            ownerDocument.insertedText(this, offset, data.length());
         } catch (StringIndexOutOfBoundsException var7) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", (Object[])null);
            throw new DOMException((short)1, msg);
         }
      }
   }

   public void replaceData(int offset, int count, String data) throws DOMException {
      CoreDocumentImpl ownerDocument = this.ownerDocument();
      String oldvalue;
      if (ownerDocument.errorChecking && this.isReadOnly()) {
         oldvalue = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", (Object[])null);
         throw new DOMException((short)7, oldvalue);
      } else {
         if (this.needsSyncData()) {
            this.synchronizeData();
         }

         ownerDocument.replacingData(this);
         oldvalue = this.data;
         this.internalDeleteData(offset, count, true);
         this.internalInsertData(offset, data, true);
         ownerDocument.replacedCharacterData(this, oldvalue, this.data);
      }
   }

   public void setData(String value) throws DOMException {
      this.setNodeValue(value);
   }

   public String substringData(int offset, int count) throws DOMException {
      if (this.needsSyncData()) {
         this.synchronizeData();
      }

      int length = this.data.length();
      if (count >= 0 && offset >= 0 && offset <= length - 1) {
         int tailIndex = Math.min(offset + count, length);
         return this.data.substring(offset, tailIndex);
      } else {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", (Object[])null);
         throw new DOMException((short)1, msg);
      }
   }
}
