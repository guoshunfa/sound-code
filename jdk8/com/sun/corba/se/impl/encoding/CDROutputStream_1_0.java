package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.CacheTable;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ValueBase;
import org.omg.CORBA_2_3.portable.OutputStream;

public class CDROutputStream_1_0 extends CDROutputStreamBase {
   private static final int INDIRECTION_TAG = -1;
   protected boolean littleEndian;
   protected BufferManagerWrite bufferManagerWrite;
   ByteBufferWithInfo bbwi;
   protected ORB orb;
   protected ORBUtilSystemException wrapper;
   protected boolean debug = false;
   protected int blockSizeIndex = -1;
   protected int blockSizePosition = 0;
   protected byte streamFormatVersion;
   private static final int DEFAULT_BUFFER_SIZE = 1024;
   private static final String kWriteMethod = "write";
   private CacheTable codebaseCache = null;
   private CacheTable valueCache = null;
   private CacheTable repositoryIdCache = null;
   private int end_flag = 0;
   private int chunkedValueNestingLevel = 0;
   private boolean mustChunk = false;
   protected boolean inBlock = false;
   private int end_flag_position = 0;
   private int end_flag_index = 0;
   private ValueHandler valueHandler = null;
   private RepositoryIdUtility repIdUtil;
   private RepositoryIdStrings repIdStrs;
   private CodeSetConversion.CTBConverter charConverter;
   private CodeSetConversion.CTBConverter wcharConverter;
   private static final String _id = "IDL:omg.org/CORBA/DataOutputStream:1.0";
   private static final String[] _ids = new String[]{"IDL:omg.org/CORBA/DataOutputStream:1.0"};

   public void init(org.omg.CORBA.ORB var1, boolean var2, BufferManagerWrite var3, byte var4, boolean var5) {
      this.orb = (ORB)var1;
      this.wrapper = ORBUtilSystemException.get(this.orb, "rpc.encoding");
      this.debug = this.orb.transportDebugFlag;
      this.littleEndian = var2;
      this.bufferManagerWrite = var3;
      this.bbwi = new ByteBufferWithInfo(var1, var3, var5);
      this.streamFormatVersion = var4;
      this.createRepositoryIdHandlers();
   }

   public void init(org.omg.CORBA.ORB var1, boolean var2, BufferManagerWrite var3, byte var4) {
      this.init(var1, var2, var3, var4, true);
   }

   private final void createRepositoryIdHandlers() {
      this.repIdUtil = RepositoryIdFactory.getRepIdUtility();
      this.repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
   }

   public BufferManagerWrite getBufferManager() {
      return this.bufferManagerWrite;
   }

   public byte[] toByteArray() {
      byte[] var1 = new byte[this.bbwi.position()];

      for(int var2 = 0; var2 < this.bbwi.position(); ++var2) {
         var1[var2] = this.bbwi.byteBuffer.get(var2);
      }

      return var1;
   }

   public GIOPVersion getGIOPVersion() {
      return GIOPVersion.V1_0;
   }

   void setHeaderPadding(boolean var1) {
      throw this.wrapper.giopVersionError();
   }

   protected void handleSpecialChunkBegin(int var1) {
   }

   protected void handleSpecialChunkEnd() {
   }

   protected final int computeAlignment(int var1) {
      if (var1 > 1) {
         int var2 = this.bbwi.position() & var1 - 1;
         if (var2 != 0) {
            return var1 - var2;
         }
      }

      return 0;
   }

   protected void alignAndReserve(int var1, int var2) {
      this.bbwi.position(this.bbwi.position() + this.computeAlignment(var1));
      if (this.bbwi.position() + var2 > this.bbwi.buflen) {
         this.grow(var1, var2);
      }

   }

   protected void grow(int var1, int var2) {
      this.bbwi.needed = var2;
      this.bufferManagerWrite.overflow(this.bbwi);
   }

   public final void putEndian() throws SystemException {
      this.write_boolean(this.littleEndian);
   }

   public final boolean littleEndian() {
      return this.littleEndian;
   }

   void freeInternalCaches() {
      if (this.codebaseCache != null) {
         this.codebaseCache.done();
      }

      if (this.valueCache != null) {
         this.valueCache.done();
      }

      if (this.repositoryIdCache != null) {
         this.repositoryIdCache.done();
      }

   }

   public final void write_longdouble(double var1) {
      throw this.wrapper.longDoubleNotImplemented(CompletionStatus.COMPLETED_MAYBE);
   }

