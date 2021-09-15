package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.server.LogStream;
import java.rmi.server.RMISocketFactory;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Vector;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetLongAction;
import sun.security.action.GetPropertyAction;

public class RMIMasterSocketFactory extends RMISocketFactory {
   static int logLevel = LogStream.parseLevel(getLogLevel());
   static final Log proxyLog;
   private static long connectTimeout;
   private static final boolean eagerHttpFallback;
   private Hashtable<String, RMISocketFactory> successTable = new Hashtable();
   private static final int MaxRememberedHosts = 64;
   private Vector<String> hostList = new Vector(64);
   protected RMISocketFactory initialFactory = new RMIDirectSocketFactory();
   protected Vector<RMISocketFactory> altFactoryList = new Vector(2);

   private static String getLogLevel() {
      return (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.transport.proxy.logLevel")));
   }

   private static long getConnectTimeout() {
      return (Long)AccessController.doPrivileged((PrivilegedAction)(new GetLongAction("sun.rmi.transport.proxy.connectTimeout", 15000L)));
   }

   public RMIMasterSocketFactory() {
      boolean var1 = false;

      try {
         String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("http.proxyHost")));
         if (var2 == null) {
            var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("proxyHost")));
         }

         boolean var3 = ((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.rmi.server.disableHttp", "true")))).equalsIgnoreCase("true");
         if (!var3 && var2 != null && var2.length() > 0) {
            var1 = true;
         }
      } catch (Exception var4) {
      }

      if (var1) {
         this.altFactoryList.addElement(new RMIHttpToPortSocketFactory());
         this.altFactoryList.addElement(new RMIHttpToCGISocketFactory());
      }

   }

   public Socket createSocket(String var1, int var2) throws IOException {
      if (proxyLog.isLoggable(Log.BRIEF)) {
         proxyLog.log(Log.BRIEF, "host: " + var1 + ", port: " + var2);
      }

      if (this.altFactoryList.size() == 0) {
         return this.initialFactory.createSocket(var1, var2);
      } else {
         RMISocketFactory var3 = (RMISocketFactory)this.successTable.get(var1);
         if (var3 != null) {
            if (proxyLog.isLoggable(Log.BRIEF)) {
               proxyLog.log(Log.BRIEF, "previously successful factory found: " + var3);
            }

            return var3.createSocket(var1, var2);
         } else {
            Socket var4 = null;
            Socket var5 = null;
            RMIMasterSocketFactory.AsyncConnector var6 = new RMIMasterSocketFactory.AsyncConnector(this.initialFactory, var1, var2, AccessController.getContext());
            Object var7 = null;
            boolean var186 = false;

            Socket var8;
            label2145: {
               label2135: {
                  InputStream var11;
                  int var216;
                  Socket var217;
                  Throwable var219;
                  int var221;
                  label2134: {
                     try {
                        var186 = true;
                        synchronized(var6) {
                           Thread var9 = (Thread)AccessController.doPrivileged((PrivilegedAction)(new NewThreadAction(var6, "AsyncConnector", true)));
                           var9.start();

                           try {
                              long var10 = System.currentTimeMillis();
                              long var12 = var10 + connectTimeout;

                              do {
                                 var6.wait(var12 - var10);
                                 var4 = this.checkConnector(var6);
                                 if (var4 != null) {
                                    break;
                                 }

                                 var10 = System.currentTimeMillis();
                              } while(var10 < var12);
                           } catch (InterruptedException var211) {
                              throw new InterruptedIOException("interrupted while waiting for connector");
                           }
                        }

                        if (var4 == null) {
                           throw new NoRouteToHostException("connect timed out: " + var1);
                        }

                        proxyLog.log(Log.BRIEF, "direct socket connection successful");
                        var8 = var4;
                        var186 = false;
                        break label2145;
                     } catch (NoRouteToHostException | UnknownHostException var213) {
                        var7 = var213;
                        var186 = false;
                     } catch (SocketException var214) {
                        if (!eagerHttpFallback) {
                           throw var214;
                        }

                        var7 = var214;
                        var186 = false;
                        break label2134;
                     } finally {
                        if (var186) {
                           if (var7 != null) {
                              label2163: {
                                 if (proxyLog.isLoggable(Log.BRIEF)) {
                                    proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)var7);
                                 }

                                 int var22 = 0;

                                 while(true) {
                                    if (var22 >= this.altFactoryList.size()) {
                                       break label2163;
                                    }

                                    var3 = (RMISocketFactory)this.altFactoryList.elementAt(var22);
                                    if (proxyLog.isLoggable(Log.BRIEF)) {
                                       proxyLog.log(Log.BRIEF, "trying with factory: " + var3);
                                    }

                                    try {
                                       Socket var23 = var3.createSocket(var1, var2);
                                       Throwable var24 = null;

                                       try {
                                          InputStream var25 = var23.getInputStream();
                                          int var26 = var25.read();
                                          break;
                                       } catch (Throwable var189) {
                                          var24 = var189;
                                          throw var189;
                                       } finally {
                                          if (var23 != null) {
                                             if (var24 != null) {
                                                try {
                                                   var23.close();
                                                } catch (Throwable var188) {
                                                   var24.addSuppressed(var188);
                                                }
                                             } else {
                                                var23.close();
                                             }
                                          }

                                       }
                                    } catch (IOException var201) {
                                       if (proxyLog.isLoggable(Log.BRIEF)) {
                                          proxyLog.log(Log.BRIEF, "factory failed: ", var201);
                                       }

                                       ++var22;
                                    }
                                 }

                                 proxyLog.log(Log.BRIEF, "factory succeeded");

                                 try {
                                    var3.createSocket(var1, var2);
                                 } catch (IOException var187) {
                                 }
                              }
                           }

                        }
                     }

                     if (var7 != null) {
                        if (proxyLog.isLoggable(Log.BRIEF)) {
                           proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)var7);
                        }

                        var216 = 0;

                        while(var216 < this.altFactoryList.size()) {
                           var3 = (RMISocketFactory)this.altFactoryList.elementAt(var216);
                           if (proxyLog.isLoggable(Log.BRIEF)) {
                              proxyLog.log(Log.BRIEF, "trying with factory: " + var3);
                           }

                           try {
                              var217 = var3.createSocket(var1, var2);
                              var219 = null;

                              try {
                                 var11 = var217.getInputStream();
                                 var221 = var11.read();
                              } catch (Throwable var196) {
                                 var219 = var196;
                                 throw var196;
                              } finally {
                                 if (var217 != null) {
                                    if (var219 != null) {
                                       try {
                                          var217.close();
                                       } catch (Throwable var195) {
                                          var219.addSuppressed(var195);
                                       }
                                    } else {
                                       var217.close();
                                    }
                                 }

                              }
                           } catch (IOException var208) {
                              if (proxyLog.isLoggable(Log.BRIEF)) {
                                 proxyLog.log(Log.BRIEF, "factory failed: ", var208);
                              }

                              ++var216;
                              continue;
                           }

                           proxyLog.log(Log.BRIEF, "factory succeeded");

                           try {
                              var5 = var3.createSocket(var1, var2);
                           } catch (IOException var194) {
                           }
                           break label2135;
                        }
                     }
                     break label2135;
                  }

                  if (var7 != null) {
                     if (proxyLog.isLoggable(Log.BRIEF)) {
                        proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)var7);
                     }

                     var216 = 0;

                     while(var216 < this.altFactoryList.size()) {
                        var3 = (RMISocketFactory)this.altFactoryList.elementAt(var216);
                        if (proxyLog.isLoggable(Log.BRIEF)) {
                           proxyLog.log(Log.BRIEF, "trying with factory: " + var3);
                        }

                        try {
                           var217 = var3.createSocket(var1, var2);
                           var219 = null;

                           try {
                              var11 = var217.getInputStream();
                              var221 = var11.read();
                           } catch (Throwable var193) {
                              var219 = var193;
                              throw var193;
                           } finally {
                              if (var217 != null) {
                                 if (var219 != null) {
                                    try {
                                       var217.close();
                                    } catch (Throwable var192) {
                                       var219.addSuppressed(var192);
                                    }
                                 } else {
                                    var217.close();
                                 }
                              }

                           }
                        } catch (IOException var206) {
                           if (proxyLog.isLoggable(Log.BRIEF)) {
                              proxyLog.log(Log.BRIEF, "factory failed: ", var206);
                           }

                           ++var216;
                           continue;
                        }

                        proxyLog.log(Log.BRIEF, "factory succeeded");

                        try {
                           var5 = var3.createSocket(var1, var2);
                        } catch (IOException var191) {
                        }
                        break;
                     }
                  }
               }

               synchronized(this.successTable) {
                  try {
                     synchronized(var6) {
                        var4 = this.checkConnector(var6);
                     }

                     if (var4 != null) {
                        if (var5 != null) {
                           var5.close();
                        }

                        Socket var10000 = var4;
                        return var10000;
                     }

                     var6.notUsed();
                  } catch (NoRouteToHostException | UnknownHostException var202) {
                     var7 = var202;
                  } catch (SocketException var203) {
                     if (!eagerHttpFallback) {
                        throw var203;
                     }

                     var7 = var203;
                  }

                  if (var5 != null) {
                     this.rememberFactory(var1, var3);
                     return var5;
                  }

                  throw var7;
               }
            }

            if (var7 != null) {
               if (proxyLog.isLoggable(Log.BRIEF)) {
                  proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)var7);
               }

               int var218 = 0;

               while(var218 < this.altFactoryList.size()) {
                  var3 = (RMISocketFactory)this.altFactoryList.elementAt(var218);
                  if (proxyLog.isLoggable(Log.BRIEF)) {
                     proxyLog.log(Log.BRIEF, "trying with factory: " + var3);
                  }

                  try {
                     Socket var222 = var3.createSocket(var1, var2);
                     Throwable var220 = null;

                     try {
                        InputStream var223 = var222.getInputStream();
                        int var13 = var223.read();
                     } catch (Throwable var199) {
                        var220 = var199;
                        throw var199;
                     } finally {
                        if (var222 != null) {
                           if (var220 != null) {
                              try {
                                 var222.close();
                              } catch (Throwable var198) {
                                 var220.addSuppressed(var198);
                              }
                           } else {
                              var222.close();
                           }
                        }

                     }
                  } catch (IOException var210) {
                     if (proxyLog.isLoggable(Log.BRIEF)) {
                        proxyLog.log(Log.BRIEF, "factory failed: ", var210);
                     }

                     ++var218;
                     continue;
                  }

                  proxyLog.log(Log.BRIEF, "factory succeeded");

                  try {
                     var3.createSocket(var1, var2);
                  } catch (IOException var197) {
                  }
                  break;
               }
            }

            return var8;
         }
      }
   }

   void rememberFactory(String var1, RMISocketFactory var2) {
      synchronized(this.successTable) {
         while(this.hostList.size() >= 64) {
            this.successTable.remove(this.hostList.elementAt(0));
            this.hostList.removeElementAt(0);
         }

         this.hostList.addElement(var1);
         this.successTable.put(var1, var2);
      }
   }

   Socket checkConnector(RMIMasterSocketFactory.AsyncConnector var1) throws IOException {
      Exception var2 = var1.getException();
      if (var2 != null) {
         var2.fillInStackTrace();
         if (var2 instanceof IOException) {
            throw (IOException)var2;
         } else if (var2 instanceof RuntimeException) {
            throw (RuntimeException)var2;
         } else {
            throw new Error("internal error: unexpected checked exception: " + var2.toString());
         }
      } else {
         return var1.getSocket();
      }
   }

   public ServerSocket createServerSocket(int var1) throws IOException {
      return this.initialFactory.createServerSocket(var1);
   }

   static {
      proxyLog = Log.getLog("sun.rmi.transport.tcp.proxy", "transport", logLevel);
      connectTimeout = getConnectTimeout();
      eagerHttpFallback = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.rmi.transport.proxy.eagerHttpFallback")));
   }

   private class AsyncConnector implements Runnable {
      private RMISocketFactory factory;
      private String host;
      private int port;
      private AccessControlContext acc;
      private Exception exception = null;
      private Socket socket = null;
      private boolean cleanUp = false;

      AsyncConnector(RMISocketFactory var2, String var3, int var4, AccessControlContext var5) {
         this.factory = var2;
         this.host = var3;
         this.port = var4;
         this.acc = var5;
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            var6.checkConnect(var3, var4);
         }

      }

      public void run() {
         try {
            Socket var11 = this.factory.createSocket(this.host, this.port);
            synchronized(this) {
               this.socket = var11;
               this.notify();
            }

            RMIMasterSocketFactory.this.rememberFactory(this.host, this.factory);
            synchronized(this) {
               if (this.cleanUp) {
                  try {
                     this.socket.close();
                  } catch (IOException var7) {
                  }
               }
            }
         } catch (Exception var10) {
            Exception var1 = var10;
            synchronized(this) {
               this.exception = var1;
               this.notify();
            }
         }

      }

      private synchronized Exception getException() {
         return this.exception;
      }

      private synchronized Socket getSocket() {
         return this.socket;
      }

      synchronized void notUsed() {
         if (this.socket != null) {
            try {
               this.socket.close();
            } catch (IOException var2) {
            }
         }

         this.cleanUp = true;
      }
   }
}
