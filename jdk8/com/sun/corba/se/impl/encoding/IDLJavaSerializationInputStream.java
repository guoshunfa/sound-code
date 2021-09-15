package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.PrincipalImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.MARSHAL;
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
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA_2_3.portable.InputStream;

public class IDLJavaSerializationInputStream extends CDRInputStreamBase {
   private ORB orb;
   private int bufSize;
   private ByteBuffer buffer;
   private byte encodingVersion;
   private ObjectInputStream is;
   private IDLJavaSerializationInputStream._ByteArrayInputStream bis;
   private BufferManagerRead bufferManager;
   private final int directReadLength = 16;
   private boolean markOn;
   private int peekIndex;
   private int peekCount;
   private LinkedList markedItemQ = new LinkedList();
   protected ORBUtilSystemException wrapper;

   public IDLJavaSerializationInputStream(byte var1) {
      this.encodingVersion = var1;
   }

   public void init(org.omg.CORBA.ORB var1, ByteBuffer var2, int var3, boolean var4, BufferManagerRead var5) {
      this.orb = (ORB)var1;
      this.bufSize = var3;
      this.bufferManager = var5;
      this.buffer = var2;
      this.wrapper = ORBUtilSystemException.get((ORB)var1, "rpc.encoding");
      byte[] var6;
      if (this.buffer.hasArray()) {
         var6 = this.buffer.array();
      } else {
         var6 = new byte[var3];
         this.buffer.get(var6);
      }

      this.bis = new IDLJavaSerializationInputStream._ByteArrayInputStream(var6);
   }

   private void initObjectInputStream() {
      if (this.is != null) {
         throw this.wrapper.javaStreamInitFailed();
      } else {
         try {
            this.is = new IDLJavaSerializationInputStream.MarshalObjectInputStream(this.bis, this.orb);
         } catch (Exception var2) {
            throw this.wrapper.javaStreamInitFailed((Throwable)var2);
         }
      }
   }

