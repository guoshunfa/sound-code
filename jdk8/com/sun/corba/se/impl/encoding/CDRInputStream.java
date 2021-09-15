package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.Context;
import org.omg.CORBA.DataInputStream;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
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
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class CDRInputStream extends InputStream implements MarshalInputStream, DataInputStream, ValueInputStream {
   protected CorbaMessageMediator messageMediator;
   private CDRInputStreamBase impl;

   public CDRInputStream() {
   }

   public CDRInputStream(CDRInputStream var1) {
      this.impl = var1.impl.dup();
      this.impl.setParent(this);
   }

   public CDRInputStream(ORB var1, ByteBuffer var2, int var3, boolean var4, GIOPVersion var5, byte var6, BufferManagerRead var7) {
      this.impl = CDRInputStream.InputStreamFactory.newInputStream((com.sun.corba.se.spi.orb.ORB)var1, var5, var6);
      this.impl.init(var1, var2, var3, var4, var7);
      this.impl.setParent(this);
   }

   public final boolean read_boolean() {
      return this.impl.read_boolean();
   }

   public final char read_char() {
      return this.impl.read_char();
   }

   public final char read_wchar() {
      return this.impl.read_wchar();
   }

   public final byte read_octet() {
      return this.impl.read_octet();
   }

   public final short read_short() {
      return this.impl.read_short();
   }

   public final short read_ushort() {
      return this.impl.read_ushort();
   }

   public final int read_long() {
      return this.impl.read_long();
   }

   public final int read_ulong() {
      return this.impl.read_ulong();
   }

   public final long read_longlong() {
      return this.impl.read_longlong();
   }

   public final long read_ulonglong() {
      return this.impl.read_ulonglong();
   }

   public final float read_float() {
      return this.impl.read_float();
   }

   public final double read_double() {
      return this.impl.read_double();
   }

   public final String read_string() {
      return this.impl.read_string();
   }

   public final String read_wstring() {
      return this.impl.read_wstring();
   }

   public final void read_boolean_array(boolean[] var1, int var2, int var3) {
      this.impl.read_boolean_array(var1, var2, var3);
   }

   public final void read_char_array(char[] var1, int var2, int var3) {
      this.impl.read_char_array(var1, var2, var3);
   }

   public final void read_wchar_array(char[] var1, int var2, int var3) {
      this.impl.read_wchar_array(var1, var2, var3);
   }

   public final void read_octet_array(byte[] var1, int var2, int var3) {
      this.impl.read_octet_array(var1, var2, var3);
   }

   public final void read_short_array(short[] var1, int var2, int var3) {
      this.impl.read_short_array(var1, var2, var3);
   }

   public final void read_ushort_array(short[] var1, int var2, int var3) {
      this.impl.read_ushort_array(var1, var2, var3);
   }

   public final void read_long_array(int[] var1, int var2, int var3) {
      this.impl.read_long_array(var1, var2, var3);
   }

   public final void read_ulong_array(int[] var1, int var2, int var3) {
      this.impl.read_ulong_array(var1, var2, var3);
   }

   public final void read_longlong_array(long[] var1, int var2, int var3) {
      this.impl.read_longlong_array(var1, var2, var3);
   }

   public final void read_ulonglong_array(long[] var1, int var2, int var3) {
      this.impl.read_ulonglong_array(var1, var2, var3);
   }

   public final void read_float_array(float[] var1, int var2, int var3) {
      this.impl.read_float_array(var1, var2, var3);
   }

   public final void read_double_array(double[] var1, int var2, int var3) {
      this.impl.read_double_array(var1, var2, var3);
   }

   public final Object read_Object() {
      return this.impl.read_Object();
   }

   public final TypeCode read_TypeCode() {
      return this.impl.read_TypeCode();
   }

   public final Any read_any() {
      return this.impl.read_any();
   }

   public final Principal read_Principal() {
      return this.impl.read_Principal();
   }

   public final int read() throws IOException {
      return this.impl.read();
   }

   public final BigDecimal read_fixed() {
      return this.impl.read_fixed();
   }

   public final Context read_Context() {
      return this.impl.read_Context();
   }

   public final Object read_Object(Class var1) {
      return this.impl.read_Object(var1);
   }

   public final ORB orb() {
      return this.impl.orb();
   }

   public final Serializable read_value() {
      return this.impl.read_value();
   }

   public final Serializable read_value(Class var1) {
      return this.impl.read_value(var1);
   }

   public final Serializable read_value(BoxedValueHelper var1) {
      return this.impl.read_value(var1);
   }

   public final Serializable read_value(String var1) {
      return this.impl.read_value(var1);
   }

   public final Serializable read_value(Serializable var1) {
      return this.impl.read_value(var1);
   }

   public final java.lang.Object read_abstract_interface() {
      return this.impl.read_abstract_interface();
   }

   public final java.lang.Object read_abstract_interface(Class var1) {
      return this.impl.read_abstract_interface(var1);
   }

   public final void consumeEndian() {
      this.impl.consumeEndian();
   }

   public final int getPosition() {
      return this.impl.getPosition();
   }

   public final java.lang.Object read_Abstract() {
      return this.impl.read_Abstract();
   }

   public final Serializable read_Value() {
      return this.impl.read_Value();
   }

   public final void read_any_array(AnySeqHolder var1, int var2, int var3) {
      this.impl.read_any_array(var1, var2, var3);
   }

   public final void read_boolean_array(BooleanSeqHolder var1, int var2, int var3) {
      this.impl.read_boolean_array(var1, var2, var3);
   }

   public final void read_char_array(CharSeqHolder var1, int var2, int var3) {
      this.impl.read_char_array(var1, var2, var3);
   }

   public final void read_wchar_array(WCharSeqHolder var1, int var2, int var3) {
      this.impl.read_wchar_array(var1, var2, var3);
   }

   public final void read_octet_array(OctetSeqHolder var1, int var2, int var3) {
      this.impl.read_octet_array(var1, var2, var3);
   }

   public final void read_short_array(ShortSeqHolder var1, int var2, int var3) {
      this.impl.read_short_array(var1, var2, var3);
   }

   public final void read_ushort_array(UShortSeqHolder var1, int var2, int var3) {
      this.impl.read_ushort_array(var1, var2, var3);
   }

   public final void read_long_array(LongSeqHolder var1, int var2, int var3) {
      this.impl.read_long_array(var1, var2, var3);
   }

   public final void read_ulong_array(ULongSeqHolder var1, int var2, int var3) {
      this.impl.read_ulong_array(var1, var2, var3);
   }

   public final void read_ulonglong_array(ULongLongSeqHolder var1, int var2, int var3) {
      this.impl.read_ulonglong_array(var1, var2, var3);
   }

   public final void read_longlong_array(LongLongSeqHolder var1, int var2, int var3) {
      this.impl.read_longlong_array(var1, var2, var3);
   }

   public final void read_float_array(FloatSeqHolder var1, int var2, int var3) {
      this.impl.read_float_array(var1, var2, var3);
   }

   public final void read_double_array(DoubleSeqHolder var1, int var2, int var3) {
      this.impl.read_double_array(var1, var2, var3);
   }

   public final String[] _truncatable_ids() {
      return this.impl._truncatable_ids();
   }

   public final int read(byte[] var1) throws IOException {
      return this.impl.read(var1);
   }

   public final int read(byte[] var1, int var2, int var3) throws IOException {
      return this.impl.read(var1, var2, var3);
   }

   public final long skip(long var1) throws IOException {
      return this.impl.skip(var1);
   }

   public final int available() throws IOException {
      return this.impl.available();
   }

   public final void close() throws IOException {
      this.impl.close();
   }

   public final void mark(int var1) {
      this.impl.mark(var1);
   }

   public final void reset() {
      this.impl.reset();
   }

   public final boolean markSupported() {
      return this.impl.markSupported();
   }

   public abstract CDRInputStream dup();

   public final BigDecimal read_fixed(short var1, short var2) {
      return this.impl.read_fixed(var1, var2);
   }

   public final boolean isLittleEndian() {
      return this.impl.isLittleEndian();
   }

   protected final ByteBuffer getByteBuffer() {
      return this.impl.getByteBuffer();
   }

   protected final void setByteBuffer(ByteBuffer var1) {
      this.impl.setByteBuffer(var1);
   }

   protected final void setByteBufferWithInfo(ByteBufferWithInfo var1) {
      this.impl.setByteBufferWithInfo(var1);
   }

   protected final boolean isSharing(ByteBuffer var1) {
      return this.getByteBuffer() == var1;
   }

   public final int getBufferLength() {
      return this.impl.getBufferLength();
   }

   protected final void setBufferLength(int var1) {
      this.impl.setBufferLength(var1);
   }

   protected final int getIndex() {
      return this.impl.getIndex();
   }

   protected final void setIndex(int var1) {
      this.impl.setIndex(var1);
   }

   public final void orb(ORB var1) {
      this.impl.orb(var1);
   }

   public final GIOPVersion getGIOPVersion() {
      return this.impl.getGIOPVersion();
   }

   public final BufferManagerRead getBufferManager() {
      return this.impl.getBufferManager();
   }

   public CodeBase getCodeBase() {
      return null;
   }

   protected CodeSetConversion.BTCConverter createCharBTCConverter() {
      return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1, this.impl.isLittleEndian());
   }

   protected abstract CodeSetConversion.BTCConverter createWCharBTCConverter();

   void printBuffer() {
      this.impl.printBuffer();
   }

   public void alignOnBoundary(int var1) {
      this.impl.alignOnBoundary(var1);
   }

   public void setHeaderPadding(boolean var1) {
      this.impl.setHeaderPadding(var1);
   }

   public void performORBVersionSpecificInit() {
      if (this.impl != null) {
         this.impl.performORBVersionSpecificInit();
      }

   }

   public void resetCodeSetConverters() {
      this.impl.resetCodeSetConverters();
   }

   public void setMessageMediator(MessageMediator var1) {
      this.messageMediator = (CorbaMessageMediator)var1;
   }

   public MessageMediator getMessageMediator() {
      return this.messageMediator;
   }

   public void start_value() {
      this.impl.start_value();
   }

   public void end_value() {
      this.impl.end_value();
   }

   private static class InputStreamFactory {
      public static CDRInputStreamBase newInputStream(com.sun.corba.se.spi.orb.ORB var0, GIOPVersion var1, byte var2) {
         switch(var1.intValue()) {
         case 256:
            return new CDRInputStream_1_0();
         case 257:
            return new CDRInputStream_1_1();
         case 258:
            if (var2 != 0) {
               return new IDLJavaSerializationInputStream(var2);
            }

            return new CDRInputStream_1_2();
         default:
            ORBUtilSystemException var3 = ORBUtilSystemException.get(var0, "rpc.encoding");
            throw var3.unsupportedGiopVersion(var1);
         }
      }
   }
}
