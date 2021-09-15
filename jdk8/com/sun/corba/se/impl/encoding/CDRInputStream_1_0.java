package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.impl.corba.PrincipalImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.CacheTable;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdInterface;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.Object;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ValueBase;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA_2_3.portable.InputStream;

public class CDRInputStream_1_0 extends CDRInputStreamBase implements RestorableInputStream {
   private static final String kReadMethod = "read";
   private static final int maxBlockLength = 2147483392;
   protected BufferManagerRead bufferManagerRead;
   protected ByteBufferWithInfo bbwi;
   private boolean debug = false;
   protected boolean littleEndian;
   protected ORB orb;
   protected ORBUtilSystemException wrapper;
   protected OMGSystemException omgWrapper;
   protected ValueHandler valueHandler = null;
   private CacheTable valueCache = null;
   private CacheTable repositoryIdCache = null;
   private CacheTable codebaseCache = null;
   protected int blockLength = 2147483392;
   protected int end_flag = 0;
   private int chunkedValueNestingLevel = 0;
   protected int valueIndirection = 0;
   protected int stringIndirection = 0;
   protected boolean isChunked = false;
   private RepositoryIdUtility repIdUtil;
   private RepositoryIdStrings repIdStrs;
   private CodeSetConversion.BTCConverter charConverter;
   private CodeSetConversion.BTCConverter wcharConverter;
   private boolean specialNoOptionalDataState = false;
   private static final String _id = "IDL:omg.org/CORBA/DataInputStream:1.0";
   private static final String[] _ids = new String[]{"IDL:omg.org/CORBA/DataInputStream:1.0"};
   protected MarkAndResetHandler markAndResetHandler = null;

   public CDRInputStreamBase dup() {
      CDRInputStreamBase var1 = null;

      try {
         var1 = (CDRInputStreamBase)this.getClass().newInstance();
      } catch (Exception var3) {
         throw this.wrapper.couldNotDuplicateCdrInputStream((Throwable)var3);
      }

      var1.init(this.orb, this.bbwi.byteBuffer, this.bbwi.buflen, this.littleEndian, this.bufferManagerRead);
      ((CDRInputStream_1_0)var1).bbwi.position(this.bbwi.position());
      ((CDRInputStream_1_0)var1).bbwi.byteBuffer.limit(this.bbwi.buflen);
      return var1;
   }

   public void init(org.omg.CORBA.ORB var1, ByteBuffer var2, int var3, boolean var4, BufferManagerRead var5) {
      this.orb = (ORB)var1;
      this.wrapper = ORBUtilSystemException.get((ORB)var1, "rpc.encoding");
      this.omgWrapper = OMGSystemException.get((ORB)var1, "rpc.encoding");
      this.littleEndian = var4;
      this.bufferManagerRead = var5;
      this.bbwi = new ByteBufferWithInfo(var1, var2, 0);
      this.bbwi.buflen = var3;
      this.bbwi.byteBuffer.limit(this.bbwi.buflen);
      this.markAndResetHandler = this.bufferManagerRead.getMarkAndResetHandler();
      this.debug = ((ORB)var1).transportDebugFlag;
   }

   void performORBVersionSpecificInit() {
      this.createRepositoryIdHandlers();
   }

   private final void createRepositoryIdHandlers() {
      this.repIdUtil = RepositoryIdFactory.getRepIdUtility();
      this.repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
   }

   public GIOPVersion getGIOPVersion() {
      return GIOPVersion.V1_0;
   }

   void setHeaderPadding(boolean var1) {
      throw this.wrapper.giopVersionError();
   }

   protected final int computeAlignment(int var1, int var2) {
      if (var2 > 1) {
         int var3 = var1 & var2 - 1;
         if (var3 != 0) {
            return var2 - var3;
         }
      }

      return 0;
   }

   public int getSize() {
      return this.bbwi.position();
   }

   protected void checkBlockLength(int var1, int var2) {
      if (this.isChunked) {
         if (this.specialNoOptionalDataState) {
            throw this.omgWrapper.rmiiiopOptionalDataIncompatible1();
         } else {
            boolean var3 = false;
            if (this.blockLength == this.get_offset()) {
               this.blockLength = 2147483392;
               this.start_block();
               if (this.blockLength == 2147483392) {
                  var3 = true;
               }
            } else if (this.blockLength < this.get_offset()) {
               throw this.wrapper.chunkOverflow();
            }

            int var4 = this.computeAlignment(this.bbwi.position(), var1) + var2;
            if (this.blockLength != 2147483392 && this.blockLength < this.get_offset() + var4) {
               throw this.omgWrapper.rmiiiopOptionalDataIncompatible2();
            } else {
               if (var3) {
                  int var5 = this.read_long();
                  this.bbwi.position(this.bbwi.position() - 4);
                  if (var5 < 0) {
                     throw this.omgWrapper.rmiiiopOptionalDataIncompatible3();
                  }
               }

            }
         }
      }
   }

   protected void alignAndCheck(int var1, int var2) {
      this.checkBlockLength(var1, var2);
      int var3 = this.computeAlignment(this.bbwi.position(), var1);
      this.bbwi.position(this.bbwi.position() + var3);
      if (this.bbwi.position() + var2 > this.bbwi.buflen) {
         this.grow(var1, var2);
      }

   }

   protected void grow(int var1, int var2) {
      this.bbwi.needed = var2;
      this.bbwi = this.bufferManagerRead.underflow(this.bbwi);
   }

   public final void consumeEndian() {
      this.littleEndian = this.read_boolean();
   }

   public final double read_longdouble() {
      throw this.wrapper.longDoubleNotImplemented(CompletionStatus.COMPLETED_MAYBE);
   }

   public final boolean read_boolean() {
      return this.read_octet() != 0;
   }

