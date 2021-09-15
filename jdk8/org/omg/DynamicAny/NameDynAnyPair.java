package org.omg.DynamicAny;

import org.omg.CORBA.portable.IDLEntity;

public final class NameDynAnyPair implements IDLEntity {
   public String id = null;
   public DynAny value = null;

   public NameDynAnyPair() {
   }

   public NameDynAnyPair(String var1, DynAny var2) {
      this.id = var1;
      this.value = var2;
   }
}
