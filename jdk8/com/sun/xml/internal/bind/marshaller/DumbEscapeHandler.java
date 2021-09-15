package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class DumbEscapeHandler implements CharacterEscapeHandler {
   public static final CharacterEscapeHandler theInstance = new DumbEscapeHandler();

   private DumbEscapeHandler() {
   }

   public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
      int limit = start + length;

      for(int i = start; i < limit; ++i) {
         switch(ch[i]) {
         case '"':
            if (isAttVal) {
               out.write("&quot;");
            } else {
               out.write(34);
            }
            break;
         case '&':
            out.write("&amp;");
            break;
         case '<':
            out.write("&lt;");
            break;
         case '>':
            out.write("&gt;");
            break;
         default:
            if (ch[i] > 127) {
               out.write("&#");
               out.write(Integer.toString(ch[i]));
               out.write(59);
            } else {
               out.write(ch[i]);
            }
         }
      }

   }
}
