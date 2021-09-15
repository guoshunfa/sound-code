package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynSequence;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

public class DynSequenceImpl extends DynAnyCollectionImpl implements DynSequence {
   private DynSequenceImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynSequenceImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected DynSequenceImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
   }

   protected boolean initializeComponentsFromAny() {
      TypeCode var1 = this.any.type();
      TypeCode var3 = this.getContentType();

      InputStream var4;
      try {
         var4 = this.any.create_input_stream();
      } catch (BAD_OPERATION var8) {
         return false;
      }

      int var2 = var4.read_long();
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
      this.components = new DynAny[0];
      this.anys = new Any[0];
      return true;
   }

   protected boolean initializeAnyFromComponents() {
      OutputStream var1 = this.any.create_output_stream();
      var1.write_long(this.components.length);

      for(int var2 = 0; var2 < this.components.length; ++var2) {
         if (this.components[var2] instanceof DynAnyImpl) {
            ((DynAnyImpl)this.components[var2]).writeAny(var1);
         } else {
            this.components[var2].to_any().write_value(var1);
         }
      }

      this.any.read_value(var1.create_input_stream(), this.any.type());
      return true;
   }

   public int get_length() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.checkInitComponents() ? this.components.length : 0;
      }
   }

   public void set_length(int var1) throws InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         int var2 = this.getBound();
         if (var2 > 0 && var1 > var2) {
            throw new InvalidValue();
         } else {
            this.checkInitComponents();
            int var3 = this.components.length;
            DynAny[] var4;
            Any[] var5;
            if (var1 > var3) {
               var4 = new DynAny[var1];
               var5 = new Any[var1];
               System.arraycopy(this.components, 0, var4, 0, var3);
               System.arraycopy(this.anys, 0, var5, 0, var3);
               this.components = var4;
               this.anys = var5;
               TypeCode var6 = this.getContentType();

               for(int var7 = var3; var7 < var1; ++var7) {
                  this.createDefaultComponentAt(var7, var6);
               }

               if (this.index == -1) {
                  this.index = var3;
               }
            } else if (var1 < var3) {
               var4 = new DynAny[var1];
               var5 = new Any[var1];
               System.arraycopy(this.components, 0, var4, 0, var1);
               System.arraycopy(this.anys, 0, var5, 0, var1);
               this.components = var4;
               this.anys = var5;
               if (var1 == 0 || this.index >= var1) {
                  this.index = -1;
               }
            } else if (this.index == -1 && var1 > 0) {
               this.index = 0;
            }

         }
      }
   }

   protected void checkValue(Object[] var1) throws InvalidValue {
      if (var1 != null && var1.length != 0) {
         this.index = 0;
         int var2 = this.getBound();
         if (var2 > 0 && var1.length > var2) {
            throw new InvalidValue();
         }
      } else {
         this.clearData();
         this.index = -1;
      }
   }
}
