package java.util.jar;

import java.io.IOException;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.zip.ZipEntry;

public class JarEntry extends ZipEntry {
   Attributes attr;
   Certificate[] certs;
   CodeSigner[] signers;

   public JarEntry(String var1) {
      super(var1);
   }

   public JarEntry(ZipEntry var1) {
      super(var1);
   }

   public JarEntry(JarEntry var1) {
      this((ZipEntry)var1);
      this.attr = var1.attr;
      this.certs = var1.certs;
      this.signers = var1.signers;
   }

   public Attributes getAttributes() throws IOException {
      return this.attr;
   }

   public Certificate[] getCertificates() {
      return this.certs == null ? null : (Certificate[])this.certs.clone();
   }

   public CodeSigner[] getCodeSigners() {
      return this.signers == null ? null : (CodeSigner[])this.signers.clone();
   }
}
