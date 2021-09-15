package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynAnyUtil {
   static boolean isConsistentType(TypeCode var0) {
      int var1 = var0.kind().value();
      return var1 != 13 && var1 != 31 && var1 != 32;
   }

   static boolean isConstructedDynAny(DynAny var0) {
      int var1 = var0.type().kind().value();
      return var1 == 19 || var1 == 15 || var1 == 20 || var1 == 16 || var1 == 17 || var1 == 28 || var1 == 29 || var1 == 30;
   }

   static DynAny createMostDerivedDynAny(Any var0, ORB var1, boolean var2) throws InconsistentTypeCode {
      if (var0 != null && isConsistentType(var0.type())) {
         switch(var0.type().kind().value()) {
         case 15:
            return new DynStructImpl(var1, var0, var2);
         case 16:
            return new DynUnionImpl(var1, var0, var2);
         case 17:
            return new DynEnumImpl(var1, var0, var2);
         case 18:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         default:
            return new DynAnyBasicImpl(var1, var0, var2);
         case 19:
            return new DynSequenceImpl(var1, var0, var2);
         case 20:
            return new DynArrayImpl(var1, var0, var2);
         case 28:
            return new DynFixedImpl(var1, var0, var2);
         case 29:
            return new DynValueImpl(var1, var0, var2);
         case 30:
            return new DynValueBoxImpl(var1, var0, var2);
         }
      } else {
         throw new InconsistentTypeCode();
      }
   }

   static DynAny createMostDerivedDynAny(TypeCode var0, ORB var1) throws InconsistentTypeCode {
      if (var0 != null && isConsistentType(var0)) {
         switch(var0.kind().value()) {
         case 15:
            return new DynStructImpl(var1, var0);
         case 16:
            return new DynUnionImpl(var1, var0);
         case 17:
            return new DynEnumImpl(var1, var0);
         case 18:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         default:
            return new DynAnyBasicImpl(var1, var0);
         case 19:
            return new DynSequenceImpl(var1, var0);
         case 20:
            return new DynArrayImpl(var1, var0);
         case 28:
            return new DynFixedImpl(var1, var0);
         case 29:
            return new DynValueImpl(var1, var0);
         case 30:
            return new DynValueBoxImpl(var1, var0);
         }
      } else {
         throw new InconsistentTypeCode();
      }
   }

   static Any extractAnyFromStream(TypeCode var0, InputStream var1, ORB var2) {
      return AnyImpl.extractAnyFromStream(var0, var1, var2);
   }

   static Any createDefaultAnyOfType(TypeCode var0, ORB var1) {
      ORBUtilSystemException var2 = ORBUtilSystemException.get(var1, "rpc.presentation");
      Any var3 = var1.create_any();
      switch(var0.kind().value()) {
      case 0:
         break;
      case 1:
      case 13:
      case 21:
      case 31:
      case 32:
         var3.type(var0);
         break;
      case 2:
         var3.insert_short((short)0);
         break;
      case 3:
         var3.insert_long(0);
         break;
      case 4:
         var3.insert_ushort((short)0);
         break;
      case 5:
         var3.insert_ulong(0);
         break;
      case 6:
         var3.insert_float(0.0F);
         break;
      case 7:
         var3.insert_double(0.0D);
         break;
      case 8:
         var3.insert_boolean(false);
         break;
      case 9:
         var3.insert_char('\u0000');
         break;
      case 10:
         var3.insert_octet((byte)0);
         break;
      case 11:
         var3.insert_any(var1.create_any());
         break;
      case 12:
         var3.insert_TypeCode(var3.type());
         break;
      case 14:
         var3.insert_Object((Object)null);
         break;
      case 15:
      case 16:
      case 17:
      case 19:
      case 20:
      case 22:
      case 29:
      case 30:
         var3.type(var0);
         break;
      case 18:
         var3.type(var0);
         var3.insert_string("");
         break;
      case 23:
         var3.insert_longlong(0L);
         break;
      case 24:
         var3.insert_ulonglong(0L);
         break;
      case 25:
         throw var2.tkLongDoubleNotSupported();
      case 26:
         var3.insert_wchar('\u0000');
         break;
      case 27:
         var3.type(var0);
         var3.insert_wstring("");
         break;
      case 28:
         var3.insert_fixed(new BigDecimal("0.0"), var0);
         break;
      default:
         throw var2.typecodeNotSupported();
      }

      return var3;
   }

   static Any copy(Any var0, ORB var1) {
      return new AnyImpl(var1, var0);
   }

   static DynAny convertToNative(DynAny var0, ORB var1) {
      if (var0 instanceof DynAnyImpl) {
         return var0;
      } else {
         try {
            return createMostDerivedDynAny(var0.to_any(), var1, true);
         } catch (InconsistentTypeCode var3) {
            return null;
         }
      }
   }

   static boolean isInitialized(Any var0) {
      boolean var1 = ((AnyImpl)var0).isInitialized();
      switch(var0.type().kind().value()) {
      case 18:
         return var1 && var0.extract_string() != null;
      case 27:
         return var1 && var0.extract_wstring() != null;
      default:
         return var1;
      }
   }

   static boolean set_current_component(DynAny var0, DynAny var1) {
      if (var1 != null) {
         try {
            var0.rewind();

            do {
               if (var0.current_component() == var1) {
                  return true;
               }
            } while(var0.next());
         } catch (TypeMismatch var3) {
         }
      }

      return false;
   }
}
