package org.omg.CORBA;

/** @deprecated */
@Deprecated
public interface DynEnum extends Object, DynAny {
   String value_as_string();

   void value_as_string(String var1);

   int value_as_ulong();

   void value_as_ulong(int var1);
}
