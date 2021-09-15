package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ValueMember implements IDLEntity {
   public String name;
   public String id;
   public String defined_in;
   public String version;
   public TypeCode type;
   public IDLType type_def;
   public short access;

   public ValueMember() {
   }

   public ValueMember(String var1, String var2, String var3, String var4, TypeCode var5, IDLType var6, short var7) {
      this.name = var1;
      this.id = var2;
      this.defined_in = var3;
      this.version = var4;
      this.type = var5;
      this.type_def = var6;
      this.access = var7;
   }
}
