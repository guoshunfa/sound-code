package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ULongSeqHolder implements Streamable {
   public int[] value = null;

   public ULongSeqHolder() {
   }

   public ULongSeqHolder(int[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ULongSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ULongSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ULongSeqHelper.type();
   }
}
