package sun.net.www.protocol.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.security.Permission;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import sun.net.ProgressMonitor;
import sun.net.ProgressSource;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;

public class FileURLConnection extends URLConnection {
   static String CONTENT_LENGTH = "content-length";
   static String CONTENT_TYPE = "content-type";
   static String TEXT_PLAIN = "text/plain";
   static String LAST_MODIFIED = "last-modified";
   String contentType;
   InputStream is;
   File file;
   String filename;
   boolean isDirectory = false;
   boolean exists = false;
   List<String> files;
   long length = -1L;
   long lastModified = 0L;
   private boolean initializedHeaders = false;
   Permission permission;

   protected FileURLConnection(URL var1, File var2) {
      super(var1);
      this.file = var2;
   }

   public void connect() throws IOException {
      if (!this.connected) {
         try {
            this.filename = this.file.toString();
            this.isDirectory = this.file.isDirectory();
            if (this.isDirectory) {
               String[] var1 = this.file.list();
               if (var1 == null) {
                  throw new FileNotFoundException(this.filename + " exists, but is not accessible");
               }

               this.files = Arrays.asList(var1);
            } else {
               this.is = new BufferedInputStream(new FileInputStream(this.filename));
               boolean var4 = ProgressMonitor.getDefault().shouldMeterInput(this.url, "GET");
               if (var4) {
                  ProgressSource var2 = new ProgressSource(this.url, "GET", this.file.length());
                  this.is = new MeteredStream(this.is, var2, this.file.length());
               }
            }
         } catch (IOException var3) {
            throw var3;
         }

         this.connected = true;
      }

   }

   private void initializeHeaders() {
      try {
         this.connect();
         this.exists = this.file.exists();
      } catch (IOException var4) {
      }

      if (!this.initializedHeaders || !this.exists) {
         this.length = this.file.length();
         this.lastModified = this.file.lastModified();
         if (!this.isDirectory) {
            FileNameMap var1 = java.net.URLConnection.getFileNameMap();
            this.contentType = var1.getContentTypeFor(this.filename);
            if (this.contentType != null) {
               this.properties.add(CONTENT_TYPE, this.contentType);
            }

            this.properties.add(CONTENT_LENGTH, String.valueOf(this.length));
            if (this.lastModified != 0L) {
               Date var2 = new Date(this.lastModified);
               SimpleDateFormat var3 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
               var3.setTimeZone(TimeZone.getTimeZone("GMT"));
               this.properties.add(LAST_MODIFIED, var3.format(var2));
            }
         } else {
            this.properties.add(CONTENT_TYPE, TEXT_PLAIN);
         }

         this.initializedHeaders = true;
      }

   }

   public String getHeaderField(String var1) {
      this.initializeHeaders();
      return super.getHeaderField(var1);
   }

   public String getHeaderField(int var1) {
      this.initializeHeaders();
      return super.getHeaderField(var1);
   }

   public int getContentLength() {
      this.initializeHeaders();
      return this.length > 2147483647L ? -1 : (int)this.length;
   }

   public long getContentLengthLong() {
      this.initializeHeaders();
      return this.length;
   }

   public String getHeaderFieldKey(int var1) {
      this.initializeHeaders();
      return super.getHeaderFieldKey(var1);
   }

   public MessageHeader getProperties() {
      this.initializeHeaders();
      return super.getProperties();
   }

   public long getLastModified() {
      this.initializeHeaders();
      return this.lastModified;
   }

   public synchronized InputStream getInputStream() throws IOException {
      this.connect();
      if (this.is == null) {
         if (!this.isDirectory) {
            throw new FileNotFoundException(this.filename);
         }

         FileNameMap var3 = java.net.URLConnection.getFileNameMap();
         StringBuffer var4 = new StringBuffer();
         if (this.files == null) {
            throw new FileNotFoundException(this.filename);
         }

         Collections.sort(this.files, Collator.getInstance());

         for(int var5 = 0; var5 < this.files.size(); ++var5) {
            String var6 = (String)this.files.get(var5);
            var4.append(var6);
            var4.append("\n");
         }

         this.is = new ByteArrayInputStream(var4.toString().getBytes());
      }

      return this.is;
   }

   public Permission getPermission() throws IOException {
      if (this.permission == null) {
         String var1 = ParseUtil.decode(this.url.getPath());
         if (File.separatorChar == '/') {
            this.permission = new FilePermission(var1, "read");
         } else {
            this.permission = new FilePermission(var1.replace('/', File.separatorChar), "read");
         }
      }

      return this.permission;
   }
}
