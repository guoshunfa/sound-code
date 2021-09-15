package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class UnionMember implements IDLEntity {
   public String name;
   public Any label;
   public TypeCode type;
   public IDLType type_def;

   public UnionMember() {
   }

   public UnionMember(String var1, Any var2, TypeCode var3, IDLType var4) {
      this.name = var1;
      this.label = var2;
      this.type = var3;
      this.type_def = var4;
   }
}
