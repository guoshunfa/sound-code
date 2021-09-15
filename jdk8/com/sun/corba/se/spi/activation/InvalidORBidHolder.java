package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidORBidHolder implements Streamable {
   public InvalidORBid value = null;

   public InvalidORBidHolder() {
   }

   public InvalidORBidHolder(InvalidORBid var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = InvalidORBidHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      InvalidORBidHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return InvalidORBidHelper.type();
   }
}
