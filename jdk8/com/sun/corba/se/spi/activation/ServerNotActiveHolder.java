package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerNotActiveHolder implements Streamable {
   public ServerNotActive value = null;

   public ServerNotActiveHolder() {
   }

   public ServerNotActiveHolder(ServerNotActive var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerNotActiveHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerNotActiveHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerNotActiveHelper.type();
   }
}
