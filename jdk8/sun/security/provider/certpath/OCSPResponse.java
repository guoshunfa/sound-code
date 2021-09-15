package sun.security.provider.certpath;

import java.io.IOException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLReason;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.Extension;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.action.GetIntegerAction;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X509CertImpl;

public final class OCSPResponse {
   private static final OCSPResponse.ResponseStatus[] rsvalues = OCSPResponse.ResponseStatus.values();
   private static final Debug debug = Debug.getInstance("certpath");
   private static final boolean dump;
   private static final ObjectIdentifier OCSP_BASIC_RESPONSE_OID;
   private static final int CERT_STATUS_GOOD = 0;
   private static final int CERT_STATUS_REVOKED = 1;
   private static final int CERT_STATUS_UNKNOWN = 2;
   private static final int NAME_TAG = 1;
   private static final int KEY_TAG = 2;
   private static final String KP_OCSP_SIGNING_OID = "1.3.6.1.5.5.7.3.9";
   private static final int DEFAULT_MAX_CLOCK_SKEW = 900000;
   private static final int MAX_CLOCK_SKEW;
   private static final CRLReason[] values;
   private final OCSPResponse.ResponseStatus responseStatus;
   private final Map<CertId, OCSPResponse.SingleResponse> singleResponseMap;
   private final AlgorithmId sigAlgId;
   private final byte[] signature;
   private final byte[] tbsResponseData;
   private final byte[] responseNonce;
   private List<X509CertImpl> certs;
   private X509CertImpl signerCert = null;
   private final ResponderId respId;
   private Date producedAtDate = null;
   private final Map<String, Extension> responseExtensions;

