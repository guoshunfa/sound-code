package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JFormattedTextField;

public class MaskFormatter extends DefaultFormatter {
   private static final char DIGIT_KEY = '#';
   private static final char LITERAL_KEY = '\'';
   private static final char UPPERCASE_KEY = 'U';
   private static final char LOWERCASE_KEY = 'L';
   private static final char ALPHA_NUMERIC_KEY = 'A';
   private static final char CHARACTER_KEY = '?';
   private static final char ANYTHING_KEY = '*';
   private static final char HEX_KEY = 'H';
   private static final MaskFormatter.MaskCharacter[] EmptyMaskChars = new MaskFormatter.MaskCharacter[0];
   private String mask;
   private transient MaskFormatter.MaskCharacter[] maskChars;
   private String validCharacters;
   private String invalidCharacters;
   private String placeholderString;
   private char placeholder;
   private boolean containsLiteralChars;

   public MaskFormatter() {
      this.setAllowsInvalid(false);
      this.containsLiteralChars = true;
      this.maskChars = EmptyMaskChars;
      this.placeholder = ' ';
   }

   public MaskFormatter(String var1) throws ParseException {
      this();
      this.setMask(var1);
   }

   public void setMask(String var1) throws ParseException {
      this.mask = var1;
      this.updateInternalMask();
   }

   public String getMask() {
      return this.mask;
   }

   public void setValidCharacters(String var1) {
      this.validCharacters = var1;
   }

   public String getValidCharacters() {
      return this.validCharacters;
   }

   public void setInvalidCharacters(String var1) {
      this.invalidCharacters = var1;
   }

   public String getInvalidCharacters() {
      return this.invalidCharacters;
   }

   public void setPlaceholder(String var1) {
      this.placeholderString = var1;
   }

   public String getPlaceholder() {
      return this.placeholderString;
   }

   public void setPlaceholderCharacter(char var1) {
      this.placeholder = var1;
   }

   public char getPlaceholderCharacter() {
      return this.placeholder;
   }

   public void setValueContainsLiteralCharacters(boolean var1) {
      this.containsLiteralChars = var1;
   }

   public boolean getValueContainsLiteralCharacters() {
      return this.containsLiteralChars;
   }

   public Object stringToValue(String var1) throws ParseException {
      return this.stringToValue(var1, true);
   }

   public String valueToString(Object var1) throws ParseException {
      String var2 = var1 == null ? "" : var1.toString();
      StringBuilder var3 = new StringBuilder();
      String var4 = this.getPlaceholder();
      int[] var5 = new int[]{0};
      this.append(var3, var2, var5, var4, this.maskChars);
      return var3.toString();
   }

   public void install(JFormattedTextField var1) {
      super.install(var1);
      if (var1 != null) {
         Object var2 = var1.getValue();

         try {
            this.stringToValue(this.valueToString(var2));
         } catch (ParseException var4) {
            this.setEditValid(false);
         }
      }

   }

   private Object stringToValue(String var1, boolean var2) throws ParseException {
      int var3;
      if ((var3 = this.getInvalidOffset(var1, var2)) == -1) {
         if (!this.getValueContainsLiteralCharacters()) {
            var1 = this.stripLiteralChars(var1);
         }

         return super.stringToValue(var1);
      } else {
         throw new ParseException("stringToValue passed invalid value", var3);
      }
   }

   private int getInvalidOffset(String var1, boolean var2) {
      int var3 = var1.length();
      if (var3 != this.getMaxLength()) {
         return var3;
      } else {
         int var4 = 0;

         for(int var5 = var1.length(); var4 < var5; ++var4) {
            char var6 = var1.charAt(var4);
            if (!this.isValidCharacter(var4, var6) && (var2 || !this.isPlaceholder(var4, var6))) {
               return var4;
            }
         }

         return -1;
      }
   }

