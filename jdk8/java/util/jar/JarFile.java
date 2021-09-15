package java.util.jar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import sun.misc.IOUtils;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;
import sun.security.util.ManifestEntryVerifier;
import sun.security.util.SignatureFileVerifier;

public class JarFile extends ZipFile {
   private SoftReference<Manifest> manRef;
   private JarEntry manEntry;
   private JarVerifier jv;
   private boolean jvInitialized;
   private boolean verify;
   private boolean hasClassPathAttribute;
   private volatile boolean hasCheckedSpecialAttributes;
   public static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
   private static final char[] CLASSPATH_CHARS;
   private static final int[] CLASSPATH_LASTOCC;
   private static final int[] CLASSPATH_OPTOSFT;
   private static String javaHome;
   private static volatile String[] jarNames;

   public JarFile(String var1) throws IOException {
      this(new File(var1), true, 1);
   }

   public JarFile(String var1, boolean var2) throws IOException {
      this(new File(var1), var2, 1);
   }

   public JarFile(File var1) throws IOException {
      this(var1, true, 1);
   }

   public JarFile(File var1, boolean var2) throws IOException {
      this(var1, var2, 1);
   }

   public JarFile(File var1, boolean var2, int var3) throws IOException {
      super(var1, var3);
      this.verify = var2;
   }

   public Manifest getManifest() throws IOException {
      return this.getManifestFromReference();
   }

   private Manifest getManifestFromReference() throws IOException {
      Manifest var1 = this.manRef != null ? (Manifest)this.manRef.get() : null;
      if (var1 == null) {
         JarEntry var2 = this.getManEntry();
         if (var2 != null) {
            if (this.verify) {
               byte[] var3 = this.getBytes(var2);
               var1 = new Manifest(new ByteArrayInputStream(var3));
               if (!this.jvInitialized) {
                  this.jv = new JarVerifier(var3);
               }
            } else {
               var1 = new Manifest(super.getInputStream(var2));
            }

            this.manRef = new SoftReference(var1);
         }
      }

      return var1;
   }

   private native String[] getMetaInfEntryNames();

   public JarEntry getJarEntry(String var1) {
      return (JarEntry)this.getEntry(var1);
   }

   public ZipEntry getEntry(String var1) {
      ZipEntry var2 = super.getEntry(var1);
      return var2 != null ? new JarFile.JarFileEntry(var2) : null;
   }

   public Enumeration<JarEntry> entries() {
      return new JarFile.JarEntryIterator();
   }

   public Stream<JarEntry> stream() {
      return StreamSupport.stream(Spliterators.spliterator((Iterator)(new JarFile.JarEntryIterator()), (long)this.size(), 1297), false);
   }

   private void maybeInstantiateVerifier() throws IOException {
      if (this.jv == null) {
         if (this.verify) {
            String[] var1 = this.getMetaInfEntryNames();
            if (var1 != null) {
               for(int var2 = 0; var2 < var1.length; ++var2) {
                  String var3 = var1[var2].toUpperCase(Locale.ENGLISH);
                  if (var3.endsWith(".DSA") || var3.endsWith(".RSA") || var3.endsWith(".EC") || var3.endsWith(".SF")) {
                     this.getManifest();
                     return;
                  }
               }
            }

            this.verify = false;
         }

      }
   }

