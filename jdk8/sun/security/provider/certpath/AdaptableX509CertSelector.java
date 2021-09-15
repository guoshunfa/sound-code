package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.SerialNumber;

class AdaptableX509CertSelector extends X509CertSelector {
   private static final Debug debug = Debug.getInstance("certpath");
   private Date startDate;
   private Date endDate;
   private byte[] ski;
   private BigInteger serial;

   void setValidityPeriod(Date var1, Date var2) {
      this.startDate = var1;
      this.endDate = var2;
   }

   public void setSubjectKeyIdentifier(byte[] var1) {
      throw new IllegalArgumentException();
   }

   public void setSerialNumber(BigInteger var1) {
      throw new IllegalArgumentException();
   }

   void setSkiAndSerialNumber(AuthorityKeyIdentifierExtension var1) throws IOException {
      this.ski = null;
      this.serial = null;
      if (var1 != null) {
         this.ski = var1.getEncodedKeyIdentifier();
         SerialNumber var2 = (SerialNumber)var1.get("serial_number");
         if (var2 != null) {
            this.serial = var2.getNumber();
         }
      }

   }

   public boolean match(Certificate var1) {
      X509Certificate var2 = (X509Certificate)var1;
      if (!this.matchSubjectKeyID(var2)) {
         return false;
      } else {
         int var3 = var2.getVersion();
         if (this.serial != null && var3 > 2 && !this.serial.equals(var2.getSerialNumber())) {
            return false;
         } else {
            if (var3 < 3) {
               if (this.startDate != null) {
                  try {
                     var2.checkValidity(this.startDate);
                  } catch (CertificateException var6) {
                     return false;
                  }
               }

               if (this.endDate != null) {
                  try {
                     var2.checkValidity(this.endDate);
                  } catch (CertificateException var5) {
                     return false;
                  }
               }
            }

            return super.match(var1);
         }
      }
   }

   private boolean matchSubjectKeyID(X509Certificate var1) {
      if (this.ski == null) {
         return true;
      } else {
         try {
            byte[] var2 = var1.getExtensionValue("2.5.29.14");
            if (var2 == null) {
               if (debug != null) {
                  debug.println("AdaptableX509CertSelector.match: no subject key ID extension. Subject: " + var1.getSubjectX500Principal());
               }

               return true;
            } else {
               DerInputStream var3 = new DerInputStream(var2);
               byte[] var4 = var3.getOctetString();
               if (var4 != null && Arrays.equals(this.ski, var4)) {
                  return true;
               } else {
                  if (debug != null) {
                     debug.println("AdaptableX509CertSelector.match: subject key IDs don't match. Expected: " + Arrays.toString(this.ski) + " Cert's: " + Arrays.toString(var4));
                  }

                  return false;
               }
            }
         } catch (IOException var5) {
            if (debug != null) {
               debug.println("AdaptableX509CertSelector.match: exception in subject key ID check");
            }

            return false;
         }
      }
   }

   public Object clone() {
      AdaptableX509CertSelector var1 = (AdaptableX509CertSelector)super.clone();
      if (this.startDate != null) {
         var1.startDate = (Date)this.startDate.clone();
      }

      if (this.endDate != null) {
         var1.endDate = (Date)this.endDate.clone();
      }

      if (this.ski != null) {
         var1.ski = (byte[])this.ski.clone();
      }

      return var1;
   }
}
