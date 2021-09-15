package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class ExceptionDescription implements IDLEntity {
   public String name = null;
   public String id = null;
   public String defined_in = null;
   public String version = null;
   public TypeCode type = null;

   public ExceptionDescription() {
   }

   public ExceptionDescription(String var1, String var2, String var3, String var4, TypeCode var5) {
      this.name = var1;
      this.id = var2;
      this.defined_in = var3;
      this.version = var4;
      this.type = var5;
   }
}
