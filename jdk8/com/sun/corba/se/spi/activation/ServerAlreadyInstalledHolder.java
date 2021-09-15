package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyInstalledHolder implements Streamable {
   public ServerAlreadyInstalled value = null;

   public ServerAlreadyInstalledHolder() {
   }

   public ServerAlreadyInstalledHolder(ServerAlreadyInstalled var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerAlreadyInstalledHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerAlreadyInstalledHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerAlreadyInstalledHelper.type();
   }
}
