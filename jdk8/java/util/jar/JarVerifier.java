package java.util.jar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import sun.security.util.Debug;
import sun.security.util.ManifestDigester;
import sun.security.util.ManifestEntryVerifier;
import sun.security.util.SignatureFileVerifier;

class JarVerifier {
   static final Debug debug = Debug.getInstance("jar");
   private Hashtable<String, CodeSigner[]> verifiedSigners;
   private Hashtable<String, CodeSigner[]> sigFileSigners;
   private Hashtable<String, byte[]> sigFileData;
   private ArrayList<SignatureFileVerifier> pendingBlocks;
   private ArrayList<CodeSigner[]> signerCache;
   private boolean parsingBlockOrSF = false;
   private boolean parsingMeta = true;
   private boolean anyToVerify = true;
   private ByteArrayOutputStream baos;
   private volatile ManifestDigester manDig;
   byte[] manifestRawBytes = null;
   boolean eagerValidation;
   private Object csdomain = new Object();
   private List<Object> manifestDigests;
   private Map<URL, Map<CodeSigner[], CodeSource>> urlToCodeSourceMap = new HashMap();
   private Map<CodeSigner[], CodeSource> signerToCodeSource = new HashMap();
   private URL lastURL;
   private Map<CodeSigner[], CodeSource> lastURLMap;
   private CodeSigner[] emptySigner = new CodeSigner[0];
   private Map<String, CodeSigner[]> signerMap;
   private Enumeration<String> emptyEnumeration = new Enumeration<String>() {
      public boolean hasMoreElements() {
         return false;
      }

      public String nextElement() {
         throw new NoSuchElementException();
      }
   };
   private List<CodeSigner[]> jarCodeSigners;

   public JarVerifier(byte[] var1) {
      this.manifestRawBytes = var1;
      this.sigFileSigners = new Hashtable();
      this.verifiedSigners = new Hashtable();
      this.sigFileData = new Hashtable(11);
      this.pendingBlocks = new ArrayList();
      this.baos = new ByteArrayOutputStream();
      this.manifestDigests = new ArrayList();
   }

   public void beginEntry(JarEntry var1, ManifestEntryVerifier var2) throws IOException {
      if (var1 != null) {
         if (debug != null) {
            debug.println("beginEntry " + var1.getName());
         }

         String var3 = var1.getName();
         if (this.parsingMeta) {
            String var4 = var3.toUpperCase(Locale.ENGLISH);
            if (var4.startsWith("META-INF/") || var4.startsWith("/META-INF/")) {
               if (var1.isDirectory()) {
                  var2.setEntry((String)null, var1);
                  return;
               }

               if (var4.equals("META-INF/MANIFEST.MF") || var4.equals("META-INF/INDEX.LIST")) {
                  return;
               }

               if (SignatureFileVerifier.isBlockOrSF(var4)) {
                  this.parsingBlockOrSF = true;
                  this.baos.reset();
                  var2.setEntry((String)null, var1);
                  return;
               }
            }
         }

         if (this.parsingMeta) {
            this.doneWithMeta();
         }

         if (var1.isDirectory()) {
            var2.setEntry((String)null, var1);
         } else {
            if (var3.startsWith("./")) {
               var3 = var3.substring(2);
            }

            if (var3.startsWith("/")) {
               var3 = var3.substring(1);
            }

            if (var3.equals("META-INF/MANIFEST.MF") || this.sigFileSigners.get(var3) == null && this.verifiedSigners.get(var3) == null) {
               var2.setEntry((String)null, var1);
            } else {
               var2.setEntry(var3, var1);
            }
         }
      }
   }

   public void update(int var1, ManifestEntryVerifier var2) throws IOException {
      if (var1 != -1) {
         if (this.parsingBlockOrSF) {
            this.baos.write(var1);
         } else {
            var2.update((byte)var1);
         }
      } else {
         this.processEntry(var2);
      }

   }

