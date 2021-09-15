package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerHolder implements Streamable {
   public Server value = null;

   public ServerHolder() {
   }

   public ServerHolder(Server var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerHelper.type();
   }
}