   public final char read_char() {
      this.alignAndCheck(1, 1);
      return this.getConvertedChars(1, this.getCharConverter())[0];
   }

   public char read_wchar() {
      if (ORBUtility.isForeignORB(this.orb)) {
         throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.alignAndCheck(2, 2);
         int var1;
         int var2;
         if (this.littleEndian) {
            var2 = this.bbwi.byteBuffer.get(this.bbwi.position()) & 255;
            this.bbwi.position(this.bbwi.position() + 1);
            var1 = this.bbwi.byteBuffer.get(this.bbwi.position()) & 255;
            this.bbwi.position(this.bbwi.position() + 1);
         } else {
            var1 = this.bbwi.byteBuffer.get(this.bbwi.position()) & 255;
            this.bbwi.position(this.bbwi.position() + 1);
            var2 = this.bbwi.byteBuffer.get(this.bbwi.position()) & 255;
            this.bbwi.position(this.bbwi.position() + 1);
         }

         return (char)((var1 << 8) + (var2 << 0));
      }
   }

   public final byte read_octet() {
      this.alignAndCheck(1, 1);
      byte var1 = this.bbwi.byteBuffer.get(this.bbwi.position());
      this.bbwi.position(this.bbwi.position() + 1);
      return var1;
   }

   public final short read_short() {
      this.alignAndCheck(2, 2);
      int var1;
      int var2;
      if (this.littleEndian) {
         var2 = this.bbwi.byteBuffer.get(this.bbwi.position()) << 0 & 255;
         this.bbwi.position(this.bbwi.position() + 1);
         var1 = this.bbwi.byteBuffer.get(this.bbwi.position()) << 8 & '\uff00';
         this.bbwi.position(this.bbwi.position() + 1);
      } else {
         var1 = this.bbwi.byteBuffer.get(this.bbwi.position()) << 8 & '\uff00';
         this.bbwi.position(this.bbwi.position() + 1);
         var2 = this.bbwi.byteBuffer.get(this.bbwi.position()) << 0 & 255;
         this.bbwi.position(this.bbwi.position() + 1);
      }

      return (short)(var1 | var2);
   }

   public final short read_ushort() {
      return this.read_short();
   }

   public final int read_long() {
      this.alignAndCheck(4, 4);
      int var5 = this.bbwi.position();
      int var1;
      int var2;
      int var3;
      int var4;
      if (this.littleEndian) {
         var4 = this.bbwi.byteBuffer.get(var5++) & 255;
         var3 = this.bbwi.byteBuffer.get(var5++) & 255;
         var2 = this.bbwi.byteBuffer.get(var5++) & 255;
         var1 = this.bbwi.byteBuffer.get(var5++) & 255;
      } else {
         var1 = this.bbwi.byteBuffer.get(var5++) & 255;
         var2 = this.bbwi.byteBuffer.get(var5++) & 255;
         var3 = this.bbwi.byteBuffer.get(var5++) & 255;
         var4 = this.bbwi.byteBuffer.get(var5++) & 255;
      }

      this.bbwi.position(var5);
      return var1 << 24 | var2 << 16 | var3 << 8 | var4;
   }

   public final int read_ulong() {
      return this.read_long();
   }

   public final long read_longlong() {
      this.alignAndCheck(8, 8);
      long var1;
      long var3;
      if (this.littleEndian) {
         var3 = (long)this.read_long() & 4294967295L;
         var1 = (long)this.read_long() << 32;
      } else {
         var1 = (long)this.read_long() << 32;
         var3 = (long)this.read_long() & 4294967295L;
      }

      return var1 | var3;
   }

   public final long read_ulonglong() {
      return this.read_longlong();
   }

   public final float read_float() {
      return Float.intBitsToFloat(this.read_long());
   }

   public final double read_double() {
      return Double.longBitsToDouble(this.read_longlong());
   }

   protected final void checkForNegativeLength(int var1) {
      if (var1 < 0) {
         throw this.wrapper.negativeStringLength((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(var1));
      }
   }

   protected final String readStringOrIndirection(boolean var1) {
      int var2 = this.read_long();
      if (var1) {
         if (var2 == -1) {
            return null;
         }

         this.stringIndirection = this.get_offset() - 4;
      }

      this.checkForNegativeLength(var2);
      return this.internalReadString(var2);
   }

   private final String internalReadString(int var1) {
      if (var1 == 0) {
         return new String("");
      } else {
         char[] var2 = this.getConvertedChars(var1 - 1, this.getCharConverter());
         this.read_octet();
         return new String(var2, 0, this.getCharConverter().getNumChars());
      }
   }

   public final String read_string() {
      return this.readStringOrIndirection(false);
   }

   public String read_wstring() {
      if (ORBUtility.isForeignORB(this.orb)) {
         throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
      } else {
         int var1 = this.read_long();
         if (var1 == 0) {
            return new String("");
         } else {
            this.checkForNegativeLength(var1);
            --var1;
            char[] var2 = new char[var1];

            for(int var3 = 0; var3 < var1; ++var3) {
               var2[var3] = this.read_wchar();
            }

            this.read_wchar();
            return new String(var2);
         }
      }
   }

   public final void read_octet_array(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw this.wrapper.nullParam();
      } else if (var3 != 0) {
         this.alignAndCheck(1, 1);

         int var6;
         for(int var4 = var2; var4 < var3 + var2; var4 += var6) {
            int var5 = this.bbwi.buflen - this.bbwi.position();
            if (var5 <= 0) {
               this.grow(1, 1);
               var5 = this.bbwi.buflen - this.bbwi.position();
            }

            int var7 = var3 + var2 - var4;
            var6 = var7 < var5 ? var7 : var5;

            for(int var8 = 0; var8 < var6; ++var8) {
               var1[var4 + var8] = this.bbwi.byteBuffer.get(this.bbwi.position() + var8);
            }

            this.bbwi.position(this.bbwi.position() + var6);
         }

      }
   }

