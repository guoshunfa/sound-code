package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class WStringSeqHolder implements Streamable {
   public String[] value = null;

   public WStringSeqHolder() {
   }

   public WStringSeqHolder(String[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = WStringSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      WStringSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return WStringSeqHelper.type();
   }
}
