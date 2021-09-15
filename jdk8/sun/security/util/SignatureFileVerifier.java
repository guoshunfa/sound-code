package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.CodeSigner;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarException;
import java.util.jar.Manifest;
import sun.security.jca.Providers;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;

public class SignatureFileVerifier {
   private static final Debug debug = Debug.getInstance("jar");
   private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
   private ArrayList<CodeSigner[]> signerCache;
   private static final String ATTR_DIGEST;
   private PKCS7 block;
   private byte[] sfBytes;
   private String name;
   private ManifestDigester md;
   private HashMap<String, MessageDigest> createdDigests;
   private boolean workaround = false;
   private CertificateFactory certificateFactory = null;
   private Map<String, Boolean> permittedAlgs = new HashMap();
   private Timestamp timestamp = null;
   private static final char[] hexc;

   public SignatureFileVerifier(ArrayList<CodeSigner[]> var1, ManifestDigester var2, String var3, byte[] var4) throws IOException, CertificateException {
      Object var5 = null;

      try {
         var5 = Providers.startJarVerification();
         this.block = new PKCS7(var4);
         this.sfBytes = this.block.getContentInfo().getData();
         this.certificateFactory = CertificateFactory.getInstance("X509");
      } finally {
         Providers.stopJarVerification(var5);
      }

      this.name = var3.substring(0, var3.lastIndexOf(46)).toUpperCase(Locale.ENGLISH);
      this.md = var2;
      this.signerCache = var1;
   }

   public boolean needSignatureFileBytes() {
      return this.sfBytes == null;
   }

   public boolean needSignatureFile(String var1) {
      return this.name.equalsIgnoreCase(var1);
   }

   public void setSignatureFile(byte[] var1) {
      this.sfBytes = var1;
   }

   public static boolean isBlockOrSF(String var0) {
      return var0.endsWith(".SF") || var0.endsWith(".DSA") || var0.endsWith(".RSA") || var0.endsWith(".EC");
   }

   public static boolean isSigningRelated(String var0) {
      var0 = var0.toUpperCase(Locale.ENGLISH);
      if (!var0.startsWith("META-INF/")) {
         return false;
      } else {
         var0 = var0.substring(9);
         if (var0.indexOf(47) != -1) {
            return false;
         } else if (!isBlockOrSF(var0) && !var0.equals("MANIFEST.MF")) {
            if (!var0.startsWith("SIG-")) {
               return false;
            } else {
               int var1 = var0.lastIndexOf(46);
               if (var1 != -1) {
                  String var2 = var0.substring(var1 + 1);
                  if (var2.length() <= 3 && var2.length() >= 1) {
                     for(int var3 = 0; var3 < var2.length(); ++var3) {
                        char var4 = var2.charAt(var3);
                        if ((var4 < 'A' || var4 > 'Z') && (var4 < '0' || var4 > '9')) {
                           return false;
                        }
                     }

                     return true;
                  } else {
                     return false;
                  }
               } else {
                  return true;
               }
            }
         } else {
            return true;
         }
      }
   }

   private MessageDigest getDigest(String var1) throws SignatureException {
      if (this.createdDigests == null) {
         this.createdDigests = new HashMap();
      }

      MessageDigest var2 = (MessageDigest)this.createdDigests.get(var1);
      if (var2 == null) {
         try {
            var2 = MessageDigest.getInstance(var1);
            this.createdDigests.put(var1, var2);
         } catch (NoSuchAlgorithmException var4) {
         }
      }

      return var2;
   }

   public void process(Hashtable<String, CodeSigner[]> var1, List<Object> var2) throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException {
      Object var3 = null;

      try {
         var3 = Providers.startJarVerification();
         this.processImpl(var1, var2);
      } finally {
         Providers.stopJarVerification(var3);
      }

   }

