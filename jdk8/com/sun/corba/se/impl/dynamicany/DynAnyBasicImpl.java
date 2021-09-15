package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynAnyBasicImpl extends DynAnyImpl {
   private DynAnyBasicImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynAnyBasicImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
      this.index = -1;
   }

   protected DynAnyBasicImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.index = -1;
   }

   public void assign(DynAny var1) throws TypeMismatch {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         super.assign(var1);
         this.index = -1;
      }
   }

   public void from_any(Any var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         super.from_any(var1);
         this.index = -1;
      }
   }

   public Any to_any() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return DynAnyUtil.copy(this.any, this.orb);
      }
   }

   public boolean equal(DynAny var1) {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (var1 == this) {
         return true;
      } else {
         return !this.any.type().equal(var1.type()) ? false : this.any.equal(this.getAny(var1));
      }
   }

   public void destroy() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         if (this.status == 0) {
            this.status = 2;
         }

      }
   }

   public DynAny copy() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         try {
            return DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, true);
         } catch (InconsistentTypeCode var2) {
            return null;
         }
      }
   }

   public DynAny current_component() throws TypeMismatch {
      return null;
   }

   public int component_count() {
      return 0;
   }

   public boolean next() {
      return false;
   }

   public boolean seek(int var1) {
      return false;
   }

   public void rewind() {
   }

   public void insert_boolean(boolean var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 8) {
         throw new TypeMismatch();
      } else {
         this.any.insert_boolean(var1);
      }
   }

   public void insert_octet(byte var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 10) {
         throw new TypeMismatch();
      } else {
         this.any.insert_octet(var1);
      }
   }

   public void insert_char(char var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 9) {
         throw new TypeMismatch();
      } else {
         this.any.insert_char(var1);
      }
   }

   public void insert_short(short var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 2) {
         throw new TypeMismatch();
      } else {
         this.any.insert_short(var1);
      }
   }

   public void insert_ushort(short var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 4) {
         throw new TypeMismatch();
      } else {
         this.any.insert_ushort(var1);
      }
   }

   public void insert_long(int var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 3) {
         throw new TypeMismatch();
      } else {
         this.any.insert_long(var1);
      }
   }

   public void insert_ulong(int var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 5) {
         throw new TypeMismatch();
      } else {
         this.any.insert_ulong(var1);
      }
   }

   public void insert_float(float var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 6) {
         throw new TypeMismatch();
      } else {
         this.any.insert_float(var1);
      }
   }

   public void insert_double(double var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 7) {
         throw new TypeMismatch();
      } else {
         this.any.insert_double(var1);
      }
   }

   public void insert_string(String var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 18) {
         throw new TypeMismatch();
      } else if (var1 == null) {
         throw new InvalidValue();
      } else {
         try {
            if (this.any.type().length() > 0 && this.any.type().length() < var1.length()) {
               throw new InvalidValue();
            }
         } catch (BadKind var3) {
         }

         this.any.insert_string(var1);
      }
   }

   public void insert_reference(Object var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 14) {
         throw new TypeMismatch();
      } else {
         this.any.insert_Object(var1);
      }
   }

   public void insert_typecode(TypeCode var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 12) {
         throw new TypeMismatch();
      } else {
         this.any.insert_TypeCode(var1);
      }
   }

   public void insert_longlong(long var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 23) {
         throw new TypeMismatch();
      } else {
         this.any.insert_longlong(var1);
      }
   }

   public void insert_ulonglong(long var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 24) {
         throw new TypeMismatch();
      } else {
         this.any.insert_ulonglong(var1);
      }
   }

   public void insert_wchar(char var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 26) {
         throw new TypeMismatch();
      } else {
         this.any.insert_wchar(var1);
      }
   }

   public void insert_wstring(String var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 27) {
         throw new TypeMismatch();
      } else if (var1 == null) {
         throw new InvalidValue();
      } else {
         try {
            if (this.any.type().length() > 0 && this.any.type().length() < var1.length()) {
               throw new InvalidValue();
            }
         } catch (BadKind var3) {
         }

         this.any.insert_wstring(var1);
      }
   }

   public void insert_any(Any var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 11) {
         throw new TypeMismatch();
      } else {
         this.any.insert_any(var1);
      }
   }

   public void insert_dyn_any(DynAny var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 11) {
         throw new TypeMismatch();
      } else {
         this.any.insert_any(var1.to_any());
      }
   }

   public void insert_val(Serializable var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         int var2 = this.any.type().kind().value();
         if (var2 != 29 && var2 != 30) {
            throw new TypeMismatch();
         } else {
            this.any.insert_Value(var1);
         }
      }
   }

   public Serializable get_val() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         int var1 = this.any.type().kind().value();
         if (var1 != 29 && var1 != 30) {
            throw new TypeMismatch();
         } else {
            return this.any.extract_Value();
         }
      }
   }

   public boolean get_boolean() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 8) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_boolean();
      }
   }

   public byte get_octet() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 10) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_octet();
      }
   }

   public char get_char() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 9) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_char();
      }
   }

   public short get_short() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 2) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_short();
      }
   }

   public short get_ushort() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 4) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_ushort();
      }
   }

   public int get_long() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 3) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_long();
      }
   }

   public int get_ulong() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 5) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_ulong();
      }
   }

   public float get_float() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 6) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_float();
      }
   }

   public double get_double() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 7) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_double();
      }
   }

   public String get_string() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 18) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_string();
      }
   }

   public Object get_reference() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 14) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_Object();
      }
   }

   public TypeCode get_typecode() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 12) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_TypeCode();
      }
   }

   public long get_longlong() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 23) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_longlong();
      }
   }

   public long get_ulonglong() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 24) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_ulonglong();
      }
   }

   public char get_wchar() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 26) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_wchar();
      }
   }

   public String get_wstring() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 27) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_wstring();
      }
   }

   public Any get_any() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 11) {
         throw new TypeMismatch();
      } else {
         return this.any.extract_any();
      }
   }

   public DynAny get_dyn_any() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.any.type().kind().value() != 11) {
         throw new TypeMismatch();
      } else {
         try {
            return DynAnyUtil.createMostDerivedDynAny(this.any.extract_any(), this.orb, true);
         } catch (InconsistentTypeCode var2) {
            return null;
         }
      }
   }
}
