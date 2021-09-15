package com.sun.jndi.ldap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import javax.naming.CommunicationException;
import javax.naming.InterruptedNamingException;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.ldap.Control;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import sun.misc.IOUtils;

public final class Connection implements Runnable {
   private static final boolean debug = false;
   private static final int dump = 0;
   private final Thread worker;
   private boolean v3 = true;
   public final String host;
   public final int port;
   private boolean bound = false;
   private OutputStream traceFile = null;
   private String traceTagIn = null;
   private String traceTagOut = null;
   public InputStream inStream;
   public OutputStream outStream;
   public Socket sock;
   private final LdapClient parent;
   private int outMsgId = 0;
   private LdapRequest pendingRequests = null;
   volatile IOException closureReason = null;
   volatile boolean useable = true;
   int readTimeout;
   int connectTimeout;
   private static final boolean IS_HOSTNAME_VERIFICATION_DISABLED = hostnameVerificationDisabledValue();
   private Object pauseLock = new Object();
   private boolean paused = false;

   private static boolean hostnameVerificationDisabledValue() {
      PrivilegedAction var0 = () -> {
         return System.getProperty("com.sun.jndi.ldap.object.disableEndpointIdentification");
      };
      String var1 = (String)AccessController.doPrivileged(var0);
      if (var1 == null) {
         return false;
      } else {
         return var1.isEmpty() ? true : Boolean.parseBoolean(var1);
      }
   }

   void setV3(boolean var1) {
      this.v3 = var1;
   }

   void setBound() {
      this.bound = true;
   }

   Connection(LdapClient var1, String var2, int var3, String var4, int var5, int var6, OutputStream var7) throws NamingException {
      this.host = var2;
      this.port = var3;
      this.parent = var1;
      this.readTimeout = var6;
      this.connectTimeout = var5;
      if (var7 != null) {
         this.traceFile = var7;
         this.traceTagIn = "<- " + var2 + ":" + var3 + "\n\n";
         this.traceTagOut = "-> " + var2 + ":" + var3 + "\n\n";
      }

      try {
         this.sock = this.createSocket(var2, var3, var4, var5);
         this.inStream = new BufferedInputStream(this.sock.getInputStream());
         this.outStream = new BufferedOutputStream(this.sock.getOutputStream());
      } catch (InvocationTargetException var11) {
         Throwable var13 = var11.getTargetException();
         CommunicationException var10 = new CommunicationException(var2 + ":" + var3);
         var10.setRootCause(var13);
         throw var10;
      } catch (Exception var12) {
         CommunicationException var9 = new CommunicationException(var2 + ":" + var3);
         var9.setRootCause(var12);
         throw var9;
      }

      this.worker = Obj.helper.createThread(this);
      this.worker.setDaemon(true);
      this.worker.start();
   }

   private Object createInetSocketAddress(String var1, int var2) throws NoSuchMethodException {
      try {
         Class var3 = Class.forName("java.net.InetSocketAddress");
         Constructor var4 = var3.getConstructor(String.class, Integer.TYPE);
         return var4.newInstance(var1, new Integer(var2));
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException | ClassNotFoundException var5) {
         throw new NoSuchMethodException();
      }
   }

   private Socket createSocket(String var1, int var2, String var3, int var4) throws Exception {
      Socket var5 = null;
      Method var7;
      Object var8;
      if (var3 != null) {
         Class var6 = Obj.helper.loadClass(var3);
         var7 = var6.getMethod("getDefault");
         var8 = var7.invoke((Object)null);
         Method var9 = null;
         if (var4 > 0) {
            try {
               var9 = var6.getMethod("createSocket");
               Method var10 = Socket.class.getMethod("connect", Class.forName("java.net.SocketAddress"), Integer.TYPE);
               Object var11 = this.createInetSocketAddress(var1, var2);
               var5 = (Socket)var9.invoke(var8);
               var10.invoke(var5, var11, new Integer(var4));
            } catch (NoSuchMethodException var13) {
            }
         }

         if (var5 == null) {
            var9 = var6.getMethod("createSocket", String.class, Integer.TYPE);
            var5 = (Socket)var9.invoke(var8, var1, new Integer(var2));
         }
      } else {
         if (var4 > 0) {
            try {
               Constructor var14 = Socket.class.getConstructor();
               var7 = Socket.class.getMethod("connect", Class.forName("java.net.SocketAddress"), Integer.TYPE);
               var8 = this.createInetSocketAddress(var1, var2);
               var5 = (Socket)var14.newInstance();
               var7.invoke(var5, var8, new Integer(var4));
            } catch (NoSuchMethodException var12) {
            }
         }

         if (var5 == null) {
            var5 = new Socket(var1, var2);
         }
      }

      if (var5 instanceof SSLSocket) {
         SSLSocket var15 = (SSLSocket)var5;
         int var16 = var15.getSoTimeout();
         if (!IS_HOSTNAME_VERIFICATION_DISABLED) {
            SSLParameters var17 = var15.getSSLParameters();
            var17.setEndpointIdentificationAlgorithm("LDAPS");
            var15.setSSLParameters(var17);
         }

         if (var4 > 0) {
            var15.setSoTimeout(var4);
         }

         var15.startHandshake();
         var15.setSoTimeout(var16);
      }

      return var5;
   }

