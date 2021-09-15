package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceContext implements IDLEntity {
   public int context_id = 0;
   public byte[] context_data = null;

   public ServiceContext() {
   }

   public ServiceContext(int var1, byte[] var2) {
      this.context_id = var1;
      this.context_data = var2;
   }
}
