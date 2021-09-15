package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.InputStream;

final class MimeUtility {
   private static final boolean ignoreUnknownEncoding = PropUtil.getBooleanSystemProperty("mail.mime.ignoreunknownencoding", false);

   private MimeUtility() {
   }

   public static InputStream decode(InputStream is, String encoding) throws DecodingException {
      if (encoding.equalsIgnoreCase("base64")) {
         return new BASE64DecoderStream(is);
      } else if (encoding.equalsIgnoreCase("quoted-printable")) {
         return new QPDecoderStream(is);
      } else if (!encoding.equalsIgnoreCase("uuencode") && !encoding.equalsIgnoreCase("x-uuencode") && !encoding.equalsIgnoreCase("x-uue")) {
         if (!encoding.equalsIgnoreCase("binary") && !encoding.equalsIgnoreCase("7bit") && !encoding.equalsIgnoreCase("8bit")) {
            if (!ignoreUnknownEncoding) {
               throw new DecodingException("Unknown encoding: " + encoding);
            } else {
               return is;
            }
         } else {
            return is;
         }
      } else {
         return new UUDecoderStream(is);
      }
   }
}
