package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.ValueBase;

public interface DataInputStream extends ValueBase {
   Any read_any();

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

   Object read_Object();

   java.lang.Object read_Abstract();

   Serializable read_Value();

   TypeCode read_TypeCode();

   void read_any_array(AnySeqHolder var1, int var2, int var3);

   void read_boolean_array(BooleanSeqHolder var1, int var2, int var3);

   void read_char_array(CharSeqHolder var1, int var2, int var3);

   void read_wchar_array(WCharSeqHolder var1, int var2, int var3);

   void read_octet_array(OctetSeqHolder var1, int var2, int var3);

   void read_short_array(ShortSeqHolder var1, int var2, int var3);

   void read_ushort_array(UShortSeqHolder var1, int var2, int var3);

   void read_long_array(LongSeqHolder var1, int var2, int var3);

   void read_ulong_array(ULongSeqHolder var1, int var2, int var3);

   void read_ulonglong_array(ULongLongSeqHolder var1, int var2, int var3);

   void read_longlong_array(LongLongSeqHolder var1, int var2, int var3);

   void read_float_array(FloatSeqHolder var1, int var2, int var3);

   void read_double_array(DoubleSeqHolder var1, int var2, int var3);
}
