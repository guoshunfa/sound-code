package sun.net.www.protocol.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import sun.net.www.ParseUtil;

public class URLJarFile extends JarFile {
   private static URLJarFileCallBack callback = null;
   private URLJarFile.URLJarFileCloseController closeController;
   private static int BUF_SIZE = 2048;
   private Manifest superMan;
   private Attributes superAttr;
   private Map<String, Attributes> superEntries;

   static JarFile getJarFile(URL var0) throws IOException {
      return getJarFile(var0, (URLJarFile.URLJarFileCloseController)null);
   }

   static JarFile getJarFile(URL var0, URLJarFile.URLJarFileCloseController var1) throws IOException {
      return (JarFile)(isFileURL(var0) ? new URLJarFile(var0, var1) : retrieve(var0, var1));
   }

   public URLJarFile(File var1) throws IOException {
      this((File)var1, (URLJarFile.URLJarFileCloseController)null);
   }

   public URLJarFile(File var1, URLJarFile.URLJarFileCloseController var2) throws IOException {
      super(var1, true, 5);
      this.closeController = null;
      this.closeController = var2;
   }

   private URLJarFile(URL var1, URLJarFile.URLJarFileCloseController var2) throws IOException {
      super(ParseUtil.decode(var1.getFile()));
      this.closeController = null;
      this.closeController = var2;
   }

   private static boolean isFileURL(URL var0) {
      if (var0.getProtocol().equalsIgnoreCase("file")) {
         String var1 = var0.getHost();
         if (var1 == null || var1.equals("") || var1.equals("~") || var1.equalsIgnoreCase("localhost")) {
            return true;
         }
      }

      return false;
   }

   protected void finalize() throws IOException {
      this.close();
   }

   public ZipEntry getEntry(String var1) {
      ZipEntry var2 = super.getEntry(var1);
      if (var2 != null) {
         if (var2 instanceof JarEntry) {
            return new URLJarFile.URLJarFileEntry((JarEntry)var2);
         } else {
            throw new InternalError(super.getClass() + " returned unexpected entry type " + var2.getClass());
         }
      } else {
         return null;
      }
   }

   public Manifest getManifest() throws IOException {
      if (!this.isSuperMan()) {
         return null;
      } else {
         Manifest var1 = new Manifest();
         Attributes var2 = var1.getMainAttributes();
         var2.putAll((Map)this.superAttr.clone());
         if (this.superEntries != null) {
            Map var3 = var1.getEntries();
            Iterator var4 = this.superEntries.keySet().iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               Attributes var6 = (Attributes)this.superEntries.get(var5);
               var3.put(var5, (Attributes)var6.clone());
            }
         }

         return var1;
      }
   }

   public void close() throws IOException {
      if (this.closeController != null) {
         this.closeController.close(this);
      }

      super.close();
   }

   private synchronized boolean isSuperMan() throws IOException {
      if (this.superMan == null) {
         this.superMan = super.getManifest();
      }

      if (this.superMan != null) {
         this.superAttr = this.superMan.getMainAttributes();
         this.superEntries = this.superMan.getEntries();
         return true;
      } else {
         return false;
      }
   }

   private static JarFile retrieve(URL var0) throws IOException {
      return retrieve(var0, (URLJarFile.URLJarFileCloseController)null);
   }

   private static JarFile retrieve(URL var0, final URLJarFile.URLJarFileCloseController var1) throws IOException {
      if (callback != null) {
         return callback.retrieve(var0);
      } else {
         JarFile var2 = null;

         try {
            final InputStream var3 = var0.openConnection().getInputStream();
            Throwable var4 = null;

            try {
               var2 = (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction<JarFile>() {
                  public JarFile run() throws IOException {
                     Path var1x = Files.createTempFile("jar_cache", (String)null);

                     try {
                        Files.copy(var3, var1x, StandardCopyOption.REPLACE_EXISTING);
                        URLJarFile var2 = new URLJarFile(var1x.toFile(), var1);
                        var1x.toFile().deleteOnExit();
                        return var2;
                     } catch (Throwable var5) {
                        try {
                           Files.delete(var1x);
                        } catch (IOException var4) {
                           var5.addSuppressed(var4);
                        }

                        throw var5;
                     }
                  }
               });
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     var3.close();
                  }
               }

            }

            return var2;
         } catch (PrivilegedActionException var16) {
            throw (IOException)var16.getException();
         }
      }
   }

   public static void setCallBack(URLJarFileCallBack var0) {
      callback = var0;
   }

   public interface URLJarFileCloseController {
      void close(JarFile var1);
   }

   private class URLJarFileEntry extends JarEntry {
      private JarEntry je;

      URLJarFileEntry(JarEntry var2) {
         super(var2);
         this.je = var2;
      }

      public Attributes getAttributes() throws IOException {
         if (URLJarFile.this.isSuperMan()) {
            Map var1 = URLJarFile.this.superEntries;
            if (var1 != null) {
               Attributes var2 = (Attributes)var1.get(this.getName());
               if (var2 != null) {
                  return (Attributes)var2.clone();
               }
            }
         }

         return null;
      }

      public Certificate[] getCertificates() {
         Certificate[] var1 = this.je.getCertificates();
         return var1 == null ? null : (Certificate[])var1.clone();
      }

      public CodeSigner[] getCodeSigners() {
         CodeSigner[] var1 = this.je.getCodeSigners();
         return var1 == null ? null : (CodeSigner[])var1.clone();
      }
   }
}
