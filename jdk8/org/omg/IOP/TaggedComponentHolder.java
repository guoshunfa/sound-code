package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedComponentHolder implements Streamable {
   public TaggedComponent value = null;

   public TaggedComponentHolder() {
   }

   public TaggedComponentHolder(TaggedComponent var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = TaggedComponentHelper.read(var1);
   }

   public void _write(OutputStream var1) {
      TaggedComponentHelper.write(var1, this.value);
   }

   public TypeCode _type() {
      return TaggedComponentHelper.type();
   }
}
