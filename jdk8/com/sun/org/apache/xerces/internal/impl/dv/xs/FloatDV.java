package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSFloat;

public class FloatDV extends TypeValidator {
   public short getAllowedFacets() {
      return 2552;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return new FloatDV.XFloat(content);
      } catch (NumberFormatException var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "float"});
      }
   }

   public int compare(Object value1, Object value2) {
      return ((FloatDV.XFloat)value1).compareTo((FloatDV.XFloat)value2);
   }

   public boolean isIdentical(Object value1, Object value2) {
      return value2 instanceof FloatDV.XFloat ? ((FloatDV.XFloat)value1).isIdentical((FloatDV.XFloat)value2) : false;
   }

   private static final class XFloat implements XSFloat {
      private final float value;
      private String canonical;

      public XFloat(String s) throws NumberFormatException {
         if (DoubleDV.isPossibleFP(s)) {
            this.value = Float.parseFloat(s);
         } else if (s.equals("INF")) {
            this.value = Float.POSITIVE_INFINITY;
         } else if (s.equals("-INF")) {
            this.value = Float.NEGATIVE_INFINITY;
         } else {
            if (!s.equals("NaN")) {
               throw new NumberFormatException(s);
            }

            this.value = Float.NaN;
         }

      }

      public boolean equals(Object val) {
         if (val == this) {
            return true;
         } else if (!(val instanceof FloatDV.XFloat)) {
            return false;
         } else {
            FloatDV.XFloat oval = (FloatDV.XFloat)val;
            if (this.value == oval.value) {
               return true;
            } else {
               return this.value != this.value && oval.value != oval.value;
            }
         }
      }

      public int hashCode() {
         return this.value == 0.0F ? 0 : Float.floatToIntBits(this.value);
      }

      public boolean isIdentical(FloatDV.XFloat val) {
         if (val == this) {
            return true;
         } else if (this.value != val.value) {
            return this.value != this.value && val.value != val.value;
         } else {
            return this.value != 0.0F || Float.floatToIntBits(this.value) == Float.floatToIntBits(val.value);
         }
      }

      private int compareTo(FloatDV.XFloat val) {
         float oval = val.value;
         if (this.value < oval) {
            return -1;
         } else if (this.value > oval) {
            return 1;
         } else if (this.value == oval) {
            return 0;
         } else if (this.value != this.value) {
            return oval != oval ? 0 : 2;
         } else {
            return 2;
         }
      }

      public synchronized String toString() {
         if (this.canonical == null) {
            if (this.value == Float.POSITIVE_INFINITY) {
               this.canonical = "INF";
            } else if (this.value == Float.NEGATIVE_INFINITY) {
               this.canonical = "-INF";
            } else if (this.value != this.value) {
               this.canonical = "NaN";
            } else if (this.value == 0.0F) {
               this.canonical = "0.0E1";
            } else {
               this.canonical = Float.toString(this.value);
               if (this.canonical.indexOf(69) == -1) {
                  int len = this.canonical.length();
                  char[] chars = new char[len + 3];
                  this.canonical.getChars(0, len, chars, 0);
                  int edp = chars[0] == '-' ? 2 : 1;
                  int dp;
                  int shift;
                  if (this.value < 1.0F && this.value > -1.0F) {
                     for(dp = edp + 1; chars[dp] == '0'; ++dp) {
                     }

                     chars[edp - 1] = chars[dp];
                     chars[edp] = '.';
                     shift = dp + 1;

                     for(int j = edp + 1; shift < len; ++j) {
                        chars[j] = chars[shift];
                        ++shift;
                     }

                     len -= dp - edp;
                     if (len == edp + 1) {
                        chars[len++] = '0';
                     }

                     chars[len++] = 'E';
                     chars[len++] = '-';
                     shift = dp - edp;
                     chars[len++] = (char)(shift + 48);
                  } else {
                     dp = this.canonical.indexOf(46);

                     for(shift = dp; shift > edp; --shift) {
                        chars[shift] = chars[shift - 1];
                     }

                     for(chars[edp] = '.'; chars[len - 1] == '0'; --len) {
                     }

                     if (chars[len - 1] == '.') {
                        ++len;
                     }

                     chars[len++] = 'E';
                     shift = dp - edp;
                     chars[len++] = (char)(shift + 48);
                  }

                  this.canonical = new String(chars, 0, len);
               }
            }
         }

         return this.canonical;
      }

      public float getValue() {
         return this.value;
      }
   }
}
