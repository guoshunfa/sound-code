package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CurrentHolder implements Streamable {
   public Current value = null;

   public CurrentHolder() {
   }

   public CurrentHolder(Current var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = CurrentHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      CurrentHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return CurrentHelper.type();
   }
}
