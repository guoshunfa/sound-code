package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynArrayOperations extends DynAnyOperations {
   Any[] get_elements();

   void set_elements(Any[] var1) throws TypeMismatch, InvalidValue;

   DynAny[] get_elements_as_dyn_any();

   void set_elements_as_dyn_any(DynAny[] var1) throws TypeMismatch, InvalidValue;
}
