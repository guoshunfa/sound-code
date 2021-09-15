package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidValue;

/** @deprecated */
@Deprecated
public interface DynFixed extends Object, DynAny {
   byte[] get_value();

   void set_value(byte[] var1) throws InvalidValue;
}
