package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynArray;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

public class DynArrayImpl extends DynAnyCollectionImpl implements DynArray {
   private DynArrayImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynArrayImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected DynArrayImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
   }

   protected boolean initializeComponentsFromAny() {
      TypeCode var1 = this.any.type();
      int var2 = this.getBound();
      TypeCode var3 = this.getContentType();

      InputStream var4;
      try {
         var4 = this.any.create_input_stream();
      } catch (BAD_OPERATION var8) {
         return false;
      }

      this.components = new DynAny[var2];
      this.anys = new Any[var2];

      for(int var5 = 0; var5 < var2; ++var5) {
         this.anys[var5] = DynAnyUtil.extractAnyFromStream(var3, var4, this.orb);

         try {
            this.components[var5] = DynAnyUtil.createMostDerivedDynAny(this.anys[var5], this.orb, false);
         } catch (InconsistentTypeCode var7) {
         }
      }

      return true;
   }

   protected boolean initializeComponentsFromTypeCode() {
      TypeCode var1 = this.any.type();
      int var2 = this.getBound();
      TypeCode var3 = this.getContentType();
      this.components = new DynAny[var2];
      this.anys = new Any[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         this.createDefaultComponentAt(var4, var3);
      }

      return true;
   }

   protected void checkValue(Object[] var1) throws InvalidValue {
      if (var1 == null || var1.length != this.getBound()) {
         throw new InvalidValue();
      }
   }
}
