package org.omg.CORBA.portable;

import java.io.IOException;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;

public abstract class InputStream extends java.io.InputStream {
   public abstract boolean read_boolean();

   public abstract char read_char();

   public abstract char read_wchar();

   public abstract byte read_octet();

   public abstract short read_short();

   public abstract short read_ushort();

   public abstract int read_long();

   public abstract int read_ulong();

   public abstract long read_longlong();

   public abstract long read_ulonglong();

   public abstract float read_float();

   public abstract double read_double();

   public abstract String read_string();

   public abstract String read_wstring();

   public abstract void read_boolean_array(boolean[] var1, int var2, int var3);

   public abstract void read_char_array(char[] var1, int var2, int var3);

   public abstract void read_wchar_array(char[] var1, int var2, int var3);

   public abstract void read_octet_array(byte[] var1, int var2, int var3);

   public abstract void read_short_array(short[] var1, int var2, int var3);

   public abstract void read_ushort_array(short[] var1, int var2, int var3);

   public abstract void read_long_array(int[] var1, int var2, int var3);

   public abstract void read_ulong_array(int[] var1, int var2, int var3);

   public abstract void read_longlong_array(long[] var1, int var2, int var3);

   public abstract void read_ulonglong_array(long[] var1, int var2, int var3);

   public abstract void read_float_array(float[] var1, int var2, int var3);

   public abstract void read_double_array(double[] var1, int var2, int var3);

   public abstract Object read_Object();

   public abstract TypeCode read_TypeCode();

   public abstract Any read_any();

   /** @deprecated */
   @Deprecated
   public Principal read_Principal() {
      throw new NO_IMPLEMENT();
   }

   public int read() throws IOException {
      throw new NO_IMPLEMENT();
   }

   public BigDecimal read_fixed() {
      throw new NO_IMPLEMENT();
   }

   public Context read_Context() {
      throw new NO_IMPLEMENT();
   }

   public Object read_Object(Class var1) {
      throw new NO_IMPLEMENT();
   }

   public ORB orb() {
      throw new NO_IMPLEMENT();
   }
}