   private void initializeVerifier() {
      ManifestEntryVerifier var1 = null;

      try {
         String[] var2 = this.getMetaInfEntryNames();
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               String var4 = var2[var3].toUpperCase(Locale.ENGLISH);
               if ("META-INF/MANIFEST.MF".equals(var4) || SignatureFileVerifier.isBlockOrSF(var4)) {
                  JarEntry var5 = this.getJarEntry(var2[var3]);
                  if (var5 == null) {
                     throw new JarException("corrupted jar file");
                  }

                  if (var1 == null) {
                     var1 = new ManifestEntryVerifier(this.getManifestFromReference());
                  }

                  byte[] var6 = this.getBytes(var5);
                  if (var6 != null && var6.length > 0) {
                     this.jv.beginEntry(var5, var1);
                     this.jv.update(var6.length, var6, 0, var6.length, var1);
                     this.jv.update(-1, (byte[])null, 0, 0, var1);
                  }
               }
            }
         }
      } catch (IOException var7) {
         this.jv = null;
         this.verify = false;
         if (JarVerifier.debug != null) {
            JarVerifier.debug.println("jarfile parsing error!");
            var7.printStackTrace();
         }
      }

      if (this.jv != null) {
         this.jv.doneWithMeta();
         if (JarVerifier.debug != null) {
            JarVerifier.debug.println("done with meta!");
         }

         if (this.jv.nothingToVerify()) {
            if (JarVerifier.debug != null) {
               JarVerifier.debug.println("nothing to verify!");
            }

            this.jv = null;
            this.verify = false;
         }
      }

   }

   private byte[] getBytes(ZipEntry var1) throws IOException {
      InputStream var2 = super.getInputStream(var1);
      Throwable var3 = null;

      byte[] var4;
      try {
         var4 = IOUtils.readFully(var2, (int)var1.getSize(), true);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               var2.close();
            }
         }

      }

      return var4;
   }

   public synchronized InputStream getInputStream(ZipEntry var1) throws IOException {
      this.maybeInstantiateVerifier();
      if (this.jv == null) {
         return super.getInputStream(var1);
      } else {
         if (!this.jvInitialized) {
            this.initializeVerifier();
            this.jvInitialized = true;
            if (this.jv == null) {
               return super.getInputStream(var1);
            }
         }

         return new JarVerifier.VerifierStream(this.getManifestFromReference(), var1 instanceof JarFile.JarFileEntry ? (JarEntry)var1 : this.getJarEntry(var1.getName()), super.getInputStream(var1), this.jv);
      }
   }

   private JarEntry getManEntry() {
      if (this.manEntry == null) {
         this.manEntry = this.getJarEntry("META-INF/MANIFEST.MF");
         if (this.manEntry == null) {
            String[] var1 = this.getMetaInfEntryNames();
            if (var1 != null) {
               for(int var2 = 0; var2 < var1.length; ++var2) {
                  if ("META-INF/MANIFEST.MF".equals(var1[var2].toUpperCase(Locale.ENGLISH))) {
                     this.manEntry = this.getJarEntry(var1[var2]);
                     break;
                  }
               }
            }
         }
      }

      return this.manEntry;
   }

   boolean hasClassPathAttribute() throws IOException {
      this.checkForSpecialAttributes();
      return this.hasClassPathAttribute;
   }

   private boolean match(char[] var1, byte[] var2, int[] var3, int[] var4) {
      int var5 = var1.length;
      int var6 = var2.length - var5;

      int var8;
      char var9;
      label28:
      for(int var7 = 0; var7 <= var6; var7 += Math.max(var8 + 1 - var3[var9 & 127], var4[var8])) {
         for(var8 = var5 - 1; var8 >= 0; --var8) {
            var9 = (char)var2[var7 + var8];
            var9 = (var9 - 65 | 90 - var9) >= 0 ? (char)(var9 + 32) : var9;
            if (var9 != var1[var8]) {
               continue label28;
            }
         }

         return true;
      }

      return false;
   }

   private void checkForSpecialAttributes() throws IOException {
      if (!this.hasCheckedSpecialAttributes) {
         if (!this.isKnownNotToHaveSpecialAttributes()) {
            JarEntry var1 = this.getManEntry();
            if (var1 != null) {
               byte[] var2 = this.getBytes(var1);
               if (this.match(CLASSPATH_CHARS, var2, CLASSPATH_LASTOCC, CLASSPATH_OPTOSFT)) {
                  this.hasClassPathAttribute = true;
               }
            }
         }

         this.hasCheckedSpecialAttributes = true;
      }
   }

   private boolean isKnownNotToHaveSpecialAttributes() {
      if (javaHome == null) {
         javaHome = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.home")));
      }

      String var2;
      if (jarNames == null) {
         String[] var1 = new String[11];
         var2 = File.separator;
         byte var3 = 0;
         int var6 = var3 + 1;
         var1[var3] = var2 + "rt.jar";
         var1[var6++] = var2 + "jsse.jar";
         var1[var6++] = var2 + "jce.jar";
         var1[var6++] = var2 + "charsets.jar";
         var1[var6++] = var2 + "dnsns.jar";
         var1[var6++] = var2 + "zipfs.jar";
         var1[var6++] = var2 + "localedata.jar";
         int var8 = var6++;
         var2 = "cldrdata.jar";
         var1[var8] = "cldrdata.jar";
         var1[var6++] = var2 + "sunjce_provider.jar";
         var1[var6++] = var2 + "sunpkcs11.jar";
         var1[var6++] = var2 + "sunec.jar";
         jarNames = var1;
      }

      String var5 = this.getName();
      var2 = javaHome;
      if (var5.startsWith(var2)) {
         String[] var7 = jarNames;

         for(int var4 = 0; var4 < var7.length; ++var4) {
            if (var5.endsWith(var7[var4])) {
               return true;
            }
         }
      }

      return false;
   }

   private synchronized void ensureInitialization() {
      try {
         this.maybeInstantiateVerifier();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }

      if (this.jv != null && !this.jvInitialized) {
         this.initializeVerifier();
         this.jvInitialized = true;
      }

   }

   JarEntry newEntry(ZipEntry var1) {
      return new JarFile.JarFileEntry(var1);
   }

   Enumeration<String> entryNames(CodeSource[] var1) {
      this.ensureInitialization();
      if (this.jv != null) {
         return this.jv.entryNames(this, var1);
      } else {
         boolean var2 = false;

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3].getCodeSigners() == null) {
               var2 = true;
               break;
            }
         }

         return var2 ? this.unsignedEntryNames() : new Enumeration<String>() {
            public boolean hasMoreElements() {
               return false;
            }

            public String nextElement() {
               throw new NoSuchElementException();
            }
         };
      }
   }

   Enumeration<JarEntry> entries2() {
      this.ensureInitialization();
      if (this.jv != null) {
         return this.jv.entries2(this, super.entries());
      } else {
         final Enumeration var1 = super.entries();
         return new Enumeration<JarEntry>() {
            ZipEntry entry;

            public boolean hasMoreElements() {
               if (this.entry != null) {
                  return true;
               } else {
                  ZipEntry var1x;
                  do {
                     if (!var1.hasMoreElements()) {
                        return false;
                     }

                     var1x = (ZipEntry)var1.nextElement();
                  } while(JarVerifier.isSigningRelated(var1x.getName()));

                  this.entry = var1x;
                  return true;
               }
            }

            public JarFile.JarFileEntry nextElement() {
               if (this.hasMoreElements()) {
                  ZipEntry var1x = this.entry;
                  this.entry = null;
                  return JarFile.this.new JarFileEntry(var1x);
               } else {
                  throw new NoSuchElementException();
               }
            }
         };
      }
   }

   CodeSource[] getCodeSources(URL var1) {
      this.ensureInitialization();
      if (this.jv != null) {
         return this.jv.getCodeSources(this, var1);
      } else {
         Enumeration var2 = this.unsignedEntryNames();
         return var2.hasMoreElements() ? new CodeSource[]{JarVerifier.getUnsignedCS(var1)} : null;
      }
   }

   private Enumeration<String> unsignedEntryNames() {
      final Enumeration var1 = this.entries();
      return new Enumeration<String>() {
         String name;

         public boolean hasMoreElements() {
            if (this.name != null) {
               return true;
            } else {
               String var1x;
               ZipEntry var2;
               do {
                  if (!var1.hasMoreElements()) {
                     return false;
                  }

                  var2 = (ZipEntry)var1.nextElement();
                  var1x = var2.getName();
               } while(var2.isDirectory() || JarVerifier.isSigningRelated(var1x));

               this.name = var1x;
               return true;
            }
         }

         public String nextElement() {
            if (this.hasMoreElements()) {
               String var1x = this.name;
               this.name = null;
               return var1x;
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   CodeSource getCodeSource(URL var1, String var2) {
      this.ensureInitialization();
      if (this.jv != null) {
         if (this.jv.eagerValidation) {
            CodeSource var3 = null;
            JarEntry var4 = this.getJarEntry(var2);
            if (var4 != null) {
               var3 = this.jv.getCodeSource(var1, this, var4);
            } else {
               var3 = this.jv.getCodeSource(var1, var2);
            }

            return var3;
         } else {
            return this.jv.getCodeSource(var1, var2);
         }
      } else {
         return JarVerifier.getUnsignedCS(var1);
      }
   }

   void setEagerValidation(boolean var1) {
      try {
         this.maybeInstantiateVerifier();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }

      if (this.jv != null) {
         this.jv.setEagerValidation(var1);
      }

   }

   List<Object> getManifestDigests() {
      this.ensureInitialization();
      return (List)(this.jv != null ? this.jv.getManifestDigests() : new ArrayList());
   }

   static {
      SharedSecrets.setJavaUtilJarAccess(new JavaUtilJarAccessImpl());
      CLASSPATH_CHARS = new char[]{'c', 'l', 'a', 's', 's', '-', 'p', 'a', 't', 'h'};
      CLASSPATH_LASTOCC = new int[128];
      CLASSPATH_OPTOSFT = new int[10];
      CLASSPATH_LASTOCC[99] = 1;
      CLASSPATH_LASTOCC[108] = 2;
      CLASSPATH_LASTOCC[115] = 5;
      CLASSPATH_LASTOCC[45] = 6;
      CLASSPATH_LASTOCC[112] = 7;
      CLASSPATH_LASTOCC[97] = 8;
      CLASSPATH_LASTOCC[116] = 9;
      CLASSPATH_LASTOCC[104] = 10;

      for(int var0 = 0; var0 < 9; ++var0) {
         CLASSPATH_OPTOSFT[var0] = 10;
      }

      CLASSPATH_OPTOSFT[9] = 1;
   }

   private class JarFileEntry extends JarEntry {
      JarFileEntry(ZipEntry var2) {
         super(var2);
      }

      public Attributes getAttributes() throws IOException {
         Manifest var1 = JarFile.this.getManifest();
         return var1 != null ? var1.getAttributes(this.getName()) : null;
      }

      public Certificate[] getCertificates() {
         try {
            JarFile.this.maybeInstantiateVerifier();
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }

         if (this.certs == null && JarFile.this.jv != null) {
            this.certs = JarFile.this.jv.getCerts(JarFile.this, this);
         }

         return this.certs == null ? null : (Certificate[])this.certs.clone();
      }

      public CodeSigner[] getCodeSigners() {
         try {
            JarFile.this.maybeInstantiateVerifier();
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }

         if (this.signers == null && JarFile.this.jv != null) {
            this.signers = JarFile.this.jv.getCodeSigners(JarFile.this, this);
         }

         return this.signers == null ? null : (CodeSigner[])this.signers.clone();
      }
   }

   private class JarEntryIterator implements Enumeration<JarEntry>, Iterator<JarEntry> {
      final Enumeration<? extends ZipEntry> e;

      private JarEntryIterator() {
         this.e = JarFile.super.entries();
      }

      public boolean hasNext() {
         return this.e.hasMoreElements();
      }

      public JarEntry next() {
         ZipEntry var1 = (ZipEntry)this.e.nextElement();
         return JarFile.this.new JarFileEntry(var1);
      }

      public boolean hasMoreElements() {
         return this.hasNext();
      }

      public JarEntry nextElement() {
         return this.next();
      }

      // $FF: synthetic method
      JarEntryIterator(Object var2) {
         this();
      }
   }
}
