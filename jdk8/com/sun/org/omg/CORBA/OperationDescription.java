package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class OperationDescription implements IDLEntity {
   public String name = null;
   public String id = null;
   public String defined_in = null;
   public String version = null;
   public TypeCode result = null;
   public OperationMode mode = null;
   public String[] contexts = null;
   public ParameterDescription[] parameters = null;
   public ExceptionDescription[] exceptions = null;

   public OperationDescription() {
   }

   public OperationDescription(String var1, String var2, String var3, String var4, TypeCode var5, OperationMode var6, String[] var7, ParameterDescription[] var8, ExceptionDescription[] var9) {
      this.name = var1;
      this.id = var2;
      this.defined_in = var3;
      this.version = var4;
      this.result = var5;
      this.mode = var6;
      this.contexts = var7;
      this.parameters = var8;
      this.exceptions = var9;
   }
}
