package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class WCharSeqHolder implements Streamable {
   public char[] value = null;

   public WCharSeqHolder() {
   }

   public WCharSeqHolder(char[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = WCharSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      WCharSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return WCharSeqHelper.type();
   }
}
