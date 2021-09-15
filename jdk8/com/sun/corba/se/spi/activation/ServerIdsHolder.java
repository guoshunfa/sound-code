package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerIdsHolder implements Streamable {
   public int[] value = null;

   public ServerIdsHolder() {
   }

   public ServerIdsHolder(int[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerIdsHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerIdsHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerIdsHelper.type();
   }
}
