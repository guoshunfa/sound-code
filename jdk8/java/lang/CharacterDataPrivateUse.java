package java.lang;

class CharacterDataPrivateUse extends CharacterData {
   static final CharacterData instance = new CharacterDataPrivateUse();

   int getProperties(int var1) {
      return 0;
   }

   int getType(int var1) {
      return (var1 & '\ufffe') == 65534 ? 0 : 18;
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
      return (byte)((var1 & '\ufffe') == 65534 ? -1 : 0);
   }

   boolean isMirrored(int var1) {
      return false;
   }

   private CharacterDataPrivateUse() {
   }
}
