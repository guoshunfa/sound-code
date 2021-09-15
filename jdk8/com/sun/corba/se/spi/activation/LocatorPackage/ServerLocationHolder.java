package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerLocationHolder implements Streamable {
   public ServerLocation value = null;

   public ServerLocationHolder() {
   }

   public ServerLocationHolder(ServerLocation var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerLocationHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerLocationHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerLocationHelper.type();
   }
}
