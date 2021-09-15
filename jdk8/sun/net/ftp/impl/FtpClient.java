package sun.net.ftp.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpDirParser;
import sun.net.ftp.FtpProtocolException;
import sun.net.ftp.FtpReplyCode;
import sun.util.logging.PlatformLogger;

public class FtpClient extends sun.net.ftp.FtpClient {
   private static int defaultSoTimeout;
   private static int defaultConnectTimeout;
   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.net.ftp.FtpClient");
   private Proxy proxy;
   private Socket server;
   private PrintStream out;
   private InputStream in;
   private int readTimeout = -1;
   private int connectTimeout = -1;
   private static String encoding = "ISO8859_1";
   private InetSocketAddress serverAddr;
   private boolean replyPending = false;
   private boolean loggedIn = false;
   private boolean useCrypto = false;
   private SSLSocketFactory sslFact;
   private Socket oldSocket;
   private Vector<String> serverResponse = new Vector(1);
   private FtpReplyCode lastReplyCode = null;
   private String welcomeMsg;
   private final boolean passiveMode = true;
   private sun.net.ftp.FtpClient.TransferType type;
   private long restartOffset;
   private long lastTransSize;
   private String lastFileName;
   private static String[] patStrings = new String[]{"([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d\\d:\\d\\d)\\s*(\\p{Print}*)", "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d{4})\\s*(\\p{Print}*)", "(\\d{2}/\\d{2}/\\d{4})\\s*(\\d{2}:\\d{2}[ap])\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)", "(\\d{2}-\\d{2}-\\d{2})\\s*(\\d{2}:\\d{2}[AP]M)\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)"};
   private static int[][] patternGroups = new int[][]{{7, 4, 5, 6, 0, 1, 2, 3}, {7, 4, 5, 0, 6, 1, 2, 3}, {4, 3, 1, 2, 0, 0, 0, 0}, {4, 3, 1, 2, 0, 0, 0, 0}};
   private static Pattern[] patterns;
   private static Pattern linkp = Pattern.compile("(\\p{Print}+) \\-\\> (\\p{Print}+)$");
   private DateFormat df;
   private FtpDirParser parser;
   private FtpDirParser mlsxParser;
   private static Pattern transPat;
   private static Pattern epsvPat;
   private static Pattern pasvPat;
   private static String[] MDTMformats;
   private static SimpleDateFormat[] dateFormats;

