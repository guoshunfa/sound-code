package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class StructMember implements IDLEntity {
   public String name;
   public TypeCode type;
   public IDLType type_def;

   public StructMember() {
   }

   public StructMember(String var1, TypeCode var2, IDLType var3) {
      this.name = var1;
      this.type = var2;
      this.type_def = var3;
   }
}
