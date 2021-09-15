package org.omg.DynamicAny;

import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynStructOperations extends DynAnyOperations {
   String current_member_name() throws TypeMismatch, InvalidValue;

   TCKind current_member_kind() throws TypeMismatch, InvalidValue;

   NameValuePair[] get_members();

   void set_members(NameValuePair[] var1) throws TypeMismatch, InvalidValue;

   NameDynAnyPair[] get_members_as_dyn_any();

   void set_members_as_dyn_any(NameDynAnyPair[] var1) throws TypeMismatch, InvalidValue;
}
