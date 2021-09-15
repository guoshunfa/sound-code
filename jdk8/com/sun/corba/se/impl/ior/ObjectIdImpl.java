package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectId;
import java.util.Arrays;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class ObjectIdImpl implements ObjectId {
   private byte[] id;

   public boolean equals(Object var1) {
      if (!(var1 instanceof ObjectIdImpl)) {
         return false;
      } else {
         ObjectIdImpl var2 = (ObjectIdImpl)var1;
         return Arrays.equals(this.id, var2.id);
      }
   }

   public int hashCode() {
      int var1 = 17;

      for(int var2 = 0; var2 < this.id.length; ++var2) {
         var1 = 37 * var1 + this.id[var2];
      }

      return var1;
   }

   public ObjectIdImpl(byte[] var1) {
      this.id = var1;
   }

   public byte[] getId() {
      return this.id;
   }

   public void write(OutputStream var1) {
      var1.write_long(this.id.length);
      var1.write_octet_array(this.id, 0, this.id.length);
   }
}
