package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import java.util.Arrays;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class GenericIdentifiable implements Identifiable {
   private int id;
   private byte[] data;

   public GenericIdentifiable(int var1, InputStream var2) {
      this.id = var1;
      this.data = EncapsulationUtility.readOctets(var2);
   }

   public int getId() {
      return this.id;
   }

   public void write(OutputStream var1) {
      var1.write_ulong(this.data.length);
      var1.write_octet_array(this.data, 0, this.data.length);
   }

   public String toString() {
      return "GenericIdentifiable[id=" + this.getId() + "]";
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof GenericIdentifiable)) {
         return false;
      } else {
         GenericIdentifiable var2 = (GenericIdentifiable)var1;
         return this.getId() == var2.getId() && Arrays.equals(this.getData(), var2.getData());
      }
   }

   public int hashCode() {
      int var1 = 17;

      for(int var2 = 0; var2 < this.data.length; ++var2) {
         var1 = 37 * var1 + this.data[var2];
      }

      return var1;
   }

   public GenericIdentifiable(int var1, byte[] var2) {
      this.id = var1;
      this.data = (byte[])((byte[])var2.clone());
   }

   public byte[] getData() {
      return this.data;
   }
}
