package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyConstructedImpl extends DynAnyImpl {
   protected static final byte REPRESENTATION_NONE = 0;
   protected static final byte REPRESENTATION_TYPECODE = 1;
   protected static final byte REPRESENTATION_ANY = 2;
   protected static final byte REPRESENTATION_COMPONENTS = 4;
   protected static final byte RECURSIVE_UNDEF = -1;
   protected static final byte RECURSIVE_NO = 0;
   protected static final byte RECURSIVE_YES = 1;
   protected static final DynAny[] emptyComponents = new DynAny[0];
   DynAny[] components;
   byte representations;
   byte isRecursive;

   private DynAnyConstructedImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynAnyConstructedImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
      this.components = emptyComponents;
      this.representations = 0;
      this.isRecursive = -1;
      if (this.any != null) {
         this.representations = 2;
      }

      this.index = 0;
   }

   protected DynAnyConstructedImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.components = emptyComponents;
      this.representations = 0;
      this.isRecursive = -1;
      if (var2 != null) {
         this.representations = 1;
      }

      this.index = -1;
   }

   protected boolean isRecursive() {
      if (this.isRecursive == -1) {
         TypeCode var1 = this.any.type();
         if (var1 instanceof TypeCodeImpl) {
            if (((TypeCodeImpl)var1).is_recursive()) {
               this.isRecursive = 1;
            } else {
               this.isRecursive = 0;
            }
         } else {
            this.isRecursive = 0;
         }
      }

      return this.isRecursive == 1;
   }

   public DynAny current_component() throws TypeMismatch {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         return null;
      } else {
         return this.checkInitComponents() ? this.components[this.index] : null;
      }
   }

   public int component_count() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.checkInitComponents() ? this.components.length : 0;
      }
   }

   public boolean next() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (!this.checkInitComponents()) {
         return false;
      } else {
         ++this.index;
         if (this.index >= 0 && this.index < this.components.length) {
            return true;
         } else {
            this.index = -1;
            return false;
         }
      }
   }

   public boolean seek(int var1) {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (var1 < 0) {
         this.index = -1;
         return false;
      } else if (!this.checkInitComponents()) {
         return false;
      } else if (var1 < this.components.length) {
         this.index = var1;
         return true;
      } else {
         return false;
      }
   }

   public void rewind() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.seek(0);
      }
   }

   protected void clearData() {
      super.clearData();
      this.components = emptyComponents;
      this.index = -1;
      this.representations = 0;
   }

   protected void writeAny(OutputStream var1) {
      this.checkInitAny();
      super.writeAny(var1);
   }

   protected boolean checkInitComponents() {
      if ((this.representations & 4) == 0) {
         if ((this.representations & 2) != 0) {
            if (!this.initializeComponentsFromAny()) {
               return false;
            }

            this.representations = (byte)(this.representations | 4);
         } else if ((this.representations & 1) != 0) {
            if (!this.initializeComponentsFromTypeCode()) {
               return false;
            }

            this.representations = (byte)(this.representations | 4);
         }
      }

      return true;
   }

   protected void checkInitAny() {
      if ((this.representations & 2) == 0) {
         if ((this.representations & 4) != 0) {
            if (this.initializeAnyFromComponents()) {
               this.representations = (byte)(this.representations | 2);
            }
         } else if ((this.representations & 1) != 0) {
            if (this.representations == 1 && this.isRecursive()) {
               return;
            }

            if (this.initializeComponentsFromTypeCode()) {
               this.representations = (byte)(this.representations | 4);
            }

            if (this.initializeAnyFromComponents()) {
               this.representations = (byte)(this.representations | 2);
            }
         }
      }

   }

   protected abstract boolean initializeComponentsFromAny();

   protected abstract boolean initializeComponentsFromTypeCode();

   protected boolean initializeAnyFromComponents() {
      OutputStream var1 = this.any.create_output_stream();

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

   public void assign(DynAny var1) throws TypeMismatch {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.clearData();
         super.assign(var1);
         this.representations = 2;
         this.index = 0;
      }
   }

   public void from_any(Any var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.clearData();
         super.from_any(var1);
         this.representations = 2;
         this.index = 0;
      }
   }

   public Any to_any() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.checkInitAny();
         return DynAnyUtil.copy(this.any, this.orb);
      }
   }

   public boolean equal(DynAny var1) {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (var1 == this) {
         return true;
      } else if (!this.any.type().equal(var1.type())) {
         return false;
      } else if (!this.checkInitComponents()) {
         return false;
      } else {
         DynAny var2 = null;

         try {
            var2 = var1.current_component();

            for(int var3 = 0; var3 < this.components.length; ++var3) {
               boolean var4;
               if (!var1.seek(var3)) {
                  var4 = false;
                  return var4;
               }

               if (!this.components[var3].equal(var1.current_component())) {
                  var4 = false;
                  return var4;
               }
            }
         } catch (TypeMismatch var8) {
         } finally {
            DynAnyUtil.set_current_component(var1, var2);
         }

         return true;
      }
   }

   public void destroy() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         if (this.status == 0) {
            this.status = 2;

            for(int var1 = 0; var1 < this.components.length; ++var1) {
               if (this.components[var1] instanceof DynAnyImpl) {
                  ((DynAnyImpl)this.components[var1]).setStatus((byte)0);
               }

               this.components[var1].destroy();
            }
         }

      }
   }

   public DynAny copy() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         this.checkInitAny();

         try {
            return DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, true);
         } catch (InconsistentTypeCode var2) {
            return null;
         }
      }
   }

   public void insert_boolean(boolean var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_boolean(var1);
         }
      }
   }

   public void insert_octet(byte var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_octet(var1);
         }
      }
   }

   public void insert_char(char var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_char(var1);
         }
      }
   }

   public void insert_short(short var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_short(var1);
         }
      }
   }

   public void insert_ushort(short var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_ushort(var1);
         }
      }
   }

   public void insert_long(int var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_long(var1);
         }
      }
   }

   public void insert_ulong(int var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_ulong(var1);
         }
      }
   }

   public void insert_float(float var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_float(var1);
         }
      }
   }

   public void insert_double(double var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var3 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var3)) {
            throw new TypeMismatch();
         } else {
            var3.insert_double(var1);
         }
      }
   }

   public void insert_string(String var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_string(var1);
         }
      }
   }

   public void insert_reference(Object var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_reference(var1);
         }
      }
   }

   public void insert_typecode(TypeCode var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_typecode(var1);
         }
      }
   }

   public void insert_longlong(long var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var3 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var3)) {
            throw new TypeMismatch();
         } else {
            var3.insert_longlong(var1);
         }
      }
   }

   public void insert_ulonglong(long var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var3 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var3)) {
            throw new TypeMismatch();
         } else {
            var3.insert_ulonglong(var1);
         }
      }
   }

   public void insert_wchar(char var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_wchar(var1);
         }
      }
   }

   public void insert_wstring(String var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_wstring(var1);
         }
      }
   }

   public void insert_any(Any var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_any(var1);
         }
      }
   }

   public void insert_dyn_any(DynAny var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_dyn_any(var1);
         }
      }
   }

   public void insert_val(Serializable var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var2 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var2)) {
            throw new TypeMismatch();
         } else {
            var2.insert_val(var1);
         }
      }
   }

   public Serializable get_val() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_val();
         }
      }
   }

   public boolean get_boolean() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_boolean();
         }
      }
   }

   public byte get_octet() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_octet();
         }
      }
   }

   public char get_char() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_char();
         }
      }
   }

   public short get_short() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_short();
         }
      }
   }

   public short get_ushort() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_ushort();
         }
      }
   }

   public int get_long() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_long();
         }
      }
   }

   public int get_ulong() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_ulong();
         }
      }
   }

   public float get_float() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_float();
         }
      }
   }

   public double get_double() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_double();
         }
      }
   }

   public String get_string() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_string();
         }
      }
   }

   public Object get_reference() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_reference();
         }
      }
   }

   public TypeCode get_typecode() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_typecode();
         }
      }
   }

   public long get_longlong() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_longlong();
         }
      }
   }

   public long get_ulonglong() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_ulonglong();
         }
      }
   }

   public char get_wchar() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_wchar();
         }
      }
   }

   public String get_wstring() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_wstring();
         }
      }
   }

   public Any get_any() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_any();
         }
      }
   }

   public DynAny get_dyn_any() throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else if (this.index == -1) {
         throw new InvalidValue();
      } else {
         DynAny var1 = this.current_component();
         if (DynAnyUtil.isConstructedDynAny(var1)) {
            throw new TypeMismatch();
         } else {
            return var1.get_dyn_any();
         }
      }
   }
}
