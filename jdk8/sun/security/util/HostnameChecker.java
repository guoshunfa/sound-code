package sun.security.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.net.ssl.SNIHostName;
import javax.security.auth.x500.X500Principal;
import sun.net.util.IPAddressUtil;
import sun.security.ssl.Krb5Helper;
import sun.security.x509.X500Name;

public class HostnameChecker {
   public static final byte TYPE_TLS = 1;
   private static final HostnameChecker INSTANCE_TLS = new HostnameChecker((byte)1);
   public static final byte TYPE_LDAP = 2;
   private static final HostnameChecker INSTANCE_LDAP = new HostnameChecker((byte)2);
   private static final int ALTNAME_DNS = 2;
   private static final int ALTNAME_IP = 7;
   private final byte checkType;

   private HostnameChecker(byte var1) {
      this.checkType = var1;
   }

   public static HostnameChecker getInstance(byte var0) {
      if (var0 == 1) {
         return INSTANCE_TLS;
      } else if (var0 == 2) {
         return INSTANCE_LDAP;
      } else {
         throw new IllegalArgumentException("Unknown check type: " + var0);
      }
   }

   public void match(String var1, X509Certificate var2) throws CertificateException {
      if (isIpAddress(var1)) {
         matchIP(var1, var2);
      } else {
         this.matchDNS(var1, var2);
      }

   }

   public static boolean match(String var0, Principal var1) {
      String var2 = getServerName(var1);
      return var0.equalsIgnoreCase(var2);
   }

   public static String getServerName(Principal var0) {
      return Krb5Helper.getPrincipalHostName(var0);
   }

   private static boolean isIpAddress(String var0) {
      return IPAddressUtil.isIPv4LiteralAddress(var0) || IPAddressUtil.isIPv6LiteralAddress(var0);
   }

   private static void matchIP(String var0, X509Certificate var1) throws CertificateException {
      Collection var2 = var1.getSubjectAlternativeNames();
      if (var2 == null) {
         throw new CertificateException("No subject alternative names present");
      } else {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            List var4 = (List)var3.next();
            if ((Integer)var4.get(0) == 7) {
               String var5 = (String)var4.get(1);
               if (var0.equalsIgnoreCase(var5)) {
                  return;
               }

               try {
                  if (InetAddress.getByName(var0).equals(InetAddress.getByName(var5))) {
                     return;
                  }
               } catch (UnknownHostException var7) {
               } catch (SecurityException var8) {
               }
            }
         }

         throw new CertificateException("No subject alternative names matching IP address " + var0 + " found");
      }
   }

   private void matchDNS(String var1, X509Certificate var2) throws CertificateException {
      try {
         new SNIHostName(var1);
      } catch (IllegalArgumentException var9) {
         throw new CertificateException("Illegal given domain name: " + var1, var9);
      }

      Collection var3 = var2.getSubjectAlternativeNames();
      if (var3 != null) {
         boolean var4 = false;
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            List var6 = (List)var5.next();
            if ((Integer)var6.get(0) == 2) {
               var4 = true;
               String var7 = (String)var6.get(1);
               if (this.isMatched(var1, var7)) {
                  return;
               }
            }
         }

         if (var4) {
            throw new CertificateException("No subject alternative DNS name matching " + var1 + " found.");
         }
      }

      X500Name var10 = getSubjectX500Name(var2);
      DerValue var11 = var10.findMostSpecificAttribute(X500Name.commonName_oid);
      if (var11 != null) {
         try {
            if (this.isMatched(var1, var11.getAsString())) {
               return;
            }
         } catch (IOException var8) {
         }
      }

      String var12 = "No name matching " + var1 + " found";
      throw new CertificateException(var12);
   }

   public static X500Name getSubjectX500Name(X509Certificate var0) throws CertificateParsingException {
      try {
         Principal var1 = var0.getSubjectDN();
         if (var1 instanceof X500Name) {
            return (X500Name)var1;
         } else {
            X500Principal var2 = var0.getSubjectX500Principal();
            return new X500Name(var2.getEncoded());
         }
      } catch (IOException var3) {
         throw (CertificateParsingException)(new CertificateParsingException()).initCause(var3);
      }
   }

   private boolean isMatched(String var1, String var2) {
      try {
         new SNIHostName(var2.replace('*', 'x'));
      } catch (IllegalArgumentException var4) {
         return false;
      }

      if (this.checkType == 1) {
         return matchAllWildcards(var1, var2);
      } else {
         return this.checkType == 2 ? matchLeftmostWildcard(var1, var2) : false;
      }
   }

   private static boolean matchAllWildcards(String var0, String var1) {
      var0 = var0.toLowerCase(Locale.ENGLISH);
      var1 = var1.toLowerCase(Locale.ENGLISH);
      StringTokenizer var2 = new StringTokenizer(var0, ".");
      StringTokenizer var3 = new StringTokenizer(var1, ".");
      if (var2.countTokens() != var3.countTokens()) {
         return false;
      } else {
         do {
            if (!var2.hasMoreTokens()) {
               return true;
            }
         } while(matchWildCards(var2.nextToken(), var3.nextToken()));

         return false;
      }
   }

   private static boolean matchLeftmostWildcard(String var0, String var1) {
      var0 = var0.toLowerCase(Locale.ENGLISH);
      var1 = var1.toLowerCase(Locale.ENGLISH);
      int var2 = var1.indexOf(".");
      int var3 = var0.indexOf(".");
      if (var2 == -1) {
         var2 = var1.length();
      }

      if (var3 == -1) {
         var3 = var0.length();
      }

      return matchWildCards(var0.substring(0, var3), var1.substring(0, var2)) ? var1.substring(var2).equals(var0.substring(var3)) : false;
   }

   private static boolean matchWildCards(String var0, String var1) {
      int var2 = var1.indexOf("*");
      if (var2 == -1) {
         return var0.equals(var1);
      } else {
         boolean var3 = true;
         String var4 = "";

         String var5;
         for(var5 = var1; var2 != -1; var2 = var5.indexOf("*")) {
            var4 = var5.substring(0, var2);
            var5 = var5.substring(var2 + 1);
            int var6 = var0.indexOf(var4);
            if (var6 == -1 || var3 && var6 != 0) {
               return false;
            }

            var3 = false;
            var0 = var0.substring(var6 + var4.length());
         }

         return var0.endsWith(var5);
      }
   }
}
