package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class OctetSeqHolder implements Streamable {
   public byte[] value = null;

   public OctetSeqHolder() {
   }

   public OctetSeqHolder(byte[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = OctetSeqHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      OctetSeqHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return OctetSeqHelper.type();
   }
}
