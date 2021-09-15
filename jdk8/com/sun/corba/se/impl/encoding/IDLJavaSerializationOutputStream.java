package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA_2_3.portable.OutputStream;

final class IDLJavaSerializationOutputStream extends CDROutputStreamBase {
   private ORB orb;
   private byte encodingVersion;
   private ObjectOutputStream os;
   private IDLJavaSerializationOutputStream._ByteArrayOutputStream bos;
   private BufferManagerWrite bufferManager;
   private final int directWriteLength = 16;
   protected ORBUtilSystemException wrapper;

   public IDLJavaSerializationOutputStream(byte var1) {
      this.encodingVersion = var1;
   }

   public void init(org.omg.CORBA.ORB var1, boolean var2, BufferManagerWrite var3, byte var4, boolean var5) {
      this.orb = (ORB)var1;
      this.bufferManager = var3;
      this.wrapper = ORBUtilSystemException.get((ORB)var1, "rpc.encoding");
      this.bos = new IDLJavaSerializationOutputStream._ByteArrayOutputStream(1024);
   }

   private void initObjectOutputStream() {
      if (this.os != null) {
         throw this.wrapper.javaStreamInitFailed();
      } else {
         try {
            this.os = new IDLJavaSerializationOutputStream.MarshalObjectOutputStream(this.bos, this.orb);
         } catch (Exception var2) {
            throw this.wrapper.javaStreamInitFailed((Throwable)var2);
         }
      }
   }

