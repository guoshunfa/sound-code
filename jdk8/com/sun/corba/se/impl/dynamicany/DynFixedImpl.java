package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynFixed;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynFixedImpl extends DynAnyBasicImpl implements DynFixed {
   private DynFixedImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynFixedImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected DynFixedImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
      this.index = -1;
   }

   public String get_value() {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         return this.any.extract_fixed().toString();
      }
   }

   public boolean set_value(String var1) throws TypeMismatch, InvalidValue {
      if (this.status == 2) {
         throw this.wrapper.dynAnyDestroyed();
      } else {
         short var2 = 0;
         boolean var3 = false;
         boolean var4 = true;

         try {
            var2 = this.any.type().fixed_digits();
            short var17 = this.any.type().fixed_scale();
         } catch (BadKind var16) {
         }

         String var5 = var1.trim();
         if (var5.length() == 0) {
            throw new TypeMismatch();
         } else {
            String var6 = "";
            if (var5.charAt(0) == '-') {
               var6 = "-";
               var5 = var5.substring(1);
            } else if (var5.charAt(0) == '+') {
               var6 = "+";
               var5 = var5.substring(1);
            }

            int var7 = var5.indexOf(100);
            if (var7 == -1) {
               var7 = var5.indexOf(68);
            }

            if (var7 != -1) {
               var5 = var5.substring(0, var7);
            }

            if (var5.length() == 0) {
               throw new TypeMismatch();
            } else {
               int var12 = var5.indexOf(46);
               String var8;
               String var9;
               int var11;
               if (var12 == -1) {
                  var8 = var5;
                  var9 = null;
                  boolean var10 = false;
                  var11 = var5.length();
               } else {
                  int var18;
                  if (var12 == 0) {
                     var8 = null;
                     var9 = var5;
                     var18 = var5.length();
                     var11 = var18;
                  } else {
                     var8 = var5.substring(0, var12);
                     var9 = var5.substring(var12 + 1);
                     var18 = var9.length();
                     var11 = var8.length() + var18;
                  }
               }

               if (var11 > var2) {
                  var4 = false;
                  if (var8.length() < var2) {
                     var9 = var9.substring(0, var2 - var8.length());
                  } else {
                     if (var8.length() != var2) {
                        throw new InvalidValue();
                     }

                     var9 = null;
                  }
               }

               BigDecimal var13;
               try {
                  new BigInteger(var8);
                  if (var9 == null) {
                     var13 = new BigDecimal(var6 + var8);
                  } else {
                     new BigInteger(var9);
                     var13 = new BigDecimal(var6 + var8 + "." + var9);
                  }
               } catch (NumberFormatException var15) {
                  throw new TypeMismatch();
               }

               this.any.insert_fixed(var13, this.any.type());
               return var4;
            }
         }
      }
   }

   public String toString() {
      short var1 = 0;
      short var2 = 0;

      try {
         var1 = this.any.type().fixed_digits();
         var2 = this.any.type().fixed_scale();
      } catch (BadKind var4) {
      }

      return "DynFixed with value=" + this.get_value() + ", digits=" + var1 + ", scale=" + var2;
   }
}