   public void update(int var1, byte[] var2, int var3, int var4, ManifestEntryVerifier var5) throws IOException {
      if (var1 != -1) {
         if (this.parsingBlockOrSF) {
            this.baos.write(var2, var3, var1);
         } else {
            var5.update(var2, var3, var1);
         }
      } else {
         this.processEntry(var5);
      }

   }

   private void processEntry(ManifestEntryVerifier var1) throws IOException {
      if (!this.parsingBlockOrSF) {
         JarEntry var2 = var1.getEntry();
         if (var2 != null && var2.signers == null) {
            var2.signers = var1.verify(this.verifiedSigners, this.sigFileSigners);
            var2.certs = mapSignersToCertArray(var2.signers);
         }
      } else {
         try {
            this.parsingBlockOrSF = false;
            if (debug != null) {
               debug.println("processEntry: processing block");
            }

            String var13 = var1.getEntry().getName().toUpperCase(Locale.ENGLISH);
            String var3;
            if (var13.endsWith(".SF")) {
               var3 = var13.substring(0, var13.length() - 3);
               byte[] var14 = this.baos.toByteArray();
               this.sigFileData.put(var3, var14);
               Iterator var15 = this.pendingBlocks.iterator();

               while(var15.hasNext()) {
                  SignatureFileVerifier var6 = (SignatureFileVerifier)var15.next();
                  if (var6.needSignatureFile(var3)) {
                     if (debug != null) {
                        debug.println("processEntry: processing pending block");
                     }

                     var6.setSignatureFile(var14);
                     var6.process(this.sigFileSigners, this.manifestDigests);
                  }
               }

               return;
            }

            var3 = var13.substring(0, var13.lastIndexOf("."));
            if (this.signerCache == null) {
               this.signerCache = new ArrayList();
            }

            if (this.manDig == null) {
               synchronized(this.manifestRawBytes) {
                  if (this.manDig == null) {
                     this.manDig = new ManifestDigester(this.manifestRawBytes);
                     this.manifestRawBytes = null;
                  }
               }
            }

            SignatureFileVerifier var4 = new SignatureFileVerifier(this.signerCache, this.manDig, var13, this.baos.toByteArray());
            if (var4.needSignatureFileBytes()) {
               byte[] var5 = (byte[])this.sigFileData.get(var3);
               if (var5 == null) {
                  if (debug != null) {
                     debug.println("adding pending block");
                  }

                  this.pendingBlocks.add(var4);
                  return;
               }

               var4.setSignatureFile(var5);
            }

            var4.process(this.sigFileSigners, this.manifestDigests);
         } catch (IOException var9) {
            if (debug != null) {
               debug.println("processEntry caught: " + var9);
            }
         } catch (SignatureException var10) {
            if (debug != null) {
               debug.println("processEntry caught: " + var10);
            }
         } catch (NoSuchAlgorithmException var11) {
            if (debug != null) {
               debug.println("processEntry caught: " + var11);
            }
         } catch (CertificateException var12) {
            if (debug != null) {
               debug.println("processEntry caught: " + var12);
            }
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public Certificate[] getCerts(String var1) {
      return mapSignersToCertArray(this.getCodeSigners(var1));
   }

   public Certificate[] getCerts(JarFile var1, JarEntry var2) {
      return mapSignersToCertArray(this.getCodeSigners(var1, var2));
   }

   public CodeSigner[] getCodeSigners(String var1) {
      return (CodeSigner[])this.verifiedSigners.get(var1);
   }

   public CodeSigner[] getCodeSigners(JarFile var1, JarEntry var2) {
      String var3 = var2.getName();
      if (this.eagerValidation && this.sigFileSigners.get(var3) != null) {
         try {
            InputStream var4 = var1.getInputStream(var2);
            byte[] var5 = new byte[1024];

            for(int var6 = var5.length; var6 != -1; var6 = var4.read(var5, 0, var5.length)) {
            }

            var4.close();
         } catch (IOException var7) {
         }
      }

      return this.getCodeSigners(var3);
   }

   private static Certificate[] mapSignersToCertArray(CodeSigner[] var0) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var1 = new ArrayList();

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1.addAll(var0[var2].getSignerCertPath().getCertificates());
         }

         return (Certificate[])var1.toArray(new Certificate[var1.size()]);
      }
   }

