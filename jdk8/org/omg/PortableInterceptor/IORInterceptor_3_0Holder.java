package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class IORInterceptor_3_0Holder implements Streamable {
   public IORInterceptor_3_0 value = null;

   public IORInterceptor_3_0Holder() {
   }

   public IORInterceptor_3_0Holder(IORInterceptor_3_0 var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = IORInterceptor_3_0Helper.read(var1);
   }

   public void _write(OutputStream var1) {
      IORInterceptor_3_0Helper.write(var1, this.value);
   }

   public TypeCode _type() {
      return IORInterceptor_3_0Helper.type();
   }
}
