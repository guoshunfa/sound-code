package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class MinimumEscapeHandler implements CharacterEscapeHandler {
   public static final CharacterEscapeHandler theInstance = new MinimumEscapeHandler();

   private MinimumEscapeHandler() {
   }

   public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
      int limit = start + length;

      for(int i = start; i < limit; ++i) {
         char c = ch[i];
         if (c == '&' || c == '<' || c == '>' || c == '\r' || c == '"' && isAttVal) {
            if (i != start) {
               out.write(ch, start, i - start);
            }

            start = i + 1;
            switch(ch[i]) {
            case '"':
               out.write("&quot;");
               break;
            case '&':
               out.write("&amp;");
               break;
            case '<':
               out.write("&lt;");
               break;
            case '>':
               out.write("&gt;");
            }
         }
      }

      if (start != limit) {
         out.write(ch, start, limit - start);
      }

   }
}
