package org.omg.CORBA;

import java.math.BigDecimal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class FixedHolder implements Streamable {
   public BigDecimal value;

   public FixedHolder() {
   }

   public FixedHolder(BigDecimal var1) {
      this.value = var1;
   }

   public void _read(InputStream var1) {
      this.value = var1.read_fixed();
   }

   public void _write(OutputStream var1) {
      var1.write_fixed(this.value);
   }

   public TypeCode _type() {
      return ORB.init().get_primitive_tc(TCKind.tk_fixed);
   }
}
