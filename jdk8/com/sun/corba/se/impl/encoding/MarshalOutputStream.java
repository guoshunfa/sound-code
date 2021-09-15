package com.sun.corba.se.impl.encoding;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;

public interface MarshalOutputStream {
   InputStream create_input_stream();

   void write_boolean(boolean var1);

   void write_char(char var1);

   void write_wchar(char var1);

   void write_octet(byte var1);

   void write_short(short var1);

   void write_ushort(short var1);

   void write_long(int var1);

   void write_ulong(int var1);

   void write_longlong(long var1);

   void write_ulonglong(long var1);

   void write_float(float var1);

   void write_double(double var1);

   void write_string(String var1);

   void write_wstring(String var1);

   void write_boolean_array(boolean[] var1, int var2, int var3);

   void write_char_array(char[] var1, int var2, int var3);

   void write_wchar_array(char[] var1, int var2, int var3);

   void write_octet_array(byte[] var1, int var2, int var3);

   void write_short_array(short[] var1, int var2, int var3);

   void write_ushort_array(short[] var1, int var2, int var3);

   void write_long_array(int[] var1, int var2, int var3);

   void write_ulong_array(int[] var1, int var2, int var3);

   void write_longlong_array(long[] var1, int var2, int var3);

   void write_ulonglong_array(long[] var1, int var2, int var3);

   void write_float_array(float[] var1, int var2, int var3);

   void write_double_array(double[] var1, int var2, int var3);

   void write_Object(Object var1);

   void write_TypeCode(TypeCode var1);

   void write_any(Any var1);

   void write_Principal(Principal var1);

   void write_value(Serializable var1);

   void start_block();

   void end_block();

   void putEndian();

   void writeTo(OutputStream var1) throws IOException;

   byte[] toByteArray();
}
