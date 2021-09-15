package java.lang;

class CharacterDataUndefined extends CharacterData {
   static final CharacterData instance = new CharacterDataUndefined();

   int getProperties(int var1) {
      return 0;
   }

   int getType(int var1) {
      return 0;
   }

   boolean isJavaIdentifierStart(int var1) {
      return false;
   }

   boolean isJavaIdentifierPart(int var1) {
      return false;
   }

   boolean isUnicodeIdentifierStart(int var1) {
      return false;
   }

   boolean isUnicodeIdentifierPart(int var1) {
      return false;
   }

   boolean isIdentifierIgnorable(int var1) {
      return false;
   }

   int toLowerCase(int var1) {
      return var1;
   }

   int toUpperCase(int var1) {
      return var1;
   }

   int toTitleCase(int var1) {
      return var1;
   }

   int digit(int var1, int var2) {
      return -1;
   }

   int getNumericValue(int var1) {
      return -1;
   }

   boolean isWhitespace(int var1) {
      return false;
   }

   byte getDirectionality(int var1) {
      return -1;
   }

   boolean isMirrored(int var1) {
      return false;
   }

   private CharacterDataUndefined() {
   }
}
