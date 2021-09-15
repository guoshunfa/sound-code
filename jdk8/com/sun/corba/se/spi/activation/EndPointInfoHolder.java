package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class EndPointInfoHolder implements Streamable {
   public EndPointInfo value = null;

   public EndPointInfoHolder() {
   }

   public EndPointInfoHolder(EndPointInfo var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = EndPointInfoHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      EndPointInfoHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return EndPointInfoHelper.type();
   }
}
