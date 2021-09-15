package sun.net.www.protocol.jar;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarURLConnection extends java.net.JarURLConnection {
   private static final boolean debug = false;
   private static final JarFileFactory factory = JarFileFactory.getInstance();
   private URL jarFileURL = this.getJarFileURL();
   private Permission permission;
   private URLConnection jarFileURLConnection;
   private String entryName;
   private JarEntry jarEntry;
   private JarFile jarFile;
   private String contentType;

   public JarURLConnection(URL var1, Handler var2) throws MalformedURLException, IOException {
      super(var1);
      this.jarFileURLConnection = this.jarFileURL.openConnection();
      this.entryName = this.getEntryName();
   }

   public JarFile getJarFile() throws IOException {
      this.connect();
      return this.jarFile;
   }

   public JarEntry getJarEntry() throws IOException {
      this.connect();
      return this.jarEntry;
   }

   public Permission getPermission() throws IOException {
      return this.jarFileURLConnection.getPermission();
   }

   public void connect() throws IOException {
      if (!this.connected) {
         this.jarFile = factory.get(this.getJarFileURL(), this.getUseCaches());
         if (this.getUseCaches()) {
            boolean var1 = this.jarFileURLConnection.getUseCaches();
            this.jarFileURLConnection = factory.getConnection(this.jarFile);
            this.jarFileURLConnection.setUseCaches(var1);
         }

         if (this.entryName != null) {
            this.jarEntry = (JarEntry)this.jarFile.getEntry(this.entryName);
            if (this.jarEntry == null) {
               try {
                  if (!this.getUseCaches()) {
                     this.jarFile.close();
                  }
               } catch (Exception var2) {
               }

               throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
            }
         }

         this.connected = true;
      }

   }

   public InputStream getInputStream() throws IOException {
      this.connect();
      JarURLConnection.JarURLInputStream var1 = null;
      if (this.entryName == null) {
         throw new IOException("no entry name specified");
      } else if (this.jarEntry == null) {
         throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
      } else {
         var1 = new JarURLConnection.JarURLInputStream(this.jarFile.getInputStream(this.jarEntry));
         return var1;
      }
   }

   public int getContentLength() {
      long var1 = this.getContentLengthLong();
      return var1 > 2147483647L ? -1 : (int)var1;
   }

   public long getContentLengthLong() {
      long var1 = -1L;

      try {
         this.connect();
         if (this.jarEntry == null) {
            var1 = this.jarFileURLConnection.getContentLengthLong();
         } else {
            var1 = this.getJarEntry().getSize();
         }
      } catch (IOException var4) {
      }

      return var1;
   }

   public Object getContent() throws IOException {
      Object var1 = null;
      this.connect();
      if (this.entryName == null) {
         var1 = this.jarFile;
      } else {
         var1 = super.getContent();
      }

      return var1;
   }

   public String getContentType() {
      if (this.contentType == null) {
         if (this.entryName == null) {
            this.contentType = "x-java/jar";
         } else {
            try {
               this.connect();
               InputStream var1 = this.jarFile.getInputStream(this.jarEntry);
               this.contentType = guessContentTypeFromStream(new BufferedInputStream(var1));
               var1.close();
            } catch (IOException var2) {
            }
         }

         if (this.contentType == null) {
            this.contentType = guessContentTypeFromName(this.entryName);
         }

         if (this.contentType == null) {
            this.contentType = "content/unknown";
         }
      }

      return this.contentType;
   }

   public String getHeaderField(String var1) {
      return this.jarFileURLConnection.getHeaderField(var1);
   }

   public void setRequestProperty(String var1, String var2) {
      this.jarFileURLConnection.setRequestProperty(var1, var2);
   }

   public String getRequestProperty(String var1) {
      return this.jarFileURLConnection.getRequestProperty(var1);
   }

   public void addRequestProperty(String var1, String var2) {
      this.jarFileURLConnection.addRequestProperty(var1, var2);
   }

   public Map<String, List<String>> getRequestProperties() {
      return this.jarFileURLConnection.getRequestProperties();
   }

   public void setAllowUserInteraction(boolean var1) {
      this.jarFileURLConnection.setAllowUserInteraction(var1);
   }

   public boolean getAllowUserInteraction() {
      return this.jarFileURLConnection.getAllowUserInteraction();
   }

   public void setUseCaches(boolean var1) {
      this.jarFileURLConnection.setUseCaches(var1);
   }

   public boolean getUseCaches() {
      return this.jarFileURLConnection.getUseCaches();
   }

   public void setIfModifiedSince(long var1) {
      this.jarFileURLConnection.setIfModifiedSince(var1);
   }

   public void setDefaultUseCaches(boolean var1) {
      this.jarFileURLConnection.setDefaultUseCaches(var1);
   }

   public boolean getDefaultUseCaches() {
      return this.jarFileURLConnection.getDefaultUseCaches();
   }

   class JarURLInputStream extends FilterInputStream {
      JarURLInputStream(InputStream var2) {
         super(var2);
      }

      public void close() throws IOException {
         try {
            super.close();
         } finally {
            if (!JarURLConnection.this.getUseCaches()) {
               JarURLConnection.this.jarFile.close();
            }

         }

      }
   }
}
