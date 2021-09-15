package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerHeldDownHolder implements Streamable {
   public ServerHeldDown value = null;

   public ServerHeldDownHolder() {
   }

   public ServerHeldDownHolder(ServerHeldDown var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerHeldDownHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerHeldDownHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerHeldDownHelper.type();
   }
}
