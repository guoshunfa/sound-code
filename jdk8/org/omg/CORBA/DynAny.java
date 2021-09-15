package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.omg.CORBA.DynAnyPackage.InvalidValue;
import org.omg.CORBA.DynAnyPackage.TypeMismatch;

/** @deprecated */
@Deprecated
public interface DynAny extends Object {
   TypeCode type();

   void assign(DynAny var1) throws Invalid;

   void from_any(Any var1) throws Invalid;

   Any to_any() throws Invalid;

   void destroy();

   DynAny copy();

   void insert_boolean(boolean var1) throws InvalidValue;

   void insert_octet(byte var1) throws InvalidValue;

   void insert_char(char var1) throws InvalidValue;

   void insert_short(short var1) throws InvalidValue;

   void insert_ushort(short var1) throws InvalidValue;

   void insert_long(int var1) throws InvalidValue;

   void insert_ulong(int var1) throws InvalidValue;

   void insert_float(float var1) throws InvalidValue;

   void insert_double(double var1) throws InvalidValue;

   void insert_string(String var1) throws InvalidValue;

   void insert_reference(Object var1) throws InvalidValue;

   void insert_typecode(TypeCode var1) throws InvalidValue;

   void insert_longlong(long var1) throws InvalidValue;

   void insert_ulonglong(long var1) throws InvalidValue;

   void insert_wchar(char var1) throws InvalidValue;

   void insert_wstring(String var1) throws InvalidValue;

   void insert_any(Any var1) throws InvalidValue;

   void insert_val(Serializable var1) throws InvalidValue;

   Serializable get_val() throws TypeMismatch;

   boolean get_boolean() throws TypeMismatch;

   byte get_octet() throws TypeMismatch;

   char get_char() throws TypeMismatch;

   short get_short() throws TypeMismatch;

   short get_ushort() throws TypeMismatch;

   int get_long() throws TypeMismatch;

   int get_ulong() throws TypeMismatch;

   float get_float() throws TypeMismatch;

   double get_double() throws TypeMismatch;

   String get_string() throws TypeMismatch;

   Object get_reference() throws TypeMismatch;

   TypeCode get_typecode() throws TypeMismatch;

   long get_longlong() throws TypeMismatch;

   long get_ulonglong() throws TypeMismatch;

   char get_wchar() throws TypeMismatch;

   String get_wstring() throws TypeMismatch;

   Any get_any() throws TypeMismatch;

   DynAny current_component();

   boolean next();

   boolean seek(int var1);

   void rewind();
}
