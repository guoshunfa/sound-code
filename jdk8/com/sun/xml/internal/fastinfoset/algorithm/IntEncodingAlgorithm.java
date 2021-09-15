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

public class IntEncodingAlgorithm extends IntegerEncodingAlgorithm {
   public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
      if (octetLength % 4 != 0) {
         throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfInt", new Object[]{4}));
      } else {
         return octetLength / 4;
      }
   }

   public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
      return primitiveLength * 4;
   }

   public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
      int[] data = new int[this.getPrimtiveLengthFromOctetLength(length)];
      this.decodeFromBytesToIntArray(data, 0, b, start, length);
      return data;
   }

   public final Object decodeFromInputStream(InputStream s) throws IOException {
      return this.decodeFromInputStreamToIntArray(s);
   }

   public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
      if (!(data instanceof int[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray"));
      } else {
         int[] idata = (int[])((int[])data);
         this.encodeToOutputStreamFromIntArray(idata, s);
      }
   }

   public final Object convertFromCharacters(char[] ch, int start, int length) {
      final CharBuffer cb = CharBuffer.wrap(ch, start, length);
      final List integerList = new ArrayList();
      this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener() {
         public void word(int start, int end) {
            String iStringValue = cb.subSequence(start, end).toString();
            integerList.add(Integer.valueOf(iStringValue));
         }
      });
      return this.generateArrayFromList(integerList);
   }

   public final void convertToCharacters(Object data, StringBuffer s) {
      if (!(data instanceof int[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray"));
      } else {
         int[] idata = (int[])((int[])data);
         this.convertToCharactersFromIntArray(idata, s);
      }
   }

   public final void decodeFromBytesToIntArray(int[] idata, int istart, byte[] b, int start, int length) {
      int size = length / 4;

      for(int i = 0; i < size; ++i) {
         idata[istart++] = (b[start++] & 255) << 24 | (b[start++] & 255) << 16 | (b[start++] & 255) << 8 | b[start++] & 255;
      }

   }

   public final int[] decodeFromInputStreamToIntArray(InputStream s) throws IOException {
      List integerList = new ArrayList();
      byte[] b = new byte[4];

      while(true) {
         int n = s.read(b);
         int m;
         if (n != 4) {
            if (n == -1) {
               return this.generateArrayFromList(integerList);
            }

            while(n != 4) {
               m = s.read(b, n, 4 - n);
               if (m == -1) {
                  throw new EOFException();
               }

               n += m;
            }
         }

         m = (b[0] & 255) << 24 | (b[1] & 255) << 16 | (b[2] & 255) << 8 | b[3] & 255;
         integerList.add(m);
      }
   }

   public final void encodeToOutputStreamFromIntArray(int[] idata, OutputStream s) throws IOException {
      for(int i = 0; i < idata.length; ++i) {
         int bits = idata[i];
         s.write(bits >>> 24 & 255);
         s.write(bits >>> 16 & 255);
         s.write(bits >>> 8 & 255);
         s.write(bits & 255);
      }

   }

   public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
      this.encodeToBytesFromIntArray((int[])((int[])array), astart, alength, b, start);
   }

   public final void encodeToBytesFromIntArray(int[] idata, int istart, int ilength, byte[] b, int start) {
      int iend = istart + ilength;

      for(int i = istart; i < iend; ++i) {
         int bits = idata[i];
         b[start++] = (byte)(bits >>> 24 & 255);
         b[start++] = (byte)(bits >>> 16 & 255);
         b[start++] = (byte)(bits >>> 8 & 255);
         b[start++] = (byte)(bits & 255);
      }

   }

   public final void convertToCharactersFromIntArray(int[] idata, StringBuffer s) {
      int end = idata.length - 1;

      for(int i = 0; i <= end; ++i) {
         s.append(Integer.toString(idata[i]));
         if (i != end) {
            s.append(' ');
         }
      }

   }

   public final int[] generateArrayFromList(List array) {
      int[] idata = new int[array.size()];

      for(int i = 0; i < idata.length; ++i) {
         idata[i] = (Integer)array.get(i);
      }

      return idata;
   }
}
