package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CharSeqHolder implements Streamable {
   public char[] value = null;

   public CharSeqHolder() {
   }

   public CharSeqHolder(char[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = CharSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      CharSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return CharSeqHelper.type();
   }
}
