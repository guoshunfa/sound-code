package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynValueCommon;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynValueCommonImpl extends DynAnyComplexImpl implements DynValueCommon {
   protected boolean isNull;

   private DynValueCommonImpl() {
      this((ORB)null, (Any)null, false);
      this.isNull = true;
   }

   protected DynValueCommonImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
      this.isNull = this.checkInitComponents();
   }

   protected DynValueCommonImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.isNull = true;
   }

   public boolean is_null() {
      return this.isNull;
   }

   public void set_to_null() {
      this.isNull = true;
      this.clearData();
   }

   public void set_to_value() {
      if (this.isNull) {
         this.isNull = false;
      }

   }

   public NameValuePair[] get_members() throws InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.isNull) {
         throw new InvalidValue();
      } else {
         this.checkInitComponents();
         return this.nameValuePairs;
      }
   }

   public NameDynAnyPair[] get_members_as_dyn_any() throws InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.isNull) {
         throw new InvalidValue();
      } else {
         this.checkInitComponents();
         return this.nameDynAnyPairs;
      }
   }

   public void set_members(NameValuePair[] var1) throws TypeMismatch, InvalidValue {
      super.set_members(var1);
      this.isNull = false;
   }

   public void set_members_as_dyn_any(NameDynAnyPair[] var1) throws TypeMismatch, InvalidValue {
      super.set_members_as_dyn_any(var1);
      this.isNull = false;
   }
}
