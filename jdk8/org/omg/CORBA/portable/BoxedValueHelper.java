package org.omg.CORBA.portable;

import java.io.Serializable;

public interface BoxedValueHelper {
   Serializable read_value(InputStream var1);

   void write_value(OutputStream var1, Serializable var2);

   String get_id();
}
