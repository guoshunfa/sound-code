package com.sun.org.omg.CORBA;

import org.omg.CORBA.IDLType;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class ParameterDescription implements IDLEntity {
   public String name = null;
   public TypeCode type = null;
   public IDLType type_def = null;
   public ParameterMode mode = null;

   public ParameterDescription() {
   }

   public ParameterDescription(String var1, TypeCode var2, IDLType var3, ParameterMode var4) {
      this.name = var1;
      this.type = var2;
      this.type_def = var3;
      this.mode = var4;
   }
}
