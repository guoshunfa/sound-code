package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DataOutputStream;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class CDROutputStream extends OutputStream implements MarshalOutputStream, DataOutputStream, ValueOutputStream {
   private CDROutputStreamBase impl;
   protected ORB orb;
   protected ORBUtilSystemException wrapper;
   protected CorbaMessageMediator corbaMessageMediator;

   public CDROutputStream(ORB var1, GIOPVersion var2, byte var3, boolean var4, BufferManagerWrite var5, byte var6, boolean var7) {
      this.impl = CDROutputStream.OutputStreamFactory.newOutputStream(var1, var2, var3);
      this.impl.init(var1, var4, var5, var6, var7);
      this.impl.setParent(this);
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.encoding");
   }

   public CDROutputStream(ORB var1, GIOPVersion var2, byte var3, boolean var4, BufferManagerWrite var5, byte var6) {
      this(var1, var2, var3, var4, var5, var6, true);
   }

   public abstract InputStream create_input_stream();

   public final void write_boolean(boolean var1) {
      this.impl.write_boolean(var1);
   }

   public final void write_char(char var1) {
      this.impl.write_char(var1);
   }

   public final void write_wchar(char var1) {
      this.impl.write_wchar(var1);
   }

   public final void write_octet(byte var1) {
      this.impl.write_octet(var1);
   }

   public final void write_short(short var1) {
      this.impl.write_short(var1);
   }

   public final void write_ushort(short var1) {
      this.impl.write_ushort(var1);
   }

   public final void write_long(int var1) {
      this.impl.write_long(var1);
   }

   public final void write_ulong(int var1) {
      this.impl.write_ulong(var1);
   }

   public final void write_longlong(long var1) {
      this.impl.write_longlong(var1);
   }

   public final void write_ulonglong(long var1) {
      this.impl.write_ulonglong(var1);
   }

   public final void write_float(float var1) {
      this.impl.write_float(var1);
   }

   public final void write_double(double var1) {
      this.impl.write_double(var1);
   }

   public final void write_string(String var1) {
      this.impl.write_string(var1);
   }

   public final void write_wstring(String var1) {
      this.impl.write_wstring(var1);
   }

   public final void write_boolean_array(boolean[] var1, int var2, int var3) {
      this.impl.write_boolean_array(var1, var2, var3);
   }

   public final void write_char_array(char[] var1, int var2, int var3) {
      this.impl.write_char_array(var1, var2, var3);
   }

   public final void write_wchar_array(char[] var1, int var2, int var3) {
      this.impl.write_wchar_array(var1, var2, var3);
   }

   public final void write_octet_array(byte[] var1, int var2, int var3) {
      this.impl.write_octet_array(var1, var2, var3);
   }

   public final void write_short_array(short[] var1, int var2, int var3) {
      this.impl.write_short_array(var1, var2, var3);
   }

   public final void write_ushort_array(short[] var1, int var2, int var3) {
      this.impl.write_ushort_array(var1, var2, var3);
   }

   public final void write_long_array(int[] var1, int var2, int var3) {
      this.impl.write_long_array(var1, var2, var3);
   }

   public final void write_ulong_array(int[] var1, int var2, int var3) {
      this.impl.write_ulong_array(var1, var2, var3);
   }

   public final void write_longlong_array(long[] var1, int var2, int var3) {
      this.impl.write_longlong_array(var1, var2, var3);
   }

   public final void write_ulonglong_array(long[] var1, int var2, int var3) {
      this.impl.write_ulonglong_array(var1, var2, var3);
   }

   public final void write_float_array(float[] var1, int var2, int var3) {
      this.impl.write_float_array(var1, var2, var3);
   }

   public final void write_double_array(double[] var1, int var2, int var3) {
      this.impl.write_double_array(var1, var2, var3);
   }

   public final void write_Object(Object var1) {
      this.impl.write_Object(var1);
   }

   public final void write_TypeCode(TypeCode var1) {
      this.impl.write_TypeCode(var1);
   }

   public final void write_any(Any var1) {
      this.impl.write_any(var1);
   }

   public final void write_Principal(Principal var1) {
      this.impl.write_Principal(var1);
   }

   public final void write(int var1) throws IOException {
      this.impl.write(var1);
   }

   public final void write_fixed(BigDecimal var1) {
      this.impl.write_fixed(var1);
   }

   public final void write_Context(Context var1, ContextList var2) {
      this.impl.write_Context(var1, var2);
   }

   public final org.omg.CORBA.ORB orb() {
      return this.impl.orb();
   }

   public final void write_value(Serializable var1) {
      this.impl.write_value(var1);
   }

   public final void write_value(Serializable var1, Class var2) {
      this.impl.write_value(var1, var2);
   }

   public final void write_value(Serializable var1, String var2) {
      this.impl.write_value(var1, var2);
   }

   public final void write_value(Serializable var1, BoxedValueHelper var2) {
      this.impl.write_value(var1, var2);
   }

   public final void write_abstract_interface(java.lang.Object var1) {
      this.impl.write_abstract_interface(var1);
   }

   public final void write(byte[] var1) throws IOException {
      this.impl.write(var1);
   }

   public final void write(byte[] var1, int var2, int var3) throws IOException {
      this.impl.write(var1, var2, var3);
   }

   public final void flush() throws IOException {
      this.impl.flush();
   }

   public final void close() throws IOException {
      this.impl.close();
   }

   public final void start_block() {
      this.impl.start_block();
   }

   public final void end_block() {
      this.impl.end_block();
   }

   public final void putEndian() {
      this.impl.putEndian();
   }

   public void writeTo(java.io.OutputStream var1) throws IOException {
      this.impl.writeTo(var1);
   }

   public final byte[] toByteArray() {
      return this.impl.toByteArray();
   }

   public final void write_Abstract(java.lang.Object var1) {
      this.impl.write_Abstract(var1);
   }

   public final void write_Value(Serializable var1) {
      this.impl.write_Value(var1);
   }

   public final void write_any_array(Any[] var1, int var2, int var3) {
      this.impl.write_any_array(var1, var2, var3);
   }

   public void setMessageMediator(MessageMediator var1) {
      this.corbaMessageMediator = (CorbaMessageMediator)var1;
   }

   public MessageMediator getMessageMediator() {
      return this.corbaMessageMediator;
   }

   public final String[] _truncatable_ids() {
      return this.impl._truncatable_ids();
   }

   protected final int getSize() {
      return this.impl.getSize();
   }

   protected final int getIndex() {
      return this.impl.getIndex();
   }

   protected int getRealIndex(int var1) {
      return var1;
   }

   protected final void setIndex(int var1) {
      this.impl.setIndex(var1);
   }

   protected final ByteBuffer getByteBuffer() {
      return this.impl.getByteBuffer();
   }

   protected final void setByteBuffer(ByteBuffer var1) {
      this.impl.setByteBuffer(var1);
   }

   protected final boolean isSharing(ByteBuffer var1) {
      return this.getByteBuffer() == var1;
   }

   public final boolean isLittleEndian() {
      return this.impl.isLittleEndian();
   }

   public ByteBufferWithInfo getByteBufferWithInfo() {
      return this.impl.getByteBufferWithInfo();
   }

   protected void setByteBufferWithInfo(ByteBufferWithInfo var1) {
      this.impl.setByteBufferWithInfo(var1);
   }

   public final BufferManagerWrite getBufferManager() {
      return this.impl.getBufferManager();
   }

   public final void write_fixed(BigDecimal var1, short var2, short var3) {
      this.impl.write_fixed(var1, var2, var3);
   }

   public final void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream var1) {
      this.impl.writeOctetSequenceTo(var1);
   }

   public final GIOPVersion getGIOPVersion() {
      return this.impl.getGIOPVersion();
   }

   public final void writeIndirection(int var1, int var2) {
      this.impl.writeIndirection(var1, var2);
   }

   protected CodeSetConversion.CTBConverter createCharCTBConverter() {
      return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1);
   }

   protected abstract CodeSetConversion.CTBConverter createWCharCTBConverter();

   protected final void freeInternalCaches() {
      this.impl.freeInternalCaches();
   }

   void printBuffer() {
      this.impl.printBuffer();
   }

   public void alignOnBoundary(int var1) {
      this.impl.alignOnBoundary(var1);
   }

   public void setHeaderPadding(boolean var1) {
      this.impl.setHeaderPadding(var1);
   }

   public void start_value(String var1) {
      this.impl.start_value(var1);
   }

   public void end_value() {
      this.impl.end_value();
   }

   private static class OutputStreamFactory {
      public static CDROutputStreamBase newOutputStream(ORB var0, GIOPVersion var1, byte var2) {
         switch(var1.intValue()) {
         case 256:
            return new CDROutputStream_1_0();
         case 257:
            return new CDROutputStream_1_1();
         case 258:
            if (var2 != 0) {
               return new IDLJavaSerializationOutputStream(var2);
            }

            return new CDROutputStream_1_2();
         default:
            ORBUtilSystemException var3 = ORBUtilSystemException.get(var0, "rpc.encoding");
            throw var3.unsupportedGiopVersion(var1);
         }
      }
   }
}