   public void write_octet(byte var1) {
      this.alignAndReserve(1, 1);
      this.bbwi.byteBuffer.put(this.bbwi.position(), var1);
      this.bbwi.position(this.bbwi.position() + 1);
   }

   public final void write_boolean(boolean var1) {
      this.write_octet((byte)(var1 ? 1 : 0));
   }

   public void write_char(char var1) {
      CodeSetConversion.CTBConverter var2 = this.getCharConverter();
      var2.convert(var1);
      if (var2.getNumBytes() > 1) {
         throw this.wrapper.invalidSingleCharCtb(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.write_octet(var2.getBytes()[0]);
      }
   }

   private final void writeLittleEndianWchar(char var1) {
      this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(var1 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(var1 >>> 8 & 255));
      this.bbwi.position(this.bbwi.position() + 2);
   }

   private final void writeBigEndianWchar(char var1) {
      this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(var1 >>> 8 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(var1 & 255));
      this.bbwi.position(this.bbwi.position() + 2);
   }

   private final void writeLittleEndianShort(short var1) {
      this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(var1 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(var1 >>> 8 & 255));
      this.bbwi.position(this.bbwi.position() + 2);
   }

   private final void writeBigEndianShort(short var1) {
      this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(var1 >>> 8 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(var1 & 255));
      this.bbwi.position(this.bbwi.position() + 2);
   }

   private final void writeLittleEndianLong(int var1) {
      this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(var1 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(var1 >>> 8 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(var1 >>> 16 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(var1 >>> 24 & 255));
      this.bbwi.position(this.bbwi.position() + 4);
   }

   private final void writeBigEndianLong(int var1) {
      this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(var1 >>> 24 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(var1 >>> 16 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(var1 >>> 8 & 255));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(var1 & 255));
      this.bbwi.position(this.bbwi.position() + 4);
   }

   private final void writeLittleEndianLongLong(long var1) {
      this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)((int)(var1 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)((int)(var1 >>> 8 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)((int)(var1 >>> 16 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)((int)(var1 >>> 24 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 4, (byte)((int)(var1 >>> 32 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 5, (byte)((int)(var1 >>> 40 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 6, (byte)((int)(var1 >>> 48 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 7, (byte)((int)(var1 >>> 56 & 255L)));
      this.bbwi.position(this.bbwi.position() + 8);
   }

   private final void writeBigEndianLongLong(long var1) {
      this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)((int)(var1 >>> 56 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)((int)(var1 >>> 48 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)((int)(var1 >>> 40 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)((int)(var1 >>> 32 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 4, (byte)((int)(var1 >>> 24 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 5, (byte)((int)(var1 >>> 16 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 6, (byte)((int)(var1 >>> 8 & 255L)));
      this.bbwi.byteBuffer.put(this.bbwi.position() + 7, (byte)((int)(var1 & 255L)));
      this.bbwi.position(this.bbwi.position() + 8);
   }

   public void write_wchar(char var1) {
      if (ORBUtility.isForeignORB(this.orb)) {
         throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.alignAndReserve(2, 2);
         if (this.littleEndian) {
            this.writeLittleEndianWchar(var1);
         } else {
            this.writeBigEndianWchar(var1);
         }

      }
   }

   public void write_short(short var1) {
      this.alignAndReserve(2, 2);
      if (this.littleEndian) {
         this.writeLittleEndianShort(var1);
      } else {
         this.writeBigEndianShort(var1);
      }

   }

   public final void write_ushort(short var1) {
      this.write_short(var1);
   }

   public void write_long(int var1) {
      this.alignAndReserve(4, 4);
      if (this.littleEndian) {
         this.writeLittleEndianLong(var1);
      } else {
         this.writeBigEndianLong(var1);
      }

   }

   public final void write_ulong(int var1) {
      this.write_long(var1);
   }

   public void write_longlong(long var1) {
      this.alignAndReserve(8, 8);
      if (this.littleEndian) {
         this.writeLittleEndianLongLong(var1);
      } else {
         this.writeBigEndianLongLong(var1);
      }

   }

   public final void write_ulonglong(long var1) {
      this.write_longlong(var1);
   }

   public final void write_float(float var1) {
      this.write_long(Float.floatToIntBits(var1));
   }

   public final void write_double(double var1) {
      this.write_longlong(Double.doubleToLongBits(var1));
   }

   public void write_string(String var1) {
      this.writeString(var1);
   }

   protected int writeString(String var1) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         CodeSetConversion.CTBConverter var2 = this.getCharConverter();
         var2.convert(var1);
         int var3 = var2.getNumBytes() + 1;
         this.handleSpecialChunkBegin(this.computeAlignment(4) + 4 + var3);
         this.write_long(var3);
         int var4 = this.get_offset() - 4;
         this.internalWriteOctetArray(var2.getBytes(), 0, var2.getNumBytes());
         this.write_octet((byte)0);
         this.handleSpecialChunkEnd();
         return var4;
      }
   }

   public void write_wstring(String var1) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else if (ORBUtility.isForeignORB(this.orb)) {
         throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
      } else {
         int var2 = var1.length() + 1;
         this.handleSpecialChunkBegin(4 + var2 * 2 + this.computeAlignment(4));
         this.write_long(var2);

         for(int var3 = 0; var3 < var2 - 1; ++var3) {
            this.write_wchar(var1.charAt(var3));
         }

         this.write_short((short)0);
         this.handleSpecialChunkEnd();
      }
   }

   void internalWriteOctetArray(byte[] var1, int var2, int var3) {
      int var4 = var2;

      int var7;
      for(boolean var5 = true; var4 < var3 + var2; var4 += var7) {
         if (this.bbwi.position() + 1 > this.bbwi.buflen || var5) {
            var5 = false;
            this.alignAndReserve(1, 1);
         }

         int var6 = this.bbwi.buflen - this.bbwi.position();
         int var8 = var3 + var2 - var4;
         var7 = var8 < var6 ? var8 : var6;

         for(int var9 = 0; var9 < var7; ++var9) {
            this.bbwi.byteBuffer.put(this.bbwi.position() + var9, var1[var4 + var9]);
         }

         this.bbwi.position(this.bbwi.position() + var7);
      }

   }

   public final void write_octet_array(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(var3);
         this.internalWriteOctetArray(var1, var2, var3);
         this.handleSpecialChunkEnd();
      }
   }

   public void write_Principal(Principal var1) {
      this.write_long(var1.name().length);
      this.write_octet_array(var1.name(), 0, var1.name().length);
   }

   public void write_any(Any var1) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.write_TypeCode(var1.type());
         var1.write_value(this.parent);
      }
   }

   public void write_TypeCode(TypeCode var1) {
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

   public void write_Object(Object var1) {
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

   public void write_abstract_interface(java.lang.Object var1) {
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

   public void write_value(Serializable var1, Class var2) {
      this.write_value(var1);
   }

   private void writeWStringValue(String var1) {
      int var2 = this.writeValueTag(this.mustChunk, true, (String)null);
      this.write_repositoryId(this.repIdStrs.getWStringValueRepId());
      this.updateIndirectionTable(var2, var1, var1);
      if (this.mustChunk) {
         this.start_block();
         --this.end_flag;
         --this.chunkedValueNestingLevel;
      } else {
         --this.end_flag;
      }

      this.write_wstring(var1);
      if (this.mustChunk) {
         this.end_block();
      }

      this.writeEndTag(this.mustChunk);
   }

   private void writeArray(Serializable var1, Class var2) {
      if (this.valueHandler == null) {
         this.valueHandler = ORBUtility.createValueHandler();
      }

      int var3 = this.writeValueTag(this.mustChunk, true, Util.getCodebase(var2));
      this.write_repositoryId(this.repIdStrs.createSequenceRepID(var2));
      this.updateIndirectionTable(var3, var1, var1);
      if (this.mustChunk) {
         this.start_block();
         --this.end_flag;
         --this.chunkedValueNestingLevel;
      } else {
         --this.end_flag;
      }

      if (this.valueHandler instanceof ValueHandlerMultiFormat) {
         ValueHandlerMultiFormat var4 = (ValueHandlerMultiFormat)this.valueHandler;
         var4.writeValue(this.parent, var1, this.streamFormatVersion);
      } else {
         this.valueHandler.writeValue(this.parent, var1);
      }

      if (this.mustChunk) {
         this.end_block();
      }

      this.writeEndTag(this.mustChunk);
   }

   private void writeValueBase(ValueBase var1, Class var2) {
      this.mustChunk = true;
      int var3 = this.writeValueTag(true, true, Util.getCodebase(var2));
      String var4 = var1._truncatable_ids()[0];
      this.write_repositoryId(var4);
      this.updateIndirectionTable(var3, var1, var1);
      this.start_block();
      --this.end_flag;
      --this.chunkedValueNestingLevel;
      this.writeIDLValue(var1, var4);
      this.end_block();
      this.writeEndTag(true);
   }

   private void writeRMIIIOPValueType(Serializable var1, Class var2) {
      if (this.valueHandler == null) {
         this.valueHandler = ORBUtility.createValueHandler();
      }

      Serializable var3 = var1;
      var1 = this.valueHandler.writeReplace(var1);
      if (var1 == null) {
         this.write_long(0);
      } else {
         if (var1 != var3) {
            if (this.valueCache != null && this.valueCache.containsKey(var1)) {
               this.writeIndirection(-1, this.valueCache.getVal(var1));
               return;
            }

            var2 = var1.getClass();
         }

         if (this.mustChunk || this.valueHandler.isCustomMarshaled(var2)) {
            this.mustChunk = true;
         }

         int var4 = this.writeValueTag(this.mustChunk, true, Util.getCodebase(var2));
         this.write_repositoryId(this.repIdStrs.createForJavaType(var2));
         this.updateIndirectionTable(var4, var1, var3);
         if (this.mustChunk) {
            --this.end_flag;
            --this.chunkedValueNestingLevel;
            this.start_block();
         } else {
            --this.end_flag;
         }

         if (this.valueHandler instanceof ValueHandlerMultiFormat) {
            ValueHandlerMultiFormat var5 = (ValueHandlerMultiFormat)this.valueHandler;
            var5.writeValue(this.parent, var1, this.streamFormatVersion);
         } else {
            this.valueHandler.writeValue(this.parent, var1);
         }

         if (this.mustChunk) {
            this.end_block();
         }

         this.writeEndTag(this.mustChunk);
      }
   }

   public void write_value(Serializable var1, String var2) {
      if (var1 == null) {
         this.write_long(0);
      } else if (this.valueCache != null && this.valueCache.containsKey(var1)) {
         this.writeIndirection(-1, this.valueCache.getVal(var1));
      } else {
         Class var3 = var1.getClass();
         boolean var4 = this.mustChunk;
         if (this.mustChunk) {
            this.mustChunk = true;
         }

         if (this.inBlock) {
            this.end_block();
         }

         if (var3.isArray()) {
            this.writeArray(var1, var3);
         } else if (var1 instanceof ValueBase) {
            this.writeValueBase((ValueBase)var1, var3);
         } else if (this.shouldWriteAsIDLEntity(var1)) {
            this.writeIDLEntity((IDLEntity)var1);
         } else if (var1 instanceof String) {
            this.writeWStringValue((String)var1);
         } else if (var1 instanceof Class) {
            this.writeClass(var2, (Class)var1);
         } else {
            this.writeRMIIIOPValueType(var1, var3);
         }

         this.mustChunk = var4;
         if (this.mustChunk) {
            this.start_block();
         }

      }
   }

   public void write_value(Serializable var1) {
      this.write_value(var1, (String)null);
   }

   public void write_value(Serializable var1, BoxedValueHelper var2) {
      if (var1 == null) {
         this.write_long(0);
      } else if (this.valueCache != null && this.valueCache.containsKey(var1)) {
         this.writeIndirection(-1, this.valueCache.getVal(var1));
      } else {
         boolean var3 = this.mustChunk;
         boolean var4 = false;
         if (var2 instanceof ValueHelper) {
            short var5;
            try {
               var5 = ((ValueHelper)var2).get_type().type_modifier();
            } catch (BadKind var7) {
               var5 = 0;
            }

            if (var1 instanceof CustomMarshal && var5 == 1) {
               var4 = true;
               this.mustChunk = true;
            }

            if (var5 == 3) {
               this.mustChunk = true;
            }
         }

         int var8;
         if (this.mustChunk) {
            if (this.inBlock) {
               this.end_block();
            }

            var8 = this.writeValueTag(true, this.orb.getORBData().useRepId(), Util.getCodebase(var1.getClass()));
            if (this.orb.getORBData().useRepId()) {
               this.write_repositoryId(var2.get_id());
            }

            this.updateIndirectionTable(var8, var1, var1);
            this.start_block();
            --this.end_flag;
            --this.chunkedValueNestingLevel;
            if (var4) {
               ((CustomMarshal)var1).marshal(this.parent);
            } else {
               var2.write_value(this.parent, var1);
            }

            this.end_block();
            this.writeEndTag(true);
         } else {
            var8 = this.writeValueTag(false, this.orb.getORBData().useRepId(), Util.getCodebase(var1.getClass()));
            if (this.orb.getORBData().useRepId()) {
               this.write_repositoryId(var2.get_id());
            }

            this.updateIndirectionTable(var8, var1, var1);
            --this.end_flag;
            var2.write_value(this.parent, var1);
            this.writeEndTag(false);
         }

         this.mustChunk = var3;
         if (this.mustChunk) {
            this.start_block();
         }

      }
   }

   public int get_offset() {
      return this.bbwi.position();
   }

   public void start_block() {
      if (this.debug) {
         this.dprint("CDROutputStream_1_0 start_block, position" + this.bbwi.position());
      }

      this.write_long(0);
      this.inBlock = true;
      this.blockSizePosition = this.get_offset();
      this.blockSizeIndex = this.bbwi.position();
      if (this.debug) {
         this.dprint("CDROutputStream_1_0 start_block, blockSizeIndex " + this.blockSizeIndex);
      }

   }

   protected void writeLongWithoutAlign(int var1) {
      if (this.littleEndian) {
         this.writeLittleEndianLong(var1);
      } else {
         this.writeBigEndianLong(var1);
      }

   }

   public void end_block() {
      if (this.debug) {
         this.dprint("CDROutputStream_1_0.java end_block");
      }

      if (this.inBlock) {
         if (this.debug) {
            this.dprint("CDROutputStream_1_0.java end_block, in a block");
         }

         this.inBlock = false;
         if (this.get_offset() == this.blockSizePosition) {
            this.bbwi.position(this.bbwi.position() - 4);
            this.blockSizeIndex = -1;
            this.blockSizePosition = -1;
         } else {
            int var1 = this.bbwi.position();
            this.bbwi.position(this.blockSizeIndex - 4);
            this.writeLongWithoutAlign(var1 - this.blockSizeIndex);
            this.bbwi.position(var1);
            this.blockSizeIndex = -1;
            this.blockSizePosition = -1;
         }
      }
   }

   public org.omg.CORBA.ORB orb() {
      return this.orb;
   }

   public final void write_boolean_array(boolean[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(var3);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_boolean(var1[var2 + var4]);
         }

         this.handleSpecialChunkEnd();
      }
   }

   public final void write_char_array(char[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(var3);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_char(var1[var2 + var4]);
         }

         this.handleSpecialChunkEnd();
      }
   }

   public void write_wchar_array(char[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(this.computeAlignment(2) + var3 * 2);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_wchar(var1[var2 + var4]);
         }

         this.handleSpecialChunkEnd();
      }
   }

   public final void write_short_array(short[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(this.computeAlignment(2) + var3 * 2);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_short(var1[var2 + var4]);
         }

         this.handleSpecialChunkEnd();
      }
   }

   public final void write_ushort_array(short[] var1, int var2, int var3) {
      this.write_short_array(var1, var2, var3);
   }

   public final void write_long_array(int[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(this.computeAlignment(4) + var3 * 4);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_long(var1[var2 + var4]);
         }

         this.handleSpecialChunkEnd();
      }
   }

   public final void write_ulong_array(int[] var1, int var2, int var3) {
      this.write_long_array(var1, var2, var3);
   }

   public final void write_longlong_array(long[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(this.computeAlignment(8) + var3 * 8);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_longlong(var1[var2 + var4]);
         }

         this.handleSpecialChunkEnd();
      }
   }

   public final void write_ulonglong_array(long[] var1, int var2, int var3) {
      this.write_longlong_array(var1, var2, var3);
   }

   public final void write_float_array(float[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(this.computeAlignment(4) + var3 * 4);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_float(var1[var2 + var4]);
         }

         this.handleSpecialChunkEnd();
      }
   }

   public final void write_double_array(double[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.handleSpecialChunkBegin(this.computeAlignment(8) + var3 * 8);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_double(var1[var2 + var4]);
         }

         this.handleSpecialChunkEnd();
      }
   }

   public void write_string_array(String[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_string(var1[var2 + var4]);
         }

      }
   }

   public void write_wstring_array(String[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
      } else {
         for(int var4 = 0; var4 < var3; ++var4) {
            this.write_wstring(var1[var2 + var4]);
         }

      }
   }

   public final void write_any_array(Any[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write_any(var1[var2 + var4]);
      }

   }

   public void writeTo(java.io.OutputStream var1) throws IOException {
      java.lang.Object var2 = null;
      byte[] var5;
      if (this.bbwi.byteBuffer.hasArray()) {
         var5 = this.bbwi.byteBuffer.array();
      } else {
         int var3 = this.bbwi.position();
         var5 = new byte[var3];

         for(int var4 = 0; var4 < var3; ++var4) {
            var5[var4] = this.bbwi.byteBuffer.get(var4);
         }
      }

      var1.write(var5, 0, this.bbwi.position());
   }

   public void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream var1) {
      java.lang.Object var2 = null;
      byte[] var5;
      if (this.bbwi.byteBuffer.hasArray()) {
         var5 = this.bbwi.byteBuffer.array();
      } else {
         int var3 = this.bbwi.position();
         var5 = new byte[var3];

         for(int var4 = 0; var4 < var3; ++var4) {
            var5[var4] = this.bbwi.byteBuffer.get(var4);
         }
      }

      var1.write_long(this.bbwi.position());
      var1.write_octet_array(var5, 0, this.bbwi.position());
   }

   public final int getSize() {
      return this.bbwi.position();
   }

   public int getIndex() {
      return this.bbwi.position();
   }

   public boolean isLittleEndian() {
      return this.littleEndian;
   }

   public void setIndex(int var1) {
      this.bbwi.position(var1);
   }

   public ByteBufferWithInfo getByteBufferWithInfo() {
      return this.bbwi;
   }

   public void setByteBufferWithInfo(ByteBufferWithInfo var1) {
      this.bbwi = var1;
   }

   public ByteBuffer getByteBuffer() {
      ByteBuffer var1 = null;
      if (this.bbwi != null) {
         var1 = this.bbwi.byteBuffer;
      }

      return var1;
   }

   public void setByteBuffer(ByteBuffer var1) {
      this.bbwi.byteBuffer = var1;
   }

   private final void updateIndirectionTable(int var1, java.lang.Object var2, java.lang.Object var3) {
      if (this.valueCache == null) {
         this.valueCache = new CacheTable(this.orb, true);
      }

      this.valueCache.put(var2, var1);
      if (var3 != var2) {
         this.valueCache.put(var3, var1);
      }

   }

   private final void write_repositoryId(String var1) {
      if (this.repositoryIdCache != null && this.repositoryIdCache.containsKey(var1)) {
         this.writeIndirection(-1, this.repositoryIdCache.getVal(var1));
      } else {
         int var2 = this.writeString(var1);
         if (this.repositoryIdCache == null) {
            this.repositoryIdCache = new CacheTable(this.orb, true);
         }

         this.repositoryIdCache.put(var1, var2);
      }
   }

   private void write_codebase(String var1, int var2) {
      if (this.codebaseCache != null && this.codebaseCache.containsKey(var1)) {
         this.writeIndirection(-1, this.codebaseCache.getVal(var1));
      } else {
         this.write_string(var1);
         if (this.codebaseCache == null) {
            this.codebaseCache = new CacheTable(this.orb, true);
         }

         this.codebaseCache.put(var1, var2);
      }

   }

   private final int writeValueTag(boolean var1, boolean var2, String var3) {
      int var4 = 0;
      if (var1 && !var2) {
         if (var3 == null) {
            this.write_long(this.repIdUtil.getStandardRMIChunkedNoRepStrId());
            var4 = this.get_offset() - 4;
         } else {
            this.write_long(this.repIdUtil.getCodeBaseRMIChunkedNoRepStrId());
            var4 = this.get_offset() - 4;
            this.write_codebase(var3, this.get_offset());
         }
      } else if (var1 && var2) {
         if (var3 == null) {
            this.write_long(this.repIdUtil.getStandardRMIChunkedId());
            var4 = this.get_offset() - 4;
         } else {
            this.write_long(this.repIdUtil.getCodeBaseRMIChunkedId());
            var4 = this.get_offset() - 4;
            this.write_codebase(var3, this.get_offset());
         }
      } else if (!var1 && !var2) {
         if (var3 == null) {
            this.write_long(this.repIdUtil.getStandardRMIUnchunkedNoRepStrId());
            var4 = this.get_offset() - 4;
         } else {
            this.write_long(this.repIdUtil.getCodeBaseRMIUnchunkedNoRepStrId());
            var4 = this.get_offset() - 4;
            this.write_codebase(var3, this.get_offset());
         }
      } else if (!var1 && var2) {
         if (var3 == null) {
            this.write_long(this.repIdUtil.getStandardRMIUnchunkedId());
            var4 = this.get_offset() - 4;
         } else {
            this.write_long(this.repIdUtil.getCodeBaseRMIUnchunkedId());
            var4 = this.get_offset() - 4;
            this.write_codebase(var3, this.get_offset());
         }
      }

      return var4;
   }

   private void writeIDLValue(Serializable var1, String var2) {
      if (var1 instanceof StreamableValue) {
         ((StreamableValue)var1)._write(this.parent);
      } else if (var1 instanceof CustomValue) {
         ((CustomValue)var1).marshal(this.parent);
      } else {
         BoxedValueHelper var3 = Utility.getHelper(var1.getClass(), (String)null, var2);
         boolean var4 = false;
         if (var3 instanceof ValueHelper && var1 instanceof CustomMarshal) {
            try {
               if (((ValueHelper)var3).get_type().type_modifier() == 1) {
                  var4 = true;
               }
            } catch (BadKind var6) {
               throw this.wrapper.badTypecodeForCustomValue(CompletionStatus.COMPLETED_MAYBE, var6);
            }
         }

         if (var4) {
            ((CustomMarshal)var1).marshal(this.parent);
         } else {
            var3.write_value(this.parent, var1);
         }
      }

   }

   private void writeEndTag(boolean var1) {
      if (var1) {
         if (this.get_offset() == this.end_flag_position && this.bbwi.position() == this.end_flag_index) {
            this.bbwi.position(this.bbwi.position() - 4);
         }

         this.writeNestingLevel();
         this.end_flag_index = this.bbwi.position();
         this.end_flag_position = this.get_offset();
         ++this.chunkedValueNestingLevel;
      }

      ++this.end_flag;
   }

   private void writeNestingLevel() {
      if (this.orb != null && !ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) && ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) > 0) {
         this.write_long(this.end_flag);
      } else {
         this.write_long(this.chunkedValueNestingLevel);
      }

   }

   private void writeClass(String var1, Class var2) {
      if (var1 == null) {
         var1 = this.repIdStrs.getClassDescValueRepId();
      }

      int var3 = this.writeValueTag(this.mustChunk, true, (String)null);
      this.updateIndirectionTable(var3, var2, var2);
      this.write_repositoryId(var1);
      if (this.mustChunk) {
         this.start_block();
         --this.end_flag;
         --this.chunkedValueNestingLevel;
      } else {
         --this.end_flag;
      }

      this.writeClassBody(var2);
      if (this.mustChunk) {
         this.end_block();
      }

      this.writeEndTag(this.mustChunk);
   }

   private void writeClassBody(Class var1) {
      if (this.orb != null && !ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) && ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) > 0) {
         this.write_value(this.repIdStrs.createForAnyType(var1));
         this.write_value(Util.getCodebase(var1));
      } else {
         this.write_value(Util.getCodebase(var1));
         this.write_value(this.repIdStrs.createForAnyType(var1));
      }

   }

   private boolean shouldWriteAsIDLEntity(Serializable var1) {
      return var1 instanceof IDLEntity && !(var1 instanceof ValueBase) && !(var1 instanceof Object);
   }

   private void writeIDLEntity(IDLEntity var1) {
      this.mustChunk = true;
      String var2 = this.repIdStrs.createForJavaType((Serializable)var1);
      Class var3 = var1.getClass();
      String var4 = Util.getCodebase(var3);
      int var5 = this.writeValueTag(true, true, var4);
      this.updateIndirectionTable(var5, var1, var1);
      this.write_repositoryId(var2);
      --this.end_flag;
      --this.chunkedValueNestingLevel;
      this.start_block();

      try {
         ClassLoader var6 = var3 == null ? null : var3.getClassLoader();
         final Class var7 = Utility.loadClassForClass(var3.getName() + "Helper", var4, var6, var3, var6);
         final Class[] var8 = new Class[]{org.omg.CORBA.portable.OutputStream.class, var3};
         Method var9 = null;

         try {
            var9 = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public java.lang.Object run() throws NoSuchMethodException {
                  return var7.getDeclaredMethod("write", var8);
               }
            });
         } catch (PrivilegedActionException var11) {
            throw (NoSuchMethodException)var11.getException();
         }

         java.lang.Object[] var10 = new java.lang.Object[]{this.parent, var1};
         var9.invoke((java.lang.Object)null, var10);
      } catch (ClassNotFoundException var12) {
         throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, var12);
      } catch (NoSuchMethodException var13) {
         throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, var13);
      } catch (IllegalAccessException var14) {
         throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, var14);
      } catch (InvocationTargetException var15) {
         throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, var15);
      }

      this.end_block();
      this.writeEndTag(true);
   }

   public void write_Abstract(java.lang.Object var1) {
      this.write_abstract_interface(var1);
   }

   public void write_Value(Serializable var1) {
      this.write_value(var1);
   }

   public void write_fixed(BigDecimal var1, short var2, short var3) {
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

   public void write_fixed(BigDecimal var1) {
      this.write_fixed(var1.toString(), var1.signum());
   }

   public void write_fixed(String var1, int var2) {
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

   public String[] _truncatable_ids() {
      return _ids == null ? null : (String[])((String[])_ids.clone());
   }

   public void printBuffer() {
      printBuffer(this.bbwi);
   }

   public static void printBuffer(ByteBufferWithInfo var0) {
      System.out.println("+++++++ Output Buffer ++++++++");
      System.out.println();
      System.out.println("Current position: " + var0.position());
      System.out.println("Total length : " + var0.buflen);
      System.out.println();
      char[] var1 = new char[16];

      try {
         for(int var2 = 0; var2 < var0.position(); var2 += 16) {
            int var3;
            int var4;
            for(var3 = 0; var3 < 16 && var3 + var2 < var0.position(); ++var3) {
               var4 = var0.byteBuffer.get(var2 + var3);
               if (var4 < 0) {
                  var4 += 256;
               }

               String var5 = Integer.toHexString(var4);
               if (var5.length() == 1) {
                  var5 = "0" + var5;
               }

               System.out.print(var5 + " ");
            }

            while(var3 < 16) {
               System.out.print("   ");
               ++var3;
            }

            for(var4 = 0; var4 < 16 && var4 + var2 < var0.position(); ++var4) {
               if (ORBUtility.isPrintable((char)var0.byteBuffer.get(var2 + var4))) {
                  var1[var4] = (char)var0.byteBuffer.get(var2 + var4);
               } else {
                  var1[var4] = '.';
               }
            }

            System.out.println(new String(var1, 0, var4));
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
      }

      System.out.println("++++++++++++++++++++++++++++++");
   }

   public void writeIndirection(int var1, int var2) {
      this.handleSpecialChunkBegin(this.computeAlignment(4) + 8);
      this.write_long(var1);
      this.write_long(var2 - this.parent.getRealIndex(this.get_offset()));
      this.handleSpecialChunkEnd();
   }

   protected CodeSetConversion.CTBConverter getCharConverter() {
      if (this.charConverter == null) {
         this.charConverter = this.parent.createCharCTBConverter();
      }

      return this.charConverter;
   }

   protected CodeSetConversion.CTBConverter getWCharConverter() {
      if (this.wcharConverter == null) {
         this.wcharConverter = this.parent.createWCharCTBConverter();
      }

      return this.wcharConverter;
   }

   protected void dprint(String var1) {
      if (this.debug) {
         ORBUtility.dprint((java.lang.Object)this, var1);
      }

   }

   void alignOnBoundary(int var1) {
      this.alignAndReserve(var1, 0);
   }

   public void start_value(String var1) {
      if (this.debug) {
         this.dprint("start_value w/ rep id " + var1 + " called at pos " + this.get_offset() + " position " + this.bbwi.position());
      }

      if (this.inBlock) {
         this.end_block();
      }

      this.writeValueTag(true, true, (String)null);
      this.write_repositoryId(var1);
      --this.end_flag;
      --this.chunkedValueNestingLevel;
      this.start_block();
   }

   public void end_value() {
      if (this.debug) {
         this.dprint("end_value called at pos " + this.get_offset() + " position " + this.bbwi.position());
      }

      this.end_block();
      this.writeEndTag(true);
      if (this.debug) {
         this.dprint("mustChunk is " + this.mustChunk);
      }

      if (this.mustChunk) {
         this.start_block();
      }

   }

   public void close() throws IOException {
      this.getBufferManager().close();
      if (this.getByteBufferWithInfo() != null && this.getByteBuffer() != null) {
         MessageMediator var1 = this.parent.getMessageMediator();
         if (var1 != null) {
            CDRInputObject var2 = (CDRInputObject)var1.getInputObject();
            if (var2 != null && var2.isSharing(this.getByteBuffer())) {
               var2.setByteBuffer((ByteBuffer)null);
               var2.setByteBufferWithInfo((ByteBufferWithInfo)null);
            }
         }

         ByteBufferPool var6 = this.orb.getByteBufferPool();
         if (this.debug) {
            int var3 = System.identityHashCode(this.bbwi.byteBuffer);
            StringBuffer var4 = new StringBuffer(80);
            var4.append(".close - releasing ByteBuffer id (");
            var4.append(var3).append(") to ByteBufferPool.");
            String var5 = var4.toString();
            this.dprint(var5);
         }

         var6.releaseByteBuffer(this.getByteBuffer());
         this.bbwi.byteBuffer = null;
         this.bbwi = null;
      }

   }
}
