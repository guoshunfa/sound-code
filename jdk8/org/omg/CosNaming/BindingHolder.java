package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingHolder implements Streamable {
   public Binding value = null;

   public BindingHolder() {
   }

   public BindingHolder(Binding var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = BindingHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      BindingHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return BindingHelper.type();
   }
}
