package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidAddressHolder implements Streamable {
   public InvalidAddress value = null;

   public InvalidAddressHolder() {
   }

   public InvalidAddressHolder(InvalidAddress var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = InvalidAddressHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      InvalidAddressHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return InvalidAddressHelper.type();
   }
}
