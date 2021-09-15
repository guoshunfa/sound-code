package org.omg.DynamicAny;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class _DynValueStub extends ObjectImpl implements DynValue {
   public static final Class _opsClass = DynValueOperations.class;
   private static String[] __ids = new String[]{"IDL:omg.org/DynamicAny/DynValue:1.0", "IDL:omg.org/DynamicAny/DynValueCommon:1.0", "IDL:omg.org/DynamicAny/DynAny:1.0"};

   public String current_member_name() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("current_member_name", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      String var3;
      try {
         var3 = var2.current_member_name();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public TCKind current_member_kind() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("current_member_kind", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      TCKind var3;
      try {
         var3 = var2.current_member_kind();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public NameValuePair[] get_members() throws InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_members", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      NameValuePair[] var3;
      try {
         var3 = var2.get_members();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public void set_members(NameValuePair[] var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("set_members", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.set_members(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public NameDynAnyPair[] get_members_as_dyn_any() throws InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_members_as_dyn_any", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      NameDynAnyPair[] var3;
      try {
         var3 = var2.get_members_as_dyn_any();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public void set_members_as_dyn_any(NameDynAnyPair[] var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("set_members_as_dyn_any", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.set_members_as_dyn_any(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public boolean is_null() {
      ServantObject var1 = this._servant_preinvoke("is_null", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      boolean var3;
      try {
         var3 = var2.is_null();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public void set_to_null() {
      ServantObject var1 = this._servant_preinvoke("set_to_null", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      try {
         var2.set_to_null();
      } finally {
         this._servant_postinvoke(var1);
      }

   }

   public void set_to_value() {
      ServantObject var1 = this._servant_preinvoke("set_to_value", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      try {
         var2.set_to_value();
      } finally {
         this._servant_postinvoke(var1);
      }

   }

   public TypeCode type() {
      ServantObject var1 = this._servant_preinvoke("type", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      TypeCode var3;
      try {
         var3 = var2.type();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public void assign(DynAny var1) throws TypeMismatch {
      ServantObject var2 = this._servant_preinvoke("assign", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.assign(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void from_any(Any var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("from_any", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.from_any(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public Any to_any() {
      ServantObject var1 = this._servant_preinvoke("to_any", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      Any var3;
      try {
         var3 = var2.to_any();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public boolean equal(DynAny var1) {
      ServantObject var2 = this._servant_preinvoke("equal", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      boolean var4;
      try {
         var4 = var3.equal(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

      return var4;
   }

   public void destroy() {
      ServantObject var1 = this._servant_preinvoke("destroy", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      try {
         var2.destroy();
      } finally {
         this._servant_postinvoke(var1);
      }

   }

   public DynAny copy() {
      ServantObject var1 = this._servant_preinvoke("copy", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      DynAny var3;
      try {
         var3 = var2.copy();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public void insert_boolean(boolean var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_boolean", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_boolean(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_octet(byte var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_octet", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_octet(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_char(char var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_char", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_char(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_short(short var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_short", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_short(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_ushort(short var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_ushort", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_ushort(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_long(int var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_long", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_long(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_ulong(int var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_ulong", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_ulong(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_float(float var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_float", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_float(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_double(double var1) throws TypeMismatch, InvalidValue {
      ServantObject var3 = this._servant_preinvoke("insert_double", _opsClass);
      DynValueOperations var4 = (DynValueOperations)var3.servant;

      try {
         var4.insert_double(var1);
      } finally {
         this._servant_postinvoke(var3);
      }

   }

   public void insert_string(String var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_string", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_string(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_reference(Object var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_reference", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_reference(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_typecode(TypeCode var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_typecode", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_typecode(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_longlong(long var1) throws TypeMismatch, InvalidValue {
      ServantObject var3 = this._servant_preinvoke("insert_longlong", _opsClass);
      DynValueOperations var4 = (DynValueOperations)var3.servant;

      try {
         var4.insert_longlong(var1);
      } finally {
         this._servant_postinvoke(var3);
      }

   }

   public void insert_ulonglong(long var1) throws TypeMismatch, InvalidValue {
      ServantObject var3 = this._servant_preinvoke("insert_ulonglong", _opsClass);
      DynValueOperations var4 = (DynValueOperations)var3.servant;

      try {
         var4.insert_ulonglong(var1);
      } finally {
         this._servant_postinvoke(var3);
      }

   }

   public void insert_wchar(char var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_wchar", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_wchar(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_wstring(String var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_wstring", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_wstring(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_any(Any var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_any", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_any(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_dyn_any(DynAny var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_dyn_any", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_dyn_any(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public void insert_val(Serializable var1) throws TypeMismatch, InvalidValue {
      ServantObject var2 = this._servant_preinvoke("insert_val", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      try {
         var3.insert_val(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

   }

   public boolean get_boolean() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_boolean", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      boolean var3;
      try {
         var3 = var2.get_boolean();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public byte get_octet() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_octet", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      byte var3;
      try {
         var3 = var2.get_octet();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public char get_char() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_char", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      char var3;
      try {
         var3 = var2.get_char();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public short get_short() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_short", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      short var3;
      try {
         var3 = var2.get_short();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public short get_ushort() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_ushort", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      short var3;
      try {
         var3 = var2.get_ushort();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public int get_long() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_long", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      int var3;
      try {
         var3 = var2.get_long();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public int get_ulong() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_ulong", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      int var3;
      try {
         var3 = var2.get_ulong();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public float get_float() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_float", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      float var3;
      try {
         var3 = var2.get_float();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public double get_double() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_double", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      double var3;
      try {
         var3 = var2.get_double();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public String get_string() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_string", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      String var3;
      try {
         var3 = var2.get_string();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public Object get_reference() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_reference", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      Object var3;
      try {
         var3 = var2.get_reference();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public TypeCode get_typecode() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_typecode", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      TypeCode var3;
      try {
         var3 = var2.get_typecode();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public long get_longlong() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_longlong", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      long var3;
      try {
         var3 = var2.get_longlong();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public long get_ulonglong() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_ulonglong", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      long var3;
      try {
         var3 = var2.get_ulonglong();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public char get_wchar() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_wchar", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      char var3;
      try {
         var3 = var2.get_wchar();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public String get_wstring() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_wstring", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      String var3;
      try {
         var3 = var2.get_wstring();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public Any get_any() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_any", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      Any var3;
      try {
         var3 = var2.get_any();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public DynAny get_dyn_any() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_dyn_any", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      DynAny var3;
      try {
         var3 = var2.get_dyn_any();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public Serializable get_val() throws TypeMismatch, InvalidValue {
      ServantObject var1 = this._servant_preinvoke("get_val", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      Serializable var3;
      try {
         var3 = var2.get_val();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public boolean seek(int var1) {
      ServantObject var2 = this._servant_preinvoke("seek", _opsClass);
      DynValueOperations var3 = (DynValueOperations)var2.servant;

      boolean var4;
      try {
         var4 = var3.seek(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

      return var4;
   }

   public void rewind() {
      ServantObject var1 = this._servant_preinvoke("rewind", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      try {
         var2.rewind();
      } finally {
         this._servant_postinvoke(var1);
      }

   }

   public boolean next() {
      ServantObject var1 = this._servant_preinvoke("next", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      boolean var3;
      try {
         var3 = var2.next();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public int component_count() {
      ServantObject var1 = this._servant_preinvoke("component_count", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      int var3;
      try {
         var3 = var2.component_count();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public DynAny current_component() throws TypeMismatch {
      ServantObject var1 = this._servant_preinvoke("current_component", _opsClass);
      DynValueOperations var2 = (DynValueOperations)var1.servant;

      DynAny var3;
      try {
         var3 = var2.current_component();
      } finally {
         this._servant_postinvoke(var1);
      }

      return var3;
   }

   public String[] _ids() {
      return (String[])((String[])__ids.clone());
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      String var2 = var1.readUTF();
      java.lang.Object var3 = null;
      java.lang.Object var4 = null;
      ORB var5 = ORB.init((String[])var3, (Properties)var4);

      try {
         Object var6 = var5.string_to_object(var2);
         Delegate var7 = ((ObjectImpl)var6)._get_delegate();
         this._set_delegate(var7);
      } finally {
         var5.destroy();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      java.lang.Object var2 = null;
      java.lang.Object var3 = null;
      ORB var4 = ORB.init((String[])var2, (Properties)var3);

      try {
         String var5 = var4.object_to_string(this);
         var1.writeUTF(var5);
      } finally {
         var4.destroy();
      }

   }
}
