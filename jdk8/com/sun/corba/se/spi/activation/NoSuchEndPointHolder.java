package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NoSuchEndPointHolder implements Streamable {
   public NoSuchEndPoint value = null;

   public NoSuchEndPointHolder() {
   }

   public NoSuchEndPointHolder(NoSuchEndPoint var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = NoSuchEndPointHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      NoSuchEndPointHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return NoSuchEndPointHelper.type();
   }
}
