package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyUninstalledHolder implements Streamable {
   public ServerAlreadyUninstalled value = null;

   public ServerAlreadyUninstalledHolder() {
   }

   public ServerAlreadyUninstalledHolder(ServerAlreadyUninstalled var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerAlreadyUninstalledHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerAlreadyUninstalledHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerAlreadyUninstalledHelper.type();
   }
}
