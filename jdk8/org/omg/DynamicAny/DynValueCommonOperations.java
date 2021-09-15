package org.omg.DynamicAny;

public interface DynValueCommonOperations extends DynAnyOperations {
   boolean is_null();

   void set_to_null();

   void set_to_value();
}
