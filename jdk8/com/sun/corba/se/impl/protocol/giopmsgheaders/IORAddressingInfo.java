package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.portable.IDLEntity;
import org.omg.IOP.IOR;

public final class IORAddressingInfo implements IDLEntity {
   public int selected_profile_index = 0;
   public IOR ior = null;

   public IORAddressingInfo() {
   }

   public IORAddressingInfo(int var1, IOR var2) {
      this.selected_profile_index = var1;
      this.ior = var2;
   }
}