   boolean nothingToVerify() {
      return !this.anyToVerify;
   }

   void doneWithMeta() {
      this.parsingMeta = false;
      this.anyToVerify = !this.sigFileSigners.isEmpty();
      this.baos = null;
      this.sigFileData = null;
      this.pendingBlocks = null;
      this.signerCache = null;
      this.manDig = null;
      if (this.sigFileSigners.containsKey("META-INF/MANIFEST.MF")) {
         CodeSigner[] var1 = (CodeSigner[])this.sigFileSigners.remove("META-INF/MANIFEST.MF");
         this.verifiedSigners.put("META-INF/MANIFEST.MF", var1);
      }

   }

   private synchronized CodeSource mapSignersToCodeSource(URL var1, CodeSigner[] var2) {
      Object var3;
      if (var1 == this.lastURL) {
         var3 = this.lastURLMap;
      } else {
         var3 = (Map)this.urlToCodeSourceMap.get(var1);
         if (var3 == null) {
            var3 = new HashMap();
            this.urlToCodeSourceMap.put(var1, var3);
         }

         this.lastURLMap = (Map)var3;
         this.lastURL = var1;
      }

      Object var4 = (CodeSource)((Map)var3).get(var2);
      if (var4 == null) {
         var4 = new JarVerifier.VerifierCodeSource(this.csdomain, var1, var2);
         this.signerToCodeSource.put(var2, var4);
      }

      return (CodeSource)var4;
   }

   private CodeSource[] mapSignersToCodeSources(URL var1, List<CodeSigner[]> var2, boolean var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         var4.add(this.mapSignersToCodeSource(var1, (CodeSigner[])var2.get(var5)));
      }

      if (var3) {
         var4.add(this.mapSignersToCodeSource(var1, (CodeSigner[])null));
      }

