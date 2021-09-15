package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerManagerHolder implements Streamable {
   public ServerManager value = null;

   public ServerManagerHolder() {
   }

   public ServerManagerHolder(ServerManager var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerManagerHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerManagerHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerManagerHelper.type();
   }
}
