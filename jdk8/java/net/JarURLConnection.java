package java.net;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;

public abstract class JarURLConnection extends URLConnection {
   private URL jarFileURL;
   private String entryName;
   protected URLConnection jarFileURLConnection;

   protected JarURLConnection(URL var1) throws MalformedURLException {
      super(var1);
      this.parseSpecs(var1);
   }

   private void parseSpecs(URL var1) throws MalformedURLException {
      String var2 = var1.getFile();
      int var3 = var2.indexOf("!/");
      if (var3 == -1) {
         throw new MalformedURLException("no !/ found in url spec:" + var2);
      } else {
         this.jarFileURL = new URL(var2.substring(0, var3++));
         this.entryName = null;
         ++var3;
         if (var3 != var2.length()) {
            this.entryName = var2.substring(var3, var2.length());
            this.entryName = ParseUtil.decode(this.entryName);
         }

      }
   }

   public URL getJarFileURL() {
      return this.jarFileURL;
   }

   public String getEntryName() {
      return this.entryName;
   }

   public abstract JarFile getJarFile() throws IOException;

   public Manifest getManifest() throws IOException {
      return this.getJarFile().getManifest();
   }

   public JarEntry getJarEntry() throws IOException {
      return this.getJarFile().getJarEntry(this.entryName);
   }

   public Attributes getAttributes() throws IOException {
      JarEntry var1 = this.getJarEntry();
      return var1 != null ? var1.getAttributes() : null;
   }

   public Attributes getMainAttributes() throws IOException {
      Manifest var1 = this.getManifest();
      return var1 != null ? var1.getMainAttributes() : null;
   }

   public Certificate[] getCertificates() throws IOException {
      JarEntry var1 = this.getJarEntry();
      return var1 != null ? var1.getCertificates() : null;
   }
}
