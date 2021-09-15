package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class MLetParser {
   private int c;
   private static String tag = "mlet";

   public MLetParser() {
   }

   public void skipSpace(Reader var1) throws IOException {
      while(this.c >= 0 && (this.c == 32 || this.c == 9 || this.c == 10 || this.c == 13)) {
         this.c = var1.read();
      }

   }

   public String scanIdentifier(Reader var1) throws IOException {
      StringBuilder var2;
      for(var2 = new StringBuilder(); this.c >= 97 && this.c <= 122 || this.c >= 65 && this.c <= 90 || this.c >= 48 && this.c <= 57 || this.c == 95; this.c = var1.read()) {
         var2.append((char)this.c);
      }

      return var2.toString();
   }

   public Map<String, String> scanTag(Reader var1) throws IOException {
      HashMap var2 = new HashMap();
      this.skipSpace(var1);

      while(this.c >= 0 && this.c != 62) {
         if (this.c == 60) {
            throw new IOException("Missing '>' in tag");
         }

         String var3 = this.scanIdentifier(var1);
         String var4 = "";
         this.skipSpace(var1);
         if (this.c == 61) {
            int var5 = -1;
            this.c = var1.read();
            this.skipSpace(var1);
            if (this.c == 39 || this.c == 34) {
               var5 = this.c;
               this.c = var1.read();
            }

            StringBuilder var6;
            for(var6 = new StringBuilder(); this.c > 0 && (var5 < 0 && this.c != 32 && this.c != 9 && this.c != 10 && this.c != 13 && this.c != 62 || var5 >= 0 && this.c != var5); this.c = var1.read()) {
               var6.append((char)this.c);
            }

            if (this.c == var5) {
               this.c = var1.read();
            }

            this.skipSpace(var1);
            var4 = var6.toString();
         }

         var2.put(var3.toLowerCase(), var4);
         this.skipSpace(var1);
      }

      return var2;
   }

   public List<MLetContent> parse(URL var1) throws IOException {
      String var2 = "parse";
      String var3 = "<arg type=... value=...> tag requires type parameter.";
      String var4 = "<arg type=... value=...> tag requires value parameter.";
      String var5 = "<arg> tag outside <mlet> ... </mlet>.";
      String var6 = "<mlet> tag requires either code or object parameter.";
      String var7 = "<mlet> tag requires archive parameter.";
      URLConnection var8 = var1.openConnection();
      BufferedReader var9 = new BufferedReader(new InputStreamReader(var8.getInputStream(), "UTF-8"));
      var1 = var8.getURL();
      ArrayList var10 = new ArrayList();
      Map var11 = null;
      ArrayList var12 = new ArrayList();
      ArrayList var13 = new ArrayList();

      while(true) {
         this.c = var9.read();
         if (this.c == -1) {
            var9.close();
            return var10;
         }

         if (this.c == 60) {
            this.c = var9.read();
            String var14;
            if (this.c == 47) {
               this.c = var9.read();
               var14 = this.scanIdentifier(var9);
               if (this.c != 62) {
                  throw new IOException("Missing '>' in tag");
               }

               if (var14.equalsIgnoreCase(tag)) {
                  if (var11 != null) {
                     var10.add(new MLetContent(var1, var11, var12, var13));
                  }

                  var11 = null;
                  var12 = new ArrayList();
                  var13 = new ArrayList();
               }
            } else {
               var14 = this.scanIdentifier(var9);
               if (var14.equalsIgnoreCase("arg")) {
                  Map var15 = this.scanTag(var9);
                  String var16 = (String)var15.get("type");
                  if (var16 == null) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), var2, var3);
                     throw new IOException(var3);
                  }

                  if (var11 == null) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), var2, var5);
                     throw new IOException(var5);
                  }

                  var12.add(var16);
                  String var17 = (String)var15.get("value");
                  if (var17 == null) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), var2, var4);
                     throw new IOException(var4);
                  }

                  if (var11 == null) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), var2, var5);
                     throw new IOException(var5);
                  }

                  var13.add(var17);
               } else if (var14.equalsIgnoreCase(tag)) {
                  var11 = this.scanTag(var9);
                  if (var11.get("code") == null && var11.get("object") == null) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), var2, var6);
                     throw new IOException(var6);
                  }

                  if (var11.get("archive") == null) {
                     JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), var2, var7);
                     throw new IOException(var7);
                  }
               }
            }
         }
      }
   }

   public List<MLetContent> parseURL(String var1) throws IOException {
      URL var2;
      if (var1.indexOf(58) <= 1) {
         String var3 = System.getProperty("user.dir");
         String var4;
         if (var3.charAt(0) != '/' && var3.charAt(0) != File.separatorChar) {
            var4 = "file:/";
         } else {
            var4 = "file:";
         }

         var2 = new URL(var4 + var3.replace(File.separatorChar, '/') + "/");
         var2 = new URL(var2, var1);
      } else {
         var2 = new URL(var1);
      }

      return this.parse(var2);
   }
}
