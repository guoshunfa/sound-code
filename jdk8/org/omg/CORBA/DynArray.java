package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

/** @deprecated */
@Deprecated
public interface DynArray extends Object, DynAny {
   Any[] get_elements();

   void set_elements(Any[] var1) throws InvalidSeq;
}
