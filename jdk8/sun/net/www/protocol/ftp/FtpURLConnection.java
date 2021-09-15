package sun.net.www.protocol.ftp;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.StringTokenizer;
import sun.net.ProgressMonitor;
import sun.net.ProgressSource;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpLoginException;
import sun.net.ftp.FtpProtocolException;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;

public class FtpURLConnection extends URLConnection {
   HttpURLConnection http;
   private Proxy instProxy;
   InputStream is;
   OutputStream os;
   FtpClient ftp;
   Permission permission;
   String password;
   String user;
   String host;
   String pathname;
   String filename;
   String fullpath;
   int port;
   static final int NONE = 0;
   static final int ASCII = 1;
   static final int BIN = 2;
   static final int DIR = 3;
   int type;
   private int connectTimeout;
   private int readTimeout;

   public FtpURLConnection(URL var1) {
      this(var1, (Proxy)null);
   }

   FtpURLConnection(URL var1, Proxy var2) {
      super(var1);
      this.http = null;
      this.is = null;
      this.os = null;
      this.ftp = null;
      this.type = 0;
      this.connectTimeout = -1;
      this.readTimeout = -1;
      this.instProxy = var2;
      this.host = var1.getHost();
      this.port = var1.getPort();
      String var3 = var1.getUserInfo();
      if (var3 != null) {
         int var4 = var3.indexOf(58);
         if (var4 == -1) {
            this.user = ParseUtil.decode(var3);
            this.password = null;
         } else {
            this.user = ParseUtil.decode(var3.substring(0, var4++));
            this.password = ParseUtil.decode(var3.substring(var4));
         }
      }

   }

   private void setTimeouts() {
      if (this.ftp != null) {
         if (this.connectTimeout >= 0) {
            this.ftp.setConnectTimeout(this.connectTimeout);
         }

         if (this.readTimeout >= 0) {
            this.ftp.setReadTimeout(this.readTimeout);
         }
      }

   }

   public synchronized void connect() throws IOException {
      if (!this.connected) {
         Proxy var1 = null;
         if (this.instProxy != null) {
            var1 = this.instProxy;
            if (var1.type() == Proxy.Type.HTTP) {
               this.http = new HttpURLConnection(this.url, this.instProxy);
               this.http.setDoInput(this.getDoInput());
               this.http.setDoOutput(this.getDoOutput());
               if (this.connectTimeout >= 0) {
                  this.http.setConnectTimeout(this.connectTimeout);
               }

               if (this.readTimeout >= 0) {
                  this.http.setReadTimeout(this.readTimeout);
               }

               this.http.connect();
               this.connected = true;
               return;
            }
         } else {
            ProxySelector var2 = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
               public ProxySelector run() {
                  return ProxySelector.getDefault();
               }
            });
            if (var2 != null) {
               URI var3 = ParseUtil.toURI(this.url);
               Iterator var4 = var2.select(var3).iterator();

               label110:
               while(true) {
                  while(true) {
                     if (!var4.hasNext()) {
                        break label110;
                     }

                     var1 = (Proxy)var4.next();
                     if (var1 == null || var1 == Proxy.NO_PROXY || var1.type() == Proxy.Type.SOCKS) {
                        break label110;
                     }

                     if (var1.type() == Proxy.Type.HTTP && var1.address() instanceof InetSocketAddress) {
                        InetSocketAddress var5 = (InetSocketAddress)var1.address();

                        try {
                           this.http = new HttpURLConnection(this.url, var1);
                           this.http.setDoInput(this.getDoInput());
                           this.http.setDoOutput(this.getDoOutput());
                           if (this.connectTimeout >= 0) {
                              this.http.setConnectTimeout(this.connectTimeout);
                           }

                           if (this.readTimeout >= 0) {
                              this.http.setReadTimeout(this.readTimeout);
                           }

                           this.http.connect();
                           this.connected = true;
                           return;
                        } catch (IOException var11) {
                           var2.connectFailed(var3, var5, var11);
                           this.http = null;
                        }
                     } else {
                        var2.connectFailed(var3, var1.address(), new IOException("Wrong proxy type"));
                     }
                  }
               }
            }
         }

