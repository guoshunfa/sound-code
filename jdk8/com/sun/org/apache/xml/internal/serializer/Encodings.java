package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public final class Encodings {
   private static final int m_defaultLastPrintable = 127;
   private static final String ENCODINGS_FILE = "com/sun/org/apache/xml/internal/serializer/Encodings.properties";
   private static final String ENCODINGS_PROP = "com.sun.org.apache.xalan.internal.serialize.encodings";
   static final String DEFAULT_MIME_ENCODING = "UTF-8";
   private static final Encodings.EncodingInfos _encodingInfos = new Encodings.EncodingInfos();

   static Writer getWriter(OutputStream output, String encoding) throws UnsupportedEncodingException {
      EncodingInfo ei = _encodingInfos.findEncoding(toUpperCaseFast(encoding));
      if (ei != null) {
         try {
            return new BufferedWriter(new OutputStreamWriter(output, ei.javaName));
         } catch (UnsupportedEncodingException var4) {
         }
      }

      return new BufferedWriter(new OutputStreamWriter(output, encoding));
   }

   public static int getLastPrintable() {
      return 127;
   }

   static EncodingInfo getEncodingInfo(String encoding) {
      String normalizedEncoding = toUpperCaseFast(encoding);
      EncodingInfo ei = _encodingInfos.findEncoding(normalizedEncoding);
      if (ei == null) {
         try {
            Charset c = Charset.forName(encoding);
            String name = c.name();
            ei = new EncodingInfo(name, name);
            _encodingInfos.putEncoding(normalizedEncoding, ei);
         } catch (UnsupportedCharsetException | IllegalCharsetNameException var5) {
            ei = new EncodingInfo((String)null, (String)null);
         }
      }

      return ei;
   }

   private static String toUpperCaseFast(String s) {
      boolean different = false;
      int mx = s.length();
      char[] chars = new char[mx];

      for(int i = 0; i < mx; ++i) {
         char ch = s.charAt(i);
         if ('a' <= ch && ch <= 'z') {
            ch = (char)(ch + -32);
            different = true;
         }

         chars[i] = ch;
      }

      String upper;
      if (different) {
         upper = String.valueOf(chars);
      } else {
         upper = s;
      }

      return upper;
   }

   static String getMimeEncoding(String encoding) {
      if (null == encoding) {
         try {
            encoding = SecuritySupport.getSystemProperty("file.encoding", "UTF8");
            if (null != encoding) {
               String jencoding = !encoding.equalsIgnoreCase("Cp1252") && !encoding.equalsIgnoreCase("ISO8859_1") && !encoding.equalsIgnoreCase("8859_1") && !encoding.equalsIgnoreCase("UTF8") ? convertJava2MimeEncoding(encoding) : "UTF-8";
               encoding = null != jencoding ? jencoding : "UTF-8";
            } else {
               encoding = "UTF-8";
            }
         } catch (SecurityException var2) {
            encoding = "UTF-8";
         }
      } else {
         encoding = convertJava2MimeEncoding(encoding);
      }

      return encoding;
   }

   private static String convertJava2MimeEncoding(String encoding) {
      EncodingInfo enc = _encodingInfos.getEncodingFromJavaKey(toUpperCaseFast(encoding));
      return null != enc ? enc.name : encoding;
   }

   public static String convertMime2JavaEncoding(String encoding) {
      EncodingInfo info = _encodingInfos.findEncoding(toUpperCaseFast(encoding));
      return info != null ? info.javaName : encoding;
   }

   static boolean isHighUTF16Surrogate(char ch) {
      return '\ud800' <= ch && ch <= '\udbff';
   }

   static boolean isLowUTF16Surrogate(char ch) {
      return '\udc00' <= ch && ch <= '\udfff';
   }

   static int toCodePoint(char highSurrogate, char lowSurrogate) {
      int codePoint = (highSurrogate - '\ud800' << 10) + (lowSurrogate - '\udc00') + 65536;
      return codePoint;
   }

   static int toCodePoint(char ch) {
      return ch;
   }

   private static final class EncodingInfos {
      private final Map<String, EncodingInfo> _encodingTableKeyJava;
      private final Map<String, EncodingInfo> _encodingTableKeyMime;
      private final Map<String, EncodingInfo> _encodingDynamicTable;

      private EncodingInfos() {
         this._encodingTableKeyJava = new HashMap();
         this._encodingTableKeyMime = new HashMap();
         this._encodingDynamicTable = Collections.synchronizedMap(new HashMap());
         this.loadEncodingInfo();
      }

      private InputStream openEncodingsFileStream() throws MalformedURLException, IOException {
         String urlString = null;
         InputStream is = null;

         try {
            urlString = SecuritySupport.getSystemProperty("com.sun.org.apache.xalan.internal.serialize.encodings", "");
         } catch (SecurityException var4) {
         }

         if (urlString != null && urlString.length() > 0) {
            URL url = new URL(urlString);
            is = url.openStream();
         }

         if (is == null) {
            is = SecuritySupport.getResourceAsStream("com/sun/org/apache/xml/internal/serializer/Encodings.properties");
         }

         return is;
      }

      private Properties loadProperties() throws MalformedURLException, IOException {
         Properties props = new Properties();
         InputStream is = this.openEncodingsFileStream();
         Throwable var3 = null;

         try {
            if (is != null) {
               props.load(is);
            }
         } catch (Throwable var12) {
            var3 = var12;
            throw var12;
         } finally {
            if (is != null) {
               if (var3 != null) {
                  try {
                     is.close();
                  } catch (Throwable var11) {
                     var3.addSuppressed(var11);
                  }
               } else {
                  is.close();
               }
            }

         }

         return props;
      }

      private String[] parseMimeTypes(String val) {
         int pos = val.indexOf(32);
         if (pos < 0) {
            return new String[]{val};
         } else {
            StringTokenizer st = new StringTokenizer(val.substring(0, pos), ",");
            String[] values = new String[st.countTokens()];

            for(int i = 0; st.hasMoreTokens(); ++i) {
               values[i] = st.nextToken();
            }

            return values;
         }
      }

      private String findCharsetNameFor(String name) {
         try {
            return Charset.forName(name).name();
         } catch (Exception var3) {
            return null;
         }
      }

      private String findCharsetNameFor(String javaName, String[] mimes) {
         String cs = this.findCharsetNameFor(javaName);
         if (cs != null) {
            return javaName;
         } else {
            String[] var4 = mimes;
            int var5 = mimes.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String m = var4[var6];
               cs = this.findCharsetNameFor(m);
               if (cs != null) {
                  break;
               }
            }

            return cs;
         }
      }

      private void loadEncodingInfo() {
         try {
            Properties props = this.loadProperties();
            Enumeration keys = props.keys();
            HashMap canonicals = new HashMap();

            while(true) {
               String javaName;
               String[] mimes;
               String charsetName;
               do {
                  if (!keys.hasMoreElements()) {
                     Iterator var15 = this._encodingTableKeyJava.entrySet().iterator();

                     while(var15.hasNext()) {
                        Map.Entry<String, EncodingInfo> e = (Map.Entry)var15.next();
                        e.setValue(canonicals.get(Encodings.toUpperCaseFast(((EncodingInfo)e.getValue()).javaName)));
                     }

                     return;
                  }

                  javaName = (String)keys.nextElement();
                  mimes = this.parseMimeTypes(props.getProperty(javaName));
                  charsetName = this.findCharsetNameFor(javaName, mimes);
               } while(charsetName == null);

               String kj = Encodings.toUpperCaseFast(javaName);
               String kc = Encodings.toUpperCaseFast(charsetName);

               for(int i = 0; i < mimes.length; ++i) {
                  String mimeName = mimes[i];
                  String km = Encodings.toUpperCaseFast(mimeName);
                  EncodingInfo info = new EncodingInfo(mimeName, charsetName);
                  this._encodingTableKeyMime.put(km, info);
                  if (!canonicals.containsKey(kc)) {
                     canonicals.put(kc, info);
                     this._encodingTableKeyJava.put(kc, info);
                  }

                  this._encodingTableKeyJava.put(kj, info);
               }
            }
         } catch (MalformedURLException var13) {
            throw new WrappedRuntimeException(var13);
         } catch (IOException var14) {
            throw new WrappedRuntimeException(var14);
         }
      }

      EncodingInfo findEncoding(String normalizedEncoding) {
         EncodingInfo info = (EncodingInfo)this._encodingTableKeyJava.get(normalizedEncoding);
         if (info == null) {
            info = (EncodingInfo)this._encodingTableKeyMime.get(normalizedEncoding);
         }

         if (info == null) {
            info = (EncodingInfo)this._encodingDynamicTable.get(normalizedEncoding);
         }

         return info;
      }

      EncodingInfo getEncodingFromMimeKey(String normalizedMimeName) {
         return (EncodingInfo)this._encodingTableKeyMime.get(normalizedMimeName);
      }

      EncodingInfo getEncodingFromJavaKey(String normalizedJavaName) {
         return (EncodingInfo)this._encodingTableKeyJava.get(normalizedJavaName);
      }

      void putEncoding(String key, EncodingInfo info) {
         this._encodingDynamicTable.put(key, info);
      }

      // $FF: synthetic method
      EncodingInfos(Object x0) {
         this();
      }
   }
}
