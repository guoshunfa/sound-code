package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.CachedCodeBase;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.CancelRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.SystemException;
import sun.corba.OutputStreamFactory;

public class SocketOrChannelConnectionImpl extends EventHandlerBase implements CorbaConnection, Work {
   public static boolean dprintWriteLocks = false;
   protected long enqueueTime;
   protected SocketChannel socketChannel;
   protected CorbaContactInfo contactInfo;
   protected Acceptor acceptor;
   protected ConnectionCache connectionCache;
   protected Socket socket;
   protected long timeStamp;
   protected boolean isServer;
   protected int requestId;
   protected CorbaResponseWaitingRoom responseWaitingRoom;
   protected int state;
   protected Object stateEvent;
   protected Object writeEvent;
   protected boolean writeLocked;
   protected int serverRequestCount;
   Map serverRequestMap;
   protected boolean postInitialContexts;
   protected IOR codeBaseServerIOR;
   protected CachedCodeBase cachedCodeBase;
   protected ORBUtilSystemException wrapper;
   protected ReadTimeouts readTimeouts;
   protected boolean shouldReadGiopHeaderOnly;
   protected CorbaMessageMediator partialMessageMediator;
   protected CodeSetComponentInfo.CodeSetContext codeSetContext;
   protected MessageMediator clientReply_1_1;
   protected MessageMediator serverRequest_1_1;

   public SocketChannel getSocketChannel() {
      return this.socketChannel;
   }

   protected SocketOrChannelConnectionImpl(ORB var1) {
      this.timeStamp = 0L;
      this.isServer = false;
      this.requestId = 5;
      this.stateEvent = new Object();
      this.writeEvent = new Object();
      this.serverRequestCount = 0;
      this.serverRequestMap = null;
      this.postInitialContexts = false;
      this.cachedCodeBase = new CachedCodeBase(this);
      this.partialMessageMediator = null;
      this.codeSetContext = null;
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.transport");
      this.setWork(this);
      this.responseWaitingRoom = new CorbaResponseWaitingRoomImpl(var1, this);
      this.setReadTimeouts(var1.getORBData().getTransportTCPReadTimeouts());
   }

   protected SocketOrChannelConnectionImpl(ORB var1, boolean var2, boolean var3) {
      this(var1);
      this.setUseSelectThreadToWait(var2);
      this.setUseWorkerThreadForEvent(var3);
   }

   public SocketOrChannelConnectionImpl(ORB var1, CorbaContactInfo var2, boolean var3, boolean var4, String var5, String var6, int var7) {
      this(var1, var3, var4);
      this.contactInfo = var2;

      try {
         this.socket = var1.getORBData().getSocketFactory().createSocket(var5, new InetSocketAddress(var6, var7));
         this.socketChannel = this.socket.getChannel();
         if (this.socketChannel != null) {
            boolean var8 = !var3;
            this.socketChannel.configureBlocking(var8);
         } else {
            this.setUseSelectThreadToWait(false);
         }

         if (var1.transportDebugFlag) {
            this.dprint(".initialize: connection created: " + this.socket);
         }
      } catch (Throwable var9) {
         throw this.wrapper.connectFailure((Throwable)var9, var5, var6, Integer.toString(var7));
      }

      this.state = 1;
   }

   public SocketOrChannelConnectionImpl(ORB var1, CorbaContactInfo var2, String var3, String var4, int var5) {
      this(var1, var2, var1.getORBData().connectionSocketUseSelectThreadToWait(), var1.getORBData().connectionSocketUseWorkerThreadForEvent(), var3, var4, var5);
   }

