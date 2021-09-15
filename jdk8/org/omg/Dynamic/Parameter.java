package org.omg.Dynamic;

import org.omg.CORBA.Any;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.portable.IDLEntity;

public final class Parameter implements IDLEntity {
   public Any argument = null;
   public ParameterMode mode = null;

   public Parameter() {
   }

   public Parameter(Any var1, ParameterMode var2) {
      this.argument = var1;
      this.mode = var2;
   }
}
