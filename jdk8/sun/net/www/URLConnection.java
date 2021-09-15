package sun.net.www;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class URLConnection extends java.net.URLConnection {
   private String contentType;
   private int contentLength = -1;
   protected MessageHeader properties = new MessageHeader();
   private static HashMap<String, Void> proxiedHosts = new HashMap();

   public URLConnection(URL var1) {
      super(var1);
   }

   public MessageHeader getProperties() {
      return this.properties;
   }

   public void setProperties(MessageHeader var1) {
      this.properties = var1;
   }

   public void setRequestProperty(String var1, String var2) {
      if (this.connected) {
         throw new IllegalAccessError("Already connected");
      } else if (var1 == null) {
         throw new NullPointerException("key cannot be null");
      } else {
         this.properties.set(var1, var2);
      }
   }

   public void addRequestProperty(String var1, String var2) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else if (var1 == null) {
         throw new NullPointerException("key is null");
      }
   }

   public String getRequestProperty(String var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         return null;
      }
   }

   public Map<String, List<String>> getRequestProperties() {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else {
         return Collections.emptyMap();
      }
   }

   public String getHeaderField(String var1) {
      try {
         this.getInputStream();
      } catch (Exception var3) {
         return null;
      }

      return this.properties == null ? null : this.properties.findValue(var1);
   }

   public String getHeaderFieldKey(int var1) {
      try {
         this.getInputStream();
      } catch (Exception var3) {
         return null;
      }

      MessageHeader var2 = this.properties;
      return var2 == null ? null : var2.getKey(var1);
   }

   public String getHeaderField(int var1) {
      try {
         this.getInputStream();
      } catch (Exception var3) {
         return null;
      }

      MessageHeader var2 = this.properties;
      return var2 == null ? null : var2.getValue(var1);
   }

   public String getContentType() {
      if (this.contentType == null) {
         this.contentType = this.getHeaderField("content-type");
      }

      if (this.contentType == null) {
         String var1 = null;

         try {
            var1 = guessContentTypeFromStream(this.getInputStream());
         } catch (IOException var3) {
         }

         String var2 = this.properties.findValue("content-encoding");
         if (var1 == null) {
            var1 = this.properties.findValue("content-type");
            if (var1 == null) {
               if (this.url.getFile().endsWith("/")) {
                  var1 = "text/html";
               } else {
                  var1 = guessContentTypeFromName(this.url.getFile());
               }
            }
         }

         if (var1 == null || var2 != null && !var2.equalsIgnoreCase("7bit") && !var2.equalsIgnoreCase("8bit") && !var2.equalsIgnoreCase("binary")) {
            var1 = "content/unknown";
         }

         this.setContentType(var1);
      }

      return this.contentType;
   }

   public void setContentType(String var1) {
      this.contentType = var1;
      this.properties.set("content-type", var1);
   }

   public int getContentLength() {
      try {
         this.getInputStream();
      } catch (Exception var4) {
         return -1;
      }

      int var1 = this.contentLength;
      if (var1 < 0) {
         try {
            var1 = Integer.parseInt(this.properties.findValue("content-length"));
            this.setContentLength(var1);
         } catch (Exception var3) {
         }
      }

      return var1;
   }

   protected void setContentLength(int var1) {
      this.contentLength = var1;
      this.properties.set("content-length", String.valueOf(var1));
   }

   public boolean canCache() {
      return this.url.getFile().indexOf(63) < 0;
   }

   public void close() {
      this.url = null;
   }

   public static synchronized void setProxiedHost(String var0) {
      proxiedHosts.put(var0.toLowerCase(), (Object)null);
   }

   public static synchronized boolean isProxiedHost(String var0) {
      return proxiedHosts.containsKey(var0.toLowerCase());
   }
}
