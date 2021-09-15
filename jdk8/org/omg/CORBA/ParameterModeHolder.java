package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ParameterModeHolder implements Streamable {
   public ParameterMode value = null;

   public ParameterModeHolder() {
   }

   public ParameterModeHolder(ParameterMode var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ParameterModeHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ParameterModeHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ParameterModeHelper.type();
   }
}
