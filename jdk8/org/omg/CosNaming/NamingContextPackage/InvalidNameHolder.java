package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidNameHolder implements Streamable {
   public InvalidName value = null;

   public InvalidNameHolder() {
   }

   public InvalidNameHolder(InvalidName var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = InvalidNameHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      InvalidNameHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return InvalidNameHelper.type();
   }
}
