package sun.security.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import sun.security.x509.X509CertImpl;

public class AnchorCertificates {
   private static final Debug debug = Debug.getInstance("certpath");
   private static final String HASH = "SHA-256";
   private static Set<String> certs = Collections.emptySet();

   public static boolean contains(X509Certificate var0) {
      String var1 = X509CertImpl.getFingerprint("SHA-256", var0);
      boolean var2 = certs.contains(var1);
      if (var2 && debug != null) {
         debug.println("AnchorCertificate.contains: matched " + var0.getSubjectDN());
      }

      return var2;
   }

   private AnchorCertificates() {
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            File var1 = new File(System.getProperty("java.home"), "lib/security/cacerts");

            try {
               KeyStore var2 = KeyStore.getInstance("JKS");
               FileInputStream var3 = new FileInputStream(var1);
               Throwable var4 = null;

               try {
                  var2.load(var3, (char[])null);
                  AnchorCertificates.certs = new HashSet();
                  Enumeration var5 = var2.aliases();

                  while(var5.hasMoreElements()) {
                     String var6 = (String)var5.nextElement();
                     if (var6.contains(" [jdk")) {
                        X509Certificate var7 = (X509Certificate)var2.getCertificate(var6);
                        AnchorCertificates.certs.add(X509CertImpl.getFingerprint("SHA-256", var7));
                     }
                  }
               } catch (Throwable var16) {
                  var4 = var16;
                  throw var16;
               } finally {
                  if (var3 != null) {
                     if (var4 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var15) {
                           var4.addSuppressed(var15);
                        }
                     } else {
                        var3.close();
                     }
                  }

               }
            } catch (Exception var18) {
               if (AnchorCertificates.debug != null) {
                  AnchorCertificates.debug.println("Error parsing cacerts");
               }

               var18.printStackTrace();
            }

            return null;
         }
      });
   }
}
