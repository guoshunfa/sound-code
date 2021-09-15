package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyActiveHolder implements Streamable {
   public ServerAlreadyActive value = null;

   public ServerAlreadyActiveHolder() {
   }

   public ServerAlreadyActiveHolder(ServerAlreadyActive var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerAlreadyActiveHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerAlreadyActiveHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerAlreadyActiveHelper.type();
   }
}