   public final void write_boolean(boolean var1) {
      try {
         this.os.writeBoolean(var1);
      } catch (Exception var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "write_boolean");
      }
   }

   public final void write_char(char var1) {
      try {
         this.os.writeChar(var1);
      } catch (Exception var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "write_char");
      }
   }

   public final void write_wchar(char var1) {
      this.write_char(var1);
   }

   public final void write_octet(byte var1) {
      if (this.bos.size() < 16) {
         this.bos.write(var1);
         if (this.bos.size() == 16) {
            this.initObjectOutputStream();
         }

      } else {
         try {
            this.os.writeByte(var1);
         } catch (Exception var3) {
            throw this.wrapper.javaSerializationException((Throwable)var3, "write_octet");
         }
      }
   }

   public final void write_short(short var1) {
      try {
         this.os.writeShort(var1);
      } catch (Exception var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "write_short");
      }
   }

   public final void write_ushort(short var1) {
      this.write_short(var1);
   }

   public final void write_long(int var1) {
      if (this.bos.size() < 16) {
         this.bos.write((byte)(var1 >>> 24 & 255));
         this.bos.write((byte)(var1 >>> 16 & 255));
         this.bos.write((byte)(var1 >>> 8 & 255));
         this.bos.write((byte)(var1 >>> 0 & 255));
         if (this.bos.size() == 16) {
            this.initObjectOutputStream();
         } else if (this.bos.size() > 16) {
            this.wrapper.javaSerializationException("write_long");
         }

      } else {
         try {
            this.os.writeInt(var1);
         } catch (Exception var3) {
            throw this.wrapper.javaSerializationException((Throwable)var3, "write_long");
         }
      }
   }

   public final void write_ulong(int var1) {
      this.write_long(var1);
   }

   public final void write_longlong(long var1) {
      try {
         this.os.writeLong(var1);
      } catch (Exception var4) {
         throw this.wrapper.javaSerializationException((Throwable)var4, "write_longlong");
      }
   }

   public final void write_ulonglong(long var1) {
      this.write_longlong(var1);
   }

   public final void write_float(float var1) {
      try {
         this.os.writeFloat(var1);
      } catch (Exception var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "write_float");
      }
   }

   public final void write_double(double var1) {
      try {
         this.os.writeDouble(var1);
      } catch (Exception var4) {
         throw this.wrapper.javaSerializationException((Throwable)var4, "write_double");
      }
   }

   public final void write_string(String var1) {
      try {
         this.os.writeUTF(var1);
      } catch (Exception var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "write_string");
      }
   }

   public final void write_wstring(String var1) {
      try {
         this.os.writeObject(var1);
      } catch (Exception var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "write_wstring");
      }
   }

   public final void write_boolean_array(boolean[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_boolean(var1[var2 + var4]);
      }

   }

   public final void write_char_array(char[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_char(var1[var2 + var4]);
      }

   }

   public final void write_wchar_array(char[] var1, int var2, int var3) {
      this.write_char_array(var1, var2, var3);
   }

   public final void write_octet_array(byte[] var1, int var2, int var3) {
      try {
         this.os.write(var1, var2, var3);
      } catch (Exception var5) {
         throw this.wrapper.javaSerializationException((Throwable)var5, "write_octet_array");
      }
   }

   public final void write_short_array(short[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_short(var1[var2 + var4]);
      }

   }

   public final void write_ushort_array(short[] var1, int var2, int var3) {
      this.write_short_array(var1, var2, var3);
   }

   public final void write_long_array(int[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_long(var1[var2 + var4]);
      }

   }

   public final void write_ulong_array(int[] var1, int var2, int var3) {
      this.write_long_array(var1, var2, var3);
   }

   public final void write_longlong_array(long[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_longlong(var1[var2 + var4]);
      }

   }

   public final void write_ulonglong_array(long[] var1, int var2, int var3) {
      this.write_longlong_array(var1, var2, var3);
   }

   public final void write_float_array(float[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_float(var1[var2 + var4]);
      }

   }

   public final void write_double_array(double[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_double(var1[var2 + var4]);
      }

   }

   public final void write_Object(Object var1) {
      IOR var2;
      if (var1 == null) {
         var2 = IORFactories.makeIOR(this.orb);
         var2.write(this.parent);
      } else if (var1 instanceof LocalObject) {
         throw this.wrapper.writeLocalObject(CompletionStatus.COMPLETED_MAYBE);
      } else {
         var2 = ORBUtility.connectAndGetIOR(this.orb, var1);
         var2.write(this.parent);
      }
   }

   public final void write_TypeCode(TypeCode var1) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         TypeCodeImpl var2;
         if (var1 instanceof TypeCodeImpl) {
            var2 = (TypeCodeImpl)var1;
         } else {
            var2 = new TypeCodeImpl(this.orb, var1);
         }

         var2.write_value((OutputStream)this.parent);
      }
   }

   public final void write_any(Any var1) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.write_TypeCode(var1.type());
         var1.write_value(this.parent);
      }
   }

   public final void write_Principal(Principal var1) {
      this.write_long(var1.name().length);
      this.write_octet_array(var1.name(), 0, var1.name().length);
   }

   public final void write_fixed(BigDecimal var1) {
      this.write_fixed(var1.toString(), var1.signum());
   }

   private void write_fixed(String var1, int var2) {
      int var3 = var1.length();
      byte var4 = 0;
      int var7 = 0;

      char var5;
      int var8;
      for(var8 = 0; var8 < var3; ++var8) {
         var5 = var1.charAt(var8);
         if (var5 != '-' && var5 != '+' && var5 != '.') {
            ++var7;
         }
      }

      for(var8 = 0; var8 < var3; ++var8) {
         var5 = var1.charAt(var8);
         if (var5 != '-' && var5 != '+' && var5 != '.') {
            byte var6 = (byte)Character.digit((char)var5, 10);
            if (var6 == -1) {
               throw this.wrapper.badDigitInFixed(CompletionStatus.COMPLETED_MAYBE);
            }

            if (var7 % 2 == 0) {
               var4 |= var6;
               this.write_octet(var4);
               var4 = 0;
            } else {
               var4 = (byte)(var4 | var6 << 4);
            }

            --var7;
         }
      }

      if (var2 == -1) {
         var4 = (byte)(var4 | 13);
      } else {
         var4 = (byte)(var4 | 12);
      }

      this.write_octet(var4);
   }

   public final org.omg.CORBA.ORB orb() {
      return this.orb;
   }

   public final void write_value(Serializable var1) {
      this.write_value(var1, (String)null);
   }

   public final void write_value(Serializable var1, Class var2) {
      this.write_value(var1);
   }

   public final void write_value(Serializable var1, String var2) {
      try {
         this.os.writeObject(var1);
      } catch (Exception var4) {
         throw this.wrapper.javaSerializationException((Throwable)var4, "write_value");
      }
   }

   public final void write_value(Serializable var1, BoxedValueHelper var2) {
      this.write_value(var1, (String)null);
   }

   public final void write_abstract_interface(java.lang.Object var1) {
      boolean var2 = false;
      Object var3 = null;
      if (var1 != null && var1 instanceof Object) {
         var3 = (Object)var1;
         var2 = true;
      }

      this.write_boolean(var2);
      if (var2) {
         this.write_Object(var3);
      } else {
         try {
            this.write_value((Serializable)var1);
         } catch (ClassCastException var5) {
            if (var1 instanceof Serializable) {
               throw var5;
            }

            ORBUtility.throwNotSerializableForCorba(var1.getClass().getName());
         }
      }

   }

   public final void start_block() {
      throw this.wrapper.giopVersionError();
   }

   public final void end_block() {
      throw this.wrapper.giopVersionError();
   }

   public final void putEndian() {
      throw this.wrapper.giopVersionError();
   }

   public void writeTo(java.io.OutputStream var1) throws IOException {
      try {
         this.os.flush();
         this.bos.writeTo(var1);
      } catch (Exception var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "writeTo");
      }
   }

   public final byte[] toByteArray() {
      try {
         this.os.flush();
         return this.bos.toByteArray();
      } catch (Exception var2) {
         throw this.wrapper.javaSerializationException((Throwable)var2, "toByteArray");
      }
   }

   public final void write_Abstract(java.lang.Object var1) {
      this.write_abstract_interface(var1);
   }

   public final void write_Value(Serializable var1) {
      this.write_value(var1);
   }

   public final void write_any_array(Any[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_any(var1[var2 + var4]);
      }

   }

   public final String[] _truncatable_ids() {
      throw this.wrapper.giopVersionError();
   }

   public final int getSize() {
      try {
         this.os.flush();
         return this.bos.size();
      } catch (Exception var2) {
         throw this.wrapper.javaSerializationException((Throwable)var2, "write_boolean");
      }
   }

   public final int getIndex() {
      return this.getSize();
   }

   protected int getRealIndex(int var1) {
      return this.getSize();
   }

   public final void setIndex(int var1) {
      throw this.wrapper.giopVersionError();
   }

   public final ByteBuffer getByteBuffer() {
      throw this.wrapper.giopVersionError();
   }

   public final void setByteBuffer(ByteBuffer var1) {
      throw this.wrapper.giopVersionError();
   }

   public final boolean isLittleEndian() {
      return false;
   }

   public ByteBufferWithInfo getByteBufferWithInfo() {
      try {
         this.os.flush();
      } catch (Exception var2) {
         throw this.wrapper.javaSerializationException((Throwable)var2, "getByteBufferWithInfo");
      }

      ByteBuffer var1 = ByteBuffer.wrap(this.bos.getByteArray());
      var1.limit(this.bos.size());
      return new ByteBufferWithInfo(this.orb, var1, this.bos.size());
   }

   public void setByteBufferWithInfo(ByteBufferWithInfo var1) {
      throw this.wrapper.giopVersionError();
   }

   public final BufferManagerWrite getBufferManager() {
      return this.bufferManager;
   }

   public final void write_fixed(BigDecimal var1, short var2, short var3) {
      String var4 = var1.toString();
      if (var4.charAt(0) == '-' || var4.charAt(0) == '+') {
         var4 = var4.substring(1);
      }

      int var8 = var4.indexOf(46);
      String var5;
      String var6;
      if (var8 == -1) {
         var5 = var4;
         var6 = null;
      } else if (var8 == 0) {
         var5 = null;
         var6 = var4;
      } else {
         var5 = var4.substring(0, var8);
         var6 = var4.substring(var8 + 1);
      }

      StringBuffer var7 = new StringBuffer(var2);
      if (var6 != null) {
         var7.append(var6);
      }

      while(var7.length() < var3) {
         var7.append('0');
      }

      if (var5 != null) {
         var7.insert(0, (String)var5);
      }

      while(var7.length() < var2) {
         var7.insert(0, (char)'0');
      }

      this.write_fixed(var7.toString(), var1.signum());
   }

   public final void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream var1) {
      byte[] var2 = this.toByteArray();
      var1.write_long(var2.length);
      var1.write_octet_array(var2, 0, var2.length);
   }

   public final GIOPVersion getGIOPVersion() {
      return GIOPVersion.V1_2;
   }

   public final void writeIndirection(int var1, int var2) {
      throw this.wrapper.giopVersionError();
   }

   void freeInternalCaches() {
   }

   void printBuffer() {
      byte[] var1 = this.toByteArray();
      System.out.println("+++++++ Output Buffer ++++++++");
      System.out.println();
      System.out.println("Current position: " + var1.length);
      System.out.println();
      char[] var2 = new char[16];

      try {
         for(int var3 = 0; var3 < var1.length; var3 += 16) {
            int var4;
            int var5;
            for(var4 = 0; var4 < 16 && var4 + var3 < var1.length; ++var4) {
               var5 = var1[var3 + var4];
               if (var5 < 0) {
                  var5 += 256;
               }

               String var6 = Integer.toHexString(var5);
               if (var6.length() == 1) {
                  var6 = "0" + var6;
               }

               System.out.print(var6 + " ");
            }

            while(var4 < 16) {
               System.out.print("   ");
               ++var4;
            }

            for(var5 = 0; var5 < 16 && var5 + var3 < var1.length; ++var5) {
               if (ORBUtility.isPrintable((char)var1[var3 + var5])) {
                  var2[var5] = (char)var1[var3 + var5];
               } else {
                  var2[var5] = '.';
               }
            }

            System.out.println(new String(var2, 0, var5));
         }
      } catch (Throwable var7) {
         var7.printStackTrace();
      }

      System.out.println("++++++++++++++++++++++++++++++");
   }

   public void alignOnBoundary(int var1) {
      throw this.wrapper.giopVersionError();
   }

   public void setHeaderPadding(boolean var1) {
   }

   public void start_value(String var1) {
      throw this.wrapper.giopVersionError();
   }

   public void end_value() {
      throw this.wrapper.giopVersionError();
   }

   class MarshalObjectOutputStream extends ObjectOutputStream {
      ORB orb;

      MarshalObjectOutputStream(java.io.OutputStream var2, ORB var3) throws IOException {
         super(var2);
         this.orb = var3;
         AccessController.doPrivileged(new PrivilegedAction() {
            public java.lang.Object run() {
               MarshalObjectOutputStream.this.enableReplaceObject(true);
               return null;
            }
         });
      }

      protected final java.lang.Object replaceObject(java.lang.Object var1) throws IOException {
         try {
            return var1 instanceof Remote && !StubAdapter.isStub(var1) ? Utility.autoConnect(var1, this.orb, true) : var1;
         } catch (Exception var4) {
            IOException var3 = new IOException("replaceObject failed");
            var3.initCause(var4);
            throw var3;
         }
      }
   }

   class _ByteArrayOutputStream extends ByteArrayOutputStream {
      _ByteArrayOutputStream(int var2) {
         super(var2);
      }

      byte[] getByteArray() {
         return this.buf;
      }
   }
}
