package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class MultipleComponentProfileHolder implements Streamable {
   public TaggedComponent[] value = null;

   public MultipleComponentProfileHolder() {
   }

   public MultipleComponentProfileHolder(TaggedComponent[] var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = MultipleComponentProfileHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      MultipleComponentProfileHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return MultipleComponentProfileHelper.type();
   }
}
