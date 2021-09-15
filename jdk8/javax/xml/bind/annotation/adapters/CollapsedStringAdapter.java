package javax.xml.bind.annotation.adapters;

public class CollapsedStringAdapter extends XmlAdapter<String, String> {
   public String unmarshal(String text) {
      if (text == null) {
         return null;
      } else {
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

            return result.toString();
         }
      }
   }

   public String marshal(String s) {
      return s;
   }

   protected static boolean isWhiteSpace(char ch) {
      if (ch > ' ') {
         return false;
      } else {
         return ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ';
      }
   }
}
