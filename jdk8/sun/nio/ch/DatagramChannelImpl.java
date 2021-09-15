package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.ResourceManager;

class DatagramChannelImpl extends DatagramChannel implements SelChImpl {
   private static NativeDispatcher nd = new DatagramDispatcher();
   private final FileDescriptor fd;
   private final int fdVal;
   private final ProtocolFamily family;
   private volatile long readerThread = 0L;
   private volatile long writerThread = 0L;
   private InetAddress cachedSenderInetAddress;
   private int cachedSenderPort;
   private final Object readLock = new Object();
   private final Object writeLock = new Object();
   private final Object stateLock = new Object();
   private static final int ST_UNINITIALIZED = -1;
   private static final int ST_UNCONNECTED = 0;
   private static final int ST_CONNECTED = 1;
   private static final int ST_KILLED = 2;
   private int state = -1;
   private InetSocketAddress localAddress;
   private InetSocketAddress remoteAddress;
   private DatagramSocket socket;
   private MembershipRegistry registry;
   private boolean reuseAddressEmulated;
   private boolean isReuseAddress;
   private SocketAddress sender;

   public DatagramChannelImpl(SelectorProvider var1) throws IOException {
      super(var1);
      ResourceManager.beforeUdpCreate();

      try {
         this.family = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
         this.fd = Net.socket(this.family, false);
         this.fdVal = IOUtil.fdVal(this.fd);
         this.state = 0;
      } catch (IOException var3) {
         ResourceManager.afterUdpClose();
         throw var3;
      }
   }

   public DatagramChannelImpl(SelectorProvider var1, ProtocolFamily var2) throws IOException {
      super(var1);
      if (var2 != StandardProtocolFamily.INET && var2 != StandardProtocolFamily.INET6) {
         if (var2 == null) {
            throw new NullPointerException("'family' is null");
         } else {
            throw new UnsupportedOperationException("Protocol family not supported");
         }
      } else if (var2 == StandardProtocolFamily.INET6 && !Net.isIPv6Available()) {
         throw new UnsupportedOperationException("IPv6 not available");
      } else {
         this.family = var2;
         this.fd = Net.socket(var2, false);
         this.fdVal = IOUtil.fdVal(this.fd);
         this.state = 0;
      }
   }

   public DatagramChannelImpl(SelectorProvider var1, FileDescriptor var2) throws IOException {
      super(var1);
      this.family = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
      this.fd = var2;
      this.fdVal = IOUtil.fdVal(var2);
      this.state = 0;
      this.localAddress = Net.localAddress(var2);
   }

   public DatagramSocket socket() {
      synchronized(this.stateLock) {
         if (this.socket == null) {
            this.socket = DatagramSocketAdaptor.create(this);
         }

         return this.socket;
      }
   }

