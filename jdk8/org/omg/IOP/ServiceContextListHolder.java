package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceContextListHolder implements Streamable {
   public ServiceContext[] value = null;

   public ServiceContextListHolder() {
   }

   public ServiceContextListHolder(ServiceContext[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ServiceContextListHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ServiceContextListHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ServiceContextListHelper.type();
   }
}
