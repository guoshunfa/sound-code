package java.util.jar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import sun.security.util.ManifestEntryVerifier;

public class JarInputStream extends ZipInputStream {
   private Manifest man;
   private JarEntry first;
   private JarVerifier jv;
   private ManifestEntryVerifier mev;
   private final boolean doVerify;
   private boolean tryManifest;

   public JarInputStream(InputStream var1) throws IOException {
      this(var1, true);
   }

   public JarInputStream(InputStream var1, boolean var2) throws IOException {
      super(var1);
      this.doVerify = var2;
      JarEntry var3 = (JarEntry)super.getNextEntry();
      if (var3 != null && var3.getName().equalsIgnoreCase("META-INF/")) {
         var3 = (JarEntry)super.getNextEntry();
      }

      this.first = this.checkManifest(var3);
   }

   private JarEntry checkManifest(JarEntry var1) throws IOException {
      if (var1 != null && "META-INF/MANIFEST.MF".equalsIgnoreCase(var1.getName())) {
         this.man = new Manifest();
         byte[] var2 = this.getBytes(new BufferedInputStream(this));
         this.man.read(new ByteArrayInputStream(var2));
         this.closeEntry();
         if (this.doVerify) {
            this.jv = new JarVerifier(var2);
            this.mev = new ManifestEntryVerifier(this.man);
         }

         return (JarEntry)super.getNextEntry();
      } else {
         return var1;
      }
   }

   private byte[] getBytes(InputStream var1) throws IOException {
      byte[] var2 = new byte[8192];
      ByteArrayOutputStream var3 = new ByteArrayOutputStream(2048);

      int var4;
      while((var4 = var1.read(var2, 0, var2.length)) != -1) {
         var3.write(var2, 0, var4);
      }

      return var3.toByteArray();
   }

   public Manifest getManifest() {
      return this.man;
   }

   public ZipEntry getNextEntry() throws IOException {
      JarEntry var1;
      if (this.first == null) {
         var1 = (JarEntry)super.getNextEntry();
         if (this.tryManifest) {
            var1 = this.checkManifest(var1);
            this.tryManifest = false;
         }
      } else {
         var1 = this.first;
         if (this.first.getName().equalsIgnoreCase("META-INF/INDEX.LIST")) {
            this.tryManifest = true;
         }

         this.first = null;
      }

      if (this.jv != null && var1 != null) {
         if (this.jv.nothingToVerify()) {
            this.jv = null;
            this.mev = null;
         } else {
            this.jv.beginEntry(var1, this.mev);
         }
      }

      return var1;
   }

   public JarEntry getNextJarEntry() throws IOException {
      return (JarEntry)this.getNextEntry();
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4;
      if (this.first == null) {
         var4 = super.read(var1, var2, var3);
      } else {
         var4 = -1;
      }

      if (this.jv != null) {
         this.jv.update(var4, var1, var2, var3, this.mev);
      }

      return var4;
   }

   protected ZipEntry createZipEntry(String var1) {
      JarEntry var2 = new JarEntry(var1);
      if (this.man != null) {
         var2.attr = this.man.getAttributes(var1);
      }

      return var2;
   }
}