   public Principal read_Principal() {
      int var1 = this.read_long();
      byte[] var2 = new byte[var1];
      this.read_octet_array((byte[])var2, 0, var1);
      PrincipalImpl var3 = new PrincipalImpl();
      var3.name(var2);
      return var3;
   }

   public TypeCode read_TypeCode() {
      TypeCodeImpl var1 = new TypeCodeImpl(this.orb);
      var1.read_value(this.parent);
      return var1;
   }

   public Any read_any() {
      Any var1 = this.orb.create_any();
      TypeCodeImpl var2 = new TypeCodeImpl(this.orb);

      try {
         var2.read_value(this.parent);
      } catch (MARSHAL var4) {
         if (var2.kind().value() != 29) {
            throw var4;
         }

         this.dprintThrowable(var4);
      }

      var1.read_value(this.parent, var2);
      return var1;
   }

   public Object read_Object() {
      return this.read_Object((Class)null);
   }

   public Object read_Object(Class var1) {
      IOR var2 = IORFactories.makeIOR((InputStream)this.parent);
      if (var2.isNil()) {
         return null;
      } else {
         PresentationManager.StubFactoryFactory var3 = ORB.getStubFactoryFactory();
         String var4 = var2.getProfile().getCodebase();
         PresentationManager.StubFactory var5 = null;
         if (var1 == null) {
            RepositoryId var6 = RepositoryId.cache.getId(var2.getTypeId());
            String var7 = var6.getClassName();
            this.orb.validateIORClass(var7);
            boolean var8 = var6.isIDLType();
            if (var7 != null && !var7.equals("")) {
               try {
                  var5 = var3.createStubFactory(var7, var8, var4, (Class)null, (ClassLoader)null);
               } catch (Exception var10) {
                  var5 = null;
               }
            } else {
               var5 = null;
            }
         } else if (StubAdapter.isStubClass(var1)) {
            var5 = PresentationDefaults.makeStaticStubFactory(var1);
         } else {
            boolean var11 = IDLEntity.class.isAssignableFrom(var1);
            var5 = var3.createStubFactory(var1.getName(), var11, var4, var1, var1.getClassLoader());
         }

         return internalIORToObject(var2, var5, this.orb);
      }
   }

   public static Object internalIORToObject(IOR var0, PresentationManager.StubFactory var1, ORB var2) {
      ORBUtilSystemException var3 = ORBUtilSystemException.get(var2, "rpc.encoding");
      java.lang.Object var4 = var0.getProfile().getServant();
      if (var4 != null) {
         if (var4 instanceof Tie) {
            String var9 = var0.getProfile().getCodebase();
            Object var10 = (Object)Utility.loadStub((Tie)var4, var1, var9, false);
            if (var10 != null) {
               return var10;
            }

            throw var3.readObjectException();
         }

         if (!(var4 instanceof Object)) {
            throw var3.badServantReadObject();
         }

         if (!(var4 instanceof InvokeHandler)) {
            return (Object)var4;
         }
      }

      CorbaClientDelegate var5 = ORBUtility.makeClientDelegate(var0);
      java.lang.Object var6 = null;

      try {
         var6 = var1.makeStub();
      } catch (Throwable var8) {
         var3.stubCreateError(var8);
         if (var8 instanceof ThreadDeath) {
            throw (ThreadDeath)var8;
         }

         var6 = new CORBAObjectImpl();
      }

      StubAdapter.setDelegate(var6, var5);
      return (Object)var6;
   }

   public java.lang.Object read_abstract_interface() {
      return this.read_abstract_interface((Class)null);
   }

   public java.lang.Object read_abstract_interface(Class var1) {
      boolean var2 = this.read_boolean();
      return var2 ? this.read_Object(var1) : this.read_value();
   }

   public Serializable read_value() {
      return this.read_value((Class)null);
   }

   private Serializable handleIndirection() {
      int var1 = this.read_long() + this.get_offset() - 4;
      if (this.valueCache != null && this.valueCache.containsVal(var1)) {
         Serializable var2 = (Serializable)this.valueCache.getKey(var1);
         return var2;
      } else {
         throw new IndirectionException(var1);
      }
   }

   private String readRepositoryIds(int var1, Class var2, String var3) {
      return this.readRepositoryIds(var1, var2, var3, (BoxedValueHelper)null);
   }

   private String readRepositoryIds(int var1, Class var2, String var3, BoxedValueHelper var4) {
      switch(this.repIdUtil.getTypeInfo(var1)) {
      case 0:
         if (var2 == null) {
            if (var3 != null) {
               return var3;
            }

            if (var4 != null) {
               return var4.get_id();
            }

            throw this.wrapper.expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_MAYBE);
         }

         return this.repIdStrs.createForAnyType(var2);
      case 2:
         return this.read_repositoryId();
      case 6:
         return this.read_repositoryIds();
      default:
         throw this.wrapper.badValueTag((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, Integer.toHexString(var1));
      }
   }

