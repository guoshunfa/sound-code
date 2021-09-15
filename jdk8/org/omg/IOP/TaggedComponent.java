package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TaggedComponent implements IDLEntity {
   public int tag = 0;
   public byte[] component_data = null;

   public TaggedComponent() {
   }

   public TaggedComponent(int var1, byte[] var2) {
      this.tag = var1;
      this.component_data = var2;
   }
}
