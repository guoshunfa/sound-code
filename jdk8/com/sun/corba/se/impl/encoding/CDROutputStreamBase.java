package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;

abstract class CDROutputStreamBase extends OutputStream {
   protected CDROutputStream parent;

   public void setParent(CDROutputStream var1) {
      this.parent = var1;
   }

   public void init(ORB var1, BufferManagerWrite var2, byte var3) {
      this.init(var1, false, var2, var3, true);
   }

   protected abstract void init(ORB var1, boolean var2, BufferManagerWrite var3, byte var4, boolean var5);

   public abstract void write_boolean(boolean var1);

   public abstract void write_char(char var1);

   public abstract void write_wchar(char var1);

   public abstract void write_octet(byte var1);

   public abstract void write_short(short var1);

   public abstract void write_ushort(short var1);

   public abstract void write_long(int var1);

   public abstract void write_ulong(int var1);

   public abstract void write_longlong(long var1);

   public abstract void write_ulonglong(long var1);

   public abstract void write_float(float var1);

   public abstract void write_double(double var1);

   public abstract void write_string(String var1);

   public abstract void write_wstring(String var1);

   public abstract void write_boolean_array(boolean[] var1, int var2, int var3);

   public abstract void write_char_array(char[] var1, int var2, int var3);

   public abstract void write_wchar_array(char[] var1, int var2, int var3);

   public abstract void write_octet_array(byte[] var1, int var2, int var3);

   public abstract void write_short_array(short[] var1, int var2, int var3);

   public abstract void write_ushort_array(short[] var1, int var2, int var3);

   public abstract void write_long_array(int[] var1, int var2, int var3);

   public abstract void write_ulong_array(int[] var1, int var2, int var3);

   public abstract void write_longlong_array(long[] var1, int var2, int var3);

   public abstract void write_ulonglong_array(long[] var1, int var2, int var3);

   public abstract void write_float_array(float[] var1, int var2, int var3);

   public abstract void write_double_array(double[] var1, int var2, int var3);

   public abstract void write_Object(Object var1);

   public abstract void write_TypeCode(TypeCode var1);

   public abstract void write_any(Any var1);

   public abstract void write_Principal(Principal var1);

   public void write(int var1) throws IOException {
      throw new NO_IMPLEMENT();
   }

   public abstract void write_fixed(BigDecimal var1);

   public void write_Context(Context var1, ContextList var2) {
      throw new NO_IMPLEMENT();
   }

   public abstract ORB orb();

   public abstract void write_value(Serializable var1);

   public abstract void write_value(Serializable var1, Class var2);

   public abstract void write_value(Serializable var1, String var2);

   public abstract void write_value(Serializable var1, BoxedValueHelper var2);

   public abstract void write_abstract_interface(java.lang.Object var1);

   public abstract void start_block();

   public abstract void end_block();

   public abstract void putEndian();

   public abstract void writeTo(OutputStream var1) throws IOException;

   public abstract byte[] toByteArray();

   public abstract void write_Abstract(java.lang.Object var1);

   public abstract void write_Value(Serializable var1);

   public abstract void write_any_array(Any[] var1, int var2, int var3);

   public abstract String[] _truncatable_ids();

   abstract void setHeaderPadding(boolean var1);

   public abstract int getSize();

   public abstract int getIndex();

   public abstract void setIndex(int var1);

   public abstract ByteBuffer getByteBuffer();

   public abstract void setByteBuffer(ByteBuffer var1);

   public abstract boolean isLittleEndian();

   public abstract ByteBufferWithInfo getByteBufferWithInfo();

   public abstract void setByteBufferWithInfo(ByteBufferWithInfo var1);

   public abstract BufferManagerWrite getBufferManager();

   public abstract void write_fixed(BigDecimal var1, short var2, short var3);

   public abstract void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream var1);

   public abstract GIOPVersion getGIOPVersion();

   public abstract void writeIndirection(int var1, int var2);

   abstract void freeInternalCaches();

   abstract void printBuffer();

   abstract void alignOnBoundary(int var1);

   public abstract void start_value(String var1);

   public abstract void end_value();
}
