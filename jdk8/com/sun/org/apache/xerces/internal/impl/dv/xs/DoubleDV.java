package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDouble;

public class DoubleDV extends TypeValidator {
   public short getAllowedFacets() {
      return 2552;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return new DoubleDV.XDouble(content);
      } catch (NumberFormatException var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "double"});
      }
   }

   public int compare(Object value1, Object value2) {
      return ((DoubleDV.XDouble)value1).compareTo((DoubleDV.XDouble)value2);
   }

   public boolean isIdentical(Object value1, Object value2) {
      return value2 instanceof DoubleDV.XDouble ? ((DoubleDV.XDouble)value1).isIdentical((DoubleDV.XDouble)value2) : false;
   }

   static boolean isPossibleFP(String val) {
      int length = val.length();

      for(int i = 0; i < length; ++i) {
         char c = val.charAt(i);
         if ((c < '0' || c > '9') && c != '.' && c != '-' && c != '+' && c != 'E' && c != 'e') {
            return false;
         }
      }

      return true;
   }

   private static final class XDouble implements XSDouble {
      private final double value;
      private String canonical;

      public XDouble(String s) throws NumberFormatException {
         if (DoubleDV.isPossibleFP(s)) {
            this.value = Double.parseDouble(s);
         } else if (s.equals("INF")) {
            this.value = Double.POSITIVE_INFINITY;
         } else if (s.equals("-INF")) {
            this.value = Double.NEGATIVE_INFINITY;
         } else {
            if (!s.equals("NaN")) {
               throw new NumberFormatException(s);
            }

            this.value = Double.NaN;
         }

      }

      public boolean equals(Object val) {
         if (val == this) {
            return true;
         } else if (!(val instanceof DoubleDV.XDouble)) {
            return false;
         } else {
            DoubleDV.XDouble oval = (DoubleDV.XDouble)val;
            if (this.value == oval.value) {
               return true;
            } else {
               return this.value != this.value && oval.value != oval.value;
            }
         }
      }

      public int hashCode() {
         if (this.value == 0.0D) {
            return 0;
         } else {
            long v = Double.doubleToLongBits(this.value);
            return (int)(v ^ v >>> 32);
         }
      }

      public boolean isIdentical(DoubleDV.XDouble val) {
         if (val == this) {
            return true;
         } else if (this.value != val.value) {
            return this.value != this.value && val.value != val.value;
         } else {
            return this.value != 0.0D || Double.doubleToLongBits(this.value) == Double.doubleToLongBits(val.value);
         }
      }

      private int compareTo(DoubleDV.XDouble val) {
         double oval = val.value;
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
            if (this.value == Double.POSITIVE_INFINITY) {
               this.canonical = "INF";
            } else if (this.value == Double.NEGATIVE_INFINITY) {
               this.canonical = "-INF";
            } else if (this.value != this.value) {
               this.canonical = "NaN";
            } else if (this.value == 0.0D) {
               this.canonical = "0.0E1";
            } else {
               this.canonical = Double.toString(this.value);
               if (this.canonical.indexOf(69) == -1) {
                  int len = this.canonical.length();
                  char[] chars = new char[len + 3];
                  this.canonical.getChars(0, len, chars, 0);
                  int edp = chars[0] == '-' ? 2 : 1;
                  int dp;
                  int shift;
                  if (this.value < 1.0D && this.value > -1.0D) {
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

      public double getValue() {
         return this.value;
      }
   }
}
