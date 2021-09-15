package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import sun.net.www.MessageHeader;
import sun.net.www.MimeTable;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;

public abstract class URLConnection {
   protected URL url;
   protected boolean doInput = true;
   protected boolean doOutput = false;
   private static boolean defaultAllowUserInteraction = false;
   protected boolean allowUserInteraction;
   private static boolean defaultUseCaches = true;
   protected boolean useCaches;
   protected long ifModifiedSince;
   protected boolean connected;
   private int connectTimeout;
   private int readTimeout;
   private MessageHeader requests;
   private static FileNameMap fileNameMap;
   private static boolean fileNameMapLoaded = false;
   static ContentHandlerFactory factory;
   private static Hashtable<String, ContentHandler> handlers = new Hashtable();
   private static final String contentClassPrefix = "sun.net.www.content";
   private static final String contentPathProp = "java.content.handler.pkgs";

   public static synchronized FileNameMap getFileNameMap() {
      if (fileNameMap == null && !fileNameMapLoaded) {
         fileNameMap = MimeTable.loadTable();
         fileNameMapLoaded = true;
      }

      return new FileNameMap() {
         private FileNameMap map;

         {
            this.map = URLConnection.fileNameMap;
         }

         public String getContentTypeFor(String var1) {
            return this.map.getContentTypeFor(var1);
         }
      };
   }

   public static void setFileNameMap(FileNameMap var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkSetFactory();
      }

