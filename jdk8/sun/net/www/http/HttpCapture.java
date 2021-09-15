package sun.net.www.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import sun.net.NetProperties;
import sun.util.logging.PlatformLogger;

public class HttpCapture {
   private File file = null;
   private boolean incoming = true;
   private BufferedWriter out = null;
   private static boolean initialized = false;
   private static volatile ArrayList<Pattern> patterns = null;
   private static volatile ArrayList<String> capFiles = null;

   private static synchronized void init() {
      initialized = true;
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return NetProperties.get("sun.net.http.captureRules");
         }
      });
      if (var0 != null && !var0.isEmpty()) {
         BufferedReader var1;
         try {
            var1 = new BufferedReader(new FileReader(var0));
         } catch (FileNotFoundException var13) {
            return;
         }

         try {
            for(String var2 = var1.readLine(); var2 != null; var2 = var1.readLine()) {
               var2 = var2.trim();
               if (!var2.startsWith("#")) {
                  String[] var3 = var2.split(",");
                  if (var3.length == 2) {
                     if (patterns == null) {
                        patterns = new ArrayList();
                        capFiles = new ArrayList();
                     }

                     patterns.add(Pattern.compile(var3[0].trim()));
                     capFiles.add(var3[1].trim());
                  }
               }
            }
         } catch (IOException var14) {
         } finally {
            try {
               var1.close();
            } catch (IOException var12) {
            }

         }
      }

   }

   private static synchronized boolean isInitialized() {
      return initialized;
   }

   private HttpCapture(File var1, URL var2) {
      this.file = var1;

      try {
         this.out = new BufferedWriter(new FileWriter(this.file, true));
         this.out.write("URL: " + var2 + "\n");
      } catch (IOException var4) {
         PlatformLogger.getLogger(HttpCapture.class.getName()).severe((String)null, (Throwable)var4);
      }

   }

   public synchronized void sent(int var1) throws IOException {
      if (this.incoming) {
         this.out.write("\n------>\n");
         this.incoming = false;
         this.out.flush();
      }

      this.out.write(var1);
   }

   public synchronized void received(int var1) throws IOException {
      if (!this.incoming) {
         this.out.write("\n<------\n");
         this.incoming = true;
         this.out.flush();
      }

      this.out.write(var1);
   }

   public synchronized void flush() throws IOException {
      this.out.flush();
   }

   public static HttpCapture getCapture(URL var0) {
      if (!isInitialized()) {
         init();
      }

      if (patterns != null && !patterns.isEmpty()) {
         String var1 = var0.toString();

         for(int var2 = 0; var2 < patterns.size(); ++var2) {
            Pattern var3 = (Pattern)patterns.get(var2);
            if (var3.matcher(var1).find()) {
               String var4 = (String)capFiles.get(var2);
               File var5;
               if (var4.indexOf("%d") >= 0) {
                  Random var6 = new Random();

                  do {
                     String var7 = var4.replace("%d", Integer.toString(var6.nextInt()));
                     var5 = new File(var7);
                  } while(var5.exists());
               } else {
                  var5 = new File(var4);
               }

               return new HttpCapture(var5, var0);
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
