package sun.security.provider.certpath.ldap;

import com.sun.jndi.ldap.LdapReferralException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.LDAPCertStoreParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
import sun.security.provider.certpath.X509CertificatePair;
import sun.security.util.Cache;
import sun.security.util.Debug;
import sun.security.x509.X500Name;

public final class LDAPCertStore extends CertStoreSpi {
   private static final Debug debug = Debug.getInstance("certpath");
   private static final boolean DEBUG = false;
   private static final String USER_CERT = "userCertificate;binary";
   private static final String CA_CERT = "cACertificate;binary";
   private static final String CROSS_CERT = "crossCertificatePair;binary";
   private static final String CRL = "certificateRevocationList;binary";
   private static final String ARL = "authorityRevocationList;binary";
   private static final String DELTA_CRL = "deltaRevocationList;binary";
   private static final String[] STRING0 = new String[0];
   private static final byte[][] BB0 = new byte[0][];
   private static final Attributes EMPTY_ATTRIBUTES = new BasicAttributes();
   private static final int DEFAULT_CACHE_SIZE = 750;
   private static final int DEFAULT_CACHE_LIFETIME = 30;
   private static final int LIFETIME;
   private static final String PROP_LIFETIME = "sun.security.certpath.ldap.cache.lifetime";
   private static final String PROP_DISABLE_APP_RESOURCE_FILES = "sun.security.certpath.ldap.disable.app.resource.files";
   private CertificateFactory cf;
   private DirContext ctx;
   private boolean prefetchCRLs = false;
   private final Cache<String, byte[][]> valueCache;
   private int cacheHits = 0;
   private int cacheMisses = 0;
   private int requests = 0;
   private static final Cache<LDAPCertStoreParameters, CertStore> certStoreCache;

   public LDAPCertStore(CertStoreParameters var1) throws InvalidAlgorithmParameterException {
      super(var1);
      if (!(var1 instanceof LDAPCertStoreParameters)) {
         throw new InvalidAlgorithmParameterException("parameters must be LDAPCertStoreParameters");
      } else {
         LDAPCertStoreParameters var2 = (LDAPCertStoreParameters)var1;
         this.createInitialDirContext(var2.getServerName(), var2.getPort());

         try {
            this.cf = CertificateFactory.getInstance("X.509");
         } catch (CertificateException var4) {
            throw new InvalidAlgorithmParameterException("unable to create CertificateFactory for X.509");
         }

         if (LIFETIME == 0) {
            this.valueCache = Cache.newNullCache();
         } else if (LIFETIME < 0) {
            this.valueCache = Cache.newSoftMemoryCache(750);
         } else {
            this.valueCache = Cache.newSoftMemoryCache(750, LIFETIME);
         }

      }
   }

