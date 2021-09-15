package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.NetHooks;

class SocketChannelImpl extends SocketChannel implements SelChImpl {
   private static NativeDispatcher nd;
   private final FileDescriptor fd;
   private final int fdVal;
   private volatile long readerThread = 0L;
   private volatile long writerThread = 0L;
   private final Object readLock = new Object();
   private final Object writeLock = new Object();
   private final Object stateLock = new Object();
   private boolean isReuseAddress;
   private static final int ST_UNINITIALIZED = -1;
   private static final int ST_UNCONNECTED = 0;
   private static final int ST_PENDING = 1;
   private static final int ST_CONNECTED = 2;
   private static final int ST_KILLPENDING = 3;
   private static final int ST_KILLED = 4;
   private int state = -1;
   private InetSocketAddress localAddress;
   private InetSocketAddress remoteAddress;
   private boolean isInputOpen = true;
   private boolean isOutputOpen = true;
   private boolean readyToConnect = false;
   private Socket socket;

   SocketChannelImpl(SelectorProvider var1) throws IOException {
      super(var1);
      this.fd = Net.socket(true);
      this.fdVal = IOUtil.fdVal(this.fd);
      this.state = 0;
   }

   SocketChannelImpl(SelectorProvider var1, FileDescriptor var2, boolean var3) throws IOException {
      super(var1);
      this.fd = var2;
      this.fdVal = IOUtil.fdVal(var2);
      this.state = 0;
      if (var3) {
         this.localAddress = Net.localAddress(var2);
      }

   }

   SocketChannelImpl(SelectorProvider var1, FileDescriptor var2, InetSocketAddress var3) throws IOException {
      super(var1);
      this.fd = var2;
      this.fdVal = IOUtil.fdVal(var2);
      this.state = 2;
      this.localAddress = Net.localAddress(var2);
      this.remoteAddress = var3;
   }

