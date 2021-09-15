package javax.swing.text;

import java.lang.reflect.Constructor;
import java.text.AttributedCharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;

public class NumberFormatter extends InternationalFormatter {
   private String specialChars;

   public NumberFormatter() {
      this(NumberFormat.getNumberInstance());
   }

   public NumberFormatter(NumberFormat var1) {
      super(var1);
      this.setFormat(var1);
      this.setAllowsInvalid(true);
      this.setCommitsOnValidEdit(false);
      this.setOverwriteMode(false);
   }

   public void setFormat(Format var1) {
      super.setFormat(var1);
      DecimalFormatSymbols var2 = this.getDecimalFormatSymbols();
      if (var2 != null) {
         StringBuilder var3 = new StringBuilder();
         var3.append(var2.getCurrencySymbol());
         var3.append(var2.getDecimalSeparator());
         var3.append(var2.getGroupingSeparator());
         var3.append(var2.getInfinity());
         var3.append(var2.getInternationalCurrencySymbol());
         var3.append(var2.getMinusSign());
         var3.append(var2.getMonetaryDecimalSeparator());
         var3.append(var2.getNaN());
         var3.append(var2.getPercent());
         var3.append('+');
         this.specialChars = var3.toString();
      } else {
         this.specialChars = "";
      }

   }

   Object stringToValue(String var1, Format var2) throws ParseException {
      if (var2 == null) {
         return var1;
      } else {
         Object var3 = var2.parseObject(var1);
         return this.convertValueToValueClass(var3, this.getValueClass());
      }
   }

   private Object convertValueToValueClass(Object var1, Class var2) {
      if (var2 != null && var1 instanceof Number) {
         Number var3 = (Number)var1;
         if (var2 == Integer.class) {
            return var3.intValue();
         }

         if (var2 == Long.class) {
            return var3.longValue();
         }

         if (var2 == Float.class) {
            return var3.floatValue();
         }

         if (var2 == Double.class) {
            return var3.doubleValue();
         }

         if (var2 == Byte.class) {
            return var3.byteValue();
         }

         if (var2 == Short.class) {
            return var3.shortValue();
         }
      }

      return var1;
   }

   private char getPositiveSign() {
      return '+';
   }

   private char getMinusSign() {
      DecimalFormatSymbols var1 = this.getDecimalFormatSymbols();
      return var1 != null ? var1.getMinusSign() : '-';
   }

   private char getDecimalSeparator() {
      DecimalFormatSymbols var1 = this.getDecimalFormatSymbols();
      return var1 != null ? var1.getDecimalSeparator() : '.';
   }

   private DecimalFormatSymbols getDecimalFormatSymbols() {
      Format var1 = this.getFormat();
      return var1 instanceof DecimalFormat ? ((DecimalFormat)var1).getDecimalFormatSymbols() : null;
   }

   boolean isLegalInsertText(String var1) {
      if (this.getAllowsInvalid()) {
         return true;
      } else {
         for(int var2 = var1.length() - 1; var2 >= 0; --var2) {
            char var3 = var1.charAt(var2);
            if (!Character.isDigit(var3) && this.specialChars.indexOf(var3) == -1) {
               return false;
            }
         }

         return true;
      }
   }

   boolean isLiteral(Map var1) {
      if (!super.isLiteral(var1)) {
         if (var1 == null) {
            return false;
         } else {
            int var2 = var1.size();
            if (var1.get(NumberFormat.Field.GROUPING_SEPARATOR) != null) {
               --var2;
               if (var1.get(NumberFormat.Field.INTEGER) != null) {
                  --var2;
               }
            }

            if (var1.get(NumberFormat.Field.EXPONENT_SYMBOL) != null) {
               --var2;
            }

            if (var1.get(NumberFormat.Field.PERCENT) != null) {
               --var2;
            }

            if (var1.get(NumberFormat.Field.PERMILLE) != null) {
               --var2;
            }

            if (var1.get(NumberFormat.Field.CURRENCY) != null) {
               --var2;
            }

            if (var1.get(NumberFormat.Field.SIGN) != null) {
               --var2;
            }

            return var2 == 0;
         }
      } else {
         return true;
      }
   }

