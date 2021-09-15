package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class Encoding implements IDLEntity {
   public short format = 0;
   public byte major_version = 0;
   public byte minor_version = 0;

   public Encoding() {
   }

   public Encoding(short var1, byte var2, byte var3) {
      this.format = var1;
      this.major_version = var2;
      this.minor_version = var3;
   }
}
