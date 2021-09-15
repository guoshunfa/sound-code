package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ActivatorHolder implements Streamable {
   public Activator value = null;

   public ActivatorHolder() {
   }

   public ActivatorHolder(Activator var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ActivatorHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ActivatorHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ActivatorHelper.type();
   }
}
