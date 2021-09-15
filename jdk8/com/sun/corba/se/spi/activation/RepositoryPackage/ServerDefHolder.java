package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerDefHolder implements Streamable {
   public ServerDef value = null;

   public ServerDefHolder() {
   }

   public ServerDefHolder(ServerDef var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerDefHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerDefHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerDefHelper.type();
   }
}
