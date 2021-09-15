package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class POANameHolder implements Streamable {
   public String[] value = null;

   public POANameHolder() {
   }

   public POANameHolder(String[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = POANameHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      POANameHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return POANameHelper.type();
   }
}
