package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyComplexImpl extends DynAnyConstructedImpl {
   String[] names;
   NameValuePair[] nameValuePairs;
   NameDynAnyPair[] nameDynAnyPairs;

   private DynAnyComplexImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynAnyComplexImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
      this.names = null;
      this.nameValuePairs = null;
      this.nameDynAnyPairs = null;
   }

   protected DynAnyComplexImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.names = null;
      this.nameValuePairs = null;
      this.nameDynAnyPairs = null;
      this.index = 0;
   }

   public String current_member_name() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.checkInitComponents() && this.index >= 0 && this.index < this.names.length) {
         return this.names[this.index];
      } else {
         throw new InvalidValue();
      }
   }

   public TCKind current_member_kind() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.checkInitComponents() && this.index >= 0 && this.index < this.components.length) {
         return this.components[this.index].type().kind();
      } else {
         throw new InvalidValue();
      }
   }

   public void set_members(NameValuePair[] var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (var1 != null && var1.length != 0) {
         DynAny var3 = null;
         TypeCode var5 = this.any.type();
         int var6 = 0;

         try {
            var6 = var5.member_count();
         } catch (BadKind var16) {
         }

         if (var6 != var1.length) {
            this.clearData();
            throw new InvalidValue();
         } else {
            this.allocComponents(var1);

            for(int var7 = 0; var7 < var1.length; ++var7) {
               if (var1[var7] == null) {
                  this.clearData();
                  throw new InvalidValue();
               }

               String var4 = var1[var7].id;
               String var8 = null;

               try {
                  var8 = var5.member_name(var7);
               } catch (BadKind var14) {
               } catch (Bounds var15) {
               }

               if (!var8.equals(var4) && !var4.equals("")) {
                  this.clearData();
                  throw new TypeMismatch();
               }

               Any var2 = var1[var7].value;
               TypeCode var9 = null;

               try {
                  var9 = var5.member_type(var7);
               } catch (BadKind var12) {
               } catch (Bounds var13) {
               }

               if (!var9.equal(var2.type())) {
                  this.clearData();
                  throw new TypeMismatch();
               }

               try {
                  var3 = DynAnyUtil.createMostDerivedDynAny(var2, this.orb, false);
               } catch (InconsistentTypeCode var11) {
                  throw new InvalidValue();
               }

               this.addComponent(var7, var4, var2, var3);
            }

            this.index = var1.length == 0 ? -1 : 0;
            this.representations = 4;
         }
      } else {
         this.clearData();
      }
   }

   public void set_members_as_dyn_any(NameDynAnyPair[] var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (var1 != null && var1.length != 0) {
         TypeCode var5 = this.any.type();
         int var6 = 0;

         try {
            var6 = var5.member_count();
         } catch (BadKind var15) {
         }

         if (var6 != var1.length) {
            this.clearData();
            throw new InvalidValue();
         } else {
            this.allocComponents(var1);

            for(int var7 = 0; var7 < var1.length; ++var7) {
               if (var1[var7] == null) {
                  this.clearData();
                  throw new InvalidValue();
               }

               String var4 = var1[var7].id;
               String var8 = null;

               try {
                  var8 = var5.member_name(var7);
               } catch (BadKind var13) {
               } catch (Bounds var14) {
               }

               if (!var8.equals(var4) && !var4.equals("")) {
                  this.clearData();
                  throw new TypeMismatch();
               }

               DynAny var3 = var1[var7].value;
               Any var2 = this.getAny(var3);
               TypeCode var9 = null;

               try {
                  var9 = var5.member_type(var7);
               } catch (BadKind var11) {
               } catch (Bounds var12) {
               }

               if (!var9.equal(var2.type())) {
                  this.clearData();
                  throw new TypeMismatch();
               }

               this.addComponent(var7, var4, var2, var3);
            }

            this.index = var1.length == 0 ? -1 : 0;
            this.representations = 4;
         }
      } else {
         this.clearData();
      }
   }

   private void allocComponents(int var1) {
      this.components = new DynAny[var1];
      this.names = new String[var1];
      this.nameValuePairs = new NameValuePair[var1];
      this.nameDynAnyPairs = new NameDynAnyPair[var1];

      for(int var2 = 0; var2 < var1; ++var2) {
         this.nameValuePairs[var2] = new NameValuePair();
         this.nameDynAnyPairs[var2] = new NameDynAnyPair();
      }

   }

   private void allocComponents(NameValuePair[] var1) {
      this.components = new DynAny[var1.length];
      this.names = new String[var1.length];
      this.nameValuePairs = var1;
      this.nameDynAnyPairs = new NameDynAnyPair[var1.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.nameDynAnyPairs[var2] = new NameDynAnyPair();
      }

   }

   private void allocComponents(NameDynAnyPair[] var1) {
      this.components = new DynAny[var1.length];
      this.names = new String[var1.length];
      this.nameValuePairs = new NameValuePair[var1.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.nameValuePairs[var2] = new NameValuePair();
      }

      this.nameDynAnyPairs = var1;
   }

   private void addComponent(int var1, String var2, Any var3, DynAny var4) {
      this.components[var1] = var4;
      this.names[var1] = var2 != null ? var2 : "";
      this.nameValuePairs[var1].id = var2;
      this.nameValuePairs[var1].value = var3;
      this.nameDynAnyPairs[var1].id = var2;
      this.nameDynAnyPairs[var1].value = var4;
      if (var4 instanceof DynAnyImpl) {
         ((DynAnyImpl)var4).setStatus((byte)1);
      }

   }

   protected boolean initializeComponentsFromAny() {
      TypeCode var1 = this.any.type();
      TypeCode var2 = null;
      DynAny var4 = null;
      String var5 = null;
      int var6 = 0;

      try {
         var6 = var1.member_count();
      } catch (BadKind var13) {
      }

      InputStream var7 = this.any.create_input_stream();
      this.allocComponents(var6);

      for(int var8 = 0; var8 < var6; ++var8) {
         try {
            var5 = var1.member_name(var8);
            var2 = var1.member_type(var8);
         } catch (BadKind var11) {
         } catch (Bounds var12) {
         }

         Any var3 = DynAnyUtil.extractAnyFromStream(var2, var7, this.orb);

         try {
            var4 = DynAnyUtil.createMostDerivedDynAny(var3, this.orb, false);
         } catch (InconsistentTypeCode var10) {
         }

         this.addComponent(var8, var5, var3, var4);
      }

      return true;
   }

   protected boolean initializeComponentsFromTypeCode() {
      TypeCode var1 = this.any.type();
      TypeCode var2 = null;
      DynAny var4 = null;
      int var6 = 0;

      try {
         var6 = var1.member_count();
      } catch (BadKind var12) {
      }

      this.allocComponents(var6);

      for(int var7 = 0; var7 < var6; ++var7) {
         String var5 = null;

         try {
            var5 = var1.member_name(var7);
            var2 = var1.member_type(var7);
         } catch (BadKind var10) {
         } catch (Bounds var11) {
         }

         try {
            var4 = DynAnyUtil.createMostDerivedDynAny(var2, this.orb);
         } catch (InconsistentTypeCode var9) {
         }

         Any var3 = this.getAny(var4);
         this.addComponent(var7, var5, var3, var4);
      }

      return true;
   }

   protected void clearData() {
      super.clearData();
      this.names = null;
      this.nameValuePairs = null;
      this.nameDynAnyPairs = null;
   }
}
