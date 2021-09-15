package org.omg.CosNaming;

import org.omg.CORBA.portable.IDLEntity;

public final class Binding implements IDLEntity {
   public NameComponent[] binding_name = null;
   public BindingType binding_type = null;

   public Binding() {
   }

   public Binding(NameComponent[] var1, BindingType var2) {
      this.binding_name = var1;
      this.binding_type = var2;
   }
}
