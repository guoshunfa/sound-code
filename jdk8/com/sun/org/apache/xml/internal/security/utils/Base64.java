package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class Base64 {
   public static final int BASE64DEFAULTLENGTH = 76;
   private static final int BASELENGTH = 255;
   private static final int LOOKUPLENGTH = 64;
   private static final int TWENTYFOURBITGROUP = 24;
   private static final int EIGHTBIT = 8;
   private static final int SIXTEENBIT = 16;
   private static final int FOURBYTE = 4;
   private static final int SIGN = -128;
   private static final char PAD = '=';
   private static final byte[] base64Alphabet = new byte[255];
   private static final char[] lookUpBase64Alphabet = new char[64];

   private Base64() {
   }

   static final byte[] getBytes(BigInteger var0, int var1) {
      var1 = var1 + 7 >> 3 << 3;
      if (var1 < var0.bitLength()) {
         throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
      } else {
         byte[] var2 = var0.toByteArray();
         if (var0.bitLength() % 8 != 0 && var0.bitLength() / 8 + 1 == var1 / 8) {
            return var2;
         } else {
            byte var3 = 0;
            int var4 = var2.length;
            if (var0.bitLength() % 8 == 0) {
               var3 = 1;
               --var4;
            }

            int var5 = var1 / 8 - var4;
            byte[] var6 = new byte[var1 / 8];
            System.arraycopy(var2, var3, var6, var5, var4);
            return var6;
         }
      }
   }

   public static final String encode(BigInteger var0) {
      return encode(getBytes(var0, var0.bitLength()));
   }

   public static final byte[] encode(BigInteger var0, int var1) {
      var1 = var1 + 7 >> 3 << 3;
      if (var1 < var0.bitLength()) {
         throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
      } else {
         byte[] var2 = var0.toByteArray();
         if (var0.bitLength() % 8 != 0 && var0.bitLength() / 8 + 1 == var1 / 8) {
            return var2;
         } else {
            byte var3 = 0;
            int var4 = var2.length;
            if (var0.bitLength() % 8 == 0) {
               var3 = 1;
               --var4;
            }

            int var5 = var1 / 8 - var4;
            byte[] var6 = new byte[var1 / 8];
            System.arraycopy(var2, var3, var6, var5, var4);
            return var6;
         }
      }
   }

   public static final BigInteger decodeBigIntegerFromElement(Element var0) throws Base64DecodingException {
      return new BigInteger(1, decode(var0));
   }

   public static final BigInteger decodeBigIntegerFromText(Text var0) throws Base64DecodingException {
      return new BigInteger(1, decode(var0.getData()));
   }

   public static final void fillElementWithBigInteger(Element var0, BigInteger var1) {
      String var2 = encode(var1);
      if (!XMLUtils.ignoreLineBreaks() && var2.length() > 76) {
         var2 = "\n" + var2 + "\n";
      }

      Document var3 = var0.getOwnerDocument();
      Text var4 = var3.createTextNode(var2);
      var0.appendChild(var4);
   }

   public static final byte[] decode(Element var0) throws Base64DecodingException {
      Node var1 = var0.getFirstChild();

      StringBuffer var2;
      for(var2 = new StringBuffer(); var1 != null; var1 = var1.getNextSibling()) {
         if (var1.getNodeType() == 3) {
            Text var3 = (Text)var1;
            var2.append(var3.getData());
         }
      }

      return decode(var2.toString());
   }

   public static final Element encodeToElement(Document var0, String var1, byte[] var2) {
      Element var3 = XMLUtils.createElementInSignatureSpace(var0, var1);
      Text var4 = var0.createTextNode(encode(var2));
      var3.appendChild(var4);
      return var3;
   }

   public static final byte[] decode(byte[] var0) throws Base64DecodingException {
      return decodeInternal(var0, -1);
   }

   public static final String encode(byte[] var0) {
      return XMLUtils.ignoreLineBreaks() ? encode(var0, Integer.MAX_VALUE) : encode((byte[])var0, 76);
   }

   public static final byte[] decode(BufferedReader var0) throws IOException, Base64DecodingException {
      Object var1 = null;
      UnsyncByteArrayOutputStream var2 = null;

      try {
         var2 = new UnsyncByteArrayOutputStream();

         String var3;
         while(null != (var3 = var0.readLine())) {
            byte[] var4 = decode(var3);
            var2.write(var4);
         }

         byte[] var8 = var2.toByteArray();
         return var8;
      } finally {
         var2.close();
      }
   }

   protected static final boolean isWhiteSpace(byte var0) {
      return var0 == 32 || var0 == 13 || var0 == 10 || var0 == 9;
   }

   protected static final boolean isPad(byte var0) {
      return var0 == 61;
   }

   public static final String encode(byte[] var0, int var1) {
      if (var1 < 4) {
         var1 = Integer.MAX_VALUE;
      }

      if (var0 == null) {
         return null;
      } else {
         int var2 = var0.length * 8;
         if (var2 == 0) {
            return "";
         } else {
            int var3 = var2 % 24;
            int var4 = var2 / 24;
            int var5 = var3 != 0 ? var4 + 1 : var4;
            int var6 = var1 / 4;
            int var7 = (var5 - 1) / var6;
            Object var8 = null;
            char[] var22 = new char[var5 * 4 + var7];
            boolean var9 = false;
            boolean var10 = false;
            boolean var11 = false;
            boolean var12 = false;
            boolean var13 = false;
            int var14 = 0;
            int var15 = 0;
            int var16 = 0;

            byte var19;
            byte var23;
            byte var24;
            byte var25;
            byte var26;
            byte var27;
            for(int var17 = 0; var17 < var7; ++var17) {
               for(int var18 = 0; var18 < 19; ++var18) {
                  var25 = var0[var15++];
                  var26 = var0[var15++];
                  var27 = var0[var15++];
                  var24 = (byte)(var26 & 15);
                  var23 = (byte)(var25 & 3);
                  var19 = (var25 & -128) == 0 ? (byte)(var25 >> 2) : (byte)(var25 >> 2 ^ 192);
                  byte var20 = (var26 & -128) == 0 ? (byte)(var26 >> 4) : (byte)(var26 >> 4 ^ 240);
                  byte var21 = (var27 & -128) == 0 ? (byte)(var27 >> 6) : (byte)(var27 >> 6 ^ 252);
                  var22[var14++] = lookUpBase64Alphabet[var19];
                  var22[var14++] = lookUpBase64Alphabet[var20 | var23 << 4];
                  var22[var14++] = lookUpBase64Alphabet[var24 << 2 | var21];
                  var22[var14++] = lookUpBase64Alphabet[var27 & 63];
                  ++var16;
               }

               var22[var14++] = '\n';
            }

            byte var28;
            byte var29;
            while(var16 < var4) {
               var25 = var0[var15++];
               var26 = var0[var15++];
               var27 = var0[var15++];
               var24 = (byte)(var26 & 15);
               var23 = (byte)(var25 & 3);
               var28 = (var25 & -128) == 0 ? (byte)(var25 >> 2) : (byte)(var25 >> 2 ^ 192);
               var29 = (var26 & -128) == 0 ? (byte)(var26 >> 4) : (byte)(var26 >> 4 ^ 240);
               var19 = (var27 & -128) == 0 ? (byte)(var27 >> 6) : (byte)(var27 >> 6 ^ 252);
               var22[var14++] = lookUpBase64Alphabet[var28];
               var22[var14++] = lookUpBase64Alphabet[var29 | var23 << 4];
               var22[var14++] = lookUpBase64Alphabet[var24 << 2 | var19];
               var22[var14++] = lookUpBase64Alphabet[var27 & 63];
               ++var16;
            }

            if (var3 == 8) {
               var25 = var0[var15];
               var23 = (byte)(var25 & 3);
               var28 = (var25 & -128) == 0 ? (byte)(var25 >> 2) : (byte)(var25 >> 2 ^ 192);
               var22[var14++] = lookUpBase64Alphabet[var28];
               var22[var14++] = lookUpBase64Alphabet[var23 << 4];
               var22[var14++] = '=';
               var22[var14++] = '=';
            } else if (var3 == 16) {
               var25 = var0[var15];
               var26 = var0[var15 + 1];
               var24 = (byte)(var26 & 15);
               var23 = (byte)(var25 & 3);
               var28 = (var25 & -128) == 0 ? (byte)(var25 >> 2) : (byte)(var25 >> 2 ^ 192);
               var29 = (var26 & -128) == 0 ? (byte)(var26 >> 4) : (byte)(var26 >> 4 ^ 240);
               var22[var14++] = lookUpBase64Alphabet[var28];
               var22[var14++] = lookUpBase64Alphabet[var29 | var23 << 4];
               var22[var14++] = lookUpBase64Alphabet[var24 << 2];
               var22[var14++] = '=';
            }

            return new String(var22);
         }
      }
   }

   public static final byte[] decode(String var0) throws Base64DecodingException {
      if (var0 == null) {
         return null;
      } else {
         byte[] var1 = new byte[var0.length()];
         int var2 = getBytesInternal(var0, var1);
         return decodeInternal(var1, var2);
      }
   }

   protected static final int getBytesInternal(String var0, byte[] var1) {
      int var2 = var0.length();
      int var3 = 0;

      for(int var4 = 0; var4 < var2; ++var4) {
         byte var5 = (byte)var0.charAt(var4);
         if (!isWhiteSpace(var5)) {
            var1[var3++] = var5;
         }
      }

      return var3;
   }

   protected static final byte[] decodeInternal(byte[] var0, int var1) throws Base64DecodingException {
      if (var1 == -1) {
         var1 = removeWhiteSpace(var0);
      }

      if (var1 % 4 != 0) {
         throw new Base64DecodingException("decoding.divisible.four");
      } else {
         int var2 = var1 / 4;
         if (var2 == 0) {
            return new byte[0];
         } else {
            Object var3 = null;
            boolean var4 = false;
            boolean var5 = false;
            boolean var6 = false;
            boolean var7 = false;
            boolean var8 = false;
            boolean var9 = false;
            boolean var10 = false;
            int var20 = (var2 - 1) * 4;
            int var19 = (var2 - 1) * 3;
            byte var14 = base64Alphabet[var0[var20++]];
            byte var15 = base64Alphabet[var0[var20++]];
            if (var14 != -1 && var15 != -1) {
               byte var11;
               byte var16 = base64Alphabet[var11 = var0[var20++]];
               byte var12;
               byte var17 = base64Alphabet[var12 = var0[var20++]];
               byte[] var13;
               if (var16 != -1 && var17 != -1) {
                  var13 = new byte[var19 + 3];
                  var13[var19++] = (byte)(var14 << 2 | var15 >> 4);
                  var13[var19++] = (byte)((var15 & 15) << 4 | var16 >> 2 & 15);
                  var13[var19++] = (byte)(var16 << 6 | var17);
               } else if (isPad(var11) && isPad(var12)) {
                  if ((var15 & 15) != 0) {
                     throw new Base64DecodingException("decoding.general");
                  }

                  var13 = new byte[var19 + 1];
                  var13[var19] = (byte)(var14 << 2 | var15 >> 4);
               } else {
                  if (isPad(var11) || !isPad(var12)) {
                     throw new Base64DecodingException("decoding.general");
                  }

                  if ((var16 & 3) != 0) {
                     throw new Base64DecodingException("decoding.general");
                  }

                  var13 = new byte[var19 + 2];
                  var13[var19++] = (byte)(var14 << 2 | var15 >> 4);
                  var13[var19] = (byte)((var15 & 15) << 4 | var16 >> 2 & 15);
               }

               var19 = 0;
               var20 = 0;

               for(int var18 = var2 - 1; var18 > 0; --var18) {
                  var14 = base64Alphabet[var0[var20++]];
                  var15 = base64Alphabet[var0[var20++]];
                  var16 = base64Alphabet[var0[var20++]];
                  var17 = base64Alphabet[var0[var20++]];
                  if (var14 == -1 || var15 == -1 || var16 == -1 || var17 == -1) {
                     throw new Base64DecodingException("decoding.general");
                  }

                  var13[var19++] = (byte)(var14 << 2 | var15 >> 4);
                  var13[var19++] = (byte)((var15 & 15) << 4 | var16 >> 2 & 15);
                  var13[var19++] = (byte)(var16 << 6 | var17);
               }

               return var13;
            } else {
               throw new Base64DecodingException("decoding.general");
            }
         }
      }
   }

   public static final void decode(String var0, OutputStream var1) throws Base64DecodingException, IOException {
      byte[] var2 = new byte[var0.length()];
      int var3 = getBytesInternal(var0, var2);
      decode(var2, var1, var3);
   }

   public static final void decode(byte[] var0, OutputStream var1) throws Base64DecodingException, IOException {
      decode(var0, var1, -1);
   }

   protected static final void decode(byte[] var0, OutputStream var1, int var2) throws Base64DecodingException, IOException {
      if (var2 == -1) {
         var2 = removeWhiteSpace(var0);
      }

      if (var2 % 4 != 0) {
         throw new Base64DecodingException("decoding.divisible.four");
      } else {
         int var3 = var2 / 4;
         if (var3 != 0) {
            boolean var4 = false;
            boolean var5 = false;
            boolean var6 = false;
            boolean var7 = false;
            boolean var8 = false;
            int var9 = 0;

            byte var12;
            byte var13;
            byte var14;
            byte var15;
            for(int var16 = var3 - 1; var16 > 0; --var16) {
               var12 = base64Alphabet[var0[var9++]];
               var13 = base64Alphabet[var0[var9++]];
               var14 = base64Alphabet[var0[var9++]];
               var15 = base64Alphabet[var0[var9++]];
               if (var12 == -1 || var13 == -1 || var14 == -1 || var15 == -1) {
                  throw new Base64DecodingException("decoding.general");
               }

               var1.write((byte)(var12 << 2 | var13 >> 4));
               var1.write((byte)((var13 & 15) << 4 | var14 >> 2 & 15));
               var1.write((byte)(var14 << 6 | var15));
            }

            var12 = base64Alphabet[var0[var9++]];
            var13 = base64Alphabet[var0[var9++]];
            if (var12 != -1 && var13 != -1) {
               byte var10;
               var14 = base64Alphabet[var10 = var0[var9++]];
               byte var11;
               var15 = base64Alphabet[var11 = var0[var9++]];
               if (var14 != -1 && var15 != -1) {
                  var1.write((byte)(var12 << 2 | var13 >> 4));
                  var1.write((byte)((var13 & 15) << 4 | var14 >> 2 & 15));
                  var1.write((byte)(var14 << 6 | var15));
               } else if (isPad(var10) && isPad(var11)) {
                  if ((var13 & 15) != 0) {
                     throw new Base64DecodingException("decoding.general");
                  }

                  var1.write((byte)(var12 << 2 | var13 >> 4));
               } else {
                  if (isPad(var10) || !isPad(var11)) {
                     throw new Base64DecodingException("decoding.general");
                  }

                  if ((var14 & 3) != 0) {
                     throw new Base64DecodingException("decoding.general");
                  }

                  var1.write((byte)(var12 << 2 | var13 >> 4));
                  var1.write((byte)((var13 & 15) << 4 | var14 >> 2 & 15));
               }

            } else {
               throw new Base64DecodingException("decoding.general");
            }
         }
      }
   }

   public static final void decode(InputStream var0, OutputStream var1) throws Base64DecodingException, IOException {
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      int var6 = 0;
      byte[] var7 = new byte[4];

      int var8;
      byte var9;
      byte var13;
      byte var14;
      byte var15;
      byte var16;
      while((var8 = var0.read()) > 0) {
         var9 = (byte)var8;
         if (!isWhiteSpace(var9)) {
            if (isPad(var9)) {
               var7[var6++] = var9;
               if (var6 == 3) {
                  var7[var6++] = (byte)var0.read();
               }
               break;
            }

            if ((var7[var6++] = var9) == -1) {
               throw new Base64DecodingException("decoding.general");
            }

            if (var6 == 4) {
               var6 = 0;
               var13 = base64Alphabet[var7[0]];
               var14 = base64Alphabet[var7[1]];
               var15 = base64Alphabet[var7[2]];
               var16 = base64Alphabet[var7[3]];
               var1.write((byte)(var13 << 2 | var14 >> 4));
               var1.write((byte)((var14 & 15) << 4 | var15 >> 2 & 15));
               var1.write((byte)(var15 << 6 | var16));
            }
         }
      }

      var9 = var7[0];
      byte var10 = var7[1];
      byte var11 = var7[2];
      byte var12 = var7[3];
      var13 = base64Alphabet[var9];
      var14 = base64Alphabet[var10];
      var15 = base64Alphabet[var11];
      var16 = base64Alphabet[var12];
      if (var15 != -1 && var16 != -1) {
         var1.write((byte)(var13 << 2 | var14 >> 4));
         var1.write((byte)((var14 & 15) << 4 | var15 >> 2 & 15));
         var1.write((byte)(var15 << 6 | var16));
      } else if (isPad(var11) && isPad(var12)) {
         if ((var14 & 15) != 0) {
            throw new Base64DecodingException("decoding.general");
         }

         var1.write((byte)(var13 << 2 | var14 >> 4));
      } else {
         if (isPad(var11) || !isPad(var12)) {
            throw new Base64DecodingException("decoding.general");
         }

         var15 = base64Alphabet[var11];
         if ((var15 & 3) != 0) {
            throw new Base64DecodingException("decoding.general");
         }

         var1.write((byte)(var13 << 2 | var14 >> 4));
         var1.write((byte)((var14 & 15) << 4 | var15 >> 2 & 15));
      }

   }

   protected static final int removeWhiteSpace(byte[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            byte var4 = var0[var3];
            if (!isWhiteSpace(var4)) {
               var0[var1++] = var4;
            }
         }

         return var1;
      }
   }

   static {
      int var0;
      for(var0 = 0; var0 < 255; ++var0) {
         base64Alphabet[var0] = -1;
      }

      for(var0 = 90; var0 >= 65; --var0) {
         base64Alphabet[var0] = (byte)(var0 - 65);
      }

      for(var0 = 122; var0 >= 97; --var0) {
         base64Alphabet[var0] = (byte)(var0 - 97 + 26);
      }

      for(var0 = 57; var0 >= 48; --var0) {
         base64Alphabet[var0] = (byte)(var0 - 48 + 52);
      }

      base64Alphabet[43] = 62;
      base64Alphabet[47] = 63;

      for(var0 = 0; var0 <= 25; ++var0) {
         lookUpBase64Alphabet[var0] = (char)(65 + var0);
      }

      var0 = 26;

      int var1;
      for(var1 = 0; var0 <= 51; ++var1) {
         lookUpBase64Alphabet[var0] = (char)(97 + var1);
         ++var0;
      }

      var0 = 52;

      for(var1 = 0; var0 <= 61; ++var1) {
         lookUpBase64Alphabet[var0] = (char)(48 + var1);
         ++var0;
      }

      lookUpBase64Alphabet[62] = '+';
      lookUpBase64Alphabet[63] = '/';
   }
}
