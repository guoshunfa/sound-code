package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.util.EncodingMap;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Encodings {
   static final int DEFAULT_LAST_PRINTABLE = 127;
   static final int LAST_PRINTABLE_UNICODE = 65535;
   static final String[] UNICODE_ENCODINGS = new String[]{"Unicode", "UnicodeBig", "UnicodeLittle", "GB2312", "UTF8", "UTF-16"};
   static final String DEFAULT_ENCODING = "UTF8";
   private static final Map<String, EncodingInfo> _encodings = new ConcurrentHashMap();
   static final String JIS_DANGER_CHARS = "\\~\u007f¢£¥¬—―‖…‾‾∥∯〜＼～￠￡￢￣";

   static EncodingInfo getEncodingInfo(String encoding, boolean allowJavaNames) throws UnsupportedEncodingException {
      EncodingInfo eInfo = null;
      if (encoding == null) {
         if ((eInfo = (EncodingInfo)_encodings.get("UTF8")) != null) {
            return eInfo;
         } else {
            eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping("UTF8"), "UTF8", 65535);
            _encodings.put("UTF8", eInfo);
            return eInfo;
         }
      } else {
         encoding = encoding.toUpperCase(Locale.ENGLISH);
         String jName = EncodingMap.getIANA2JavaMapping(encoding);
         int i;
         if (jName == null) {
            if (!allowJavaNames) {
               throw new UnsupportedEncodingException(encoding);
            } else {
               EncodingInfo.testJavaEncodingName(encoding);
               if ((eInfo = (EncodingInfo)_encodings.get(encoding)) != null) {
                  return eInfo;
               } else {
                  for(i = 0; i < UNICODE_ENCODINGS.length; ++i) {
                     if (UNICODE_ENCODINGS[i].equalsIgnoreCase(encoding)) {
                        eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(encoding), encoding, 65535);
                        break;
                     }
                  }

                  if (i == UNICODE_ENCODINGS.length) {
                     eInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(encoding), encoding, 127);
                  }

                  _encodings.put(encoding, eInfo);
                  return eInfo;
               }
            }
         } else if ((eInfo = (EncodingInfo)_encodings.get(jName)) != null) {
            return eInfo;
         } else {
            for(i = 0; i < UNICODE_ENCODINGS.length; ++i) {
               if (UNICODE_ENCODINGS[i].equalsIgnoreCase(jName)) {
                  eInfo = new EncodingInfo(encoding, jName, 65535);
                  break;
               }
            }

            if (i == UNICODE_ENCODINGS.length) {
               eInfo = new EncodingInfo(encoding, jName, 127);
            }

            _encodings.put(jName, eInfo);
            return eInfo;
         }
      }
   }
}