   public boolean read_boolean() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Boolean)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Boolean)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            boolean var1 = this.is.readBoolean();
            if (this.markOn) {
               this.markedItemQ.addLast(var1);
            }

            return var1;
         } catch (Exception var2) {
            throw this.wrapper.javaSerializationException((Throwable)var2, "read_boolean");
         }
      }
   }

   public char read_char() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Character)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Character)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            char var1 = this.is.readChar();
            if (this.markOn) {
               this.markedItemQ.addLast(new Character(var1));
            }

            return var1;
         } catch (Exception var2) {
            throw this.wrapper.javaSerializationException((Throwable)var2, "read_char");
         }
      }
   }

   public char read_wchar() {
      return this.read_char();
   }

   public byte read_octet() {
      byte var1;
      if (this.bis.getPosition() < 16) {
         var1 = (byte)this.bis.read();
         if (this.bis.getPosition() == 16) {
            this.initObjectInputStream();
         }

         return var1;
      } else if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Byte)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Byte)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            var1 = this.is.readByte();
            if (this.markOn) {
               this.markedItemQ.addLast(new Byte(var1));
            }

            return var1;
         } catch (Exception var2) {
            throw this.wrapper.javaSerializationException((Throwable)var2, "read_octet");
         }
      }
   }

   public short read_short() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Short)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Short)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            short var1 = this.is.readShort();
            if (this.markOn) {
               this.markedItemQ.addLast(new Short(var1));
            }

            return var1;
         } catch (Exception var2) {
            throw this.wrapper.javaSerializationException((Throwable)var2, "read_short");
         }
      }
   }

   public short read_ushort() {
      return this.read_short();
   }

   public int read_long() {
      int var1;
      if (this.bis.getPosition() < 16) {
         var1 = this.bis.read() << 24 & -16777216;
         int var2 = this.bis.read() << 16 & 16711680;
         int var3 = this.bis.read() << 8 & '\uff00';
         int var4 = this.bis.read() << 0 & 255;
         if (this.bis.getPosition() == 16) {
            this.initObjectInputStream();
         } else if (this.bis.getPosition() > 16) {
            this.wrapper.javaSerializationException("read_long");
         }

         return var1 | var2 | var3 | var4;
      } else if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Integer)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Integer)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            var1 = this.is.readInt();
            if (this.markOn) {
               this.markedItemQ.addLast(new Integer(var1));
            }

            return var1;
         } catch (Exception var5) {
            throw this.wrapper.javaSerializationException((Throwable)var5, "read_long");
         }
      }
   }

   public int read_ulong() {
      return this.read_long();
   }

   public long read_longlong() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Long)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Long)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            long var1 = this.is.readLong();
            if (this.markOn) {
               this.markedItemQ.addLast(new Long(var1));
            }

            return var1;
         } catch (Exception var3) {
            throw this.wrapper.javaSerializationException((Throwable)var3, "read_longlong");
         }
      }
   }

   public long read_ulonglong() {
      return this.read_longlong();
   }

   public float read_float() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Float)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Float)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            float var1 = this.is.readFloat();
            if (this.markOn) {
               this.markedItemQ.addLast(new Float(var1));
            }

            return var1;
         } catch (Exception var2) {
            throw this.wrapper.javaSerializationException((Throwable)var2, "read_float");
         }
      }
   }

   public double read_double() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Double)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Double)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            double var1 = this.is.readDouble();
            if (this.markOn) {
               this.markedItemQ.addLast(new Double(var1));
            }

            return var1;
         } catch (Exception var3) {
            throw this.wrapper.javaSerializationException((Throwable)var3, "read_double");
         }
      }
   }

   public String read_string() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (String)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (String)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            String var1 = this.is.readUTF();
            if (this.markOn) {
               this.markedItemQ.addLast(var1);
            }

            return var1;
         } catch (Exception var2) {
            throw this.wrapper.javaSerializationException((Throwable)var2, "read_string");
         }
      }
   }

   public String read_wstring() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (String)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (String)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            String var1 = (String)this.is.readObject();
            if (this.markOn) {
               this.markedItemQ.addLast(var1);
            }

            return var1;
         } catch (Exception var2) {
            throw this.wrapper.javaSerializationException((Throwable)var2, "read_wstring");
         }
      }
   }

   public void read_boolean_array(boolean[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_boolean();
      }

   }

   public void read_char_array(char[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_char();
      }

   }

   public void read_wchar_array(char[] var1, int var2, int var3) {
      this.read_char_array(var1, var2, var3);
   }

   public void read_octet_array(byte[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_octet();
      }

   }

   public void read_short_array(short[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_short();
      }

   }

   public void read_ushort_array(short[] var1, int var2, int var3) {
      this.read_short_array(var1, var2, var3);
   }

   public void read_long_array(int[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_long();
      }

   }

   public void read_ulong_array(int[] var1, int var2, int var3) {
      this.read_long_array(var1, var2, var3);
   }

   public void read_longlong_array(long[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_longlong();
      }

   }

   public void read_ulonglong_array(long[] var1, int var2, int var3) {
      this.read_longlong_array(var1, var2, var3);
   }

   public void read_float_array(float[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_float();
      }

   }

   public void read_double_array(double[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_double();
      }

   }

   public Object read_Object() {
      return this.read_Object((Class)null);
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

         var4.printStackTrace();
      }

      var1.read_value(this.parent, var2);
      return var1;
   }

   public Principal read_Principal() {
      int var1 = this.read_long();
      byte[] var2 = new byte[var1];
      this.read_octet_array((byte[])var2, 0, var1);
      PrincipalImpl var3 = new PrincipalImpl();
      var3.name(var2);
      return var3;
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

         return CDRInputStream_1_0.internalIORToObject(var2, var5, this.orb);
      }
   }

   public org.omg.CORBA.ORB orb() {
      return this.orb;
   }

   public Serializable read_value() {
      if (!this.markOn && !this.markedItemQ.isEmpty()) {
         return (Serializable)this.markedItemQ.removeFirst();
      } else if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
         return (Serializable)this.markedItemQ.get(this.peekIndex++);
      } else {
         try {
            Serializable var1 = (Serializable)this.is.readObject();
            if (this.markOn) {
               this.markedItemQ.addLast(var1);
            }

            return var1;
         } catch (Exception var2) {
            throw this.wrapper.javaSerializationException((Throwable)var2, "read_value");
         }
      }
   }

   public Serializable read_value(Class var1) {
      return this.read_value();
   }

   public Serializable read_value(BoxedValueHelper var1) {
      return this.read_value();
   }

   public Serializable read_value(String var1) {
      return this.read_value();
   }

   public Serializable read_value(Serializable var1) {
      return this.read_value();
   }

   public java.lang.Object read_abstract_interface() {
      return this.read_abstract_interface((Class)null);
   }

   public java.lang.Object read_abstract_interface(Class var1) {
      boolean var2 = this.read_boolean();
      return var2 ? this.read_Object(var1) : this.read_value();
   }

   public void consumeEndian() {
      throw this.wrapper.giopVersionError();
   }

   public int getPosition() {
      try {
         return this.bis.getPosition();
      } catch (Exception var2) {
         throw this.wrapper.javaSerializationException((Throwable)var2, "getPosition");
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

   private final void read_any_array(Any[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4 + var2] = this.read_any();
      }

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

   public String[] _truncatable_ids() {
      throw this.wrapper.giopVersionError();
   }

   public void mark(int var1) {
      if (!this.markOn && this.is != null) {
         this.markOn = true;
         if (!this.markedItemQ.isEmpty()) {
            this.peekIndex = 0;
            this.peekCount = this.markedItemQ.size();
         }

      } else {
         throw this.wrapper.javaSerializationException("mark");
      }
   }

   public void reset() {
      this.markOn = false;
      this.peekIndex = 0;
      this.peekCount = 0;
   }

   public boolean markSupported() {
      return true;
   }

   public CDRInputStreamBase dup() {
      CDRInputStreamBase var1 = null;

      try {
         var1 = (CDRInputStreamBase)this.getClass().newInstance();
      } catch (Exception var3) {
         throw this.wrapper.couldNotDuplicateCdrInputStream((Throwable)var3);
      }

      var1.init(this.orb, this.buffer, this.bufSize, false, (BufferManagerRead)null);
      ((IDLJavaSerializationInputStream)var1).skipBytes(this.getPosition());
      ((IDLJavaSerializationInputStream)var1).setMarkData(this.markOn, this.peekIndex, this.peekCount, (LinkedList)this.markedItemQ.clone());
      return var1;
   }

   void skipBytes(int var1) {
      try {
         this.is.skipBytes(var1);
      } catch (Exception var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "skipBytes");
      }
   }

   void setMarkData(boolean var1, int var2, int var3, LinkedList var4) {
      this.markOn = var1;
      this.peekIndex = var2;
      this.peekCount = var3;
      this.markedItemQ = var4;
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

   public boolean isLittleEndian() {
      throw this.wrapper.giopVersionError();
   }

   void setHeaderPadding(boolean var1) {
   }

   public ByteBuffer getByteBuffer() {
      throw this.wrapper.giopVersionError();
   }

   public void setByteBuffer(ByteBuffer var1) {
      throw this.wrapper.giopVersionError();
   }

   public void setByteBufferWithInfo(ByteBufferWithInfo var1) {
      throw this.wrapper.giopVersionError();
   }

   public int getBufferLength() {
      return this.bufSize;
   }

   public void setBufferLength(int var1) {
   }

   public int getIndex() {
      return this.bis.getPosition();
   }

   public void setIndex(int var1) {
      try {
         this.bis.setPosition(var1);
      } catch (IndexOutOfBoundsException var3) {
         throw this.wrapper.javaSerializationException((Throwable)var3, "setIndex");
      }
   }

   public void orb(org.omg.CORBA.ORB var1) {
      this.orb = (ORB)var1;
   }

   public BufferManagerRead getBufferManager() {
      return this.bufferManager;
   }

   public GIOPVersion getGIOPVersion() {
      return GIOPVersion.V1_2;
   }

   CodeBase getCodeBase() {
      return this.parent.getCodeBase();
   }

   void printBuffer() {
      byte[] var1 = this.buffer.array();
      System.out.println("+++++++ Input Buffer ++++++++");
      System.out.println();
      System.out.println("Current position: " + this.getPosition());
      System.out.println("Total length : " + this.bufSize);
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

   void alignOnBoundary(int var1) {
      throw this.wrapper.giopVersionError();
   }

   void performORBVersionSpecificInit() {
   }

   public void resetCodeSetConverters() {
   }

   public void start_value() {
      throw this.wrapper.giopVersionError();
   }

   public void end_value() {
      throw this.wrapper.giopVersionError();
   }

   class MarshalObjectInputStream extends ObjectInputStream {
      ORB orb;

      MarshalObjectInputStream(java.io.InputStream var2, ORB var3) throws IOException {
         super(var2);
         this.orb = var3;
         AccessController.doPrivileged(new PrivilegedAction() {
            public java.lang.Object run() {
               MarshalObjectInputStream.this.enableResolveObject(true);
               return null;
            }
         });
      }

      protected final java.lang.Object resolveObject(java.lang.Object var1) throws IOException {
         try {
            if (StubAdapter.isStub(var1)) {
               StubAdapter.connect(var1, this.orb);
            }

            return var1;
         } catch (RemoteException var4) {
            IOException var3 = new IOException("resolveObject failed");
            var3.initCause(var4);
            throw var3;
         }
      }
   }

   class _ByteArrayInputStream extends ByteArrayInputStream {
      _ByteArrayInputStream(byte[] var2) {
         super(var2);
      }

      int getPosition() {
         return this.pos;
      }

      void setPosition(int var1) {
         if (var1 >= 0 && var1 <= this.count) {
            this.pos = var1;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }
}
