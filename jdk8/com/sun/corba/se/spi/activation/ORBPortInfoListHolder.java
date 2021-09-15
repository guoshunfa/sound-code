package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBPortInfoListHolder implements Streamable {
   public ORBPortInfo[] value = null;

   public ORBPortInfoListHolder() {
   }

   public ORBPortInfoListHolder(ORBPortInfo[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ORBPortInfoListHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ORBPortInfoListHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ORBPortInfoListHelper.type();
   }
}