         if (this.user == null) {
            this.user = "anonymous";
            String var12 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.version")));
            this.password = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("ftp.protocol.user", "Java" + var12 + "@")));
         }

         try {
            this.ftp = FtpClient.create();
            if (var1 != null) {
               this.ftp.setProxy(var1);
            }

            this.setTimeouts();
            if (this.port != -1) {
               this.ftp.connect(new InetSocketAddress(this.host, this.port));
            } else {
               this.ftp.connect(new InetSocketAddress(this.host, FtpClient.defaultPort()));
            }
         } catch (UnknownHostException var9) {
            throw var9;
         } catch (FtpProtocolException var10) {
            if (this.ftp != null) {
               try {
                  this.ftp.close();
               } catch (IOException var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw new IOException(var10);
         }

         try {
            this.ftp.login(this.user, this.password == null ? null : this.password.toCharArray());
         } catch (FtpProtocolException var8) {
            this.ftp.close();
            throw new FtpLoginException("Invalid username/password");
         }

         this.connected = true;
      }
   }

   private void decodePath(String var1) {
      int var2 = var1.indexOf(";type=");
      if (var2 >= 0) {
         String var3 = var1.substring(var2 + 6, var1.length());
         if ("i".equalsIgnoreCase(var3)) {
            this.type = 2;
         }

         if ("a".equalsIgnoreCase(var3)) {
            this.type = 1;
         }

         if ("d".equalsIgnoreCase(var3)) {
            this.type = 3;
         }

         var1 = var1.substring(0, var2);
      }

      if (var1 != null && var1.length() > 1 && var1.charAt(0) == '/') {
         var1 = var1.substring(1);
      }

      if (var1 == null || var1.length() == 0) {
         var1 = "./";
      }

      if (!var1.endsWith("/")) {
         var2 = var1.lastIndexOf(47);
         if (var2 > 0) {
            this.filename = var1.substring(var2 + 1, var1.length());
            this.filename = ParseUtil.decode(this.filename);
            this.pathname = var1.substring(0, var2);
         } else {
            this.filename = ParseUtil.decode(var1);
            this.pathname = null;
         }
      } else {
         this.pathname = var1.substring(0, var1.length() - 1);
         this.filename = null;
      }

      if (this.pathname != null) {
         this.fullpath = this.pathname + "/" + (this.filename != null ? this.filename : "");
      } else {
         this.fullpath = this.filename;
      }

   }

   private void cd(String var1) throws FtpProtocolException, IOException {
      if (var1 != null && !var1.isEmpty()) {
         if (var1.indexOf(47) == -1) {
            this.ftp.changeDirectory(ParseUtil.decode(var1));
         } else {
            StringTokenizer var2 = new StringTokenizer(var1, "/");

            while(var2.hasMoreTokens()) {
               this.ftp.changeDirectory(ParseUtil.decode(var2.nextToken()));
            }

         }
      }
   }

   public InputStream getInputStream() throws IOException {
      if (!this.connected) {
         this.connect();
      }

      if (this.http != null) {
         return this.http.getInputStream();
      } else if (this.os != null) {
         throw new IOException("Already opened for output");
      } else if (this.is != null) {
         return this.is;
      } else {
         MessageHeader var1 = new MessageHeader();
         boolean var2 = false;

         try {
            this.decodePath(this.url.getPath());
            if (this.filename != null && this.type != 3) {
               if (this.type == 1) {
                  this.ftp.setAsciiType();
               } else {
                  this.ftp.setBinaryType();
               }

               this.cd(this.pathname);
               this.is = new FtpURLConnection.FtpInputStream(this.ftp, this.ftp.getFileStream(this.filename));
            } else {
               this.ftp.setAsciiType();
               this.cd(this.pathname);
               if (this.filename == null) {
                  this.is = new FtpURLConnection.FtpInputStream(this.ftp, this.ftp.list((String)null));
               } else {
                  this.is = new FtpURLConnection.FtpInputStream(this.ftp, this.ftp.nameList(this.filename));
               }
            }

            try {
               long var3 = this.ftp.getLastTransferSize();
               var1.add("content-length", Long.toString(var3));
               if (var3 > 0L) {
                  boolean var16 = ProgressMonitor.getDefault().shouldMeterInput(this.url, "GET");
                  ProgressSource var6 = null;
                  if (var16) {
                     var6 = new ProgressSource(this.url, "GET", var3);
                     var6.beginTracking();
                  }

                  this.is = new MeteredStream(this.is, var6, var3);
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }

            if (var2) {
               var1.add("content-type", "text/plain");
               var1.add("access-type", "directory");
            } else {
               var1.add("access-type", "file");
               String var15 = guessContentTypeFromName(this.fullpath);
               if (var15 == null && this.is.markSupported()) {
                  var15 = guessContentTypeFromStream(this.is);
               }

               if (var15 != null) {
                  var1.add("content-type", var15);
               }
            }
         } catch (FileNotFoundException var13) {
            FileNotFoundException var5;
            try {
               this.cd(this.fullpath);
               this.ftp.setAsciiType();
               this.is = new FtpURLConnection.FtpInputStream(this.ftp, this.ftp.list((String)null));
               var1.add("content-type", "text/plain");
               var1.add("access-type", "directory");
            } catch (IOException var11) {
               var5 = new FileNotFoundException(this.fullpath);
               if (this.ftp != null) {
                  try {
                     this.ftp.close();
                  } catch (IOException var9) {
                     var5.addSuppressed(var9);
                  }
               }

               throw var5;
            } catch (FtpProtocolException var12) {
               var5 = new FileNotFoundException(this.fullpath);
               if (this.ftp != null) {
                  try {
                     this.ftp.close();
                  } catch (IOException var8) {
                     var5.addSuppressed(var8);
                  }
               }

               throw var5;
            }
         } catch (FtpProtocolException var14) {
            if (this.ftp != null) {
               try {
                  this.ftp.close();
               } catch (IOException var7) {
                  var14.addSuppressed(var7);
               }
            }

            throw new IOException(var14);
         }

         this.setProperties(var1);
         return this.is;
      }
   }

   public OutputStream getOutputStream() throws IOException {
      if (!this.connected) {
         this.connect();
      }

      if (this.http != null) {
         OutputStream var1 = this.http.getOutputStream();
         this.http.getInputStream();
         return var1;
      } else if (this.is != null) {
         throw new IOException("Already opened for input");
      } else if (this.os != null) {
         return this.os;
      } else {
         this.decodePath(this.url.getPath());
         if (this.filename != null && this.filename.length() != 0) {
            try {
               if (this.pathname != null) {
                  this.cd(this.pathname);
               }

               if (this.type == 1) {
                  this.ftp.setAsciiType();
               } else {
                  this.ftp.setBinaryType();
               }

               this.os = new FtpURLConnection.FtpOutputStream(this.ftp, this.ftp.putFileStream(this.filename, false));
            } catch (FtpProtocolException var2) {
               throw new IOException(var2);
            }

            return this.os;
         } else {
            throw new IOException("illegal filename for a PUT");
         }
      }
   }

   String guessContentTypeFromFilename(String var1) {
      return guessContentTypeFromName(var1);
   }

   public Permission getPermission() {
      if (this.permission == null) {
         int var1 = this.url.getPort();
         var1 = var1 < 0 ? FtpClient.defaultPort() : var1;
         String var2 = this.host + ":" + var1;
         this.permission = new SocketPermission(var2, "connect");
      }

      return this.permission;
   }

   public void setRequestProperty(String var1, String var2) {
      super.setRequestProperty(var1, var2);
      if ("type".equals(var1)) {
         if ("i".equalsIgnoreCase(var2)) {
            this.type = 2;
         } else if ("a".equalsIgnoreCase(var2)) {
            this.type = 1;
         } else {
            if (!"d".equalsIgnoreCase(var2)) {
               throw new IllegalArgumentException("Value of '" + var1 + "' request property was '" + var2 + "' when it must be either 'i', 'a' or 'd'");
            }

            this.type = 3;
         }
      }

   }

   public String getRequestProperty(String var1) {
      String var2 = super.getRequestProperty(var1);
      if (var2 == null && "type".equals(var1)) {
         var2 = this.type == 1 ? "a" : (this.type == 3 ? "d" : "i");
      }

      return var2;
   }

   public void setConnectTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeouts can't be negative");
      } else {
         this.connectTimeout = var1;
      }
   }

   public int getConnectTimeout() {
      return this.connectTimeout < 0 ? 0 : this.connectTimeout;
   }

   public void setReadTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeouts can't be negative");
      } else {
         this.readTimeout = var1;
      }
   }

   public int getReadTimeout() {
      return this.readTimeout < 0 ? 0 : this.readTimeout;
   }

   protected class FtpOutputStream extends FilterOutputStream {
      FtpClient ftp;

      FtpOutputStream(FtpClient var2, OutputStream var3) {
         super(var3);
         this.ftp = var2;
      }

      public void close() throws IOException {
         super.close();
         if (this.ftp != null) {
            this.ftp.close();
         }

      }
   }

   protected class FtpInputStream extends FilterInputStream {
      FtpClient ftp;

      FtpInputStream(FtpClient var2, InputStream var3) {
         super(new BufferedInputStream(var3));
         this.ftp = var2;
      }

      public void close() throws IOException {
         super.close();
         if (this.ftp != null) {
            this.ftp.close();
         }

      }
   }
}