   static synchronized CertStore getInstance(LDAPCertStoreParameters var0) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkConnect(var0.getServerName(), var0.getPort());
      }

      CertStore var2 = (CertStore)certStoreCache.get(var0);
      if (var2 == null) {
         var2 = CertStore.getInstance("LDAP", var0);
         certStoreCache.put(var0, var2);
      } else if (debug != null) {
         debug.println("LDAPCertStore.getInstance: cache hit");
      }

      return var2;
   }

   private void createInitialDirContext(String var1, int var2) throws InvalidAlgorithmParameterException {
      String var3 = "ldap://" + var1 + ":" + var2;
      Hashtable var4 = new Hashtable();
      var4.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
      var4.put("java.naming.provider.url", var3);
      boolean var5 = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.security.certpath.ldap.disable.app.resource.files")));
      if (var5) {
         if (debug != null) {
            debug.println("LDAPCertStore disabling app resource files");
         }

         var4.put("com.sun.naming.disable.app.resource.files", "true");
      }

      try {
         this.ctx = new InitialDirContext(var4);
         Hashtable var6 = this.ctx.getEnvironment();
         if (var6.get("java.naming.referral") == null) {
            this.ctx.addToEnvironment("java.naming.referral", "throw");
         }

      } catch (NamingException var8) {
         if (debug != null) {
            debug.println("LDAPCertStore.engineInit about to throw InvalidAlgorithmParameterException");
            var8.printStackTrace();
         }

         InvalidAlgorithmParameterException var7 = new InvalidAlgorithmParameterException("unable to create InitialDirContext using supplied parameters");
         var7.initCause(var8);
         throw (InvalidAlgorithmParameterException)var7;
      }
   }

   private Collection<X509Certificate> getCertificates(LDAPCertStore.LDAPRequest var1, String var2, X509CertSelector var3) throws CertStoreException {
      byte[][] var4;
      try {
         var4 = var1.getValues(var2);
      } catch (NamingException var11) {
         throw new CertStoreException(var11);
      }

      int var5 = var4.length;
      if (var5 == 0) {
         return Collections.emptySet();
      } else {
         ArrayList var6 = new ArrayList(var5);

         for(int var7 = 0; var7 < var5; ++var7) {
            ByteArrayInputStream var8 = new ByteArrayInputStream(var4[var7]);

            try {
               Certificate var9 = this.cf.generateCertificate(var8);
               if (var3.match(var9)) {
                  var6.add((X509Certificate)var9);
               }
            } catch (CertificateException var12) {
               if (debug != null) {
                  debug.println("LDAPCertStore.getCertificates() encountered exception while parsing cert, skipping the bad data: ");
                  HexDumpEncoder var10 = new HexDumpEncoder();
                  debug.println("[ " + var10.encodeBuffer(var4[var7]) + " ]");
               }
            }
         }

         return var6;
      }
   }

   private Collection<X509CertificatePair> getCertPairs(LDAPCertStore.LDAPRequest var1, String var2) throws CertStoreException {
      byte[][] var3;
      try {
         var3 = var1.getValues(var2);
      } catch (NamingException var9) {
         throw new CertStoreException(var9);
      }

      int var4 = var3.length;
      if (var4 == 0) {
         return Collections.emptySet();
      } else {
         ArrayList var5 = new ArrayList(var4);

         for(int var6 = 0; var6 < var4; ++var6) {
            try {
               X509CertificatePair var7 = X509CertificatePair.generateCertificatePair(var3[var6]);
               var5.add(var7);
            } catch (CertificateException var10) {
               if (debug != null) {
                  debug.println("LDAPCertStore.getCertPairs() encountered exception while parsing cert, skipping the bad data: ");
                  HexDumpEncoder var8 = new HexDumpEncoder();
                  debug.println("[ " + var8.encodeBuffer(var3[var6]) + " ]");
               }
            }
         }

         return var5;
      }
   }

   private Collection<X509Certificate> getMatchingCrossCerts(LDAPCertStore.LDAPRequest var1, X509CertSelector var2, X509CertSelector var3) throws CertStoreException {
      Collection var4 = this.getCertPairs(var1, "crossCertificatePair;binary");
      ArrayList var5 = new ArrayList();
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         X509CertificatePair var7 = (X509CertificatePair)var6.next();
         X509Certificate var8;
         if (var2 != null) {
            var8 = var7.getForward();
            if (var8 != null && var2.match(var8)) {
               var5.add(var8);
            }
         }

         if (var3 != null) {
            var8 = var7.getReverse();
            if (var8 != null && var3.match(var8)) {
               var5.add(var8);
            }
         }
      }

      return var5;
   }

   public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector var1) throws CertStoreException {
      if (debug != null) {
         debug.println("LDAPCertStore.engineGetCertificates() selector: " + String.valueOf(var1));
      }

      if (var1 == null) {
         var1 = new X509CertSelector();
      }

      if (!(var1 instanceof X509CertSelector)) {
         throw new CertStoreException("LDAPCertStore needs an X509CertSelector to find certs");
      } else {
         X509CertSelector var2 = (X509CertSelector)var1;
         int var3 = var2.getBasicConstraints();
         String var4 = var2.getSubjectAsString();
         String var5 = var2.getIssuerAsString();
         HashSet var6 = new HashSet();
         if (debug != null) {
            debug.println("LDAPCertStore.engineGetCertificates() basicConstraints: " + var3);
         }

         LDAPCertStore.LDAPRequest var7;
         if (var4 != null) {
            if (debug != null) {
               debug.println("LDAPCertStore.engineGetCertificates() subject is not null");
            }

            var7 = new LDAPCertStore.LDAPRequest(var4);
            if (var3 > -2) {
               var7.addRequestedAttribute("crossCertificatePair;binary");
               var7.addRequestedAttribute("cACertificate;binary");
               var7.addRequestedAttribute("authorityRevocationList;binary");
               if (this.prefetchCRLs) {
                  var7.addRequestedAttribute("certificateRevocationList;binary");
               }
            }

            if (var3 < 0) {
               var7.addRequestedAttribute("userCertificate;binary");
            }

            if (var3 > -2) {
               var6.addAll(this.getMatchingCrossCerts(var7, var2, (X509CertSelector)null));
               if (debug != null) {
                  debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(subject,xsel,null),certs.size(): " + var6.size());
               }

               var6.addAll(this.getCertificates(var7, "cACertificate;binary", var2));
               if (debug != null) {
                  debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,CA_CERT,xsel),certs.size(): " + var6.size());
               }
            }

            if (var3 < 0) {
               var6.addAll(this.getCertificates(var7, "userCertificate;binary", var2));
               if (debug != null) {
                  debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,USER_CERT, xsel),certs.size(): " + var6.size());
               }
            }
         } else {
            if (debug != null) {
               debug.println("LDAPCertStore.engineGetCertificates() subject is null");
            }

            if (var3 == -2) {
               throw new CertStoreException("need subject to find EE certs");
            }

            if (var5 == null) {
               throw new CertStoreException("need subject or issuer to find certs");
            }
         }

         if (debug != null) {
            debug.println("LDAPCertStore.engineGetCertificates() about to getMatchingCrossCerts...");
         }

         if (var5 != null && var3 > -2) {
            var7 = new LDAPCertStore.LDAPRequest(var5);
            var7.addRequestedAttribute("crossCertificatePair;binary");
            var7.addRequestedAttribute("cACertificate;binary");
            var7.addRequestedAttribute("authorityRevocationList;binary");
            if (this.prefetchCRLs) {
               var7.addRequestedAttribute("certificateRevocationList;binary");
            }

            var6.addAll(this.getMatchingCrossCerts(var7, (X509CertSelector)null, var2));
            if (debug != null) {
               debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(issuer,null,xsel),certs.size(): " + var6.size());
            }

            var6.addAll(this.getCertificates(var7, "cACertificate;binary", var2));
            if (debug != null) {
               debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(issuer,CA_CERT,xsel),certs.size(): " + var6.size());
            }
         }

         if (debug != null) {
            debug.println("LDAPCertStore.engineGetCertificates() returning certs");
         }

         return var6;
      }
   }

   private Collection<X509CRL> getCRLs(LDAPCertStore.LDAPRequest var1, String var2, X509CRLSelector var3) throws CertStoreException {
      byte[][] var4;
      try {
         var4 = var1.getValues(var2);
      } catch (NamingException var10) {
         throw new CertStoreException(var10);
      }

      int var5 = var4.length;
      if (var5 == 0) {
         return Collections.emptySet();
      } else {
         ArrayList var6 = new ArrayList(var5);

         for(int var7 = 0; var7 < var5; ++var7) {
            try {
               CRL var8 = this.cf.generateCRL(new ByteArrayInputStream(var4[var7]));
               if (var3.match(var8)) {
                  var6.add((X509CRL)var8);
               }
            } catch (CRLException var11) {
               if (debug != null) {
                  debug.println("LDAPCertStore.getCRLs() encountered exception while parsing CRL, skipping the bad data: ");
                  HexDumpEncoder var9 = new HexDumpEncoder();
                  debug.println("[ " + var9.encodeBuffer(var4[var7]) + " ]");
               }
            }
         }

         return var6;
      }
   }

   public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector var1) throws CertStoreException {
      if (debug != null) {
         debug.println("LDAPCertStore.engineGetCRLs() selector: " + var1);
      }

      if (var1 == null) {
         var1 = new X509CRLSelector();
      }

      if (!(var1 instanceof X509CRLSelector)) {
         throw new CertStoreException("need X509CRLSelector to find CRLs");
      } else {
         X509CRLSelector var2 = (X509CRLSelector)var1;
         HashSet var3 = new HashSet();
         X509Certificate var5 = var2.getCertificateChecking();
         Object var4;
         if (var5 != null) {
            var4 = new HashSet();
            X500Principal var6 = var5.getIssuerX500Principal();
            ((Collection)var4).add(var6.getName("RFC2253"));
         } else {
            var4 = var2.getIssuerNames();
            if (var4 == null) {
               throw new CertStoreException("need issuerNames or certChecking to find CRLs");
            }
         }

         Iterator var14 = ((Collection)var4).iterator();

         while(true) {
            String var8;
            LDAPCertStore.LDAPRequest var10;
            Object var15;
            do {
               while(true) {
                  if (!var14.hasNext()) {
                     return var3;
                  }

                  Object var7 = var14.next();
                  if (!(var7 instanceof byte[])) {
                     var8 = (String)var7;
                     break;
                  }

                  try {
                     X500Principal var9 = new X500Principal((byte[])((byte[])var7));
                     var8 = var9.getName("RFC2253");
                     break;
                  } catch (IllegalArgumentException var12) {
                  }
               }

               var15 = Collections.emptySet();
               if (var5 == null || var5.getBasicConstraints() != -1) {
                  var10 = new LDAPCertStore.LDAPRequest(var8);
                  var10.addRequestedAttribute("crossCertificatePair;binary");
                  var10.addRequestedAttribute("cACertificate;binary");
                  var10.addRequestedAttribute("authorityRevocationList;binary");
                  if (this.prefetchCRLs) {
                     var10.addRequestedAttribute("certificateRevocationList;binary");
                  }

                  try {
                     var15 = this.getCRLs(var10, "authorityRevocationList;binary", var2);
                     if (((Collection)var15).isEmpty()) {
                        this.prefetchCRLs = true;
                     } else {
                        var3.addAll((Collection)var15);
                     }
                  } catch (CertStoreException var13) {
                     if (debug != null) {
                        debug.println("LDAPCertStore.engineGetCRLs non-fatal error retrieving ARLs:" + var13);
                        var13.printStackTrace();
                     }
                  }
               }
            } while(!((Collection)var15).isEmpty() && var5 != null);

            var10 = new LDAPCertStore.LDAPRequest(var8);
            var10.addRequestedAttribute("certificateRevocationList;binary");
            Collection var16 = this.getCRLs(var10, "certificateRevocationList;binary", var2);
            var3.addAll(var16);
         }
      }
   }

   static LDAPCertStoreParameters getParameters(URI var0) {
      String var1 = var0.getHost();
      if (var1 == null) {
         return new LDAPCertStore.SunLDAPCertStoreParameters();
      } else {
         int var2 = var0.getPort();
         return var2 == -1 ? new LDAPCertStore.SunLDAPCertStoreParameters(var1) : new LDAPCertStore.SunLDAPCertStoreParameters(var1, var2);
      }
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.security.certpath.ldap.cache.lifetime")));
      if (var0 != null) {
         LIFETIME = Integer.parseInt(var0);
      } else {
         LIFETIME = 30;
      }

      certStoreCache = Cache.newSoftMemoryCache(185);
   }

   static class LDAPCRLSelector extends X509CRLSelector {
      private X509CRLSelector selector;
      private Collection<X500Principal> certIssuers;
      private Collection<X500Principal> issuers;
      private HashSet<Object> issuerNames;

      LDAPCRLSelector(X509CRLSelector var1, Collection<X500Principal> var2, String var3) throws IOException {
         this.selector = var1 == null ? new X509CRLSelector() : var1;
         this.certIssuers = var2;
         this.issuerNames = new HashSet();
         this.issuerNames.add(var3);
         this.issuers = new HashSet();
         this.issuers.add((new X500Name(var3)).asX500Principal());
      }

      public Collection<X500Principal> getIssuers() {
         return Collections.unmodifiableCollection(this.issuers);
      }

      public Collection<Object> getIssuerNames() {
         return Collections.unmodifiableCollection(this.issuerNames);
      }

      public BigInteger getMinCRL() {
         return this.selector.getMinCRL();
      }

      public BigInteger getMaxCRL() {
         return this.selector.getMaxCRL();
      }

      public Date getDateAndTime() {
         return this.selector.getDateAndTime();
      }

      public X509Certificate getCertificateChecking() {
         return this.selector.getCertificateChecking();
      }

      public boolean match(CRL var1) {
         this.selector.setIssuers(this.certIssuers);
         boolean var2 = this.selector.match(var1);
         this.selector.setIssuers(this.issuers);
         return var2;
      }
   }

   static class LDAPCertSelector extends X509CertSelector {
      private X500Principal certSubject;
      private X509CertSelector selector;
      private X500Principal subject;

      LDAPCertSelector(X509CertSelector var1, X500Principal var2, String var3) throws IOException {
         this.selector = var1 == null ? new X509CertSelector() : var1;
         this.certSubject = var2;
         this.subject = (new X500Name(var3)).asX500Principal();
      }

      public X509Certificate getCertificate() {
         return this.selector.getCertificate();
      }

      public BigInteger getSerialNumber() {
         return this.selector.getSerialNumber();
      }

      public X500Principal getIssuer() {
         return this.selector.getIssuer();
      }

      public String getIssuerAsString() {
         return this.selector.getIssuerAsString();
      }

      public byte[] getIssuerAsBytes() throws IOException {
         return this.selector.getIssuerAsBytes();
      }

      public X500Principal getSubject() {
         return this.subject;
      }

      public String getSubjectAsString() {
         return this.subject.getName();
      }

      public byte[] getSubjectAsBytes() throws IOException {
         return this.subject.getEncoded();
      }

      public byte[] getSubjectKeyIdentifier() {
         return this.selector.getSubjectKeyIdentifier();
      }

      public byte[] getAuthorityKeyIdentifier() {
         return this.selector.getAuthorityKeyIdentifier();
      }

      public Date getCertificateValid() {
         return this.selector.getCertificateValid();
      }

      public Date getPrivateKeyValid() {
         return this.selector.getPrivateKeyValid();
      }

      public String getSubjectPublicKeyAlgID() {
         return this.selector.getSubjectPublicKeyAlgID();
      }

      public PublicKey getSubjectPublicKey() {
         return this.selector.getSubjectPublicKey();
      }

      public boolean[] getKeyUsage() {
         return this.selector.getKeyUsage();
      }

      public Set<String> getExtendedKeyUsage() {
         return this.selector.getExtendedKeyUsage();
      }

      public boolean getMatchAllSubjectAltNames() {
         return this.selector.getMatchAllSubjectAltNames();
      }

      public Collection<List<?>> getSubjectAlternativeNames() {
         return this.selector.getSubjectAlternativeNames();
      }

      public byte[] getNameConstraints() {
         return this.selector.getNameConstraints();
      }

      public int getBasicConstraints() {
         return this.selector.getBasicConstraints();
      }

      public Set<String> getPolicy() {
         return this.selector.getPolicy();
      }

      public Collection<List<?>> getPathToNames() {
         return this.selector.getPathToNames();
      }

      public boolean match(Certificate var1) {
         this.selector.setSubject(this.certSubject);
         boolean var2 = this.selector.match(var1);
         this.selector.setSubject(this.subject);
         return var2;
      }
   }

   private static class SunLDAPCertStoreParameters extends LDAPCertStoreParameters {
      private volatile int hashCode = 0;

      SunLDAPCertStoreParameters(String var1, int var2) {
         super(var1, var2);
      }

      SunLDAPCertStoreParameters(String var1) {
         super(var1);
      }

      SunLDAPCertStoreParameters() {
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof LDAPCertStoreParameters)) {
            return false;
         } else {
            LDAPCertStoreParameters var2 = (LDAPCertStoreParameters)var1;
            return this.getPort() == var2.getPort() && this.getServerName().equalsIgnoreCase(var2.getServerName());
         }
      }

      public int hashCode() {
         if (this.hashCode == 0) {
            byte var1 = 17;
            int var2 = 37 * var1 + this.getPort();
            var2 = 37 * var2 + this.getServerName().toLowerCase(Locale.ENGLISH).hashCode();
            this.hashCode = var2;
         }

         return this.hashCode;
      }
   }

   private class LDAPRequest {
      private final String name;
      private Map<String, byte[][]> valueMap;
      private final List<String> requestedAttributes;

      LDAPRequest(String var2) throws CertStoreException {
         this.name = this.checkName(var2);
         this.requestedAttributes = new ArrayList(5);
      }

      private String checkName(String var1) throws CertStoreException {
         if (var1 == null) {
            throw new CertStoreException("Name absent");
         } else {
            try {
               if ((new CompositeName(var1)).size() > 1) {
                  throw new CertStoreException("Invalid name: " + var1);
               } else {
                  return var1;
               }
            } catch (InvalidNameException var3) {
               throw new CertStoreException("Invalid name: " + var1, var3);
            }
         }
      }

      String getName() {
         return this.name;
      }

      void addRequestedAttribute(String var1) {
         if (this.valueMap != null) {
            throw new IllegalStateException("Request already sent");
         } else {
            this.requestedAttributes.add(var1);
         }
      }

      byte[][] getValues(String var1) throws NamingException {
         String var2 = this.name + "|" + var1;
         byte[][] var3 = (byte[][])LDAPCertStore.this.valueCache.get(var2);
         if (var3 != null) {
            LDAPCertStore.this.cacheHits++;
            return var3;
         } else {
            LDAPCertStore.this.cacheMisses++;
            Map var4 = this.getValueMap();
            var3 = (byte[][])var4.get(var1);
            return var3;
         }
      }

      private Map<String, byte[][]> getValueMap() throws NamingException {
         if (this.valueMap != null) {
            return this.valueMap;
         } else {
            this.valueMap = new HashMap(8);
            String[] var1 = (String[])this.requestedAttributes.toArray(LDAPCertStore.STRING0);

            Attributes var2;
            String var4;
            try {
               var2 = LDAPCertStore.this.ctx.getAttributes(this.name, var1);
            } catch (LdapReferralException var16) {
               LdapReferralException var3 = var16;

               while(true) {
                  try {
                     var4 = (String)var3.getReferralInfo();
                     URI var5 = new URI(var4);
                     if (!var5.getScheme().equalsIgnoreCase("ldap")) {
                        throw new IllegalArgumentException("Not LDAP");
                     }

                     String var6 = var5.getPath();
                     if (var6 != null && var6.charAt(0) == '/') {
                        var6 = var6.substring(1);
                     }

                     this.checkName(var6);
                  } catch (Exception var13) {
                     throw new NamingException("Cannot follow referral to " + var3.getReferralInfo());
                  }

                  LdapContext var19 = (LdapContext)var3.getReferralContext();

                  try {
                     var2 = var19.getAttributes(this.name, var1);
                     break;
                  } catch (LdapReferralException var14) {
                     var3 = var14;
                  } finally {
                     var19.close();
                  }
               }
            } catch (NameNotFoundException var17) {
               var2 = LDAPCertStore.EMPTY_ATTRIBUTES;
            }

            Iterator var18 = this.requestedAttributes.iterator();

            while(var18.hasNext()) {
               var4 = (String)var18.next();
               Attribute var20 = var2.get(var4);
               byte[][] var21 = this.getAttributeValues(var20);
               this.cacheAttribute(var4, var21);
               this.valueMap.put(var4, var21);
            }

            return this.valueMap;
         }
      }

      private void cacheAttribute(String var1, byte[][] var2) {
         String var3 = this.name + "|" + var1;
         LDAPCertStore.this.valueCache.put(var3, var2);
      }

      private byte[][] getAttributeValues(Attribute var1) throws NamingException {
         byte[][] var2;
         if (var1 == null) {
            var2 = LDAPCertStore.BB0;
         } else {
            var2 = new byte[var1.size()][];
            int var3 = 0;

            byte[] var6;
            for(NamingEnumeration var4 = var1.getAll(); var4.hasMore(); var2[var3++] = var6) {
               Object var5 = var4.next();
               if (LDAPCertStore.debug != null && var5 instanceof String) {
                  LDAPCertStore.debug.println("LDAPCertStore.getAttrValues() enum.next is a string!: " + var5);
               }

               var6 = (byte[])((byte[])var5);
            }
         }

         return var2;
      }
   }
}
