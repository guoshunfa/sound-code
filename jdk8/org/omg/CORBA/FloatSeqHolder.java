package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class FloatSeqHolder implements Streamable {
   public float[] value = null;

   public FloatSeqHolder() {
   }

   public FloatSeqHolder(float[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = FloatSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      FloatSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return FloatSeqHelper.type();
   }
}
