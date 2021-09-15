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

public class FloatEncodingAlgorithm extends IEEE754FloatingPointEncodingAlgorithm {
   public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
      if (octetLength % 4 != 0) {
         throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfFloat", new Object[]{4}));
      } else {
         return octetLength / 4;
      }
   }

   public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
      return primitiveLength * 4;
   }

   public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
      float[] data = new float[this.getPrimtiveLengthFromOctetLength(length)];
      this.decodeFromBytesToFloatArray(data, 0, b, start, length);
      return data;
   }

   public final Object decodeFromInputStream(InputStream s) throws IOException {
      return this.decodeFromInputStreamToFloatArray(s);
   }

   public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
      if (!(data instanceof float[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotFloat"));
      } else {
         float[] fdata = (float[])((float[])data);
         this.encodeToOutputStreamFromFloatArray(fdata, s);
      }
   }

   public final Object convertFromCharacters(char[] ch, int start, int length) {
      final CharBuffer cb = CharBuffer.wrap(ch, start, length);
      final List floatList = new ArrayList();
      this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener() {
         public void word(int start, int end) {
            String fStringValue = cb.subSequence(start, end).toString();
            floatList.add(Float.valueOf(fStringValue));
         }
      });
      return this.generateArrayFromList(floatList);
   }

   public final void convertToCharacters(Object data, StringBuffer s) {
      if (!(data instanceof float[])) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotFloat"));
      } else {
         float[] fdata = (float[])((float[])data);
         this.convertToCharactersFromFloatArray(fdata, s);
      }
   }

   public final void decodeFromBytesToFloatArray(float[] data, int fstart, byte[] b, int start, int length) {
      int size = length / 4;

      for(int i = 0; i < size; ++i) {
         int bits = (b[start++] & 255) << 24 | (b[start++] & 255) << 16 | (b[start++] & 255) << 8 | b[start++] & 255;
         data[fstart++] = Float.intBitsToFloat(bits);
      }

   }

   public final float[] decodeFromInputStreamToFloatArray(InputStream s) throws IOException {
      List floatList = new ArrayList();
      byte[] b = new byte[4];

      while(true) {
         int n = s.read(b);
         int m;
         if (n != 4) {
            if (n == -1) {
               return this.generateArrayFromList(floatList);
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
         floatList.add(Float.intBitsToFloat(m));
      }
   }

   public final void encodeToOutputStreamFromFloatArray(float[] fdata, OutputStream s) throws IOException {
      for(int i = 0; i < fdata.length; ++i) {
         int bits = Float.floatToIntBits(fdata[i]);
         s.write(bits >>> 24 & 255);
         s.write(bits >>> 16 & 255);
         s.write(bits >>> 8 & 255);
         s.write(bits & 255);
      }

   }

   public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
      this.encodeToBytesFromFloatArray((float[])((float[])array), astart, alength, b, start);
   }

   public final void encodeToBytesFromFloatArray(float[] fdata, int fstart, int flength, byte[] b, int start) {
      int fend = fstart + flength;

      for(int i = fstart; i < fend; ++i) {
         int bits = Float.floatToIntBits(fdata[i]);
         b[start++] = (byte)(bits >>> 24 & 255);
         b[start++] = (byte)(bits >>> 16 & 255);
         b[start++] = (byte)(bits >>> 8 & 255);
         b[start++] = (byte)(bits & 255);
      }

   }

   public final void convertToCharactersFromFloatArray(float[] fdata, StringBuffer s) {
      int end = fdata.length - 1;

      for(int i = 0; i <= end; ++i) {
         s.append(Float.toString(fdata[i]));
         if (i != end) {
            s.append(' ');
         }
      }

   }

   public final float[] generateArrayFromList(List array) {
      float[] fdata = new float[array.size()];

      for(int i = 0; i < fdata.length; ++i) {
         fdata[i] = (Float)array.get(i);
      }

      return fdata;
   }
}