      return (CodeSource[])var4.toArray(new CodeSource[var4.size()]);
   }

   private CodeSigner[] findMatchingSigners(CodeSource var1) {
      if (var1 instanceof JarVerifier.VerifierCodeSource) {
         JarVerifier.VerifierCodeSource var2 = (JarVerifier.VerifierCodeSource)var1;
         if (var2.isSameDomain(this.csdomain)) {
            return ((JarVerifier.VerifierCodeSource)var1).getPrivateSigners();
         }
      }

      CodeSource[] var6 = this.mapSignersToCodeSources(var1.getLocation(), this.getJarCodeSigners(), true);
      ArrayList var3 = new ArrayList();

      int var4;
      for(var4 = 0; var4 < var6.length; ++var4) {
         var3.add(var6[var4]);
      }

      var4 = var3.indexOf(var1);
      if (var4 != -1) {
         CodeSigner[] var5 = ((JarVerifier.VerifierCodeSource)var3.get(var4)).getPrivateSigners();
         if (var5 == null) {
            var5 = this.emptySigner;
         }

         return var5;
      } else {
         return null;
      }
   }

   private synchronized Map<String, CodeSigner[]> signerMap() {
      if (this.signerMap == null) {
         this.signerMap = new HashMap(this.verifiedSigners.size() + this.sigFileSigners.size());
         this.signerMap.putAll(this.verifiedSigners);
         this.signerMap.putAll(this.sigFileSigners);
      }

      return this.signerMap;
   }

   public synchronized Enumeration<String> entryNames(JarFile var1, CodeSource[] var2) {
      Map var3 = this.signerMap();
      final Iterator var4 = var3.entrySet().iterator();
      boolean var5 = false;
      final ArrayList var6 = new ArrayList(var2.length);

      for(int var7 = 0; var7 < var2.length; ++var7) {
         CodeSigner[] var8 = this.findMatchingSigners(var2[var7]);
         if (var8 != null) {
            if (var8.length > 0) {
               var6.add(var8);
            } else {
               var5 = true;
            }
         } else {
            var5 = true;
         }
      }

      final Enumeration var9 = var5 ? this.unsignedEntryNames(var1) : this.emptyEnumeration;
      return new Enumeration<String>() {
         String name;

         public boolean hasMoreElements() {
            if (this.name != null) {
               return true;
            } else {
               Map.Entry var1;
               do {
                  if (!var4.hasNext()) {
                     if (var9.hasMoreElements()) {
                        this.name = (String)var9.nextElement();
                        return true;
                     }

                     return false;
                  }

                  var1 = (Map.Entry)var4.next();
               } while(!var6.contains(var1.getValue()));

               this.name = (String)var1.getKey();
               return true;
            }
         }

         public String nextElement() {
            if (this.hasMoreElements()) {
               String var1 = this.name;
               this.name = null;
               return var1;
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   public Enumeration<JarEntry> entries2(final JarFile var1, final Enumeration<? extends ZipEntry> var2) {
      final HashMap var3 = new HashMap();
      var3.putAll(this.signerMap());
      return new Enumeration<JarEntry>() {
         Enumeration<String> signers = null;
         JarEntry entry;

         public boolean hasMoreElements() {
            if (this.entry != null) {
               return true;
            } else {
               ZipEntry var1x;
               do {
                  if (!var2.hasMoreElements()) {
                     if (this.signers == null) {
                        this.signers = Collections.enumeration(var3.keySet());
                     }

                     if (this.signers.hasMoreElements()) {
                        String var2x = (String)this.signers.nextElement();
                        this.entry = var1.newEntry(new ZipEntry(var2x));
                        return true;
                     }

                     return false;
                  }

                  var1x = (ZipEntry)var2.nextElement();
               } while(JarVerifier.isSigningRelated(var1x.getName()));

               this.entry = var1.newEntry(var1x);
               return true;
            }
         }

         public JarEntry nextElement() {
            if (this.hasMoreElements()) {
               JarEntry var1x = this.entry;
               var3.remove(var1x.getName());
               this.entry = null;
               return var1x;
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   static boolean isSigningRelated(String var0) {
      return SignatureFileVerifier.isSigningRelated(var0);
   }

   private Enumeration<String> unsignedEntryNames(JarFile var1) {
      final Map var2 = this.signerMap();
      final Enumeration var3 = var1.entries();
      return new Enumeration<String>() {
         String name;

         public boolean hasMoreElements() {
            if (this.name != null) {
               return true;
            } else {
               String var1;
               ZipEntry var2x;
               do {
                  if (!var3.hasMoreElements()) {
                     return false;
                  }

                  var2x = (ZipEntry)var3.nextElement();
                  var1 = var2x.getName();
               } while(var2x.isDirectory() || JarVerifier.isSigningRelated(var1) || var2.get(var1) != null);

               this.name = var1;
               return true;
            }
         }

         public String nextElement() {
            if (this.hasMoreElements()) {
               String var1 = this.name;
               this.name = null;
               return var1;
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   private synchronized List<CodeSigner[]> getJarCodeSigners() {
      if (this.jarCodeSigners == null) {
         HashSet var2 = new HashSet();
         var2.addAll(this.signerMap().values());
         this.jarCodeSigners = new ArrayList();
         this.jarCodeSigners.addAll(var2);
      }

      return this.jarCodeSigners;
   }

   public synchronized CodeSource[] getCodeSources(JarFile var1, URL var2) {
      boolean var3 = this.unsignedEntryNames(var1).hasMoreElements();
      return this.mapSignersToCodeSources(var2, this.getJarCodeSigners(), var3);
   }

   public CodeSource getCodeSource(URL var1, String var2) {
      CodeSigner[] var3 = (CodeSigner[])this.signerMap().get(var2);
      return this.mapSignersToCodeSource(var1, var3);
   }

   public CodeSource getCodeSource(URL var1, JarFile var2, JarEntry var3) {
      return this.mapSignersToCodeSource(var1, this.getCodeSigners(var2, var3));
   }

   public void setEagerValidation(boolean var1) {
      this.eagerValidation = var1;
   }

   public synchronized List<Object> getManifestDigests() {
      return Collections.unmodifiableList(this.manifestDigests);
   }

   static CodeSource getUnsignedCS(URL var0) {
      return new JarVerifier.VerifierCodeSource((Object)null, var0, (Certificate[])null);
   }

   private static class VerifierCodeSource extends CodeSource {
      private static final long serialVersionUID = -9047366145967768825L;
      URL vlocation;
      CodeSigner[] vsigners;
      Certificate[] vcerts;
      Object csdomain;

      VerifierCodeSource(Object var1, URL var2, CodeSigner[] var3) {
         super(var2, var3);
         this.csdomain = var1;
         this.vlocation = var2;
         this.vsigners = var3;
      }

      VerifierCodeSource(Object var1, URL var2, Certificate[] var3) {
         super(var2, var3);
         this.csdomain = var1;
         this.vlocation = var2;
         this.vcerts = var3;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            if (var1 instanceof JarVerifier.VerifierCodeSource) {
               JarVerifier.VerifierCodeSource var2 = (JarVerifier.VerifierCodeSource)var1;
               if (this.isSameDomain(var2.csdomain)) {
                  if (var2.vsigners == this.vsigners && var2.vcerts == this.vcerts) {
                     if (var2.vlocation != null) {
                        return var2.vlocation.equals(this.vlocation);
                     }

                     if (this.vlocation != null) {
                        return this.vlocation.equals(var2.vlocation);
                     }

                     return true;
                  }

                  return false;
               }
            }

            return super.equals(var1);
         }
      }

      boolean isSameDomain(Object var1) {
         return this.csdomain == var1;
      }

      private CodeSigner[] getPrivateSigners() {
         return this.vsigners;
      }

      private Certificate[] getPrivateCertificates() {
         return this.vcerts;
      }
   }

   static class VerifierStream extends InputStream {
      private InputStream is;
      private JarVerifier jv;
      private ManifestEntryVerifier mev;
      private long numLeft;

      VerifierStream(Manifest var1, JarEntry var2, InputStream var3, JarVerifier var4) throws IOException {
         this.is = var3;
         this.jv = var4;
         this.mev = new ManifestEntryVerifier(var1);
         this.jv.beginEntry(var2, this.mev);
         this.numLeft = var2.getSize();
         if (this.numLeft == 0L) {
            this.jv.update(-1, this.mev);
         }

      }

      public int read() throws IOException {
         if (this.numLeft > 0L) {
            int var1 = this.is.read();
            this.jv.update(var1, this.mev);
            --this.numLeft;
            if (this.numLeft == 0L) {
               this.jv.update(-1, this.mev);
            }

            return var1;
         } else {
            return -1;
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (this.numLeft > 0L && this.numLeft < (long)var3) {
            var3 = (int)this.numLeft;
         }

         if (this.numLeft > 0L) {
            int var4 = this.is.read(var1, var2, var3);
            this.jv.update(var4, var1, var2, var3, this.mev);
            this.numLeft -= (long)var4;
            if (this.numLeft == 0L) {
               this.jv.update(-1, var1, var2, var3, this.mev);
            }

            return var4;
         } else {
            return -1;
         }
      }

      public void close() throws IOException {
         if (this.is != null) {
            this.is.close();
         }

         this.is = null;
         this.mev = null;
         this.jv = null;
      }

      public int available() throws IOException {
         return this.is.available();
      }
   }
}
