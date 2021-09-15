package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class UUIDEncodingAlgorithm extends LongEncodingAlgorithm {
   private long _msb;
   private long _lsb;

   public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
      if (octetLength % 16 != 0) {
         throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfUUID", new Object[]{16}));
      } else {
         return octetLength / 8;
      }
   }

   public final Object convertFromCharacters(char[] ch, int start, int length) {
      final CharBuffer cb = CharBuffer.wrap(ch, start, length);
      final List longList = new ArrayList();
      this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener() {
         public void word(int start, int end) {
            String uuidValue = cb.subSequence(start, end).toString();
            UUIDEncodingAlgorithm.this.fromUUIDString(uuidValue);
            longList.add(UUIDEncodingAlgorithm.this._msb);
            longList.add(UUIDEncodingAlgorithm.this._lsb);
         }
      });
      return this.generateArrayFromList(longList);
   }

   public final void convertToCharacters(Object data, StringBuffer s) {
      if (!(data instanceof long[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray"));
      } else {
         long[] ldata = (long[])((long[])data);
         int end = ldata.length - 2;

         for(int i = 0; i <= end; i += 2) {
            s.append(this.toUUIDString(ldata[i], ldata[i + 1]));
            if (i != end) {
               s.append(' ');
            }
         }

      }
   }

   final void fromUUIDString(String name) {
      String[] components = name.split("-");
      if (components.length != 5) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.invalidUUID", new Object[]{name}));
      } else {
         for(int i = 0; i < 5; ++i) {
            components[i] = "0x" + components[i];
         }

         this._msb = Long.parseLong(components[0], 16);
         this._msb <<= 16;
         this._msb |= Long.parseLong(components[1], 16);
         this._msb <<= 16;
         this._msb |= Long.parseLong(components[2], 16);
         this._lsb = Long.parseLong(components[3], 16);
         this._lsb <<= 48;
         this._lsb |= Long.parseLong(components[4], 16);
      }
   }

   final String toUUIDString(long msb, long lsb) {
      return this.digits(msb >> 32, 8) + "-" + this.digits(msb >> 16, 4) + "-" + this.digits(msb, 4) + "-" + this.digits(lsb >> 48, 4) + "-" + this.digits(lsb, 12);
   }

   final String digits(long val, int digits) {
      long hi = 1L << digits * 4;
      return Long.toHexString(hi | val & hi - 1L).substring(1);
   }
}
