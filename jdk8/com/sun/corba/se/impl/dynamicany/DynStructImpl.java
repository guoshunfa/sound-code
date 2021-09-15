package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynStruct;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

public class DynStructImpl extends DynAnyComplexImpl implements DynStruct {
   private DynStructImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynStructImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected DynStructImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.index = 0;
   }

   public NameValuePair[] get_members() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.checkInitComponents();
         return this.nameValuePairs;
      }
   }

   public NameDynAnyPair[] get_members_as_dyn_any() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.checkInitComponents();
         return this.nameDynAnyPairs;
      }
   }
}
