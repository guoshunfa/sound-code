package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class IOR implements IDLEntity {
   public String type_id = null;
   public TaggedProfile[] profiles = null;

   public IOR() {
   }

   public IOR(String var1, TaggedProfile[] var2) {
      this.type_id = var1;
      this.profiles = var2;
   }
}