   private void processImpl(Hashtable<String, CodeSigner[]> var1, List<Object> var2) throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException {
      Manifest var3 = new Manifest();
      var3.read(new ByteArrayInputStream(this.sfBytes));
      String var4 = var3.getMainAttributes().getValue(Attributes.Name.SIGNATURE_VERSION);
      if (var4 != null && var4.equalsIgnoreCase("1.0")) {
         SignerInfo[] var5 = this.block.verify(this.sfBytes);
         if (var5 == null) {
            throw new SecurityException("cannot verify signature block file " + this.name);
         } else {
            CodeSigner[] var6 = this.getSigners(var5, this.block);
            if (var6 != null) {
               CodeSigner[] var7 = var6;
               int var8 = var6.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  CodeSigner var10 = var7[var9];
                  if (debug != null) {
                     debug.println("Gathering timestamp for:  " + var10.toString());
                  }

                  if (var10.getTimestamp() == null) {
                     this.timestamp = null;
                     break;
                  }

                  if (this.timestamp == null) {
                     this.timestamp = var10.getTimestamp();
                  } else if (this.timestamp.getTimestamp().before(var10.getTimestamp().getTimestamp())) {
                     this.timestamp = var10.getTimestamp();
                  }
               }

               Iterator var11 = var3.getEntries().entrySet().iterator();
               boolean var12 = this.verifyManifestHash(var3, this.md, var2);
               if (!var12 && !this.verifyManifestMainAttrs(var3, this.md)) {
                  throw new SecurityException("Invalid signature file digest for Manifest main attributes");
               } else {
                  while(true) {
                     while(var11.hasNext()) {
                        Map.Entry var13 = (Map.Entry)var11.next();
                        String var14 = (String)var13.getKey();
                        if (!var12 && !this.verifySection((Attributes)var13.getValue(), var14, this.md)) {
                           if (debug != null) {
                              debug.println("processSignature unsigned name = " + var14);
                           }
                        } else {
                           if (var14.startsWith("./")) {
                              var14 = var14.substring(2);
                           }

                           if (var14.startsWith("/")) {
                              var14 = var14.substring(1);
                           }

                           this.updateSigners(var6, var1, var14);
                           if (debug != null) {
                              debug.println("processSignature signed name = " + var14);
                           }
                        }
                     }

                     this.updateSigners(var6, var1, "META-INF/MANIFEST.MF");
                     return;
                  }
               }
            }
         }
      }
   }

   boolean permittedCheck(String var1, String var2) {
      Boolean var3 = (Boolean)this.permittedAlgs.get(var2);
      if (var3 == null) {
         try {
            JAR_DISABLED_CHECK.permits(var2, new ConstraintsParameters(this.timestamp));
         } catch (GeneralSecurityException var5) {
            this.permittedAlgs.put(var2, Boolean.FALSE);
            this.permittedAlgs.put(var1.toUpperCase(), Boolean.FALSE);
            if (debug != null) {
               if (var5.getMessage() != null) {
                  debug.println(var1 + ":  " + var5.getMessage());
               } else {
                  debug.println(var1 + ":  " + var2 + " was disabled, no exception msg given.");
                  var5.printStackTrace();
               }
            }

            return false;
         }

         this.permittedAlgs.put(var2, Boolean.TRUE);
         return true;
      } else {
         return var3;
      }
   }

   String getWeakAlgorithms(String var1) {
      String var2 = "";

      try {
         Iterator var3 = this.permittedAlgs.keySet().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            if (var4.endsWith(var1)) {
               var2 = var2 + var4.substring(0, var4.length() - var1.length()) + " ";
            }
         }
      } catch (RuntimeException var5) {
         var2 = "Unknown Algorithm(s).  Error processing " + var1 + ".  " + var5.getMessage();
      }

      return var2.length() == 0 ? "Unknown Algorithm(s)" : var2;
   }

   private boolean verifyManifestHash(Manifest var1, ManifestDigester var2, List<Object> var3) throws IOException, SignatureException {
      Attributes var4 = var1.getMainAttributes();
      boolean var5 = false;
      boolean var6 = true;
      boolean var7 = false;
      Iterator var8 = var4.entrySet().iterator();

      while(var8.hasNext()) {
         Map.Entry var9 = (Map.Entry)var8.next();
         String var10 = var9.getKey().toString();
         if (var10.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST-MANIFEST")) {
            String var11 = var10.substring(0, var10.length() - 16);
            var7 = true;
            if (this.permittedCheck(var10, var11)) {
               var6 = false;
               var3.add(var10);
               var3.add(var9.getValue());
               MessageDigest var12 = this.getDigest(var11);
               if (var12 != null) {
                  byte[] var13 = var2.manifestDigest(var12);
                  byte[] var14 = Base64.getMimeDecoder().decode((String)var9.getValue());
                  if (debug != null) {
                     debug.println("Signature File: Manifest digest " + var11);
                     debug.println("  sigfile  " + toHex(var14));
                     debug.println("  computed " + toHex(var13));
                     debug.println();
                  }

                  if (MessageDigest.isEqual(var13, var14)) {
                     var5 = true;
                  }
               }
            }
         }
      }

      if (debug != null) {
         debug.println("PermittedAlgs mapping: ");
         var8 = this.permittedAlgs.keySet().iterator();

         while(var8.hasNext()) {
            String var15 = (String)var8.next();
            debug.println(var15 + " : " + ((Boolean)this.permittedAlgs.get(var15)).toString());
         }
      }

      if (var7 && var6) {
         throw new SignatureException("Manifest hash check failed (DIGEST-MANIFEST). Disabled algorithm(s) used: " + this.getWeakAlgorithms("-DIGEST-MANIFEST"));
      } else {
         return var5;
      }
   }

   private boolean verifyManifestMainAttrs(Manifest var1, ManifestDigester var2) throws IOException, SignatureException {
      Attributes var3 = var1.getMainAttributes();
      boolean var4 = true;
      boolean var5 = true;
      boolean var6 = false;
      Iterator var7 = var3.entrySet().iterator();

      while(var7.hasNext()) {
         Map.Entry var8 = (Map.Entry)var7.next();
         String var9 = var8.getKey().toString();
         if (var9.toUpperCase(Locale.ENGLISH).endsWith(ATTR_DIGEST)) {
            String var10 = var9.substring(0, var9.length() - ATTR_DIGEST.length());
            var6 = true;
            if (this.permittedCheck(var9, var10)) {
               var5 = false;
               MessageDigest var11 = this.getDigest(var10);
               if (var11 != null) {
                  ManifestDigester.Entry var12 = var2.get("Manifest-Main-Attributes", false);
                  byte[] var13 = var12.digest(var11);
                  byte[] var14 = Base64.getMimeDecoder().decode((String)var8.getValue());
                  if (debug != null) {
                     debug.println("Signature File: Manifest Main Attributes digest " + var11.getAlgorithm());
                     debug.println("  sigfile  " + toHex(var14));
                     debug.println("  computed " + toHex(var13));
                     debug.println();
                  }

                  if (!MessageDigest.isEqual(var13, var14)) {
                     var4 = false;
                     if (debug != null) {
                        debug.println("Verification of Manifest main attributes failed");
                        debug.println();
                     }
                     break;
                  }
               }
            }
         }
      }

      if (debug != null) {
         debug.println("PermittedAlgs mapping: ");
         var7 = this.permittedAlgs.keySet().iterator();

         while(var7.hasNext()) {
            String var15 = (String)var7.next();
            debug.println(var15 + " : " + ((Boolean)this.permittedAlgs.get(var15)).toString());
         }
      }

      if (var6 && var5) {
         throw new SignatureException("Manifest Main Attribute check failed (" + ATTR_DIGEST + ").  Disabled algorithm(s) used: " + this.getWeakAlgorithms(ATTR_DIGEST));
      } else {
         return var4;
      }
   }

   private boolean verifySection(Attributes var1, String var2, ManifestDigester var3) throws IOException, SignatureException {
      boolean var4 = false;
      ManifestDigester.Entry var5 = var3.get(var2, this.block.isOldStyle());
      boolean var6 = true;
      boolean var7 = false;
      if (var5 == null) {
         throw new SecurityException("no manifest section for signature file entry " + var2);
      } else {
         Iterator var8;
         if (var1 != null) {
            var8 = var1.entrySet().iterator();

            while(var8.hasNext()) {
               Map.Entry var9 = (Map.Entry)var8.next();
               String var10 = var9.getKey().toString();
               if (var10.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST")) {
                  String var11 = var10.substring(0, var10.length() - 7);
                  var7 = true;
                  if (this.permittedCheck(var10, var11)) {
                     var6 = false;
                     MessageDigest var12 = this.getDigest(var11);
                     if (var12 != null) {
                        boolean var13 = false;
                        byte[] var14 = Base64.getMimeDecoder().decode((String)var9.getValue());
                        byte[] var15;
                        if (this.workaround) {
                           var15 = var5.digestWorkaround(var12);
                        } else {
                           var15 = var5.digest(var12);
                        }

                        if (debug != null) {
                           debug.println("Signature Block File: " + var2 + " digest=" + var12.getAlgorithm());
                           debug.println("  expected " + toHex(var14));
                           debug.println("  computed " + toHex(var15));
                           debug.println();
                        }

                        if (MessageDigest.isEqual(var15, var14)) {
                           var4 = true;
                           var13 = true;
                        } else if (!this.workaround) {
                           var15 = var5.digestWorkaround(var12);
                           if (MessageDigest.isEqual(var15, var14)) {
                              if (debug != null) {
                                 debug.println("  re-computed " + toHex(var15));
                                 debug.println();
                              }

                              this.workaround = true;
                              var4 = true;
                              var13 = true;
                           }
                        }

                        if (!var13) {
                           throw new SecurityException("invalid " + var12.getAlgorithm() + " signature file digest for " + var2);
                        }
                     }
                  }
               }
            }
         }

         if (debug != null) {
            debug.println("PermittedAlgs mapping: ");
            var8 = this.permittedAlgs.keySet().iterator();

            while(var8.hasNext()) {
               String var16 = (String)var8.next();
               debug.println(var16 + " : " + ((Boolean)this.permittedAlgs.get(var16)).toString());
            }
         }

         if (var7 && var6) {
            throw new SignatureException("Manifest Main Attribute check failed (DIGEST).  Disabled algorithm(s) used: " + this.getWeakAlgorithms("DIGEST"));
         } else {
            return var4;
         }
      }
   }

   private CodeSigner[] getSigners(SignerInfo[] var1, PKCS7 var2) throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException {
      ArrayList var3 = null;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         SignerInfo var5 = var1[var4];
         ArrayList var6 = var5.getCertificateChain(var2);
         CertPath var7 = this.certificateFactory.generateCertPath((List)var6);
         if (var3 == null) {
            var3 = new ArrayList();
         }

         var3.add(new CodeSigner(var7, var5.getTimestamp()));
         if (debug != null) {
            debug.println("Signature Block Certificate: " + var6.get(0));
         }
      }

      if (var3 != null) {
         return (CodeSigner[])var3.toArray(new CodeSigner[var3.size()]);
      } else {
         return null;
      }
   }

   static String toHex(byte[] var0) {
      StringBuilder var1 = new StringBuilder(var0.length * 2);

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1.append(hexc[var0[var2] >> 4 & 15]);
         var1.append(hexc[var0[var2] & 15]);
      }

      return var1.toString();
   }

   static boolean contains(CodeSigner[] var0, CodeSigner var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2].equals(var1)) {
            return true;
         }
      }

      return false;
   }

   static boolean isSubSet(CodeSigner[] var0, CodeSigner[] var1) {
      if (var1 == var0) {
         return true;
      } else {
         for(int var3 = 0; var3 < var0.length; ++var3) {
            if (!contains(var1, var0[var3])) {
               return false;
            }
         }

         return true;
      }
   }

   static boolean matches(CodeSigner[] var0, CodeSigner[] var1, CodeSigner[] var2) {
      if (var1 == null && var0 == var2) {
         return true;
      } else if (var1 != null && !isSubSet(var1, var0)) {
         return false;
      } else if (!isSubSet(var2, var0)) {
         return false;
      } else {
         for(int var4 = 0; var4 < var0.length; ++var4) {
            boolean var5 = var1 != null && contains(var1, var0[var4]) || contains(var2, var0[var4]);
            if (!var5) {
               return false;
            }
         }

         return true;
      }
   }

   void updateSigners(CodeSigner[] var1, Hashtable<String, CodeSigner[]> var2, String var3) {
      CodeSigner[] var4 = (CodeSigner[])var2.get(var3);

      CodeSigner[] var5;
      for(int var6 = this.signerCache.size() - 1; var6 != -1; --var6) {
         var5 = (CodeSigner[])this.signerCache.get(var6);
         if (matches(var5, var4, var1)) {
            var2.put(var3, var5);
            return;
         }
      }

      if (var4 == null) {
         var5 = var1;
      } else {
         var5 = new CodeSigner[var4.length + var1.length];
         System.arraycopy(var4, 0, var5, 0, var4.length);
         System.arraycopy(var1, 0, var5, var4.length, var1.length);
      }

      this.signerCache.add(var5);
      var2.put(var3, var5);
   }

   static {
      ATTR_DIGEST = "-DIGEST-Manifest-Main-Attributes".toUpperCase(Locale.ENGLISH);
      hexc = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   }
}
