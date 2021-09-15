package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CannotProceedHolder implements Streamable {
   public CannotProceed value = null;

   public CannotProceedHolder() {
   }

   public CannotProceedHolder(CannotProceed var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = CannotProceedHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      CannotProceedHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return CannotProceedHelper.type();
   }
}
