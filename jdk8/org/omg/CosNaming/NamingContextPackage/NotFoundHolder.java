package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotFoundHolder implements Streamable {
   public NotFound value = null;

   public NotFoundHolder() {
   }

   public NotFoundHolder(NotFound var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NotFoundHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NotFoundHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NotFoundHelper.type();
   }
}
