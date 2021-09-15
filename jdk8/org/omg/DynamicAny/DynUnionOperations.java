package org.omg.DynamicAny;

import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynUnionOperations extends DynAnyOperations {
   DynAny get_discriminator();

   void set_discriminator(DynAny var1) throws TypeMismatch;

   void set_to_default_member() throws TypeMismatch;

   void set_to_no_active_member() throws TypeMismatch;

   boolean has_no_active_member();

   TCKind discriminator_kind();

   TCKind member_kind() throws InvalidValue;

   DynAny member() throws InvalidValue;

   String member_name() throws InvalidValue;
}
