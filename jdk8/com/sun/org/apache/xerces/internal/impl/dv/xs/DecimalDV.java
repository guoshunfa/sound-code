package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDecimal;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class DecimalDV extends TypeValidator {
   public final short getAllowedFacets() {
      return 4088;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return new DecimalDV.XDecimal(content);
      } catch (NumberFormatException var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "decimal"});
      }
   }

   public final int compare(Object value1, Object value2) {
      return ((DecimalDV.XDecimal)value1).compareTo((DecimalDV.XDecimal)value2);
   }

   public final int getTotalDigits(Object value) {
      return ((DecimalDV.XDecimal)value).totalDigits;
   }

   public final int getFractionDigits(Object value) {
      return ((DecimalDV.XDecimal)value).fracDigits;
   }

   static final class XDecimal implements XSDecimal {
      int sign = 1;
      int totalDigits = 0;
      int intDigits = 0;
      int fracDigits = 0;
      String ivalue = "";
      String fvalue = "";
      boolean integer = false;
      private String canonical;

      XDecimal(String content) throws NumberFormatException {
         this.initD(content);
      }

      XDecimal(String content, boolean integer) throws NumberFormatException {
         if (integer) {
            this.initI(content);
         } else {
            this.initD(content);
         }

      }

      void initD(String content) throws NumberFormatException {
         int len = content.length();
         if (len == 0) {
            throw new NumberFormatException();
         } else {
            int intStart = 0;
            int intEnd = false;
            int fracStart = 0;
            int fracEnd = 0;
            if (content.charAt(0) == '+') {
               intStart = 1;
            } else if (content.charAt(0) == '-') {
               intStart = 1;
               this.sign = -1;
            }

            int actualIntStart;
            for(actualIntStart = intStart; actualIntStart < len && content.charAt(actualIntStart) == '0'; ++actualIntStart) {
            }

            int intEnd;
            for(intEnd = actualIntStart; intEnd < len && TypeValidator.isDigit(content.charAt(intEnd)); ++intEnd) {
            }

            if (intEnd < len) {
               if (content.charAt(intEnd) != '.') {
                  throw new NumberFormatException();
               }

               fracStart = intEnd + 1;
               fracEnd = len;
            }

            if (intStart == intEnd && fracStart == fracEnd) {
               throw new NumberFormatException();
            } else {
               while(fracEnd > fracStart && content.charAt(fracEnd - 1) == '0') {
                  --fracEnd;
               }

               for(int fracPos = fracStart; fracPos < fracEnd; ++fracPos) {
                  if (!TypeValidator.isDigit(content.charAt(fracPos))) {
                     throw new NumberFormatException();
                  }
               }

               this.intDigits = intEnd - actualIntStart;
               this.fracDigits = fracEnd - fracStart;
               this.totalDigits = this.intDigits + this.fracDigits;
               if (this.intDigits > 0) {
                  this.ivalue = content.substring(actualIntStart, intEnd);
                  if (this.fracDigits > 0) {
                     this.fvalue = content.substring(fracStart, fracEnd);
                  }
               } else if (this.fracDigits > 0) {
                  this.fvalue = content.substring(fracStart, fracEnd);
               } else {
                  this.sign = 0;
               }

            }
         }
      }

      void initI(String content) throws NumberFormatException {
         int len = content.length();
         if (len == 0) {
            throw new NumberFormatException();
         } else {
            int intStart = 0;
            int intEnd = false;
            if (content.charAt(0) == '+') {
               intStart = 1;
            } else if (content.charAt(0) == '-') {
               intStart = 1;
               this.sign = -1;
            }

            int actualIntStart;
            for(actualIntStart = intStart; actualIntStart < len && content.charAt(actualIntStart) == '0'; ++actualIntStart) {
            }

            int intEnd;
            for(intEnd = actualIntStart; intEnd < len && TypeValidator.isDigit(content.charAt(intEnd)); ++intEnd) {
            }

            if (intEnd < len) {
               throw new NumberFormatException();
            } else if (intStart == intEnd) {
               throw new NumberFormatException();
            } else {
               this.intDigits = intEnd - actualIntStart;
               this.fracDigits = 0;
               this.totalDigits = this.intDigits;
               if (this.intDigits > 0) {
                  this.ivalue = content.substring(actualIntStart, intEnd);
               } else {
                  this.sign = 0;
               }

               this.integer = true;
            }
         }
      }

      public boolean equals(Object val) {
         if (val == this) {
            return true;
         } else if (!(val instanceof DecimalDV.XDecimal)) {
            return false;
         } else {
            DecimalDV.XDecimal oval = (DecimalDV.XDecimal)val;
            if (this.sign != oval.sign) {
               return false;
            } else if (this.sign == 0) {
               return true;
            } else {
               return this.intDigits == oval.intDigits && this.fracDigits == oval.fracDigits && this.ivalue.equals(oval.ivalue) && this.fvalue.equals(oval.fvalue);
            }
         }
      }

      public int hashCode() {
         int hash = 7;
         int hash = 17 * hash + this.sign;
         if (this.sign == 0) {
            return hash;
         } else {
            hash = 17 * hash + this.intDigits;
            hash = 17 * hash + this.fracDigits;
            hash = 17 * hash + Objects.hashCode(this.ivalue);
            hash = 17 * hash + Objects.hashCode(this.fvalue);
            return hash;
         }
      }

      public int compareTo(DecimalDV.XDecimal val) {
         if (this.sign != val.sign) {
            return this.sign > val.sign ? 1 : -1;
         } else {
            return this.sign == 0 ? 0 : this.sign * this.intComp(val);
         }
      }

      private int intComp(DecimalDV.XDecimal val) {
         if (this.intDigits != val.intDigits) {
            return this.intDigits > val.intDigits ? 1 : -1;
         } else {
            int ret = this.ivalue.compareTo(val.ivalue);
            if (ret != 0) {
               return ret > 0 ? 1 : -1;
            } else {
               ret = this.fvalue.compareTo(val.fvalue);
               return ret == 0 ? 0 : (ret > 0 ? 1 : -1);
            }
         }
      }

      public synchronized String toString() {
         if (this.canonical == null) {
            this.makeCanonical();
         }

         return this.canonical;
      }

      private void makeCanonical() {
         if (this.sign == 0) {
            if (this.integer) {
               this.canonical = "0";
            } else {
               this.canonical = "0.0";
            }

         } else if (this.integer && this.sign > 0) {
            this.canonical = this.ivalue;
         } else {
            StringBuilder buffer = new StringBuilder(this.totalDigits + 3);
            if (this.sign == -1) {
               buffer.append('-');
            }

            if (this.intDigits != 0) {
               buffer.append(this.ivalue);
            } else {
               buffer.append('0');
            }

            if (!this.integer) {
               buffer.append('.');
               if (this.fracDigits != 0) {
                  buffer.append(this.fvalue);
               } else {
                  buffer.append('0');
               }
            }

            this.canonical = buffer.toString();
         }
      }

      public BigDecimal getBigDecimal() {
         return this.sign == 0 ? new BigDecimal(BigInteger.ZERO) : new BigDecimal(this.toString());
      }

      public BigInteger getBigInteger() throws NumberFormatException {
         if (this.fracDigits != 0) {
            throw new NumberFormatException();
         } else if (this.sign == 0) {
            return BigInteger.ZERO;
         } else {
            return this.sign == 1 ? new BigInteger(this.ivalue) : new BigInteger("-" + this.ivalue);
         }
      }

      public long getLong() throws NumberFormatException {
         if (this.fracDigits != 0) {
            throw new NumberFormatException();
         } else if (this.sign == 0) {
            return 0L;
         } else {
            return this.sign == 1 ? Long.parseLong(this.ivalue) : Long.parseLong("-" + this.ivalue);
         }
      }

      public int getInt() throws NumberFormatException {
         if (this.fracDigits != 0) {
            throw new NumberFormatException();
         } else if (this.sign == 0) {
            return 0;
         } else {
            return this.sign == 1 ? Integer.parseInt(this.ivalue) : Integer.parseInt("-" + this.ivalue);
         }
      }

      public short getShort() throws NumberFormatException {
         if (this.fracDigits != 0) {
            throw new NumberFormatException();
         } else if (this.sign == 0) {
            return 0;
         } else {
            return this.sign == 1 ? Short.parseShort(this.ivalue) : Short.parseShort("-" + this.ivalue);
         }
      }

      public byte getByte() throws NumberFormatException {
         if (this.fracDigits != 0) {
            throw new NumberFormatException();
         } else if (this.sign == 0) {
            return 0;
         } else {
            return this.sign == 1 ? Byte.parseByte(this.ivalue) : Byte.parseByte("-" + this.ivalue);
         }
      }
   }
}
