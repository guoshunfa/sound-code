package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ValueBase;

public interface ObjectReferenceFactory extends ValueBase {
   Object make_object(String var1, byte[] var2);
}
