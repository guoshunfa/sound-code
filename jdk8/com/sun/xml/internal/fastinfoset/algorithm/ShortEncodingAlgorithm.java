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

public class ShortEncodingAlgorithm extends IntegerEncodingAlgorithm {
   public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
      if (octetLength % 2 != 0) {
         throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfShort", new Object[]{2}));
      } else {
         return octetLength / 2;
      }
   }

   public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
      return primitiveLength * 2;
   }

   public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
      short[] data = new short[this.getPrimtiveLengthFromOctetLength(length)];
      this.decodeFromBytesToShortArray(data, 0, b, start, length);
      return data;
   }

   public final Object decodeFromInputStream(InputStream s) throws IOException {
      return this.decodeFromInputStreamToShortArray(s);
   }

   public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
      if (!(data instanceof short[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray"));
      } else {
         short[] idata = (short[])((short[])data);
         this.encodeToOutputStreamFromShortArray(idata, s);
      }
   }

   public final Object convertFromCharacters(char[] ch, int start, int length) {
      final CharBuffer cb = CharBuffer.wrap(ch, start, length);
      final List shortList = new ArrayList();
      this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener() {
         public void word(int start, int end) {
            String iStringValue = cb.subSequence(start, end).toString();
            shortList.add(Short.valueOf(iStringValue));
         }
      });
      return this.generateArrayFromList(shortList);
   }

   public final void convertToCharacters(Object data, StringBuffer s) {
      if (!(data instanceof short[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray"));
      } else {
         short[] idata = (short[])((short[])data);
         this.convertToCharactersFromShortArray(idata, s);
      }
   }

   public final void decodeFromBytesToShortArray(short[] sdata, int istart, byte[] b, int start, int length) {
      int size = length / 2;

      for(int i = 0; i < size; ++i) {
         sdata[istart++] = (short)((b[start++] & 255) << 8 | b[start++] & 255);
      }

   }

   public final short[] decodeFromInputStreamToShortArray(InputStream s) throws IOException {
      List shortList = new ArrayList();
      byte[] b = new byte[2];

      while(true) {
         int n = s.read(b);
         int m;
         if (n != 2) {
            if (n == -1) {
               return this.generateArrayFromList(shortList);
            }

            while(n != 2) {
               m = s.read(b, n, 2 - n);
               if (m == -1) {
                  throw new EOFException();
               }

               n += m;
            }
         }

         m = (b[0] & 255) << 8 | b[1] & 255;
         shortList.add((short)m);
      }
   }

   public final void encodeToOutputStreamFromShortArray(short[] idata, OutputStream s) throws IOException {
      for(int i = 0; i < idata.length; ++i) {
         int bits = idata[i];
         s.write(bits >>> 8 & 255);
         s.write(bits & 255);
      }

   }

   public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
      this.encodeToBytesFromShortArray((short[])((short[])array), astart, alength, b, start);
   }

   public final void encodeToBytesFromShortArray(short[] sdata, int istart, int ilength, byte[] b, int start) {
      int iend = istart + ilength;

      for(int i = istart; i < iend; ++i) {
         short bits = sdata[i];
         b[start++] = (byte)(bits >>> 8 & 255);
         b[start++] = (byte)(bits & 255);
      }

   }

   public final void convertToCharactersFromShortArray(short[] sdata, StringBuffer s) {
      int end = sdata.length - 1;

      for(int i = 0; i <= end; ++i) {
         s.append(Short.toString(sdata[i]));
         if (i != end) {
            s.append(' ');
         }
      }

   }

   public final short[] generateArrayFromList(List array) {
      short[] sdata = new short[array.size()];

      for(int i = 0; i < sdata.length; ++i) {
         sdata[i] = (Short)array.get(i);
      }

      return sdata;
   }
}
