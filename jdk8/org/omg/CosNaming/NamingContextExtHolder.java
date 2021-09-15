package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextExtHolder implements Streamable {
   public NamingContextExt value = null;

   public NamingContextExtHolder() {
   }

   public NamingContextExtHolder(NamingContextExt var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NamingContextExtHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NamingContextExtHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NamingContextExtHelper.type();
   }
}
