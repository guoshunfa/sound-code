package com.sun.org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;

/** @deprecated */
@Deprecated
public interface ValueHelper extends BoxedValueHelper {
   Class get_class();

   String[] get_truncatable_base_ids();

   TypeCode get_type();
}
