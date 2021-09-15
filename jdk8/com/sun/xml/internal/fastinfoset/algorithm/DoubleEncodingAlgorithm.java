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

public class DoubleEncodingAlgorithm extends IEEE754FloatingPointEncodingAlgorithm {
   public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
      if (octetLength % 8 != 0) {
         throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthIsNotMultipleOfDouble", new Object[]{8}));
      } else {
         return octetLength / 8;
      }
   }

   public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
      return primitiveLength * 8;
   }

   public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
      double[] data = new double[this.getPrimtiveLengthFromOctetLength(length)];
      this.decodeFromBytesToDoubleArray(data, 0, b, start, length);
      return data;
   }

   public final Object decodeFromInputStream(InputStream s) throws IOException {
      return this.decodeFromInputStreamToDoubleArray(s);
   }

   public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
      if (!(data instanceof double[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotDouble"));
      } else {
         double[] fdata = (double[])((double[])data);
         this.encodeToOutputStreamFromDoubleArray(fdata, s);
      }
   }

   public final Object convertFromCharacters(char[] ch, int start, int length) {
      final CharBuffer cb = CharBuffer.wrap(ch, start, length);
      final List doubleList = new ArrayList();
      this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener() {
         public void word(int start, int end) {
            String fStringValue = cb.subSequence(start, end).toString();
            doubleList.add(Double.valueOf(fStringValue));
         }
      });
      return this.generateArrayFromList(doubleList);
   }

   public final void convertToCharacters(Object data, StringBuffer s) {
      if (!(data instanceof double[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotDouble"));
      } else {
         double[] fdata = (double[])((double[])data);
         this.convertToCharactersFromDoubleArray(fdata, s);
      }
   }

   public final void decodeFromBytesToDoubleArray(double[] data, int fstart, byte[] b, int start, int length) {
      int size = length / 8;

      for(int i = 0; i < size; ++i) {
         long bits = (long)(b[start++] & 255) << 56 | (long)(b[start++] & 255) << 48 | (long)(b[start++] & 255) << 40 | (long)(b[start++] & 255) << 32 | (long)(b[start++] & 255) << 24 | (long)(b[start++] & 255) << 16 | (long)(b[start++] & 255) << 8 | (long)(b[start++] & 255);
         data[fstart++] = Double.longBitsToDouble(bits);
      }

   }

   public final double[] decodeFromInputStreamToDoubleArray(InputStream s) throws IOException {
      List doubleList = new ArrayList();
      byte[] b = new byte[8];

      while(true) {
         int n = s.read(b);
         if (n != 8) {
            if (n == -1) {
               return this.generateArrayFromList(doubleList);
            }

            while(n != 8) {
               int m = s.read(b, n, 8 - n);
               if (m == -1) {
                  throw new EOFException();
               }

               n += m;
            }
         }

         long bits = (long)(b[0] & 255) << 56 | (long)(b[1] & 255) << 48 | (long)(b[2] & 255) << 40 | (long)(b[3] & 255) << 32 | (long)((b[4] & 255) << 24) | (long)((b[5] & 255) << 16) | (long)((b[6] & 255) << 8) | (long)(b[7] & 255);
         doubleList.add(Double.longBitsToDouble(bits));
      }
   }

   public final void encodeToOutputStreamFromDoubleArray(double[] fdata, OutputStream s) throws IOException {
      for(int i = 0; i < fdata.length; ++i) {
         long bits = Double.doubleToLongBits(fdata[i]);
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
      this.encodeToBytesFromDoubleArray((double[])((double[])array), astart, alength, b, start);
   }

   public final void encodeToBytesFromDoubleArray(double[] fdata, int fstart, int flength, byte[] b, int start) {
      int fend = fstart + flength;

      for(int i = fstart; i < fend; ++i) {
         long bits = Double.doubleToLongBits(fdata[i]);
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

   public final void convertToCharactersFromDoubleArray(double[] fdata, StringBuffer s) {
      int end = fdata.length - 1;

      for(int i = 0; i <= end; ++i) {
         s.append(Double.toString(fdata[i]));
         if (i != end) {
            s.append(' ');
         }
      }

   }

   public final double[] generateArrayFromList(List array) {
      double[] fdata = new double[array.size()];

      for(int i = 0; i < fdata.length; ++i) {
         fdata[i] = (Double)array.get(i);
      }

      return fdata;
   }
}