   synchronized int getMsgId() {
      return ++this.outMsgId;
   }

   LdapRequest writeRequest(BerEncoder var1, int var2) throws IOException {
      return this.writeRequest(var1, var2, false, -1);
   }

   LdapRequest writeRequest(BerEncoder var1, int var2, boolean var3) throws IOException {
      return this.writeRequest(var1, var2, var3, -1);
   }

   LdapRequest writeRequest(BerEncoder var1, int var2, boolean var3, int var4) throws IOException {
      LdapRequest var5 = new LdapRequest(var2, var3, var4);
      this.addRequest(var5);
      if (this.traceFile != null) {
         Ber.dumpBER(this.traceFile, this.traceTagOut, var1.getBuf(), 0, var1.getDataLen());
      }

      this.unpauseReader();

      try {
         synchronized(this) {
            this.outStream.write(var1.getBuf(), 0, var1.getDataLen());
            this.outStream.flush();
            return var5;
         }
      } catch (IOException var9) {
         this.cleanup((Control[])null, true);
         throw this.closureReason = var9;
      }
   }

   BerDecoder readReply(LdapRequest var1) throws IOException, NamingException {
      long var3 = 0L;
      long var5 = 0L;

      BerDecoder var2;
      while((var2 = var1.getReplyBer()) == null && (this.readTimeout <= 0 || var3 < (long)this.readTimeout)) {
         try {
            synchronized(this) {
               if (this.sock == null) {
                  throw new ServiceUnavailableException(this.host + ":" + this.port + "; socket closed");
               }
            }

            synchronized(var1) {
               var2 = var1.getReplyBer();
               if (var2 == null) {
                  if (this.readTimeout > 0) {
                     long var8 = System.nanoTime();
                     var1.wait((long)this.readTimeout - var3);
                     var5 += System.nanoTime() - var8;
                     var3 += var5 / 1000000L;
                     var5 %= 1000000L;
                  } else {
                     var1.wait();
                  }
               } else {
                  break;
               }
            }
         } catch (InterruptedException var13) {
            throw new InterruptedNamingException("Interrupted during LDAP operation");
         }
      }

      if (var2 == null && var3 >= (long)this.readTimeout) {
         this.abandonRequest(var1, (Control[])null);
         throw new NamingException("LDAP response read timed out, timeout used:" + this.readTimeout + "ms.");
      } else {
         return var2;
      }
   }

   private synchronized void addRequest(LdapRequest var1) {
      LdapRequest var2 = this.pendingRequests;
      if (var2 == null) {
         this.pendingRequests = var1;
         var1.next = null;
      } else {
         var1.next = this.pendingRequests;
         this.pendingRequests = var1;
      }

   }

   synchronized LdapRequest findRequest(int var1) {
      for(LdapRequest var2 = this.pendingRequests; var2 != null; var2 = var2.next) {
         if (var2.msgId == var1) {
            return var2;
         }
      }

      return null;
   }

   synchronized void removeRequest(LdapRequest var1) {
      LdapRequest var2 = this.pendingRequests;

      for(LdapRequest var3 = null; var2 != null; var2 = var2.next) {
         if (var2 == var1) {
            var2.cancel();
            if (var3 != null) {
               var3.next = var2.next;
            } else {
               this.pendingRequests = var2.next;
            }

            var2.next = null;
         }

         var3 = var2;
      }

   }

   void abandonRequest(LdapRequest var1, Control[] var2) {
      this.removeRequest(var1);
      BerEncoder var3 = new BerEncoder(256);
      int var4 = this.getMsgId();

      try {
         var3.beginSeq(48);
         var3.encodeInt(var4);
         var3.encodeInt(var1.msgId, 80);
         if (this.v3) {
            LdapClient.encodeControls(var3, var2);
         }

         var3.endSeq();
         if (this.traceFile != null) {
            Ber.dumpBER(this.traceFile, this.traceTagOut, var3.getBuf(), 0, var3.getDataLen());
         }

         synchronized(this) {
            this.outStream.write(var3.getBuf(), 0, var3.getDataLen());
            this.outStream.flush();
         }
      } catch (IOException var8) {
      }

   }

   synchronized void abandonOutstandingReqs(Control[] var1) {
      for(LdapRequest var2 = this.pendingRequests; var2 != null; this.pendingRequests = var2 = var2.next) {
         this.abandonRequest(var2, var1);
      }

   }

   private void ldapUnbind(Control[] var1) {
      BerEncoder var2 = new BerEncoder(256);
      int var3 = this.getMsgId();

      try {
         var2.beginSeq(48);
         var2.encodeInt(var3);
         var2.encodeByte(66);
         var2.encodeByte(0);
         if (this.v3) {
            LdapClient.encodeControls(var2, var1);
         }

         var2.endSeq();
         if (this.traceFile != null) {
            Ber.dumpBER(this.traceFile, this.traceTagOut, var2.getBuf(), 0, var2.getDataLen());
         }

         synchronized(this) {
            this.outStream.write(var2.getBuf(), 0, var2.getDataLen());
            this.outStream.flush();
         }
      } catch (IOException var7) {
      }

   }

