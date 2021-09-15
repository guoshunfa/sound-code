package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingTypeHolder implements Streamable {
   public BindingType value = null;

   public BindingTypeHolder() {
   }

   public BindingTypeHolder(BindingType var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = BindingTypeHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      BindingTypeHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return BindingTypeHelper.type();
   }
}