   public Serializable read_value(Class var1) {
      int var2 = this.readValueTag();
      if (var2 == 0) {
         return null;
      } else if (var2 == -1) {
         return this.handleIndirection();
      } else {
         int var3 = this.get_offset() - 4;
         boolean var4 = this.isChunked;
         this.isChunked = this.repIdUtil.isChunkedEncoding(var2);
         java.lang.Object var5 = null;
         String var6 = null;
         if (this.repIdUtil.isCodeBasePresent(var2)) {
            var6 = this.read_codebase_URL();
         }

         String var7 = this.readRepositoryIds(var2, var1, (String)null);
         this.start_block();
         --this.end_flag;
         if (this.isChunked) {
            --this.chunkedValueNestingLevel;
         }

         if (var7.equals(this.repIdStrs.getWStringValueRepId())) {
            var5 = this.read_wstring();
         } else if (var7.equals(this.repIdStrs.getClassDescValueRepId())) {
            var5 = this.readClass();
         } else {
            Class var8 = var1;
            if (var1 == null || !var7.equals(this.repIdStrs.createForAnyType(var1))) {
               var8 = this.getClassFromString(var7, var6, var1);
            }

            if (var8 == null) {
               throw this.wrapper.couldNotFindClass(CompletionStatus.COMPLETED_MAYBE, new ClassNotFoundException());
            }

            if (var8 != null && IDLEntity.class.isAssignableFrom(var8)) {
               var5 = this.readIDLValue(var3, var7, var8, var6);
            } else {
               try {
                  if (this.valueHandler == null) {
                     this.valueHandler = ORBUtility.createValueHandler();
                  }

                  var5 = this.valueHandler.readValue(this.parent, var3, var8, var7, this.getCodeBase());
               } catch (SystemException var10) {
                  throw var10;
               } catch (Exception var11) {
                  throw this.wrapper.valuehandlerReadException(CompletionStatus.COMPLETED_MAYBE, var11);
               } catch (Error var12) {
                  throw this.wrapper.valuehandlerReadError(CompletionStatus.COMPLETED_MAYBE, var12);
               }
            }
         }

         this.handleEndOfValue();
         this.readEndTag();
         if (this.valueCache == null) {
            this.valueCache = new CacheTable(this.orb, false);
         }

         this.valueCache.put(var5, var3);
         this.isChunked = var4;
         this.start_block();
         return (Serializable)var5;
      }
   }

   public Serializable read_value(BoxedValueHelper var1) {
      int var2 = this.readValueTag();
      if (var2 == 0) {
         return null;
      } else {
         int var3;
         if (var2 == -1) {
            var3 = this.read_long() + this.get_offset() - 4;
            if (this.valueCache != null && this.valueCache.containsVal(var3)) {
               Serializable var8 = (Serializable)this.valueCache.getKey(var3);
               return var8;
            } else {
               throw new IndirectionException(var3);
            }
         } else {
            var3 = this.get_offset() - 4;
            boolean var4 = this.isChunked;
            this.isChunked = this.repIdUtil.isChunkedEncoding(var2);
            java.lang.Object var5 = null;
            String var6 = null;
            if (this.repIdUtil.isCodeBasePresent(var2)) {
               var6 = this.read_codebase_URL();
            }

            String var7 = this.readRepositoryIds(var2, (Class)null, (String)null, var1);
            if (!var7.equals(var1.get_id())) {
               var1 = Utility.getHelper((Class)null, var6, var7);
            }

            this.start_block();
            --this.end_flag;
            if (this.isChunked) {
               --this.chunkedValueNestingLevel;
            }

            if (var1 instanceof ValueHelper) {
               var5 = this.readIDLValueWithHelper((ValueHelper)var1, var3);
            } else {
               this.valueIndirection = var3;
               var5 = var1.read_value(this.parent);
            }

            this.handleEndOfValue();
            this.readEndTag();
            if (this.valueCache == null) {
               this.valueCache = new CacheTable(this.orb, false);
            }

            this.valueCache.put(var5, var3);
            this.isChunked = var4;
            this.start_block();
            return (Serializable)var5;
         }
      }
   }

   private boolean isCustomType(ValueHelper var1) {
      try {
         TypeCode var2 = var1.get_type();
         int var3 = var2.kind().value();
         if (var3 == 29) {
            return var2.type_modifier() == 1;
         } else {
            return false;
         }
      } catch (BadKind var4) {
         throw this.wrapper.badKind((Throwable)var4);
      }
   }

   public Serializable read_value(Serializable var1) {
      if (this.valueCache == null) {
         this.valueCache = new CacheTable(this.orb, false);
      }

      this.valueCache.put(var1, this.valueIndirection);
      if (var1 instanceof StreamableValue) {
         ((StreamableValue)var1)._read(this.parent);
      } else if (var1 instanceof CustomValue) {
         ((CustomValue)var1).unmarshal(this.parent);
      }

      return var1;
   }

   public Serializable read_value(String var1) {
      int var2 = this.readValueTag();
      if (var2 == 0) {
         return null;
      } else {
         int var3;
         if (var2 == -1) {
            var3 = this.read_long() + this.get_offset() - 4;
            if (this.valueCache != null && this.valueCache.containsVal(var3)) {
               Serializable var9 = (Serializable)this.valueCache.getKey(var3);
               return var9;
            } else {
               throw new IndirectionException(var3);
            }
         } else {
            var3 = this.get_offset() - 4;
            boolean var4 = this.isChunked;
            this.isChunked = this.repIdUtil.isChunkedEncoding(var2);
            Serializable var5 = null;
            String var6 = null;
            if (this.repIdUtil.isCodeBasePresent(var2)) {
               var6 = this.read_codebase_URL();
            }

            String var7 = this.readRepositoryIds(var2, (Class)null, var1);
            ValueFactory var8 = Utility.getFactory((Class)null, var6, this.orb, var7);
            this.start_block();
            --this.end_flag;
            if (this.isChunked) {
               --this.chunkedValueNestingLevel;
            }

            this.valueIndirection = var3;
            var5 = var8.read_value(this.parent);
            this.handleEndOfValue();
            this.readEndTag();
            if (this.valueCache == null) {
               this.valueCache = new CacheTable(this.orb, false);
            }

            this.valueCache.put(var5, var3);
            this.isChunked = var4;
            this.start_block();
            return (Serializable)var5;
         }
      }
   }

