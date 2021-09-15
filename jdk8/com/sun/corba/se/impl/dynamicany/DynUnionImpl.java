package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynUnion;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynUnionImpl extends DynAnyConstructedImpl implements DynUnion {
   DynAny discriminator;
   DynAny currentMember;
   int currentMemberIndex;

   private DynUnionImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynUnionImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
      this.discriminator = null;
      this.currentMember = null;
      this.currentMemberIndex = -1;
   }

   protected DynUnionImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.discriminator = null;
      this.currentMember = null;
      this.currentMemberIndex = -1;
   }

   protected boolean initializeComponentsFromAny() {
      try {
         InputStream var1 = this.any.create_input_stream();
         Any var2 = DynAnyUtil.extractAnyFromStream(this.discriminatorType(), var1, this.orb);
         this.discriminator = DynAnyUtil.createMostDerivedDynAny(var2, this.orb, false);
         this.currentMemberIndex = this.currentUnionMemberIndex(var2);
         Any var3 = DynAnyUtil.extractAnyFromStream(this.memberType(this.currentMemberIndex), var1, this.orb);
         this.currentMember = DynAnyUtil.createMostDerivedDynAny(var3, this.orb, false);
         this.components = new DynAny[]{this.discriminator, this.currentMember};
      } catch (InconsistentTypeCode var4) {
      }

      return true;
   }

   protected boolean initializeComponentsFromTypeCode() {
      try {
         this.discriminator = DynAnyUtil.createMostDerivedDynAny(this.memberLabel(0), this.orb, false);
         this.index = 0;
         this.currentMemberIndex = 0;
         this.currentMember = DynAnyUtil.createMostDerivedDynAny(this.memberType(0), this.orb);
         this.components = new DynAny[]{this.discriminator, this.currentMember};
      } catch (InconsistentTypeCode var2) {
      }

      return true;
   }

   private TypeCode discriminatorType() {
      TypeCode var1 = null;

      try {
         var1 = this.any.type().discriminator_type();
      } catch (BadKind var3) {
      }

      return var1;
   }

   private int memberCount() {
      int var1 = 0;

      try {
         var1 = this.any.type().member_count();
      } catch (BadKind var3) {
      }

      return var1;
   }

   private Any memberLabel(int var1) {
      Any var2 = null;

      try {
         var2 = this.any.type().member_label(var1);
      } catch (BadKind var4) {
      } catch (Bounds var5) {
      }

      return var2;
   }

   private TypeCode memberType(int var1) {
      TypeCode var2 = null;

      try {
         var2 = this.any.type().member_type(var1);
      } catch (BadKind var4) {
      } catch (Bounds var5) {
      }

      return var2;
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

   private int defaultIndex() {
      int var1 = -1;

      try {
         var1 = this.any.type().default_index();
      } catch (BadKind var3) {
      }

      return var1;
   }

   private int currentUnionMemberIndex(Any var1) {
      int var2 = this.memberCount();

      for(int var4 = 0; var4 < var2; ++var4) {
         Any var3 = this.memberLabel(var4);
         if (var3.equal(var1)) {
            return var4;
         }
      }

      if (this.defaultIndex() != -1) {
         return this.defaultIndex();
      } else {
         return -1;
      }
   }

   protected void clearData() {
      super.clearData();
      this.discriminator = null;
      this.currentMember.destroy();
      this.currentMember = null;
      this.currentMemberIndex = -1;
   }

   public DynAny get_discriminator() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.checkInitComponents() ? this.discriminator : null;
      }
   }

   public void set_discriminator(DynAny var1) throws TypeMismatch {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (!var1.type().equal(this.discriminatorType())) {
         throw new TypeMismatch();
      } else {
         var1 = DynAnyUtil.convertToNative(var1, this.orb);
         Any var2 = this.getAny(var1);
         int var3 = this.currentUnionMemberIndex(var2);
         if (var3 == -1) {
            this.clearData();
            this.index = 0;
         } else {
            this.checkInitComponents();
            if (this.currentMemberIndex == -1 || var3 != this.currentMemberIndex) {
               this.clearData();
               this.index = 1;
               this.currentMemberIndex = var3;

               try {
                  this.currentMember = DynAnyUtil.createMostDerivedDynAny(this.memberType(this.currentMemberIndex), this.orb);
               } catch (InconsistentTypeCode var5) {
               }

               this.discriminator = var1;
               this.components = new DynAny[]{this.discriminator, this.currentMember};
               this.representations = 4;
            }
         }

      }
   }

   public void set_to_default_member() throws TypeMismatch {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         int var1 = this.defaultIndex();
         if (var1 == -1) {
            throw new TypeMismatch();
         } else {
            try {
               this.clearData();
               this.index = 1;
               this.currentMemberIndex = var1;
               this.currentMember = DynAnyUtil.createMostDerivedDynAny(this.memberType(var1), this.orb);
               this.components = new DynAny[]{this.discriminator, this.currentMember};
               Any var2 = this.orb.create_any();
               var2.insert_octet((byte)0);
               this.discriminator = DynAnyUtil.createMostDerivedDynAny(var2, this.orb, false);
               this.representations = 4;
            } catch (InconsistentTypeCode var3) {
            }

         }
      }
   }

   public void set_to_no_active_member() throws TypeMismatch {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.defaultIndex() != -1) {
         throw new TypeMismatch();
      } else {
         this.checkInitComponents();
         Any var1 = this.getAny(this.discriminator);
         var1.type(var1.type());
         this.index = 0;
         this.currentMemberIndex = -1;
         this.currentMember.destroy();
         this.currentMember = null;
         this.components[0] = this.discriminator;
         this.representations = 4;
      }
   }

   public boolean has_no_active_member() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.defaultIndex() != -1) {
         return false;
      } else {
         this.checkInitComponents();
         return this.checkInitComponents() ? this.currentMemberIndex == -1 : false;
      }
   }

   public TCKind discriminator_kind() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.discriminatorType().kind();
      }
   }

   public DynAny member() throws InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.checkInitComponents() && this.currentMemberIndex != -1) {
         return this.currentMember;
      } else {
         throw new InvalidValue();
      }
   }

   public String member_name() throws InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.checkInitComponents() && this.currentMemberIndex != -1) {
         String var1 = this.memberName(this.currentMemberIndex);
         return var1 == null ? "" : var1;
      } else {
         throw new InvalidValue();
      }
   }

   public TCKind member_kind() throws InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.checkInitComponents() && this.currentMemberIndex != -1) {
         return this.memberType(this.currentMemberIndex).kind();
      } else {
         throw new InvalidValue();
      }
   }
}
