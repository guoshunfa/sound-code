package org.omg.DynamicAny;

import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynAnyOperations {
   TypeCode type();

   void assign(DynAny var1) throws TypeMismatch;

   void from_any(Any var1) throws TypeMismatch, InvalidValue;

   Any to_any();

   boolean equal(DynAny var1);

   void destroy();

   DynAny copy();

   void insert_boolean(boolean var1) throws TypeMismatch, InvalidValue;

   void insert_octet(byte var1) throws TypeMismatch, InvalidValue;

   void insert_char(char var1) throws TypeMismatch, InvalidValue;

   void insert_short(short var1) throws TypeMismatch, InvalidValue;

   void insert_ushort(short var1) throws TypeMismatch, InvalidValue;

   void insert_long(int var1) throws TypeMismatch, InvalidValue;

   void insert_ulong(int var1) throws TypeMismatch, InvalidValue;

   void insert_float(float var1) throws TypeMismatch, InvalidValue;

   void insert_double(double var1) throws TypeMismatch, InvalidValue;

   void insert_string(String var1) throws TypeMismatch, InvalidValue;

   void insert_reference(Object var1) throws TypeMismatch, InvalidValue;

   void insert_typecode(TypeCode var1) throws TypeMismatch, InvalidValue;

   void insert_longlong(long var1) throws TypeMismatch, InvalidValue;

   void insert_ulonglong(long var1) throws TypeMismatch, InvalidValue;

   void insert_wchar(char var1) throws TypeMismatch, InvalidValue;

   void insert_wstring(String var1) throws TypeMismatch, InvalidValue;

   void insert_any(Any var1) throws TypeMismatch, InvalidValue;

   void insert_dyn_any(DynAny var1) throws TypeMismatch, InvalidValue;

   void insert_val(Serializable var1) throws TypeMismatch, InvalidValue;

   boolean get_boolean() throws TypeMismatch, InvalidValue;

   byte get_octet() throws TypeMismatch, InvalidValue;

   char get_char() throws TypeMismatch, InvalidValue;

   short get_short() throws TypeMismatch, InvalidValue;

   short get_ushort() throws TypeMismatch, InvalidValue;

   int get_long() throws TypeMismatch, InvalidValue;

   int get_ulong() throws TypeMismatch, InvalidValue;

   float get_float() throws TypeMismatch, InvalidValue;

   double get_double() throws TypeMismatch, InvalidValue;

   String get_string() throws TypeMismatch, InvalidValue;

   Object get_reference() throws TypeMismatch, InvalidValue;

   TypeCode get_typecode() throws TypeMismatch, InvalidValue;

   long get_longlong() throws TypeMismatch, InvalidValue;

   long get_ulonglong() throws TypeMismatch, InvalidValue;

   char get_wchar() throws TypeMismatch, InvalidValue;

   String get_wstring() throws TypeMismatch, InvalidValue;

   Any get_any() throws TypeMismatch, InvalidValue;

   DynAny get_dyn_any() throws TypeMismatch, InvalidValue;

   Serializable get_val() throws TypeMismatch, InvalidValue;

   boolean seek(int var1);

   void rewind();

   boolean next();

   int component_count();

   DynAny current_component() throws TypeMismatch;
}
