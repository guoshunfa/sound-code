package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.io.ValueUtility;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public class AnyImpl extends Any {
   private TypeCodeImpl typeCode;
   protected ORB orb;
   private ORBUtilSystemException wrapper;
   private CDRInputStream stream;
   private long value;
   private Object object;
   private boolean isInitialized;
   private static final int DEFAULT_BUFFER_SIZE = 32;
   static boolean[] isStreamed = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, true, false, false, true, true, true, true, false, false, false, false, false, false, false, false, false, false};

   static AnyImpl convertToNative(ORB var0, Any var1) {
      if (var1 instanceof AnyImpl) {
         return (AnyImpl)var1;
      } else {
         AnyImpl var2 = new AnyImpl(var0, var1);
         var2.typeCode = TypeCodeImpl.convertToNative(var0, var2.typeCode);
         return var2;
      }
   }

   public AnyImpl(ORB var1) {
      this.isInitialized = false;
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.presentation");
      this.typeCode = var1.get_primitive_tc(0);
      this.stream = null;
      this.object = null;
      this.value = 0L;
      this.isInitialized = true;
   }

   public AnyImpl(ORB var1, Any var2) {
      this(var1);
      if (var2 instanceof AnyImpl) {
         AnyImpl var3 = (AnyImpl)var2;
         this.typeCode = var3.typeCode;
         this.value = var3.value;
         this.object = var3.object;
         this.isInitialized = var3.isInitialized;
         if (var3.stream != null) {
            this.stream = var3.stream.dup();
         }
      } else {
         this.read_value(var2.create_input_stream(), var2.type());
      }

   }

   public TypeCode type() {
      return this.typeCode;
   }

   private TypeCode realType() {
      return this.realType(this.typeCode);
   }

   private TypeCode realType(TypeCode var1) {
      TypeCode var2 = var1;

      try {
         while(var2.kind().value() == 21) {
            var2 = var2.content_type();
         }

         return var2;
      } catch (BadKind var4) {
         throw this.wrapper.badkindCannotOccur((Throwable)var4);
      }
   }

   public void type(TypeCode var1) {
      this.typeCode = TypeCodeImpl.convertToNative(this.orb, var1);
      this.stream = null;
      this.value = 0L;
      this.object = null;
      this.isInitialized = var1.kind().value() == 0;
   }

   public boolean equal(Any var1) {
      if (var1 == this) {
         return true;
      } else if (!this.typeCode.equal(var1.type())) {
         return false;
      } else {
         TypeCode var2 = this.realType();
         switch(var2.kind().value()) {
         case 0:
         case 1:
            return true;
         case 2:
            return this.extract_short() == var1.extract_short();
         case 3:
            return this.extract_long() == var1.extract_long();
         case 4:
            return this.extract_ushort() == var1.extract_ushort();
         case 5:
            return this.extract_ulong() == var1.extract_ulong();
         case 6:
            return this.extract_float() == var1.extract_float();
         case 7:
            return this.extract_double() == var1.extract_double();
         case 8:
            return this.extract_boolean() == var1.extract_boolean();
         case 9:
            return this.extract_char() == var1.extract_char();
         case 10:
            return this.extract_octet() == var1.extract_octet();
         case 11:
            return this.extract_any().equal(var1.extract_any());
         case 12:
            return this.extract_TypeCode().equal(var1.extract_TypeCode());
         case 13:
            return this.extract_Principal().equals(var1.extract_Principal());
         case 14:
            return this.extract_Object().equals(var1.extract_Object());
         case 15:
         case 16:
         case 19:
         case 20:
         case 22:
            InputStream var3 = this.create_input_stream();
            InputStream var4 = var1.create_input_stream();
            return this.equalMember(var2, var3, var4);
         case 17:
            return this.extract_long() == var1.extract_long();
         case 18:
            return this.extract_string().equals(var1.extract_string());
         case 21:
            throw this.wrapper.errorResolvingAlias();
         case 23:
            return this.extract_longlong() == var1.extract_longlong();
         case 24:
            return this.extract_ulonglong() == var1.extract_ulonglong();
         case 25:
            throw this.wrapper.tkLongDoubleNotSupported();
         case 26:
            return this.extract_wchar() == var1.extract_wchar();
         case 27:
            return this.extract_wstring().equals(var1.extract_wstring());
         case 28:
            return this.extract_fixed().compareTo(var1.extract_fixed()) == 0;
         case 29:
         case 30:
            return this.extract_Value().equals(var1.extract_Value());
         default:
            throw this.wrapper.typecodeNotSupported();
         }
      }
   }

   private boolean equalMember(TypeCode var1, InputStream var2, InputStream var3) {
      TypeCode var4 = this.realType(var1);

      try {
         int var11;
         int var13;
         switch(var4.kind().value()) {
         case 0:
         case 1:
            return true;
         case 2:
            return var2.read_short() == var3.read_short();
         case 3:
            return var2.read_long() == var3.read_long();
         case 4:
            return var2.read_ushort() == var3.read_ushort();
         case 5:
            return var2.read_ulong() == var3.read_ulong();
         case 6:
            return var2.read_float() == var3.read_float();
         case 7:
            return var2.read_double() == var3.read_double();
         case 8:
            return var2.read_boolean() == var3.read_boolean();
         case 9:
            return var2.read_char() == var3.read_char();
         case 10:
            return var2.read_octet() == var3.read_octet();
         case 11:
            return var2.read_any().equal(var3.read_any());
         case 12:
            return var2.read_TypeCode().equal(var3.read_TypeCode());
         case 13:
            return var2.read_Principal().equals(var3.read_Principal());
         case 14:
            return var2.read_Object().equals(var3.read_Object());
         case 15:
         case 22:
            var11 = var4.member_count();

            for(var13 = 0; var13 < var11; ++var13) {
               if (!this.equalMember(var4.member_type(var13), var2, var3)) {
                  return false;
               }
            }

            return true;
         case 16:
            Any var12 = this.orb.create_any();
            Any var14 = this.orb.create_any();
            var12.read_value(var2, var4.discriminator_type());
            var14.read_value(var3, var4.discriminator_type());
            if (!var12.equal(var14)) {
               return false;
            } else {
               TypeCodeImpl var7 = TypeCodeImpl.convertToNative(this.orb, var4);
               int var8 = var7.currentUnionMemberIndex(var12);
               if (var8 == -1) {
                  throw this.wrapper.unionDiscriminatorError();
               } else {
                  if (!this.equalMember(var4.member_type(var8), var2, var3)) {
                     return false;
                  }

                  return true;
               }
            }
         case 17:
            return var2.read_long() == var3.read_long();
         case 18:
            return var2.read_string().equals(var3.read_string());
         case 19:
            var11 = var2.read_long();
            var3.read_long();

            for(var13 = 0; var13 < var11; ++var13) {
               if (!this.equalMember(var4.content_type(), var2, var3)) {
                  return false;
               }
            }

            return true;
         case 20:
            var11 = var4.member_count();

            for(var13 = 0; var13 < var11; ++var13) {
               if (!this.equalMember(var4.content_type(), var2, var3)) {
                  return false;
               }
            }

            return true;
         case 21:
            throw this.wrapper.errorResolvingAlias();
         case 23:
            return var2.read_longlong() == var3.read_longlong();
         case 24:
            return var2.read_ulonglong() == var3.read_ulonglong();
         case 25:
            throw this.wrapper.tkLongDoubleNotSupported();
         case 26:
            return var2.read_wchar() == var3.read_wchar();
         case 27:
            return var2.read_wstring().equals(var3.read_wstring());
         case 28:
            return var2.read_fixed().compareTo(var3.read_fixed()) == 0;
         case 29:
         case 30:
            org.omg.CORBA_2_3.portable.InputStream var5 = (org.omg.CORBA_2_3.portable.InputStream)var2;
            org.omg.CORBA_2_3.portable.InputStream var6 = (org.omg.CORBA_2_3.portable.InputStream)var3;
            return var5.read_value().equals(var6.read_value());
         default:
            throw this.wrapper.typecodeNotSupported();
         }
      } catch (BadKind var9) {
         throw this.wrapper.badkindCannotOccur();
      } catch (Bounds var10) {
         throw this.wrapper.boundsCannotOccur();
      }
   }

   public OutputStream create_output_stream() {
      final ORB var1 = this.orb;
      return (OutputStream)AccessController.doPrivileged(new PrivilegedAction<AnyImpl.AnyOutputStream>() {
         public AnyImpl.AnyOutputStream run() {
            return new AnyImpl.AnyOutputStream(var1);
         }
      });
   }

   public InputStream create_input_stream() {
      if (isStreamed[this.realType().kind().value()]) {
         return this.stream.dup();
      } else {
         OutputStream var1 = this.orb.create_output_stream();
         TCUtility.marshalIn(var1, this.realType(), this.value, this.object);
         return var1.create_input_stream();
      }
   }

   public void read_value(InputStream var1, TypeCode var2) {
      this.typeCode = TypeCodeImpl.convertToNative(this.orb, var2);
      int var3 = this.realType().kind().value();
      if (var3 >= isStreamed.length) {
         throw this.wrapper.invalidIsstreamedTckind((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, new Integer(var3));
      } else {
         if (isStreamed[var3]) {
            if (var1 instanceof AnyImpl.AnyInputStream) {
               this.stream = (CDRInputStream)var1;
            } else {
               org.omg.CORBA_2_3.portable.OutputStream var4 = (org.omg.CORBA_2_3.portable.OutputStream)this.orb.create_output_stream();
               this.typeCode.copy((org.omg.CORBA_2_3.portable.InputStream)var1, var4);
               this.stream = (CDRInputStream)var4.create_input_stream();
            }
         } else {
            Object[] var6 = new Object[]{this.object};
            long[] var5 = new long[1];
            TCUtility.unmarshalIn(var1, this.realType(), var5, var6);
            this.value = var5[0];
            this.object = var6[0];
            this.stream = null;
         }

         this.isInitialized = true;
      }
   }

   public void write_value(OutputStream var1) {
      if (isStreamed[this.realType().kind().value()]) {
         this.typeCode.copy(this.stream.dup(), var1);
      } else {
         TCUtility.marshalIn(var1, this.realType(), this.value, this.object);
      }

   }

   public void insert_Streamable(Streamable var1) {
      this.typeCode = TypeCodeImpl.convertToNative(this.orb, var1._type());
      this.object = var1;
      this.isInitialized = true;
   }

   public Streamable extract_Streamable() {
      return (Streamable)this.object;
   }

   public void insert_short(short var1) {
      this.typeCode = this.orb.get_primitive_tc(2);
      this.value = (long)var1;
      this.isInitialized = true;
   }

   private String getTCKindName(int var1) {
      return var1 >= 0 && var1 < TypeCodeImpl.kindNames.length ? TypeCodeImpl.kindNames[var1] : "UNKNOWN(" + var1 + ")";
   }

   private void checkExtractBadOperation(int var1) {
      if (!this.isInitialized) {
         throw this.wrapper.extractNotInitialized();
      } else {
         int var2 = this.realType().kind().value();
         if (var2 != var1) {
            String var3 = this.getTCKindName(var2);
            String var4 = this.getTCKindName(var1);
            throw this.wrapper.extractWrongType(var4, var3);
         }
      }
   }

   private void checkExtractBadOperationList(int[] var1) {
      if (!this.isInitialized) {
         throw this.wrapper.extractNotInitialized();
      } else {
         int var2 = this.realType().kind().value();

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var2 == var1[var3]) {
               return;
            }
         }

         ArrayList var5 = new ArrayList();

         for(int var4 = 0; var4 < var1.length; ++var4) {
            var5.add(this.getTCKindName(var1[var4]));
         }

         String var6 = this.getTCKindName(var2);
         throw this.wrapper.extractWrongTypeList(var5, var6);
      }
   }

   public short extract_short() {
      this.checkExtractBadOperation(2);
      return (short)((int)this.value);
   }

   public void insert_long(int var1) {
      int var2 = this.realType().kind().value();
      if (var2 != 3 && var2 != 17) {
         this.typeCode = this.orb.get_primitive_tc(3);
      }

      this.value = (long)var1;
      this.isInitialized = true;
   }

   public int extract_long() {
      this.checkExtractBadOperationList(new int[]{3, 17});
      return (int)this.value;
   }

   public void insert_ushort(short var1) {
      this.typeCode = this.orb.get_primitive_tc(4);
      this.value = (long)var1;
      this.isInitialized = true;
   }

   public short extract_ushort() {
      this.checkExtractBadOperation(4);
      return (short)((int)this.value);
   }

   public void insert_ulong(int var1) {
      this.typeCode = this.orb.get_primitive_tc(5);
      this.value = (long)var1;
      this.isInitialized = true;
   }

   public int extract_ulong() {
      this.checkExtractBadOperation(5);
      return (int)this.value;
   }

   public void insert_float(float var1) {
      this.typeCode = this.orb.get_primitive_tc(6);
      this.value = (long)Float.floatToIntBits(var1);
      this.isInitialized = true;
   }

   public float extract_float() {
      this.checkExtractBadOperation(6);
      return Float.intBitsToFloat((int)this.value);
   }

   public void insert_double(double var1) {
      this.typeCode = this.orb.get_primitive_tc(7);
      this.value = Double.doubleToLongBits(var1);
      this.isInitialized = true;
   }

   public double extract_double() {
      this.checkExtractBadOperation(7);
      return Double.longBitsToDouble(this.value);
   }

   public void insert_longlong(long var1) {
      this.typeCode = this.orb.get_primitive_tc(23);
      this.value = var1;
      this.isInitialized = true;
   }

   public long extract_longlong() {
      this.checkExtractBadOperation(23);
      return this.value;
   }

   public void insert_ulonglong(long var1) {
      this.typeCode = this.orb.get_primitive_tc(24);
      this.value = var1;
      this.isInitialized = true;
   }

   public long extract_ulonglong() {
      this.checkExtractBadOperation(24);
      return this.value;
   }

   public void insert_boolean(boolean var1) {
      this.typeCode = this.orb.get_primitive_tc(8);
      this.value = var1 ? 1L : 0L;
      this.isInitialized = true;
   }

   public boolean extract_boolean() {
      this.checkExtractBadOperation(8);
      return this.value != 0L;
   }

   public void insert_char(char var1) {
      this.typeCode = this.orb.get_primitive_tc(9);
      this.value = (long)var1;
      this.isInitialized = true;
   }

   public char extract_char() {
      this.checkExtractBadOperation(9);
      return (char)((int)this.value);
   }

   public void insert_wchar(char var1) {
      this.typeCode = this.orb.get_primitive_tc(26);
      this.value = (long)var1;
      this.isInitialized = true;
   }

   public char extract_wchar() {
      this.checkExtractBadOperation(26);
      return (char)((int)this.value);
   }

   public void insert_octet(byte var1) {
      this.typeCode = this.orb.get_primitive_tc(10);
      this.value = (long)var1;
      this.isInitialized = true;
   }

   public byte extract_octet() {
      this.checkExtractBadOperation(10);
      return (byte)((int)this.value);
   }

   public void insert_string(String var1) {
      if (this.typeCode.kind() == TCKind.tk_string) {
         boolean var2 = false;

         int var5;
         try {
            var5 = this.typeCode.length();
         } catch (BadKind var4) {
            throw this.wrapper.badkindCannotOccur();
         }

         if (var5 != 0 && var1 != null && var1.length() > var5) {
            throw this.wrapper.badStringBounds(new Integer(var1.length()), new Integer(var5));
         }
      } else {
         this.typeCode = this.orb.get_primitive_tc(18);
      }

      this.object = var1;
      this.isInitialized = true;
   }

   public String extract_string() {
      this.checkExtractBadOperation(18);
      return (String)this.object;
   }

   public void insert_wstring(String var1) {
      if (this.typeCode.kind() == TCKind.tk_wstring) {
         boolean var2 = false;

         int var5;
         try {
            var5 = this.typeCode.length();
         } catch (BadKind var4) {
            throw this.wrapper.badkindCannotOccur();
         }

         if (var5 != 0 && var1 != null && var1.length() > var5) {
            throw this.wrapper.badStringBounds(new Integer(var1.length()), new Integer(var5));
         }
      } else {
         this.typeCode = this.orb.get_primitive_tc(27);
      }

      this.object = var1;
      this.isInitialized = true;
   }

   public String extract_wstring() {
      this.checkExtractBadOperation(27);
      return (String)this.object;
   }

   public void insert_any(Any var1) {
      this.typeCode = this.orb.get_primitive_tc(11);
      this.object = var1;
      this.stream = null;
      this.isInitialized = true;
   }

   public Any extract_any() {
      this.checkExtractBadOperation(11);
      return (Any)this.object;
   }

   public void insert_Object(org.omg.CORBA.Object var1) {
      if (var1 == null) {
         this.typeCode = this.orb.get_primitive_tc(14);
      } else {
         if (!StubAdapter.isStub(var1)) {
            throw this.wrapper.badInsertobjParam((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, var1.getClass().getName());
         }

         String[] var2 = StubAdapter.getTypeIds(var1);
         this.typeCode = new TypeCodeImpl(this.orb, 14, var2[0], "");
      }

      this.object = var1;
      this.isInitialized = true;
   }

   public void insert_Object(org.omg.CORBA.Object var1, TypeCode var2) {
      try {
         if (!var2.id().equals("IDL:omg.org/CORBA/Object:1.0") && !var1._is_a(var2.id())) {
            throw this.wrapper.insertObjectIncompatible();
         }

         this.typeCode = TypeCodeImpl.convertToNative(this.orb, var2);
         this.object = var1;
      } catch (Exception var4) {
         throw this.wrapper.insertObjectFailed((Throwable)var4);
      }

      this.isInitialized = true;
   }

   public org.omg.CORBA.Object extract_Object() {
      if (!this.isInitialized) {
         throw this.wrapper.extractNotInitialized();
      } else {
         org.omg.CORBA.Object var1 = null;

         try {
            var1 = (org.omg.CORBA.Object)this.object;
            if (!this.typeCode.id().equals("IDL:omg.org/CORBA/Object:1.0") && !var1._is_a(this.typeCode.id())) {
               throw this.wrapper.extractObjectIncompatible();
            } else {
               return var1;
            }
         } catch (Exception var3) {
            throw this.wrapper.extractObjectFailed((Throwable)var3);
         }
      }
   }

   public void insert_TypeCode(TypeCode var1) {
      this.typeCode = this.orb.get_primitive_tc(12);
      this.object = var1;
      this.isInitialized = true;
   }

   public TypeCode extract_TypeCode() {
      this.checkExtractBadOperation(12);
      return (TypeCode)this.object;
   }

   /** @deprecated */
   @Deprecated
   public void insert_Principal(Principal var1) {
      this.typeCode = this.orb.get_primitive_tc(13);
      this.object = var1;
      this.isInitialized = true;
   }

   /** @deprecated */
   @Deprecated
   public Principal extract_Principal() {
      this.checkExtractBadOperation(13);
      return (Principal)this.object;
   }

   public Serializable extract_Value() {
      this.checkExtractBadOperationList(new int[]{29, 30, 32});
      return (Serializable)this.object;
   }

   public void insert_Value(Serializable var1) {
      this.object = var1;
      TypeCode var2;
      if (var1 == null) {
         var2 = this.orb.get_primitive_tc(TCKind.tk_value);
      } else {
         var2 = this.createTypeCodeForClass(var1.getClass(), (ORB)ORB.init());
      }

      this.typeCode = TypeCodeImpl.convertToNative(this.orb, var2);
      this.isInitialized = true;
   }

   public void insert_Value(Serializable var1, TypeCode var2) {
      this.object = var1;
      this.typeCode = TypeCodeImpl.convertToNative(this.orb, var2);
      this.isInitialized = true;
   }

   public void insert_fixed(BigDecimal var1) {
      this.typeCode = TypeCodeImpl.convertToNative(this.orb, this.orb.create_fixed_tc(TypeCodeImpl.digits(var1), TypeCodeImpl.scale(var1)));
      this.object = var1;
      this.isInitialized = true;
   }

   public void insert_fixed(BigDecimal var1, TypeCode var2) {
      try {
         if (TypeCodeImpl.digits(var1) > var2.fixed_digits() || TypeCodeImpl.scale(var1) > var2.fixed_scale()) {
            throw this.wrapper.fixedNotMatch();
         }
      } catch (BadKind var4) {
         throw this.wrapper.fixedBadTypecode((Throwable)var4);
      }

      this.typeCode = TypeCodeImpl.convertToNative(this.orb, var2);
      this.object = var1;
      this.isInitialized = true;
   }

   public BigDecimal extract_fixed() {
      this.checkExtractBadOperation(28);
      return (BigDecimal)this.object;
   }

   public TypeCode createTypeCodeForClass(Class var1, ORB var2) {
      TypeCodeImpl var3 = var2.getTypeCodeForClass(var1);
      if (var3 != null) {
         return var3;
      } else {
         RepositoryIdStrings var4 = RepositoryIdFactory.getRepIdStringsFactory();
         if (var1.isArray()) {
            Class var9 = var1.getComponentType();
            TypeCode var10;
            if (var9.isPrimitive()) {
               var10 = this.getPrimitiveTypeCodeForClass(var9, var2);
            } else {
               var10 = this.createTypeCodeForClass(var9, var2);
            }

            TypeCode var7 = var2.create_sequence_tc(0, var10);
            String var8 = var4.createForJavaType(var1);
            return var2.create_value_box_tc(var8, "Sequence", var7);
         } else if (var1 == String.class) {
            TypeCode var5 = var2.create_string_tc(0);
            String var6 = var4.createForJavaType(var1);
            return var2.create_value_box_tc(var6, "StringValue", var5);
         } else {
            var3 = (TypeCodeImpl)ValueUtility.createTypeCodeForClass(var2, var1, ORBUtility.createValueHandler());
            var3.setCaching(true);
            var2.setTypeCodeForClass(var1, var3);
            return var3;
         }
      }
   }

   private TypeCode getPrimitiveTypeCodeForClass(Class var1, ORB var2) {
      if (var1 == Integer.TYPE) {
         return var2.get_primitive_tc(TCKind.tk_long);
      } else if (var1 == Byte.TYPE) {
         return var2.get_primitive_tc(TCKind.tk_octet);
      } else if (var1 == Long.TYPE) {
         return var2.get_primitive_tc(TCKind.tk_longlong);
      } else if (var1 == Float.TYPE) {
         return var2.get_primitive_tc(TCKind.tk_float);
      } else if (var1 == Double.TYPE) {
         return var2.get_primitive_tc(TCKind.tk_double);
      } else if (var1 == Short.TYPE) {
         return var2.get_primitive_tc(TCKind.tk_short);
      } else if (var1 == Character.TYPE) {
         return ORBVersionFactory.getFOREIGN().compareTo(var2.getORBVersion()) != 0 && ORBVersionFactory.getNEWER().compareTo(var2.getORBVersion()) > 0 ? var2.get_primitive_tc(TCKind.tk_char) : var2.get_primitive_tc(TCKind.tk_wchar);
      } else {
         return var1 == Boolean.TYPE ? var2.get_primitive_tc(TCKind.tk_boolean) : var2.get_primitive_tc(TCKind.tk_any);
      }
   }

   public Any extractAny(TypeCode var1, ORB var2) {
      Any var3 = var2.create_any();
      OutputStream var4 = var3.create_output_stream();
      TypeCodeImpl.convertToNative(var2, var1).copy(this.stream, var4);
      var3.read_value(var4.create_input_stream(), var1);
      return var3;
   }

   public static Any extractAnyFromStream(TypeCode var0, InputStream var1, ORB var2) {
      Any var3 = var2.create_any();
      OutputStream var4 = var3.create_output_stream();
      TypeCodeImpl.convertToNative(var2, var0).copy(var1, var4);
      var3.read_value(var4.create_input_stream(), var0);
      return var3;
   }

   public boolean isInitialized() {
      return this.isInitialized;
   }

   private static final class AnyOutputStream extends EncapsOutputStream {
      public AnyOutputStream(ORB var1) {
         super(var1);
      }

      public InputStream create_input_stream() {
         final InputStream var1 = super.create_input_stream();
         AnyImpl.AnyInputStream var2 = (AnyImpl.AnyInputStream)AccessController.doPrivileged(new PrivilegedAction<AnyImpl.AnyInputStream>() {
            public AnyImpl.AnyInputStream run() {
               return new AnyImpl.AnyInputStream((EncapsInputStream)var1);
            }
         });
         return var2;
      }
   }

   private static final class AnyInputStream extends EncapsInputStream {
      public AnyInputStream(EncapsInputStream var1) {
         super(var1);
      }
   }
}
