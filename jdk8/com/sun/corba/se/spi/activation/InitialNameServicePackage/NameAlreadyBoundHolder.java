package com.sun.corba.se.spi.activation.InitialNameServicePackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameAlreadyBoundHolder implements Streamable {
   public NameAlreadyBound value = null;

   public NameAlreadyBoundHolder() {
   }

   public NameAlreadyBoundHolder(NameAlreadyBound var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NameAlreadyBoundHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NameAlreadyBoundHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NameAlreadyBoundHelper.type();
   }
}
