package javax.lang.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum SourceVersion {
   RELEASE_0,
   RELEASE_1,
   RELEASE_2,
   RELEASE_3,
   RELEASE_4,
   RELEASE_5,
   RELEASE_6,
   RELEASE_7,
   RELEASE_8;

   private static final SourceVersion latestSupported = getLatestSupported();
   private static final Set<String> keywords;

   public static SourceVersion latest() {
      return RELEASE_8;
   }

   private static SourceVersion getLatestSupported() {
      try {
         String var0 = System.getProperty("java.specification.version");
         if ("1.8".equals(var0)) {
            return RELEASE_8;
         }

         if ("1.7".equals(var0)) {
            return RELEASE_7;
         }

         if ("1.6".equals(var0)) {
            return RELEASE_6;
         }
      } catch (SecurityException var1) {
      }

      return RELEASE_5;
   }

   public static SourceVersion latestSupported() {
      return latestSupported;
   }

   public static boolean isIdentifier(CharSequence var0) {
      String var1 = var0.toString();
      if (var1.length() == 0) {
         return false;
      } else {
         int var2 = var1.codePointAt(0);
         if (!Character.isJavaIdentifierStart(var2)) {
            return false;
         } else {
            for(int var3 = Character.charCount(var2); var3 < var1.length(); var3 += Character.charCount(var2)) {
               var2 = var1.codePointAt(var3);
               if (!Character.isJavaIdentifierPart(var2)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static boolean isName(CharSequence var0) {
      String var1 = var0.toString();
      String[] var2 = var1.split("\\.", -1);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (!isIdentifier(var5) || isKeyword(var5)) {
            return false;
         }
      }

      return true;
   }

   public static boolean isKeyword(CharSequence var0) {
      String var1 = var0.toString();
      return keywords.contains(var1);
   }

   static {
      HashSet var0 = new HashSet();
      String[] var1 = new String[]{"abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package", "synchronized", "boolean", "do", "goto", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while", "null", "true", "false"};
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         var0.add(var5);
      }

      keywords = Collections.unmodifiableSet(var0);
   }
}
