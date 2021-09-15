package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

public interface DynEnumOperations extends DynAnyOperations {
   String get_as_string();

   void set_as_string(String var1) throws InvalidValue;

   int get_as_ulong();

   void set_as_ulong(int var1) throws InvalidValue;
}
