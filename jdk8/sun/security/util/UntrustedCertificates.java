package sun.security.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import sun.security.x509.X509CertImpl;

public final class UntrustedCertificates {
   private static final Debug debug = Debug.getInstance("certpath");
   private static final String ALGORITHM_KEY = "Algorithm";
   private static final Properties props = new Properties();
   private static final String algorithm;

   private static String stripColons(Object var0) {
      String var1 = (String)var0;
      char[] var2 = var1.toCharArray();
      int var3 = 0;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         if (var2[var4] != ':') {
            if (var4 != var3) {
               var2[var3] = var2[var4];
            }

            ++var3;
         }
      }

      if (var3 == var2.length) {
         return var1;
      } else {
         return new String(var2, 0, var3);
      }
   }

   public static boolean isUntrusted(X509Certificate var0) {
      if (algorithm == null) {
         return false;
      } else {
         String var1;
         if (var0 instanceof X509CertImpl) {
            var1 = ((X509CertImpl)var0).getFingerprint(algorithm);
         } else {
            try {
               var1 = (new X509CertImpl(var0.getEncoded())).getFingerprint(algorithm);
            } catch (CertificateException var3) {
               return false;
            }
         }

         return props.containsKey(var1);
      }
   }

   private UntrustedCertificates() {
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            File var1 = new File(System.getProperty("java.home"), "lib/security/blacklisted.certs");

            try {
               FileInputStream var2 = new FileInputStream(var1);
               Throwable var3 = null;

               try {
                  UntrustedCertificates.props.load((InputStream)var2);
                  Iterator var4 = UntrustedCertificates.props.entrySet().iterator();

                  while(var4.hasNext()) {
                     Map.Entry var5 = (Map.Entry)var4.next();
                     var5.setValue(UntrustedCertificates.stripColons(var5.getValue()));
                  }
               } catch (Throwable var14) {
                  var3 = var14;
                  throw var14;
               } finally {
                  if (var2 != null) {
                     if (var3 != null) {
                        try {
                           var2.close();
                        } catch (Throwable var13) {
                           var3.addSuppressed(var13);
                        }
                     } else {
                        var2.close();
                     }
                  }

               }
            } catch (IOException var16) {
               if (UntrustedCertificates.debug != null) {
                  UntrustedCertificates.debug.println("Error parsing blacklisted.certs");
               }
            }

            return null;
         }
      });
      algorithm = props.getProperty("Algorithm");
   }
}
