package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ShortSeqHolder implements Streamable {
   public short[] value = null;

   public ShortSeqHolder() {
   }

   public ShortSeqHolder(short[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ShortSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ShortSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ShortSeqHelper.type();
   }
}
