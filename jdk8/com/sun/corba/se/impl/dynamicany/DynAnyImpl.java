package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyImpl extends LocalObject implements DynAny {
   protected static final int NO_INDEX = -1;
   protected static final byte STATUS_DESTROYABLE = 0;
   protected static final byte STATUS_UNDESTROYABLE = 1;
   protected static final byte STATUS_DESTROYED = 2;
   protected ORB orb = null;
   protected ORBUtilSystemException wrapper;
   protected Any any = null;
   protected byte status = 0;
   protected int index = -1;
   private String[] __ids = new String[]{"IDL:omg.org/DynamicAny/DynAny:1.0"};

   protected DynAnyImpl() {
      this.wrapper = ORBUtilSystemException.get("rpc.presentation");
   }

   protected DynAnyImpl(ORB var1, Any var2, boolean var3) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.presentation");
      if (var3) {
         this.any = DynAnyUtil.copy(var2, var1);
      } else {
         this.any = var2;
      }

      this.index = -1;
   }

   protected DynAnyImpl(ORB var1, TypeCode var2) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.presentation");
      this.any = DynAnyUtil.createDefaultAnyOfType(var2, var1);
   }

   protected DynAnyFactory factory() {
      try {
         return (DynAnyFactory)this.orb.resolve_initial_references("DynAnyFactory");
      } catch (InvalidName var2) {
         throw new RuntimeException("Unable to find DynAnyFactory");
      }
   }

   protected Any getAny() {
      return this.any;
   }

   protected Any getAny(DynAny var1) {
      return var1 instanceof DynAnyImpl ? ((DynAnyImpl)var1).getAny() : var1.to_any();
   }

   protected void writeAny(OutputStream var1) {
      this.any.write_value(var1);
   }

   protected void setStatus(byte var1) {
      this.status = var1;
   }

   protected void clearData() {
      this.any.type(this.any.type());
   }

   public TypeCode type() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.any.type();
      }
   }

   public void assign(DynAny var1) throws TypeMismatch {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any != null && !this.any.type().equal(var1.type())) {
         throw new TypeMismatch();
      } else {
         this.any = var1.to_any();
      }
   }

   public void from_any(Any var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any != null && !this.any.type().equal(var1.type())) {
         throw new TypeMismatch();
      } else {
         Any var2 = null;

         try {
            var2 = DynAnyUtil.copy(var1, this.orb);
         } catch (Exception var4) {
            throw new InvalidValue();
         }

         if (!DynAnyUtil.isInitialized(var2)) {
            throw new InvalidValue();
         } else {
            this.any = var2;
         }
      }
   }

   public abstract Any to_any();

   public abstract boolean equal(DynAny var1);

   public abstract void destroy();

   public abstract DynAny copy();

   public String[] _ids() {
      return (String[])((String[])this.__ids.clone());
   }
}
