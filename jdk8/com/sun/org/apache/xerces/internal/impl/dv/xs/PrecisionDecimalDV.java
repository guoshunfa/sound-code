package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

class PrecisionDecimalDV extends TypeValidator {
   public short getAllowedFacets() {
      return 4088;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return new PrecisionDecimalDV.XPrecisionDecimal(content);
      } catch (NumberFormatException var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "precisionDecimal"});
      }
   }

   public int compare(Object value1, Object value2) {
      return ((PrecisionDecimalDV.XPrecisionDecimal)value1).compareTo((PrecisionDecimalDV.XPrecisionDecimal)value2);
   }

   public int getFractionDigits(Object value) {
      return ((PrecisionDecimalDV.XPrecisionDecimal)value).fracDigits;
   }

   public int getTotalDigits(Object value) {
      return ((PrecisionDecimalDV.XPrecisionDecimal)value).totalDigits;
   }

   public boolean isIdentical(Object value1, Object value2) {
      return value2 instanceof PrecisionDecimalDV.XPrecisionDecimal && value1 instanceof PrecisionDecimalDV.XPrecisionDecimal ? ((PrecisionDecimalDV.XPrecisionDecimal)value1).isIdentical((PrecisionDecimalDV.XPrecisionDecimal)value2) : false;
   }

   static final class XPrecisionDecimal {
      int sign = 1;
      int totalDigits = 0;
      int intDigits = 0;
      int fracDigits = 0;
      String ivalue = "";
      String fvalue = "";
      int pvalue = 0;
      private String canonical;

      XPrecisionDecimal(String content) throws NumberFormatException {
         if (content.equals("NaN")) {
            this.ivalue = content;
            this.sign = 0;
         }

         if (!content.equals("+INF") && !content.equals("INF") && !content.equals("-INF")) {
            this.initD(content);
         } else {
            this.ivalue = content.charAt(0) == '+' ? content.substring(1) : content;
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
               if (content.charAt(intEnd) != '.' && content.charAt(intEnd) != 'E' && content.charAt(intEnd) != 'e') {
                  throw new NumberFormatException();
               }

               if (content.charAt(intEnd) == '.') {
                  fracStart = intEnd + 1;

                  for(fracEnd = fracStart; fracEnd < len && TypeValidator.isDigit(content.charAt(fracEnd)); ++fracEnd) {
                  }
               } else {
                  this.pvalue = Integer.parseInt(content.substring(intEnd + 1, len));
               }
            }

            if (intStart == intEnd && fracStart == fracEnd) {
               throw new NumberFormatException();
            } else {
               for(int fracPos = fracStart; fracPos < fracEnd; ++fracPos) {
                  if (!TypeValidator.isDigit(content.charAt(fracPos))) {
                     throw new NumberFormatException();
                  }
               }

               this.intDigits = intEnd - actualIntStart;
               this.fracDigits = fracEnd - fracStart;
               if (this.intDigits > 0) {
                  this.ivalue = content.substring(actualIntStart, intEnd);
               }

               if (this.fracDigits > 0) {
                  this.fvalue = content.substring(fracStart, fracEnd);
                  if (fracEnd < len) {
                     this.pvalue = Integer.parseInt(content.substring(fracEnd + 1, len));
                  }
               }

               this.totalDigits = this.intDigits + this.fracDigits;
            }
         }
      }

      private static String canonicalToStringForHashCode(String ivalue, String fvalue, int sign, int pvalue) {
         if ("NaN".equals(ivalue)) {
            return "NaN";
         } else if ("INF".equals(ivalue)) {
            return sign < 0 ? "-INF" : "INF";
         } else {
            StringBuilder builder = new StringBuilder();
            int ilen = ivalue.length();
            int flen0 = fvalue.length();

            int lastNonZero;
            for(lastNonZero = flen0; lastNonZero > 0 && fvalue.charAt(lastNonZero - 1) == '0'; --lastNonZero) {
            }

            int flen = lastNonZero;
            int exponent = pvalue;

            int iStart;
            for(iStart = 0; iStart < ilen && ivalue.charAt(iStart) == '0'; ++iStart) {
            }

            int fStart = 0;
            if (iStart < ivalue.length()) {
               builder.append(sign == -1 ? "-" : "");
               builder.append(ivalue.charAt(iStart));
               ++iStart;
            } else {
               if (lastNonZero <= 0) {
                  return "0";
               }

               for(fStart = 0; fStart < flen && fvalue.charAt(fStart) == '0'; ++fStart) {
               }

               if (fStart >= flen) {
                  return "0";
               }

               builder.append(sign == -1 ? "-" : "");
               builder.append(fvalue.charAt(fStart));
               ++fStart;
               exponent = pvalue - fStart;
            }

            if (iStart < ilen || fStart < flen) {
               builder.append('.');
            }

            while(iStart < ilen) {
               builder.append(ivalue.charAt(iStart++));
               ++exponent;
            }

            while(fStart < flen) {
               builder.append(fvalue.charAt(fStart++));
            }

            if (exponent != 0) {
               builder.append("E").append(exponent);
            }

            return builder.toString();
         }
      }

      public boolean equals(Object val) {
         if (val == this) {
            return true;
         } else if (!(val instanceof PrecisionDecimalDV.XPrecisionDecimal)) {
            return false;
         } else {
            PrecisionDecimalDV.XPrecisionDecimal oval = (PrecisionDecimalDV.XPrecisionDecimal)val;
            return this.compareTo(oval) == 0;
         }
      }

      public int hashCode() {
         return canonicalToStringForHashCode(this.ivalue, this.fvalue, this.sign, this.pvalue).hashCode();
      }

      private int compareFractionalPart(PrecisionDecimalDV.XPrecisionDecimal oval) {
         if (this.fvalue.equals(oval.fvalue)) {
            return 0;
         } else {
            StringBuffer temp1 = new StringBuffer(this.fvalue);
            StringBuffer temp2 = new StringBuffer(oval.fvalue);
            this.truncateTrailingZeros(temp1, temp2);
            return temp1.toString().compareTo(temp2.toString());
         }
      }

      private void truncateTrailingZeros(StringBuffer fValue, StringBuffer otherFValue) {
         int i;
         for(i = fValue.length() - 1; i >= 0 && fValue.charAt(i) == '0'; --i) {
            fValue.deleteCharAt(i);
         }

         for(i = otherFValue.length() - 1; i >= 0 && otherFValue.charAt(i) == '0'; --i) {
            otherFValue.deleteCharAt(i);
         }

      }

      public int compareTo(PrecisionDecimalDV.XPrecisionDecimal val) {
         if (this.sign == 0) {
            return 2;
         } else if (!this.ivalue.equals("INF") && !val.ivalue.equals("INF")) {
            if (!this.ivalue.equals("-INF") && !val.ivalue.equals("-INF")) {
               if (this.sign != val.sign) {
                  return this.sign > val.sign ? 1 : -1;
               } else {
                  return this.sign * this.compare(val);
               }
            } else if (this.ivalue.equals(val.ivalue)) {
               return 0;
            } else {
               return this.ivalue.equals("-INF") ? -1 : 1;
            }
         } else if (this.ivalue.equals(val.ivalue)) {
            return 0;
         } else {
            return this.ivalue.equals("INF") ? 1 : -1;
         }
      }

      private int compare(PrecisionDecimalDV.XPrecisionDecimal val) {
         if (this.pvalue == 0 && val.pvalue == 0) {
            return this.intComp(val);
         } else if (this.pvalue == val.pvalue) {
            return this.intComp(val);
         } else if (this.intDigits + this.pvalue != val.intDigits + val.pvalue) {
            return this.intDigits + this.pvalue > val.intDigits + val.pvalue ? 1 : -1;
         } else {
            int expDiff;
            StringBuffer buffer;
            StringBuffer fbuffer;
            int i;
            if (this.pvalue > val.pvalue) {
               expDiff = this.pvalue - val.pvalue;
               buffer = new StringBuffer(this.ivalue);
               fbuffer = new StringBuffer(this.fvalue);

               for(i = 0; i < expDiff; ++i) {
                  if (i < this.fracDigits) {
                     buffer.append(this.fvalue.charAt(i));
                     fbuffer.deleteCharAt(i);
                  } else {
                     buffer.append('0');
                  }
               }

               return this.compareDecimal(buffer.toString(), val.ivalue, fbuffer.toString(), val.fvalue);
            } else {
               expDiff = val.pvalue - this.pvalue;
               buffer = new StringBuffer(val.ivalue);
               fbuffer = new StringBuffer(val.fvalue);

               for(i = 0; i < expDiff; ++i) {
                  if (i < val.fracDigits) {
                     buffer.append(val.fvalue.charAt(i));
                     fbuffer.deleteCharAt(i);
                  } else {
                     buffer.append('0');
                  }
               }

               return this.compareDecimal(this.ivalue, buffer.toString(), this.fvalue, fbuffer.toString());
            }
         }
      }

      private int intComp(PrecisionDecimalDV.XPrecisionDecimal val) {
         if (this.intDigits != val.intDigits) {
            return this.intDigits > val.intDigits ? 1 : -1;
         } else {
            return this.compareDecimal(this.ivalue, val.ivalue, this.fvalue, val.fvalue);
         }
      }

      private int compareDecimal(String iValue, String fValue, String otherIValue, String otherFValue) {
         int ret = iValue.compareTo(otherIValue);
         if (ret != 0) {
            return ret > 0 ? 1 : -1;
         } else if (fValue.equals(otherFValue)) {
            return 0;
         } else {
            StringBuffer temp1 = new StringBuffer(fValue);
            StringBuffer temp2 = new StringBuffer(otherFValue);
            this.truncateTrailingZeros(temp1, temp2);
            ret = temp1.toString().compareTo(temp2.toString());
            return ret == 0 ? 0 : (ret > 0 ? 1 : -1);
         }
      }

      public synchronized String toString() {
         if (this.canonical == null) {
            this.makeCanonical();
         }

         return this.canonical;
      }

      private void makeCanonical() {
         this.canonical = "TBD by Working Group";
      }

      public boolean isIdentical(PrecisionDecimalDV.XPrecisionDecimal decimal) {
         if (!this.ivalue.equals(decimal.ivalue) || !this.ivalue.equals("INF") && !this.ivalue.equals("-INF") && !this.ivalue.equals("NaN")) {
            return this.sign == decimal.sign && this.intDigits == decimal.intDigits && this.fracDigits == decimal.fracDigits && this.pvalue == decimal.pvalue && this.ivalue.equals(decimal.ivalue) && this.fvalue.equals(decimal.fvalue);
         } else {
            return true;
         }
      }
   }
}
