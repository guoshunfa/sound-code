package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.util.ByteListImpl;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class HexBinaryDV extends TypeValidator {
   public short getAllowedFacets() {
      return 2079;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      byte[] decoded = HexBin.decode(content);
      if (decoded == null) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "hexBinary"});
      } else {
         return new HexBinaryDV.XHex(decoded);
      }
   }

   public int getDataLength(Object value) {
      return ((HexBinaryDV.XHex)value).getLength();
   }

   private static final class XHex extends ByteListImpl {
      public XHex(byte[] data) {
         super(data);
      }

      public synchronized String toString() {
         if (this.canonical == null) {
            this.canonical = HexBin.encode(this.data);
         }

         return this.canonical;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof HexBinaryDV.XHex)) {
            return false;
         } else {
            byte[] odata = ((HexBinaryDV.XHex)obj).data;
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
