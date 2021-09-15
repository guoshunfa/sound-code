package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InitialNameServiceHolder implements Streamable {
   public InitialNameService value = null;

   public InitialNameServiceHolder() {
   }

   public InitialNameServiceHolder(InitialNameService var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = InitialNameServiceHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      InitialNameServiceHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return InitialNameServiceHelper.type();
   }
}
