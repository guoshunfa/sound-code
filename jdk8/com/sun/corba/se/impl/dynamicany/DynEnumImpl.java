package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynEnum;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynEnumImpl extends DynAnyBasicImpl implements DynEnum {
   int currentEnumeratorIndex;

   private DynEnumImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynEnumImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
      this.currentEnumeratorIndex = -1;
      this.index = -1;

      try {
         this.currentEnumeratorIndex = this.any.extract_long();
      } catch (BAD_OPERATION var5) {
         this.currentEnumeratorIndex = 0;
         this.any.type(this.any.type());
         this.any.insert_long(0);
      }

   }

   protected DynEnumImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.currentEnumeratorIndex = -1;
      this.index = -1;
      this.currentEnumeratorIndex = 0;
      this.any.insert_long(0);
   }

   private int memberCount() {
      int var1 = 0;

      try {
         var1 = this.any.type().member_count();
      } catch (BadKind var3) {
      }

      return var1;
   }

   private String memberName(int var1) {
      String var2 = null;

      try {
         var2 = this.any.type().member_name(var1);
      } catch (BadKind var4) {
      } catch (Bounds var5) {
      }

      return var2;
   }

   private int computeCurrentEnumeratorIndex(String var1) {
      int var2 = this.memberCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         if (this.memberName(var3).equals(var1)) {
            return var3;
         }
      }

      return -1;
   }

   public int component_count() {
      return 0;
   }

   public DynAny current_component() throws TypeMismatch {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         throw new TypeMismatch();
      }
   }

   public String get_as_string() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.memberName(this.currentEnumeratorIndex);
      }
   }

   public void set_as_string(String var1) throws InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         int var2 = this.computeCurrentEnumeratorIndex(var1);
         if (var2 == -1) {
            throw new InvalidValue();
         } else {
            this.currentEnumeratorIndex = var2;
            this.any.insert_long(var2);
         }
      }
   }

   public int get_as_ulong() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.currentEnumeratorIndex;
      }
   }

   public void set_as_ulong(int var1) throws InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (var1 >= 0 && var1 < this.memberCount()) {
         this.currentEnumeratorIndex = var1;
         this.any.insert_long(var1);
      } else {
         throw new InvalidValue();
      }
   }
}
