package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextHolder implements Streamable {
   public NamingContext value = null;

   public NamingContextHolder() {
   }

   public NamingContextHolder(NamingContext var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NamingContextHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NamingContextHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NamingContextHelper.type();
   }
}
