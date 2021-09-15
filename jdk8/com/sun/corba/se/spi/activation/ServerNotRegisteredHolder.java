package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerNotRegisteredHolder implements Streamable {
   public ServerNotRegistered value = null;

   public ServerNotRegisteredHolder() {
   }

   public ServerNotRegisteredHolder(ServerNotRegistered var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerNotRegisteredHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerNotRegisteredHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerNotRegisteredHelper.type();
   }
}
