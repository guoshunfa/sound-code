package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BooleanSeqHolder implements Streamable {
   public boolean[] value = null;

   public BooleanSeqHolder() {
   }

   public BooleanSeqHolder(boolean[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = BooleanSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      BooleanSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return BooleanSeqHelper.type();
   }
}
