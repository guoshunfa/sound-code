package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.xml.transform.TransformerException;

final class CharInfo {
   private HashMap m_charToString;
   public static final String HTML_ENTITIES_RESOURCE = "com.sun.org.apache.xml.internal.serializer.HTMLEntities";
   public static final String XML_ENTITIES_RESOURCE = "com.sun.org.apache.xml.internal.serializer.XMLEntities";
   public static final char S_HORIZONAL_TAB = '\t';
   public static final char S_LINEFEED = '\n';
   public static final char S_CARRIAGERETURN = '\r';
   final boolean onlyQuotAmpLtGt;
   private static final int ASCII_MAX = 128;
   private boolean[] isSpecialAttrASCII;
   private boolean[] isSpecialTextASCII;
   private boolean[] isCleanTextASCII;
   private int[] array_of_bits;
   private static final int SHIFT_PER_WORD = 5;
   private static final int LOW_ORDER_BITMASK = 31;
   private int firstWordNotUsed;
   private static HashMap m_getCharInfoCache = new HashMap();

   private CharInfo(String entitiesResource, String method) {
      this(entitiesResource, method, false);
   }

   private CharInfo(String entitiesResource, String method, boolean internal) {
      this.m_charToString = new HashMap();
      this.isSpecialAttrASCII = new boolean[128];
      this.isSpecialTextASCII = new boolean[128];
      this.isCleanTextASCII = new boolean[128];
      this.array_of_bits = this.createEmptySetOfIntegers(65535);
      ResourceBundle entities = null;
      boolean noExtraEntities = true;

      try {
         if (internal) {
            entities = PropertyResourceBundle.getBundle(entitiesResource);
         } else {
            ClassLoader cl = SecuritySupport.getContextClassLoader();
            if (cl != null) {
               entities = PropertyResourceBundle.getBundle(entitiesResource, Locale.getDefault(), cl);
            }
         }
      } catch (Exception var28) {
      }

      String err;
      if (entities != null) {
         Enumeration keys = entities.getKeys();

         while(keys.hasMoreElements()) {
            err = (String)keys.nextElement();
            String value = entities.getString(err);
            int code = Integer.parseInt(value);
            this.defineEntity(err, (char)code);
            if (this.extraEntity(code)) {
               noExtraEntities = false;
            }
         }

         this.set(10);
         this.set(13);
      } else {
         InputStream is = null;
         err = null;

         try {
            if (internal) {
               is = CharInfo.class.getResourceAsStream(entitiesResource);
            } else {
               ClassLoader cl = SecuritySupport.getContextClassLoader();
               if (cl != null) {
                  try {
                     is = cl.getResourceAsStream(entitiesResource);
                  } catch (Exception var27) {
                     err = var27.getMessage();
                  }
               }

               if (is == null) {
                  try {
                     URL url = new URL(entitiesResource);
                     is = url.openStream();
                  } catch (Exception var26) {
                     err = var26.getMessage();
                  }
               }
            }

            if (is == null) {
               throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_FIND", new Object[]{entitiesResource, err}));
            }

            BufferedReader reader;
            try {
               reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            } catch (UnsupportedEncodingException var25) {
               reader = new BufferedReader(new InputStreamReader(is));
            }

            String line = reader.readLine();

            while(true) {
               while(line != null) {
                  if (line.length() != 0 && line.charAt(0) != '#') {
                     int index = line.indexOf(32);
                     if (index > 1) {
                        String name = line.substring(0, index);
                        ++index;
                        if (index < line.length()) {
                           String value = line.substring(index);
                           index = value.indexOf(32);
                           if (index > 0) {
                              value = value.substring(0, index);
                           }

                           int code = Integer.parseInt(value);
                           this.defineEntity(name, (char)code);
                           if (this.extraEntity(code)) {
                              noExtraEntities = false;
                           }
                        }
                     }

                     line = reader.readLine();
                  } else {
                     line = reader.readLine();
                  }
               }

               is.close();
               this.set(10);
               this.set(13);
               break;
            }
         } catch (Exception var29) {
            throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_LOAD", new Object[]{entitiesResource, var29.toString(), entitiesResource, var29.toString()}));
         } finally {
            if (is != null) {
               try {
                  is.close();
               } catch (Exception var24) {
               }
            }

         }
      }

      int i;
      for(i = 0; i < 128; ++i) {
         if ((32 > i && 10 != i && 13 != i && 9 != i || this.get(i)) && 34 != i) {
            this.isCleanTextASCII[i] = false;
            this.isSpecialTextASCII[i] = true;
         } else {
            this.isCleanTextASCII[i] = true;
            this.isSpecialTextASCII[i] = false;
         }
      }

      this.onlyQuotAmpLtGt = noExtraEntities;

      for(i = 0; i < 128; ++i) {
         this.isSpecialAttrASCII[i] = this.get(i);
      }

      if ("xml".equals(method)) {
         this.isSpecialAttrASCII[9] = true;
      }

   }

