package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingIteratorHolder implements Streamable {
   public BindingIterator value = null;

   public BindingIteratorHolder() {
   }

   public BindingIteratorHolder(BindingIterator var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = BindingIteratorHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      BindingIteratorHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return BindingIteratorHelper.type();
   }
}
