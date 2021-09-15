package java.lang;

abstract class CharacterData {
   abstract int getProperties(int var1);

   abstract int getType(int var1);

   abstract boolean isWhitespace(int var1);

   abstract boolean isMirrored(int var1);

   abstract boolean isJavaIdentifierStart(int var1);

   abstract boolean isJavaIdentifierPart(int var1);

   abstract boolean isUnicodeIdentifierStart(int var1);

   abstract boolean isUnicodeIdentifierPart(int var1);

   abstract boolean isIdentifierIgnorable(int var1);

   abstract int toLowerCase(int var1);

   abstract int toUpperCase(int var1);

   abstract int toTitleCase(int var1);

   abstract int digit(int var1, int var2);

   abstract int getNumericValue(int var1);

   abstract byte getDirectionality(int var1);

   int toUpperCaseEx(int var1) {
      return this.toUpperCase(var1);
   }

   char[] toUpperCaseCharArray(int var1) {
      return null;
   }

   boolean isOtherLowercase(int var1) {
      return false;
   }

   boolean isOtherUppercase(int var1) {
      return false;
   }

   boolean isOtherAlphabetic(int var1) {
      return false;
   }

   boolean isIdeographic(int var1) {
      return false;
   }

   static final CharacterData of(int var0) {
      if (var0 >>> 8 == 0) {
         return CharacterDataLatin1.instance;
      } else {
         switch(var0 >>> 16) {
         case 0:
            return CharacterData00.instance;
         case 1:
            return CharacterData01.instance;
         case 2:
            return CharacterData02.instance;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         default:
            return CharacterDataUndefined.instance;
         case 14:
            return CharacterData0E.instance;
         case 15:
         case 16:
            return CharacterDataPrivateUse.instance;
         }
      }
   }
}
