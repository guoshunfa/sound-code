package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedProfileHolder implements Streamable {
   public TaggedProfile value = null;

   public TaggedProfileHolder() {
   }

   public TaggedProfileHolder(TaggedProfile var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = TaggedProfileHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      TaggedProfileHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return TaggedProfileHelper.type();
   }
}
