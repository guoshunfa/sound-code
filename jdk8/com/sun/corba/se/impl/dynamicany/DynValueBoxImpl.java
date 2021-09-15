package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynValueBox;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynValueBoxImpl extends DynValueCommonImpl implements DynValueBox {
   private DynValueBoxImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynValueBoxImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected DynValueBoxImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
   }

   public Any get_boxed_value() throws InvalidValue {
      if (this.isNull) {
         throw new InvalidValue();
      } else {
         this.checkInitAny();
         return this.any;
      }
   }

   public void set_boxed_value(Any var1) throws TypeMismatch {
      if (!this.isNull && !var1.type().equal(this.type())) {
         throw new TypeMismatch();
      } else {
         this.clearData();
         this.any = var1;
         this.representations = 2;
         this.index = 0;
         this.isNull = false;
      }
   }

   public DynAny get_boxed_value_as_dyn_any() throws InvalidValue {
      if (this.isNull) {
         throw new InvalidValue();
      } else {
         this.checkInitComponents();
         return this.components[0];
      }
   }

   public void set_boxed_value_as_dyn_any(DynAny var1) throws TypeMismatch {
      if (!this.isNull && !var1.type().equal(this.type())) {
         throw new TypeMismatch();
      } else {
         this.clearData();
         this.components = new DynAny[]{var1};
         this.representations = 4;
         this.index = 0;
         this.isNull = false;
      }
   }

   protected boolean initializeComponentsFromAny() {
      try {
         this.components = new DynAny[]{DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, false)};
         return true;
      } catch (InconsistentTypeCode var2) {
         return false;
      }
   }

   protected boolean initializeComponentsFromTypeCode() {
      try {
         this.any = DynAnyUtil.createDefaultAnyOfType(this.any.type(), this.orb);
         this.components = new DynAny[]{DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, false)};
         return true;
      } catch (InconsistentTypeCode var2) {
         return false;
      }
   }

   protected boolean initializeAnyFromComponents() {
      this.any = this.getAny(this.components[0]);
      return true;
   }
}
