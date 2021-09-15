package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CharHolder implements Streamable {
   public char value;

   public CharHolder() {
   }

   public CharHolder(char var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = var1.read_char();
   }

   public void _write(OutputStream var1) {
      var1.write_char(this.value);
   }

   public TypeCode _type() {
      return ORB.init().get_primitive_tc(TCKind.tk_char);
   }
}
