package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyRegisteredHolder implements Streamable {
   public ServerAlreadyRegistered value = null;

   public ServerAlreadyRegisteredHolder() {
   }

   public ServerAlreadyRegisteredHolder(ServerAlreadyRegistered var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerAlreadyRegisteredHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerAlreadyRegisteredHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerAlreadyRegisteredHelper.type();
   }
}
