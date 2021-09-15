package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynValueBoxOperations extends DynValueCommonOperations {
   Any get_boxed_value() throws InvalidValue;

   void set_boxed_value(Any var1) throws TypeMismatch;

   DynAny get_boxed_value_as_dyn_any() throws InvalidValue;

   void set_boxed_value_as_dyn_any(DynAny var1) throws TypeMismatch;
}
