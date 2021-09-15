package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;

public class SchemaNamespaceSupport extends NamespaceSupport {
   public SchemaNamespaceSupport() {
   }

   public SchemaNamespaceSupport(SchemaNamespaceSupport nSupport) {
      this.fNamespaceSize = nSupport.fNamespaceSize;
      if (this.fNamespace.length < this.fNamespaceSize) {
         this.fNamespace = new String[this.fNamespaceSize];
      }

      System.arraycopy(nSupport.fNamespace, 0, this.fNamespace, 0, this.fNamespaceSize);
      this.fCurrentContext = nSupport.fCurrentContext;
      if (this.fContext.length <= this.fCurrentContext) {
         this.fContext = new int[this.fCurrentContext + 1];
      }

      System.arraycopy(nSupport.fContext, 0, this.fContext, 0, this.fCurrentContext + 1);
   }

   public void setEffectiveContext(String[] namespaceDecls) {
      if (namespaceDecls != null && namespaceDecls.length != 0) {
         this.pushContext();
         int newSize = this.fNamespaceSize + namespaceDecls.length;
         if (this.fNamespace.length < newSize) {
            String[] tempNSArray = new String[newSize];
            System.arraycopy(this.fNamespace, 0, tempNSArray, 0, this.fNamespace.length);
            this.fNamespace = tempNSArray;
         }

         System.arraycopy(namespaceDecls, 0, this.fNamespace, this.fNamespaceSize, namespaceDecls.length);
         this.fNamespaceSize = newSize;
      }
   }

   public String[] getEffectiveLocalContext() {
      String[] returnVal = null;
      if (this.fCurrentContext >= 3) {
         int bottomLocalContext = this.fContext[3];
         int copyCount = this.fNamespaceSize - bottomLocalContext;
         if (copyCount > 0) {
            returnVal = new String[copyCount];
            System.arraycopy(this.fNamespace, bottomLocalContext, returnVal, 0, copyCount);
         }
      }

      return returnVal;
   }

   public void makeGlobal() {
      if (this.fCurrentContext >= 3) {
         this.fCurrentContext = 3;
         this.fNamespaceSize = this.fContext[3];
      }

   }
}
