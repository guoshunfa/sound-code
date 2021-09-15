package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TypeCodeHolder implements Streamable {
   public TypeCode value;

   public TypeCodeHolder() {
   }

   public TypeCodeHolder(TypeCode var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = var1.read_TypeCode();
   }

   public void _write(OutputStream var1) {
      var1.write_TypeCode(this.value);
   }

   public TypeCode _type() {
      return ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
   }
}
