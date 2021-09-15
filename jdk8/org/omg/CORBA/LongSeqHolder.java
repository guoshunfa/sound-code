package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongSeqHolder implements Streamable {
   public int[] value = null;

   public LongSeqHolder() {
   }

   public LongSeqHolder(int[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = LongSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      LongSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return LongSeqHelper.type();
   }
}
