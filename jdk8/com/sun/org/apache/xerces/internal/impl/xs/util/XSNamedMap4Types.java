package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public final class XSNamedMap4Types extends XSNamedMapImpl {
   private final short fType;

   public XSNamedMap4Types(String namespace, SymbolHash map, short type) {
      super(namespace, map);
      this.fType = type;
   }

   public XSNamedMap4Types(String[] namespaces, SymbolHash[] maps, int num, short type) {
      super(namespaces, maps, num);
      this.fType = type;
   }

   public synchronized int getLength() {
      if (this.fLength == -1) {
         int length = 0;

         int pos;
         for(pos = 0; pos < this.fNSNum; ++pos) {
            length += this.fMaps[pos].getLength();
         }

         pos = 0;
         XSObject[] array = new XSObject[length];

         for(int i = 0; i < this.fNSNum; ++i) {
            pos += this.fMaps[i].getValues(array, pos);
         }

         this.fLength = 0;
         this.fArray = new XSObject[length];

         for(int i = 0; i < length; ++i) {
            XSTypeDefinition type = (XSTypeDefinition)array[i];
            if (type.getTypeCategory() == this.fType) {
               this.fArray[this.fLength++] = type;
            }
         }
      }

      return this.fLength;
   }

   public XSObject itemByName(String namespace, String localName) {
      for(int i = 0; i < this.fNSNum; ++i) {
         if (isEqual(namespace, this.fNamespaces[i])) {
            XSTypeDefinition type = (XSTypeDefinition)this.fMaps[i].get(localName);
            if (type != null && type.getTypeCategory() == this.fType) {
               return type;
            }

            return null;
         }
      }

      return null;
   }

   public synchronized XSObject item(int index) {
      if (this.fArray == null) {
         this.getLength();
      }

      return index >= 0 && index < this.fLength ? this.fArray[index] : null;
   }
}
