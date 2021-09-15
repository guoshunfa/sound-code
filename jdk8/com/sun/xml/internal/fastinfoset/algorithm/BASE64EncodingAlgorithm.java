package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BASE64EncodingAlgorithm extends BuiltInEncodingAlgorithm {
   static final char[] encodeBase64 = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
   static final int[] decodeBase64 = new int[]{62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};

   public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
      byte[] data = new byte[length];
      System.arraycopy(b, start, data, 0, length);
      return data;
   }

   public final Object decodeFromInputStream(InputStream s) throws IOException {
      throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
   }

   public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
      if (!(data instanceof byte[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotByteArray"));
      } else {
         s.write((byte[])((byte[])data));
      }
   }

   public final Object convertFromCharacters(char[] ch, int start, int length) {
      if (length == 0) {
         return new byte[0];
      } else {
         StringBuilder encodedValue = this.removeWhitespace(ch, start, length);
         int encodedLength = encodedValue.length();
         if (encodedLength == 0) {
            return new byte[0];
         } else {
            int blockCount = encodedLength / 4;
            int partialBlockLength = 3;
            if (encodedValue.charAt(encodedLength - 1) == '=') {
               --partialBlockLength;
               if (encodedValue.charAt(encodedLength - 2) == '=') {
                  --partialBlockLength;
               }
            }

            int valueLength = (blockCount - 1) * 3 + partialBlockLength;
            byte[] value = new byte[valueLength];
            int idx = 0;
            int encodedIdx = 0;

            for(int i = 0; i < blockCount; ++i) {
               int x1 = decodeBase64[encodedValue.charAt(encodedIdx++) - 43];
               int x2 = decodeBase64[encodedValue.charAt(encodedIdx++) - 43];
               int x3 = decodeBase64[encodedValue.charAt(encodedIdx++) - 43];
               int x4 = decodeBase64[encodedValue.charAt(encodedIdx++) - 43];
               value[idx++] = (byte)(x1 << 2 | x2 >> 4);
               if (idx < valueLength) {
                  value[idx++] = (byte)((x2 & 15) << 4 | x3 >> 2);
               }

               if (idx < valueLength) {
                  value[idx++] = (byte)((x3 & 3) << 6 | x4);
               }
            }

            return value;
         }
      }
   }

   public final void convertToCharacters(Object data, StringBuffer s) {
      if (data != null) {
         byte[] value = (byte[])((byte[])data);
         this.convertToCharacters(value, 0, value.length, s);
      }
   }

   public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
      return octetLength;
   }

   public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
      return primitiveLength;
   }

   public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
      System.arraycopy((byte[])((byte[])array), astart, b, start, alength);
   }

   public final void convertToCharacters(byte[] data, int offset, int length, StringBuffer s) {
      if (data != null) {
         byte[] value = data;
         if (length != 0) {
            int partialBlockLength = length % 3;
            int blockCount = partialBlockLength != 0 ? length / 3 + 1 : length / 3;
            int encodedLength = blockCount * 4;
            int originalBufferSize = s.length();
            s.ensureCapacity(encodedLength + originalBufferSize);
            int idx = offset;
            int lastIdx = offset + length;

            for(int i = 0; i < blockCount; ++i) {
               int b1 = value[idx++] & 255;
               int b2 = idx < lastIdx ? value[idx++] & 255 : 0;
               int b3 = idx < lastIdx ? value[idx++] & 255 : 0;
               s.append(encodeBase64[b1 >> 2]);
               s.append(encodeBase64[(b1 & 3) << 4 | b2 >> 4]);
               s.append(encodeBase64[(b2 & 15) << 2 | b3 >> 6]);
               s.append(encodeBase64[b3 & 63]);
            }

            switch(partialBlockLength) {
            case 1:
               s.setCharAt(originalBufferSize + encodedLength - 1, '=');
               s.setCharAt(originalBufferSize + encodedLength - 2, '=');
               break;
            case 2:
               s.setCharAt(originalBufferSize + encodedLength - 1, '=');
            }

         }
      }
   }
}
