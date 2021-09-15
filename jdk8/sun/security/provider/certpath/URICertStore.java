package sun.security.provider.certpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import sun.security.action.GetIntegerAction;
import sun.security.util.Cache;
import sun.security.util.Debug;
import sun.security.x509.AccessDescription;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.URIName;

class URICertStore extends CertStoreSpi {
   private static final Debug debug = Debug.getInstance("certpath");
   private static final int CHECK_INTERVAL = 30000;
   private static final int CACHE_SIZE = 185;
   private final CertificateFactory factory;
   private Collection<X509Certificate> certs = Collections.emptySet();
   private X509CRL crl;
   private long lastChecked;
   private long lastModified;
   private URI uri;
   private boolean ldap = false;
   private CertStoreHelper ldapHelper;
   private CertStore ldapCertStore;
   private String ldapPath;
   private static final int DEFAULT_CRL_CONNECT_TIMEOUT = 15000;
   private static final int CRL_CONNECT_TIMEOUT = initializeTimeout();
   private static final Cache<URICertStore.URICertStoreParameters, CertStore> certStoreCache = Cache.newSoftMemoryCache(185);

   private static int initializeTimeout() {
      Integer var0 = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("com.sun.security.crl.timeout")));
      return var0 != null && var0 >= 0 ? var0 * 1000 : 15000;
   }

   URICertStore(CertStoreParameters var1) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
      super(var1);
      if (!(var1 instanceof URICertStore.URICertStoreParameters)) {
         throw new InvalidAlgorithmParameterException("params must be instanceof URICertStoreParameters");
      } else {
         this.uri = ((URICertStore.URICertStoreParameters)var1).uri;
         if (this.uri.getScheme().toLowerCase(Locale.ENGLISH).equals("ldap")) {
            this.ldap = true;
            this.ldapHelper = CertStoreHelper.getInstance("LDAP");
            this.ldapCertStore = this.ldapHelper.getCertStore(this.uri);
            this.ldapPath = this.uri.getPath();
            if (this.ldapPath.charAt(0) == '/') {
               this.ldapPath = this.ldapPath.substring(1);
            }
         }

         try {
            this.factory = CertificateFactory.getInstance("X.509");
         } catch (CertificateException var3) {
            throw new RuntimeException();
         }
      }
   }

   static synchronized CertStore getInstance(URICertStore.URICertStoreParameters var0) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      if (debug != null) {
         debug.println("CertStore URI:" + var0.uri);
      }

      Object var1 = (CertStore)certStoreCache.get(var0);
      if (var1 == null) {
         var1 = new URICertStore.UCS(new URICertStore(var0), (Provider)null, "URI", var0);
         certStoreCache.put(var0, var1);
      } else if (debug != null) {
         debug.println("URICertStore.getInstance: cache hit");
      }

      return (CertStore)var1;
   }

   static CertStore getInstance(AccessDescription var0) {
      if (!var0.getAccessMethod().equals((Object)AccessDescription.Ad_CAISSUERS_Id)) {
         return null;
      } else {
         GeneralNameInterface var1 = var0.getAccessLocation().getName();
         if (!(var1 instanceof URIName)) {
            return null;
         } else {
            URI var2 = ((URIName)var1).getURI();

            try {
               return getInstance(new URICertStore.URICertStoreParameters(var2));
            } catch (Exception var4) {
               if (debug != null) {
                  debug.println("exception creating CertStore: " + var4);
                  var4.printStackTrace();
               }

               return null;
            }
         }
      }
   }

   public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector var1) throws CertStoreException {
      if (this.ldap) {
         X509CertSelector var27 = (X509CertSelector)var1;

         try {
            var27 = this.ldapHelper.wrap(var27, var27.getSubject(), this.ldapPath);
         } catch (IOException var23) {
            throw new CertStoreException(var23);
         }

         return this.ldapCertStore.getCertificates(var27);
      } else {
         long var2 = System.currentTimeMillis();
         if (var2 - this.lastChecked < 30000L) {
            if (debug != null) {
               debug.println("Returning certificates from cache");
            }

            return getMatchingCerts(this.certs, var1);
         } else {
            this.lastChecked = var2;

            try {
               URLConnection var4 = this.uri.toURL().openConnection();
               if (this.lastModified != 0L) {
                  var4.setIfModifiedSince(this.lastModified);
               }

               long var5 = this.lastModified;
               InputStream var7 = var4.getInputStream();
               Throwable var8 = null;

               try {
                  this.lastModified = var4.getLastModified();
                  if (var5 != 0L) {
                     if (var5 == this.lastModified) {
                        if (debug != null) {
                           debug.println("Not modified, using cached copy");
                        }

                        Collection var28 = getMatchingCerts(this.certs, var1);
                        return var28;
                     }

                     if (var4 instanceof HttpURLConnection) {
                        HttpURLConnection var9 = (HttpURLConnection)var4;
                        if (var9.getResponseCode() == 304) {
                           if (debug != null) {
                              debug.println("Not modified, using cached copy");
                           }

                           Collection var10 = getMatchingCerts(this.certs, var1);
                           return var10;
                        }
                     }
                  }

                  if (debug != null) {
                     debug.println("Downloading new certificates...");
                  }

                  this.certs = this.factory.generateCertificates(var7);
               } catch (Throwable var24) {
                  var8 = var24;
                  throw var24;
               } finally {
                  if (var7 != null) {
                     if (var8 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var22) {
                           var8.addSuppressed(var22);
                        }
                     } else {
                        var7.close();
                     }
                  }

               }

               return getMatchingCerts(this.certs, var1);
            } catch (CertificateException | IOException var26) {
               if (debug != null) {
                  debug.println("Exception fetching certificates:");
                  var26.printStackTrace();
               }

               this.lastModified = 0L;
               this.certs = Collections.emptySet();
               return this.certs;
            }
         }
      }
   }

   private static Collection<X509Certificate> getMatchingCerts(Collection<X509Certificate> var0, CertSelector var1) {
      if (var1 == null) {
         return var0;
      } else {
         ArrayList var2 = new ArrayList(var0.size());
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            X509Certificate var4 = (X509Certificate)var3.next();
            if (var1.match(var4)) {
               var2.add(var4);
            }
         }

         return var2;
      }
   }

   public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector var1) throws CertStoreException {
      if (this.ldap) {
         X509CRLSelector var29 = (X509CRLSelector)var1;

         try {
            var29 = this.ldapHelper.wrap((X509CRLSelector)var29, (Collection)null, this.ldapPath);
         } catch (IOException var25) {
            throw new CertStoreException(var25);
         }

         try {
            return this.ldapCertStore.getCRLs(var29);
         } catch (CertStoreException var24) {
            throw new PKIX.CertStoreTypeException("LDAP", var24);
         }
      } else {
         long var2 = System.currentTimeMillis();
         if (var2 - this.lastChecked < 30000L) {
            if (debug != null) {
               debug.println("Returning CRL from cache");
            }

            return getMatchingCRLs(this.crl, var1);
         } else {
            this.lastChecked = var2;

            try {
               URLConnection var4 = this.uri.toURL().openConnection();
               if (this.lastModified != 0L) {
                  var4.setIfModifiedSince(this.lastModified);
               }

               long var5 = this.lastModified;
               var4.setConnectTimeout(CRL_CONNECT_TIMEOUT);
               InputStream var7 = var4.getInputStream();
               Throwable var8 = null;

               try {
                  this.lastModified = var4.getLastModified();
                  if (var5 != 0L) {
                     if (var5 == this.lastModified) {
                        if (debug != null) {
                           debug.println("Not modified, using cached copy");
                        }

                        Collection var30 = getMatchingCRLs(this.crl, var1);
                        return var30;
                     }

                     if (var4 instanceof HttpURLConnection) {
                        HttpURLConnection var9 = (HttpURLConnection)var4;
                        if (var9.getResponseCode() == 304) {
                           if (debug != null) {
                              debug.println("Not modified, using cached copy");
                           }

                           Collection var10 = getMatchingCRLs(this.crl, var1);
                           return var10;
                        }
                     }
                  }

                  if (debug != null) {
                     debug.println("Downloading new CRL...");
                  }

                  this.crl = (X509CRL)this.factory.generateCRL(var7);
                  return getMatchingCRLs(this.crl, var1);
               } catch (Throwable var26) {
                  var8 = var26;
                  throw var26;
               } finally {
                  if (var7 != null) {
                     if (var8 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var23) {
                           var8.addSuppressed(var23);
                        }
                     } else {
                        var7.close();
                     }
                  }

               }
            } catch (CRLException | IOException var28) {
               if (debug != null) {
                  debug.println("Exception fetching CRL:");
                  var28.printStackTrace();
               }

               this.lastModified = 0L;
               this.crl = null;
               throw new PKIX.CertStoreTypeException("URI", new CertStoreException(var28));
            }
         }
      }
   }

   private static Collection<X509CRL> getMatchingCRLs(X509CRL var0, CRLSelector var1) {
      return var1 != null && (var0 == null || !var1.match(var0)) ? Collections.emptyList() : Collections.singletonList(var0);
   }

   private static class UCS extends CertStore {
      protected UCS(CertStoreSpi var1, Provider var2, String var3, CertStoreParameters var4) {
         super(var1, var2, var3, var4);
      }
   }

   static class URICertStoreParameters implements CertStoreParameters {
      private final URI uri;
      private volatile int hashCode = 0;

      URICertStoreParameters(URI var1) {
         this.uri = var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof URICertStore.URICertStoreParameters)) {
            return false;
         } else {
            URICertStore.URICertStoreParameters var2 = (URICertStore.URICertStoreParameters)var1;
            return this.uri.equals(var2.uri);
         }
      }

      public int hashCode() {
         if (this.hashCode == 0) {
            byte var1 = 17;
            int var2 = 37 * var1 + this.uri.hashCode();
            this.hashCode = var2;
         }

         return this.hashCode;
      }

      public Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new InternalError(var2.toString(), var2);
         }
      }
   }
}
