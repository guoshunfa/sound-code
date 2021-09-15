package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyCollectionImpl extends DynAnyConstructedImpl {
   Any[] anys;

   private DynAnyCollectionImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynAnyCollectionImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
      this.anys = null;
   }

   protected DynAnyCollectionImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.anys = null;
   }

   protected void createDefaultComponentAt(int var1, TypeCode var2) {
      try {
         this.components[var1] = DynAnyUtil.createMostDerivedDynAny(var2, this.orb);
      } catch (InconsistentTypeCode var4) {
      }

      this.anys[var1] = this.getAny(this.components[var1]);
   }

   protected TypeCode getContentType() {
      try {
         return this.any.type().content_type();
      } catch (BadKind var2) {
         return null;
      }
   }

   protected int getBound() {
      try {
         return this.any.type().length();
      } catch (BadKind var2) {
         return 0;
      }
   }

   public Any[] get_elements() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.checkInitComponents() ? this.anys : null;
      }
   }

   protected abstract void checkValue(Object[] var1) throws InvalidValue;

   public void set_elements(Any[] var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.checkValue(var1);
         this.components = new DynAny[var1.length];
         this.anys = var1;
         TypeCode var2 = this.getContentType();

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] == null) {
               this.clearData();
               throw new InvalidValue();
            }

            if (!var1[var3].type().equal(var2)) {
               this.clearData();
               throw new TypeMismatch();
            }

            try {
               this.components[var3] = DynAnyUtil.createMostDerivedDynAny(var1[var3], this.orb, false);
            } catch (InconsistentTypeCode var5) {
               throw new InvalidValue();
            }
         }

         this.index = var1.length == 0 ? -1 : 0;
         this.representations = 4;
      }
   }

   public DynAny[] get_elements_as_dyn_any() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.checkInitComponents() ? this.components : null;
      }
   }

   public void set_elements_as_dyn_any(DynAny[] var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.checkValue(var1);
         this.components = var1 == null ? emptyComponents : var1;
         this.anys = new Any[var1.length];
         TypeCode var2 = this.getContentType();

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] == null) {
               this.clearData();
               throw new InvalidValue();
            }

            if (!var1[var3].type().equal(var2)) {
               this.clearData();
               throw new TypeMismatch();
            }

            this.anys[var3] = this.getAny(var1[var3]);
         }

         this.index = var1.length == 0 ? -1 : 0;
         this.representations = 4;
      }
   }
}