   private void append(StringBuilder var1, String var2, int[] var3, String var4, MaskFormatter.MaskCharacter[] var5) throws ParseException {
      int var6 = 0;

      for(int var7 = var5.length; var6 < var7; ++var6) {
         var5[var6].append(var1, var2, var3, var4);
      }

   }

   private void updateInternalMask() throws ParseException {
      String var1 = this.getMask();
      ArrayList var2 = new ArrayList();
      ArrayList var3 = var2;
      if (var1 != null) {
         int var4 = 0;

         for(int var5 = var1.length(); var4 < var5; ++var4) {
            char var6 = var1.charAt(var4);
            switch(var6) {
            case '#':
               var3.add(new MaskFormatter.DigitMaskCharacter());
               break;
            case '\'':
               ++var4;
               if (var4 < var5) {
                  var6 = var1.charAt(var4);
                  var3.add(new MaskFormatter.LiteralCharacter(var6));
               }
               break;
            case '*':
               var3.add(new MaskFormatter.MaskCharacter());
               break;
            case '?':
               var3.add(new MaskFormatter.CharCharacter());
               break;
            case 'A':
               var3.add(new MaskFormatter.AlphaNumericCharacter());
               break;
            case 'H':
               var3.add(new MaskFormatter.HexCharacter());
               break;
            case 'L':
               var3.add(new MaskFormatter.LowerCaseCharacter());
               break;
            case 'U':
               var3.add(new MaskFormatter.UpperCaseCharacter());
               break;
            default:
               var3.add(new MaskFormatter.LiteralCharacter(var6));
            }
         }
      }

      if (var2.size() == 0) {
         this.maskChars = EmptyMaskChars;
      } else {
         this.maskChars = new MaskFormatter.MaskCharacter[var2.size()];
         var2.toArray(this.maskChars);
      }

   }

   private MaskFormatter.MaskCharacter getMaskCharacter(int var1) {
      return var1 >= this.maskChars.length ? null : this.maskChars[var1];
   }

   private boolean isPlaceholder(int var1, char var2) {
      return this.getPlaceholderCharacter() == var2;
   }

   private boolean isValidCharacter(int var1, char var2) {
      return this.getMaskCharacter(var1).isValidCharacter(var2);
   }

   private boolean isLiteral(int var1) {
      return this.getMaskCharacter(var1).isLiteral();
   }

   private int getMaxLength() {
      return this.maskChars.length;
   }

   private char getLiteral(int var1) {
      return this.getMaskCharacter(var1).getChar('\u0000');
   }

   private char getCharacter(int var1, char var2) {
      return this.getMaskCharacter(var1).getChar(var2);
   }

   private String stripLiteralChars(String var1) {
      StringBuilder var2 = null;
      int var3 = 0;
      int var4 = 0;

      for(int var5 = var1.length(); var4 < var5; ++var4) {
         if (this.isLiteral(var4)) {
            if (var2 == null) {
               var2 = new StringBuilder();
               if (var4 > 0) {
                  var2.append(var1.substring(0, var4));
               }

               var3 = var4 + 1;
            } else if (var3 != var4) {
               var2.append(var1.substring(var3, var4));
            }

            var3 = var4 + 1;
         }
      }

      if (var2 == null) {
         return var1;
      } else {
         if (var3 != var1.length()) {
            if (var2 == null) {
               return var1.substring(var3);
            }

            var2.append(var1.substring(var3));
         }

         return var2.toString();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.updateInternalMask();
      } catch (ParseException var3) {
      }

   }

   boolean isNavigatable(int var1) {
      if (this.getAllowsInvalid()) {
         return true;
      } else {
         return var1 < this.getMaxLength() && !this.isLiteral(var1);
      }
   }