   private static boolean isASCIISuperset(String var0) throws Exception {
      String var1 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,";
      byte[] var2 = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59, 47, 63, 58, 64, 38, 61, 43, 36, 44};
      byte[] var3 = var1.getBytes(var0);
      return Arrays.equals(var3, var2);
   }

   private void getTransferSize() {
      this.lastTransSize = -1L;
      String var1 = this.getLastResponseString();
      if (transPat == null) {
         transPat = Pattern.compile("150 Opening .*\\((\\d+) bytes\\).");
      }

      Matcher var2 = transPat.matcher(var1);
      if (var2.find()) {
         String var3 = var2.group(1);
         this.lastTransSize = Long.parseLong(var3);
      }

   }

   private void getTransferName() {
      this.lastFileName = null;
      String var1 = this.getLastResponseString();
      int var2 = var1.indexOf("unique file name:");
      int var3 = var1.lastIndexOf(41);
      if (var2 >= 0) {
         var2 += 17;
         this.lastFileName = var1.substring(var2, var3);
      }

   }

   private int readServerResponse() throws IOException {
      StringBuffer var1 = new StringBuffer(32);
      int var3 = -1;
      this.serverResponse.setSize(0);

      int var4;
      while(true) {
         String var5;
         while(true) {
            int var2;
            while((var2 = this.in.read()) != -1) {
               if (var2 == 13 && (var2 = this.in.read()) != 10) {
                  var1.append('\r');
               }

               var1.append((char)var2);
               if (var2 == 10) {
                  break;
               }
            }

            var5 = var1.toString();
            var1.setLength(0);
            if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
               logger.finest("Server [" + this.serverAddr + "] --> " + var5);
            }

            if (var5.length() == 0) {
               var4 = -1;
               break;
            }

            try {
               var4 = Integer.parseInt(var5.substring(0, 3));
               break;
            } catch (NumberFormatException var7) {
               var4 = -1;
               break;
            } catch (StringIndexOutOfBoundsException var8) {
            }
         }

         this.serverResponse.addElement(var5);
         if (var3 != -1) {
            if (var4 == var3 && (var5.length() < 4 || var5.charAt(3) != '-')) {
               boolean var9 = true;
               break;
            }
         } else {
            if (var5.length() < 4 || var5.charAt(3) != '-') {
               break;
            }

            var3 = var4;
         }
      }

      return var4;
   }

   private void sendServer(String var1) {
      this.out.print(var1);
      if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
         logger.finest("Server [" + this.serverAddr + "] <-- " + var1);
      }

   }

   private String getResponseString() {
      return (String)this.serverResponse.elementAt(0);
   }

   private Vector<String> getResponseStrings() {
      return this.serverResponse;
   }

   private boolean readReply() throws IOException {
      this.lastReplyCode = FtpReplyCode.find(this.readServerResponse());
      if (this.lastReplyCode.isPositivePreliminary()) {
         this.replyPending = true;
         return true;
      } else if (!this.lastReplyCode.isPositiveCompletion() && !this.lastReplyCode.isPositiveIntermediate()) {
         return false;
      } else {
         if (this.lastReplyCode == FtpReplyCode.CLOSING_DATA_CONNECTION) {
            this.getTransferName();
         }

         return true;
      }
   }

   private boolean issueCommand(String var1) throws IOException, FtpProtocolException {
      if (!this.isConnected()) {
         throw new IllegalStateException("Not connected");
      } else {
         if (this.replyPending) {
            try {
               this.completePending();
            } catch (FtpProtocolException var3) {
            }
         }

         if (var1.indexOf(10) != -1) {
            FtpProtocolException var2 = new FtpProtocolException("Illegal FTP command");
            var2.initCause(new IllegalArgumentException("Illegal carriage return"));
            throw var2;
         } else {
            this.sendServer(var1 + "\r\n");
            return this.readReply();
         }
      }
   }

   private void issueCommandCheck(String var1) throws FtpProtocolException, IOException {
      if (!this.issueCommand(var1)) {
         throw new FtpProtocolException(var1 + ":" + this.getResponseString(), this.getLastReplyCode());
      }
   }

   private Socket openPassiveDataConnection(String var1) throws FtpProtocolException, IOException {
      InetSocketAddress var4 = null;
      String var2;
      int var3;
      Matcher var5;
      String var6;
      if (this.issueCommand("EPSV ALL")) {
         this.issueCommandCheck("EPSV");
         var2 = this.getResponseString();
         if (epsvPat == null) {
            epsvPat = Pattern.compile("^229 .* \\(\\|\\|\\|(\\d+)\\|\\)");
         }

         var5 = epsvPat.matcher(var2);
         if (!var5.find()) {
            throw new FtpProtocolException("EPSV failed : " + var2);
         }

         var6 = var5.group(1);
         var3 = Integer.parseInt(var6);
         InetAddress var7 = this.server.getInetAddress();
         if (var7 != null) {
            var4 = new InetSocketAddress(var7, var3);
         } else {
            var4 = InetSocketAddress.createUnresolved(this.serverAddr.getHostName(), var3);
         }
      } else {
         this.issueCommandCheck("PASV");
         var2 = this.getResponseString();
         if (pasvPat == null) {
            pasvPat = Pattern.compile("227 .* \\(?(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)?");
         }

         var5 = pasvPat.matcher(var2);
         if (!var5.find()) {
            throw new FtpProtocolException("PASV failed : " + var2);
         }

         var3 = Integer.parseInt(var5.group(3)) + (Integer.parseInt(var5.group(2)) << 8);
         var6 = var5.group(1).replace(',', '.');
         var4 = new InetSocketAddress(var6, var3);
      }

      Socket var9;
      if (this.proxy != null) {
         if (this.proxy.type() == Proxy.Type.SOCKS) {
            var9 = (Socket)AccessController.doPrivileged(new PrivilegedAction<Socket>() {
               public Socket run() {
                  return new Socket(FtpClient.this.proxy);
               }
            });
         } else {
            var9 = new Socket(Proxy.NO_PROXY);
         }
      } else {
         var9 = new Socket();
      }

      InetAddress var10 = (InetAddress)AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
         public InetAddress run() {
            return FtpClient.this.server.getLocalAddress();
         }
      });
      var9.bind(new InetSocketAddress(var10, 0));
      if (this.connectTimeout >= 0) {
         var9.connect(var4, this.connectTimeout);
      } else if (defaultConnectTimeout > 0) {
         var9.connect(var4, defaultConnectTimeout);
      } else {
         var9.connect(var4);
      }

      if (this.readTimeout >= 0) {
         var9.setSoTimeout(this.readTimeout);
      } else if (defaultSoTimeout > 0) {
         var9.setSoTimeout(defaultSoTimeout);
      }

      if (this.useCrypto) {
         try {
            var9 = this.sslFact.createSocket(var9, var4.getHostName(), var4.getPort(), true);
         } catch (Exception var8) {
            throw new FtpProtocolException("Can't open secure data channel: " + var8);
         }
      }

      if (!this.issueCommand(var1)) {
         var9.close();
         if (this.getLastReplyCode() == FtpReplyCode.FILE_UNAVAILABLE) {
            throw new FileNotFoundException(var1);
         } else {
            throw new FtpProtocolException(var1 + ":" + this.getResponseString(), this.getLastReplyCode());
         }
      } else {
         return var9;
      }
   }

   private Socket openDataConnection(String var1) throws FtpProtocolException, IOException {
      try {
         return this.openPassiveDataConnection(var1);
      } catch (FtpProtocolException var14) {
         String var4 = var14.getMessage();
         if (!var4.startsWith("PASV") && !var4.startsWith("EPSV")) {
            throw var14;
         } else if (this.proxy != null && this.proxy.type() == Proxy.Type.SOCKS) {
            throw new FtpProtocolException("Passive mode failed");
         } else {
            ServerSocket var3 = new ServerSocket(0, 1, this.server.getLocalAddress());

            Socket var2;
            try {
               InetAddress var15 = var3.getInetAddress();
               if (var15.isAnyLocalAddress()) {
                  var15 = this.server.getLocalAddress();
               }

               String var5 = "EPRT |" + (var15 instanceof Inet6Address ? "2" : "1") + "|" + var15.getHostAddress() + "|" + var3.getLocalPort() + "|";
               if (!this.issueCommand(var5) || !this.issueCommand(var1)) {
                  var5 = "PORT ";
                  byte[] var6 = var15.getAddress();

                  for(int var7 = 0; var7 < var6.length; ++var7) {
                     var5 = var5 + (var6[var7] & 255) + ",";
                  }

                  var5 = var5 + (var3.getLocalPort() >>> 8 & 255) + "," + (var3.getLocalPort() & 255);
                  this.issueCommandCheck(var5);
                  this.issueCommandCheck(var1);
               }

               if (this.connectTimeout >= 0) {
                  var3.setSoTimeout(this.connectTimeout);
               } else if (defaultConnectTimeout > 0) {
                  var3.setSoTimeout(defaultConnectTimeout);
               }

               var2 = var3.accept();
               if (this.readTimeout >= 0) {
                  var2.setSoTimeout(this.readTimeout);
               } else if (defaultSoTimeout > 0) {
                  var2.setSoTimeout(defaultSoTimeout);
               }
            } finally {
               var3.close();
            }

            if (this.useCrypto) {
               try {
                  var2 = this.sslFact.createSocket(var2, this.serverAddr.getHostName(), this.serverAddr.getPort(), true);
               } catch (Exception var12) {
                  throw new IOException(var12.getLocalizedMessage());
               }
            }

            return var2;
         }
      }
   }

   private InputStream createInputStream(InputStream var1) {
      return (InputStream)(this.type == sun.net.ftp.FtpClient.TransferType.ASCII ? new TelnetInputStream(var1, false) : var1);
   }

   private OutputStream createOutputStream(OutputStream var1) {
      return (OutputStream)(this.type == sun.net.ftp.FtpClient.TransferType.ASCII ? new TelnetOutputStream(var1, false) : var1);
   }

   protected FtpClient() {
      this.type = sun.net.ftp.FtpClient.TransferType.BINARY;
      this.restartOffset = 0L;
      this.lastTransSize = -1L;
      this.df = DateFormat.getDateInstance(2, Locale.US);
      this.parser = new FtpClient.DefaultParser();
      this.mlsxParser = new FtpClient.MLSxParser();
   }

   public static sun.net.ftp.FtpClient create() {
      return new FtpClient();
   }

   public sun.net.ftp.FtpClient enablePassiveMode(boolean var1) {
      return this;
   }

   public boolean isPassiveModeEnabled() {
      return true;
   }

   public sun.net.ftp.FtpClient setConnectTimeout(int var1) {
      this.connectTimeout = var1;
      return this;
   }

   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   public sun.net.ftp.FtpClient setReadTimeout(int var1) {
      this.readTimeout = var1;
      return this;
   }

   public int getReadTimeout() {
      return this.readTimeout;
   }

   public sun.net.ftp.FtpClient setProxy(Proxy var1) {
      this.proxy = var1;
      return this;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   private void tryConnect(InetSocketAddress var1, int var2) throws IOException {
      if (this.isConnected()) {
         this.disconnect();
      }

      this.server = this.doConnect(var1, var2);

      try {
         this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
      } catch (UnsupportedEncodingException var4) {
         throw new InternalError(encoding + "encoding not found", var4);
      }

      this.in = new BufferedInputStream(this.server.getInputStream());
   }

   private Socket doConnect(InetSocketAddress var1, int var2) throws IOException {
      Socket var3;
      if (this.proxy != null) {
         if (this.proxy.type() == Proxy.Type.SOCKS) {
            var3 = (Socket)AccessController.doPrivileged(new PrivilegedAction<Socket>() {
               public Socket run() {
                  return new Socket(FtpClient.this.proxy);
               }
            });
         } else {
            var3 = new Socket(Proxy.NO_PROXY);
         }
      } else {
         var3 = new Socket();
      }

      if (var2 >= 0) {
         var3.connect(var1, var2);
      } else if (this.connectTimeout >= 0) {
         var3.connect(var1, this.connectTimeout);
      } else if (defaultConnectTimeout > 0) {
         var3.connect(var1, defaultConnectTimeout);
      } else {
         var3.connect(var1);
      }

      if (this.readTimeout >= 0) {
         var3.setSoTimeout(this.readTimeout);
      } else if (defaultSoTimeout > 0) {
         var3.setSoTimeout(defaultSoTimeout);
      }

      return var3;
   }

   private void disconnect() throws IOException {
      if (this.isConnected()) {
         this.server.close();
      }

      this.server = null;
      this.in = null;
      this.out = null;
      this.lastTransSize = -1L;
      this.lastFileName = null;
      this.restartOffset = 0L;
      this.welcomeMsg = null;
      this.lastReplyCode = null;
      this.serverResponse.setSize(0);
   }

   public boolean isConnected() {
      return this.server != null;
   }

   public SocketAddress getServerAddress() {
      return this.server == null ? null : this.server.getRemoteSocketAddress();
   }

   public sun.net.ftp.FtpClient connect(SocketAddress var1) throws FtpProtocolException, IOException {
      return this.connect(var1, -1);
   }

   public sun.net.ftp.FtpClient connect(SocketAddress var1, int var2) throws FtpProtocolException, IOException {
      if (!(var1 instanceof InetSocketAddress)) {
         throw new IllegalArgumentException("Wrong address type");
      } else {
         this.serverAddr = (InetSocketAddress)var1;
         this.tryConnect(this.serverAddr, var2);
         if (!this.readReply()) {
            throw new FtpProtocolException("Welcome message: " + this.getResponseString(), this.lastReplyCode);
         } else {
            this.welcomeMsg = this.getResponseString().substring(4);
            return this;
         }
      }
   }

   private void tryLogin(String var1, char[] var2) throws FtpProtocolException, IOException {
      this.issueCommandCheck("USER " + var1);
      if (this.lastReplyCode == FtpReplyCode.NEED_PASSWORD && var2 != null && var2.length > 0) {
         this.issueCommandCheck("PASS " + String.valueOf(var2));
      }

   }

   public sun.net.ftp.FtpClient login(String var1, char[] var2) throws FtpProtocolException, IOException {
      if (!this.isConnected()) {
         throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
      } else if (var1 != null && var1.length() != 0) {
         this.tryLogin(var1, var2);
         StringBuffer var4 = new StringBuffer();

         for(int var5 = 0; var5 < this.serverResponse.size(); ++var5) {
            String var3 = (String)this.serverResponse.elementAt(var5);
            if (var3 != null) {
               if (var3.length() >= 4 && var3.startsWith("230")) {
                  var3 = var3.substring(4);
               }

               var4.append(var3);
            }
         }

         this.welcomeMsg = var4.toString();
         this.loggedIn = true;
         return this;
      } else {
         throw new IllegalArgumentException("User name can't be null or empty");
      }
   }

   public sun.net.ftp.FtpClient login(String var1, char[] var2, String var3) throws FtpProtocolException, IOException {
      if (!this.isConnected()) {
         throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
      } else if (var1 != null && var1.length() != 0) {
         this.tryLogin(var1, var2);
         if (this.lastReplyCode == FtpReplyCode.NEED_ACCOUNT) {
            this.issueCommandCheck("ACCT " + var3);
         }

         StringBuffer var4 = new StringBuffer();
         if (this.serverResponse != null) {
            Iterator var5 = this.serverResponse.iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               if (var6 != null) {
                  if (var6.length() >= 4 && var6.startsWith("230")) {
                     var6 = var6.substring(4);
                  }

                  var4.append(var6);
               }
            }
         }

         this.welcomeMsg = var4.toString();
         this.loggedIn = true;
         return this;
      } else {
         throw new IllegalArgumentException("User name can't be null or empty");
      }
   }

   public void close() throws IOException {
      if (this.isConnected()) {
         try {
            this.issueCommand("QUIT");
         } catch (FtpProtocolException var2) {
         }

         this.loggedIn = false;
      }

      this.disconnect();
   }

   public boolean isLoggedIn() {
      return this.loggedIn;
   }

   public sun.net.ftp.FtpClient changeDirectory(String var1) throws FtpProtocolException, IOException {
      if (var1 != null && !"".equals(var1)) {
         this.issueCommandCheck("CWD " + var1);
         return this;
      } else {
         throw new IllegalArgumentException("directory can't be null or empty");
      }
   }

   public sun.net.ftp.FtpClient changeToParentDirectory() throws FtpProtocolException, IOException {
      this.issueCommandCheck("CDUP");
      return this;
   }

   public String getWorkingDirectory() throws FtpProtocolException, IOException {
      this.issueCommandCheck("PWD");
      String var1 = this.getResponseString();
      return !var1.startsWith("257") ? null : var1.substring(5, var1.lastIndexOf(34));
   }

   public sun.net.ftp.FtpClient setRestartOffset(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("offset can't be negative");
      } else {
         this.restartOffset = var1;
         return this;
      }
   }

   public sun.net.ftp.FtpClient getFile(String var1, OutputStream var2) throws FtpProtocolException, IOException {
      short var3 = 1500;
      Socket var4;
      InputStream var5;
      byte[] var6;
      int var7;
      if (this.restartOffset > 0L) {
         try {
            var4 = this.openDataConnection("REST " + this.restartOffset);
         } finally {
            this.restartOffset = 0L;
         }

         this.issueCommandCheck("RETR " + var1);
         this.getTransferSize();
         var5 = this.createInputStream(var4.getInputStream());
         var6 = new byte[var3 * 10];

         while((var7 = var5.read(var6)) >= 0) {
            if (var7 > 0) {
               var2.write(var6, 0, var7);
            }
         }

         var5.close();
      } else {
         var4 = this.openDataConnection("RETR " + var1);
         this.getTransferSize();
         var5 = this.createInputStream(var4.getInputStream());
         var6 = new byte[var3 * 10];

         while((var7 = var5.read(var6)) >= 0) {
            if (var7 > 0) {
               var2.write(var6, 0, var7);
            }
         }

         var5.close();
      }

      return this.completePending();
   }

   public InputStream getFileStream(String var1) throws FtpProtocolException, IOException {
      Socket var2;
      if (this.restartOffset > 0L) {
         try {
            var2 = this.openDataConnection("REST " + this.restartOffset);
         } finally {
            this.restartOffset = 0L;
         }

         if (var2 == null) {
            return null;
         } else {
            this.issueCommandCheck("RETR " + var1);
            this.getTransferSize();
            return this.createInputStream(var2.getInputStream());
         }
      } else {
         var2 = this.openDataConnection("RETR " + var1);
         if (var2 == null) {
            return null;
         } else {
            this.getTransferSize();
            return this.createInputStream(var2.getInputStream());
         }
      }
   }

   public OutputStream putFileStream(String var1, boolean var2) throws FtpProtocolException, IOException {
      String var3 = var2 ? "STOU " : "STOR ";
      Socket var4 = this.openDataConnection(var3 + var1);
      if (var4 == null) {
         return null;
      } else {
         boolean var5 = this.type == sun.net.ftp.FtpClient.TransferType.BINARY;
         return new TelnetOutputStream(var4.getOutputStream(), var5);
      }
   }

   public sun.net.ftp.FtpClient putFile(String var1, InputStream var2, boolean var3) throws FtpProtocolException, IOException {
      String var4 = var3 ? "STOU " : "STOR ";
      short var5 = 1500;
      if (this.type == sun.net.ftp.FtpClient.TransferType.BINARY) {
         Socket var6 = this.openDataConnection(var4 + var1);
         OutputStream var7 = this.createOutputStream(var6.getOutputStream());
         byte[] var8 = new byte[var5 * 10];

         int var9;
         while((var9 = var2.read(var8)) >= 0) {
            if (var9 > 0) {
               var7.write(var8, 0, var9);
            }
         }

         var7.close();
      }

      return this.completePending();
   }

   public sun.net.ftp.FtpClient appendFile(String var1, InputStream var2) throws FtpProtocolException, IOException {
      short var3 = 1500;
      Socket var4 = this.openDataConnection("APPE " + var1);
      OutputStream var5 = this.createOutputStream(var4.getOutputStream());
      byte[] var6 = new byte[var3 * 10];

      int var7;
      while((var7 = var2.read(var6)) >= 0) {
         if (var7 > 0) {
            var5.write(var6, 0, var7);
         }
      }

      var5.close();
      return this.completePending();
   }

   public sun.net.ftp.FtpClient rename(String var1, String var2) throws FtpProtocolException, IOException {
      this.issueCommandCheck("RNFR " + var1);
      this.issueCommandCheck("RNTO " + var2);
      return this;
   }

   public sun.net.ftp.FtpClient deleteFile(String var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck("DELE " + var1);
      return this;
   }

   public sun.net.ftp.FtpClient makeDirectory(String var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck("MKD " + var1);
      return this;
   }

   public sun.net.ftp.FtpClient removeDirectory(String var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck("RMD " + var1);
      return this;
   }

   public sun.net.ftp.FtpClient noop() throws FtpProtocolException, IOException {
      this.issueCommandCheck("NOOP");
      return this;
   }

   public String getStatus(String var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck(var1 == null ? "STAT" : "STAT " + var1);
      Vector var2 = this.getResponseStrings();
      StringBuffer var3 = new StringBuffer();

      for(int var4 = 1; var4 < var2.size() - 1; ++var4) {
         var3.append((String)var2.get(var4));
      }

      return var3.toString();
   }

   public List<String> getFeatures() throws FtpProtocolException, IOException {
      ArrayList var1 = new ArrayList();
      this.issueCommandCheck("FEAT");
      Vector var2 = this.getResponseStrings();

      for(int var3 = 1; var3 < var2.size() - 1; ++var3) {
         String var4 = (String)var2.get(var3);
         var1.add(var4.substring(1, var4.length() - 1));
      }

      return var1;
   }

   public sun.net.ftp.FtpClient abort() throws FtpProtocolException, IOException {
      this.issueCommandCheck("ABOR");
      return this;
   }

   public sun.net.ftp.FtpClient completePending() throws FtpProtocolException, IOException {
      while(true) {
         if (this.replyPending) {
            this.replyPending = false;
            if (this.readReply()) {
               continue;
            }

            throw new FtpProtocolException(this.getLastResponseString(), this.lastReplyCode);
         }

         return this;
      }
   }

   public sun.net.ftp.FtpClient reInit() throws FtpProtocolException, IOException {
      this.issueCommandCheck("REIN");
      this.loggedIn = false;
      if (this.useCrypto && this.server instanceof SSLSocket) {
         SSLSession var1 = ((SSLSocket)this.server).getSession();
         var1.invalidate();
         this.server = this.oldSocket;
         this.oldSocket = null;

         try {
            this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
         } catch (UnsupportedEncodingException var3) {
            throw new InternalError(encoding + "encoding not found", var3);
         }

         this.in = new BufferedInputStream(this.server.getInputStream());
      }

      this.useCrypto = false;
      return this;
   }

   public sun.net.ftp.FtpClient setType(sun.net.ftp.FtpClient.TransferType var1) throws FtpProtocolException, IOException {
      String var2 = "NOOP";
      this.type = var1;
      if (var1 == sun.net.ftp.FtpClient.TransferType.ASCII) {
         var2 = "TYPE A";
      }

      if (var1 == sun.net.ftp.FtpClient.TransferType.BINARY) {
         var2 = "TYPE I";
      }

      if (var1 == sun.net.ftp.FtpClient.TransferType.EBCDIC) {
         var2 = "TYPE E";
      }

      this.issueCommandCheck(var2);
      return this;
   }

   public InputStream list(String var1) throws FtpProtocolException, IOException {
      Socket var2 = this.openDataConnection(var1 == null ? "LIST" : "LIST " + var1);
      return var2 != null ? this.createInputStream(var2.getInputStream()) : null;
   }

   public InputStream nameList(String var1) throws FtpProtocolException, IOException {
      Socket var2 = this.openDataConnection(var1 == null ? "NLST" : "NLST " + var1);
      return var2 != null ? this.createInputStream(var2.getInputStream()) : null;
   }

   public long getSize(String var1) throws FtpProtocolException, IOException {
      if (var1 != null && var1.length() != 0) {
         this.issueCommandCheck("SIZE " + var1);
         if (this.lastReplyCode == FtpReplyCode.FILE_STATUS) {
            String var2 = this.getResponseString();
            var2 = var2.substring(4, var2.length() - 1);
            return Long.parseLong(var2);
         } else {
            return -1L;
         }
      } else {
         throw new IllegalArgumentException("path can't be null or empty");
      }
   }

   public Date getLastModified(String var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck("MDTM " + var1);
      if (this.lastReplyCode == FtpReplyCode.FILE_STATUS) {
         String var2 = this.getResponseString().substring(4);
         Date var3 = null;
         SimpleDateFormat[] var4 = dateFormats;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            SimpleDateFormat var7 = var4[var6];

            try {
               var3 = var7.parse(var2);
            } catch (ParseException var9) {
            }

            if (var3 != null) {
               return var3;
            }
         }
      }

      return null;
   }

   public sun.net.ftp.FtpClient setDirParser(FtpDirParser var1) {
      this.parser = var1;
      return this;
   }

   public Iterator<FtpDirEntry> listFiles(String var1) throws FtpProtocolException, IOException {
      Socket var2 = null;
      BufferedReader var3 = null;

      try {
         var2 = this.openDataConnection(var1 == null ? "MLSD" : "MLSD " + var1);
      } catch (FtpProtocolException var5) {
      }

      if (var2 != null) {
         var3 = new BufferedReader(new InputStreamReader(var2.getInputStream()));
         return new FtpClient.FtpFileIterator(this.mlsxParser, var3);
      } else {
         var2 = this.openDataConnection(var1 == null ? "LIST" : "LIST " + var1);
         if (var2 != null) {
            var3 = new BufferedReader(new InputStreamReader(var2.getInputStream()));
            return new FtpClient.FtpFileIterator(this.parser, var3);
         } else {
            return null;
         }
      }
   }

   private boolean sendSecurityData(byte[] var1) throws IOException, FtpProtocolException {
      BASE64Encoder var2 = new BASE64Encoder();
      String var3 = var2.encode(var1);
      return this.issueCommand("ADAT " + var3);
   }

   private byte[] getSecurityData() {
      String var1 = this.getLastResponseString();
      if (var1.substring(4, 9).equalsIgnoreCase("ADAT=")) {
         BASE64Decoder var2 = new BASE64Decoder();

         try {
            return var2.decodeBuffer(var1.substring(9, var1.length() - 1));
         } catch (IOException var4) {
         }
      }

      return null;
   }

   public sun.net.ftp.FtpClient useKerberos() throws FtpProtocolException, IOException {
      return this;
   }

   public String getWelcomeMsg() {
      return this.welcomeMsg;
   }

   public FtpReplyCode getLastReplyCode() {
      return this.lastReplyCode;
   }

   public String getLastResponseString() {
      StringBuffer var1 = new StringBuffer();
      if (this.serverResponse != null) {
         Iterator var2 = this.serverResponse.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            if (var3 != null) {
               var1.append(var3);
            }
         }
      }

      return var1.toString();
   }

   public long getLastTransferSize() {
      return this.lastTransSize;
   }

   public String getLastFileName() {
      return this.lastFileName;
   }

   public sun.net.ftp.FtpClient startSecureSession() throws FtpProtocolException, IOException {
      if (!this.isConnected()) {
         throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
      } else {
         if (this.sslFact == null) {
            try {
               this.sslFact = (SSLSocketFactory)SSLSocketFactory.getDefault();
            } catch (Exception var7) {
               throw new IOException(var7.getLocalizedMessage());
            }
         }

         this.issueCommandCheck("AUTH TLS");
         Socket var1 = null;

         try {
            var1 = this.sslFact.createSocket(this.server, this.serverAddr.getHostName(), this.serverAddr.getPort(), true);
         } catch (SSLException var6) {
            try {
               this.disconnect();
            } catch (Exception var4) {
            }

            throw var6;
         }

         this.oldSocket = this.server;
         this.server = var1;

         try {
            this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
         } catch (UnsupportedEncodingException var5) {
            throw new InternalError(encoding + "encoding not found", var5);
         }

         this.in = new BufferedInputStream(this.server.getInputStream());
         this.issueCommandCheck("PBSZ 0");
         this.issueCommandCheck("PROT P");
         this.useCrypto = true;
         return this;
      }
   }

   public sun.net.ftp.FtpClient endSecureSession() throws FtpProtocolException, IOException {
      if (!this.useCrypto) {
         return this;
      } else {
         this.issueCommandCheck("CCC");
         this.issueCommandCheck("PROT C");
         this.useCrypto = false;
         this.server = this.oldSocket;
         this.oldSocket = null;

         try {
            this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
         } catch (UnsupportedEncodingException var2) {
            throw new InternalError(encoding + "encoding not found", var2);
         }

         this.in = new BufferedInputStream(this.server.getInputStream());
         return this;
      }
   }

   public sun.net.ftp.FtpClient allocate(long var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck("ALLO " + var1);
      return this;
   }

   public sun.net.ftp.FtpClient structureMount(String var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck("SMNT " + var1);
      return this;
   }

   public String getSystem() throws FtpProtocolException, IOException {
      this.issueCommandCheck("SYST");
      String var1 = this.getResponseString();
      return var1.substring(4);
   }

   public String getHelp(String var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck("HELP " + var1);
      Vector var2 = this.getResponseStrings();
      if (var2.size() == 1) {
         return ((String)var2.get(0)).substring(4);
      } else {
         StringBuffer var3 = new StringBuffer();

         for(int var4 = 1; var4 < var2.size() - 1; ++var4) {
            var3.append(((String)var2.get(var4)).substring(3));
         }

         return var3.toString();
      }
   }

   public sun.net.ftp.FtpClient siteCmd(String var1) throws FtpProtocolException, IOException {
      this.issueCommandCheck("SITE " + var1);
      return this;
   }

   static {
      final int[] var0 = new int[]{0, 0};
      final String[] var1 = new String[]{null};
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            var0[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 300000);
            var0[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 300000);
            var1[0] = System.getProperty("file.encoding", "ISO8859_1");
            return null;
         }
      });
      if (var0[0] == 0) {
         defaultSoTimeout = -1;
      } else {
         defaultSoTimeout = var0[0];
      }

      if (var0[1] == 0) {
         defaultConnectTimeout = -1;
      } else {
         defaultConnectTimeout = var0[1];
      }

      encoding = var1[0];

      try {
         if (!isASCIISuperset(encoding)) {
            encoding = "ISO8859_1";
         }
      } catch (Exception var3) {
         encoding = "ISO8859_1";
      }

      patterns = new Pattern[patStrings.length];

      for(int var2 = 0; var2 < patStrings.length; ++var2) {
         patterns[var2] = Pattern.compile(patStrings[var2]);
      }

      transPat = null;
      epsvPat = null;
      pasvPat = null;
      MDTMformats = new String[]{"yyyyMMddHHmmss.SSS", "yyyyMMddHHmmss"};
      dateFormats = new SimpleDateFormat[MDTMformats.length];

      for(int var4 = 0; var4 < MDTMformats.length; ++var4) {
         dateFormats[var4] = new SimpleDateFormat(MDTMformats[var4]);
         dateFormats[var4].setTimeZone(TimeZone.getTimeZone("GMT"));
      }

   }

   private class FtpFileIterator implements Iterator<FtpDirEntry>, Closeable {
      private BufferedReader in = null;
      private FtpDirEntry nextFile = null;
      private FtpDirParser fparser = null;
      private boolean eof = false;

      public FtpFileIterator(FtpDirParser var2, BufferedReader var3) {
         this.in = var3;
         this.fparser = var2;
         this.readNext();
      }

      private void readNext() {
         this.nextFile = null;
         if (!this.eof) {
            String var1 = null;

            try {
               do {
                  var1 = this.in.readLine();
                  if (var1 != null) {
                     this.nextFile = this.fparser.parseLine(var1);
                     if (this.nextFile != null) {
                        return;
                     }
                  }
               } while(var1 != null);

               this.in.close();
            } catch (IOException var3) {
            }

            this.eof = true;
         }
      }

      public boolean hasNext() {
         return this.nextFile != null;
      }

      public FtpDirEntry next() {
         FtpDirEntry var1 = this.nextFile;
         this.readNext();
         return var1;
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void close() throws IOException {
         if (this.in != null && !this.eof) {
            this.in.close();
         }

         this.eof = true;
         this.nextFile = null;
      }
   }

   private class MLSxParser implements FtpDirParser {
      private SimpleDateFormat df;

      private MLSxParser() {
         this.df = new SimpleDateFormat("yyyyMMddhhmmss");
      }

      public FtpDirEntry parseLine(String var1) {
         String var2 = null;
         int var3 = var1.lastIndexOf(";");
         if (var3 > 0) {
            var2 = var1.substring(var3 + 1).trim();
            var1 = var1.substring(0, var3);
         } else {
            var2 = var1.trim();
            var1 = "";
         }

         FtpDirEntry var4 = new FtpDirEntry(var2);

         String var5;
         while(!var1.isEmpty()) {
            var3 = var1.indexOf(";");
            if (var3 > 0) {
               var5 = var1.substring(0, var3);
               var1 = var1.substring(var3 + 1);
            } else {
               var5 = var1;
               var1 = "";
            }

            var3 = var5.indexOf("=");
            if (var3 > 0) {
               String var6 = var5.substring(0, var3);
               String var7 = var5.substring(var3 + 1);
               var4.addFact(var6, var7);
            }
         }

         var5 = var4.getFact("Size");
         if (var5 != null) {
            var4.setSize(Long.parseLong(var5));
         }

         var5 = var4.getFact("Modify");
         Date var10;
         if (var5 != null) {
            var10 = null;

            try {
               var10 = this.df.parse(var5);
            } catch (ParseException var9) {
            }

            if (var10 != null) {
               var4.setLastModified(var10);
            }
         }

         var5 = var4.getFact("Create");
         if (var5 != null) {
            var10 = null;

            try {
               var10 = this.df.parse(var5);
            } catch (ParseException var8) {
            }

            if (var10 != null) {
               var4.setCreated(var10);
            }
         }

         var5 = var4.getFact("Type");
         if (var5 != null) {
            if (var5.equalsIgnoreCase("file")) {
               var4.setType(FtpDirEntry.Type.FILE);
            }

            if (var5.equalsIgnoreCase("dir")) {
               var4.setType(FtpDirEntry.Type.DIR);
            }

            if (var5.equalsIgnoreCase("cdir")) {
               var4.setType(FtpDirEntry.Type.CDIR);
            }

            if (var5.equalsIgnoreCase("pdir")) {
               var4.setType(FtpDirEntry.Type.PDIR);
            }
         }

         return var4;
      }

      // $FF: synthetic method
      MLSxParser(Object var2) {
         this();
      }
   }

   private class DefaultParser implements FtpDirParser {
      private DefaultParser() {
      }

      public FtpDirEntry parseLine(String var1) {
         String var2 = null;
         String var3 = null;
         String var4 = null;
         String var5 = null;
         String var6 = null;
         String var7 = null;
         String var8 = null;
         boolean var9 = false;
         Calendar var10 = Calendar.getInstance();
         int var11 = var10.get(1);
         Matcher var12 = null;

         for(int var13 = 0; var13 < FtpClient.patterns.length; ++var13) {
            var12 = FtpClient.patterns[var13].matcher(var1);
            if (var12.find()) {
               var5 = var12.group(FtpClient.patternGroups[var13][0]);
               var3 = var12.group(FtpClient.patternGroups[var13][1]);
               var2 = var12.group(FtpClient.patternGroups[var13][2]);
               if (FtpClient.patternGroups[var13][4] > 0) {
                  var2 = var2 + ", " + var12.group(FtpClient.patternGroups[var13][4]);
               } else if (FtpClient.patternGroups[var13][3] > 0) {
                  var2 = var2 + ", " + var11;
               }

               if (FtpClient.patternGroups[var13][3] > 0) {
                  var4 = var12.group(FtpClient.patternGroups[var13][3]);
               }

               if (FtpClient.patternGroups[var13][5] > 0) {
                  var6 = var12.group(FtpClient.patternGroups[var13][5]);
                  var9 = var6.startsWith("d");
               }

               if (FtpClient.patternGroups[var13][6] > 0) {
                  var7 = var12.group(FtpClient.patternGroups[var13][6]);
               }

               if (FtpClient.patternGroups[var13][7] > 0) {
                  var8 = var12.group(FtpClient.patternGroups[var13][7]);
               }

               if ("<DIR>".equals(var3)) {
                  var9 = true;
                  var3 = null;
               }
            }
         }

         if (var5 == null) {
            return null;
         } else {
            Date var19;
            try {
               var19 = FtpClient.this.df.parse(var2);
            } catch (Exception var18) {
               var19 = null;
            }

            if (var19 != null && var4 != null) {
               int var14 = var4.indexOf(":");
               var10.setTime(var19);
               var10.set(10, Integer.parseInt(var4.substring(0, var14)));
               var10.set(12, Integer.parseInt(var4.substring(var14 + 1)));
               var19 = var10.getTime();
            }

            Matcher var20 = FtpClient.linkp.matcher(var5);
            if (var20.find()) {
               var5 = var20.group(1);
            }

            boolean[][] var15 = new boolean[3][3];

            for(int var16 = 0; var16 < 3; ++var16) {
               for(int var17 = 0; var17 < 3; ++var17) {
                  var15[var16][var17] = var6.charAt(var16 * 3 + var17) != '-';
               }
            }

            FtpDirEntry var21 = new FtpDirEntry(var5);
            var21.setUser(var7).setGroup(var8);
            var21.setSize(Long.parseLong(var3)).setLastModified(var19);
            var21.setPermissions(var15);
            var21.setType(var9 ? FtpDirEntry.Type.DIR : (var1.charAt(0) == 'l' ? FtpDirEntry.Type.LINK : FtpDirEntry.Type.FILE));
            return var21;
         }
      }

      // $FF: synthetic method
      DefaultParser(Object var2) {
         this();
      }
   }
}
