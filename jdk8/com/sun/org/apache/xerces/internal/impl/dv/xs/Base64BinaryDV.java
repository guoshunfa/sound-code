package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.org.apache.xerces.internal.impl.dv.util.ByteListImpl;

public class Base64BinaryDV extends TypeValidator {
   public short getAllowedFacets() {
      return 2079;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      byte[] decoded = Base64.decode(content);
      if (decoded == null) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "base64Binary"});
      } else {
         return new Base64BinaryDV.XBase64(decoded);
      }
   }

   public int getDataLength(Object value) {
      return ((Base64BinaryDV.XBase64)value).getLength();
   }

   private static final class XBase64 extends ByteListImpl {
      public XBase64(byte[] data) {
         super(data);
      }

      public synchronized String toString() {
         if (this.canonical == null) {
            this.canonical = Base64.encode(this.data);
         }

         return this.canonical;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Base64BinaryDV.XBase64)) {
            return false;
         } else {
            byte[] odata = ((Base64BinaryDV.XBase64)obj).data;
            int len = this.data.length;
            if (len != odata.length) {
               return false;
            } else {
               for(int i = 0; i < len; ++i) {
                  if (this.data[i] != odata[i]) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hash = 0;

         for(int i = 0; i < this.data.length; ++i) {
            hash = hash * 37 + (this.data[i] & 255);
         }

         return hash;
      }
   }
}
