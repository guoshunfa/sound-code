package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LocatorHolder implements Streamable {
   public Locator value = null;

   public LocatorHolder() {
   }

   public LocatorHolder(Locator var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = LocatorHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      LocatorHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return LocatorHelper.type();
   }
}