      fileNameMap = var0;
   }

   public abstract void connect() throws IOException;

   public void setConnectTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeout can not be negative");
      } else {
         this.connectTimeout = var1;
      }
   }

   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setReadTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeout can not be negative");
      } else {
         this.readTimeout = var1;
      }
   }

   public int getReadTimeout() {
      return this.readTimeout;
   }

   protected URLConnection(URL var1) {
      this.allowUserInteraction = defaultAllowUserInteraction;
      this.useCaches = defaultUseCaches;
      this.ifModifiedSince = 0L;
      this.connected = false;
      this.url = var1;
   }

   public URL getURL() {
      return this.url;
   }

   public int getContentLength() {
      long var1 = this.getContentLengthLong();
      return var1 > 2147483647L ? -1 : (int)var1;
   }

   public long getContentLengthLong() {
      return this.getHeaderFieldLong("content-length", -1L);
   }

   public String getContentType() {
      return this.getHeaderField("content-type");
   }

   public String getContentEncoding() {
      return this.getHeaderField("content-encoding");
   }

   public long getExpiration() {
      return this.getHeaderFieldDate("expires", 0L);
   }

   public long getDate() {
      return this.getHeaderFieldDate("date", 0L);
   }

   public long getLastModified() {
      return this.getHeaderFieldDate("last-modified", 0L);
   }

   public String getHeaderField(String var1) {
      return null;
   }

   public Map<String, List<String>> getHeaderFields() {
      return Collections.emptyMap();
   }

   public int getHeaderFieldInt(String var1, int var2) {
      String var3 = this.getHeaderField(var1);

      try {
         return Integer.parseInt(var3);
      } catch (Exception var5) {
         return var2;
      }
   }

   public long getHeaderFieldLong(String var1, long var2) {
      String var4 = this.getHeaderField(var1);

      try {
         return Long.parseLong(var4);
      } catch (Exception var6) {
         return var2;
      }
   }

   public long getHeaderFieldDate(String var1, long var2) {
      String var4 = this.getHeaderField(var1);

      try {
         return Date.parse(var4);
      } catch (Exception var6) {
         return var2;
      }
   }

   public String getHeaderFieldKey(int var1) {
      return null;
   }

   public String getHeaderField(int var1) {
      return null;
   }

   public Object getContent() throws IOException {
      this.getInputStream();
      return this.getContentHandler().getContent(this);
   }

   public Object getContent(Class[] var1) throws IOException {
      this.getInputStream();
      return this.getContentHandler().getContent(this, var1);
   }

   public Permission getPermission() throws IOException {
      return SecurityConstants.ALL_PERMISSION;
   }

   public InputStream getInputStream() throws IOException {
      throw new UnknownServiceException("protocol doesn't support input");
   }

   public OutputStream getOutputStream() throws IOException {
      throw new UnknownServiceException("protocol doesn't support output");
   }

   public String toString() {
      return this.getClass().getName() + ":" + this.url;
   }

   public void setDoInput(boolean var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         this.doInput = var1;
      }
   }

   public boolean getDoInput() {
      return this.doInput;
   }

   public void setDoOutput(boolean var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         this.doOutput = var1;
      }
   }

   public boolean getDoOutput() {
      return this.doOutput;
   }

   public void setAllowUserInteraction(boolean var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         this.allowUserInteraction = var1;
      }
   }

   public boolean getAllowUserInteraction() {
      return this.allowUserInteraction;
   }

   public static void setDefaultAllowUserInteraction(boolean var0) {
      defaultAllowUserInteraction = var0;
   }

   public static boolean getDefaultAllowUserInteraction() {
      return defaultAllowUserInteraction;
   }

   public void setUseCaches(boolean var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         this.useCaches = var1;
      }
   }

   public boolean getUseCaches() {
      return this.useCaches;
   }

   public void setIfModifiedSince(long var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         this.ifModifiedSince = var1;
      }
   }

   public long getIfModifiedSince() {
      return this.ifModifiedSince;
   }

   public boolean getDefaultUseCaches() {
      return defaultUseCaches;
   }

   public void setDefaultUseCaches(boolean var1) {
      defaultUseCaches = var1;
   }

   public void setRequestProperty(String var1, String var2) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else if (var1 == null) {
         throw new NullPointerException("key is null");
      } else {
         if (this.requests == null) {
            this.requests = new MessageHeader();
         }

         this.requests.set(var1, var2);
      }
   }

   public void addRequestProperty(String var1, String var2) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else if (var1 == null) {
         throw new NullPointerException("key is null");
      } else {
         if (this.requests == null) {
            this.requests = new MessageHeader();
         }

         this.requests.add(var1, var2);
      }
   }

   public String getRequestProperty(String var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         return this.requests == null ? null : this.requests.findValue(var1);
      }
   }

   public Map<String, List<String>> getRequestProperties() {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         return this.requests == null ? Collections.emptyMap() : this.requests.getHeaders((String[])null);
      }
   }

   /** @deprecated */
   @Deprecated
   public static void setDefaultRequestProperty(String var0, String var1) {
   }

   /** @deprecated */
   @Deprecated
   public static String getDefaultRequestProperty(String var0) {
      return null;
   }

   public static synchronized void setContentHandlerFactory(ContentHandlerFactory var0) {
      if (factory != null) {
         throw new Error("factory already defined");
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkSetFactory();
         }

         factory = var0;
      }
   }

   synchronized ContentHandler getContentHandler() throws UnknownServiceException {
      String var1 = this.stripOffParameters(this.getContentType());
      ContentHandler var2 = null;
      if (var1 == null) {
         throw new UnknownServiceException("no content-type");
      } else {
         try {
            var2 = (ContentHandler)handlers.get(var1);
            if (var2 != null) {
               return var2;
            }
         } catch (Exception var5) {
         }

         if (factory != null) {
            var2 = factory.createContentHandler(var1);
         }

         if (var2 == null) {
            try {
               var2 = this.lookupContentHandlerClassFor(var1);
            } catch (Exception var4) {
               var4.printStackTrace();
               var2 = UnknownContentHandler.INSTANCE;
            }

            handlers.put(var1, var2);
         }

         return var2;
      }
   }

   private String stripOffParameters(String var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = var1.indexOf(59);
         return var2 > 0 ? var1.substring(0, var2) : var1;
      }
   }

   private ContentHandler lookupContentHandlerClassFor(String var1) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
      String var2 = this.typeToPackageName(var1);
      String var3 = this.getContentHandlerPkgPrefixes();
      StringTokenizer var4 = new StringTokenizer(var3, "|");

      while(var4.hasMoreTokens()) {
         String var5 = var4.nextToken().trim();

         try {
            String var6 = var5 + "." + var2;
            Class var7 = null;

            try {
               var7 = Class.forName(var6);
            } catch (ClassNotFoundException var10) {
               ClassLoader var9 = ClassLoader.getSystemClassLoader();
               if (var9 != null) {
                  var7 = var9.loadClass(var6);
               }
            }

            if (var7 != null) {
               ContentHandler var8 = (ContentHandler)var7.newInstance();
               return var8;
            }
         } catch (Exception var11) {
         }
      }

      return UnknownContentHandler.INSTANCE;
   }

   private String typeToPackageName(String var1) {
      var1 = var1.toLowerCase();
      int var2 = var1.length();
      char[] var3 = new char[var2];
      var1.getChars(0, var2, var3, 0);

      for(int var4 = 0; var4 < var2; ++var4) {
         char var5 = var3[var4];
         if (var5 == '/') {
            var3[var4] = '.';
         } else if (('A' > var5 || var5 > 'Z') && ('a' > var5 || var5 > 'z') && ('0' > var5 || var5 > '9')) {
            var3[var4] = '_';
         }
      }

      return new String(var3);
   }

   private String getContentHandlerPkgPrefixes() {
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.content.handler.pkgs", "")));
      if (var1 != "") {
         var1 = var1 + "|";
      }

      return var1 + "sun.net.www.content";
   }

   public static String guessContentTypeFromName(String var0) {
      return getFileNameMap().getContentTypeFor(var0);
   }

   public static String guessContentTypeFromStream(InputStream var0) throws IOException {
      if (!var0.markSupported()) {
         return null;
      } else {
         var0.mark(16);
         int var1 = var0.read();
         int var2 = var0.read();
         int var3 = var0.read();
         int var4 = var0.read();
         int var5 = var0.read();
         int var6 = var0.read();
         int var7 = var0.read();
         int var8 = var0.read();
         int var9 = var0.read();
         int var10 = var0.read();
         int var11 = var0.read();
         int var12 = var0.read();
         int var13 = var0.read();
         int var14 = var0.read();
         int var15 = var0.read();
         int var16 = var0.read();
         var0.reset();
         if (var1 == 202 && var2 == 254 && var3 == 186 && var4 == 190) {
            return "application/java-vm";
         } else if (var1 == 172 && var2 == 237) {
            return "application/x-java-serialized-object";
         } else {
            if (var1 == 60) {
               if (var2 == 33 || var2 == 104 && (var3 == 116 && var4 == 109 && var5 == 108 || var3 == 101 && var4 == 97 && var5 == 100) || var2 == 98 && var3 == 111 && var4 == 100 && var5 == 121 || var2 == 72 && (var3 == 84 && var4 == 77 && var5 == 76 || var3 == 69 && var4 == 65 && var5 == 68) || var2 == 66 && var3 == 79 && var4 == 68 && var5 == 89) {
                  return "text/html";
               }

               if (var2 == 63 && var3 == 120 && var4 == 109 && var5 == 108 && var6 == 32) {
                  return "application/xml";
               }
            }

            if (var1 == 239 && var2 == 187 && var3 == 191 && var4 == 60 && var5 == 63 && var6 == 120) {
               return "application/xml";
            } else if (var1 == 254 && var2 == 255 && var3 == 0 && var4 == 60 && var5 == 0 && var6 == 63 && var7 == 0 && var8 == 120) {
               return "application/xml";
            } else if (var1 == 255 && var2 == 254 && var3 == 60 && var4 == 0 && var5 == 63 && var6 == 0 && var7 == 120 && var8 == 0) {
               return "application/xml";
            } else if (var1 == 0 && var2 == 0 && var3 == 254 && var4 == 255 && var5 == 0 && var6 == 0 && var7 == 0 && var8 == 60 && var9 == 0 && var10 == 0 && var11 == 0 && var12 == 63 && var13 == 0 && var14 == 0 && var15 == 0 && var16 == 120) {
               return "application/xml";
            } else if (var1 == 255 && var2 == 254 && var3 == 0 && var4 == 0 && var5 == 60 && var6 == 0 && var7 == 0 && var8 == 0 && var9 == 63 && var10 == 0 && var11 == 0 && var12 == 0 && var13 == 120 && var14 == 0 && var15 == 0 && var16 == 0) {
               return "application/xml";
            } else if (var1 == 71 && var2 == 73 && var3 == 70 && var4 == 56) {
               return "image/gif";
            } else if (var1 == 35 && var2 == 100 && var3 == 101 && var4 == 102) {
               return "image/x-bitmap";
            } else if (var1 == 33 && var2 == 32 && var3 == 88 && var4 == 80 && var5 == 77 && var6 == 50) {
               return "image/x-pixmap";
            } else if (var1 == 137 && var2 == 80 && var3 == 78 && var4 == 71 && var5 == 13 && var6 == 10 && var7 == 26 && var8 == 10) {
               return "image/png";
            } else {
               if (var1 == 255 && var2 == 216 && var3 == 255) {
                  if (var4 == 224 || var4 == 238) {
                     return "image/jpeg";
                  }

                  if (var4 == 225 && var7 == 69 && var8 == 120 && var9 == 105 && var10 == 102 && var11 == 0) {
                     return "image/jpeg";
                  }
               }

               if (var1 == 208 && var2 == 207 && var3 == 17 && var4 == 224 && var5 == 161 && var6 == 177 && var7 == 26 && var8 == 225 && checkfpx(var0)) {
                  return "image/vnd.fpx";
               } else if (var1 == 46 && var2 == 115 && var3 == 110 && var4 == 100) {
                  return "audio/basic";
               } else if (var1 == 100 && var2 == 110 && var3 == 115 && var4 == 46) {
                  return "audio/basic";
               } else if (var1 == 82 && var2 == 73 && var3 == 70 && var4 == 70) {
                  return "audio/x-wav";
               } else {
                  return null;
               }
            }
         }
      }
   }

   private static boolean checkfpx(InputStream var0) throws IOException {
      var0.mark(256);
      long var1 = 28L;
      long var3;
      if ((var3 = skipForward(var0, var1)) < var1) {
         var0.reset();
         return false;
      } else {
         int[] var5 = new int[16];
         if (readBytes(var5, 2, var0) < 0) {
            var0.reset();
            return false;
         } else {
            int var6 = var5[0];
            var3 += 2L;
            if (readBytes(var5, 2, var0) < 0) {
               var0.reset();
               return false;
            } else {
               int var7;
               if (var6 == 254) {
                  var7 = var5[0];
                  var7 += var5[1] << 8;
               } else {
                  var7 = var5[0] << 8;
                  var7 += var5[1];
               }

               var3 += 2L;
               var1 = 48L - var3;
               long var8 = 0L;
               if ((var8 = skipForward(var0, var1)) < var1) {
                  var0.reset();
                  return false;
               } else {
                  var3 += var8;
                  if (readBytes(var5, 4, var0) < 0) {
                     var0.reset();
                     return false;
                  } else {
                     int var10;
                     if (var6 == 254) {
                        var10 = var5[0];
                        var10 += var5[1] << 8;
                        var10 += var5[2] << 16;
                        var10 += var5[3] << 24;
                     } else {
                        var10 = var5[0] << 24;
                        var10 += var5[1] << 16;
                        var10 += var5[2] << 8;
                        var10 += var5[3];
                     }

                     var3 += 4L;
                     var0.reset();
                     var1 = 512L + (long)(1 << var7) * (long)var10 + 80L;
                     if (var1 < 0L) {
                        return false;
                     } else {
                        var0.mark((int)var1 + 48);
                        if (skipForward(var0, var1) < var1) {
                           var0.reset();
                           return false;
                        } else if (readBytes(var5, 16, var0) < 0) {
                           var0.reset();
                           return false;
                        } else if (var6 == 254 && var5[0] == 0 && var5[2] == 97 && var5[3] == 86 && var5[4] == 84 && var5[5] == 193 && var5[6] == 206 && var5[7] == 17 && var5[8] == 133 && var5[9] == 83 && var5[10] == 0 && var5[11] == 170 && var5[12] == 0 && var5[13] == 161 && var5[14] == 249 && var5[15] == 91) {
                           var0.reset();
                           return true;
                        } else if (var5[3] == 0 && var5[1] == 97 && var5[0] == 86 && var5[5] == 84 && var5[4] == 193 && var5[7] == 206 && var5[6] == 17 && var5[8] == 133 && var5[9] == 83 && var5[10] == 0 && var5[11] == 170 && var5[12] == 0 && var5[13] == 161 && var5[14] == 249 && var5[15] == 91) {
                           var0.reset();
                           return true;
                        } else {
                           var0.reset();
                           return false;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static int readBytes(int[] var0, int var1, InputStream var2) throws IOException {
      byte[] var3 = new byte[var1];
      if (var2.read(var3, 0, var1) < var1) {
         return -1;
      } else {
         for(int var4 = 0; var4 < var1; ++var4) {
            var0[var4] = var3[var4] & 255;
         }

         return 0;
      }
   }

   private static long skipForward(InputStream var0, long var1) throws IOException {
      long var3 = 0L;

      long var5;
      for(var5 = 0L; var5 != var1; var5 += var3) {
         var3 = var0.skip(var1 - var5);
         if (var3 <= 0L) {
            if (var0.read() == -1) {
               return var5;
            }

            ++var5;
         }
      }

      return var5;
   }
}