   boolean isNavigatable(int var1) {
      if (!super.isNavigatable(var1)) {
         return this.getBufferedChar(var1) == this.getDecimalSeparator();
      } else {
         return true;
      }
   }

   private NumberFormat.Field getFieldFrom(int var1, int var2) {
      if (this.isValidMask()) {
         int var3 = this.getFormattedTextField().getDocument().getLength();
         AttributedCharacterIterator var4 = this.getIterator();
         if (var1 >= var3) {
            var1 += var2;
         }

         for(; var1 >= 0 && var1 < var3; var1 += var2) {
            var4.setIndex(var1);
            Map var5 = var4.getAttributes();
            if (var5 != null && var5.size() > 0) {
               Iterator var6 = var5.keySet().iterator();

               while(var6.hasNext()) {
                  Object var7 = var6.next();
                  if (var7 instanceof NumberFormat.Field) {
                     return (NumberFormat.Field)var7;
                  }
               }
            }
         }
      }

      return null;
   }

   void replace(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) throws BadLocationException {
      if (this.getAllowsInvalid() || var3 != 0 || var4 == null || var4.length() != 1 || !this.toggleSignIfNecessary(var1, var2, var4.charAt(0))) {
         super.replace(var1, var2, var3, var4, var5);
      }
   }

   private boolean toggleSignIfNecessary(DocumentFilter.FilterBypass var1, int var2, char var3) throws BadLocationException {
      if (var3 == this.getMinusSign() || var3 == this.getPositiveSign()) {
         NumberFormat.Field var4 = this.getFieldFrom(var2, -1);

         try {
            Object var5;
            if (var4 == null || var4 != NumberFormat.Field.EXPONENT && var4 != NumberFormat.Field.EXPONENT_SYMBOL && var4 != NumberFormat.Field.EXPONENT_SIGN) {
               var5 = this.toggleSign(var3 == this.getPositiveSign());
            } else {
               var5 = this.toggleExponentSign(var2, var3);
            }

            if (var5 != null && this.isValidValue(var5, false)) {
               int var6 = this.getLiteralCountTo(var2);
               String var7 = this.valueToString(var5);
               var1.remove(0, var1.getDocument().getLength());
               var1.insertString(0, var7, (AttributeSet)null);
               this.updateValue(var5);
               this.repositionCursor(this.getLiteralCountTo(var2) - var6 + var2, 1);
               return true;
            }
         } catch (ParseException var8) {
            this.invalidEdit();
         }
      }

      return false;
   }

   private Object toggleSign(boolean var1) throws ParseException {
      Object var2 = this.stringToValue(this.getFormattedTextField().getText());
      if (var2 != null) {
         String var3 = var2.toString();
         if (var3 != null && var3.length() > 0) {
            if (var1) {
               if (var3.charAt(0) == '-') {
                  var3 = var3.substring(1);
               }
            } else {
               if (var3.charAt(0) == '+') {
                  var3 = var3.substring(1);
               }

               if (var3.length() > 0 && var3.charAt(0) != '-') {
                  var3 = "-" + var3;
               }
            }

            if (var3 != null) {
               Class var4 = this.getValueClass();
               if (var4 == null) {
                  var4 = var2.getClass();
               }

               try {
                  ReflectUtil.checkPackageAccess(var4);
                  SwingUtilities2.checkAccess(var4.getModifiers());
                  Constructor var5 = var4.getConstructor(String.class);
                  if (var5 != null) {
                     SwingUtilities2.checkAccess(var5.getModifiers());
                     return var5.newInstance(var3);
                  }
               } catch (Throwable var6) {
               }
            }
         }
      }

      return null;
   }

   private Object toggleExponentSign(int var1, char var2) throws BadLocationException, ParseException {
      String var3 = this.getFormattedTextField().getText();
      byte var4 = 0;
      int var5 = this.getAttributeStart(NumberFormat.Field.EXPONENT_SIGN);
      if (var5 >= 0) {
         var4 = 1;
         var1 = var5;
      }

      if (var2 == this.getPositiveSign()) {
         var3 = this.getReplaceString(var1, var4, (String)null);
      } else {
         var3 = this.getReplaceString(var1, var4, new String(new char[]{var2}));
      }

      return this.stringToValue(var3);
   }
}
