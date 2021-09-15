package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageFormat extends Format {
   private static final long serialVersionUID = 6479157306784022952L;
   private Locale locale;
   private String pattern = "";
   private static final int INITIAL_FORMATS = 10;
   private Format[] formats = new Format[10];
   private int[] offsets = new int[10];
   private int[] argumentNumbers = new int[10];
   private int maxOffset = -1;
   private static final int SEG_RAW = 0;
   private static final int SEG_INDEX = 1;
   private static final int SEG_TYPE = 2;
   private static final int SEG_MODIFIER = 3;
   private static final int TYPE_NULL = 0;
   private static final int TYPE_NUMBER = 1;
   private static final int TYPE_DATE = 2;
   private static final int TYPE_TIME = 3;
   private static final int TYPE_CHOICE = 4;
   private static final String[] TYPE_KEYWORDS = new String[]{"", "number", "date", "time", "choice"};
   private static final int MODIFIER_DEFAULT = 0;
   private static final int MODIFIER_CURRENCY = 1;
   private static final int MODIFIER_PERCENT = 2;
   private static final int MODIFIER_INTEGER = 3;
   private static final String[] NUMBER_MODIFIER_KEYWORDS = new String[]{"", "currency", "percent", "integer"};
   private static final int MODIFIER_SHORT = 1;
   private static final int MODIFIER_MEDIUM = 2;
   private static final int MODIFIER_LONG = 3;
   private static final int MODIFIER_FULL = 4;
   private static final String[] DATE_TIME_MODIFIER_KEYWORDS = new String[]{"", "short", "medium", "long", "full"};
   private static final int[] DATE_TIME_MODIFIERS = new int[]{2, 3, 2, 1, 0};

   public MessageFormat(String var1) {
      this.locale = Locale.getDefault(Locale.Category.FORMAT);
      this.applyPattern(var1);
   }

   public MessageFormat(String var1, Locale var2) {
      this.locale = var2;
      this.applyPattern(var1);
   }

   public void setLocale(Locale var1) {
      this.locale = var1;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public void applyPattern(String var1) {
      StringBuilder[] var2 = new StringBuilder[4];
      var2[0] = new StringBuilder();
      int var3 = 0;
      int var4 = 0;
      boolean var5 = false;
      int var6 = 0;
      this.maxOffset = -1;

      for(int var7 = 0; var7 < var1.length(); ++var7) {
         char var8 = var1.charAt(var7);
         if (var3 == 0) {
            if (var8 == '\'') {
               if (var7 + 1 < var1.length() && var1.charAt(var7 + 1) == '\'') {
                  var2[var3].append(var8);
                  ++var7;
               } else {
                  var5 = !var5;
               }
            } else if (var8 == '{' && !var5) {
               var3 = 1;
               if (var2[1] == null) {
                  var2[1] = new StringBuilder();
               }
            } else {
               var2[var3].append(var8);
            }
         } else if (var5) {
            var2[var3].append(var8);
            if (var8 == '\'') {
               var5 = false;
            }
         } else {
            switch(var8) {
            case ' ':
               if (var3 != 2 || var2[2].length() > 0) {
                  var2[var3].append(var8);
               }
               break;
            case '\'':
               var5 = true;
            default:
               var2[var3].append(var8);
               break;
            case ',':
               if (var3 < 3) {
                  ++var3;
                  if (var2[var3] == null) {
                     var2[var3] = new StringBuilder();
                  }
               } else {
                  var2[var3].append(var8);
               }
               break;
            case '{':
               ++var6;
               var2[var3].append(var8);
               break;
            case '}':
               if (var6 == 0) {
                  var3 = 0;
                  this.makeFormat(var7, var4, var2);
                  ++var4;
                  var2[1] = null;
                  var2[2] = null;
                  var2[3] = null;
               } else {
                  --var6;
                  var2[var3].append(var8);
               }
            }
         }
      }

      if (var6 == 0 && var3 != 0) {
         this.maxOffset = -1;
         throw new IllegalArgumentException("Unmatched braces in the pattern.");
      } else {
         this.pattern = var2[0].toString();
      }
   }

   public String toPattern() {
      int var1 = 0;
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 <= this.maxOffset; ++var3) {
         copyAndFixQuotes(this.pattern, var1, this.offsets[var3], var2);
         var1 = this.offsets[var3];
         var2.append('{').append(this.argumentNumbers[var3]);
         Format var4 = this.formats[var3];
         if (var4 != null) {
            if (var4 instanceof NumberFormat) {
               if (var4.equals(NumberFormat.getInstance(this.locale))) {
                  var2.append(",number");
               } else if (var4.equals(NumberFormat.getCurrencyInstance(this.locale))) {
                  var2.append(",number,currency");
               } else if (var4.equals(NumberFormat.getPercentInstance(this.locale))) {
                  var2.append(",number,percent");
               } else if (var4.equals(NumberFormat.getIntegerInstance(this.locale))) {
                  var2.append(",number,integer");
               } else if (var4 instanceof DecimalFormat) {
                  var2.append(",number,").append(((DecimalFormat)var4).toPattern());
               } else if (var4 instanceof ChoiceFormat) {
                  var2.append(",choice,").append(((ChoiceFormat)var4).toPattern());
               }
            } else if (var4 instanceof DateFormat) {
               int var5;
               for(var5 = 0; var5 < DATE_TIME_MODIFIERS.length; ++var5) {
                  DateFormat var6 = DateFormat.getDateInstance(DATE_TIME_MODIFIERS[var5], this.locale);
                  if (var4.equals(var6)) {
                     var2.append(",date");
                     break;
                  }

                  var6 = DateFormat.getTimeInstance(DATE_TIME_MODIFIERS[var5], this.locale);
                  if (var4.equals(var6)) {
                     var2.append(",time");
                     break;
                  }
               }

               if (var5 >= DATE_TIME_MODIFIERS.length) {
                  if (var4 instanceof SimpleDateFormat) {
                     var2.append(",date,").append(((SimpleDateFormat)var4).toPattern());
                  }
               } else if (var5 != 0) {
                  var2.append(',').append(DATE_TIME_MODIFIER_KEYWORDS[var5]);
               }
            }
         }

         var2.append('}');
      }

      copyAndFixQuotes(this.pattern, var1, this.pattern.length(), var2);
      return var2.toString();
   }

   public void setFormatsByArgumentIndex(Format[] var1) {
      for(int var2 = 0; var2 <= this.maxOffset; ++var2) {
         int var3 = this.argumentNumbers[var2];
         if (var3 < var1.length) {
            this.formats[var2] = var1[var3];
         }
      }

   }

   public void setFormats(Format[] var1) {
      int var2 = var1.length;
      if (var2 > this.maxOffset + 1) {
         var2 = this.maxOffset + 1;
      }

      for(int var3 = 0; var3 < var2; ++var3) {
         this.formats[var3] = var1[var3];
      }

   }

   public void setFormatByArgumentIndex(int var1, Format var2) {
      for(int var3 = 0; var3 <= this.maxOffset; ++var3) {
         if (this.argumentNumbers[var3] == var1) {
            this.formats[var3] = var2;
         }
      }

   }

   public void setFormat(int var1, Format var2) {
      this.formats[var1] = var2;
   }

   public Format[] getFormatsByArgumentIndex() {
      int var1 = -1;

      for(int var2 = 0; var2 <= this.maxOffset; ++var2) {
         if (this.argumentNumbers[var2] > var1) {
            var1 = this.argumentNumbers[var2];
         }
      }

      Format[] var4 = new Format[var1 + 1];

      for(int var3 = 0; var3 <= this.maxOffset; ++var3) {
         var4[this.argumentNumbers[var3]] = this.formats[var3];
      }

      return var4;
   }

   public Format[] getFormats() {
      Format[] var1 = new Format[this.maxOffset + 1];
      System.arraycopy(this.formats, 0, var1, 0, this.maxOffset + 1);
      return var1;
   }

   public final StringBuffer format(Object[] var1, StringBuffer var2, FieldPosition var3) {
      return this.subformat(var1, var2, var3, (List)null);
   }

   public static String format(String var0, Object... var1) {
      MessageFormat var2 = new MessageFormat(var0);
      return var2.format(var1);
   }

   public final StringBuffer format(Object var1, StringBuffer var2, FieldPosition var3) {
      return this.subformat((Object[])((Object[])var1), var2, var3, (List)null);
   }

   public AttributedCharacterIterator formatToCharacterIterator(Object var1) {
      StringBuffer var2 = new StringBuffer();
      ArrayList var3 = new ArrayList();
      if (var1 == null) {
         throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
      } else {
         this.subformat((Object[])((Object[])var1), var2, (FieldPosition)null, var3);
         return var3.size() == 0 ? this.createAttributedCharacterIterator("") : this.createAttributedCharacterIterator((AttributedCharacterIterator[])var3.toArray(new AttributedCharacterIterator[var3.size()]));
      }
   }

   public Object[] parse(String var1, ParsePosition var2) {
      if (var1 == null) {
         Object[] var13 = new Object[0];
         return var13;
      } else {
         int var3 = -1;

         for(int var4 = 0; var4 <= this.maxOffset; ++var4) {
            if (this.argumentNumbers[var4] > var3) {
               var3 = this.argumentNumbers[var4];
            }
         }

         Object[] var14 = new Object[var3 + 1];
         int var5 = 0;
         int var6 = var2.index;
         ParsePosition var7 = new ParsePosition(0);

         int var8;
         for(var8 = 0; var8 <= this.maxOffset; ++var8) {
            int var9 = this.offsets[var8] - var5;
            if (var9 != 0 && !this.pattern.regionMatches(var5, var1, var6, var9)) {
               var2.errorIndex = var6;
               return null;
            }

            var6 += var9;
            var5 += var9;
            if (this.formats[var8] == null) {
               int var10 = var8 != this.maxOffset ? this.offsets[var8 + 1] : this.pattern.length();
               int var11;
               if (var5 >= var10) {
                  var11 = var1.length();
               } else {
                  var11 = var1.indexOf(this.pattern.substring(var5, var10), var6);
               }

               if (var11 < 0) {
                  var2.errorIndex = var6;
                  return null;
               }

               String var12 = var1.substring(var6, var11);
               if (!var12.equals("{" + this.argumentNumbers[var8] + "}")) {
                  var14[this.argumentNumbers[var8]] = var1.substring(var6, var11);
               }

               var6 = var11;
            } else {
               var7.index = var6;
               var14[this.argumentNumbers[var8]] = this.formats[var8].parseObject(var1, var7);
               if (var7.index == var6) {
                  var2.errorIndex = var6;
                  return null;
               }

               var6 = var7.index;
            }
         }

         var8 = this.pattern.length() - var5;
         if (var8 != 0 && !this.pattern.regionMatches(var5, var1, var6, var8)) {
            var2.errorIndex = var6;
            return null;
         } else {
            var2.index = var6 + var8;
            return var14;
         }
      }
   }

   public Object[] parse(String var1) throws ParseException {
      ParsePosition var2 = new ParsePosition(0);
      Object[] var3 = this.parse(var1, var2);
      if (var2.index == 0) {
         throw new ParseException("MessageFormat parse error!", var2.errorIndex);
      } else {
         return var3;
      }
   }

   public Object parseObject(String var1, ParsePosition var2) {
      return this.parse(var1, var2);
   }

   public Object clone() {
      MessageFormat var1 = (MessageFormat)super.clone();
      var1.formats = (Format[])this.formats.clone();

      for(int var2 = 0; var2 < this.formats.length; ++var2) {
         if (this.formats[var2] != null) {
            var1.formats[var2] = (Format)this.formats[var2].clone();
         }
      }

      var1.offsets = (int[])this.offsets.clone();
      var1.argumentNumbers = (int[])this.argumentNumbers.clone();
      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MessageFormat var2 = (MessageFormat)var1;
         return this.maxOffset == var2.maxOffset && this.pattern.equals(var2.pattern) && (this.locale != null && this.locale.equals(var2.locale) || this.locale == null && var2.locale == null) && Arrays.equals(this.offsets, var2.offsets) && Arrays.equals(this.argumentNumbers, var2.argumentNumbers) && Arrays.equals((Object[])this.formats, (Object[])var2.formats);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.pattern.hashCode();
   }

   private StringBuffer subformat(Object[] var1, StringBuffer var2, FieldPosition var3, List<AttributedCharacterIterator> var4) {
      int var5 = 0;
      int var6 = var2.length();

      for(int var7 = 0; var7 <= this.maxOffset; ++var7) {
         var2.append(this.pattern.substring(var5, this.offsets[var7]));
         var5 = this.offsets[var7];
         int var8 = this.argumentNumbers[var7];
         if (var1 != null && var8 < var1.length) {
            Object var9 = var1[var8];
            String var10 = null;
            Object var11 = null;
            if (var9 == null) {
               var10 = "null";
            } else if (this.formats[var7] != null) {
               var11 = this.formats[var7];
               if (var11 instanceof ChoiceFormat) {
                  var10 = this.formats[var7].format(var9);
                  if (var10.indexOf(123) >= 0) {
                     var11 = new MessageFormat(var10, this.locale);
                     var9 = var1;
                     var10 = null;
                  }
               }
            } else if (var9 instanceof Number) {
               var11 = NumberFormat.getInstance(this.locale);
            } else if (var9 instanceof Date) {
               var11 = DateFormat.getDateTimeInstance(3, 3, this.locale);
            } else if (var9 instanceof String) {
               var10 = (String)var9;
            } else {
               var10 = var9.toString();
               if (var10 == null) {
                  var10 = "null";
               }
            }

            if (var4 != null) {
               if (var6 != var2.length()) {
                  var4.add(this.createAttributedCharacterIterator(var2.substring(var6)));
                  var6 = var2.length();
               }

               if (var11 != null) {
                  AttributedCharacterIterator var12 = ((Format)var11).formatToCharacterIterator(var9);
                  this.append(var2, var12);
                  if (var6 != var2.length()) {
                     var4.add(this.createAttributedCharacterIterator(var12, MessageFormat.Field.ARGUMENT, var8));
                     var6 = var2.length();
                  }

                  var10 = null;
               }

               if (var10 != null && var10.length() > 0) {
                  var2.append(var10);
                  var4.add(this.createAttributedCharacterIterator(var10, MessageFormat.Field.ARGUMENT, var8));
                  var6 = var2.length();
               }
            } else {
               if (var11 != null) {
                  var10 = ((Format)var11).format(var9);
               }

               var6 = var2.length();
               var2.append(var10);
               if (var7 == 0 && var3 != null && MessageFormat.Field.ARGUMENT.equals(var3.getFieldAttribute())) {
                  var3.setBeginIndex(var6);
                  var3.setEndIndex(var2.length());
               }

               var6 = var2.length();
            }
         } else {
            var2.append('{').append(var8).append('}');
         }
      }

      var2.append(this.pattern.substring(var5, this.pattern.length()));
      if (var4 != null && var6 != var2.length()) {
         var4.add(this.createAttributedCharacterIterator(var2.substring(var6)));
      }

      return var2;
   }

   private void append(StringBuffer var1, CharacterIterator var2) {
      if (var2.first() != '\uffff') {
         var1.append(var2.first());

         char var3;
         while((var3 = var2.next()) != '\uffff') {
            var1.append(var3);
         }
      }

   }

   private void makeFormat(int var1, int var2, StringBuilder[] var3) {
      String[] var4 = new String[var3.length];

      int var5;
      for(var5 = 0; var5 < var3.length; ++var5) {
         StringBuilder var6 = var3[var5];
         var4[var5] = var6 != null ? var6.toString() : "";
      }

      try {
         var5 = Integer.parseInt(var4[1]);
      } catch (NumberFormatException var14) {
         throw new IllegalArgumentException("can't parse argument number: " + var4[1], var14);
      }

      if (var5 < 0) {
         throw new IllegalArgumentException("negative argument number: " + var5);
      } else {
         int var15;
         if (var2 >= this.formats.length) {
            var15 = this.formats.length * 2;
            Format[] var7 = new Format[var15];
            int[] var8 = new int[var15];
            int[] var9 = new int[var15];
            System.arraycopy(this.formats, 0, var7, 0, this.maxOffset + 1);
            System.arraycopy(this.offsets, 0, var8, 0, this.maxOffset + 1);
            System.arraycopy(this.argumentNumbers, 0, var9, 0, this.maxOffset + 1);
            this.formats = var7;
            this.offsets = var8;
            this.argumentNumbers = var9;
         }

         var15 = this.maxOffset;
         this.maxOffset = var2;
         this.offsets[var2] = var4[0].length();
         this.argumentNumbers[var2] = var5;
         Object var16 = null;
         if (var4[2].length() != 0) {
            int var17 = findKeyword(var4[2], TYPE_KEYWORDS);
            label59:
            switch(var17) {
            case 0:
               break;
            case 1:
               switch(findKeyword(var4[3], NUMBER_MODIFIER_KEYWORDS)) {
               case 0:
                  var16 = NumberFormat.getInstance(this.locale);
                  break label59;
               case 1:
                  var16 = NumberFormat.getCurrencyInstance(this.locale);
                  break label59;
               case 2:
                  var16 = NumberFormat.getPercentInstance(this.locale);
                  break label59;
               case 3:
                  var16 = NumberFormat.getIntegerInstance(this.locale);
                  break label59;
               default:
                  try {
                     var16 = new DecimalFormat(var4[3], DecimalFormatSymbols.getInstance(this.locale));
                     break label59;
                  } catch (IllegalArgumentException var13) {
                     this.maxOffset = var15;
                     throw var13;
                  }
               }
            case 2:
            case 3:
               int var18 = findKeyword(var4[3], DATE_TIME_MODIFIER_KEYWORDS);
               if (var18 >= 0 && var18 < DATE_TIME_MODIFIER_KEYWORDS.length) {
                  if (var17 == 2) {
                     var16 = DateFormat.getDateInstance(DATE_TIME_MODIFIERS[var18], this.locale);
                  } else {
                     var16 = DateFormat.getTimeInstance(DATE_TIME_MODIFIERS[var18], this.locale);
                  }
               } else {
                  try {
                     var16 = new SimpleDateFormat(var4[3], this.locale);
                  } catch (IllegalArgumentException var12) {
                     this.maxOffset = var15;
                     throw var12;
                  }
               }
               break;
            case 4:
               try {
                  var16 = new ChoiceFormat(var4[3]);
                  break;
               } catch (Exception var11) {
                  this.maxOffset = var15;
                  throw new IllegalArgumentException("Choice Pattern incorrect: " + var4[3], var11);
               }
            default:
               this.maxOffset = var15;
               throw new IllegalArgumentException("unknown format type: " + var4[2]);
            }
         }

         this.formats[var2] = (Format)var16;
      }
   }

   private static final int findKeyword(String var0, String[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var0.equals(var1[var2])) {
            return var2;
         }
      }

      String var4 = var0.trim().toLowerCase(Locale.ROOT);
      if (var4 != var0) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var4.equals(var1[var3])) {
               return var3;
            }
         }
      }

      return -1;
   }

   private static final void copyAndFixQuotes(String var0, int var1, int var2, StringBuilder var3) {
      boolean var4 = false;

      for(int var5 = var1; var5 < var2; ++var5) {
         char var6 = var0.charAt(var5);
         if (var6 == '{') {
            if (!var4) {
               var3.append('\'');
               var4 = true;
            }

            var3.append(var6);
         } else if (var6 == '\'') {
            var3.append("''");
         } else {
            if (var4) {
               var3.append('\'');
               var4 = false;
            }

            var3.append(var6);
         }
      }

      if (var4) {
         var3.append('\'');
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      boolean var2 = this.maxOffset >= -1 && this.formats.length > this.maxOffset && this.offsets.length > this.maxOffset && this.argumentNumbers.length > this.maxOffset;
      if (var2) {
         int var3 = this.pattern.length() + 1;

         for(int var4 = this.maxOffset; var4 >= 0; --var4) {
            if (this.offsets[var4] < 0 || this.offsets[var4] > var3) {
               var2 = false;
               break;
            }

            var3 = this.offsets[var4];
         }
      }

      if (!var2) {
         throw new InvalidObjectException("Could not reconstruct MessageFormat from corrupt stream.");
      }
   }

   public static class Field extends Format.Field {
      private static final long serialVersionUID = 7899943957617360810L;
      public static final MessageFormat.Field ARGUMENT = new MessageFormat.Field("message argument field");

      protected Field(String var1) {
         super(var1);
      }

      protected Object readResolve() throws InvalidObjectException {
         if (this.getClass() != MessageFormat.Field.class) {
            throw new InvalidObjectException("subclass didn't correctly implement readResolve");
         } else {
            return ARGUMENT;
         }
      }
   }
}
