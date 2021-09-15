package java.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import sun.net.SocksProxy;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

class SocksSocketImpl extends PlainSocketImpl implements SocksConsts {
   private String server = null;
   private int serverPort = 1080;
   private InetSocketAddress external_address;
   private boolean useV4 = false;
   private Socket cmdsock = null;
   private InputStream cmdIn = null;
   private OutputStream cmdOut = null;
   private boolean applicationSetProxy;

   SocksSocketImpl() {
   }

   SocksSocketImpl(String var1, int var2) {
      this.server = var1;
      this.serverPort = var2 == -1 ? 1080 : var2;
   }

   SocksSocketImpl(Proxy var1) {
      SocketAddress var2 = var1.address();
      if (var2 instanceof InetSocketAddress) {
         InetSocketAddress var3 = (InetSocketAddress)var2;
         this.server = var3.getHostString();
         this.serverPort = var3.getPort();
      }

   }

   void setV4() {
      this.useV4 = true;
   }

   private synchronized void privilegedConnect(final String var1, final int var2, final int var3) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               SocksSocketImpl.this.superConnectServer(var1, var2, var3);
               SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.getInputStream();
               SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.getOutputStream();
               return null;
            }
         });
      } catch (PrivilegedActionException var5) {
         throw (IOException)var5.getException();
      }
   }

   private void superConnectServer(String var1, int var2, int var3) throws IOException {
      super.connect(new InetSocketAddress(var1, var2), var3);
   }

   private static int remainingMillis(long var0) throws IOException {
      if (var0 == 0L) {
         return 0;
      } else {
         long var2 = var0 - System.currentTimeMillis();
         if (var2 > 0L) {
            return (int)var2;
         } else {
            throw new SocketTimeoutException();
         }
      }
   }

   private int readSocksReply(InputStream var1, byte[] var2) throws IOException {
      return this.readSocksReply(var1, var2, 0L);
   }

   private int readSocksReply(InputStream var1, byte[] var2, long var3) throws IOException {
      int var5 = var2.length;
      int var6 = 0;

      for(int var7 = 0; var6 < var5 && var7 < 3; ++var7) {
         int var8;
         try {
            var8 = ((SocketInputStream)var1).read(var2, var6, var5 - var6, remainingMillis(var3));
         } catch (SocketTimeoutException var10) {
            throw new SocketTimeoutException("Connect timed out");
         }

         if (var8 < 0) {
            throw new SocketException("Malformed reply from SOCKS server");
         }

         var6 += var8;
      }

      return var6;
   }

   private boolean authenticate(byte var1, InputStream var2, BufferedOutputStream var3) throws IOException {
      return this.authenticate(var1, var2, var3, 0L);
   }

   private boolean authenticate(byte var1, InputStream var2, BufferedOutputStream var3, long var4) throws IOException {
      if (var1 == 0) {
         return true;
      } else if (var1 == 2) {
         String var7 = null;
         final InetAddress var8 = InetAddress.getByName(this.server);
         PasswordAuthentication var9 = (PasswordAuthentication)AccessController.doPrivileged(new PrivilegedAction<PasswordAuthentication>() {
            public PasswordAuthentication run() {
               return Authenticator.requestPasswordAuthentication(SocksSocketImpl.this.server, var8, SocksSocketImpl.this.serverPort, "SOCKS5", "SOCKS authentication", (String)null);
            }
         });
         String var6;
         if (var9 != null) {
            var6 = var9.getUserName();
            var7 = new String(var9.getPassword());
         } else {
            var6 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.name")));
         }

         if (var6 == null) {
            return false;
         } else {
            var3.write(1);
            var3.write(var6.length());

            try {
               var3.write(var6.getBytes("ISO-8859-1"));
            } catch (UnsupportedEncodingException var13) {
               assert false;
            }

            if (var7 != null) {
               var3.write(var7.length());

               try {
                  var3.write(var7.getBytes("ISO-8859-1"));
               } catch (UnsupportedEncodingException var12) {
                  assert false;
               }
            } else {
               var3.write(0);
            }

            var3.flush();
            byte[] var10 = new byte[2];
            int var11 = this.readSocksReply(var2, var10, var4);
            if (var11 == 2 && var10[1] == 0) {
               return true;
            } else {
               var3.close();
               var2.close();
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void connectV4(InputStream var1, OutputStream var2, InetSocketAddress var3, long var4) throws IOException {
      if (!(var3.getAddress() instanceof Inet4Address)) {
         throw new SocketException("SOCKS V4 requires IPv4 only addresses");
      } else {
         var2.write(4);
         var2.write(1);
         var2.write(var3.getPort() >> 8 & 255);
         var2.write(var3.getPort() >> 0 & 255);
         var2.write(var3.getAddress().getAddress());
         String var6 = this.getUserName();

         try {
            var2.write(var6.getBytes("ISO-8859-1"));
         } catch (UnsupportedEncodingException var10) {
            assert false;
         }

         var2.write(0);
         var2.flush();
         byte[] var7 = new byte[8];
         int var8 = this.readSocksReply(var1, var7, var4);
         if (var8 != 8) {
            throw new SocketException("Reply from SOCKS server has bad length: " + var8);
         } else if (var7[0] != 0 && var7[0] != 4) {
            throw new SocketException("Reply from SOCKS server has bad version");
         } else {
            SocketException var9 = null;
            switch(var7[1]) {
            case 90:
               this.external_address = var3;
               break;
            case 91:
               var9 = new SocketException("SOCKS request rejected");
               break;
            case 92:
               var9 = new SocketException("SOCKS server couldn't reach destination");
               break;
            case 93:
               var9 = new SocketException("SOCKS authentication failed");
               break;
            default:
               var9 = new SocketException("Reply from SOCKS server contains bad status");
            }

            if (var9 != null) {
               var1.close();
               var2.close();
               throw var9;
            }
         }
      }
   }

   protected void connect(SocketAddress var1, int var2) throws IOException {
      long var3;
      if (var2 == 0) {
         var3 = 0L;
      } else {
         long var5 = System.currentTimeMillis() + (long)var2;
         var3 = var5 < 0L ? Long.MAX_VALUE : var5;
      }

      SecurityManager var19 = System.getSecurityManager();
      if (var1 != null && var1 instanceof InetSocketAddress) {
         InetSocketAddress var6 = (InetSocketAddress)var1;
         if (var19 != null) {
            if (var6.isUnresolved()) {
               var19.checkConnect(var6.getHostName(), var6.getPort());
            } else {
               var19.checkConnect(var6.getAddress().getHostAddress(), var6.getPort());
            }
         }

         if (this.server == null) {
            ProxySelector var7 = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
               public ProxySelector run() {
                  return ProxySelector.getDefault();
               }
            });
            if (var7 == null) {
               super.connect(var6, remainingMillis(var3));
               return;
            }

            String var9 = var6.getHostString();
            if (var6.getAddress() instanceof Inet6Address && !var9.startsWith("[") && var9.indexOf(":") >= 0) {
               var9 = "[" + var9 + "]";
            }

            URI var8;
            try {
               var8 = new URI("socket://" + ParseUtil.encodePath(var9) + ":" + var6.getPort());
            } catch (URISyntaxException var17) {
               assert false : var17;

               var8 = null;
            }

            IOException var11;
            label183: {
               Proxy var10 = null;
               var11 = null;
               Iterator var12 = null;
               var12 = var7.select(var8).iterator();
               if (var12 != null && var12.hasNext()) {
                  while(true) {
                     if (!var12.hasNext()) {
                        break label183;
                     }

                     var10 = (Proxy)var12.next();
                     if (var10 == null || var10.type() != Proxy.Type.SOCKS) {
                        super.connect(var6, remainingMillis(var3));
                        return;
                     }

                     if (!(var10.address() instanceof InetSocketAddress)) {
                        throw new SocketException("Unknown address type for proxy: " + var10);
                     }

                     this.server = ((InetSocketAddress)var10.address()).getHostString();
                     this.serverPort = ((InetSocketAddress)var10.address()).getPort();
                     if (var10 instanceof SocksProxy && ((SocksProxy)var10).protocolVersion() == 4) {
                        this.useV4 = true;
                     }

                     try {
                        this.privilegedConnect(this.server, this.serverPort, remainingMillis(var3));
                        break label183;
                     } catch (IOException var18) {
                        var7.connectFailed(var8, var10.address(), var18);
                        this.server = null;
                        this.serverPort = -1;
                        var11 = var18;
                     }
                  }
               }

               super.connect(var6, remainingMillis(var3));
               return;
            }

            if (this.server == null) {
               throw new SocketException("Can't connect to SOCKS proxy:" + var11.getMessage());
            }
         } else {
            try {
               this.privilegedConnect(this.server, this.serverPort, remainingMillis(var3));
            } catch (IOException var15) {
               throw new SocketException(var15.getMessage());
            }
         }

         BufferedOutputStream var20 = new BufferedOutputStream(this.cmdOut, 512);
         InputStream var21 = this.cmdIn;
         if (this.useV4) {
            if (var6.isUnresolved()) {
               throw new UnknownHostException(var6.toString());
            } else {
               this.connectV4(var21, var20, var6, var3);
            }
         } else {
            var20.write(5);
            var20.write(2);
            var20.write(0);
            var20.write(2);
            var20.flush();
            byte[] var22 = new byte[2];
            int var23 = this.readSocksReply(var21, var22, var3);
            if (var23 == 2 && var22[0] == 5) {
               if (var22[1] == -1) {
                  throw new SocketException("SOCKS : No acceptable methods");
               } else if (!this.authenticate(var22[1], var21, var20, var3)) {
                  throw new SocketException("SOCKS : authentication failed");
               } else {
                  var20.write(5);
                  var20.write(1);
                  var20.write(0);
                  if (var6.isUnresolved()) {
                     var20.write(3);
                     var20.write(var6.getHostName().length());

                     try {
                        var20.write(var6.getHostName().getBytes("ISO-8859-1"));
                     } catch (UnsupportedEncodingException var16) {
                        assert false;
                     }

                     var20.write(var6.getPort() >> 8 & 255);
                     var20.write(var6.getPort() >> 0 & 255);
                  } else if (var6.getAddress() instanceof Inet6Address) {
                     var20.write(4);
                     var20.write(var6.getAddress().getAddress());
                     var20.write(var6.getPort() >> 8 & 255);
                     var20.write(var6.getPort() >> 0 & 255);
                  } else {
                     var20.write(1);
                     var20.write(var6.getAddress().getAddress());
                     var20.write(var6.getPort() >> 8 & 255);
                     var20.write(var6.getPort() >> 0 & 255);
                  }

                  var20.flush();
                  var22 = new byte[4];
                  var23 = this.readSocksReply(var21, var22, var3);
                  if (var23 != 4) {
                     throw new SocketException("Reply from SOCKS server has bad length");
                  } else {
                     SocketException var24;
                     var24 = null;
                     label120:
                     switch(var22[1]) {
                     case 0:
                        byte[] var13;
                        byte var25;
                        switch(var22[3]) {
                        case 1:
                           var13 = new byte[4];
                           var23 = this.readSocksReply(var21, var13, var3);
                           if (var23 != 4) {
                              throw new SocketException("Reply from SOCKS server badly formatted");
                           }

                           var22 = new byte[2];
                           var23 = this.readSocksReply(var21, var22, var3);
                           if (var23 != 2) {
                              throw new SocketException("Reply from SOCKS server badly formatted");
                           }
                           break label120;
                        case 2:
                        default:
                           var24 = new SocketException("Reply from SOCKS server contains wrong code");
                           break label120;
                        case 3:
                           var25 = var22[1];
                           byte[] var14 = new byte[var25];
                           var23 = this.readSocksReply(var21, var14, var3);
                           if (var23 != var25) {
                              throw new SocketException("Reply from SOCKS server badly formatted");
                           }

                           var22 = new byte[2];
                           var23 = this.readSocksReply(var21, var22, var3);
                           if (var23 != 2) {
                              throw new SocketException("Reply from SOCKS server badly formatted");
                           }
                           break label120;
                        case 4:
                           var25 = var22[1];
                           var13 = new byte[var25];
                           var23 = this.readSocksReply(var21, var13, var3);
                           if (var23 != var25) {
                              throw new SocketException("Reply from SOCKS server badly formatted");
                           }

                           var22 = new byte[2];
                           var23 = this.readSocksReply(var21, var22, var3);
                           if (var23 != 2) {
                              throw new SocketException("Reply from SOCKS server badly formatted");
                           }
                           break label120;
                        }
                     case 1:
                        var24 = new SocketException("SOCKS server general failure");
                        break;
                     case 2:
                        var24 = new SocketException("SOCKS: Connection not allowed by ruleset");
                        break;
                     case 3:
                        var24 = new SocketException("SOCKS: Network unreachable");
                        break;
                     case 4:
                        var24 = new SocketException("SOCKS: Host unreachable");
                        break;
                     case 5:
                        var24 = new SocketException("SOCKS: Connection refused");
                        break;
                     case 6:
                        var24 = new SocketException("SOCKS: TTL expired");
                        break;
                     case 7:
                        var24 = new SocketException("SOCKS: Command not supported");
                        break;
                     case 8:
                        var24 = new SocketException("SOCKS: address type not supported");
                     }

                     if (var24 != null) {
                        var21.close();
                        var20.close();
                        throw var24;
                     } else {
                        this.external_address = var6;
                     }
                  }
               }
            } else if (var6.isUnresolved()) {
               throw new UnknownHostException(var6.toString());
            } else {
               this.connectV4(var21, var20, var6, var3);
            }
         }
      } else {
         throw new IllegalArgumentException("Unsupported address type");
      }
   }

   private void bindV4(InputStream var1, OutputStream var2, InetAddress var3, int var4) throws IOException {
      if (!(var3 instanceof Inet4Address)) {
         throw new SocketException("SOCKS V4 requires IPv4 only addresses");
      } else {
         super.bind(var3, var4);
         byte[] var5 = var3.getAddress();
         if (var3.isAnyLocalAddress()) {
            InetAddress var6 = (InetAddress)AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
               public InetAddress run() {
                  return SocksSocketImpl.this.cmdsock.getLocalAddress();
               }
            });
            var5 = var6.getAddress();
         }

         var2.write(4);
         var2.write(2);
         var2.write(super.getLocalPort() >> 8 & 255);
         var2.write(super.getLocalPort() >> 0 & 255);
         var2.write(var5);
         String var7 = this.getUserName();

         try {
            var2.write(var7.getBytes("ISO-8859-1"));
         } catch (UnsupportedEncodingException var11) {
            assert false;
         }

         var2.write(0);
         var2.flush();
         byte[] var8 = new byte[8];
         int var9 = this.readSocksReply(var1, var8);
         if (var9 != 8) {
            throw new SocketException("Reply from SOCKS server has bad length: " + var9);
         } else if (var8[0] != 0 && var8[0] != 4) {
            throw new SocketException("Reply from SOCKS server has bad version");
         } else {
            SocketException var10 = null;
            switch(var8[1]) {
            case 90:
               this.external_address = new InetSocketAddress(var3, var4);
               break;
            case 91:
               var10 = new SocketException("SOCKS request rejected");
               break;
            case 92:
               var10 = new SocketException("SOCKS server couldn't reach destination");
               break;
            case 93:
               var10 = new SocketException("SOCKS authentication failed");
               break;
            default:
               var10 = new SocketException("Reply from SOCKS server contains bad status");
            }

            if (var10 != null) {
               var1.close();
               var2.close();
               throw var10;
            }
         }
      }
   }

   protected synchronized void socksBind(InetSocketAddress var1) throws IOException {
      if (this.socket == null) {
         if (this.server == null) {
            ProxySelector var2 = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
               public ProxySelector run() {
                  return ProxySelector.getDefault();
               }
            });
            if (var2 == null) {
               return;
            }

            String var4 = var1.getHostString();
            if (var1.getAddress() instanceof Inet6Address && !var4.startsWith("[") && var4.indexOf(":") >= 0) {
               var4 = "[" + var4 + "]";
            }

            URI var3;
            try {
               var3 = new URI("serversocket://" + ParseUtil.encodePath(var4) + ":" + var1.getPort());
            } catch (URISyntaxException var15) {
               assert false : var15;

               var3 = null;
            }

            Proxy var5 = null;
            Exception var6 = null;
            Iterator var7 = null;
            var7 = var2.select(var3).iterator();
            if (var7 == null || !var7.hasNext()) {
               return;
            }

            while(true) {
               if (!var7.hasNext()) {
                  if (this.server == null || this.cmdsock == null) {
                     throw new SocketException("Can't connect to SOCKS proxy:" + var6.getMessage());
                  }
                  break;
               }

               var5 = (Proxy)var7.next();
               if (var5 == null || var5.type() != Proxy.Type.SOCKS) {
                  return;
               }

               if (!(var5.address() instanceof InetSocketAddress)) {
                  throw new SocketException("Unknown address type for proxy: " + var5);
               }

               this.server = ((InetSocketAddress)var5.address()).getHostString();
               this.serverPort = ((InetSocketAddress)var5.address()).getPort();
               if (var5 instanceof SocksProxy && ((SocksProxy)var5).protocolVersion() == 4) {
                  this.useV4 = true;
               }

               try {
                  AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                     public Void run() throws Exception {
                        SocksSocketImpl.this.cmdsock = new Socket(new PlainSocketImpl());
                        SocksSocketImpl.this.cmdsock.connect(new InetSocketAddress(SocksSocketImpl.this.server, SocksSocketImpl.this.serverPort));
                        SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.cmdsock.getInputStream();
                        SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.cmdsock.getOutputStream();
                        return null;
                     }
                  });
               } catch (Exception var13) {
                  var2.connectFailed(var3, var5.address(), new SocketException(var13.getMessage()));
                  this.server = null;
                  this.serverPort = -1;
                  this.cmdsock = null;
                  var6 = var13;
               }
            }
         } else {
            try {
               AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                  public Void run() throws Exception {
                     SocksSocketImpl.this.cmdsock = new Socket(new PlainSocketImpl());
                     SocksSocketImpl.this.cmdsock.connect(new InetSocketAddress(SocksSocketImpl.this.server, SocksSocketImpl.this.serverPort));
                     SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.cmdsock.getInputStream();
                     SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.cmdsock.getOutputStream();
                     return null;
                  }
               });
            } catch (Exception var12) {
               throw new SocketException(var12.getMessage());
            }
         }

         BufferedOutputStream var16 = new BufferedOutputStream(this.cmdOut, 512);
         InputStream var17 = this.cmdIn;
         if (this.useV4) {
            this.bindV4(var17, var16, var1.getAddress(), var1.getPort());
         } else {
            var16.write(5);
            var16.write(2);
            var16.write(0);
            var16.write(2);
            var16.flush();
            byte[] var18 = new byte[2];
            int var19 = this.readSocksReply(var17, var18);
            if (var19 == 2 && var18[0] == 5) {
               if (var18[1] == -1) {
                  throw new SocketException("SOCKS : No acceptable methods");
               } else if (!this.authenticate(var18[1], var17, var16)) {
                  throw new SocketException("SOCKS : authentication failed");
               } else {
                  var16.write(5);
                  var16.write(2);
                  var16.write(0);
                  int var20 = var1.getPort();
                  if (var1.isUnresolved()) {
                     var16.write(3);
                     var16.write(var1.getHostName().length());

                     try {
                        var16.write(var1.getHostName().getBytes("ISO-8859-1"));
                     } catch (UnsupportedEncodingException var14) {
                        assert false;
                     }

                     var16.write(var20 >> 8 & 255);
                     var16.write(var20 >> 0 & 255);
                  } else {
                     byte[] var21;
                     if (var1.getAddress() instanceof Inet4Address) {
                        var21 = var1.getAddress().getAddress();
                        var16.write(1);
                        var16.write(var21);
                        var16.write(var20 >> 8 & 255);
                        var16.write(var20 >> 0 & 255);
                        var16.flush();
                     } else {
                        if (!(var1.getAddress() instanceof Inet6Address)) {
                           this.cmdsock.close();
                           throw new SocketException("unsupported address type : " + var1);
                        }

                        var21 = var1.getAddress().getAddress();
                        var16.write(4);
                        var16.write(var21);
                        var16.write(var20 >> 8 & 255);
                        var16.write(var20 >> 0 & 255);
                        var16.flush();
                     }
                  }

                  SocketException var22;
                  var18 = new byte[4];
                  this.readSocksReply(var17, var18);
                  var22 = null;
                  label112:
                  switch(var18[1]) {
                  case 0:
                     byte var8;
                     int var9;
                     byte[] var10;
                     switch(var18[3]) {
                     case 1:
                        var10 = new byte[4];
                        var19 = this.readSocksReply(var17, var10);
                        if (var19 != 4) {
                           throw new SocketException("Reply from SOCKS server badly formatted");
                        }

                        var18 = new byte[2];
                        var19 = this.readSocksReply(var17, var18);
                        if (var19 != 2) {
                           throw new SocketException("Reply from SOCKS server badly formatted");
                        }

                        var9 = (var18[0] & 255) << 8;
                        var9 += var18[1] & 255;
                        this.external_address = new InetSocketAddress(new Inet4Address("", var10), var9);
                     case 2:
                     default:
                        break label112;
                     case 3:
                        var8 = var18[1];
                        byte[] var11 = new byte[var8];
                        var19 = this.readSocksReply(var17, var11);
                        if (var19 != var8) {
                           throw new SocketException("Reply from SOCKS server badly formatted");
                        }

                        var18 = new byte[2];
                        var19 = this.readSocksReply(var17, var18);
                        if (var19 != 2) {
                           throw new SocketException("Reply from SOCKS server badly formatted");
                        }

                        var9 = (var18[0] & 255) << 8;
                        var9 += var18[1] & 255;
                        this.external_address = new InetSocketAddress(new String(var11), var9);
                        break label112;
                     case 4:
                        var8 = var18[1];
                        var10 = new byte[var8];
                        var19 = this.readSocksReply(var17, var10);
                        if (var19 != var8) {
                           throw new SocketException("Reply from SOCKS server badly formatted");
                        }

                        var18 = new byte[2];
                        var19 = this.readSocksReply(var17, var18);
                        if (var19 != 2) {
                           throw new SocketException("Reply from SOCKS server badly formatted");
                        }

                        var9 = (var18[0] & 255) << 8;
                        var9 += var18[1] & 255;
                        this.external_address = new InetSocketAddress(new Inet6Address("", var10), var9);
                        break label112;
                     }
                  case 1:
                     var22 = new SocketException("SOCKS server general failure");
                     break;
                  case 2:
                     var22 = new SocketException("SOCKS: Bind not allowed by ruleset");
                     break;
                  case 3:
                     var22 = new SocketException("SOCKS: Network unreachable");
                     break;
                  case 4:
                     var22 = new SocketException("SOCKS: Host unreachable");
                     break;
                  case 5:
                     var22 = new SocketException("SOCKS: Connection refused");
                     break;
                  case 6:
                     var22 = new SocketException("SOCKS: TTL expired");
                     break;
                  case 7:
                     var22 = new SocketException("SOCKS: Command not supported");
                     break;
                  case 8:
                     var22 = new SocketException("SOCKS: address type not supported");
                  }

                  if (var22 != null) {
                     var17.close();
                     var16.close();
                     this.cmdsock.close();
                     this.cmdsock = null;
                     throw var22;
                  } else {
                     this.cmdIn = var17;
                     this.cmdOut = var16;
                  }
               }
            } else {
               this.bindV4(var17, var16, var1.getAddress(), var1.getPort());
            }
         }
      }
   }

   protected void acceptFrom(SocketImpl var1, InetSocketAddress var2) throws IOException {
      if (this.cmdsock != null) {
         InputStream var3;
         SocketException var5;
         InetSocketAddress var8;
         var3 = this.cmdIn;
         this.socksBind(var2);
         var3.read();
         int var4 = var3.read();
         var3.read();
         var5 = null;
         var8 = null;
         label37:
         switch(var4) {
         case 0:
            var4 = var3.read();
            int var6;
            byte[] var7;
            switch(var4) {
            case 1:
               var7 = new byte[4];
               this.readSocksReply(var3, var7);
               var6 = var3.read() << 8;
               var6 += var3.read();
               var8 = new InetSocketAddress(new Inet4Address("", var7), var6);
            case 2:
            default:
               break label37;
            case 3:
               int var9 = var3.read();
               var7 = new byte[var9];
               this.readSocksReply(var3, var7);
               var6 = var3.read() << 8;
               var6 += var3.read();
               var8 = new InetSocketAddress(new String(var7), var6);
               break label37;
            case 4:
               var7 = new byte[16];
               this.readSocksReply(var3, var7);
               var6 = var3.read() << 8;
               var6 += var3.read();
               var8 = new InetSocketAddress(new Inet6Address("", var7), var6);
               break label37;
            }
         case 1:
            var5 = new SocketException("SOCKS server general failure");
            break;
         case 2:
            var5 = new SocketException("SOCKS: Accept not allowed by ruleset");
            break;
         case 3:
            var5 = new SocketException("SOCKS: Network unreachable");
            break;
         case 4:
            var5 = new SocketException("SOCKS: Host unreachable");
            break;
         case 5:
            var5 = new SocketException("SOCKS: Connection refused");
            break;
         case 6:
            var5 = new SocketException("SOCKS: TTL expired");
            break;
         case 7:
            var5 = new SocketException("SOCKS: Command not supported");
            break;
         case 8:
            var5 = new SocketException("SOCKS: address type not supported");
         }

         if (var5 != null) {
            this.cmdIn.close();
            this.cmdOut.close();
            this.cmdsock.close();
            this.cmdsock = null;
            throw var5;
         } else {
            if (var1 instanceof SocksSocketImpl) {
               ((SocksSocketImpl)var1).external_address = var8;
            }

            if (var1 instanceof PlainSocketImpl) {
               PlainSocketImpl var10 = (PlainSocketImpl)var1;
               var10.setInputStream((SocketInputStream)var3);
               var10.setFileDescriptor(this.cmdsock.getImpl().getFileDescriptor());
               var10.setAddress(this.cmdsock.getImpl().getInetAddress());
               var10.setPort(this.cmdsock.getImpl().getPort());
               var10.setLocalPort(this.cmdsock.getImpl().getLocalPort());
            } else {
               var1.fd = this.cmdsock.getImpl().fd;
               var1.address = this.cmdsock.getImpl().address;
               var1.port = this.cmdsock.getImpl().port;
               var1.localport = this.cmdsock.getImpl().localport;
            }

            this.cmdsock = null;
         }
      }
   }

   protected InetAddress getInetAddress() {
      return this.external_address != null ? this.external_address.getAddress() : super.getInetAddress();
   }

   protected int getPort() {
      return this.external_address != null ? this.external_address.getPort() : super.getPort();
   }

   protected int getLocalPort() {
      if (this.socket != null) {
         return super.getLocalPort();
      } else {
         return this.external_address != null ? this.external_address.getPort() : super.getLocalPort();
      }
   }

   protected void close() throws IOException {
      if (this.cmdsock != null) {
         this.cmdsock.close();
      }

      this.cmdsock = null;
      super.close();
   }

   private String getUserName() {
      String var1 = "";
      if (this.applicationSetProxy) {
         try {
            var1 = System.getProperty("user.name");
         } catch (SecurityException var3) {
         }
      } else {
         var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.name")));
      }

      return var1;
   }
}
