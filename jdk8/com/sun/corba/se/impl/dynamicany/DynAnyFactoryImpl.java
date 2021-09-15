package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public class DynAnyFactoryImpl extends LocalObject implements DynAnyFactory {
   private ORB orb;
   private String[] __ids = new String[]{"IDL:omg.org/DynamicAny/DynAnyFactory:1.0"};

   private DynAnyFactoryImpl() {
      this.orb = null;
   }

   public DynAnyFactoryImpl(ORB var1) {
      this.orb = var1;
   }

   public DynAny create_dyn_any(Any var1) throws InconsistentTypeCode {
      return DynAnyUtil.createMostDerivedDynAny(var1, this.orb, true);
   }

   public DynAny create_dyn_any_from_type_code(TypeCode var1) throws InconsistentTypeCode {
      return DynAnyUtil.createMostDerivedDynAny(var1, this.orb);
   }

   public String[] _ids() {
      return (String[])((String[])this.__ids.clone());
   }
}
