package com.sun.corba.se.impl.encoding;

import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;

public interface MarshalInputStream {
   boolean read_boolean();

   char read_char();

   char read_wchar();

   byte read_octet();

   short read_short();

   short read_ushort();

   int read_long();

   int read_ulong();

   long read_longlong();

   long read_ulonglong();

   float read_float();

   double read_double();

   String read_string();

   String read_wstring();

   void read_boolean_array(boolean[] var1, int var2, int var3);

   void read_char_array(char[] var1, int var2, int var3);

   void read_wchar_array(char[] var1, int var2, int var3);

   void read_octet_array(byte[] var1, int var2, int var3);

   void read_short_array(short[] var1, int var2, int var3);

   void read_ushort_array(short[] var1, int var2, int var3);

   void read_long_array(int[] var1, int var2, int var3);

   void read_ulong_array(int[] var1, int var2, int var3);

   void read_longlong_array(long[] var1, int var2, int var3);

   void read_ulonglong_array(long[] var1, int var2, int var3);

   void read_float_array(float[] var1, int var2, int var3);

   void read_double_array(double[] var1, int var2, int var3);

   Object read_Object();

   TypeCode read_TypeCode();

   Any read_any();

   Principal read_Principal();

   Object read_Object(Class var1);

   Serializable read_value() throws Exception;

   void consumeEndian();

   int getPosition();

   void mark(int var1);

   void reset();

   void performORBVersionSpecificInit();

   void resetCodeSetConverters();
}
