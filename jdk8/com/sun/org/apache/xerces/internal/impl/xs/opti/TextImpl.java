package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class TextImpl extends DefaultText {
   String fData = null;
   SchemaDOM fSchemaDOM = null;
   int fRow;
   int fCol;

   public TextImpl(StringBuffer str, SchemaDOM sDOM, int row, int col) {
      this.fData = str.toString();
      this.fSchemaDOM = sDOM;
      this.fRow = row;
      this.fCol = col;
      this.rawname = this.prefix = this.localpart = this.uri = null;
      this.nodeType = 3;
   }

   public Node getParentNode() {
      return this.fSchemaDOM.relations[this.fRow][0];
   }

   public Node getPreviousSibling() {
      return this.fCol == 1 ? null : this.fSchemaDOM.relations[this.fRow][this.fCol - 1];
   }

   public Node getNextSibling() {
      return this.fCol == this.fSchemaDOM.relations[this.fRow].length - 1 ? null : this.fSchemaDOM.relations[this.fRow][this.fCol + 1];
   }

   public String getData() throws DOMException {
      return this.fData;
   }

   public int getLength() {
      return this.fData == null ? 0 : this.fData.length();
   }

   public String substringData(int offset, int count) throws DOMException {
      if (this.fData == null) {
         return null;
      } else if (count >= 0 && offset >= 0 && offset <= this.fData.length()) {
         return offset + count >= this.fData.length() ? this.fData.substring(offset) : this.fData.substring(offset, offset + count);
      } else {
         throw new DOMException((short)1, "parameter error");
      }
   }
}