   private Class readClass() {
      String var1 = null;
      String var2 = null;
      if (this.orb != null && !ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) && ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) > 0) {
         var2 = (String)this.read_value(String.class);
         var1 = (String)this.read_value(String.class);
      } else {
         var1 = (String)this.read_value(String.class);
         var2 = (String)this.read_value(String.class);
      }

      if (this.debug) {
         this.dprint("readClass codebases: " + var1 + " rep Id: " + var2);
      }

      Class var3 = null;
      RepositoryIdInterface var4 = this.repIdStrs.getFromString(var2);

      try {
         var3 = var4.getClassFromType(var1);
         return var3;
      } catch (ClassNotFoundException var6) {
         throw this.wrapper.cnfeReadClass(CompletionStatus.COMPLETED_MAYBE, var6, var4.getClassName());
      } catch (MalformedURLException var7) {
         throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, var7, var4.getClassName(), var1);
      }
   }

   private java.lang.Object readIDLValueWithHelper(ValueHelper var1, int var2) {
      Method var3;
      Class[] var4;
      try {
         var4 = new Class[]{org.omg.CORBA.portable.InputStream.class, var1.get_class()};
         var3 = var1.getClass().getDeclaredMethod("read", var4);
      } catch (NoSuchMethodException var10) {
         Serializable var5 = var1.read_value(this.parent);
         return var5;
      }

      var4 = null;

      java.lang.Object var11;
      try {
         var11 = var1.get_class().newInstance();
      } catch (InstantiationException var8) {
         throw this.wrapper.couldNotInstantiateHelper((Throwable)var8, var1.get_class());
      } catch (IllegalAccessException var9) {
         return var1.read_value(this.parent);
      }

      if (this.valueCache == null) {
         this.valueCache = new CacheTable(this.orb, false);
      }

      this.valueCache.put(var11, var2);
      if (var11 instanceof CustomMarshal && this.isCustomType(var1)) {
         ((CustomMarshal)var11).unmarshal(this.parent);
         return var11;
      } else {
         try {
            java.lang.Object[] var12 = new java.lang.Object[]{this.parent, var11};
            var3.invoke(var1, var12);
            return var11;
         } catch (IllegalAccessException var6) {
            throw this.wrapper.couldNotInvokeHelperReadMethod((Throwable)var6, var1.get_class());
         } catch (InvocationTargetException var7) {
            throw this.wrapper.couldNotInvokeHelperReadMethod((Throwable)var7, var1.get_class());
         }
      }
   }

   private java.lang.Object readBoxedIDLEntity(Class var1, String var2) {
      Class var3 = null;

      try {
         ClassLoader var4 = var1 == null ? null : var1.getClassLoader();
         var3 = Utility.loadClassForClass(var1.getName() + "Helper", var2, var4, var1, var4);
         final Class var5 = var3;
         final Class[] var6 = new Class[]{org.omg.CORBA.portable.InputStream.class};
         Method var7 = null;

         try {
            var7 = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public java.lang.Object run() throws NoSuchMethodException {
                  return var5.getDeclaredMethod("read", var6);
               }
            });
         } catch (PrivilegedActionException var9) {
            throw (NoSuchMethodException)var9.getException();
         }

         java.lang.Object[] var8 = new java.lang.Object[]{this.parent};
         return var7.invoke((java.lang.Object)null, var8);
      } catch (ClassNotFoundException var10) {
         throw this.wrapper.couldNotInvokeHelperReadMethod((Throwable)var10, var3);
      } catch (NoSuchMethodException var11) {
         throw this.wrapper.couldNotInvokeHelperReadMethod((Throwable)var11, var3);
      } catch (IllegalAccessException var12) {
         throw this.wrapper.couldNotInvokeHelperReadMethod((Throwable)var12, var3);
      } catch (InvocationTargetException var13) {
         throw this.wrapper.couldNotInvokeHelperReadMethod((Throwable)var13, var3);
      }
   }

   private java.lang.Object readIDLValue(int var1, String var2, Class var3, String var4) {
      ValueFactory var5;
      try {
         var5 = Utility.getFactory(var3, var4, this.orb, var2);
      } catch (MARSHAL var8) {
         if (!StreamableValue.class.isAssignableFrom(var3) && !CustomValue.class.isAssignableFrom(var3) && ValueBase.class.isAssignableFrom(var3)) {
            BoxedValueHelper var7 = Utility.getHelper(var3, var4, var2);
            if (var7 instanceof ValueHelper) {
               return this.readIDLValueWithHelper((ValueHelper)var7, var1);
            }

            return var7.read_value(this.parent);
         }

         return this.readBoxedIDLEntity(var3, var4);
      }

      this.valueIndirection = var1;
      return var5.read_value(this.parent);
   }

   private void readEndTag() {
      if (this.isChunked) {
         int var1 = this.read_long();
         if (var1 >= 0) {
            throw this.wrapper.positiveEndTag((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(var1), new Integer(this.get_offset() - 4));
         }

         if (this.orb != null && !ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) && ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) > 0) {
            if (var1 != this.end_flag) {
               this.bbwi.position(this.bbwi.position() - 4);
            }
         } else {
            if (var1 < this.chunkedValueNestingLevel) {
               throw this.wrapper.unexpectedEnclosingValuetype((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(var1), new Integer(this.chunkedValueNestingLevel));
            }

            if (var1 != this.chunkedValueNestingLevel) {
               this.bbwi.position(this.bbwi.position() - 4);
            }
         }

         ++this.chunkedValueNestingLevel;
      }

      ++this.end_flag;
   }

   protected int get_offset() {
      return this.bbwi.position();
   }

   private void start_block() {
      if (this.isChunked) {
         this.blockLength = 2147483392;
         this.blockLength = this.read_long();
         if (this.blockLength > 0 && this.blockLength < 2147483392) {
            this.blockLength += this.get_offset();
         } else {
            this.blockLength = 2147483392;
            this.bbwi.position(this.bbwi.position() - 4);
         }

      }
   }

   private void handleEndOfValue() {
      if (this.isChunked) {
         while(this.blockLength != 2147483392) {
            this.end_block();
            this.start_block();
         }

         int var1 = this.read_long();
         this.bbwi.position(this.bbwi.position() - 4);
         if (var1 >= 0) {
            if (var1 != 0 && var1 < 2147483392) {
               throw this.wrapper.couldNotSkipBytes((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(var1), new Integer(this.get_offset()));
            } else {
               this.read_value();
               this.handleEndOfValue();
            }
         }
      }
   }

   private void end_block() {
      if (this.blockLength != 2147483392) {
         if (this.blockLength == this.get_offset()) {
            this.blockLength = 2147483392;
         } else {
            if (this.blockLength <= this.get_offset()) {
               throw this.wrapper.badChunkLength(new Integer(this.blockLength), new Integer(this.get_offset()));
            }

            this.skipToOffset(this.blockLength);
         }
      }

   }

   private int readValueTag() {
      return this.read_long();
   }

   public org.omg.CORBA.ORB orb() {
      return this.orb;
   }

   public final void read_boolean_array(boolean[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_boolean();
      }

   }

   public final void read_char_array(char[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_char();
      }

   }

   public final void read_wchar_array(char[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_wchar();
      }

   }

   public final void read_short_array(short[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_short();
      }

   }

   public final void read_ushort_array(short[] var1, int var2, int var3) {
      this.read_short_array(var1, var2, var3);
   }

   public final void read_long_array(int[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_long();
      }

   }

   public final void read_ulong_array(int[] var1, int var2, int var3) {
      this.read_long_array(var1, var2, var3);
   }

   public final void read_longlong_array(long[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_longlong();
      }

   }

   public final void read_ulonglong_array(long[] var1, int var2, int var3) {
      this.read_longlong_array(var1, var2, var3);
   }

   public final void read_float_array(float[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_float();
      }

   }

   public final void read_double_array(double[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_double();
      }

   }

   public final void read_any_array(Any[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_any();
      }

   }

   private String read_repositoryIds() {
      int var1 = this.read_long();
      int var2;
      if (var1 == -1) {
         var2 = this.read_long() + this.get_offset() - 4;
         if (this.repositoryIdCache != null && this.repositoryIdCache.containsOrderedVal(var2)) {
            return (String)this.repositoryIdCache.getKey(var2);
         } else {
            throw this.wrapper.unableToLocateRepIdArray(new Integer(var2));
         }
      } else {
         var2 = this.get_offset();
         String var3 = this.read_repositoryId();
         if (this.repositoryIdCache == null) {
            this.repositoryIdCache = new CacheTable(this.orb, false);
         }

         this.repositoryIdCache.put(var3, var2);

         for(int var4 = 1; var4 < var1; ++var4) {
            this.read_repositoryId();
         }

         return var3;
      }
   }

   private final String read_repositoryId() {
      String var1 = this.readStringOrIndirection(true);
      if (var1 == null) {
         int var2 = this.read_long() + this.get_offset() - 4;
         if (this.repositoryIdCache != null && this.repositoryIdCache.containsOrderedVal(var2)) {
            return (String)this.repositoryIdCache.getKey(var2);
         } else {
            throw this.wrapper.badRepIdIndirection((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(this.bbwi.position()));
         }
      } else {
         if (this.repositoryIdCache == null) {
            this.repositoryIdCache = new CacheTable(this.orb, false);
         }

         this.repositoryIdCache.put(var1, this.stringIndirection);
         return var1;
      }
   }

   private final String read_codebase_URL() {
      String var1 = this.readStringOrIndirection(true);
      if (var1 == null) {
         int var2 = this.read_long() + this.get_offset() - 4;
         if (this.codebaseCache != null && this.codebaseCache.containsVal(var2)) {
            return (String)this.codebaseCache.getKey(var2);
         } else {
            throw this.wrapper.badCodebaseIndirection((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(this.bbwi.position()));
         }
      } else {
         if (this.codebaseCache == null) {
            this.codebaseCache = new CacheTable(this.orb, false);
         }

         this.codebaseCache.put(var1, this.stringIndirection);
         return var1;
      }
   }

   public java.lang.Object read_Abstract() {
      return this.read_abstract_interface();
   }

   public Serializable read_Value() {
      return this.read_value();
   }

   public void read_any_array(AnySeqHolder var1, int var2, int var3) {
      this.read_any_array(var1.value, var2, var3);
   }

   public void read_boolean_array(BooleanSeqHolder var1, int var2, int var3) {
      this.read_boolean_array(var1.value, var2, var3);
   }

   public void read_char_array(CharSeqHolder var1, int var2, int var3) {
      this.read_char_array(var1.value, var2, var3);
   }

   public void read_wchar_array(WCharSeqHolder var1, int var2, int var3) {
      this.read_wchar_array(var1.value, var2, var3);
   }

   public void read_octet_array(OctetSeqHolder var1, int var2, int var3) {
      this.read_octet_array(var1.value, var2, var3);
   }

   public void read_short_array(ShortSeqHolder var1, int var2, int var3) {
      this.read_short_array(var1.value, var2, var3);
   }

   public void read_ushort_array(UShortSeqHolder var1, int var2, int var3) {
      this.read_ushort_array(var1.value, var2, var3);
   }

   public void read_long_array(LongSeqHolder var1, int var2, int var3) {
      this.read_long_array(var1.value, var2, var3);
   }

   public void read_ulong_array(ULongSeqHolder var1, int var2, int var3) {
      this.read_ulong_array(var1.value, var2, var3);
   }

   public void read_ulonglong_array(ULongLongSeqHolder var1, int var2, int var3) {
      this.read_ulonglong_array(var1.value, var2, var3);
   }

   public void read_longlong_array(LongLongSeqHolder var1, int var2, int var3) {
      this.read_longlong_array(var1.value, var2, var3);
   }

   public void read_float_array(FloatSeqHolder var1, int var2, int var3) {
      this.read_float_array(var1.value, var2, var3);
   }

   public void read_double_array(DoubleSeqHolder var1, int var2, int var3) {
      this.read_double_array(var1.value, var2, var3);
   }

   public BigDecimal read_fixed(short var1, short var2) {
      StringBuffer var3 = this.read_fixed_buffer();
      if (var1 != var3.length()) {
         throw this.wrapper.badFixed(new Integer(var1), new Integer(var3.length()));
      } else {
         var3.insert(var1 - var2, '.');
         return new BigDecimal(var3.toString());
      }
   }

   public BigDecimal read_fixed() {
      return new BigDecimal(this.read_fixed_buffer().toString());
   }

   private StringBuffer read_fixed_buffer() {
      StringBuffer var1 = new StringBuffer(64);
      boolean var5 = false;
      boolean var6 = true;

      while(var6) {
         byte var2 = this.read_octet();
         int var3 = (var2 & 240) >> 4;
         int var4 = var2 & 15;
         if (var5 || var3 != 0) {
            var1.append(Character.forDigit(var3, 10));
            var5 = true;
         }

         if (var4 == 12) {
            if (!var5) {
               return new StringBuffer("0.0");
            }

            var6 = false;
         } else if (var4 == 13) {
            var1.insert(0, (char)'-');
            var6 = false;
         } else {
            var1.append(Character.forDigit(var4, 10));
            var5 = true;
         }
      }

      return var1;
   }

   public String[] _truncatable_ids() {
      return _ids == null ? null : (String[])((String[])_ids.clone());
   }

   public void printBuffer() {
      printBuffer(this.bbwi);
   }

   public static void printBuffer(ByteBufferWithInfo var0) {
      System.out.println("----- Input Buffer -----");
      System.out.println();
      System.out.println("Current position: " + var0.position());
      System.out.println("Total length : " + var0.buflen);
      System.out.println();

      try {
         char[] var1 = new char[16];

         for(int var2 = 0; var2 < var0.buflen; var2 += 16) {
            int var3;
            int var4;
            for(var3 = 0; var3 < 16 && var3 + var2 < var0.buflen; ++var3) {
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

            for(var4 = 0; var4 < 16 && var4 + var2 < var0.buflen; ++var4) {
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

      System.out.println("------------------------");
   }

   public ByteBuffer getByteBuffer() {
      ByteBuffer var1 = null;
      if (this.bbwi != null) {
         var1 = this.bbwi.byteBuffer;
      }

      return var1;
   }

   public int getBufferLength() {
      return this.bbwi.buflen;
   }

   public void setBufferLength(int var1) {
      this.bbwi.buflen = var1;
      this.bbwi.byteBuffer.limit(this.bbwi.buflen);
   }

   public void setByteBufferWithInfo(ByteBufferWithInfo var1) {
      this.bbwi = var1;
   }

   public void setByteBuffer(ByteBuffer var1) {
      this.bbwi.byteBuffer = var1;
   }

   public int getIndex() {
      return this.bbwi.position();
   }

   public void setIndex(int var1) {
      this.bbwi.position(var1);
   }

   public boolean isLittleEndian() {
      return this.littleEndian;
   }

   public void orb(org.omg.CORBA.ORB var1) {
      this.orb = (ORB)var1;
   }

   public BufferManagerRead getBufferManager() {
      return this.bufferManagerRead;
   }

   private void skipToOffset(int var1) {
      int var2 = var1 - this.get_offset();

      int var5;
      for(int var3 = 0; var3 < var2; var3 += var5) {
         int var4 = this.bbwi.buflen - this.bbwi.position();
         if (var4 <= 0) {
            this.grow(1, 1);
            var4 = this.bbwi.buflen - this.bbwi.position();
         }

         int var6 = var2 - var3;
         var5 = var6 < var4 ? var6 : var4;
         this.bbwi.position(this.bbwi.position() + var5);
      }

   }

   public java.lang.Object createStreamMemento() {
      return new CDRInputStream_1_0.StreamMemento();
   }

   public void restoreInternalState(java.lang.Object var1) {
      CDRInputStream_1_0.StreamMemento var2 = (CDRInputStream_1_0.StreamMemento)var1;
      this.blockLength = var2.blockLength_;
      this.end_flag = var2.end_flag_;
      this.chunkedValueNestingLevel = var2.chunkedValueNestingLevel_;
      this.valueIndirection = var2.valueIndirection_;
      this.stringIndirection = var2.stringIndirection_;
      this.isChunked = var2.isChunked_;
      this.valueHandler = var2.valueHandler_;
      this.specialNoOptionalDataState = var2.specialNoOptionalDataState_;
      this.bbwi = var2.bbwi_;
   }

   public int getPosition() {
      return this.get_offset();
   }

   public void mark(int var1) {
      this.markAndResetHandler.mark(this);
   }

   public void reset() {
      this.markAndResetHandler.reset();
   }

   CodeBase getCodeBase() {
      return this.parent.getCodeBase();
   }

   private Class getClassFromString(String var1, String var2, Class var3) {
      RepositoryIdInterface var4 = this.repIdStrs.getFromString(var1);

      try {
         try {
            return var4.getClassFromType(var3, var2);
         } catch (ClassNotFoundException var8) {
            try {
               if (this.getCodeBase() == null) {
                  return null;
               } else {
                  var2 = this.getCodeBase().implementation(var1);
                  return var2 == null ? null : var4.getClassFromType(var3, var2);
               }
            } catch (ClassNotFoundException var7) {
               this.dprintThrowable(var7);
               return null;
            }
         }
      } catch (MalformedURLException var9) {
         throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, var9, var1, var2);
      }
   }

   private Class getClassFromString(String var1, String var2) {
      RepositoryIdInterface var3 = this.repIdStrs.getFromString(var1);

      for(int var4 = 0; var4 < 3; ++var4) {
         try {
            switch(var4) {
            case 0:
               return var3.getClassFromType();
            case 2:
               var2 = this.getCodeBase().implementation(var1);
            case 1:
            default:
               if (var2 != null) {
                  return var3.getClassFromType(var2);
               }
            }
         } catch (ClassNotFoundException var6) {
         } catch (MalformedURLException var7) {
            throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, var7, var1, var2);
         }
      }

      this.dprint("getClassFromString failed with rep id " + var1 + " and codebase " + var2);
      return null;
   }

   char[] getConvertedChars(int var1, CodeSetConversion.BTCConverter var2) {
      byte[] var3;
      if (this.bbwi.buflen - this.bbwi.position() < var1) {
         var3 = new byte[var1];
         this.read_octet_array((byte[])var3, 0, var3.length);
         return var2.getChars(var3, 0, var1);
      } else {
         if (this.bbwi.byteBuffer.hasArray()) {
            var3 = this.bbwi.byteBuffer.array();
         } else {
            var3 = new byte[this.bbwi.buflen];

            for(int var4 = 0; var4 < this.bbwi.buflen; ++var4) {
               var3[var4] = this.bbwi.byteBuffer.get(var4);
            }
         }

         char[] var5 = var2.getChars(var3, this.bbwi.position(), var1);
         this.bbwi.position(this.bbwi.position() + var1);
         return var5;
      }
   }

   protected CodeSetConversion.BTCConverter getCharConverter() {
      if (this.charConverter == null) {
         this.charConverter = this.parent.createCharBTCConverter();
      }

      return this.charConverter;
   }

   protected CodeSetConversion.BTCConverter getWCharConverter() {
      if (this.wcharConverter == null) {
         this.wcharConverter = this.parent.createWCharBTCConverter();
      }

      return this.wcharConverter;
   }

   protected void dprintThrowable(Throwable var1) {
      if (this.debug && var1 != null) {
         var1.printStackTrace();
      }

   }

   protected void dprint(String var1) {
      if (this.debug) {
         ORBUtility.dprint((java.lang.Object)this, var1);
      }

   }

   void alignOnBoundary(int var1) {
      int var2 = this.computeAlignment(this.bbwi.position(), var1);
      if (this.bbwi.position() + var2 <= this.bbwi.buflen) {
         this.bbwi.position(this.bbwi.position() + var2);
      }

   }

   public void resetCodeSetConverters() {
      this.charConverter = null;
      this.wcharConverter = null;
   }

   public void start_value() {
      int var1 = this.readValueTag();
      if (var1 == 0) {
         this.specialNoOptionalDataState = true;
      } else if (var1 == -1) {
         throw this.wrapper.customWrapperIndirection(CompletionStatus.COMPLETED_MAYBE);
      } else if (this.repIdUtil.isCodeBasePresent(var1)) {
         throw this.wrapper.customWrapperWithCodebase(CompletionStatus.COMPLETED_MAYBE);
      } else if (this.repIdUtil.getTypeInfo(var1) != 2) {
         throw this.wrapper.customWrapperNotSingleRepid(CompletionStatus.COMPLETED_MAYBE);
      } else {
         this.read_repositoryId();
         this.start_block();
         --this.end_flag;
         --this.chunkedValueNestingLevel;
      }
   }

   public void end_value() {
      if (this.specialNoOptionalDataState) {
         this.specialNoOptionalDataState = false;
      } else {
         this.handleEndOfValue();
         this.readEndTag();
         this.start_block();
      }
   }

   public void close() throws IOException {
      this.getBufferManager().close(this.bbwi);
      if (this.bbwi != null && this.getByteBuffer() != null) {
         MessageMediator var1 = this.parent.getMessageMediator();
         if (var1 != null) {
            CDROutputObject var2 = (CDROutputObject)var1.getOutputObject();
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

         var6.releaseByteBuffer(this.bbwi.byteBuffer);
         this.bbwi.byteBuffer = null;
         this.bbwi = null;
      }

   }

   protected class StreamMemento {
      private int blockLength_;
      private int end_flag_;
      private int chunkedValueNestingLevel_;
      private int valueIndirection_;
      private int stringIndirection_;
      private boolean isChunked_;
      private ValueHandler valueHandler_;
      private ByteBufferWithInfo bbwi_;
      private boolean specialNoOptionalDataState_;

      public StreamMemento() {
         this.blockLength_ = CDRInputStream_1_0.this.blockLength;
         this.end_flag_ = CDRInputStream_1_0.this.end_flag;
         this.chunkedValueNestingLevel_ = CDRInputStream_1_0.this.chunkedValueNestingLevel;
         this.valueIndirection_ = CDRInputStream_1_0.this.valueIndirection;
         this.stringIndirection_ = CDRInputStream_1_0.this.stringIndirection;
         this.isChunked_ = CDRInputStream_1_0.this.isChunked;
         this.valueHandler_ = CDRInputStream_1_0.this.valueHandler;
         this.specialNoOptionalDataState_ = CDRInputStream_1_0.this.specialNoOptionalDataState;
         this.bbwi_ = new ByteBufferWithInfo(CDRInputStream_1_0.this.bbwi);
      }
   }
}