   public Socket socket() {
      synchronized(this.stateLock) {
         if (this.socket == null) {
            this.socket = SocketAdaptor.create(this);
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

   public <T> SocketChannel setOption(SocketOption<T> var1, T var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         synchronized(this.stateLock) {
            if (!this.isOpen()) {
               throw new ClosedChannelException();
            } else if (var1 == StandardSocketOptions.IP_TOS) {
               StandardProtocolFamily var4 = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
               Net.setSocketOption(this.fd, var4, var1, var2);
               return this;
            } else if (var1 == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
               this.isReuseAddress = (Boolean)var2;
               return this;
            } else {
               Net.setSocketOption(this.fd, Net.UNSPEC, var1, var2);
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
            if (!this.isOpen()) {
               throw new ClosedChannelException();
            } else if (var1 == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
               return this.isReuseAddress;
            } else if (var1 == StandardSocketOptions.IP_TOS) {
               StandardProtocolFamily var3 = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
               return Net.getSocketOption(this.fd, var3, var1);
            } else {
               return Net.getSocketOption(this.fd, Net.UNSPEC, var1);
            }
         }
      }
   }

   public final Set<SocketOption<?>> supportedOptions() {
      return SocketChannelImpl.DefaultOptionsHolder.defaultOptions;
   }

   private boolean ensureReadOpen() throws ClosedChannelException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (!this.isConnected()) {
            throw new NotYetConnectedException();
         } else {
            return this.isInputOpen;
         }
      }
   }

   private void ensureWriteOpen() throws ClosedChannelException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (!this.isOutputOpen) {
            throw new ClosedChannelException();
         } else if (!this.isConnected()) {
            throw new NotYetConnectedException();
         }
      }
   }

   private void readerCleanup() throws IOException {
      synchronized(this.stateLock) {
         this.readerThread = 0L;
         if (this.state == 3) {
            this.kill();
         }

      }
   }

   private void writerCleanup() throws IOException {
      synchronized(this.stateLock) {
         this.writerThread = 0L;
         if (this.state == 3) {
            this.kill();
         }

      }
   }

   public int read(ByteBuffer var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(this.readLock) {
            if (!this.ensureReadOpen()) {
               return -1;
            } else {
               int var3 = 0;
               boolean var20 = false;

               byte var10000;
               int var4;
               label354: {
                  byte var5;
                  try {
                     label360: {
                        var20 = true;
                        this.begin();
                        synchronized(this.stateLock) {
                           if (!this.isOpen()) {
                              var5 = 0;
                              var20 = false;
                              break label360;
                           }

                           this.readerThread = NativeThread.current();
                        }

                        do {
                           var3 = IOUtil.read(this.fd, var1, -1L, nd);
                        } while(var3 == -3 && this.isOpen());

                        var4 = IOStatus.normalize(var3);
                        var20 = false;
                        break label354;
                     }
                  } finally {
                     if (var20) {
                        label271: {
                           this.readerCleanup();
                           this.end(var3 > 0 || var3 == -2);
                           synchronized(this.stateLock) {
                              if (var3 > 0 || this.isInputOpen) {
                                 break label271;
                              }

                              var10000 = -1;
                           }

                           return var10000;
                        }

                        assert IOStatus.check(var3);

                     }
                  }

                  label287: {
                     this.readerCleanup();
                     this.end(var3 > 0 || var3 == -2);
                     synchronized(this.stateLock) {
                        if (var3 > 0 || this.isInputOpen) {
                           break label287;
                        }

                        var10000 = -1;
                     }

                     return var10000;
                  }

                  assert IOStatus.check(var3);

                  return var5;
               }

               label303: {
                  this.readerCleanup();
                  this.end(var3 > 0 || var3 == -2);
                  synchronized(this.stateLock) {
                     if (var3 > 0 || this.isInputOpen) {
                        break label303;
                     }

                     var10000 = -1;
                  }

                  return var10000;
               }

               assert IOStatus.check(var3);

               return var4;
            }
         }
      }
   }

   public long read(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         synchronized(this.readLock) {
            if (!this.ensureReadOpen()) {
               return -1L;
            } else {
               long var5 = 0L;
               boolean var24 = false;

               long var10000;
               long var8;
               label376: {
                  long var7;
                  try {
                     var24 = true;
                     this.begin();
                     synchronized(this.stateLock) {
                        if (!this.isOpen()) {
                           var8 = 0L;
                           var24 = false;
                           break label376;
                        }

                        this.readerThread = NativeThread.current();
                     }

                     while(true) {
                        var5 = IOUtil.read(this.fd, var1, var2, var3, nd);
                        if (var5 != -3L || !this.isOpen()) {
                           var7 = IOStatus.normalize(var5);
                           var24 = false;
                           break;
                        }
                     }
                  } finally {
                     if (var24) {
                        this.readerCleanup();
                        this.end(var5 > 0L || var5 == -2L);
                        synchronized(this.stateLock) {
                           if (var5 <= 0L && !this.isInputOpen) {
                              var10000 = -1L;
                              return var10000;
                           }
                        }

                        assert IOStatus.check(var5);

                     }
                  }

                  this.readerCleanup();
                  this.end(var5 > 0L || var5 == -2L);
                  synchronized(this.stateLock) {
                     if (var5 <= 0L && !this.isInputOpen) {
                        var10000 = -1L;
                        return var10000;
                     }
                  }

                  assert IOStatus.check(var5);

                  return var7;
               }

               this.readerCleanup();
               this.end(var5 > 0L || var5 == -2L);
               synchronized(this.stateLock) {
                  if (var5 <= 0L && !this.isInputOpen) {
                     var10000 = -1L;
                     return var10000;
                  }
               }

               assert IOStatus.check(var5);

               return var8;
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
            this.ensureWriteOpen();
            int var3 = 0;
            boolean var20 = false;

            byte var5;
            label310: {
               int var4;
               try {
                  var20 = true;
                  this.begin();
                  synchronized(this.stateLock) {
                     if (!this.isOpen()) {
                        var5 = 0;
                        var20 = false;
                        break label310;
                     }

                     this.writerThread = NativeThread.current();
                  }

                  do {
                     var3 = IOUtil.write(this.fd, var1, -1L, nd);
                  } while(var3 == -3 && this.isOpen());

                  var4 = IOStatus.normalize(var3);
                  var20 = false;
               } finally {
                  if (var20) {
                     this.writerCleanup();
                     this.end(var3 > 0 || var3 == -2);
                     synchronized(this.stateLock) {
                        if (var3 <= 0 && !this.isOutputOpen) {
                           throw new AsynchronousCloseException();
                        }
                     }

                     assert IOStatus.check(var3);

                  }
               }

               this.writerCleanup();
               this.end(var3 > 0 || var3 == -2);
               synchronized(this.stateLock) {
                  if (var3 <= 0 && !this.isOutputOpen) {
                     throw new AsynchronousCloseException();
                  }
               }

               assert IOStatus.check(var3);

               return var4;
            }

            this.writerCleanup();
            this.end(var3 > 0 || var3 == -2);
            synchronized(this.stateLock) {
               if (var3 <= 0 && !this.isOutputOpen) {
                  throw new AsynchronousCloseException();
               }
            }

            assert IOStatus.check(var3);

            return var5;
         }
      }
   }

   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         synchronized(this.writeLock) {
            this.ensureWriteOpen();
            long var5 = 0L;
            boolean var24 = false;

            long var7;
            label319: {
               long var8;
               try {
                  label322: {
                     var24 = true;
                     this.begin();
                     synchronized(this.stateLock) {
                        if (!this.isOpen()) {
                           var8 = 0L;
                           var24 = false;
                           break label322;
                        }

                        this.writerThread = NativeThread.current();
                     }

                     do {
                        var5 = IOUtil.write(this.fd, var1, var2, var3, nd);
                     } while(var5 == -3L && this.isOpen());

                     var7 = IOStatus.normalize(var5);
                     var24 = false;
                     break label319;
                  }
               } finally {
                  if (var24) {
                     this.writerCleanup();
                     this.end(var5 > 0L || var5 == -2L);
                     synchronized(this.stateLock) {
                        if (var5 <= 0L && !this.isOutputOpen) {
                           throw new AsynchronousCloseException();
                        }
                     }

                     assert IOStatus.check(var5);

                  }
               }

               this.writerCleanup();
               this.end(var5 > 0L || var5 == -2L);
               synchronized(this.stateLock) {
                  if (var5 <= 0L && !this.isOutputOpen) {
                     throw new AsynchronousCloseException();
                  }
               }

               assert IOStatus.check(var5);

               return var8;
            }

            this.writerCleanup();
            this.end(var5 > 0L || var5 == -2L);
            synchronized(this.stateLock) {
               if (var5 <= 0L && !this.isOutputOpen) {
                  throw new AsynchronousCloseException();
               }
            }

            assert IOStatus.check(var5);

            return var7;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   int sendOutOfBandData(byte var1) throws IOException {
      synchronized(this.writeLock) {
         this.ensureWriteOpen();
         int var3 = 0;
         boolean var20 = false;

         int var4;
         label305: {
            byte var5;
            try {
               label308: {
                  var20 = true;
                  this.begin();
                  synchronized(this.stateLock) {
                     if (!this.isOpen()) {
                        var5 = 0;
                        var20 = false;
                        break label308;
                     }

                     this.writerThread = NativeThread.current();
                  }

                  do {
                     var3 = sendOutOfBandData(this.fd, var1);
                  } while(var3 == -3 && this.isOpen());

                  var4 = IOStatus.normalize(var3);
                  var20 = false;
                  break label305;
               }
            } finally {
               if (var20) {
                  this.writerCleanup();
                  this.end(var3 > 0 || var3 == -2);
                  synchronized(this.stateLock) {
                     if (var3 <= 0 && !this.isOutputOpen) {
                        throw new AsynchronousCloseException();
                     }
                  }

                  assert IOStatus.check(var3);

               }
            }

            this.writerCleanup();
            this.end(var3 > 0 || var3 == -2);
            synchronized(this.stateLock) {
               if (var3 <= 0 && !this.isOutputOpen) {
                  throw new AsynchronousCloseException();
               }
            }

            assert IOStatus.check(var3);

            return var5;
         }

         this.writerCleanup();
         this.end(var3 > 0 || var3 == -2);
         synchronized(this.stateLock) {
            if (var3 <= 0 && !this.isOutputOpen) {
               throw new AsynchronousCloseException();
            }
         }

         assert IOStatus.check(var3);

         return var4;
      }
   }

   protected void implConfigureBlocking(boolean var1) throws IOException {
      IOUtil.configureBlocking(this.fd, var1);
   }

   public InetSocketAddress localAddress() {
      synchronized(this.stateLock) {
         return this.localAddress;
      }
   }

   public SocketAddress remoteAddress() {
      synchronized(this.stateLock) {
         return this.remoteAddress;
      }
   }

   public SocketChannel bind(SocketAddress var1) throws IOException {
      synchronized(this.readLock) {
         synchronized(this.writeLock) {
            synchronized(this.stateLock) {
               if (!this.isOpen()) {
                  throw new ClosedChannelException();
               }

               if (this.state == 1) {
                  throw new ConnectionPendingException();
               }

               if (this.localAddress != null) {
                  throw new AlreadyBoundException();
               }

               InetSocketAddress var5 = var1 == null ? new InetSocketAddress(0) : Net.checkAddress(var1);
               SecurityManager var6 = System.getSecurityManager();
               if (var6 != null) {
                  var6.checkListen(var5.getPort());
               }

               NetHooks.beforeTcpBind(this.fd, var5.getAddress(), var5.getPort());
               Net.bind(this.fd, var5.getAddress(), var5.getPort());
               this.localAddress = Net.localAddress(this.fd);
            }
         }

         return this;
      }
   }

   public boolean isConnected() {
      synchronized(this.stateLock) {
         return this.state == 2;
      }
   }

   public boolean isConnectionPending() {
      synchronized(this.stateLock) {
         return this.state == 1;
      }
   }

   void ensureOpenAndUnconnected() throws IOException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (this.state == 2) {
            throw new AlreadyConnectedException();
         } else if (this.state == 1) {
            throw new ConnectionPendingException();
         }
      }
   }

   public boolean connect(SocketAddress var1) throws IOException {
      boolean var2 = false;
      synchronized(this.readLock) {
         synchronized(this.writeLock) {
            this.ensureOpenAndUnconnected();
            InetSocketAddress var5 = Net.checkAddress(var1);
            SecurityManager var6 = System.getSecurityManager();
            if (var6 != null) {
               var6.checkConnect(var5.getAddress().getHostAddress(), var5.getPort());
            }

            boolean var10000;
            synchronized(this.blockingLock()) {
               int var8 = 0;

               try {
                  try {
                     this.begin();
                     synchronized(this.stateLock) {
                        if (!this.isOpen()) {
                           boolean var10 = false;
                           return var10;
                        }

                        if (this.localAddress == null) {
                           NetHooks.beforeTcpConnect(this.fd, var5.getAddress(), var5.getPort());
                        }

                        this.readerThread = NativeThread.current();
                     }

                     do {
                        InetAddress var9 = var5.getAddress();
                        if (var9.isAnyLocalAddress()) {
                           var9 = InetAddress.getLocalHost();
                        }

                        var8 = Net.connect(this.fd, var9, var5.getPort());
                     } while(var8 == -3 && this.isOpen());
                  } finally {
                     this.readerCleanup();
                     this.end(var8 > 0 || var8 == -2);

                     assert IOStatus.check(var8);

                  }
               } catch (IOException var27) {
                  this.close();
                  throw var27;
               }

               synchronized(this.stateLock) {
                  this.remoteAddress = var5;
                  if (var8 <= 0) {
                     if (!this.isBlocking()) {
                        this.state = 1;
                     } else {
                        assert false;
                     }
                  } else {
                     this.state = 2;
                     if (this.isOpen()) {
                        this.localAddress = Net.localAddress(this.fd);
                     }

                     var10000 = true;
                     return var10000;
                  }
               }
            }

            var10000 = false;
            return var10000;
         }
      }
   }

   public boolean finishConnect() throws IOException {
      synchronized(this.readLock) {
         synchronized(this.writeLock) {
            boolean var10000;
            synchronized(this.stateLock) {
               if (!this.isOpen()) {
                  throw new ClosedChannelException();
               }

               if (this.state == 2) {
                  var10000 = true;
                  return var10000;
               }

               if (this.state != 1) {
                  throw new NoConnectionPendingException();
               }
            }

            int var3 = 0;

            try {
               label525: {
                  boolean var29 = false;

                  boolean var6;
                  label506: {
                     try {
                        var29 = true;
                        this.begin();
                        synchronized(this.blockingLock()) {
                           label480: {
                              label494: {
                                 synchronized(this.stateLock) {
                                    if (!this.isOpen()) {
                                       var6 = false;
                                       break label494;
                                    }

                                    this.readerThread = NativeThread.current();
                                 }

                                 if (!this.isBlocking()) {
                                    do {
                                       var3 = checkConnect(this.fd, false, this.readyToConnect);
                                    } while(var3 == -3 && this.isOpen());
                                 } else {
                                    do {
                                       while(true) {
                                          var3 = checkConnect(this.fd, true, this.readyToConnect);
                                          if (var3 == 0) {
                                             continue;
                                          }
                                          break;
                                       }
                                    } while(var3 == -3 && this.isOpen());
                                 }

                                 var29 = false;
                                 break label480;
                              }

                              var29 = false;
                              break label506;
                           }
                        }
                     } finally {
                        if (var29) {
                           synchronized(this.stateLock) {
                              this.readerThread = 0L;
                              if (this.state == 3) {
                                 this.kill();
                                 var3 = 0;
                              }
                           }

                           this.end(var3 > 0 || var3 == -2);

                           assert IOStatus.check(var3);

                        }
                     }

                     synchronized(this.stateLock) {
                        this.readerThread = 0L;
                        if (this.state == 3) {
                           this.kill();
                           var3 = 0;
                        }
                     }

                     this.end(var3 > 0 || var3 == -2);

                     assert IOStatus.check(var3);
                     break label525;
                  }

                  synchronized(this.stateLock) {
                     this.readerThread = 0L;
                     if (this.state == 3) {
                        this.kill();
                        var3 = 0;
                     }
                  }

                  this.end(var3 > 0 || var3 == -2);

                  assert IOStatus.check(var3);

                  return var6;
               }
            } catch (IOException var38) {
               this.close();
               throw var38;
            }

            if (var3 > 0) {
               synchronized(this.stateLock) {
                  this.state = 2;
                  if (this.isOpen()) {
                     this.localAddress = Net.localAddress(this.fd);
                  }
               }

               var10000 = true;
               return var10000;
            } else {
               var10000 = false;
               return var10000;
            }
         }
      }
   }

   public SocketChannel shutdownInput() throws IOException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (!this.isConnected()) {
            throw new NotYetConnectedException();
         } else {
            if (this.isInputOpen) {
               Net.shutdown(this.fd, 0);
               if (this.readerThread != 0L) {
                  NativeThread.signal(this.readerThread);
               }

               this.isInputOpen = false;
            }

            return this;
         }
      }
   }

   public SocketChannel shutdownOutput() throws IOException {
      synchronized(this.stateLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (!this.isConnected()) {
            throw new NotYetConnectedException();
         } else {
            if (this.isOutputOpen) {
               Net.shutdown(this.fd, 1);
               if (this.writerThread != 0L) {
                  NativeThread.signal(this.writerThread);
               }

               this.isOutputOpen = false;
            }

            return this;
         }
      }
   }

   public boolean isInputOpen() {
      synchronized(this.stateLock) {
         return this.isInputOpen;
      }
   }

   public boolean isOutputOpen() {
      synchronized(this.stateLock) {
         return this.isOutputOpen;
      }
   }

   protected void implCloseSelectableChannel() throws IOException {
      synchronized(this.stateLock) {
         this.isInputOpen = false;
         this.isOutputOpen = false;
         if (this.state != 4) {
            nd.preClose(this.fd);
         }

         if (this.readerThread != 0L) {
            NativeThread.signal(this.readerThread);
         }

         if (this.writerThread != 0L) {
            NativeThread.signal(this.writerThread);
         }

         if (!this.isRegistered()) {
            this.kill();
         }

      }
   }

   public void kill() throws IOException {
      synchronized(this.stateLock) {
         if (this.state != 4) {
            if (this.state == -1) {
               this.state = 4;
            } else {
               assert !this.isOpen() && !this.isRegistered();

               if (this.readerThread == 0L && this.writerThread == 0L) {
                  nd.close(this.fd);
                  this.state = 4;
               } else {
                  this.state = 3;
               }

            }
         }
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
         this.readyToConnect = true;
         return (var4 & ~var5) != 0;
      } else {
         if ((var1 & Net.POLLIN) != 0 && (var4 & 1) != 0 && this.state == 2) {
            var6 = var2 | 1;
         }

         if ((var1 & Net.POLLCONN) != 0 && (var4 & 8) != 0 && (this.state == 0 || this.state == 1)) {
            var6 |= 8;
            this.readyToConnect = true;
         }

         if ((var1 & Net.POLLOUT) != 0 && (var4 & 4) != 0 && this.state == 2) {
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
            this.readerCleanup();
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
         var3 |= Net.POLLCONN;
      }

      var2.selector.putEventOps(var2, var3);
   }

   public FileDescriptor getFD() {
      return this.fd;
   }

   public int getFDVal() {
      return this.fdVal;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.getClass().getSuperclass().getName());
      var1.append('[');
      if (!this.isOpen()) {
         var1.append("closed");
      } else {
         synchronized(this.stateLock) {
            switch(this.state) {
            case 0:
               var1.append("unconnected");
               break;
            case 1:
               var1.append("connection-pending");
               break;
            case 2:
               var1.append("connected");
               if (!this.isInputOpen) {
                  var1.append(" ishut");
               }

               if (!this.isOutputOpen) {
                  var1.append(" oshut");
               }
            }

            InetSocketAddress var3 = this.localAddress();
            if (var3 != null) {
               var1.append(" local=");
               var1.append(Net.getRevealedLocalAddressAsString(var3));
            }

            if (this.remoteAddress() != null) {
               var1.append(" remote=");
               var1.append(this.remoteAddress().toString());
            }
         }
      }

      var1.append(']');
      return var1.toString();
   }

   private static native int checkConnect(FileDescriptor var0, boolean var1, boolean var2) throws IOException;

   private static native int sendOutOfBandData(FileDescriptor var0, byte var1) throws IOException;

   static {
      IOUtil.load();
      nd = new SocketDispatcher();
   }

   private static class DefaultOptionsHolder {
      static final Set<SocketOption<?>> defaultOptions = defaultOptions();

      private static Set<SocketOption<?>> defaultOptions() {
         HashSet var0 = new HashSet(8);
         var0.add(StandardSocketOptions.SO_SNDBUF);
         var0.add(StandardSocketOptions.SO_RCVBUF);
         var0.add(StandardSocketOptions.SO_KEEPALIVE);
         var0.add(StandardSocketOptions.SO_REUSEADDR);
         var0.add(StandardSocketOptions.SO_LINGER);
         var0.add(StandardSocketOptions.TCP_NODELAY);
         var0.add(StandardSocketOptions.IP_TOS);
         var0.add(ExtendedSocketOption.SO_OOBINLINE);
         if (ExtendedOptionsImpl.flowSupported()) {
            var0.add(ExtendedSocketOptions.SO_FLOW_SLA);
         }

         return Collections.unmodifiableSet(var0);
      }
   }
}
