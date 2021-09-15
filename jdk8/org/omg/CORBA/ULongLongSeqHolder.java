package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ULongLongSeqHolder implements Streamable {
   public long[] value = null;

   public ULongLongSeqHolder() {
   }

   public ULongLongSeqHolder(long[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = ULongLongSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      ULongLongSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return ULongLongSeqHelper.type();
   }
}