   void cleanup(Control[] var1, boolean var2) {
      boolean var3 = false;
      synchronized(this) {
         this.useable = false;
         LdapRequest var5;
         if (this.sock != null) {
            boolean var15 = false;

            try {
               var15 = true;
               if (!var2) {
                  this.abandonOutstandingReqs(var1);
               }

               if (this.bound) {
                  this.ldapUnbind(var1);
                  var15 = false;
               } else {
                  var15 = false;
               }
            } finally {
               if (var15) {
                  try {
                     this.outStream.flush();
                     this.sock.close();
                     this.unpauseReader();
                  } catch (IOException var16) {
                  }

                  if (!var2) {
                     for(LdapRequest var7 = this.pendingRequests; var7 != null; var7 = var7.next) {
                        var7.cancel();
                     }
                  }

                  this.sock = null;
               }
            }

            try {
               this.outStream.flush();
               this.sock.close();
               this.unpauseReader();
            } catch (IOException var18) {
            }

            if (!var2) {
               for(var5 = this.pendingRequests; var5 != null; var5 = var5.next) {
                  var5.cancel();
               }
            }

            this.sock = null;
            var3 = var2;
         }

         if (var3) {
            var5 = this.pendingRequests;

            while(var5 != null) {
               synchronized(var5) {
                  var5.notify();
                  var5 = var5.next;
               }
            }
         }
      }

      if (var3) {
         this.parent.processConnectionClosure();
      }

   }

   public synchronized void replaceStreams(InputStream var1, OutputStream var2) {
      this.inStream = var1;

      try {
         this.outStream.flush();
      } catch (IOException var4) {
      }

      this.outStream = var2;
   }

   private synchronized InputStream getInputStream() {
      return this.inStream;
   }

   private void unpauseReader() throws IOException {
      synchronized(this.pauseLock) {
         if (this.paused) {
            this.paused = false;
            this.pauseLock.notify();
         }

      }
   }

   private void pauseReader() throws IOException {
      this.paused = true;

      try {
         while(this.paused) {
            this.pauseLock.wait();
         }

      } catch (InterruptedException var2) {
         throw new InterruptedIOException("Pause/unpause reader has problems.");
      }
   }

   public void run() {
      InputStream var10 = null;

      try {
         while(true) {
            while(true) {
               while(true) {
                  try {
                     byte[] var1 = new byte[129];
                     byte var5 = 0;
                     boolean var6 = false;
                     boolean var7 = false;
                     var10 = this.getInputStream();
                     int var3 = var10.read(var1, var5, 1);
                     if (var3 < 0) {
                        if (var10 == this.getInputStream()) {
                           return;
                        }
                     } else {
                        int var27 = var5 + 1;
                        if (var1[var5] == 48) {
                           var3 = var10.read(var1, var27, 1);
                           if (var3 < 0) {
                              return;
                           }

                           int var28 = var1[var27++];
                           if ((var28 & 128) == 128) {
                              int var29 = var28 & 127;
                              var3 = 0;

                              int var4;
                              boolean var8;
                              for(var8 = false; var3 < var29; var3 += var4) {
                                 var4 = var10.read(var1, var27 + var3, var29 - var3);
                                 if (var4 < 0) {
                                    var8 = true;
                                    break;
                                 }
                              }

                              if (var8) {
                                 return;
                              }

                              var28 = 0;

                              for(int var11 = 0; var11 < var29; ++var11) {
                                 var28 = (var28 << 8) + (var1[var27 + var11] & 255);
                              }

                              var27 += var3;
                           }

                           byte[] var30 = IOUtils.readFully(var10, var28, false);
                           var1 = Arrays.copyOf(var1, var27 + var30.length);
                           System.arraycopy(var30, 0, var1, var27, var30.length);
                           var27 += var30.length;

                           try {
                              BerDecoder var9 = new BerDecoder(var1, 0, var27);
                              if (this.traceFile != null) {
                                 Ber.dumpBER(this.traceFile, this.traceTagIn, var1, 0, var27);
                              }

                              var9.parseSeq((int[])null);
                              int var2 = var9.parseInt();
                              var9.reset();
                              boolean var12 = false;
                              if (var2 == 0) {
                                 this.parent.processUnsolicited(var9);
                              } else {
                                 LdapRequest var13 = this.findRequest(var2);
                                 if (var13 != null) {
                                    synchronized(this.pauseLock) {
                                       var12 = var13.addReplyBer(var9);
                                       if (var12) {
                                          this.pauseReader();
                                       }
                                    }
                                 }
                              }
                           } catch (Ber.DecodeException var23) {
                           }
                        }
                     }
                  } catch (IOException var24) {
                     if (var10 == this.getInputStream()) {
                        throw var24;
                     }
                  }
               }
            }
         }
      } catch (IOException var25) {
         this.closureReason = var25;
      } finally {
         this.cleanup((Control[])null, true);
      }

   }
}
