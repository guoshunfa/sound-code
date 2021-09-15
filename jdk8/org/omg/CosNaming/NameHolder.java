package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameHolder implements Streamable {
   public NameComponent[] value = null;

   public NameHolder() {
   }

   public NameHolder(NameComponent[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NameHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NameHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NameHelper.type();
   }
}
