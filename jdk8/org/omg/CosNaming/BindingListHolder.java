package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingListHolder implements Streamable {
   public Binding[] value = null;

   public BindingListHolder() {
   }

   public BindingListHolder(Binding[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = BindingListHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      BindingListHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return BindingListHelper.type();
   }
}
