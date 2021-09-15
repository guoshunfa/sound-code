package java.security.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.x509.CRLNumberExtension;
import sun.security.x509.X500Name;

public class X509CRLSelector implements CRLSelector {
   private static final Debug debug;
   private HashSet<Object> issuerNames;
   private HashSet<X500Principal> issuerX500Principals;
   private BigInteger minCRL;
   private BigInteger maxCRL;
   private Date dateAndTime;
   private X509Certificate certChecking;
   private long skew = 0L;

   public void setIssuers(Collection<X500Principal> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.issuerX500Principals = new HashSet(var1);
         this.issuerNames = new HashSet();
         Iterator var2 = this.issuerX500Principals.iterator();

         while(var2.hasNext()) {
            X500Principal var3 = (X500Principal)var2.next();
            this.issuerNames.add(var3.getEncoded());
         }
      } else {
         this.issuerNames = null;
         this.issuerX500Principals = null;
      }

   }

   public void setIssuerNames(Collection<?> var1) throws IOException {
      if (var1 != null && var1.size() != 0) {
         HashSet var2 = cloneAndCheckIssuerNames(var1);
         this.issuerX500Principals = parseIssuerNames(var2);
         this.issuerNames = var2;
      } else {
         this.issuerNames = null;
         this.issuerX500Principals = null;
      }

   }

   public void addIssuer(X500Principal var1) {
      this.addIssuerNameInternal(var1.getEncoded(), var1);
   }

   public void addIssuerName(String var1) throws IOException {
      this.addIssuerNameInternal(var1, (new X500Name(var1)).asX500Principal());
   }

   public void addIssuerName(byte[] var1) throws IOException {
      this.addIssuerNameInternal(var1.clone(), (new X500Name(var1)).asX500Principal());
   }

   private void addIssuerNameInternal(Object var1, X500Principal var2) {
      if (this.issuerNames == null) {
         this.issuerNames = new HashSet();
      }

      if (this.issuerX500Principals == null) {
         this.issuerX500Principals = new HashSet();
      }

      this.issuerNames.add(var1);
      this.issuerX500Principals.add(var2);
   }

   private static HashSet<Object> cloneAndCheckIssuerNames(Collection<?> var0) throws IOException {
      HashSet var1 = new HashSet();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         if (!(var3 instanceof byte[]) && !(var3 instanceof String)) {
            throw new IOException("name not byte array or String");
         }

         if (var3 instanceof byte[]) {
            var1.add(((byte[])((byte[])var3)).clone());
         } else {
            var1.add(var3);
         }
      }

      return var1;
   }

   private static HashSet<Object> cloneIssuerNames(Collection<Object> var0) {
      try {
         return cloneAndCheckIssuerNames(var0);
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   private static HashSet<X500Principal> parseIssuerNames(Collection<Object> var0) throws IOException {
      HashSet var1 = new HashSet();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         if (var3 instanceof String) {
            var1.add((new X500Name((String)var3)).asX500Principal());
         } else {
            try {
               var1.add(new X500Principal((byte[])((byte[])var3)));
            } catch (IllegalArgumentException var5) {
               throw (IOException)(new IOException("Invalid name")).initCause(var5);
            }
         }
      }

      return var1;
   }

   public void setMinCRLNumber(BigInteger var1) {
      this.minCRL = var1;
   }

   public void setMaxCRLNumber(BigInteger var1) {
      this.maxCRL = var1;
   }

   public void setDateAndTime(Date var1) {
      if (var1 == null) {
         this.dateAndTime = null;
      } else {
         this.dateAndTime = new Date(var1.getTime());
      }

      this.skew = 0L;
   }

   void setDateAndTime(Date var1, long var2) {
      this.dateAndTime = var1 == null ? null : new Date(var1.getTime());
      this.skew = var2;
   }

   public void setCertificateChecking(X509Certificate var1) {
      this.certChecking = var1;
   }

   public Collection<X500Principal> getIssuers() {
      return this.issuerX500Principals == null ? null : Collections.unmodifiableCollection(this.issuerX500Principals);
   }

   public Collection<Object> getIssuerNames() {
      return this.issuerNames == null ? null : cloneIssuerNames(this.issuerNames);
   }

   public BigInteger getMinCRL() {
      return this.minCRL;
   }

   public BigInteger getMaxCRL() {
      return this.maxCRL;
   }

   public Date getDateAndTime() {
      return this.dateAndTime == null ? null : (Date)this.dateAndTime.clone();
   }

   public X509Certificate getCertificateChecking() {
      return this.certChecking;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("X509CRLSelector: [\n");
      if (this.issuerNames != null) {
         var1.append("  IssuerNames:\n");
         Iterator var2 = this.issuerNames.iterator();

         while(var2.hasNext()) {
            var1.append("    " + var2.next() + "\n");
         }
      }

      if (this.minCRL != null) {
         var1.append("  minCRLNumber: " + this.minCRL + "\n");
      }

      if (this.maxCRL != null) {
         var1.append("  maxCRLNumber: " + this.maxCRL + "\n");
      }

      if (this.dateAndTime != null) {
         var1.append("  dateAndTime: " + this.dateAndTime + "\n");
      }

      if (this.certChecking != null) {
         var1.append("  Certificate being checked: " + this.certChecking + "\n");
      }

      var1.append("]");
      return var1.toString();
   }

   public boolean match(CRL var1) {
      if (!(var1 instanceof X509CRL)) {
         return false;
      } else {
         X509CRL var2 = (X509CRL)var1;
         if (this.issuerNames != null) {
            X500Principal var3 = var2.getIssuerX500Principal();
            Iterator var4 = this.issuerX500Principals.iterator();
            boolean var5 = false;

            while(!var5 && var4.hasNext()) {
               if (((X500Principal)var4.next()).equals(var3)) {
                  var5 = true;
               }
            }

            if (!var5) {
               if (debug != null) {
                  debug.println("X509CRLSelector.match: issuer DNs don't match");
               }

               return false;
            }
         }

         if (this.minCRL != null || this.maxCRL != null) {
            byte[] var9 = var2.getExtensionValue("2.5.29.20");
            if (var9 == null && debug != null) {
               debug.println("X509CRLSelector.match: no CRLNumber");
            }

            BigInteger var11;
            try {
               DerInputStream var13 = new DerInputStream(var9);
               byte[] var6 = var13.getOctetString();
               CRLNumberExtension var7 = new CRLNumberExtension(Boolean.FALSE, var6);
               var11 = var7.get("value");
            } catch (IOException var8) {
               if (debug != null) {
                  debug.println("X509CRLSelector.match: exception in decoding CRL number");
               }

               return false;
            }

            if (this.minCRL != null && var11.compareTo(this.minCRL) < 0) {
               if (debug != null) {
                  debug.println("X509CRLSelector.match: CRLNumber too small");
               }

               return false;
            }

            if (this.maxCRL != null && var11.compareTo(this.maxCRL) > 0) {
               if (debug != null) {
                  debug.println("X509CRLSelector.match: CRLNumber too large");
               }

               return false;
            }
         }

         if (this.dateAndTime != null) {
            Date var10 = var2.getThisUpdate();
            Date var12 = var2.getNextUpdate();
            if (var12 == null) {
               if (debug != null) {
                  debug.println("X509CRLSelector.match: nextUpdate null");
               }

               return false;
            }

            Date var14 = this.dateAndTime;
            Date var15 = this.dateAndTime;
            if (this.skew > 0L) {
               var14 = new Date(this.dateAndTime.getTime() + this.skew);
               var15 = new Date(this.dateAndTime.getTime() - this.skew);
            }

            if (var15.after(var12) || var14.before(var10)) {
               if (debug != null) {
                  debug.println("X509CRLSelector.match: update out-of-range");
               }

               return false;
            }
         }

         return true;
      }
   }

   public Object clone() {
      try {
         X509CRLSelector var1 = (X509CRLSelector)super.clone();
         if (this.issuerNames != null) {
            var1.issuerNames = new HashSet(this.issuerNames);
            var1.issuerX500Principals = new HashSet(this.issuerX500Principals);
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }

   static {
      CertPathHelperImpl.initialize();
      debug = Debug.getInstance("certpath");
   }
}
