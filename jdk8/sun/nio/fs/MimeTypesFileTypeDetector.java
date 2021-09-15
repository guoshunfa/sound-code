package sun.nio.fs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MimeTypesFileTypeDetector extends AbstractFileTypeDetector {
   private final Path mimeTypesFile;
   private Map<String, String> mimeTypeMap;
   private volatile boolean loaded = false;

   public MimeTypesFileTypeDetector(Path var1) {
      this.mimeTypesFile = var1;
   }

   protected String implProbeContentType(Path var1) {
      Path var2 = var1.getFileName();
      if (var2 == null) {
         return null;
      } else {
         String var3 = getExtension(var2.toString());
         if (var3.isEmpty()) {
            return null;
         } else {
            this.loadMimeTypes();
            if (this.mimeTypeMap != null && !this.mimeTypeMap.isEmpty()) {
               String var4;
               do {
                  var4 = (String)this.mimeTypeMap.get(var3);
                  if (var4 == null) {
                     var3 = getExtension(var3);
                  }
               } while(var4 == null && !var3.isEmpty());

               return var4;
            } else {
               return null;
            }
         }
      }
   }

   private static String getExtension(String var0) {
      String var1 = "";
      if (var0 != null && !var0.isEmpty()) {
         int var2 = var0.indexOf(46);
         if (var2 >= 0 && var2 < var0.length() - 1) {
            var1 = var0.substring(var2 + 1);
         }
      }

      return var1;
   }

   private void loadMimeTypes() {
      if (!this.loaded) {
         synchronized(this) {
            if (!this.loaded) {
               List var2 = (List)AccessController.doPrivileged(new PrivilegedAction<List<String>>() {
                  public List<String> run() {
                     try {
                        return Files.readAllLines(MimeTypesFileTypeDetector.this.mimeTypesFile, Charset.defaultCharset());
                     } catch (IOException var2) {
                        return Collections.emptyList();
                     }
                  }
               });
               this.mimeTypeMap = new HashMap(var2.size());
               String var3 = "";
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  String var5 = (String)var4.next();
                  var3 = var3 + var5;
                  if (var3.endsWith("\\")) {
                     var3 = var3.substring(0, var3.length() - 1);
                  } else {
                     this.parseMimeEntry(var3);
                     var3 = "";
                  }
               }

               if (!var3.isEmpty()) {
                  this.parseMimeEntry(var3);
               }

               this.loaded = true;
            }
         }
      }

   }

   private void parseMimeEntry(String var1) {
      var1 = var1.trim();
      if (!var1.isEmpty() && var1.charAt(0) != '#') {
         var1 = var1.replaceAll("\\s*#.*", "");
         int var2 = var1.indexOf(61);
         if (var2 > 0) {
            String var4 = "\\btype=(\"\\p{Graph}+?/\\p{Graph}+?\"|\\p{Graph}+/\\p{Graph}+\\b)";
            Pattern var5 = Pattern.compile(var4);
            Matcher var6 = var5.matcher(var1);
            if (var6.find()) {
               String var7 = var6.group().substring("type=".length());
               if (var7.charAt(0) == '"') {
                  var7 = var7.substring(1, var7.length() - 1);
               }

               String var9 = "\\bexts=(\"[\\p{Graph}\\p{Blank}]+?\"|\\p{Graph}+\\b)";
               Pattern var10 = Pattern.compile(var9);
               Matcher var11 = var10.matcher(var1);
               if (var11.find()) {
                  String var12 = var11.group().substring("exts=".length());
                  if (var12.charAt(0) == '"') {
                     var12 = var12.substring(1, var12.length() - 1);
                  }

                  String[] var13 = var12.split("[\\p{Blank}\\p{Punct}]+");
                  String[] var14 = var13;
                  int var15 = var13.length;

                  for(int var16 = 0; var16 < var15; ++var16) {
                     String var17 = var14[var16];
                     this.putIfAbsent(var17, var7);
                  }
               }
            }
         } else {
            String[] var3 = var1.split("\\s+");
            int var18 = 1;

            while(var18 < var3.length) {
               this.putIfAbsent(var3[var18++], var3[0]);
            }
         }

      }
   }

   private void putIfAbsent(String var1, String var2) {
      if (var1 != null && !var1.isEmpty() && var2 != null && !var2.isEmpty() && !this.mimeTypeMap.containsKey(var1)) {
         this.mimeTypeMap.put(var1, var2);
      }

   }
}
