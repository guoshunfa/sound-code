package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TCUtility {
   static void marshalIn(OutputStream var0, TypeCode var1, long var2, Object var4) {
      switch(var1.kind().value()) {
      case 0:
      case 1:
      case 31:
         break;
      case 2:
         var0.write_short((short)((int)(var2 & 65535L)));
         break;
      case 3:
      case 17:
         var0.write_long((int)(var2 & 4294967295L));
         break;
      case 4:
         var0.write_ushort((short)((int)(var2 & 65535L)));
         break;
      case 5:
         var0.write_ulong((int)(var2 & 4294967295L));
         break;
      case 6:
         var0.write_float(Float.intBitsToFloat((int)(var2 & 4294967295L)));
         break;
      case 7:
         var0.write_double(Double.longBitsToDouble(var2));
         break;
      case 8:
         if (var2 == 0L) {
            var0.write_boolean(false);
         } else {
            var0.write_boolean(true);
         }
         break;
      case 9:
         var0.write_char((char)((int)(var2 & 65535L)));
         break;
      case 10:
         var0.write_octet((byte)((int)(var2 & 255L)));
         break;
      case 11:
         var0.write_any((Any)var4);
         break;
      case 12:
         var0.write_TypeCode((TypeCode)var4);
         break;
      case 13:
         var0.write_Principal((Principal)var4);
         break;
      case 14:
         var0.write_Object((org.omg.CORBA.Object)var4);
         break;
      case 15:
      case 16:
      case 19:
      case 20:
      case 21:
      case 22:
         ((Streamable)var4)._write(var0);
         break;
      case 18:
         var0.write_string((String)var4);
         break;
      case 23:
         var0.write_longlong(var2);
         break;
      case 24:
         var0.write_ulonglong(var2);
         break;
      case 25:
      default:
         ORBUtilSystemException var5 = ORBUtilSystemException.get((ORB)var0.orb(), "rpc.presentation");
         throw var5.typecodeNotSupported();
      case 26:
         var0.write_wchar((char)((int)(var2 & 65535L)));
         break;
      case 27:
         var0.write_wstring((String)var4);
         break;
      case 28:
         if (var0 instanceof CDROutputStream) {
            try {
               ((CDROutputStream)var0).write_fixed((BigDecimal)var4, var1.fixed_digits(), var1.fixed_scale());
            } catch (BadKind var6) {
            }
         } else {
            var0.write_fixed((BigDecimal)var4);
         }
         break;
      case 29:
      case 30:
         ((org.omg.CORBA_2_3.portable.OutputStream)var0).write_value((Serializable)var4);
         break;
      case 32:
         ((org.omg.CORBA_2_3.portable.OutputStream)var0).write_abstract_interface(var4);
      }

   }

   static void unmarshalIn(InputStream var0, TypeCode var1, long[] var2, Object[] var3) {
      int var4 = var1.kind().value();
      long var5 = 0L;
      Object var7 = var3[0];
      switch(var4) {
      case 0:
      case 1:
      case 31:
         break;
      case 2:
         var5 = (long)var0.read_short() & 65535L;
         break;
      case 3:
      case 17:
         var5 = (long)var0.read_long() & 4294967295L;
         break;
      case 4:
         var5 = (long)var0.read_ushort() & 65535L;
         break;
      case 5:
         var5 = (long)var0.read_ulong() & 4294967295L;
         break;
      case 6:
         var5 = (long)Float.floatToIntBits(var0.read_float()) & 4294967295L;
         break;
      case 7:
         var5 = Double.doubleToLongBits(var0.read_double());
         break;
      case 8:
         if (var0.read_boolean()) {
            var5 = 1L;
         } else {
            var5 = 0L;
         }
         break;
      case 9:
         var5 = (long)var0.read_char() & 65535L;
         break;
      case 10:
         var5 = (long)var0.read_octet() & 255L;
         break;
      case 11:
         var7 = var0.read_any();
         break;
      case 12:
         var7 = var0.read_TypeCode();
         break;
      case 13:
         var7 = var0.read_Principal();
         break;
      case 14:
         if (var7 instanceof Streamable) {
            ((Streamable)var7)._read(var0);
         } else {
            var7 = var0.read_Object();
         }
         break;
      case 15:
      case 16:
      case 19:
      case 20:
      case 21:
      case 22:
         ((Streamable)var7)._read(var0);
         break;
      case 18:
         var7 = var0.read_string();
         break;
      case 23:
         var5 = var0.read_longlong();
         break;
      case 24:
         var5 = var0.read_ulonglong();
         break;
      case 25:
      default:
         ORBUtilSystemException var10 = ORBUtilSystemException.get((ORB)var0.orb(), "rpc.presentation");
         throw var10.typecodeNotSupported();
      case 26:
         var5 = (long)var0.read_wchar() & 65535L;
         break;
      case 27:
         var7 = var0.read_wstring();
         break;
      case 28:
         try {
            if (var0 instanceof CDRInputStream) {
               var7 = ((CDRInputStream)var0).read_fixed(var1.fixed_digits(), var1.fixed_scale());
            } else {
               BigDecimal var8 = var0.read_fixed();
               var7 = var8.movePointLeft(var1.fixed_scale());
            }
         } catch (BadKind var9) {
         }
         break;
      case 29:
      case 30:
         var7 = ((org.omg.CORBA_2_3.portable.InputStream)var0).read_value();
         break;
      case 32:
         var7 = ((org.omg.CORBA_2_3.portable.InputStream)var0).read_abstract_interface();
      }

      var3[0] = var7;
      var2[0] = var5;
   }
}
