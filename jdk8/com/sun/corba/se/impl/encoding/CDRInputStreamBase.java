package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.Context;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.omg.CORBA.portable.BoxedValueHelper;

abstract class CDRInputStreamBase extends InputStream {
   protected CDRInputStream parent;

   public void setParent(CDRInputStream var1) {
      this.parent = var1;
   }

   public abstract void init(ORB var1, ByteBuffer var2, int var3, boolean var4, BufferManagerRead var5);

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

   public abstract Principal read_Principal();

   public int read() throws IOException {
      throw new NO_IMPLEMENT();
   }

   public abstract BigDecimal read_fixed();

   public Context read_Context() {
      throw new NO_IMPLEMENT();
   }

   public abstract Object read_Object(Class var1);

   public abstract ORB orb();

   public abstract Serializable read_value();

   public abstract Serializable read_value(Class var1);

   public abstract Serializable read_value(BoxedValueHelper var1);

   public abstract Serializable read_value(String var1);

   public abstract Serializable read_value(Serializable var1);

   public abstract java.lang.Object read_abstract_interface();

   public abstract java.lang.Object read_abstract_interface(Class var1);

   public abstract void consumeEndian();

   public abstract int getPosition();

   public abstract java.lang.Object read_Abstract();

   public abstract Serializable read_Value();

   public abstract void read_any_array(AnySeqHolder var1, int var2, int var3);

   public abstract void read_boolean_array(BooleanSeqHolder var1, int var2, int var3);

   public abstract void read_char_array(CharSeqHolder var1, int var2, int var3);

   public abstract void read_wchar_array(WCharSeqHolder var1, int var2, int var3);

   public abstract void read_octet_array(OctetSeqHolder var1, int var2, int var3);

   public abstract void read_short_array(ShortSeqHolder var1, int var2, int var3);

   public abstract void read_ushort_array(UShortSeqHolder var1, int var2, int var3);

   public abstract void read_long_array(LongSeqHolder var1, int var2, int var3);

   public abstract void read_ulong_array(ULongSeqHolder var1, int var2, int var3);

   public abstract void read_ulonglong_array(ULongLongSeqHolder var1, int var2, int var3);

   public abstract void read_longlong_array(LongLongSeqHolder var1, int var2, int var3);

   public abstract void read_float_array(FloatSeqHolder var1, int var2, int var3);

   public abstract void read_double_array(DoubleSeqHolder var1, int var2, int var3);

   public abstract String[] _truncatable_ids();

   public abstract void mark(int var1);

   public abstract void reset();

   public boolean markSupported() {
      return false;
   }

   public abstract CDRInputStreamBase dup();

   public abstract BigDecimal read_fixed(short var1, short var2);

   public abstract boolean isLittleEndian();

   abstract void setHeaderPadding(boolean var1);

   public abstract ByteBuffer getByteBuffer();

   public abstract void setByteBuffer(ByteBuffer var1);

   public abstract void setByteBufferWithInfo(ByteBufferWithInfo var1);

   public abstract int getBufferLength();

   public abstract void setBufferLength(int var1);

   public abstract int getIndex();

   public abstract void setIndex(int var1);

   public abstract void orb(ORB var1);

   public abstract BufferManagerRead getBufferManager();

   public abstract GIOPVersion getGIOPVersion();

   abstract CodeBase getCodeBase();

   abstract void printBuffer();

   abstract void alignOnBoundary(int var1);

   abstract void performORBVersionSpecificInit();

   public abstract void resetCodeSetConverters();

   public abstract void start_value();

   public abstract void end_value();
}
