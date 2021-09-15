package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotEmptyHolder implements Streamable {
   public NotEmpty value = null;

   public NotEmptyHolder() {
   }

   public NotEmptyHolder(NotEmpty var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NotEmptyHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NotEmptyHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NotEmptyHelper.type();
   }
}
