package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementImpl extends DefaultElement {
   SchemaDOM schemaDOM;
   Attr[] attrs;
   int row;
   int col;
   int parentRow;
   int line;
   int column;
   int charOffset;
   String fAnnotation;
   String fSyntheticAnnotation;

   public ElementImpl(int line, int column, int offset) {
      this.row = -1;
      this.col = -1;
      this.parentRow = -1;
      this.nodeType = 1;
      this.line = line;
      this.column = column;
      this.charOffset = offset;
   }

   public ElementImpl(int line, int column) {
      this(line, column, -1);
   }

   public ElementImpl(String prefix, String localpart, String rawname, String uri, int line, int column, int offset) {
      super(prefix, localpart, rawname, uri, (short)1);
      this.row = -1;
      this.col = -1;
      this.parentRow = -1;
      this.line = line;
      this.column = column;
      this.charOffset = offset;
   }

   public ElementImpl(String prefix, String localpart, String rawname, String uri, int line, int column) {
      this(prefix, localpart, rawname, uri, line, column, -1);
   }

   public Document getOwnerDocument() {
      return this.schemaDOM;
   }

   public Node getParentNode() {
      return this.schemaDOM.relations[this.row][0];
   }

   public boolean hasChildNodes() {
      return this.parentRow != -1;
   }

   public Node getFirstChild() {
      return this.parentRow == -1 ? null : this.schemaDOM.relations[this.parentRow][1];
   }

   public Node getLastChild() {
      if (this.parentRow == -1) {
         return null;
      } else {
         int i;
         for(i = 1; i < this.schemaDOM.relations[this.parentRow].length; ++i) {
            if (this.schemaDOM.relations[this.parentRow][i] == null) {
               return this.schemaDOM.relations[this.parentRow][i - 1];
            }
         }

         if (i == 1) {
            ++i;
         }

         return this.schemaDOM.relations[this.parentRow][i - 1];
      }
   }

   public Node getPreviousSibling() {
      return this.col == 1 ? null : this.schemaDOM.relations[this.row][this.col - 1];
   }

   public Node getNextSibling() {
      return this.col == this.schemaDOM.relations[this.row].length - 1 ? null : this.schemaDOM.relations[this.row][this.col + 1];
   }

   public NamedNodeMap getAttributes() {
      return new NamedNodeMapImpl(this.attrs);
   }

   public boolean hasAttributes() {
      return this.attrs.length != 0;
   }

   public String getTagName() {
      return this.rawname;
   }

   public String getAttribute(String name) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getName().equals(name)) {
            return this.attrs[i].getValue();
         }
      }

      return "";
   }

   public Attr getAttributeNode(String name) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getName().equals(name)) {
            return this.attrs[i];
         }
      }

      return null;
   }

   public String getAttributeNS(String namespaceURI, String localName) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getLocalName().equals(localName) && nsEquals(this.attrs[i].getNamespaceURI(), namespaceURI)) {
            return this.attrs[i].getValue();
         }
      }

      return "";
   }

   public Attr getAttributeNodeNS(String namespaceURI, String localName) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getName().equals(localName) && nsEquals(this.attrs[i].getNamespaceURI(), namespaceURI)) {
            return this.attrs[i];
         }
      }

      return null;
   }

   public boolean hasAttribute(String name) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getName().equals(name)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasAttributeNS(String namespaceURI, String localName) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getName().equals(localName) && nsEquals(this.attrs[i].getNamespaceURI(), namespaceURI)) {
            return true;
         }
      }

      return false;
   }

   public void setAttribute(String name, String value) {
      for(int i = 0; i < this.attrs.length; ++i) {
         if (this.attrs[i].getName().equals(name)) {
            this.attrs[i].setValue(value);
            return;
         }
      }

   }

   public int getLineNumber() {
      return this.line;
   }

   public int getColumnNumber() {
      return this.column;
   }

   public int getCharacterOffset() {
      return this.charOffset;
   }

   public String getAnnotation() {
      return this.fAnnotation;
   }

   public String getSyntheticAnnotation() {
      return this.fSyntheticAnnotation;
   }

   private static boolean nsEquals(String nsURI_1, String nsURI_2) {
      if (nsURI_1 == null) {
         return nsURI_2 == null;
      } else {
         return nsURI_1.equals(nsURI_2);
      }
   }
}
