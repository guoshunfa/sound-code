package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerLocationPerORBHolder implements Streamable {
   public ServerLocationPerORB value = null;

   public ServerLocationPerORBHolder() {
   }

   public ServerLocationPerORBHolder(ServerLocationPerORB var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServerLocationPerORBHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServerLocationPerORBHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServerLocationPerORBHelper.type();
   }
}
