package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBAlreadyRegisteredHolder implements Streamable {
   public ORBAlreadyRegistered value = null;

   public ORBAlreadyRegisteredHolder() {
   }

   public ORBAlreadyRegisteredHolder(ORBAlreadyRegistered var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ORBAlreadyRegisteredHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ORBAlreadyRegisteredHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ORBAlreadyRegisteredHelper.type();
   }
}