   private static int initializeClockSkew() {
      Integer var0 = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("com.sun.security.ocsp.clockSkew")));
      return var0 != null && var0 >= 0 ? var0 * 1000 : 900000;
   }

   public OCSPResponse(byte[] var1) throws IOException {
      if (dump) {
         HexDumpEncoder var2 = new HexDumpEncoder();
         debug.println("OCSPResponse bytes...\n\n" + var2.encode(var1) + "\n");
      }

      DerValue var20 = new DerValue(var1);
      if (var20.tag != 48) {
         throw new IOException("Bad encoding in OCSP response: expected ASN.1 SEQUENCE tag.");
      } else {
         DerInputStream var3 = var20.getData();
         int var4 = var3.getEnumerated();
         if (var4 >= 0 && var4 < rsvalues.length) {
            this.responseStatus = rsvalues[var4];
            if (debug != null) {
               debug.println("OCSP response status: " + this.responseStatus);
            }

            if (this.responseStatus != OCSPResponse.ResponseStatus.SUCCESSFUL) {
               this.singleResponseMap = Collections.emptyMap();
               this.certs = new ArrayList();
               this.sigAlgId = null;
               this.signature = null;
               this.tbsResponseData = null;
               this.responseNonce = null;
               this.responseExtensions = Collections.emptyMap();
               this.respId = null;
            } else {
               var20 = var3.getDerValue();
               if (!var20.isContextSpecific((byte)0)) {
                  throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 context specific tag 0.");
               } else {
                  DerValue var5 = var20.data.getDerValue();
                  if (var5.tag != 48) {
                     throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 SEQUENCE tag.");
                  } else {
                     var3 = var5.data;
                     ObjectIdentifier var6 = var3.getOID();
                     if (!var6.equals((Object)OCSP_BASIC_RESPONSE_OID)) {
                        if (debug != null) {
                           debug.println("OCSP response type: " + var6);
                        }

                        throw new IOException("Unsupported OCSP response type: " + var6);
                     } else {
                        if (debug != null) {
                           debug.println("OCSP response type: basic");
                        }

                        DerInputStream var7 = new DerInputStream(var3.getOctetString());
                        DerValue[] var8 = var7.getSequence(2);
                        if (var8.length < 3) {
                           throw new IOException("Unexpected BasicOCSPResponse value");
                        } else {
                           DerValue var9 = var8[0];
                           this.tbsResponseData = var8[0].toByteArray();
                           if (var9.tag != 48) {
                              throw new IOException("Bad encoding in tbsResponseData element of OCSP response: expected ASN.1 SEQUENCE tag.");
                           } else {
                              DerInputStream var10 = var9.data;
                              DerValue var11 = var10.getDerValue();
                              if (var11.isContextSpecific((byte)0) && var11.isConstructed() && var11.isContextSpecific()) {
                                 var11 = var11.data.getDerValue();
                                 int var12 = var11.getInteger();
                                 if (var11.data.available() != 0) {
                                    throw new IOException("Bad encoding in version  element of OCSP response: bad format");
                                 }

                                 var11 = var10.getDerValue();
                              }

                              this.respId = new ResponderId(var11.toByteArray());
                              if (debug != null) {
                                 debug.println("Responder ID: " + this.respId);
                              }

                              var11 = var10.getDerValue();
                              this.producedAtDate = var11.getGeneralizedTime();
                              if (debug != null) {
                                 debug.println("OCSP response produced at: " + this.producedAtDate);
                              }

                              DerValue[] var21 = var10.getSequence(1);
                              this.singleResponseMap = new HashMap(var21.length);
                              if (debug != null) {
                                 debug.println("OCSP number of SingleResponses: " + var21.length);
                              }

                              DerValue[] var13 = var21;
                              int var14 = var21.length;

                              for(int var15 = 0; var15 < var14; ++var15) {
                                 DerValue var16 = var13[var15];
                                 OCSPResponse.SingleResponse var17 = new OCSPResponse.SingleResponse(var16);
                                 this.singleResponseMap.put(var17.getCertId(), var17);
                              }

                              Object var22 = new HashMap();
                              if (var10.available() > 0) {
                                 var11 = var10.getDerValue();
                                 if (var11.isContextSpecific((byte)1)) {
                                    var22 = parseExtensions(var11);
                                 }
                              }

                              this.responseExtensions = (Map)var22;
                              sun.security.x509.Extension var23 = (sun.security.x509.Extension)((Map)var22).get(PKIXExtensions.OCSPNonce_Id.toString());
                              this.responseNonce = var23 != null ? var23.getExtensionValue() : null;
                              if (debug != null && this.responseNonce != null) {
                                 debug.println("Response nonce: " + Arrays.toString(this.responseNonce));
                              }

                              this.sigAlgId = AlgorithmId.parse(var8[1]);
                              this.signature = var8[2].getBitString();
                              if (var8.length > 3) {
                                 DerValue var24 = var8[3];
                                 if (!var24.isContextSpecific((byte)0)) {
                                    throw new IOException("Bad encoding in certs element of OCSP response: expected ASN.1 context specific tag 0.");
                                 }

                                 DerValue[] var25 = var24.getData().getSequence(3);
                                 this.certs = new ArrayList(var25.length);

                                 try {
                                    for(int var26 = 0; var26 < var25.length; ++var26) {
                                       X509CertImpl var18 = new X509CertImpl(var25[var26].toByteArray());
                                       this.certs.add(var18);
                                       if (debug != null) {
                                          debug.println("OCSP response cert #" + (var26 + 1) + ": " + var18.getSubjectX500Principal());
                                       }
                                    }
                                 } catch (CertificateException var19) {
                                    throw new IOException("Bad encoding in X509 Certificate", var19);
                                 }
                              } else {
                                 this.certs = new ArrayList();
                              }

                           }
                        }
                     }
                  }
               }
            }
         } else {
            throw new IOException("Unknown OCSPResponse status: " + var4);
         }
      }
   }

   void verify(List<CertId> var1, OCSPResponse.IssuerInfo var2, X509Certificate var3, Date var4, byte[] var5, String var6) throws CertPathValidatorException {
      switch(this.responseStatus) {
      case SUCCESSFUL:
         Iterator var7 = var1.iterator();

         while(var7.hasNext()) {
            CertId var8 = (CertId)var7.next();
            OCSPResponse.SingleResponse var9 = this.getSingleResponse(var8);
            if (var9 == null) {
               if (debug != null) {
                  debug.println("No response found for CertId: " + var8);
               }

               throw new CertPathValidatorException("OCSP response does not include a response for a certificate supplied in the OCSP request");
            }

            if (debug != null) {
               debug.println("Status of certificate (with serial number " + var8.getSerialNumber() + ") is: " + var9.getCertStatus());
            }
         }

         if (this.signerCert == null) {
            try {
               if (var2.getCertificate() != null) {
                  this.certs.add(X509CertImpl.toImpl(var2.getCertificate()));
               }

               if (var3 != null) {
                  this.certs.add(X509CertImpl.toImpl(var3));
               }
            } catch (CertificateException var18) {
               throw new CertPathValidatorException("Invalid issuer or trusted responder certificate", var18);
            }

            Iterator var22;
            X509CertImpl var26;
            if (this.respId.getType() == ResponderId.Type.BY_NAME) {
               X500Principal var19 = this.respId.getResponderName();
               var22 = this.certs.iterator();

               while(var22.hasNext()) {
                  var26 = (X509CertImpl)var22.next();
                  if (var26.getSubjectX500Principal().equals(var19)) {
                     this.signerCert = var26;
                     break;
                  }
               }
            } else if (this.respId.getType() == ResponderId.Type.BY_KEY) {
               KeyIdentifier var20 = this.respId.getKeyIdentifier();
               var22 = this.certs.iterator();

               while(var22.hasNext()) {
                  var26 = (X509CertImpl)var22.next();
                  KeyIdentifier var10 = var26.getSubjectKeyId();
                  if (var10 != null && var20.equals(var10)) {
                     this.signerCert = var26;
                     break;
                  }

                  try {
                     var10 = new KeyIdentifier(var26.getPublicKey());
                  } catch (IOException var16) {
                  }

                  if (var20.equals(var10)) {
                     this.signerCert = var26;
                     break;
                  }
               }
            }
         }

         if (this.signerCert != null) {
            if (this.signerCert.getSubjectX500Principal().equals(var2.getName()) && this.signerCert.getPublicKey().equals(var2.getPublicKey())) {
               if (debug != null) {
                  debug.println("OCSP response is signed by the target's Issuing CA");
               }
            } else if (this.signerCert.equals(var3)) {
               if (debug != null) {
                  debug.println("OCSP response is signed by a Trusted Responder");
               }
            } else {
               if (!this.signerCert.getIssuerX500Principal().equals(var2.getName())) {
                  throw new CertPathValidatorException("Responder's certificate is not authorized to sign OCSP responses");
               }

               try {
                  List var21 = this.signerCert.getExtendedKeyUsage();
                  if (var21 == null || !var21.contains("1.3.6.1.5.5.7.3.9")) {
                     throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses");
                  }
               } catch (CertificateParsingException var17) {
                  throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses", var17);
               }

               AlgorithmChecker var23 = new AlgorithmChecker(var2.getAnchor(), var4, var6);
               var23.init(false);
               var23.check(this.signerCert, Collections.emptySet());

               try {
                  if (var4 == null) {
                     this.signerCert.checkValidity();
                  } else {
                     this.signerCert.checkValidity(var4);
                  }
               } catch (CertificateException var15) {
                  throw new CertPathValidatorException("Responder's certificate not within the validity period", var15);
               }

               sun.security.x509.Extension var25 = this.signerCert.getExtension(PKIXExtensions.OCSPNoCheck_Id);
               if (var25 != null && debug != null) {
                  debug.println("Responder's certificate includes the extension id-pkix-ocsp-nocheck.");
               }

               try {
                  this.signerCert.verify(var2.getPublicKey());
                  if (debug != null) {
                     debug.println("OCSP response is signed by an Authorized Responder");
                  }
               } catch (GeneralSecurityException var14) {
                  this.signerCert = null;
               }
            }
         }

         if (this.signerCert == null) {
            throw new CertPathValidatorException("Unable to verify OCSP Response's signature");
         } else {
            AlgorithmChecker.check(this.signerCert.getPublicKey(), this.sigAlgId, var6);
            if (!this.verifySignature(this.signerCert)) {
               throw new CertPathValidatorException("Error verifying OCSP Response's signature");
            } else if (var5 != null && this.responseNonce != null && !Arrays.equals(var5, this.responseNonce)) {
               throw new CertPathValidatorException("Nonces don't match");
            } else {
               long var24 = var4 == null ? System.currentTimeMillis() : var4.getTime();
               Date var27 = new Date(var24 + (long)MAX_CLOCK_SKEW);
               Date var28 = new Date(var24 - (long)MAX_CLOCK_SKEW);
               Iterator var11 = this.singleResponseMap.values().iterator();

               OCSPResponse.SingleResponse var12;
               do {
                  if (!var11.hasNext()) {
                     return;
                  }

                  var12 = (OCSPResponse.SingleResponse)var11.next();
                  if (debug != null) {
                     String var13 = "";
                     if (var12.nextUpdate != null) {
                        var13 = " until " + var12.nextUpdate;
                     }

                     debug.println("OCSP response validity interval is from " + var12.thisUpdate + var13);
                     debug.println("Checking validity of OCSP response on: " + new Date(var24));
                  }
               } while(!var27.before(var12.thisUpdate) && !var28.after(var12.nextUpdate != null ? var12.nextUpdate : var12.thisUpdate));

               throw new CertPathValidatorException("Response is unreliable: its validity interval is out-of-date");
            }
         }
      case TRY_LATER:
      case INTERNAL_ERROR:
         throw new CertPathValidatorException("OCSP response error: " + this.responseStatus, (Throwable)null, (CertPath)null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      case UNAUTHORIZED:
      default:
         throw new CertPathValidatorException("OCSP response error: " + this.responseStatus);
      }
   }

   public OCSPResponse.ResponseStatus getResponseStatus() {
      return this.responseStatus;
   }

   private boolean verifySignature(X509Certificate var1) throws CertPathValidatorException {
      try {
         Signature var2 = Signature.getInstance(this.sigAlgId.getName());
         var2.initVerify(var1.getPublicKey());
         var2.update(this.tbsResponseData);
         if (var2.verify(this.signature)) {
            if (debug != null) {
               debug.println("Verified signature of OCSP Response");
            }

            return true;
         } else {
            if (debug != null) {
               debug.println("Error verifying signature of OCSP Response");
            }

            return false;
         }
      } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException var3) {
         throw new CertPathValidatorException(var3);
      }
   }

   public OCSPResponse.SingleResponse getSingleResponse(CertId var1) {
      return (OCSPResponse.SingleResponse)this.singleResponseMap.get(var1);
   }

   public Set<CertId> getCertIds() {
      return Collections.unmodifiableSet(this.singleResponseMap.keySet());
   }

   X509Certificate getSignerCertificate() {
      return this.signerCert;
   }

   public ResponderId getResponderId() {
      return this.respId;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("OCSP Response:\n");
      var1.append("Response Status: ").append((Object)this.responseStatus).append("\n");
      var1.append("Responder ID: ").append((Object)this.respId).append("\n");
      var1.append("Produced at: ").append((Object)this.producedAtDate).append("\n");
      int var2 = this.singleResponseMap.size();
      var1.append(var2).append(var2 == 1 ? " response:\n" : " responses:\n");
      Iterator var3 = this.singleResponseMap.values().iterator();

      while(var3.hasNext()) {
         OCSPResponse.SingleResponse var4 = (OCSPResponse.SingleResponse)var3.next();
         var1.append((Object)var4).append("\n");
      }

      if (this.responseExtensions != null && this.responseExtensions.size() > 0) {
         var2 = this.responseExtensions.size();
         var1.append(var2).append(var2 == 1 ? " extension:\n" : " extensions:\n");
         var3 = this.responseExtensions.keySet().iterator();

         while(var3.hasNext()) {
            String var5 = (String)var3.next();
            var1.append(this.responseExtensions.get(var5)).append("\n");
         }
      }

      return var1.toString();
   }

   private static Map<String, Extension> parseExtensions(DerValue var0) throws IOException {
      DerValue[] var1 = var0.data.getSequence(3);
      HashMap var2 = new HashMap(var1.length);
      DerValue[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         DerValue var6 = var3[var5];
         sun.security.x509.Extension var7 = new sun.security.x509.Extension(var6);
         if (debug != null) {
            debug.println("Extension: " + var7);
         }

         if (var7.isCritical()) {
            throw new IOException("Unsupported OCSP critical extension: " + var7.getExtensionId());
         }

         var2.put(var7.getId(), var7);
      }

      return var2;
   }

   static {
      dump = debug != null && Debug.isOn("ocsp");
      OCSP_BASIC_RESPONSE_OID = ObjectIdentifier.newInternal(new int[]{1, 3, 6, 1, 5, 5, 7, 48, 1, 1});
      MAX_CLOCK_SKEW = initializeClockSkew();
      values = CRLReason.values();
   }

   static final class IssuerInfo {
      private final TrustAnchor anchor;
      private final X509Certificate certificate;
      private final X500Principal name;
      private final PublicKey pubKey;

      IssuerInfo(TrustAnchor var1) {
         this(var1, var1 != null ? var1.getTrustedCert() : null);
      }

      IssuerInfo(X509Certificate var1) {
         this((TrustAnchor)null, var1);
      }

      IssuerInfo(TrustAnchor var1, X509Certificate var2) {
         if (var1 == null && var2 == null) {
            throw new NullPointerException("TrustAnchor and issuerCert cannot be null");
         } else {
            this.anchor = var1;
            if (var2 != null) {
               this.name = var2.getSubjectX500Principal();
               this.pubKey = var2.getPublicKey();
               this.certificate = var2;
            } else {
               this.name = var1.getCA();
               this.pubKey = var1.getCAPublicKey();
               this.certificate = var1.getTrustedCert();
            }

         }
      }

      X509Certificate getCertificate() {
         return this.certificate;
      }

      X500Principal getName() {
         return this.name;
      }

      PublicKey getPublicKey() {
         return this.pubKey;
      }

      TrustAnchor getAnchor() {
         return this.anchor;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("Issuer Info:\n");
         var1.append("Name: ").append(this.name.toString()).append("\n");
         var1.append("Public Key:\n").append(this.pubKey.toString()).append("\n");
         return var1.toString();
      }
   }

   public static final class SingleResponse implements OCSP.RevocationStatus {
      private final CertId certId;
      private final OCSP.RevocationStatus.CertStatus certStatus;
      private final Date thisUpdate;
      private final Date nextUpdate;
      private final Date revocationTime;
      private final CRLReason revocationReason;
      private final Map<String, Extension> singleExtensions;

      private SingleResponse(DerValue var1) throws IOException {
         if (var1.tag != 48) {
            throw new IOException("Bad ASN.1 encoding in SingleResponse");
         } else {
            DerInputStream var2 = var1.data;
            this.certId = new CertId(var2.getDerValue().data);
            DerValue var3 = var2.getDerValue();
            short var4 = (short)((byte)(var3.tag & 31));
            if (var4 == 1) {
               this.certStatus = OCSP.RevocationStatus.CertStatus.REVOKED;
               this.revocationTime = var3.data.getGeneralizedTime();
               if (var3.data.available() != 0) {
                  DerValue var5 = var3.data.getDerValue();
                  var4 = (short)((byte)(var5.tag & 31));
                  if (var4 == 0) {
                     int var6 = var5.data.getEnumerated();
                     if (var6 >= 0 && var6 < OCSPResponse.values.length) {
                        this.revocationReason = OCSPResponse.values[var6];
                     } else {
                        this.revocationReason = CRLReason.UNSPECIFIED;
                     }
                  } else {
                     this.revocationReason = CRLReason.UNSPECIFIED;
                  }
               } else {
                  this.revocationReason = CRLReason.UNSPECIFIED;
               }

               if (OCSPResponse.debug != null) {
                  OCSPResponse.debug.println("Revocation time: " + this.revocationTime);
                  OCSPResponse.debug.println("Revocation reason: " + this.revocationReason);
               }
            } else {
               this.revocationTime = null;
               this.revocationReason = null;
               if (var4 == 0) {
                  this.certStatus = OCSP.RevocationStatus.CertStatus.GOOD;
               } else {
                  if (var4 != 2) {
                     throw new IOException("Invalid certificate status");
                  }

                  this.certStatus = OCSP.RevocationStatus.CertStatus.UNKNOWN;
               }
            }

            this.thisUpdate = var2.getGeneralizedTime();
            if (OCSPResponse.debug != null) {
               OCSPResponse.debug.println("thisUpdate: " + this.thisUpdate);
            }

            Date var9 = null;
            Map var10 = null;
            if (var2.available() > 0) {
               var3 = var2.getDerValue();
               if (var3.isContextSpecific((byte)0)) {
                  var9 = var3.data.getGeneralizedTime();
                  if (OCSPResponse.debug != null) {
                     OCSPResponse.debug.println("nextUpdate: " + var9);
                  }

                  var3 = var2.available() > 0 ? var2.getDerValue() : null;
               }

               if (var3 != null) {
                  if (!var3.isContextSpecific((byte)1)) {
                     throw new IOException("Unsupported singleResponse item, tag = " + String.format("%02X", var3.tag));
                  }

                  var10 = OCSPResponse.parseExtensions(var3);
                  if (var2.available() > 0) {
                     throw new IOException(var2.available() + " bytes of additional data in singleResponse");
                  }
               }
            }

            this.nextUpdate = var9;
            this.singleExtensions = var10 != null ? var10 : Collections.emptyMap();
            if (OCSPResponse.debug != null) {
               Iterator var7 = this.singleExtensions.values().iterator();

               while(var7.hasNext()) {
                  Extension var8 = (Extension)var7.next();
                  OCSPResponse.debug.println("singleExtension: " + var8);
               }
            }

         }
      }

      public OCSP.RevocationStatus.CertStatus getCertStatus() {
         return this.certStatus;
      }

      public CertId getCertId() {
         return this.certId;
      }

      public Date getThisUpdate() {
         return this.thisUpdate != null ? (Date)this.thisUpdate.clone() : null;
      }

      public Date getNextUpdate() {
         return this.nextUpdate != null ? (Date)this.nextUpdate.clone() : null;
      }

      public Date getRevocationTime() {
         return this.revocationTime != null ? (Date)this.revocationTime.clone() : null;
      }

      public CRLReason getRevocationReason() {
         return this.revocationReason;
      }

      public Map<String, Extension> getSingleExtensions() {
         return Collections.unmodifiableMap(this.singleExtensions);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("SingleResponse:\n");
         var1.append((Object)this.certId);
         var1.append("\nCertStatus: ").append((Object)this.certStatus).append("\n");
         if (this.certStatus == OCSP.RevocationStatus.CertStatus.REVOKED) {
            var1.append("revocationTime is ");
            var1.append((Object)this.revocationTime).append("\n");
            var1.append("revocationReason is ");
            var1.append((Object)this.revocationReason).append("\n");
         }

         var1.append("thisUpdate is ").append((Object)this.thisUpdate).append("\n");
         if (this.nextUpdate != null) {
            var1.append("nextUpdate is ").append((Object)this.nextUpdate).append("\n");
         }

         Iterator var2 = this.singleExtensions.values().iterator();

         while(var2.hasNext()) {
            Extension var3 = (Extension)var2.next();
            var1.append("singleExtension: ");
            var1.append(var3.toString()).append("\n");
         }

         return var1.toString();
      }

      // $FF: synthetic method
      SingleResponse(DerValue var1, Object var2) throws IOException {
         this(var1);
      }
   }

   public static enum ResponseStatus {
      SUCCESSFUL,
      MALFORMED_REQUEST,
      INTERNAL_ERROR,
      TRY_LATER,
      UNUSED,
      SIG_REQUIRED,
      UNAUTHORIZED;
   }
}
