package org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;

public interface Streamable {
   void _read(InputStream var1);

   void _write(OutputStream var1);

   TypeCode _type();
}