   boolean isValidEdit(DefaultFormatter.ReplaceHolder var1) {
      if (!this.getAllowsInvalid()) {
         String var2 = this.getReplaceString(var1.offset, var1.length, var1.text);

         try {
            var1.value = this.stringToValue(var2, false);
            return true;
         } catch (ParseException var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   boolean canReplace(DefaultFormatter.ReplaceHolder var1) {
      if (!this.getAllowsInvalid()) {
         StringBuilder var2 = null;
         String var3 = var1.text;
         int var4 = var3 != null ? var3.length() : 0;
         if (var4 == 0 && var1.length == 1 && this.getFormattedTextField().getSelectionStart() != var1.offset) {
            while(var1.offset > 0 && this.isLiteral(var1.offset)) {
               --var1.offset;
            }
         }

         int var5 = Math.min(this.getMaxLength() - var1.offset, Math.max(var4, var1.length));
         int var6 = 0;

         for(int var7 = 0; var6 < var5; ++var6) {
            if (var7 < var4 && this.isValidCharacter(var1.offset + var6, var3.charAt(var7))) {
               char var8 = var3.charAt(var7);
               if (var8 != this.getCharacter(var1.offset + var6, var8) && var2 == null) {
                  var2 = new StringBuilder();
                  if (var7 > 0) {
                     var2.append(var3.substring(0, var7));
                  }
               }

               if (var2 != null) {
                  var2.append(this.getCharacter(var1.offset + var6, var8));
               }

               ++var7;
            } else if (this.isLiteral(var1.offset + var6)) {
               if (var2 != null) {
                  var2.append(this.getLiteral(var1.offset + var6));
                  if (var7 < var4) {
                     var5 = Math.min(var5 + 1, this.getMaxLength() - var1.offset);
                  }
               } else if (var7 > 0) {
                  var2 = new StringBuilder(var5);
                  var2.append(var3.substring(0, var7));
                  var2.append(this.getLiteral(var1.offset + var6));
                  if (var7 < var4) {
                     var5 = Math.min(var5 + 1, this.getMaxLength() - var1.offset);
                  } else if (var1.cursorPosition == -1) {
                     var1.cursorPosition = var1.offset + var6;
                  }
               } else {
                  ++var1.offset;
                  --var1.length;
                  --var6;
                  --var5;
               }
            } else {
               if (var7 < var4) {
                  return false;
               }

               if (var2 == null) {
                  var2 = new StringBuilder();
                  if (var3 != null) {
                     var2.append(var3);
                  }
               }

               var2.append(this.getPlaceholderCharacter());
               if (var4 > 0 && var1.cursorPosition == -1) {
                  var1.cursorPosition = var1.offset + var6;
               }
            }
         }

         if (var2 != null) {
            var1.text = var2.toString();
         } else if (var3 != null && var1.offset + var4 > this.getMaxLength()) {
            var1.text = var3.substring(0, this.getMaxLength() - var1.offset);
         }

         if (this.getOverwriteMode() && var1.text != null) {
            var1.length = var1.text.length();
         }
      }

      return super.canReplace(var1);
   }

   private class HexCharacter extends MaskFormatter.MaskCharacter {
      private HexCharacter() {
         super(null);
      }

      public boolean isValidCharacter(char var1) {
         return (var1 == '0' || var1 == '1' || var1 == '2' || var1 == '3' || var1 == '4' || var1 == '5' || var1 == '6' || var1 == '7' || var1 == '8' || var1 == '9' || var1 == 'a' || var1 == 'A' || var1 == 'b' || var1 == 'B' || var1 == 'c' || var1 == 'C' || var1 == 'd' || var1 == 'D' || var1 == 'e' || var1 == 'E' || var1 == 'f' || var1 == 'F') && super.isValidCharacter(var1);
      }

      public char getChar(char var1) {
         return Character.isDigit(var1) ? var1 : Character.toUpperCase(var1);
      }

      // $FF: synthetic method
      HexCharacter(Object var2) {
         this();
      }
   }

   private class CharCharacter extends MaskFormatter.MaskCharacter {
      private CharCharacter() {
         super(null);
      }

      public boolean isValidCharacter(char var1) {
         return Character.isLetter(var1) && super.isValidCharacter(var1);
      }

      // $FF: synthetic method
      CharCharacter(Object var2) {
         this();
      }
   }

   private class AlphaNumericCharacter extends MaskFormatter.MaskCharacter {
      private AlphaNumericCharacter() {
         super(null);
      }

      public boolean isValidCharacter(char var1) {
         return Character.isLetterOrDigit(var1) && super.isValidCharacter(var1);
      }

      // $FF: synthetic method
      AlphaNumericCharacter(Object var2) {
         this();
      }
   }

   private class LowerCaseCharacter extends MaskFormatter.MaskCharacter {
      private LowerCaseCharacter() {
         super(null);
      }

      public boolean isValidCharacter(char var1) {
         return Character.isLetter(var1) && super.isValidCharacter(var1);
      }

      public char getChar(char var1) {
         return Character.toLowerCase(var1);
      }

      // $FF: synthetic method
      LowerCaseCharacter(Object var2) {
         this();
      }
   }

   private class UpperCaseCharacter extends MaskFormatter.MaskCharacter {
      private UpperCaseCharacter() {
         super(null);
      }

      public boolean isValidCharacter(char var1) {
         return Character.isLetter(var1) && super.isValidCharacter(var1);
      }

      public char getChar(char var1) {
         return Character.toUpperCase(var1);
      }

      // $FF: synthetic method
      UpperCaseCharacter(Object var2) {
         this();
      }
   }

   private class DigitMaskCharacter extends MaskFormatter.MaskCharacter {
      private DigitMaskCharacter() {
         super(null);
      }

      public boolean isValidCharacter(char var1) {
         return Character.isDigit(var1) && super.isValidCharacter(var1);
      }

      // $FF: synthetic method
      DigitMaskCharacter(Object var2) {
         this();
      }
   }

   private class LiteralCharacter extends MaskFormatter.MaskCharacter {
      private char fixedChar;

      public LiteralCharacter(char var2) {
         super(null);
         this.fixedChar = var2;
      }

      public boolean isLiteral() {
         return true;
      }

      public char getChar(char var1) {
         return this.fixedChar;
      }
   }

   private class MaskCharacter {
      private MaskCharacter() {
      }

      public boolean isLiteral() {
         return false;
      }

      public boolean isValidCharacter(char var1) {
         if (this.isLiteral()) {
            return this.getChar(var1) == var1;
         } else {
            var1 = this.getChar(var1);
            String var2 = MaskFormatter.this.getValidCharacters();
            if (var2 != null && var2.indexOf(var1) == -1) {
               return false;
            } else {
               var2 = MaskFormatter.this.getInvalidCharacters();
               return var2 == null || var2.indexOf(var1) == -1;
            }
         }
      }

      public char getChar(char var1) {
         return var1;
      }

      public void append(StringBuilder var1, String var2, int[] var3, String var4) throws ParseException {
         boolean var5 = var3[0] < var2.length();
         char var6 = var5 ? var2.charAt(var3[0]) : 0;
         int var10002;
         if (this.isLiteral()) {
            var1.append(this.getChar(var6));
            if (MaskFormatter.this.getValueContainsLiteralCharacters()) {
               if (var5 && var6 != this.getChar(var6)) {
                  throw new ParseException("Invalid character: " + var6, var3[0]);
               }

               var10002 = var3[0]++;
            }
         } else if (var3[0] >= var2.length()) {
            if (var4 != null && var3[0] < var4.length()) {
               var1.append(var4.charAt(var3[0]));
            } else {
               var1.append(MaskFormatter.this.getPlaceholderCharacter());
            }

            var10002 = var3[0]++;
         } else {
            if (!this.isValidCharacter(var6)) {
               throw new ParseException("Invalid character: " + var6, var3[0]);
            }

            var1.append(this.getChar(var6));
            var10002 = var3[0]++;
         }

      }

      // $FF: synthetic method
      MaskCharacter(Object var2) {
         this();
      }
   }
}
