package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TaggedProfile implements IDLEntity {
   public int tag = 0;
   public byte[] profile_data = null;

   public TaggedProfile() {
   }

   public TaggedProfile(int var1, byte[] var2) {
      this.tag = var1;
      this.profile_data = var2;
   }
}
