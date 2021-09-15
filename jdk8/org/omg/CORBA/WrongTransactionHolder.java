package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class WrongTransactionHolder implements Streamable {
   public WrongTransaction value = null;

   public WrongTransactionHolder() {
   }

   public WrongTransactionHolder(WrongTransaction var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = WrongTransactionHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      WrongTransactionHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return WrongTransactionHelper.type();
   }
}
