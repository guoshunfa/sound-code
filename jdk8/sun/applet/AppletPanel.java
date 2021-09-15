package sun.applet;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.EmbeddedFrame;
import sun.awt.SunToolkit;
import sun.misc.MessageUtils;
import sun.misc.PerformanceLogger;
import sun.misc.Queue;
import sun.security.util.SecurityConstants;

public abstract class AppletPanel extends Panel implements AppletStub, Runnable {
   Applet applet;
   protected boolean doInit = true;
   protected AppletClassLoader loader;
   public static final int APPLET_DISPOSE = 0;
   public static final int APPLET_LOAD = 1;
   public static final int APPLET_INIT = 2;
   public static final int APPLET_START = 3;
   public static final int APPLET_STOP = 4;
   public static final int APPLET_DESTROY = 5;
   public static final int APPLET_QUIT = 6;
   public static final int APPLET_ERROR = 7;
   public static final int APPLET_RESIZE = 51234;
   public static final int APPLET_LOADING = 51235;
   public static final int APPLET_LOADING_COMPLETED = 51236;
   protected int status;
   protected Thread handler;
   Dimension defaultAppletSize = new Dimension(10, 10);
   Dimension currentAppletSize = new Dimension(10, 10);
   MessageUtils mu = new MessageUtils();
   Thread loaderThread = null;
   boolean loadAbortRequest = false;
   private static int threadGroupNumber = 0;
   private AppletListener listeners;
   private Queue queue = null;
   private EventQueue appEvtQ = null;
   private static HashMap classloaders = new HashMap();
   private boolean jdk11Applet = false;
   private boolean jdk12Applet = false;
   private static AppletMessageHandler amh = new AppletMessageHandler("appletpanel");

   protected abstract String getCode();

   protected abstract String getJarFiles();

   protected abstract String getSerializedObject();

   public abstract int getWidth();

   public abstract int getHeight();

   public abstract boolean hasInitialFocus();

   protected void setupAppletAppContext() {
   }

