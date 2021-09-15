package sun.security.provider.certpath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.cert.CRLReason;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.Extension;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.security.action.GetIntegerAction;
import sun.security.util.Debug;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.GeneralName;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.URIName;
import sun.security.x509.X509CertImpl;

public final class OCSP {
   private static final Debug debug = Debug.getInstance("certpath");
   private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
   private static final int CONNECT_TIMEOUT = initializeTimeout();

   private static int initializeTimeout() {
      Integer var0 = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("com.sun.security.ocsp.timeout")));
      return var0 != null && var0 >= 0 ? var0 * 1000 : 15000;
   }

   private OCSP() {
   }

   public static OCSP.RevocationStatus check(X509Certificate var0, X509Certificate var1, URI var2, X509Certificate var3, Date var4) throws IOException, CertPathValidatorException {
      return check(var0, var1, var2, var3, var4, Collections.emptyList(), "generic");
   }

   public static OCSP.RevocationStatus check(X509Certificate var0, X509Certificate var1, URI var2, X509Certificate var3, Date var4, List<Extension> var5, String var6) throws IOException, CertPathValidatorException {
      return check(var0, var2, (TrustAnchor)null, var1, var3, var4, var5, var6);
   }

   public static OCSP.RevocationStatus check(X509Certificate var0, URI var1, TrustAnchor var2, X509Certificate var3, X509Certificate var4, Date var5, List<Extension> var6, String var7) throws IOException, CertPathValidatorException {
      CertId var8;
      try {
         X509CertImpl var9 = X509CertImpl.toImpl(var0);
         var8 = new CertId(var3, var9.getSerialNumberObject());
      } catch (IOException | CertificateException var10) {
         throw new CertPathValidatorException("Exception while encoding OCSPRequest", var10);
      }

      OCSPResponse var11 = check(Collections.singletonList(var8), var1, new OCSPResponse.IssuerInfo(var2, var3), var4, var5, var6, var7);
      return var11.getSingleResponse(var8);
   }

   static OCSPResponse check(List<CertId> var0, URI var1, OCSPResponse.IssuerInfo var2, X509Certificate var3, Date var4, List<Extension> var5, String var6) throws IOException, CertPathValidatorException {
      byte[] var7 = null;
      Iterator var8 = var5.iterator();

      while(var8.hasNext()) {
         Extension var9 = (Extension)var8.next();
         if (var9.getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
            var7 = var9.getValue();
         }
      }

      var8 = null;

      try {
         byte[] var12 = getOCSPBytes(var0, var1, var5);
         OCSPResponse var11 = new OCSPResponse(var12);
         var11.verify(var0, var2, var3, var4, var7, var6);
         return var11;
      } catch (IOException var10) {
         throw new CertPathValidatorException("Unable to determine revocation status due to network error", var10, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      }
   }

   public static byte[] getOCSPBytes(List<CertId> var0, URI var1, List<Extension> var2) throws IOException {
      OCSPRequest var3 = new OCSPRequest(var0, var2);
      byte[] var4 = var3.encodeBytes();
      InputStream var5 = null;
      OutputStream var6 = null;
      Object var7 = null;

      byte[] var23;
      try {
         URL var8 = var1.toURL();
         if (debug != null) {
            debug.println("connecting to OCSP service at: " + var8);
         }

         HttpURLConnection var9 = (HttpURLConnection)var8.openConnection();
         var9.setConnectTimeout(CONNECT_TIMEOUT);
         var9.setReadTimeout(CONNECT_TIMEOUT);
         var9.setDoOutput(true);
         var9.setDoInput(true);
         var9.setRequestMethod("POST");
         var9.setRequestProperty("Content-type", "application/ocsp-request");
         var9.setRequestProperty("Content-length", String.valueOf(var4.length));
         var6 = var9.getOutputStream();
         var6.write(var4);
         var6.flush();
         if (debug != null && var9.getResponseCode() != 200) {
            debug.println("Received HTTP error: " + var9.getResponseCode() + " - " + var9.getResponseMessage());
         }

         var5 = var9.getInputStream();
         int var10 = var9.getContentLength();
         if (var10 == -1) {
            var10 = Integer.MAX_VALUE;
         }

         var23 = new byte[var10 > 2048 ? 2048 : var10];
         int var11 = 0;

         while(var11 < var10) {
            int var12 = var5.read(var23, var11, var23.length - var11);
            if (var12 < 0) {
               break;
            }

            var11 += var12;
            if (var11 >= var23.length && var11 < var10) {
               var23 = Arrays.copyOf(var23, var11 * 2);
            }
         }

         var23 = Arrays.copyOf(var23, var11);
      } finally {
         if (var5 != null) {
            try {
               var5.close();
            } catch (IOException var21) {
               throw var21;
            }
         }

         if (var6 != null) {
            try {
               var6.close();
            } catch (IOException var20) {
               throw var20;
            }
         }

      }

      return var23;
   }

   public static URI getResponderURI(X509Certificate var0) {
      try {
         return getResponderURI(X509CertImpl.toImpl(var0));
      } catch (CertificateException var2) {
         return null;
      }
   }

   static URI getResponderURI(X509CertImpl var0) {
      AuthorityInfoAccessExtension var1 = var0.getAuthorityInfoAccessExtension();
      if (var1 == null) {
         return null;
      } else {
         List var2 = var1.getAccessDescriptions();
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            AccessDescription var4 = (AccessDescription)var3.next();
            if (var4.getAccessMethod().equals(AccessDescription.Ad_OCSP_Id)) {
               GeneralName var5 = var4.getAccessLocation();
               if (var5.getType() == 6) {
                  URIName var6 = (URIName)var5.getName();
                  return var6.getURI();
               }
            }
         }

         return null;
      }
   }

   public interface RevocationStatus {
      OCSP.RevocationStatus.CertStatus getCertStatus();

      Date getRevocationTime();

      CRLReason getRevocationReason();

      Map<String, Extension> getSingleExtensions();

      public static enum CertStatus {
         GOOD,
         REVOKED,
         UNKNOWN;
      }
   }
}