   public SocketAddress getLocalAddress() throws IOException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else {
            return Net.getRevealedLocalAddress(this.localAddress);
         }
      }
   }

   public SocketAddress getRemoteAddress() throws IOException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else {
            return this.remoteAddress;
         }
      }
   }

   public <T> DatagramChannel setOption(SocketOption<T> var1, T var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         synchronized(this.stateLock) {
            this.ensureOpen();
            if (var1 != StandardSocketOptions.IP_TOS && var1 != StandardSocketOptions.IP_MULTICAST_TTL && var1 != StandardSocketOptions.IP_MULTICAST_LOOP) {
               if (var1 == StandardSocketOptions.IP_MULTICAST_IF) {
                  if (var2 == null) {
                     throw new IllegalArgumentException("Cannot set IP_MULTICAST_IF to 'null'");
                  } else {
                     NetworkInterface var4 = (NetworkInterface)var2;
                     if (this.family == StandardProtocolFamily.INET6) {
                        int var5 = var4.getIndex();
                        if (var5 == -1) {
                           throw new IOException("Network interface cannot be identified");
                        }

                        Net.setInterface6(this.fd, var5);
                     } else {
                        Inet4Address var9 = Net.anyInet4Address(var4);
                        if (var9 == null) {
                           throw new IOException("Network interface not configured for IPv4");
                        }

                        int var6 = Net.inet4AsInt(var9);
                        Net.setInterface4(this.fd, var6);
                     }

                     return this;
                  }
               } else {
                  if (var1 == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind() && this.localAddress != null) {
                     this.reuseAddressEmulated = true;
                     this.isReuseAddress = (Boolean)var2;
                  }

                  Net.setSocketOption(this.fd, Net.UNSPEC, var1, var2);
                  return this;
               }
            } else {
               Net.setSocketOption(this.fd, this.family, var1, var2);
               return this;
            }
         }
      }
   }

   public <T> T getOption(SocketOption<T> var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         synchronized(this.stateLock) {
            this.ensureOpen();
            if (var1 != StandardSocketOptions.IP_TOS && var1 != StandardSocketOptions.IP_MULTICAST_TTL && var1 != StandardSocketOptions.IP_MULTICAST_LOOP) {
               if (var1 == StandardSocketOptions.IP_MULTICAST_IF) {
                  int var3;
                  if (this.family == StandardProtocolFamily.INET) {
                     var3 = Net.getInterface4(this.fd);
                     if (var3 == 0) {
                        return null;
                     } else {
                        InetAddress var8 = Net.inet4FromInt(var3);
                        NetworkInterface var5 = NetworkInterface.getByInetAddress(var8);
                        if (var5 == null) {
                           throw new IOException("Unable to map address to interface");
                        } else {
                           return var5;
                        }
                     }
                  } else {
                     var3 = Net.getInterface6(this.fd);
                     if (var3 == 0) {
                        return null;
                     } else {
                        NetworkInterface var4 = NetworkInterface.getByIndex(var3);
                        if (var4 == null) {
                           throw new IOException("Unable to map index to interface");
                        } else {
                           return var4;
                        }
                     }
                  }
               } else {
                  return var1 == StandardSocketOptions.SO_REUSEADDR && this.reuseAddressEmulated ? this.isReuseAddress : Net.getSocketOption(this.fd, Net.UNSPEC, var1);
               }
            } else {
               return Net.getSocketOption(this.fd, this.family, var1);
            }
         }
      }
   }

   public final Set<SocketOption<?>> supportedOptions() {
      return DatagramChannelImpl.DefaultOptionsHolder.defaultOptions;
   }

   private void ensureOpen() throws ClosedChannelException {
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      }
   }

   public SocketAddress receive(ByteBuffer var1) throws IOException {
      if (var1.isReadOnly()) {
         throw new IllegalArgumentException("Read-only buffer");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(this.readLock) {
            this.ensureOpen();
            if (this.localAddress() == null) {
               this.bind((SocketAddress)null);
            }

            int var3 = 0;
            ByteBuffer var4 = null;

            try {
               this.begin();
               SecurityManager var5;
               if (!this.isOpen()) {
                  var5 = null;
                  return var5;
               } else {
                  var5 = System.getSecurityManager();
                  this.readerThread = NativeThread.current();
                  InetSocketAddress var6;
                  if (!this.isConnected() && var5 != null) {
                     var4 = Util.getTemporaryDirectBuffer(var1.remaining());

                     while(true) {
                        var3 = this.receive(this.fd, var4);
                        if (var3 != -3 || !this.isOpen()) {
                           if (var3 == -2) {
                              var6 = null;
                              return var6;
                           }

                           var6 = (InetSocketAddress)this.sender;

                           try {
                              var5.checkAccept(var6.getAddress().getHostAddress(), var6.getPort());
                           } catch (SecurityException var13) {
                              var4.clear();
                              boolean var16 = false;
                              continue;
                           }

                           var4.flip();
                           var1.put(var4);
                           break;
                        }
                     }
                  } else {
                     do {
                        var3 = this.receive(this.fd, var1);
                     } while(var3 == -3 && this.isOpen());

                     if (var3 == -2) {
                        var6 = null;
                        return var6;
                     }
                  }

                  SocketAddress var17 = this.sender;
                  return var17;
               }
            } finally {
               if (var4 != null) {
                  Util.releaseTemporaryDirectBuffer(var4);
               }

               this.readerThread = 0L;
               this.end(var3 > 0 || var3 == -2);

               assert IOStatus.check(var3);

            }
         }
      }
   }

   private int receive(FileDescriptor var1, ByteBuffer var2) throws IOException {
      int var3 = var2.position();
      int var4 = var2.limit();

      assert var3 <= var4;

      int var5 = var3 <= var4 ? var4 - var3 : 0;
      if (var2 instanceof DirectBuffer && var5 > 0) {
         return this.receiveIntoNativeBuffer(var1, var2, var5, var3);
      } else {
         int var6 = Math.max(var5, 1);
         ByteBuffer var7 = Util.getTemporaryDirectBuffer(var6);

         int var9;
         try {
            int var8 = this.receiveIntoNativeBuffer(var1, var7, var6, 0);
            var7.flip();
            if (var8 > 0 && var5 > 0) {
               var2.put(var7);
            }

            var9 = var8;
         } finally {
            Util.releaseTemporaryDirectBuffer(var7);
         }

         return var9;
      }
   }

   private int receiveIntoNativeBuffer(FileDescriptor var1, ByteBuffer var2, int var3, int var4) throws IOException {
      int var5 = this.receive0(var1, ((DirectBuffer)var2).address() + (long)var4, var3, this.isConnected());
      if (var5 > 0) {
         var2.position(var4 + var5);
      }

      return var5;
   }

   public int send(ByteBuffer var1, SocketAddress var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(this.writeLock) {
            this.ensureOpen();
            InetSocketAddress var4 = Net.checkAddress(var2);
            InetAddress var5 = var4.getAddress();
            if (var5 == null) {
               throw new IOException("Target address not resolved");
            } else {
               synchronized(this.stateLock) {
                  if (this.isConnected()) {
                     if (!var2.equals(this.remoteAddress)) {
                        throw new IllegalArgumentException("Connected address not equal to target address");
                     }

                     int var10000 = this.write(var1);
                     return var10000;
                  }

                  if (var2 == null) {
                     throw new NullPointerException();
                  }

                  SecurityManager var7 = System.getSecurityManager();
                  if (var7 != null) {
                     if (var5.isMulticastAddress()) {
                        var7.checkMulticast(var5);
                     } else {
                        var7.checkConnect(var5.getHostAddress(), var4.getPort());
                     }
                  }
               }

               int var6 = 0;

               byte var20;
               try {
                  this.begin();
                  if (this.isOpen()) {
                     this.writerThread = NativeThread.current();

                     do {
                        var6 = this.send(this.fd, var1, var4);
                     } while(var6 == -3 && this.isOpen());

                     synchronized(this.stateLock) {
                        if (this.isOpen() && this.localAddress == null) {
                           this.localAddress = Net.localAddress(this.fd);
                        }
                     }

                     int var21 = IOStatus.normalize(var6);
                     return var21;
                  }

                  var20 = 0;
               } finally {
                  this.writerThread = 0L;
                  this.end(var6 > 0 || var6 == -2);

                  assert IOStatus.check(var6);

               }

               return var20;
            }
         }
      }
   }

   private int send(FileDescriptor var1, ByteBuffer var2, InetSocketAddress var3) throws IOException {
      if (var2 instanceof DirectBuffer) {
         return this.sendFromNativeBuffer(var1, var2, var3);
      } else {
         int var4 = var2.position();
         int var5 = var2.limit();

         assert var4 <= var5;

         int var6 = var4 <= var5 ? var5 - var4 : 0;
         ByteBuffer var7 = Util.getTemporaryDirectBuffer(var6);

         int var9;
         try {
            var7.put(var2);
            var7.flip();
            var2.position(var4);
            int var8 = this.sendFromNativeBuffer(var1, var7, var3);
            if (var8 > 0) {
               var2.position(var4 + var8);
            }

            var9 = var8;
         } finally {
            Util.releaseTemporaryDirectBuffer(var7);
         }

         return var9;
      }
   }

   private int sendFromNativeBuffer(FileDescriptor var1, ByteBuffer var2, InetSocketAddress var3) throws IOException {
      int var4 = var2.position();
      int var5 = var2.limit();

      assert var4 <= var5;

      int var6 = var4 <= var5 ? var5 - var4 : 0;
      boolean var7 = this.family != StandardProtocolFamily.INET;

      int var8;
      try {
         var8 = this.send0(var7, var1, ((DirectBuffer)var2).address() + (long)var4, var6, var3.getAddress(), var3.getPort());
      } catch (PortUnreachableException var10) {
         if (this.isConnected()) {
            throw var10;
         }

         var8 = var6;
      }

      if (var8 > 0) {
         var2.position(var4 + var8);
      }

      return var8;
   }

   public int read(ByteBuffer var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(this.readLock) {
            synchronized(this.stateLock) {
               this.ensureOpen();
               if (!this.isConnected()) {
                  throw new NotYetConnectedException();
               }
            }

            int var3 = 0;

            byte var4;
            try {
               this.begin();
               if (this.isOpen()) {
                  this.readerThread = NativeThread.current();

                  do {
                     var3 = IOUtil.read(this.fd, var1, -1L, nd);
                  } while(var3 == -3 && this.isOpen());

                  int var13 = IOStatus.normalize(var3);
                  return var13;
               }

               var4 = 0;
            } finally {
               this.readerThread = 0L;
               this.end(var3 > 0 || var3 == -2);

               assert IOStatus.check(var3);

            }

            return var4;
         }
      }
   }

   public long read(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         synchronized(this.readLock) {
            synchronized(this.stateLock) {
               this.ensureOpen();
               if (!this.isConnected()) {
                  throw new NotYetConnectedException();
               }
            }

            long var5 = 0L;

            try {
               this.begin();
               long var7;
               if (!this.isOpen()) {
                  var7 = 0L;
                  return var7;
               } else {
                  this.readerThread = NativeThread.current();

                  do {
                     var5 = IOUtil.read(this.fd, var1, var2, var3, nd);
                  } while(var5 == -3L && this.isOpen());

                  var7 = IOStatus.normalize(var5);
                  return var7;
               }
            } finally {
               this.readerThread = 0L;
               this.end(var5 > 0L || var5 == -2L);

               assert IOStatus.check(var5);

            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int write(ByteBuffer var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(this.writeLock) {
            synchronized(this.stateLock) {
               this.ensureOpen();
               if (!this.isConnected()) {
                  throw new NotYetConnectedException();
               }
            }

            int var3 = 0;

            try {
               this.begin();
               if (!this.isOpen()) {
                  byte var13 = 0;
                  return var13;
               } else {
                  this.writerThread = NativeThread.current();

                  do {
                     var3 = IOUtil.write(this.fd, var1, -1L, nd);
                  } while(var3 == -3 && this.isOpen());

                  int var4 = IOStatus.normalize(var3);
                  return var4;
               }
            } finally {
               this.writerThread = 0L;
               this.end(var3 > 0 || var3 == -2);

               assert IOStatus.check(var3);

            }
         }
      }
   }

   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         synchronized(this.writeLock) {
            synchronized(this.stateLock) {
               this.ensureOpen();
               if (!this.isConnected()) {
                  throw new NotYetConnectedException();
               }
            }

            long var5 = 0L;

            long var7;
            try {
               this.begin();
               if (this.isOpen()) {
                  this.writerThread = NativeThread.current();

                  do {
                     var5 = IOUtil.write(this.fd, var1, var2, var3, nd);
                  } while(var5 == -3L && this.isOpen());

                  var7 = IOStatus.normalize(var5);
                  return var7;
               }

               var7 = 0L;
            } finally {
               this.writerThread = 0L;
               this.end(var5 > 0L || var5 == -2L);

               assert IOStatus.check(var5);

            }

            return var7;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected void implConfigureBlocking(boolean var1) throws IOException {
      IOUtil.configureBlocking(this.fd, var1);
   }

   public SocketAddress localAddress() {
      synchronized(this.stateLock) {
         return this.localAddress;
      }
   }

   public SocketAddress remoteAddress() {
      synchronized(this.stateLock) {
         return this.remoteAddress;
      }
   }

   public DatagramChannel bind(SocketAddress var1) throws IOException {
      synchronized(this.readLock) {
         synchronized(this.writeLock) {
            synchronized(this.stateLock) {
               this.ensureOpen();
               if (this.localAddress != null) {
                  throw new AlreadyBoundException();
               }

               InetSocketAddress var5;
               if (var1 == null) {
                  if (this.family == StandardProtocolFamily.INET) {
                     var5 = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0);
                  } else {
                     var5 = new InetSocketAddress(0);
                  }
               } else {
                  var5 = Net.checkAddress(var1);
                  if (this.family == StandardProtocolFamily.INET) {
                     InetAddress var6 = var5.getAddress();
                     if (!(var6 instanceof Inet4Address)) {
                        throw new UnsupportedAddressTypeException();
                     }
                  }
               }

               SecurityManager var13 = System.getSecurityManager();
               if (var13 != null) {
                  var13.checkListen(var5.getPort());
               }

               Net.bind(this.family, this.fd, var5.getAddress(), var5.getPort());
               this.localAddress = Net.localAddress(this.fd);
            }
         }

         return this;
      }
   }

   public boolean isConnected() {
      synchronized(this.stateLock) {
         return this.state == 1;
      }
   }

   void ensureOpenAndUnconnected() throws IOException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (this.state != 0) {
            throw new IllegalStateException("Connect already invoked");
         }
      }
   }

   public DatagramChannel connect(SocketAddress var1) throws IOException {
      boolean var2 = false;
      synchronized(this.readLock) {
         synchronized(this.writeLock) {
            synchronized(this.stateLock) {
               this.ensureOpenAndUnconnected();
               InetSocketAddress var6 = Net.checkAddress(var1);
               SecurityManager var7 = System.getSecurityManager();
               if (var7 != null) {
                  var7.checkConnect(var6.getAddress().getHostAddress(), var6.getPort());
               }

               int var8 = Net.connect(this.family, this.fd, var6.getAddress(), var6.getPort());
               if (var8 <= 0) {
                  throw new Error();
               }

               this.state = 1;
               this.remoteAddress = var6;
               this.sender = var6;
               this.cachedSenderInetAddress = var6.getAddress();
               this.cachedSenderPort = var6.getPort();
               this.localAddress = Net.localAddress(this.fd);
               boolean var9 = false;
               synchronized(this.blockingLock()) {
                  try {
                     var9 = this.isBlocking();
                     ByteBuffer var11 = ByteBuffer.allocate(1);
                     if (var9) {
                        this.configureBlocking(false);
                     }

                     do {
                        var11.clear();
                     } while(this.receive(var11) != null);
                  } finally {
                     if (var9) {
                        this.configureBlocking(true);
                     }

                  }
               }
            }
         }

         return this;
      }
   }

   public DatagramChannel disconnect() throws IOException {
      synchronized(this.readLock) {
         DatagramChannelImpl var10000;
         synchronized(this.writeLock) {
            synchronized(this.stateLock) {
               if (!this.isConnected() || !this.isOpen()) {
                  var10000 = this;
               } else {
                  InetSocketAddress var4 = this.remoteAddress;
                  SecurityManager var5 = System.getSecurityManager();
                  if (var5 != null) {
                     var5.checkConnect(var4.getAddress().getHostAddress(), var4.getPort());
                  }

                  boolean var6 = this.family == StandardProtocolFamily.INET6;
                  disconnect0(this.fd, var6);
                  this.remoteAddress = null;
                  this.state = 0;
                  this.localAddress = Net.localAddress(this.fd);
                  return this;
               }
            }
         }

         return var10000;
      }
   }

   private MembershipKey innerJoin(InetAddress var1, NetworkInterface var2, InetAddress var3) throws IOException {
      if (!var1.isMulticastAddress()) {
         throw new IllegalArgumentException("Group not a multicast address");
      } else {
         if (var1 instanceof Inet4Address) {
            if (this.family == StandardProtocolFamily.INET6 && !Net.canIPv6SocketJoinIPv4Group()) {
               throw new IllegalArgumentException("IPv6 socket cannot join IPv4 multicast group");
            }
         } else {
            if (!(var1 instanceof Inet6Address)) {
               throw new IllegalArgumentException("Address type not supported");
            }

            if (this.family != StandardProtocolFamily.INET6) {
               throw new IllegalArgumentException("Only IPv6 sockets can join IPv6 multicast group");
            }
         }

         if (var3 != null) {
            if (var3.isAnyLocalAddress()) {
               throw new IllegalArgumentException("Source address is a wildcard address");
            }

            if (var3.isMulticastAddress()) {
               throw new IllegalArgumentException("Source address is multicast address");
            }

            if (var3.getClass() != var1.getClass()) {
               throw new IllegalArgumentException("Source address is different type to group");
            }
         }

         SecurityManager var4 = System.getSecurityManager();
         if (var4 != null) {
            var4.checkMulticast(var1);
         }

         synchronized(this.stateLock) {
            if (!this.isOpen()) {
               throw new ClosedChannelException();
            } else {
               if (this.registry == null) {
                  this.registry = new MembershipRegistry();
               } else {
                  MembershipKey var6 = this.registry.checkMembership(var1, var2, var3);
                  if (var6 != null) {
                     return var6;
                  }
               }

               int var10;
               Object var14;
               if (this.family != StandardProtocolFamily.INET6 || !(var1 instanceof Inet6Address) && !Net.canJoin6WithIPv4Group()) {
                  Inet4Address var15 = Net.anyInet4Address(var2);
                  if (var15 == null) {
                     throw new IOException("Network interface not configured for IPv4");
                  }

                  int var16 = Net.inet4AsInt(var1);
                  int var17 = Net.inet4AsInt(var15);
                  var10 = var3 == null ? 0 : Net.inet4AsInt(var3);
                  int var11 = Net.join4(this.fd, var16, var17, var10);
                  if (var11 == -2) {
                     throw new UnsupportedOperationException();
                  }

                  var14 = new MembershipKeyImpl.Type4(this, var1, var2, var3, var16, var17, var10);
               } else {
                  int var7 = var2.getIndex();
                  if (var7 == -1) {
                     throw new IOException("Network interface cannot be identified");
                  }

                  byte[] var8 = Net.inet6AsByteArray(var1);
                  byte[] var9 = var3 == null ? null : Net.inet6AsByteArray(var3);
                  var10 = Net.join6(this.fd, var8, var7, var9);
                  if (var10 == -2) {
                     throw new UnsupportedOperationException();
                  }

                  var14 = new MembershipKeyImpl.Type6(this, var1, var2, var3, var8, var7, var9);
               }

               this.registry.add((MembershipKeyImpl)var14);
               return (MembershipKey)var14;
            }
         }
      }
   }

   public MembershipKey join(InetAddress var1, NetworkInterface var2) throws IOException {
      return this.innerJoin(var1, var2, (InetAddress)null);
   }

   public MembershipKey join(InetAddress var1, NetworkInterface var2, InetAddress var3) throws IOException {
      if (var3 == null) {
         throw new NullPointerException("source address is null");
      } else {
         return this.innerJoin(var1, var2, var3);
      }
   }

   void drop(MembershipKeyImpl var1) {
      assert var1.channel() == this;

      synchronized(this.stateLock) {
         if (var1.isValid()) {
            try {
               if (var1 instanceof MembershipKeyImpl.Type6) {
                  MembershipKeyImpl.Type6 var3 = (MembershipKeyImpl.Type6)var1;
                  Net.drop6(this.fd, var3.groupAddress(), var3.index(), var3.source());
               } else {
                  MembershipKeyImpl.Type4 var7 = (MembershipKeyImpl.Type4)var1;
                  Net.drop4(this.fd, var7.groupAddress(), var7.interfaceAddress(), var7.source());
               }
            } catch (IOException var5) {
               throw new AssertionError(var5);
            }

            var1.invalidate();
            this.registry.remove(var1);
         }
      }
   }

   void block(MembershipKeyImpl var1, InetAddress var2) throws IOException {
      assert var1.channel() == this;

      assert var1.sourceAddress() == null;

      synchronized(this.stateLock) {
         if (!var1.isValid()) {
            throw new IllegalStateException("key is no longer valid");
         } else if (var2.isAnyLocalAddress()) {
            throw new IllegalArgumentException("Source address is a wildcard address");
         } else if (var2.isMulticastAddress()) {
            throw new IllegalArgumentException("Source address is multicast address");
         } else if (var2.getClass() != var1.group().getClass()) {
            throw new IllegalArgumentException("Source address is different type to group");
         } else {
            int var4;
            if (var1 instanceof MembershipKeyImpl.Type6) {
               MembershipKeyImpl.Type6 var5 = (MembershipKeyImpl.Type6)var1;
               var4 = Net.block6(this.fd, var5.groupAddress(), var5.index(), Net.inet6AsByteArray(var2));
            } else {
               MembershipKeyImpl.Type4 var8 = (MembershipKeyImpl.Type4)var1;
               var4 = Net.block4(this.fd, var8.groupAddress(), var8.interfaceAddress(), Net.inet4AsInt(var2));
            }

            if (var4 == -2) {
               throw new UnsupportedOperationException();
            }
         }
      }
   }

   void unblock(MembershipKeyImpl var1, InetAddress var2) {
      assert var1.channel() == this;

      assert var1.sourceAddress() == null;

      synchronized(this.stateLock) {
         if (!var1.isValid()) {
            throw new IllegalStateException("key is no longer valid");
         } else {
            try {
               if (var1 instanceof MembershipKeyImpl.Type6) {
                  MembershipKeyImpl.Type6 var4 = (MembershipKeyImpl.Type6)var1;
                  Net.unblock6(this.fd, var4.groupAddress(), var4.index(), Net.inet6AsByteArray(var2));
               } else {
                  MembershipKeyImpl.Type4 var8 = (MembershipKeyImpl.Type4)var1;
                  Net.unblock4(this.fd, var8.groupAddress(), var8.interfaceAddress(), Net.inet4AsInt(var2));
               }
            } catch (IOException var6) {
               throw new AssertionError(var6);
            }

         }
      }
   }

   protected void implCloseSelectableChannel() throws IOException {
      synchronized(this.stateLock) {
         if (this.state != 2) {
            nd.preClose(this.fd);
         }

         ResourceManager.afterUdpClose();
         if (this.registry != null) {
            this.registry.invalidateAll();
         }

         long var2;
         if ((var2 = this.readerThread) != 0L) {
            NativeThread.signal(var2);
         }

         if ((var2 = this.writerThread) != 0L) {
            NativeThread.signal(var2);
         }

         if (!this.isRegistered()) {
            this.kill();
         }

      }
   }

   public void kill() throws IOException {
      synchronized(this.stateLock) {
         if (this.state != 2) {
            if (this.state == -1) {
               this.state = 2;
            } else {
               assert !this.isOpen() && !this.isRegistered();

               nd.close(this.fd);
               this.state = 2;
            }
         }
      }
   }

   protected void finalize() throws IOException {
      if (this.fd != null) {
         this.close();
      }

   }

   public boolean translateReadyOps(int var1, int var2, SelectionKeyImpl var3) {
      int var4 = var3.nioInterestOps();
      int var5 = var3.nioReadyOps();
      int var6 = var2;
      if ((var1 & Net.POLLNVAL) != 0) {
         return false;
      } else if ((var1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
         var3.nioReadyOps(var4);
         return (var4 & ~var5) != 0;
      } else {
         if ((var1 & Net.POLLIN) != 0 && (var4 & 1) != 0) {
            var6 = var2 | 1;
         }

         if ((var1 & Net.POLLOUT) != 0 && (var4 & 4) != 0) {
            var6 |= 4;
         }

         var3.nioReadyOps(var6);
         return (var6 & ~var5) != 0;
      }
   }

   public boolean translateAndUpdateReadyOps(int var1, SelectionKeyImpl var2) {
      return this.translateReadyOps(var1, var2.nioReadyOps(), var2);
   }

   public boolean translateAndSetReadyOps(int var1, SelectionKeyImpl var2) {
      return this.translateReadyOps(var1, 0, var2);
   }

   int poll(int var1, long var2) throws IOException {
      assert Thread.holdsLock(this.blockingLock()) && !this.isBlocking();

      synchronized(this.readLock) {
         int var5 = 0;

         try {
            this.begin();
            synchronized(this.stateLock) {
               if (!this.isOpen()) {
                  byte var7 = 0;
                  return var7;
               }

               this.readerThread = NativeThread.current();
            }

            var5 = Net.poll(this.fd, var1, var2);
         } finally {
            this.readerThread = 0L;
            this.end(var5 > 0);
         }

         return var5;
      }
   }

   public void translateAndSetInterestOps(int var1, SelectionKeyImpl var2) {
      int var3 = 0;
      if ((var1 & 1) != 0) {
         var3 |= Net.POLLIN;
      }

      if ((var1 & 4) != 0) {
         var3 |= Net.POLLOUT;
      }

      if ((var1 & 8) != 0) {
         var3 |= Net.POLLIN;
      }

      var2.selector.putEventOps(var2, var3);
   }

   public FileDescriptor getFD() {
      return this.fd;
   }

   public int getFDVal() {
      return this.fdVal;
   }

   private static native void initIDs();

   private static native void disconnect0(FileDescriptor var0, boolean var1) throws IOException;

   private native int receive0(FileDescriptor var1, long var2, int var4, boolean var5) throws IOException;

   private native int send0(boolean var1, FileDescriptor var2, long var3, int var5, InetAddress var6, int var7) throws IOException;

   static {
      IOUtil.load();
      initIDs();
   }

   private static class DefaultOptionsHolder {
      static final Set<SocketOption<?>> defaultOptions = defaultOptions();

      private static Set<SocketOption<?>> defaultOptions() {
         HashSet var0 = new HashSet(8);
         var0.add(StandardSocketOptions.SO_SNDBUF);
         var0.add(StandardSocketOptions.SO_RCVBUF);
         var0.add(StandardSocketOptions.SO_REUSEADDR);
         var0.add(StandardSocketOptions.SO_BROADCAST);
         var0.add(StandardSocketOptions.IP_TOS);
         var0.add(StandardSocketOptions.IP_MULTICAST_IF);
         var0.add(StandardSocketOptions.IP_MULTICAST_TTL);
         var0.add(StandardSocketOptions.IP_MULTICAST_LOOP);
         if (ExtendedOptionsImpl.flowSupported()) {
            var0.add(ExtendedSocketOptions.SO_FLOW_SLA);
         }

         return Collections.unmodifiableSet(var0);
      }
   }
}