   synchronized void createAppletThread() {
      String var1 = "applet-" + this.getCode();
      this.loader = this.getClassLoader(this.getCodeBase(), this.getClassLoaderCacheKey());
      this.loader.grab();
      String var2 = this.getParameter("codebase_lookup");
      if (var2 != null && var2.equals("false")) {
         this.loader.setCodebaseLookup(false);
      } else {
         this.loader.setCodebaseLookup(true);
      }

      ThreadGroup var3 = this.loader.getThreadGroup();
      this.handler = new Thread(var3, this, "thread " + var1);
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            AppletPanel.this.handler.setContextClassLoader(AppletPanel.this.loader);
            return null;
         }
      });
      this.handler.start();
   }

   void joinAppletThread() throws InterruptedException {
      if (this.handler != null) {
         this.handler.join();
         this.handler = null;
      }

   }

   void release() {
      if (this.loader != null) {
         this.loader.release();
         this.loader = null;
      }

   }

   public void init() {
      try {
         this.defaultAppletSize.width = this.getWidth();
         this.currentAppletSize.width = this.defaultAppletSize.width;
         this.defaultAppletSize.height = this.getHeight();
         this.currentAppletSize.height = this.defaultAppletSize.height;
      } catch (NumberFormatException var2) {
         this.status = 7;
         this.showAppletStatus("badattribute.exception");
         this.showAppletLog("badattribute.exception");
         this.showAppletException(var2);
      }

      this.setLayout(new BorderLayout());
      this.createAppletThread();
   }

   public Dimension minimumSize() {
      return new Dimension(this.defaultAppletSize.width, this.defaultAppletSize.height);
   }

   public Dimension preferredSize() {
      return new Dimension(this.currentAppletSize.width, this.currentAppletSize.height);
   }

   public synchronized void addAppletListener(AppletListener var1) {
      this.listeners = AppletEventMulticaster.add(this.listeners, var1);
   }

   public synchronized void removeAppletListener(AppletListener var1) {
      this.listeners = AppletEventMulticaster.remove(this.listeners, var1);
   }

   public void dispatchAppletEvent(int var1, Object var2) {
      if (this.listeners != null) {
         AppletEvent var3 = new AppletEvent(this, var1, var2);
         this.listeners.appletStateChanged(var3);
      }

   }

   public void sendEvent(int var1) {
      synchronized(this) {
         if (this.queue == null) {
            this.queue = new Queue();
         }

         Integer var3 = var1;
         this.queue.enqueue(var3);
         this.notifyAll();
      }

      if (var1 == 6) {
         try {
            this.joinAppletThread();
         } catch (InterruptedException var5) {
         }

         if (this.loader == null) {
            this.loader = this.getClassLoader(this.getCodeBase(), this.getClassLoaderCacheKey());
         }

         this.release();
      }

   }

   synchronized AppletEvent getNextEvent() throws InterruptedException {
      while(this.queue == null || this.queue.isEmpty()) {
         this.wait();
      }

      Integer var1 = (Integer)this.queue.dequeue();
      return new AppletEvent(this, var1, (Object)null);
   }

   boolean emptyEventQueue() {
      return this.queue == null || this.queue.isEmpty();
   }

   private void setExceptionStatus(AccessControlException var1) {
      Permission var2 = var1.getPermission();
      if (var2 instanceof RuntimePermission && var2.getName().startsWith("modifyThread")) {
         if (this.loader == null) {
            this.loader = this.getClassLoader(this.getCodeBase(), this.getClassLoaderCacheKey());
         }

         this.loader.setExceptionStatus();
      }

   }

   public void run() {
      Thread var1 = Thread.currentThread();
      if (var1 == this.loaderThread) {
         this.runLoader();
      } else {
         for(boolean var2 = false; !var2 && !var1.isInterrupted(); this.clearLoadAbortRequest()) {
            AppletEvent var3;
            try {
               var3 = this.getNextEvent();
            } catch (InterruptedException var18) {
               this.showAppletStatus("bail");
               return;
            }

            try {
               final Applet var5;
               Runnable var6;
               switch(var3.getID()) {
               case 0:
                  if (this.status != 5 && this.status != 1) {
                     this.showAppletStatus("notdestroyed");
                  } else {
                     this.status = 0;

                     try {
                        var5 = this.applet;
                        var6 = new Runnable() {
                           public void run() {
                              AppletPanel.this.remove(var5);
                           }
                        };
                        AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, var6);
                     } catch (InterruptedException var8) {
                     } catch (InvocationTargetException var9) {
                     }

                     this.applet = null;
                     this.showAppletStatus("disposed");
                     var2 = true;
                  }
                  break;
               case 1:
                  if (this.okToLoad() && this.loaderThread == null) {
                     this.setLoaderThread(new Thread(this));
                     this.loaderThread.start();
                     this.loaderThread.join();
                     this.setLoaderThread((Thread)null);
                  }
                  break;
               case 2:
                  if (this.status != 1 && this.status != 5) {
                     this.showAppletStatus("notloaded");
                  } else {
                     this.applet.resize(this.defaultAppletSize);
                     if (this.doInit) {
                        if (PerformanceLogger.loggingEnabled()) {
                           PerformanceLogger.setTime("Applet Init");
                           PerformanceLogger.outputLog();
                        }

                        this.applet.init();
                     }

                     Font var4 = this.getFont();
                     if (var4 == null || "dialog".equals(var4.getFamily().toLowerCase(Locale.ENGLISH)) && var4.getSize() == 12 && var4.getStyle() == 0) {
                        this.setFont(new Font("Dialog", 0, 12));
                     }

                     this.doInit = true;

                     try {
                        var6 = new Runnable() {
                           public void run() {
                              AppletPanel.this.validate();
                           }
                        };
                        AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, var6);
                     } catch (InterruptedException var16) {
                     } catch (InvocationTargetException var17) {
                     }

                     this.status = 2;
                     this.showAppletStatus("inited");
                  }
                  break;
               case 3:
                  if (this.status != 2 && this.status != 4) {
                     this.showAppletStatus("notinited");
                  } else {
                     this.applet.resize(this.currentAppletSize);
                     this.applet.start();

                     try {
                        final Applet var22 = this.applet;
                        Runnable var7 = new Runnable() {
                           public void run() {
                              AppletPanel.this.validate();
                              var22.setVisible(true);
                              if (AppletPanel.this.hasInitialFocus()) {
                                 AppletPanel.this.setDefaultFocus();
                              }

                           }
                        };
                        AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, var7);
                     } catch (InterruptedException var14) {
                     } catch (InvocationTargetException var15) {
                     }

                     this.status = 3;
                     this.showAppletStatus("started");
                  }
                  break;
               case 4:
                  if (this.status != 3) {
                     this.showAppletStatus("notstarted");
                  } else {
                     this.status = 4;

                     try {
                        var5 = this.applet;
                        var6 = new Runnable() {
                           public void run() {
                              var5.setVisible(false);
                           }
                        };
                        AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, var6);
                     } catch (InterruptedException var12) {
                     } catch (InvocationTargetException var13) {
                     }

                     try {
                        this.applet.stop();
                     } catch (AccessControlException var11) {
                        this.setExceptionStatus(var11);
                        throw var11;
                     }

                     this.showAppletStatus("stopped");
                  }
                  break;
               case 5:
                  if (this.status != 4 && this.status != 2) {
                     this.showAppletStatus("notstopped");
                  } else {
                     this.status = 5;

                     try {
                        this.applet.destroy();
                     } catch (AccessControlException var10) {
                        this.setExceptionStatus(var10);
                        throw var10;
                     }

                     this.showAppletStatus("destroyed");
                  }
                  break;
               case 6:
                  return;
               }
            } catch (Exception var19) {
               this.status = 7;
               if (var19.getMessage() != null) {
                  this.showAppletStatus("exception2", var19.getClass().getName(), var19.getMessage());
               } else {
                  this.showAppletStatus("exception", var19.getClass().getName());
               }

               this.showAppletException(var19);
            } catch (ThreadDeath var20) {
               this.showAppletStatus("death");
               return;
            } catch (Error var21) {
               this.status = 7;
               if (var21.getMessage() != null) {
                  this.showAppletStatus("error2", var21.getClass().getName(), var21.getMessage());
               } else {
                  this.showAppletStatus("error", var21.getClass().getName());
               }

               this.showAppletException(var21);
            }
         }

      }
   }

   private Component getMostRecentFocusOwnerForWindow(Window var1) {
      Method var2 = (Method)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            Method var1 = null;

            try {
               var1 = KeyboardFocusManager.class.getDeclaredMethod("getMostRecentFocusOwner", Window.class);
               var1.setAccessible(true);
            } catch (Exception var3) {
               var3.printStackTrace();
            }

            return var1;
         }
      });
      if (var2 != null) {
         try {
            return (Component)var2.invoke((Object)null, var1);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      return var1.getMostRecentFocusOwner();
   }

   private void setDefaultFocus() {
      Component var1 = null;
      Container var2 = this.getParent();
      if (var2 != null) {
         if (var2 instanceof Window) {
            var1 = this.getMostRecentFocusOwnerForWindow((Window)var2);
            if (var1 == var2 || var1 == null) {
               var1 = var2.getFocusTraversalPolicy().getInitialComponent((Window)var2);
            }
         } else if (var2.isFocusCycleRoot()) {
            var1 = var2.getFocusTraversalPolicy().getDefaultComponent(var2);
         }
      }

      if (var1 != null) {
         if (var2 instanceof EmbeddedFrame) {
            ((EmbeddedFrame)var2).synthesizeWindowActivation(true);
         }

         var1.requestFocusInWindow();
      }

   }

   private void runLoader() {
      if (this.status != 0) {
         this.showAppletStatus("notdisposed");
      } else {
         this.dispatchAppletEvent(51235, (Object)null);
         this.status = 1;
         this.loader = this.getClassLoader(this.getCodeBase(), this.getClassLoaderCacheKey());
         String var1 = this.getCode();
         this.setupAppletAppContext();

         label122: {
            try {
               this.loadJarFiles(this.loader);
               this.applet = this.createApplet(this.loader);
               break label122;
            } catch (ClassNotFoundException var11) {
               this.status = 7;
               this.showAppletStatus("notfound", var1);
               this.showAppletLog("notfound", var1);
               this.showAppletException(var11);
               return;
            } catch (InstantiationException var12) {
               this.status = 7;
               this.showAppletStatus("nocreate", var1);
               this.showAppletLog("nocreate", var1);
               this.showAppletException(var12);
               return;
            } catch (IllegalAccessException var13) {
               this.status = 7;
               this.showAppletStatus("noconstruct", var1);
               this.showAppletLog("noconstruct", var1);
               this.showAppletException(var13);
               return;
            } catch (Exception var14) {
               this.status = 7;
               this.showAppletStatus("exception", var14.getMessage());
               this.showAppletException(var14);
            } catch (ThreadDeath var15) {
               this.status = 7;
               this.showAppletStatus("death");
               return;
            } catch (Error var16) {
               this.status = 7;
               this.showAppletStatus("error", var16.getMessage());
               this.showAppletException(var16);
               return;
            } finally {
               this.dispatchAppletEvent(51236, (Object)null);
            }

            return;
         }

         if (this.applet != null) {
            this.applet.setStub(this);
            this.applet.hide();
            this.add("Center", this.applet);
            this.showAppletStatus("loaded");
            this.validate();
         }

      }
   }

   protected Applet createApplet(AppletClassLoader var1) throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException {
      String var2 = this.getSerializedObject();
      String var3 = this.getCode();
      if (var3 != null && var2 != null) {
         System.err.println(amh.getMessage("runloader.err"));
         throw new InstantiationException("Either \"code\" or \"object\" should be specified, but not both.");
      } else {
         if (var3 == null && var2 == null) {
            String var4 = "nocode";
            this.status = 7;
            this.showAppletStatus(var4);
            this.showAppletLog(var4);
            this.repaint();
         }

         if (var3 != null) {
            this.applet = (Applet)var1.loadCode(var3).newInstance();
            this.doInit = true;
         } else {
            InputStream var47 = (InputStream)AccessController.doPrivileged(() -> {
               return var1.getResourceAsStream(var2);
            });
            Throwable var5 = null;

            try {
               AppletObjectInputStream var6 = new AppletObjectInputStream(var47, var1);
               Throwable var7 = null;

               try {
                  this.applet = (Applet)var6.readObject();
                  this.doInit = false;
               } catch (Throwable var43) {
                  var7 = var43;
                  throw var43;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var41) {
                           var7.addSuppressed(var41);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }
            } catch (Throwable var45) {
               var5 = var45;
               throw var45;
            } finally {
               if (var47 != null) {
                  if (var5 != null) {
                     try {
                        var47.close();
                     } catch (Throwable var40) {
                        var5.addSuppressed(var40);
                     }
                  } else {
                     var47.close();
                  }
               }

            }
         }

         this.findAppletJDKLevel(this.applet);
         if (Thread.interrupted()) {
            try {
               this.status = 0;
               this.applet = null;
               this.showAppletStatus("death");
            } finally {
               Thread.currentThread().interrupt();
            }

            return null;
         } else {
            return this.applet;
         }
      }
   }

   protected void loadJarFiles(AppletClassLoader var1) throws IOException, InterruptedException {
      String var2 = this.getJarFiles();
      if (var2 != null) {
         StringTokenizer var3 = new StringTokenizer(var2, ",", false);

         while(var3.hasMoreTokens()) {
            String var4 = var3.nextToken().trim();

            try {
               var1.addJar(var4);
            } catch (IllegalArgumentException var6) {
            }
         }
      }

   }

   protected synchronized void stopLoading() {
      if (this.loaderThread != null) {
         this.loaderThread.interrupt();
      } else {
         this.setLoadAbortRequest();
      }

   }

   protected synchronized boolean okToLoad() {
      return !this.loadAbortRequest;
   }

   protected synchronized void clearLoadAbortRequest() {
      this.loadAbortRequest = false;
   }

   protected synchronized void setLoadAbortRequest() {
      this.loadAbortRequest = true;
   }

   private synchronized void setLoaderThread(Thread var1) {
      this.loaderThread = var1;
   }

   public boolean isActive() {
      return this.status == 3;
   }

   public void appletResize(int var1, int var2) {
      this.currentAppletSize.width = var1;
      this.currentAppletSize.height = var2;
      final Dimension var3 = new Dimension(this.currentAppletSize.width, this.currentAppletSize.height);
      if (this.loader != null) {
         AppContext var4 = this.loader.getAppContext();
         if (var4 != null) {
            this.appEvtQ = (EventQueue)var4.get(AppContext.EVENT_QUEUE_KEY);
         }
      }

      if (this.appEvtQ != null) {
         this.appEvtQ.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new Runnable() {
            public void run() {
               if (AppletPanel.this != null) {
                  AppletPanel.this.dispatchAppletEvent(51234, var3);
               }

            }
         }));
      }

   }

   public void setBounds(int var1, int var2, int var3, int var4) {
      super.setBounds(var1, var2, var3, var4);
      this.currentAppletSize.width = var3;
      this.currentAppletSize.height = var4;
   }

   public Applet getApplet() {
      return this.applet;
   }

   protected void showAppletStatus(String var1) {
      this.getAppletContext().showStatus(amh.getMessage(var1));
   }

   protected void showAppletStatus(String var1, Object var2) {
      this.getAppletContext().showStatus(amh.getMessage(var1, var2));
   }

   protected void showAppletStatus(String var1, Object var2, Object var3) {
      this.getAppletContext().showStatus(amh.getMessage(var1, var2, var3));
   }

   protected void showAppletLog(String var1) {
      System.out.println(amh.getMessage(var1));
   }

   protected void showAppletLog(String var1, Object var2) {
      System.out.println(amh.getMessage(var1, var2));
   }

   protected void showAppletException(Throwable var1) {
      var1.printStackTrace();
      this.repaint();
   }

   public String getClassLoaderCacheKey() {
      return this.getCodeBase().toString();
   }

   public static synchronized void flushClassLoader(String var0) {
      classloaders.remove(var0);
   }

   public static synchronized void flushClassLoaders() {
      classloaders = new HashMap();
   }

   protected AppletClassLoader createClassLoader(URL var1) {
      return new AppletClassLoader(var1);
   }

   synchronized AppletClassLoader getClassLoader(final URL var1, final String var2) {
      AppletClassLoader var3 = (AppletClassLoader)classloaders.get(var2);
      if (var3 == null) {
         AccessControlContext var4 = this.getAccessControlContext(var1);
         var3 = (AppletClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               AppletClassLoader var1x = AppletPanel.this.createClassLoader(var1);
               synchronized(this.getClass()) {
                  AppletClassLoader var3 = (AppletClassLoader)AppletPanel.classloaders.get(var2);
                  if (var3 == null) {
                     AppletPanel.classloaders.put(var2, var1x);
                     return var1x;
                  } else {
                     return var3;
                  }
               }
            }
         }, var4);
      }

      return var3;
   }

   private AccessControlContext getAccessControlContext(URL var1) {
      Object var2 = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            Policy var1 = Policy.getPolicy();
            return var1 != null ? var1.getPermissions(new CodeSource((URL)null, (Certificate[])null)) : null;
         }
      });
      if (var2 == null) {
         var2 = new Permissions();
      }

      ((PermissionCollection)var2).add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
      URLConnection var4 = null;

      Permission var3;
      try {
         var4 = var1.openConnection();
         var3 = var4.getPermission();
      } catch (IOException var7) {
         var3 = null;
      }

      if (var3 != null) {
         ((PermissionCollection)var2).add(var3);
      }

      if (var3 instanceof FilePermission) {
         String var5 = var3.getName();
         int var6 = var5.lastIndexOf(File.separatorChar);
         if (var6 != -1) {
            var5 = var5.substring(0, var6 + 1);
            if (var5.endsWith(File.separator)) {
               var5 = var5 + "-";
            }

            ((PermissionCollection)var2).add(new FilePermission(var5, "read"));
         }
      } else {
         URL var8 = var1;
         if (var4 instanceof JarURLConnection) {
            var8 = ((JarURLConnection)var4).getJarFileURL();
         }

         String var9 = var8.getHost();
         if (var9 != null && var9.length() > 0) {
            ((PermissionCollection)var2).add(new SocketPermission(var9, "connect,accept"));
         }
      }

      ProtectionDomain var11 = new ProtectionDomain(new CodeSource(var1, (Certificate[])null), (PermissionCollection)var2);
      AccessControlContext var10 = new AccessControlContext(new ProtectionDomain[]{var11});
      return var10;
   }

   public Thread getAppletHandlerThread() {
      return this.handler;
   }

   public int getAppletWidth() {
      return this.currentAppletSize.width;
   }

   public int getAppletHeight() {
      return this.currentAppletSize.height;
   }

   public static void changeFrameAppContext(Frame var0, AppContext var1) {
      AppContext var2 = SunToolkit.targetToAppContext(var0);
      if (var2 != var1) {
         Class var3 = Window.class;
         synchronized(Window.class) {
            WeakReference var4 = null;
            Vector var5 = (Vector)var2.get(Window.class);
            if (var5 != null) {
               Iterator var6 = var5.iterator();

               while(var6.hasNext()) {
                  WeakReference var7 = (WeakReference)var6.next();
                  if (var7.get() == var0) {
                     var4 = var7;
                     break;
                  }
               }

               if (var4 != null) {
                  var5.remove(var4);
               }
            }

            SunToolkit.insertTargetMapping(var0, var1);
            var5 = (Vector)var1.get(Window.class);
            if (var5 == null) {
               var5 = new Vector();
               var1.put(Window.class, var5);
            }

            var5.add(var4);
         }
      }
   }

   private void findAppletJDKLevel(Applet var1) {
      Class var2 = var1.getClass();
      synchronized(var2) {
         Boolean var4 = this.loader.isJDK11Target(var2);
         Boolean var5 = this.loader.isJDK12Target(var2);
         if (var4 == null && var5 == null) {
            String var6 = var2.getName();
            var6 = var6.replace('.', '/');
            String var7 = var6 + ".class";
            byte[] var8 = new byte[8];

            try {
               InputStream var9 = (InputStream)AccessController.doPrivileged(() -> {
                  return this.loader.getResourceAsStream(var7);
               });
               Throwable var10 = null;

               try {
                  int var11 = var9.read(var8, 0, 8);
                  if (var11 != 8) {
                     return;
                  }
               } catch (Throwable var24) {
                  var10 = var24;
                  throw var24;
               } finally {
                  if (var9 != null) {
                     if (var10 != null) {
                        try {
                           var9.close();
                        } catch (Throwable var23) {
                           var10.addSuppressed(var23);
                        }
                     } else {
                        var9.close();
                     }
                  }

               }
            } catch (IOException var26) {
               return;
            }

            int var28 = this.readShort(var8, 6);
            if (var28 < 46) {
               this.jdk11Applet = true;
            } else if (var28 == 46) {
               this.jdk12Applet = true;
            }

            this.loader.setJDK11Target(var2, this.jdk11Applet);
            this.loader.setJDK12Target(var2, this.jdk12Applet);
         } else {
            this.jdk11Applet = var4 == null ? false : var4;
            this.jdk12Applet = var5 == null ? false : var5;
         }
      }
   }

   protected boolean isJDK11Applet() {
      return this.jdk11Applet;
   }

   protected boolean isJDK12Applet() {
      return this.jdk12Applet;
   }

   private int readShort(byte[] var1, int var2) {
      int var3 = this.readByte(var1[var2]);
      int var4 = this.readByte(var1[var2 + 1]);
      return var3 << 8 | var4;
   }

   private int readByte(byte var1) {
      return var1 & 255;
   }
}
