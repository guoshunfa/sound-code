package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class UShortSeqHolder implements Streamable {
   public short[] value = null;

   public UShortSeqHolder() {
   }

   public UShortSeqHolder(short[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = UShortSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      UShortSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return UShortSeqHelper.type();
   }
}
