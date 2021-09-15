package javax.xml.bind.annotation.adapters;

public final class NormalizedStringAdapter extends XmlAdapter<String, String> {
   public String unmarshal(String text) {
      if (text == null) {
         return null;
      } else {
         int i;
         for(i = text.length() - 1; i >= 0 && !isWhiteSpaceExceptSpace(text.charAt(i)); --i) {
         }

         if (i < 0) {
            return text;
         } else {
            char[] buf = text.toCharArray();

            for(buf[i--] = ' '; i >= 0; --i) {
               if (isWhiteSpaceExceptSpace(buf[i])) {
                  buf[i] = ' ';
               }
            }

            return new String(buf);
         }
      }
   }

   public String marshal(String s) {
      return s;
   }

   protected static boolean isWhiteSpaceExceptSpace(char ch) {
      if (ch >= ' ') {
         return false;
      } else {
         return ch == '\t' || ch == '\n' || ch == '\r';
      }
   }
}
