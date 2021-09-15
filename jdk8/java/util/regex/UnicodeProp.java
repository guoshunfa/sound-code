package java.util.regex;

import java.util.HashMap;
import java.util.Locale;

enum UnicodeProp {
   ALPHABETIC {
      public boolean is(int var1) {
         return Character.isAlphabetic(var1);
      }
   },
   LETTER {
      public boolean is(int var1) {
         return Character.isLetter(var1);
      }
   },
   IDEOGRAPHIC {
      public boolean is(int var1) {
         return Character.isIdeographic(var1);
      }
   },
   LOWERCASE {
      public boolean is(int var1) {
         return Character.isLowerCase(var1);
      }
   },
   UPPERCASE {
      public boolean is(int var1) {
         return Character.isUpperCase(var1);
      }
   },
   TITLECASE {
      public boolean is(int var1) {
         return Character.isTitleCase(var1);
      }
   },
   WHITE_SPACE {
      public boolean is(int var1) {
         return (28672 >> Character.getType(var1) & 1) != 0 || var1 >= 9 && var1 <= 13 || var1 == 133;
      }
   },
   CONTROL {
      public boolean is(int var1) {
         return Character.getType(var1) == 15;
      }
   },
   PUNCTUATION {
      public boolean is(int var1) {
         return (1643118592 >> Character.getType(var1) & 1) != 0;
      }
   },
   HEX_DIGIT {
      public boolean is(int var1) {
         return DIGIT.is(var1) || var1 >= 48 && var1 <= 57 || var1 >= 65 && var1 <= 70 || var1 >= 97 && var1 <= 102 || var1 >= 65296 && var1 <= 65305 || var1 >= 65313 && var1 <= 65318 || var1 >= 65345 && var1 <= 65350;
      }
   },
   ASSIGNED {
      public boolean is(int var1) {
         return Character.getType(var1) != 0;
      }
   },
   NONCHARACTER_CODE_POINT {
      public boolean is(int var1) {
         return (var1 & '\ufffe') == 65534 || var1 >= 64976 && var1 <= 65007;
      }
   },
   DIGIT {
      public boolean is(int var1) {
         return Character.isDigit(var1);
      }
   },
   ALNUM {
      public boolean is(int var1) {
         return ALPHABETIC.is(var1) || DIGIT.is(var1);
      }
   },
   BLANK {
      public boolean is(int var1) {
         return Character.getType(var1) == 12 || var1 == 9;
      }
   },
   GRAPH {
      public boolean is(int var1) {
         return (585729 >> Character.getType(var1) & 1) == 0;
      }
   },
   PRINT {
      public boolean is(int var1) {
         return (GRAPH.is(var1) || BLANK.is(var1)) && !CONTROL.is(var1);
      }
   },
   WORD {
      public boolean is(int var1) {
         return ALPHABETIC.is(var1) || (8389568 >> Character.getType(var1) & 1) != 0 || JOIN_CONTROL.is(var1);
      }
   },
   JOIN_CONTROL {
      public boolean is(int var1) {
         return var1 == 8204 || var1 == 8205;
      }
   };

   private static final HashMap<String, String> posix = new HashMap();
   private static final HashMap<String, String> aliases = new HashMap();

   private UnicodeProp() {
   }

   public static UnicodeProp forName(String var0) {
      var0 = var0.toUpperCase(Locale.ENGLISH);
      String var1 = (String)aliases.get(var0);
      if (var1 != null) {
         var0 = var1;
      }

      try {
         return valueOf(var0);
      } catch (IllegalArgumentException var3) {
         return null;
      }
   }

   public static UnicodeProp forPOSIXName(String var0) {
      var0 = (String)posix.get(var0.toUpperCase(Locale.ENGLISH));
      return var0 == null ? null : valueOf(var0);
   }

   public abstract boolean is(int var1);

   // $FF: synthetic method
   UnicodeProp(Object var3) {
      this();
   }

   static {
      posix.put("ALPHA", "ALPHABETIC");
      posix.put("LOWER", "LOWERCASE");
      posix.put("UPPER", "UPPERCASE");
      posix.put("SPACE", "WHITE_SPACE");
      posix.put("PUNCT", "PUNCTUATION");
      posix.put("XDIGIT", "HEX_DIGIT");
      posix.put("ALNUM", "ALNUM");
      posix.put("CNTRL", "CONTROL");
      posix.put("DIGIT", "DIGIT");
      posix.put("BLANK", "BLANK");
      posix.put("GRAPH", "GRAPH");
      posix.put("PRINT", "PRINT");
      aliases.put("WHITESPACE", "WHITE_SPACE");
      aliases.put("HEXDIGIT", "HEX_DIGIT");
      aliases.put("NONCHARACTERCODEPOINT", "NONCHARACTER_CODE_POINT");
      aliases.put("JOINCONTROL", "JOIN_CONTROL");
   }
}
