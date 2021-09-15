package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class DoubleSeqHolder implements Streamable {
   public double[] value = null;

   public DoubleSeqHolder() {
   }

   public DoubleSeqHolder(double[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = DoubleSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      DoubleSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return DoubleSeqHelper.type();
   }
}
