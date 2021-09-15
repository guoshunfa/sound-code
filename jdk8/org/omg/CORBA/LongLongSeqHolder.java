package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongLongSeqHolder implements Streamable {
   public long[] value = null;

   public LongLongSeqHolder() {
   }

   public LongLongSeqHolder(long[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = LongLongSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      LongLongSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return LongLongSeqHelper.type();
   }
}
