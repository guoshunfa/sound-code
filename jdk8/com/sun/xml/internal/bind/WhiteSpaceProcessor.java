package com.sun.xml.internal.bind;

public abstract class WhiteSpaceProcessor {
   public static String replace(String text) {
      return replace((CharSequence)text).toString();
   }

   public static CharSequence replace(CharSequence text) {
      int i;
      for(i = text.length() - 1; i >= 0 && !isWhiteSpaceExceptSpace(text.charAt(i)); --i) {
      }

      if (i < 0) {
         return text;
      } else {
         StringBuilder buf = new StringBuilder(text);
         buf.setCharAt(i--, ' ');

         for(; i >= 0; --i) {
            if (isWhiteSpaceExceptSpace(buf.charAt(i))) {
               buf.setCharAt(i, ' ');
            }
         }

         return new String(buf);
      }
   }

   public static CharSequence trim(CharSequence text) {
      int len = text.length();

      int start;
      for(start = 0; start < len && isWhiteSpace(text.charAt(start)); ++start) {
      }

      int end;
      for(end = len - 1; end > start && isWhiteSpace(text.charAt(end)); --end) {
      }

      return start == 0 && end == len - 1 ? text : text.subSequence(start, end + 1);
   }

   public static String collapse(String text) {
      return collapse((CharSequence)text).toString();
   }

   public static CharSequence collapse(CharSequence text) {
      int len = text.length();

      int s;
      for(s = 0; s < len && !isWhiteSpace(text.charAt(s)); ++s) {
      }

      if (s == len) {
         return text;
      } else {
         StringBuilder result = new StringBuilder(len);
         if (s != 0) {
            for(int i = 0; i < s; ++i) {
               result.append(text.charAt(i));
            }

            result.append(' ');
         }

         boolean inStripMode = true;

         for(int i = s + 1; i < len; ++i) {
            char ch = text.charAt(i);
            boolean b = isWhiteSpace(ch);
            if (!inStripMode || !b) {
               inStripMode = b;
               if (b) {
                  result.append(' ');
               } else {
                  result.append(ch);
               }
            }
         }

         len = result.length();
         if (len > 0 && result.charAt(len - 1) == ' ') {
            result.setLength(len - 1);
         }

         return result;
      }
   }

   public static boolean isWhiteSpace(CharSequence s) {
      for(int i = s.length() - 1; i >= 0; --i) {
         if (!isWhiteSpace(s.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static boolean isWhiteSpace(char ch) {
      if (ch > ' ') {
         return false;
      } else {
         return ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ';
      }
   }

   protected static boolean isWhiteSpaceExceptSpace(char ch) {
      if (ch >= ' ') {
         return false;
      } else {
         return ch == '\t' || ch == '\n' || ch == '\r';
      }
   }
}
