package com.sun.xml.internal.messaging.saaj.util;

public final class Base64 {
   private static final int BASELENGTH = 255;
   private static final int LOOKUPLENGTH = 63;
   private static final int TWENTYFOURBITGROUP = 24;
   private static final int EIGHTBIT = 8;
   private static final int SIXTEENBIT = 16;
   private static final int SIXBIT = 6;
   private static final int FOURBYTE = 4;
   private static final byte PAD = 61;
   private static byte[] base64Alphabet = new byte[255];
   private static byte[] lookUpBase64Alphabet = new byte[63];
   static final int[] base64;

   static boolean isBase64(byte octect) {
      return octect == 61 || base64Alphabet[octect] != -1;
   }

   static boolean isArrayByteBase64(byte[] arrayOctect) {
      int length = arrayOctect.length;
      if (length == 0) {
         return false;
      } else {
         for(int i = 0; i < length; ++i) {
            if (!isBase64(arrayOctect[i])) {
               return false;
            }
         }

         return true;
      }
   }

   public static byte[] encode(byte[] binaryData) {
      int lengthDataBits = binaryData.length * 8;
      int fewerThan24bits = lengthDataBits % 24;
      int numberTriplets = lengthDataBits / 24;
      byte[] encodedData = null;
      byte[] encodedData;
      if (fewerThan24bits != 0) {
         encodedData = new byte[(numberTriplets + 1) * 4];
      } else {
         encodedData = new byte[numberTriplets * 4];
      }

      byte k = false;
      byte l = false;
      byte b1 = false;
      byte b2 = false;
      byte b3 = false;
      int encodedIndex = false;
      int dataIndex = false;
      int i = false;

      byte k;
      byte l;
      byte b1;
      byte b2;
      int encodedIndex;
      int dataIndex;
      int i;
      for(i = 0; i < numberTriplets; ++i) {
         dataIndex = i * 3;
         b1 = binaryData[dataIndex];
         b2 = binaryData[dataIndex + 1];
         byte b3 = binaryData[dataIndex + 2];
         l = (byte)(b2 & 15);
         k = (byte)(b1 & 3);
         encodedIndex = i * 4;
         encodedData[encodedIndex] = lookUpBase64Alphabet[b1 >> 2];
         encodedData[encodedIndex + 1] = lookUpBase64Alphabet[b2 >> 4 | k << 4];
         encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2 | b3 >> 6];
         encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 63];
      }

      dataIndex = i * 3;
      encodedIndex = i * 4;
      if (fewerThan24bits == 8) {
         b1 = binaryData[dataIndex];
         k = (byte)(b1 & 3);
         encodedData[encodedIndex] = lookUpBase64Alphabet[b1 >> 2];
         encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
         encodedData[encodedIndex + 2] = 61;
         encodedData[encodedIndex + 3] = 61;
      } else if (fewerThan24bits == 16) {
         b1 = binaryData[dataIndex];
         b2 = binaryData[dataIndex + 1];
         l = (byte)(b2 & 15);
         k = (byte)(b1 & 3);
         encodedData[encodedIndex] = lookUpBase64Alphabet[b1 >> 2];
         encodedData[encodedIndex + 1] = lookUpBase64Alphabet[b2 >> 4 | k << 4];
         encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
         encodedData[encodedIndex + 3] = 61;
      }

      return encodedData;
   }

   public byte[] decode(byte[] base64Data) {
      int numberQuadruple = base64Data.length / 4;
      byte[] decodedData = null;
      byte b1 = false;
      byte b2 = false;
      byte b3 = false;
      byte b4 = false;
      byte marker0 = false;
      byte marker1 = false;
      int encodedIndex = 0;
      int dataIndex = false;
      byte[] decodedData = new byte[numberQuadruple * 3 + 1];

      for(int i = 0; i < numberQuadruple; ++i) {
         int dataIndex = i * 4;
         byte marker0 = base64Data[dataIndex + 2];
         byte marker1 = base64Data[dataIndex + 3];
         byte b1 = base64Alphabet[base64Data[dataIndex]];
         byte b2 = base64Alphabet[base64Data[dataIndex + 1]];
         byte b3;
         if (marker0 != 61 && marker1 != 61) {
            b3 = base64Alphabet[marker0];
            byte b4 = base64Alphabet[marker1];
            decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
            decodedData[encodedIndex + 1] = (byte)((b2 & 15) << 4 | b3 >> 2 & 15);
            decodedData[encodedIndex + 2] = (byte)(b3 << 6 | b4);
         } else if (marker0 == 61) {
            decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
            decodedData[encodedIndex + 1] = (byte)((b2 & 15) << 4);
            decodedData[encodedIndex + 2] = 0;
         } else if (marker1 == 61) {
            b3 = base64Alphabet[marker0];
            decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
            decodedData[encodedIndex + 1] = (byte)((b2 & 15) << 4 | b3 >> 2 & 15);
            decodedData[encodedIndex + 2] = (byte)(b3 << 6);
         }

         encodedIndex += 3;
      }

      return decodedData;
   }

   public static String base64Decode(String orig) {
      char[] chars = orig.toCharArray();
      StringBuffer sb = new StringBuffer();
      int i = false;
      int shift = 0;
      int acc = 0;

      for(int i = 0; i < chars.length; ++i) {
         int v = base64[chars[i] & 255];
         if (v >= 64) {
            if (chars[i] != '=') {
               System.out.println("Wrong char in base64: " + chars[i]);
            }
         } else {
            acc = acc << 6 | v;
            shift += 6;
            if (shift >= 8) {
               shift -= 8;
               sb.append((char)(acc >> shift & 255));
            }
         }
      }

      return sb.toString();
   }

   static {
      int i;
      for(i = 0; i < 255; ++i) {
         base64Alphabet[i] = -1;
      }

      for(i = 90; i >= 65; --i) {
         base64Alphabet[i] = (byte)(i - 65);
      }

      for(i = 122; i >= 97; --i) {
         base64Alphabet[i] = (byte)(i - 97 + 26);
      }

      for(i = 57; i >= 48; --i) {
         base64Alphabet[i] = (byte)(i - 48 + 52);
      }

      base64Alphabet[43] = 62;
      base64Alphabet[47] = 63;

      for(i = 0; i <= 25; ++i) {
         lookUpBase64Alphabet[i] = (byte)(65 + i);
      }

      i = 26;

      int j;
      for(j = 0; i <= 51; ++j) {
         lookUpBase64Alphabet[i] = (byte)(97 + j);
         ++i;
      }

      i = 52;

      for(j = 0; i <= 61; ++j) {
         lookUpBase64Alphabet[i] = (byte)(48 + j);
         ++i;
      }

      base64 = new int[]{64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64, 64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64, 64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64};
   }
}
