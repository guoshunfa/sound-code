package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class BooleanEncodingAlgorithm extends BuiltInEncodingAlgorithm {
   private static final int[] BIT_TABLE = new int[]{128, 64, 32, 16, 8, 4, 2, 1};

   public int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
      throw new UnsupportedOperationException();
   }

   public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
      if (primitiveLength < 5) {
         return 1;
      } else {
         int div = primitiveLength / 8;
         return div == 0 ? 2 : 1 + div;
      }
   }

   public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
      int blength = this.getPrimtiveLengthFromOctetLength(length, b[start]);
      boolean[] data = new boolean[blength];
      this.decodeFromBytesToBooleanArray(data, 0, blength, b, start, length);
      return data;
   }

   public final Object decodeFromInputStream(InputStream s) throws IOException {
      List booleanList = new ArrayList();
      int value = s.read();
      if (value == -1) {
         throw new EOFException();
      } else {
         int unusedBits = value >> 4 & 255;
         int bitPosition = 4;
         int bitPositionEnd = 8;
         boolean var7 = false;

         int valueNext;
         do {
            valueNext = s.read();
            if (valueNext == -1) {
               bitPositionEnd -= unusedBits;
            }

            while(bitPosition < bitPositionEnd) {
               booleanList.add((value & BIT_TABLE[bitPosition++]) > 0);
            }

            value = valueNext;
         } while(valueNext != -1);

         return this.generateArrayFromList(booleanList);
      }
   }

   public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
      if (!(data instanceof boolean[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean"));
      } else {
         boolean[] array = (boolean[])((boolean[])data);
         int alength = array.length;
         int mod = (alength + 4) % 8;
         int unusedBits = mod == 0 ? 0 : 8 - mod;
         int bitPosition = 4;
         int value = unusedBits << 4;
         int astart = 0;

         while(astart < alength) {
            if (array[astart++]) {
               value |= BIT_TABLE[bitPosition];
            }

            ++bitPosition;
            if (bitPosition == 8) {
               s.write(value);
               value = 0;
               bitPosition = 0;
            }
         }

         if (bitPosition != 8) {
            s.write(value);
         }

      }
   }

   public final Object convertFromCharacters(char[] ch, int start, int length) {
      if (length == 0) {
         return new boolean[0];
      } else {
         final CharBuffer cb = CharBuffer.wrap(ch, start, length);
         final List booleanList = new ArrayList();
         this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener() {
            public void word(int start, int end) {
               if (cb.charAt(start) == 't') {
                  booleanList.add(Boolean.TRUE);
               } else {
                  booleanList.add(Boolean.FALSE);
               }

            }
         });
         return this.generateArrayFromList(booleanList);
      }
   }

   public final void convertToCharacters(Object data, StringBuffer s) {
      if (data != null) {
         boolean[] value = (boolean[])((boolean[])data);
         if (value.length != 0) {
            s.ensureCapacity(value.length * 5);
            int end = value.length - 1;

            for(int i = 0; i <= end; ++i) {
               if (value[i]) {
                  s.append("true");
               } else {
                  s.append("false");
               }

               if (i != end) {
                  s.append(' ');
               }
            }

         }
      }
   }

   public int getPrimtiveLengthFromOctetLength(int octetLength, int firstOctet) throws EncodingAlgorithmException {
      int unusedBits = firstOctet >> 4 & 255;
      if (octetLength == 1) {
         if (unusedBits > 3) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits4"));
         } else {
            return 4 - unusedBits;
         }
      } else if (unusedBits > 7) {
         throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits8"));
      } else {
         return octetLength * 8 - 4 - unusedBits;
      }
   }

   public final void decodeFromBytesToBooleanArray(boolean[] bdata, int bstart, int blength, byte[] b, int start, int length) {
      int value = b[start++] & 255;
      int bitPosition = 4;

      for(int bend = bstart + blength; bstart < bend; bdata[bstart++] = (value & BIT_TABLE[bitPosition++]) > 0) {
         if (bitPosition == 8) {
            value = b[start++] & 255;
            bitPosition = 0;
         }
      }

   }

   public void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
      if (!(array instanceof boolean[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean"));
      } else {
         this.encodeToBytesFromBooleanArray((boolean[])((boolean[])array), astart, alength, b, start);
      }
   }

   public void encodeToBytesFromBooleanArray(boolean[] array, int astart, int alength, byte[] b, int start) {
      int mod = (alength + 4) % 8;
      int unusedBits = mod == 0 ? 0 : 8 - mod;
      int bitPosition = 4;
      int value = unusedBits << 4;
      int aend = astart + alength;

      while(astart < aend) {
         if (array[astart++]) {
            value |= BIT_TABLE[bitPosition];
         }

         ++bitPosition;
         if (bitPosition == 8) {
            b[start++] = (byte)value;
            value = 0;
            bitPosition = 0;
         }
      }

      if (bitPosition > 0) {
         b[start] = (byte)value;
      }

   }

   private boolean[] generateArrayFromList(List array) {
      boolean[] bdata = new boolean[array.size()];

      for(int i = 0; i < bdata.length; ++i) {
         bdata[i] = (Boolean)array.get(i);
      }

      return bdata;
   }
}