   public SocketOrChannelConnectionImpl(ORB var1, Acceptor var2, Socket var3, boolean var4, boolean var5) {
      this(var1, var4, var5);
      this.socket = var3;
      this.socketChannel = var3.getChannel();
      if (this.socketChannel != null) {
         try {
            boolean var6 = !var4;
            this.socketChannel.configureBlocking(var6);
         } catch (IOException var8) {
            RuntimeException var7 = new RuntimeException();
            var7.initCause(var8);
            throw var7;
         }
      }

      this.acceptor = var2;
      this.serverRequestMap = Collections.synchronizedMap(new HashMap());
      this.isServer = true;
      this.state = 2;
   }

   public SocketOrChannelConnectionImpl(ORB var1, Acceptor var2, Socket var3) {
      this(var1, var2, var3, var3.getChannel() == null ? false : var1.getORBData().connectionSocketUseSelectThreadToWait(), var3.getChannel() == null ? false : var1.getORBData().connectionSocketUseWorkerThreadForEvent());
   }

   public boolean shouldRegisterReadEvent() {
      return true;
   }

   public boolean shouldRegisterServerReadEvent() {
      return true;
   }

   public boolean read() {
      boolean var2;
      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".read->: " + this);
         }

         CorbaMessageMediator var1 = this.readBits();
         if (var1 != null) {
            var2 = this.dispatch(var1);
            return var2;
         }

         var2 = true;
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".read<-: " + this);
         }

      }

      return var2;
   }

   protected CorbaMessageMediator readBits() {
      try {
         Throwable var1;
         try {
            if (this.orb.transportDebugFlag) {
               this.dprint(".readBits->: " + this);
            }

            MessageMediator var15;
            if (this.contactInfo != null) {
               var15 = this.contactInfo.createMessageMediator(this.orb, this);
            } else {
               if (this.acceptor == null) {
                  throw new RuntimeException("SocketOrChannelConnectionImpl.readBits");
               }

               var15 = this.acceptor.createMessageMediator(this.orb, this);
            }

            CorbaMessageMediator var16 = (CorbaMessageMediator)var15;
            return var16;
         } catch (ThreadDeath var11) {
            ThreadDeath var14 = var11;
            if (this.orb.transportDebugFlag) {
               this.dprint(".readBits: " + this + ": ThreadDeath: " + var11, var11);
            }

            try {
               this.purgeCalls(this.wrapper.connectionAbort((Throwable)var14), false, false);
            } catch (Throwable var9) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".readBits: " + this + ": purgeCalls: Throwable: " + var9, var9);
               }
            }

            throw var11;
         } catch (Throwable var12) {
            var1 = var12;
            if (this.orb.transportDebugFlag) {
               this.dprint(".readBits: " + this + ": Throwable: " + var12, var12);
            }
         }

         try {
            if (var1 instanceof INTERNAL) {
               this.sendMessageError(GIOPVersion.DEFAULT_VERSION);
            }
         } catch (IOException var10) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".readBits: " + this + ": sendMessageError: IOException: " + var10, var10);
            }
         }

         Selector var2 = this.orb.getTransportManager().getSelector(0);
         if (var2 != null) {
            var2.unregisterForEvent(this);
         }

         this.purgeCalls(this.wrapper.connectionAbort(var12), true, false);
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".readBits<-: " + this);
         }

      }

      return null;
   }

   protected CorbaMessageMediator finishReadingBits(MessageMediator var1) {
      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".finishReadingBits->: " + this);
         }

         if (this.contactInfo != null) {
            var1 = this.contactInfo.finishCreatingMessageMediator(this.orb, this, var1);
         } else {
            if (this.acceptor == null) {
               throw new RuntimeException("SocketOrChannelConnectionImpl.finishReadingBits");
            }

            var1 = this.acceptor.finishCreatingMessageMediator(this.orb, this, var1);
         }

         CorbaMessageMediator var16 = (CorbaMessageMediator)var1;
         return var16;
      } catch (ThreadDeath var12) {
         ThreadDeath var15 = var12;
         if (this.orb.transportDebugFlag) {
            this.dprint(".finishReadingBits: " + this + ": ThreadDeath: " + var12, var12);
         }

         try {
            this.purgeCalls(this.wrapper.connectionAbort((Throwable)var15), false, false);
         } catch (Throwable var11) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".finishReadingBits: " + this + ": purgeCalls: Throwable: " + var11, var11);
            }
         }

         throw var12;
      } catch (Throwable var13) {
         Throwable var2 = var13;
         if (this.orb.transportDebugFlag) {
            this.dprint(".finishReadingBits: " + this + ": Throwable: " + var13, var13);
         }

         try {
            if (var2 instanceof INTERNAL) {
               this.sendMessageError(GIOPVersion.DEFAULT_VERSION);
            }
         } catch (IOException var10) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".finishReadingBits: " + this + ": sendMessageError: IOException: " + var10, var10);
            }
         }

         this.orb.getTransportManager().getSelector(0).unregisterForEvent(this);
         this.purgeCalls(this.wrapper.connectionAbort(var13), true, false);
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".finishReadingBits<-: " + this);
         }

      }

      return null;
   }

   protected boolean dispatch(CorbaMessageMediator var1) {
      try {
         Throwable var2;
         try {
            if (this.orb.transportDebugFlag) {
               this.dprint(".dispatch->: " + this);
            }

            boolean var16 = var1.getProtocolHandler().handleRequest(var1);
            boolean var3 = var16;
            return var3;
         } catch (ThreadDeath var12) {
            ThreadDeath var15 = var12;
            if (this.orb.transportDebugFlag) {
               this.dprint(".dispatch: ThreadDeath", var12);
            }

            try {
               this.purgeCalls(this.wrapper.connectionAbort((Throwable)var15), false, false);
            } catch (Throwable var11) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".dispatch: purgeCalls: Throwable", var11);
               }
            }

            throw var12;
         } catch (Throwable var13) {
            var2 = var13;
            if (this.orb.transportDebugFlag) {
               this.dprint(".dispatch: Throwable", var13);
            }
         }

         try {
            if (var2 instanceof INTERNAL) {
               this.sendMessageError(GIOPVersion.DEFAULT_VERSION);
            }
         } catch (IOException var10) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".dispatch: sendMessageError: IOException", var10);
            }
         }

         this.purgeCalls(this.wrapper.connectionAbort(var13), false, false);
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".dispatch<-: " + this);
         }

      }

      return true;
   }

   public boolean shouldUseDirectByteBuffers() {
      return this.getSocketChannel() != null;
   }

   public ByteBuffer read(int var1, int var2, int var3, long var4) throws IOException {
      if (this.shouldUseDirectByteBuffers()) {
         ByteBuffer var10 = this.orb.getByteBufferPool().getByteBuffer(var1);
         if (this.orb.transportDebugFlag) {
            int var11 = System.identityHashCode(var10);
            StringBuffer var8 = new StringBuffer(80);
            var8.append(".read: got ByteBuffer id (");
            var8.append(var11).append(") from ByteBufferPool.");
            String var9 = var8.toString();
            this.dprint(var9);
         }

         var10.position(var2);
         var10.limit(var1);
         this.readFully(var10, var3, var4);
         return var10;
      } else {
         byte[] var6 = new byte[var1];
         this.readFully(this.getSocket().getInputStream(), var6, var2, var3, var4);
         ByteBuffer var7 = ByteBuffer.wrap(var6);
         var7.limit(var1);
         return var7;
      }
   }

   public ByteBuffer read(ByteBuffer var1, int var2, int var3, long var4) throws IOException {
      int var6 = var2 + var3;
      if (this.shouldUseDirectByteBuffers()) {
         if (!var1.isDirect()) {
            throw this.wrapper.unexpectedNonDirectByteBufferWithChannelSocket();
         } else {
            if (var6 > var1.capacity()) {
               if (this.orb.transportDebugFlag) {
                  int var10 = System.identityHashCode(var1);
                  StringBuffer var8 = new StringBuffer(80);
                  var8.append(".read: releasing ByteBuffer id (").append(var10).append(") to ByteBufferPool.");
                  String var9 = var8.toString();
                  this.dprint(var9);
               }

               this.orb.getByteBufferPool().releaseByteBuffer(var1);
               var1 = this.orb.getByteBufferPool().getByteBuffer(var6);
            }

            var1.position(var2);
            var1.limit(var6);
            this.readFully(var1, var3, var4);
            var1.position(0);
            var1.limit(var6);
            return var1;
         }
      } else if (var1.isDirect()) {
         throw this.wrapper.unexpectedDirectByteBufferWithNonChannelSocket();
      } else {
         byte[] var7 = new byte[var6];
         this.readFully(this.getSocket().getInputStream(), var7, var2, var3, var4);
         return ByteBuffer.wrap(var7);
      }
   }

   public void readFully(ByteBuffer var1, int var2, long var3) throws IOException {
      int var5 = 0;
      boolean var6 = false;
      long var7 = (long)this.readTimeouts.get_initial_time_to_wait();
      long var9 = 0L;

      do {
         int var13 = this.getSocketChannel().read(var1);
         if (var13 < 0) {
            throw new IOException("End-of-stream");
         }

         if (var13 == 0) {
            try {
               Thread.sleep(var7);
               var9 += var7;
               var7 = (long)((double)var7 * this.readTimeouts.get_backoff_factor());
            } catch (InterruptedException var12) {
               if (this.orb.transportDebugFlag) {
                  this.dprint("readFully(): unexpected exception " + var12.toString());
               }
            }
         } else {
            var5 += var13;
         }
      } while(var5 < var2 && var9 < var3);

      if (var5 < var2 && var9 >= var3) {
         throw this.wrapper.transportReadTimeoutExceeded(new Integer(var2), new Integer(var5), new Long(var3), new Long(var9));
      } else {
         this.getConnectionCache().stampTime(this);
      }
   }

   public void readFully(InputStream var1, byte[] var2, int var3, int var4, long var5) throws IOException {
      int var7 = 0;
      boolean var8 = false;
      long var9 = (long)this.readTimeouts.get_initial_time_to_wait();
      long var11 = 0L;

      do {
         int var15 = var1.read(var2, var3 + var7, var4 - var7);
         if (var15 < 0) {
            throw new IOException("End-of-stream");
         }

         if (var15 == 0) {
            try {
               Thread.sleep(var9);
               var11 += var9;
               var9 = (long)((double)var9 * this.readTimeouts.get_backoff_factor());
            } catch (InterruptedException var14) {
               if (this.orb.transportDebugFlag) {
                  this.dprint("readFully(): unexpected exception " + var14.toString());
               }
            }
         } else {
            var7 += var15;
         }
      } while(var7 < var4 && var11 < var5);

      if (var7 < var4 && var11 >= var5) {
         throw this.wrapper.transportReadTimeoutExceeded(new Integer(var4), new Integer(var7), new Long(var5), new Long(var11));
      } else {
         this.getConnectionCache().stampTime(this);
      }
   }

   public void write(ByteBuffer var1) throws IOException {
      if (this.shouldUseDirectByteBuffers()) {
         do {
            this.getSocketChannel().write(var1);
         } while(var1.hasRemaining());
      } else {
         if (!var1.hasArray()) {
            throw this.wrapper.unexpectedDirectByteBufferWithNonChannelSocket();
         }

         byte[] var2 = var1.array();
         this.getSocket().getOutputStream().write(var2, 0, var1.limit());
         this.getSocket().getOutputStream().flush();
      }

      this.getConnectionCache().stampTime(this);
   }

   public synchronized void close() {
      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".close->: " + this);
         }

         this.writeLock();
         if (this.isBusy()) {
            this.writeUnlock();
            if (this.orb.transportDebugFlag) {
               this.dprint(".close: isBusy so no close: " + this);
            }

            return;
         }

         try {
            try {
               this.sendCloseConnection(GIOPVersion.V1_0);
            } catch (Throwable var10) {
               this.wrapper.exceptionWhenSendingCloseConnection(var10);
            }

            synchronized(this.stateEvent) {
               this.state = 3;
               this.stateEvent.notifyAll();
            }

            this.purgeCalls(this.wrapper.connectionRebind(), false, true);
         } catch (Exception var12) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".close: exception: " + this, var12);
            }
         }

         try {
            Selector var1 = this.orb.getTransportManager().getSelector(0);
            if (var1 != null) {
               var1.unregisterForEvent(this);
            }

            if (this.socketChannel != null) {
               this.socketChannel.close();
            }

            this.socket.close();
         } catch (IOException var11) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".close: " + this, var11);
            }
         }

         this.closeConnectionResources();
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".close<-: " + this);
         }

      }

   }

   public void closeConnectionResources() {
      if (this.orb.transportDebugFlag) {
         this.dprint(".closeConnectionResources->: " + this);
      }

      Selector var1 = this.orb.getTransportManager().getSelector(0);
      if (var1 != null) {
         var1.unregisterForEvent(this);
      }

      try {
         if (this.socketChannel != null) {
            this.socketChannel.close();
         }

         if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
         }
      } catch (IOException var3) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".closeConnectionResources: " + this, var3);
         }
      }

      if (this.orb.transportDebugFlag) {
         this.dprint(".closeConnectionResources<-: " + this);
      }

   }

   public Acceptor getAcceptor() {
      return this.acceptor;
   }

   public ContactInfo getContactInfo() {
      return this.contactInfo;
   }

   public EventHandler getEventHandler() {
      return this;
   }

   public OutputObject createOutputObject(MessageMediator var1) {
      throw new RuntimeException("*****SocketOrChannelConnectionImpl.createOutputObject - should not be called.");
   }

   public boolean isServer() {
      return this.isServer;
   }

   public boolean isBusy() {
      return this.serverRequestCount > 0 || this.getResponseWaitingRoom().numberRegistered() > 0;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void setTimeStamp(long var1) {
      this.timeStamp = var1;
   }

   public void setState(String var1) {
      synchronized(this.stateEvent) {
         if (var1.equals("ESTABLISHED")) {
            this.state = 2;
            this.stateEvent.notifyAll();
         }

      }
   }

   public void writeLock() {
      try {
         if (dprintWriteLocks && this.orb.transportDebugFlag) {
            this.dprint(".writeLock->: " + this);
         }

         label229:
         while(true) {
            int var1 = this.state;
            switch(var1) {
            case 1:
               synchronized(this.stateEvent) {
                  if (this.state == 1) {
                     try {
                        this.stateEvent.wait();
                     } catch (InterruptedException var20) {
                        if (this.orb.transportDebugFlag) {
                           this.dprint(".writeLock: OPENING InterruptedException: " + this);
                        }
                     }
                  }
                  break;
               }
            case 2:
               synchronized(this.writeEvent) {
                  if (!this.writeLocked) {
                     this.writeLocked = true;
                     return;
                  }

                  try {
                     while(true) {
                        if (this.state != 2 || !this.writeLocked) {
                           continue label229;
                        }

                        this.writeEvent.wait(100L);
                     }
                  } catch (InterruptedException var18) {
                     if (this.orb.transportDebugFlag) {
                        this.dprint(".writeLock: ESTABLISHED InterruptedException: " + this);
                     }
                     break;
                  }
               }
            case 3:
            default:
               if (this.orb.transportDebugFlag) {
                  this.dprint(".writeLock: default: " + this);
               }

               throw new RuntimeException(".writeLock: bad state");
            case 4:
               synchronized(this.stateEvent) {
                  if (this.state == 4) {
                     throw this.wrapper.connectionCloseRebind();
                  }
                  break;
               }
            case 5:
               synchronized(this.stateEvent) {
                  if (this.state == 5) {
                     throw this.wrapper.writeErrorSend();
                  }
               }
            }
         }
      } finally {
         if (dprintWriteLocks && this.orb.transportDebugFlag) {
            this.dprint(".writeLock<-: " + this);
         }

      }
   }

   public void writeUnlock() {
      try {
         if (dprintWriteLocks && this.orb.transportDebugFlag) {
            this.dprint(".writeUnlock->: " + this);
         }

         synchronized(this.writeEvent) {
            this.writeLocked = false;
            this.writeEvent.notify();
         }
      } finally {
         if (dprintWriteLocks && this.orb.transportDebugFlag) {
            this.dprint(".writeUnlock<-: " + this);
         }

      }

   }

   public void sendWithoutLock(OutputObject var1) {
      try {
         CDROutputObject var2 = (CDROutputObject)var1;
         var2.writeTo(this);
      } catch (IOException var4) {
         COMM_FAILURE var3 = this.wrapper.writeErrorSend((Throwable)var4);
         this.purgeCalls(var3, false, true);
         throw var3;
      }
   }

   public void registerWaiter(MessageMediator var1) {
      this.responseWaitingRoom.registerWaiter(var1);
   }

   public void unregisterWaiter(MessageMediator var1) {
      this.responseWaitingRoom.unregisterWaiter(var1);
   }

   public InputObject waitForResponse(MessageMediator var1) {
      return this.responseWaitingRoom.waitForResponse(var1);
   }

   public void setConnectionCache(ConnectionCache var1) {
      this.connectionCache = var1;
   }

   public ConnectionCache getConnectionCache() {
      return this.connectionCache;
   }

   public void setUseSelectThreadToWait(boolean var1) {
      this.useSelectThreadToWait = var1;
      this.setReadGiopHeaderOnly(this.shouldUseSelectThreadToWait());
   }

   public void handleEvent() {
      if (this.orb.transportDebugFlag) {
         this.dprint(".handleEvent->: " + this);
      }

      this.getSelectionKey().interestOps(this.getSelectionKey().interestOps() & ~this.getInterestOps());
      if (this.shouldUseWorkerThreadForEvent()) {
         Object var1 = null;

         try {
            int var2 = 0;
            if (this.shouldReadGiopHeaderOnly()) {
               this.partialMessageMediator = this.readBits();
               var2 = this.partialMessageMediator.getThreadPoolToUse();
            }

            if (this.orb.transportDebugFlag) {
               this.dprint(".handleEvent: addWork to pool: " + var2);
            }

            this.orb.getThreadPoolManager().getThreadPool(var2).getWorkQueue(0).addWork(this.getWork());
         } catch (NoSuchThreadPoolException var3) {
            var1 = var3;
         } catch (NoSuchWorkQueueException var4) {
            var1 = var4;
         }

         if (var1 != null) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".handleEvent: " + var1);
            }

            INTERNAL var5 = new INTERNAL("NoSuchThreadPoolException");
            var5.initCause((Throwable)var1);
            throw var5;
         }
      } else {
         if (this.orb.transportDebugFlag) {
            this.dprint(".handleEvent: doWork");
         }

         this.getWork().doWork();
      }

      if (this.orb.transportDebugFlag) {
         this.dprint(".handleEvent<-: " + this);
      }

   }

   public SelectableChannel getChannel() {
      return this.socketChannel;
   }

   public int getInterestOps() {
      return 1;
   }

   public Connection getConnection() {
      return this;
   }

   public String getName() {
      return this.toString();
   }

   public void doWork() {
      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".doWork->: " + this);
         }

         if (!this.shouldReadGiopHeaderOnly()) {
            this.read();
         } else {
            CorbaMessageMediator var1 = this.getPartialMessageMediator();
            var1 = this.finishReadingBits(var1);
            if (var1 != null) {
               this.dispatch(var1);
            }
         }
      } catch (Throwable var5) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".doWork: ignoring Throwable: " + var5 + " " + this);
         }
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".doWork<-: " + this);
         }

      }

   }

   public void setEnqueueTime(long var1) {
      this.enqueueTime = var1;
   }

   public long getEnqueueTime() {
      return this.enqueueTime;
   }

   public boolean shouldReadGiopHeaderOnly() {
      return this.shouldReadGiopHeaderOnly;
   }

   protected void setReadGiopHeaderOnly(boolean var1) {
      this.shouldReadGiopHeaderOnly = var1;
   }

   public ResponseWaitingRoom getResponseWaitingRoom() {
      return this.responseWaitingRoom;
   }

   public void serverRequestMapPut(int var1, CorbaMessageMediator var2) {
      this.serverRequestMap.put(new Integer(var1), var2);
   }

   public CorbaMessageMediator serverRequestMapGet(int var1) {
      return (CorbaMessageMediator)this.serverRequestMap.get(new Integer(var1));
   }

   public void serverRequestMapRemove(int var1) {
      this.serverRequestMap.remove(new Integer(var1));
   }

   public Socket getSocket() {
      return this.socket;
   }

   public synchronized void serverRequestProcessingBegins() {
      ++this.serverRequestCount;
   }

   public synchronized void serverRequestProcessingEnds() {
      --this.serverRequestCount;
   }

   public synchronized int getNextRequestId() {
      return this.requestId++;
   }

   public ORB getBroker() {
      return this.orb;
   }

   public CodeSetComponentInfo.CodeSetContext getCodeSetContext() {
      if (this.codeSetContext == null) {
         synchronized(this) {
            return this.codeSetContext;
         }
      } else {
         return this.codeSetContext;
      }
   }

   public synchronized void setCodeSetContext(CodeSetComponentInfo.CodeSetContext var1) {
      if (this.codeSetContext == null) {
         if (OSFCodeSetRegistry.lookupEntry(var1.getCharCodeSet()) == null || OSFCodeSetRegistry.lookupEntry(var1.getWCharCodeSet()) == null) {
            throw this.wrapper.badCodesetsFromClient();
         }

         this.codeSetContext = var1;
      }

   }

   public MessageMediator clientRequestMapGet(int var1) {
      return this.responseWaitingRoom.getMessageMediator(var1);
   }

   public void clientReply_1_1_Put(MessageMediator var1) {
      this.clientReply_1_1 = var1;
   }

   public MessageMediator clientReply_1_1_Get() {
      return this.clientReply_1_1;
   }

   public void clientReply_1_1_Remove() {
      this.clientReply_1_1 = null;
   }

   public void serverRequest_1_1_Put(MessageMediator var1) {
      this.serverRequest_1_1 = var1;
   }

   public MessageMediator serverRequest_1_1_Get() {
      return this.serverRequest_1_1;
   }

   public void serverRequest_1_1_Remove() {
      this.serverRequest_1_1 = null;
   }

   protected String getStateString(int var1) {
      synchronized(this.stateEvent) {
         switch(var1) {
         case 1:
            return "OPENING";
         case 2:
            return "ESTABLISHED";
         case 3:
            return "CLOSE_SENT";
         case 4:
            return "CLOSE_RECVD";
         case 5:
            return "ABORT";
         default:
            return "???";
         }
      }
   }

   public synchronized boolean isPostInitialContexts() {
      return this.postInitialContexts;
   }

   public synchronized void setPostInitialContexts() {
      this.postInitialContexts = true;
   }

   public void purgeCalls(SystemException var1, boolean var2, boolean var3) {
      int var4 = var1.minor;

      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".purgeCalls->: " + var4 + "/" + var2 + "/" + var3 + " " + this);
         }

         synchronized(this.stateEvent) {
            if (this.state == 5 || this.state == 4) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".purgeCalls: exiting since state is: " + this.getStateString(this.state) + " " + this);
               }

               return;
            }
         }

         try {
            if (!var3) {
               this.writeLock();
            }
         } catch (SystemException var16) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".purgeCalls: SystemException" + var16 + "; continuing " + this);
            }
         }

         synchronized(this.stateEvent) {
            if (var4 == 1398079697) {
               this.state = 4;
               var1.completed = CompletionStatus.COMPLETED_NO;
            } else {
               this.state = 5;
               var1.completed = CompletionStatus.COMPLETED_MAYBE;
            }

            this.stateEvent.notifyAll();
         }

         try {
            this.socket.getInputStream().close();
            this.socket.getOutputStream().close();
            this.socket.close();
         } catch (Exception var15) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".purgeCalls: Exception closing socket: " + var15 + " " + this);
            }
         }

         this.responseWaitingRoom.signalExceptionToAllWaiters(var1);
      } finally {
         if (this.contactInfo != null) {
            ((OutboundConnectionCache)this.getConnectionCache()).remove(this.contactInfo);
         } else if (this.acceptor != null) {
            ((InboundConnectionCache)this.getConnectionCache()).remove(this);
         }

         this.writeUnlock();
         if (this.orb.transportDebugFlag) {
            this.dprint(".purgeCalls<-: " + var4 + "/" + var2 + "/" + var3 + " " + this);
         }

      }
   }

   public void sendCloseConnection(GIOPVersion var1) throws IOException {
      Message var2 = MessageBase.createCloseConnection(var1);
      this.sendHelper(var1, var2);
   }

   public void sendMessageError(GIOPVersion var1) throws IOException {
      Message var2 = MessageBase.createMessageError(var1);
      this.sendHelper(var1, var2);
   }

   public void sendCancelRequest(GIOPVersion var1, int var2) throws IOException {
      CancelRequestMessage var3 = MessageBase.createCancelRequest(var1, var2);
      this.sendHelper(var1, var3);
   }

   protected void sendHelper(GIOPVersion var1, Message var2) throws IOException {
      CDROutputObject var3 = OutputStreamFactory.newCDROutputObject(this.orb, (CorbaMessageMediator)null, var1, this, var2, (byte)1);
      var2.write(var3);
      var3.writeTo(this);
   }

   public void sendCancelRequestWithLock(GIOPVersion var1, int var2) throws IOException {
      this.writeLock();

      try {
         this.sendCancelRequest(var1, var2);
      } finally {
         this.writeUnlock();
      }

   }

   public final void setCodeBaseIOR(IOR var1) {
      this.codeBaseServerIOR = var1;
   }

   public final IOR getCodeBaseIOR() {
      return this.codeBaseServerIOR;
   }

   public final CodeBase getCodeBase() {
      return this.cachedCodeBase;
   }

   protected void setReadTimeouts(ReadTimeouts var1) {
      this.readTimeouts = var1;
   }

   protected void setPartialMessageMediator(CorbaMessageMediator var1) {
      this.partialMessageMediator = var1;
   }

   protected CorbaMessageMediator getPartialMessageMediator() {
      return this.partialMessageMediator;
   }

   public String toString() {
      synchronized(this.stateEvent) {
         return "SocketOrChannelConnectionImpl[ " + (this.socketChannel == null ? this.socket.toString() : this.socketChannel.toString()) + " " + this.getStateString(this.state) + " " + this.shouldUseSelectThreadToWait() + " " + this.shouldUseWorkerThreadForEvent() + " " + this.shouldReadGiopHeaderOnly() + "]";
      }
   }

   public void dprint(String var1) {
      ORBUtility.dprint("SocketOrChannelConnectionImpl", var1);
   }

   protected void dprint(String var1, Throwable var2) {
      this.dprint(var1);
      var2.printStackTrace(System.out);
   }
}
