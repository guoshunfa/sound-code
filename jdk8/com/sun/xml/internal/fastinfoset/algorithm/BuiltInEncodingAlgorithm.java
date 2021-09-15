package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BuiltInEncodingAlgorithm implements EncodingAlgorithm {
   protected static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

   public abstract int getPrimtiveLengthFromOctetLength(int var1) throws EncodingAlgorithmException;

   public abstract int getOctetLengthFromPrimitiveLength(int var1);

   public abstract void encodeToBytes(Object var1, int var2, int var3, byte[] var4, int var5);

   public void matchWhiteSpaceDelimnatedWords(CharBuffer cb, BuiltInEncodingAlgorithm.WordListener wl) {
      Matcher m = SPACE_PATTERN.matcher(cb);
      int i = 0;

      for(boolean var5 = false; m.find(); i = m.end()) {
         int s = m.start();
         if (s != i) {
            wl.word(i, s);
         }
      }

      if (i != cb.length()) {
         wl.word(i, cb.length());
      }

   }

   public StringBuilder removeWhitespace(char[] ch, int start, int length) {
      StringBuilder buf = new StringBuilder();
      int firstNonWS = 0;

      int idx;
      for(idx = 0; idx < length; ++idx) {
         if (Character.isWhitespace(ch[idx + start])) {
            if (firstNonWS < idx) {
               buf.append(ch, firstNonWS + start, idx - firstNonWS);
            }

            firstNonWS = idx + 1;
         }
      }

      if (firstNonWS < idx) {
         buf.append(ch, firstNonWS + start, idx - firstNonWS);
      }

      return buf;
   }

   public interface WordListener {
      void word(int var1, int var2);
   }
}
