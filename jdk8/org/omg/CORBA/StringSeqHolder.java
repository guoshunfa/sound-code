package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class StringSeqHolder implements Streamable {
   public String[] value = null;

   public StringSeqHolder() {
   }

   public StringSeqHolder(String[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = StringSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      StringSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return StringSeqHelper.type();
   }
}
