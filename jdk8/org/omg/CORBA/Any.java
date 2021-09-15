package org.omg.CORBA;

import java.io.Serializable;
import java.math.BigDecimal;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public abstract class Any implements IDLEntity {
   public abstract boolean equal(Any var1);

   public abstract TypeCode type();

   public abstract void type(TypeCode var1);

   public abstract void read_value(InputStream var1, TypeCode var2) throws MARSHAL;

   public abstract void write_value(OutputStream var1);

   public abstract OutputStream create_output_stream();

   public abstract InputStream create_input_stream();

   public abstract short extract_short() throws BAD_OPERATION;

   public abstract void insert_short(short var1);

   public abstract int extract_long() throws BAD_OPERATION;

   public abstract void insert_long(int var1);

   public abstract long extract_longlong() throws BAD_OPERATION;

   public abstract void insert_longlong(long var1);

   public abstract short extract_ushort() throws BAD_OPERATION;

   public abstract void insert_ushort(short var1);

   public abstract int extract_ulong() throws BAD_OPERATION;

   public abstract void insert_ulong(int var1);

   public abstract long extract_ulonglong() throws BAD_OPERATION;

   public abstract void insert_ulonglong(long var1);

   public abstract float extract_float() throws BAD_OPERATION;

   public abstract void insert_float(float var1);

   public abstract double extract_double() throws BAD_OPERATION;

   public abstract void insert_double(double var1);

   public abstract boolean extract_boolean() throws BAD_OPERATION;

   public abstract void insert_boolean(boolean var1);

   public abstract char extract_char() throws BAD_OPERATION;

   public abstract void insert_char(char var1) throws DATA_CONVERSION;

   public abstract char extract_wchar() throws BAD_OPERATION;

   public abstract void insert_wchar(char var1);

   public abstract byte extract_octet() throws BAD_OPERATION;

   public abstract void insert_octet(byte var1);

   public abstract Any extract_any() throws BAD_OPERATION;

   public abstract void insert_any(Any var1);

   public abstract Object extract_Object() throws BAD_OPERATION;

   public abstract void insert_Object(Object var1);

   public abstract Serializable extract_Value() throws BAD_OPERATION;

   public abstract void insert_Value(Serializable var1);

   public abstract void insert_Value(Serializable var1, TypeCode var2) throws MARSHAL;

   public abstract void insert_Object(Object var1, TypeCode var2) throws BAD_PARAM;

   public abstract String extract_string() throws BAD_OPERATION;

   public abstract void insert_string(String var1) throws DATA_CONVERSION, MARSHAL;

   public abstract String extract_wstring() throws BAD_OPERATION;

   public abstract void insert_wstring(String var1) throws MARSHAL;

   public abstract TypeCode extract_TypeCode() throws BAD_OPERATION;

   public abstract void insert_TypeCode(TypeCode var1);

   /** @deprecated */
   @Deprecated
   public Principal extract_Principal() throws BAD_OPERATION {
      throw new NO_IMPLEMENT();
   }

   /** @deprecated */
   @Deprecated
   public void insert_Principal(Principal var1) {
      throw new NO_IMPLEMENT();
   }

   public Streamable extract_Streamable() throws BAD_INV_ORDER {
      throw new NO_IMPLEMENT();
   }

   public void insert_Streamable(Streamable var1) {
      throw new NO_IMPLEMENT();
   }

   public BigDecimal extract_fixed() {
      throw new NO_IMPLEMENT();
   }

   public void insert_fixed(BigDecimal var1) {
      throw new NO_IMPLEMENT();
   }

   public void insert_fixed(BigDecimal var1, TypeCode var2) throws BAD_INV_ORDER {
      throw new NO_IMPLEMENT();
   }
}
