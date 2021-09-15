package java.text;

class PatternEntry {
   static final int RESET = -2;
   static final int UNSET = -1;
   int strength = -1;
   String chars = "";
   String extension = "";

   public void appendQuotedExtension(StringBuffer var1) {
      appendQuoted(this.extension, var1);
   }

   public void appendQuotedChars(StringBuffer var1) {
      appendQuoted(this.chars, var1);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         PatternEntry var2 = (PatternEntry)var1;
         boolean var3 = this.chars.equals(var2.chars);
         return var3;
      }
   }

   public int hashCode() {
      return this.chars.hashCode();
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      this.addToBuffer(var1, true, false, (PatternEntry)null);
      return var1.toString();
   }

   final int getStrength() {
      return this.strength;
   }

   final String getExtension() {
      return this.extension;
   }

   final String getChars() {
      return this.chars;
   }

   void addToBuffer(StringBuffer var1, boolean var2, boolean var3, PatternEntry var4) {
      if (var3 && var1.length() > 0) {
         if (this.strength != 0 && var4 == null) {
            var1.append(' ');
         } else {
            var1.append('\n');
         }
      }

      if (var4 != null) {
         var1.append('&');
         if (var3) {
            var1.append(' ');
         }

         var4.appendQuotedChars(var1);
         this.appendQuotedExtension(var1);
         if (var3) {
            var1.append(' ');
         }
      }

      switch(this.strength) {
      case -2:
         var1.append('&');
         break;
      case -1:
         var1.append('?');
         break;
      case 0:
         var1.append('<');
         break;
      case 1:
         var1.append(';');
         break;
      case 2:
         var1.append(',');
         break;
      case 3:
         var1.append('=');
      }

      if (var3) {
         var1.append(' ');
      }

      appendQuoted(this.chars, var1);
      if (var2 && this.extension.length() != 0) {
         var1.append('/');
         appendQuoted(this.extension, var1);
      }

   }

   static void appendQuoted(String var0, StringBuffer var1) {
      boolean var2 = false;
      char var3 = var0.charAt(0);
      if (Character.isSpaceChar(var3)) {
         var2 = true;
         var1.append('\'');
      } else if (isSpecialChar(var3)) {
         var2 = true;
         var1.append('\'');
      } else {
         switch(var3) {
         case '\t':
         case '\n':
         case '\f':
         case '\r':
         case '\u0010':
         case '@':
            var2 = true;
            var1.append('\'');
            break;
         case '\'':
            var2 = true;
            var1.append('\'');
            break;
         default:
            if (var2) {
               var2 = false;
               var1.append('\'');
            }
         }
      }

      var1.append(var0);
      if (var2) {
         var1.append('\'');
      }

   }

   PatternEntry(int var1, StringBuffer var2, StringBuffer var3) {
      this.strength = var1;
      this.chars = var2.toString();
      this.extension = var3.length() > 0 ? var3.toString() : "";
   }

   static boolean isSpecialChar(char var0) {
      return var0 == ' ' || var0 <= '/' && var0 >= '"' || var0 <= '?' && var0 >= ':' || var0 <= '`' && var0 >= '[' || var0 <= '~' && var0 >= '{';
   }

   static class Parser {
      private String pattern;
      private int i;
      private StringBuffer newChars = new StringBuffer();
      private StringBuffer newExtension = new StringBuffer();

      public Parser(String var1) {
         this.pattern = var1;
         this.i = 0;
      }

      public PatternEntry next() throws ParseException {
         byte var1 = -1;
         this.newChars.setLength(0);
         this.newExtension.setLength(0);
         boolean var2 = true;

         label94:
         for(boolean var3 = false; this.i < this.pattern.length(); ++this.i) {
            char var4 = this.pattern.charAt(this.i);
            if (var3) {
               if (var4 == '\'') {
                  var3 = false;
               } else if (this.newChars.length() == 0) {
                  this.newChars.append(var4);
               } else if (var2) {
                  this.newChars.append(var4);
               } else {
                  this.newExtension.append(var4);
               }
            } else {
               switch(var4) {
               case '\t':
               case '\n':
               case '\f':
               case '\r':
               case ' ':
                  break;
               case '&':
                  if (var1 != -1) {
                     break label94;
                  }

                  var1 = -2;
                  break;
               case '\'':
                  var3 = true;
                  var4 = this.pattern.charAt(++this.i);
                  if (this.newChars.length() == 0) {
                     this.newChars.append(var4);
                  } else if (var2) {
                     this.newChars.append(var4);
                  } else {
                     this.newExtension.append(var4);
                  }
                  break;
               case ',':
                  if (var1 != -1) {
                     break label94;
                  }

                  var1 = 2;
                  break;
               case '/':
                  var2 = false;
                  break;
               case ';':
                  if (var1 != -1) {
                     break label94;
                  }

                  var1 = 1;
                  break;
               case '<':
                  if (var1 != -1) {
                     break label94;
                  }

                  var1 = 0;
                  break;
               case '=':
                  if (var1 != -1) {
                     break label94;
                  }

                  var1 = 3;
                  break;
               default:
                  if (var1 == -1) {
                     throw new ParseException("missing char (=,;<&) : " + this.pattern.substring(this.i, this.i + 10 < this.pattern.length() ? this.i + 10 : this.pattern.length()), this.i);
                  }

                  if (PatternEntry.isSpecialChar(var4) && !var3) {
                     throw new ParseException("Unquoted punctuation character : " + Integer.toString(var4, 16), this.i);
                  }

                  if (var2) {
                     this.newChars.append(var4);
                  } else {
                     this.newExtension.append(var4);
                  }
               }
            }
         }

         if (var1 == -1) {
            return null;
         } else if (this.newChars.length() == 0) {
            throw new ParseException("missing chars (=,;<&): " + this.pattern.substring(this.i, this.i + 10 < this.pattern.length() ? this.i + 10 : this.pattern.length()), this.i);
         } else {
            return new PatternEntry(var1, this.newChars, this.newExtension);
         }
      }
   }
}