   private void defineEntity(String name, char value) {
      StringBuilder sb = new StringBuilder("&");
      sb.append(name);
      sb.append(';');
      String entityString = sb.toString();
      this.defineChar2StringMapping(entityString, value);
   }

   String getOutputStringForChar(char value) {
      CharInfo.CharKey charKey = new CharInfo.CharKey();
      charKey.setChar(value);
      return (String)this.m_charToString.get(charKey);
   }

   final boolean isSpecialAttrChar(int value) {
      return value < 128 ? this.isSpecialAttrASCII[value] : this.get(value);
   }

   final boolean isSpecialTextChar(int value) {
      return value < 128 ? this.isSpecialTextASCII[value] : this.get(value);
   }

   final boolean isTextASCIIClean(int value) {
      return this.isCleanTextASCII[value];
   }

   static CharInfo getCharInfoInternal(String entitiesFileName, String method) {
      CharInfo charInfo = (CharInfo)m_getCharInfoCache.get(entitiesFileName);
      if (charInfo != null) {
         return charInfo;
      } else {
         charInfo = new CharInfo(entitiesFileName, method, true);
         m_getCharInfoCache.put(entitiesFileName, charInfo);
         return charInfo;
      }
   }

   static CharInfo getCharInfo(String entitiesFileName, String method) {
      try {
         return new CharInfo(entitiesFileName, method, false);
      } catch (Exception var5) {
         String absoluteEntitiesFileName;
         if (entitiesFileName.indexOf(58) < 0) {
            absoluteEntitiesFileName = SystemIDResolver.getAbsoluteURIFromRelative(entitiesFileName);
         } else {
            try {
               absoluteEntitiesFileName = SystemIDResolver.getAbsoluteURI(entitiesFileName, (String)null);
            } catch (TransformerException var4) {
               throw new WrappedRuntimeException(var4);
            }
         }

         return new CharInfo(absoluteEntitiesFileName, method, false);
      }
   }

   private static int arrayIndex(int i) {
      return i >> 5;
   }

   private static int bit(int i) {
      int ret = 1 << (i & 31);
      return ret;
   }

   private int[] createEmptySetOfIntegers(int max) {
      this.firstWordNotUsed = 0;
      int[] arr = new int[arrayIndex(max - 1) + 1];
      return arr;
   }

   private final void set(int i) {
      this.setASCIIdirty(i);
      int j = i >> 5;
      int k = j + 1;
      if (this.firstWordNotUsed < k) {
         this.firstWordNotUsed = k;
      }

      int[] var10000 = this.array_of_bits;
      var10000[j] |= 1 << (i & 31);
   }

   private final boolean get(int i) {
      boolean in_the_set = false;
      int j = i >> 5;
      if (j < this.firstWordNotUsed) {
         in_the_set = (this.array_of_bits[j] & 1 << (i & 31)) != 0;
      }

      return in_the_set;
   }

   private boolean extraEntity(int entityValue) {
      boolean extra = false;
      if (entityValue < 128) {
         switch(entityValue) {
         case 34:
         case 38:
         case 60:
         case 62:
            break;
         default:
            extra = true;
         }
      }

      return extra;
   }

   private void setASCIIdirty(int j) {
      if (0 <= j && j < 128) {
         this.isCleanTextASCII[j] = false;
         this.isSpecialTextASCII[j] = true;
      }

   }

   private void setASCIIclean(int j) {
      if (0 <= j && j < 128) {
         this.isCleanTextASCII[j] = true;
         this.isSpecialTextASCII[j] = false;
      }

   }

   private void defineChar2StringMapping(String outputString, char inputChar) {
      CharInfo.CharKey character = new CharInfo.CharKey(inputChar);
      this.m_charToString.put(character, outputString);
      this.set(inputChar);
   }

   private static class CharKey {
      private char m_char;

      public CharKey(char key) {
         this.m_char = key;
      }

      public CharKey() {
      }

      public final void setChar(char c) {
         this.m_char = c;
      }

      public final int hashCode() {
         return this.m_char;
      }

      public final boolean equals(Object obj) {
         return ((CharInfo.CharKey)obj).m_char == this.m_char;
      }
   }
}
