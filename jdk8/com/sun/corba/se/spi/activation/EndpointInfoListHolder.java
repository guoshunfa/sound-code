package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class EndpointInfoListHolder implements Streamable {
   public EndPointInfo[] value = null;

   public EndpointInfoListHolder() {
   }

   public EndpointInfoListHolder(EndPointInfo[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = EndpointInfoListHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      EndpointInfoListHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return EndpointInfoListHelper.type();
   }
}
