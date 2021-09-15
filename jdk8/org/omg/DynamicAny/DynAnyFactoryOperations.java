package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public interface DynAnyFactoryOperations {
   DynAny create_dyn_any(Any var1) throws InconsistentTypeCode;

   DynAny create_dyn_any_from_type_code(TypeCode var1) throws InconsistentTypeCode;
}
