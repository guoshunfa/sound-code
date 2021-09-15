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

public class LongEncodingAlgorithm extends IntegerEncodingAlgorithm {
   public int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
      if (octetLength % 8 != 0) {
         throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfLong", new Object[]{8}));
      } else {
         return octetLength / 8;
      }
   }

   public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
      return primitiveLength * 8;
   }

   public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
      long[] data = new long[this.getPrimtiveLengthFromOctetLength(length)];
      this.decodeFromBytesToLongArray(data, 0, b, start, length);
      return data;
   }

   public final Object decodeFromInputStream(InputStream s) throws IOException {
      return this.decodeFromInputStreamToIntArray(s);
   }

   public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
      if (!(data instanceof long[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray"));
      } else {
         long[] ldata = (long[])((long[])data);
         this.encodeToOutputStreamFromLongArray(ldata, s);
      }
   }

   public Object convertFromCharacters(char[] ch, int start, int length) {
      final CharBuffer cb = CharBuffer.wrap(ch, start, length);
      final List longList = new ArrayList();
      this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener() {
         public void word(int start, int end) {
            String lStringValue = cb.subSequence(start, end).toString();
            longList.add(Long.valueOf(lStringValue));
         }
      });
      return this.generateArrayFromList(longList);
   }

   public void convertToCharacters(Object data, StringBuffer s) {
      if (!(data instanceof long[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray"));
      } else {
         long[] ldata = (long[])((long[])data);
         this.convertToCharactersFromLongArray(ldata, s);
      }
   }

   public final void decodeFromBytesToLongArray(long[] ldata, int istart, byte[] b, int start, int length) {
      int size = length / 8;

      for(int i = 0; i < size; ++i) {
         ldata[istart++] = (long)(b[start++] & 255) << 56 | (long)(b[start++] & 255) << 48 | (long)(b[start++] & 255) << 40 | (long)(b[start++] & 255) << 32 | (long)(b[start++] & 255) << 24 | (long)(b[start++] & 255) << 16 | (long)(b[start++] & 255) << 8 | (long)(b[start++] & 255);
      }

   }

   public final long[] decodeFromInputStreamToIntArray(InputStream s) throws IOException {
      List longList = new ArrayList();
      byte[] b = new byte[8];

      while(true) {
         int n = s.read(b);
         if (n != 8) {
            if (n == -1) {
               return this.generateArrayFromList(longList);
            }

            while(n != 8) {
               int m = s.read(b, n, 8 - n);
               if (m == -1) {
                  throw new EOFException();
               }

               n += m;
            }
         }

         long l = ((long)b[0] << 56) + ((long)(b[1] & 255) << 48) + ((long)(b[2] & 255) << 40) + ((long)(b[3] & 255) << 32) + ((long)(b[4] & 255) << 24) + (long)((b[5] & 255) << 16) + (long)((b[6] & 255) << 8) + (long)((b[7] & 255) << 0);
         longList.add(l);
      }
   }

   public final void encodeToOutputStreamFromLongArray(long[] ldata, OutputStream s) throws IOException {
      for(int i = 0; i < ldata.length; ++i) {
         long bits = ldata[i];
         s.write((int)(bits >>> 56 & 255L));
         s.write((int)(bits >>> 48 & 255L));
         s.write((int)(bits >>> 40 & 255L));
         s.write((int)(bits >>> 32 & 255L));
         s.write((int)(bits >>> 24 & 255L));
         s.write((int)(bits >>> 16 & 255L));
         s.write((int)(bits >>> 8 & 255L));
         s.write((int)(bits & 255L));
      }

   }

   public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
      this.encodeToBytesFromLongArray((long[])((long[])array), astart, alength, b, start);
   }

   public final void encodeToBytesFromLongArray(long[] ldata, int lstart, int llength, byte[] b, int start) {
      int lend = lstart + llength;

      for(int i = lstart; i < lend; ++i) {
         long bits = ldata[i];
         b[start++] = (byte)((int)(bits >>> 56 & 255L));
         b[start++] = (byte)((int)(bits >>> 48 & 255L));
         b[start++] = (byte)((int)(bits >>> 40 & 255L));
         b[start++] = (byte)((int)(bits >>> 32 & 255L));
         b[start++] = (byte)((int)(bits >>> 24 & 255L));
         b[start++] = (byte)((int)(bits >>> 16 & 255L));
         b[start++] = (byte)((int)(bits >>> 8 & 255L));
         b[start++] = (byte)((int)(bits & 255L));
      }

   }

   public final void convertToCharactersFromLongArray(long[] ldata, StringBuffer s) {
      int end = ldata.length - 1;

      for(int i = 0; i <= end; ++i) {
         s.append(Long.toString(ldata[i]));
         if (i != end) {
            s.append(' ');
         }
      }

   }

   public final long[] generateArrayFromList(List array) {
      long[] ldata = new long[array.size()];

      for(int i = 0; i < ldata.length; ++i) {
         ldata[i] = (Long)array.get(i);
      }

      return ldata;
   }
}
