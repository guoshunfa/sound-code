package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class AnySeqHolder implements Streamable {
   public Any[] value = null;

   public AnySeqHolder() {
   }

   public AnySeqHolder(Any[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = AnySeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      AnySeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return AnySeqHelper.type();
   }
}
