package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceInformationHolder implements Streamable {
   public ServiceInformation value;

   public ServiceInformationHolder() {
      this((ServiceInformation)null);
   }

   public ServiceInformationHolder(ServiceInformation var1) {
      this.value = var1;
   }

   public void _write(OutputStream var1) {
      ServiceInformationHelper.write(var1, this.value);
   }

   public void _read(InputStream var1) {
      this.value = ServiceInformationHelper.read(var1);
   }

   public TypeCode _type() {
      return ServiceInformationHelper.type();
   }
}
