package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBidListHolder implements Streamable {
   public String[] value = null;

   public ORBidListHolder() {
   }

   public ORBidListHolder(String[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ORBidListHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ORBidListHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ORBidListHelper.type();
   }
}
