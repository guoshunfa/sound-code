package sun.applet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import javax.print.attribute.HashPrintRequestAttributeSet;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.misc.Ref;

public class AppletViewer extends Frame implements AppletContext, Printable {
   private static String defaultSaveFile = "Applet.ser";
   AppletViewerPanel panel;
   Label label;
   PrintStream statusMsgStream;
   AppletViewerFactory factory;
   private static Map audioClips = new HashMap();
   private static Map imageRefs = new HashMap();
   static Vector appletPanels = new Vector();
   static Hashtable systemParam = new Hashtable();
   static AppletProps props;
   static int c;
   private static int x;
   private static int y;
   private static final int XDELTA = 30;
   private static final int YDELTA = 30;
   static String encoding;
   private static AppletMessageHandler amh;

   public AppletViewer(int var1, int var2, URL var3, Hashtable var4, PrintStream var5, AppletViewerFactory var6) {
      this.factory = var6;
      this.statusMsgStream = var5;
      this.setTitle(amh.getMessage("tool.title", var4.get("code")));
      MenuBar var7 = var6.getBaseMenuBar();
      Menu var8 = new Menu(amh.getMessage("menu.applet"));
      this.addMenuItem(var8, "menuitem.restart");
      this.addMenuItem(var8, "menuitem.reload");
      this.addMenuItem(var8, "menuitem.stop");
      this.addMenuItem(var8, "menuitem.save");
      this.addMenuItem(var8, "menuitem.start");
      this.addMenuItem(var8, "menuitem.clone");
      var8.add(new MenuItem("-"));
      this.addMenuItem(var8, "menuitem.tag");
      this.addMenuItem(var8, "menuitem.info");
      this.addMenuItem(var8, "menuitem.edit").disable();
      this.addMenuItem(var8, "menuitem.encoding");
      var8.add(new MenuItem("-"));
      this.addMenuItem(var8, "menuitem.print");
      var8.add(new MenuItem("-"));
      this.addMenuItem(var8, "menuitem.props");
      var8.add(new MenuItem("-"));
      this.addMenuItem(var8, "menuitem.close");
      if (var6.isStandalone()) {
         this.addMenuItem(var8, "menuitem.quit");
      }

      var7.add(var8);
      this.setMenuBar(var7);
      this.add("Center", this.panel = new AppletViewerPanel(var3, var4));
      this.add("South", this.label = new Label(amh.getMessage("label.hello")));
      this.panel.init();
      appletPanels.addElement(this.panel);
      this.pack();
      this.move(var1, var2);
      this.setVisible(true);
      WindowAdapter var9 = new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            AppletViewer.this.appletClose();
         }

         public void windowIconified(WindowEvent var1) {
            AppletViewer.this.appletStop();
         }

         public void windowDeiconified(WindowEvent var1) {
            AppletViewer.this.appletStart();
         }
      };
      this.addWindowListener(var9);

      class AppletEventListener implements AppletListener {
         final Frame frame;

         public AppletEventListener(Frame var2) {
            this.frame = var2;
         }

         public void appletStateChanged(AppletEvent var1) {
            AppletPanel var2 = (AppletPanel)var1.getSource();
            switch(var1.getID()) {
            case 51234:
               if (var2 != null) {
                  AppletViewer.this.resize(AppletViewer.this.preferredSize());
                  AppletViewer.this.validate();
               }
               break;
            case 51236:
               Applet var3 = var2.getApplet();
               if (var3 != null) {
                  AppletPanel.changeFrameAppContext(this.frame, SunToolkit.targetToAppContext(var3));
               } else {
                  AppletPanel.changeFrameAppContext(this.frame, AppContext.getAppContext());
               }
            }

         }
      }

      this.panel.addAppletListener(new AppletEventListener(this));
      this.showStatus(amh.getMessage("status.start"));
      this.initEventQueue();
   }

   public MenuItem addMenuItem(Menu var1, String var2) {
      MenuItem var3 = new MenuItem(amh.getMessage(var2));
      var3.addActionListener(new AppletViewer.UserActionListener());
      return var1.add(var3);
   }

   private void initEventQueue() {
      String var1 = System.getProperty("appletviewer.send.event");
      if (var1 == null) {
         this.panel.sendEvent(1);
         this.panel.sendEvent(2);
         this.panel.sendEvent(3);
      } else {
         String[] var2 = this.splitSeparator(",", var1);

         for(int var3 = 0; var3 < var2.length; ++var3) {
            System.out.println("Adding event to queue: " + var2[var3]);
            if (var2[var3].equals("dispose")) {
               this.panel.sendEvent(0);
            } else if (var2[var3].equals("load")) {
               this.panel.sendEvent(1);
            } else if (var2[var3].equals("init")) {
               this.panel.sendEvent(2);
            } else if (var2[var3].equals("start")) {
               this.panel.sendEvent(3);
            } else if (var2[var3].equals("stop")) {
               this.panel.sendEvent(4);
            } else if (var2[var3].equals("destroy")) {
               this.panel.sendEvent(5);
            } else if (var2[var3].equals("quit")) {
               this.panel.sendEvent(6);
            } else if (var2[var3].equals("error")) {
               this.panel.sendEvent(7);
            } else {
               System.out.println("Unrecognized event name: " + var2[var3]);
            }
         }

         while(true) {
            if (this.panel.emptyEventQueue()) {
               this.appletSystemExit();
               break;
            }
         }
      }

   }

   private String[] splitSeparator(String var1, String var2) {
      Vector var3 = new Vector();
      int var4 = 0;

      int var7;
      for(boolean var5 = false; (var7 = var2.indexOf(var1, var4)) != -1; var4 = var7 + 1) {
         var3.addElement(var2.substring(var4, var7));
      }

      var3.addElement(var2.substring(var4));
      String[] var6 = new String[var3.size()];
      var3.copyInto(var6);
      return var6;
   }

   public AudioClip getAudioClip(URL var1) {
      checkConnect(var1);
      synchronized(audioClips) {
         Object var3 = (AudioClip)audioClips.get(var1);
         if (var3 == null) {
            audioClips.put(var1, var3 = new AppletAudioClip(var1));
         }

         return (AudioClip)var3;
      }
   }

   public Image getImage(URL var1) {
      return getCachedImage(var1);
   }

   static Image getCachedImage(URL var0) {
      return (Image)getCachedImageRef(var0).get();
   }

   static Ref getCachedImageRef(URL var0) {
      synchronized(imageRefs) {
         AppletImageRef var2 = (AppletImageRef)imageRefs.get(var0);
         if (var2 == null) {
            var2 = new AppletImageRef(var0);
            imageRefs.put(var0, var2);
         }

         return var2;
      }
   }

   static void flushImageCache() {
      imageRefs.clear();
   }

   public Applet getApplet(String var1) {
      AppletSecurity var2 = (AppletSecurity)System.getSecurityManager();
      var1 = var1.toLowerCase();
      SocketPermission var3 = new SocketPermission(this.panel.getCodeBase().getHost(), "connect");
      Enumeration var4 = appletPanels.elements();

      while(var4.hasMoreElements()) {
         AppletPanel var5 = (AppletPanel)var4.nextElement();
         String var6 = var5.getParameter("name");
         if (var6 != null) {
            var6 = var6.toLowerCase();
         }

         if (var1.equals(var6) && var5.getDocumentBase().equals(this.panel.getDocumentBase())) {
            SocketPermission var7 = new SocketPermission(var5.getCodeBase().getHost(), "connect");
            if (var3.implies(var7)) {
               return var5.applet;
            }
         }
      }

      return null;
   }

   public Enumeration getApplets() {
      AppletSecurity var1 = (AppletSecurity)System.getSecurityManager();
      Vector var2 = new Vector();
      SocketPermission var3 = new SocketPermission(this.panel.getCodeBase().getHost(), "connect");
      Enumeration var4 = appletPanels.elements();

      while(var4.hasMoreElements()) {
         AppletPanel var5 = (AppletPanel)var4.nextElement();
         if (var5.getDocumentBase().equals(this.panel.getDocumentBase())) {
            SocketPermission var6 = new SocketPermission(var5.getCodeBase().getHost(), "connect");
            if (var3.implies(var6)) {
               var2.addElement(var5.applet);
            }
         }
      }

      return var2.elements();
   }

   public void showDocument(URL var1) {
   }

   public void showDocument(URL var1, String var2) {
   }

   public void showStatus(String var1) {
      this.label.setText(var1);
   }

   public void setStream(String var1, InputStream var2) throws IOException {
   }

   public InputStream getStream(String var1) {
      return null;
   }

   public Iterator getStreamKeys() {
      return null;
   }

   public static void printTag(PrintStream var0, Hashtable var1) {
      var0.print("<applet");
      String var2 = (String)var1.get("codebase");
      if (var2 != null) {
         var0.print(" codebase=\"" + var2 + "\"");
      }

      var2 = (String)var1.get("code");
      if (var2 == null) {
         var2 = "applet.class";
      }

      var0.print(" code=\"" + var2 + "\"");
      var2 = (String)var1.get("width");
      if (var2 == null) {
         var2 = "150";
      }

      var0.print(" width=" + var2);
      var2 = (String)var1.get("height");
      if (var2 == null) {
         var2 = "100";
      }

      var0.print(" height=" + var2);
      var2 = (String)var1.get("name");
      if (var2 != null) {
         var0.print(" name=\"" + var2 + "\"");
      }

      var0.println(">");
      int var3 = var1.size();
      String[] var4 = new String[var3];
      var3 = 0;

      String var6;
      for(Enumeration var5 = var1.keys(); var5.hasMoreElements(); ++var3) {
         var6 = (String)var5.nextElement();

         int var7;
         for(var7 = 0; var7 < var3 && var4[var7].compareTo(var6) < 0; ++var7) {
         }

         System.arraycopy(var4, var7, var4, var7 + 1, var3 - var7);
         var4[var7] = var6;
      }

      for(int var8 = 0; var8 < var3; ++var8) {
         var6 = var4[var8];
         if (systemParam.get(var6) == null) {
            var0.println("<param name=" + var6 + " value=\"" + var1.get(var6) + "\">");
         }
      }

      var0.println("</applet>");
   }

   public void updateAtts() {
      Dimension var1 = this.panel.size();
      Insets var2 = this.panel.insets();
      this.panel.atts.put("width", Integer.toString(var1.width - (var2.left + var2.right)));
      this.panel.atts.put("height", Integer.toString(var1.height - (var2.top + var2.bottom)));
   }

   void appletRestart() {
      this.panel.sendEvent(4);
      this.panel.sendEvent(5);
      this.panel.sendEvent(2);
      this.panel.sendEvent(3);
   }

   void appletReload() {
      this.panel.sendEvent(4);
      this.panel.sendEvent(5);
      this.panel.sendEvent(0);
      AppletPanel.flushClassLoader(this.panel.getClassLoaderCacheKey());

      try {
         this.panel.joinAppletThread();
         this.panel.release();
      } catch (InterruptedException var2) {
         return;
      }

      this.panel.createAppletThread();
      this.panel.sendEvent(1);
      this.panel.sendEvent(2);
      this.panel.sendEvent(3);
   }

   void appletSave() {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            AppletViewer.this.panel.sendEvent(4);
            FileDialog var1 = new FileDialog(AppletViewer.this, AppletViewer.amh.getMessage("appletsave.filedialogtitle"), 1);
            var1.setDirectory(System.getProperty("user.dir"));
            var1.setFile(AppletViewer.defaultSaveFile);
            var1.show();
            String var2 = var1.getFile();
            if (var2 == null) {
               AppletViewer.this.panel.sendEvent(3);
               return null;
            } else {
               String var3 = var1.getDirectory();
               File var4 = new File(var3, var2);

               try {
                  FileOutputStream var5 = new FileOutputStream(var4);
                  Throwable var6 = null;

                  try {
                     BufferedOutputStream var7 = new BufferedOutputStream(var5);
                     Throwable var8 = null;

                     try {
                        ObjectOutputStream var9 = new ObjectOutputStream(var7);
                        Throwable var10 = null;

                        try {
                           AppletViewer.this.showStatus(AppletViewer.amh.getMessage("appletsave.err1", AppletViewer.this.panel.applet.toString(), var4.toString()));
                           var9.writeObject(AppletViewer.this.panel.applet);
                        } catch (Throwable var75) {
                           var10 = var75;
                           throw var75;
                        } finally {
                           if (var9 != null) {
                              if (var10 != null) {
                                 try {
                                    var9.close();
                                 } catch (Throwable var74) {
                                    var10.addSuppressed(var74);
                                 }
                              } else {
                                 var9.close();
                              }
                           }

                        }
                     } catch (Throwable var77) {
                        var8 = var77;
                        throw var77;
                     } finally {
                        if (var7 != null) {
                           if (var8 != null) {
                              try {
                                 var7.close();
                              } catch (Throwable var73) {
                                 var8.addSuppressed(var73);
                              }
                           } else {
                              var7.close();
                           }
                        }

                     }
                  } catch (Throwable var79) {
                     var6 = var79;
                     throw var79;
                  } finally {
                     if (var5 != null) {
                        if (var6 != null) {
                           try {
                              var5.close();
                           } catch (Throwable var72) {
                              var6.addSuppressed(var72);
                           }
                        } else {
                           var5.close();
                        }
                     }

                  }
               } catch (IOException var81) {
                  System.err.println(AppletViewer.amh.getMessage("appletsave.err2", (Object)var81));
               } finally {
                  AppletViewer.this.panel.sendEvent(3);
               }

               return null;
            }
         }
      });
   }

   void appletClone() {
      Point var1 = this.location();
      this.updateAtts();
      this.factory.createAppletViewer(var1.x + 30, var1.y + 30, this.panel.documentURL, (Hashtable)this.panel.atts.clone());
   }

   void appletTag() {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      this.updateAtts();
      printTag(new PrintStream(var1), this.panel.atts);
      this.showStatus(amh.getMessage("applettag"));
      Point var2 = this.location();
      new TextFrame(var2.x + 30, var2.y + 30, amh.getMessage("applettag.textframe"), var1.toString());
   }

   void appletInfo() {
      String var1 = this.panel.applet.getAppletInfo();
      if (var1 == null) {
         var1 = amh.getMessage("appletinfo.applet");
      }

      var1 = var1 + "\n\n";
      String[][] var2 = this.panel.applet.getParameterInfo();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            var1 = var1 + var2[var3][0] + " -- " + var2[var3][1] + " -- " + var2[var3][2] + "\n";
         }
      } else {
         var1 = var1 + amh.getMessage("appletinfo.param");
      }

      Point var4 = this.location();
      new TextFrame(var4.x + 30, var4.y + 30, amh.getMessage("appletinfo.textframe"), var1);
   }

   void appletCharacterEncoding() {
      this.showStatus(amh.getMessage("appletencoding", (Object)encoding));
   }

   void appletEdit() {
   }

   void appletPrint() {
      PrinterJob var1 = PrinterJob.getPrinterJob();
      if (var1 != null) {
         HashPrintRequestAttributeSet var2 = new HashPrintRequestAttributeSet();
         if (var1.printDialog(var2)) {
            var1.setPrintable(this);

            try {
               var1.print(var2);
               this.statusMsgStream.println(amh.getMessage("appletprint.finish"));
            } catch (PrinterException var4) {
               this.statusMsgStream.println(amh.getMessage("appletprint.fail"));
            }
         } else {
            this.statusMsgStream.println(amh.getMessage("appletprint.cancel"));
         }
      } else {
         this.statusMsgStream.println(amh.getMessage("appletprint.fail"));
      }

   }

   public int print(Graphics var1, PageFormat var2, int var3) {
      if (var3 > 0) {
         return 1;
      } else {
         Graphics2D var4 = (Graphics2D)var1;
         var4.translate(var2.getImageableX(), var2.getImageableY());
         this.panel.applet.printAll(var1);
         return 0;
      }
   }

   public static synchronized void networkProperties() {
      if (props == null) {
         props = new AppletProps();
      }

      props.addNotify();
      props.setVisible(true);
   }

   void appletStart() {
      this.panel.sendEvent(3);
   }

   void appletStop() {
      this.panel.sendEvent(4);
   }

   private void appletShutdown(AppletPanel var1) {
      var1.sendEvent(4);
      var1.sendEvent(5);
      var1.sendEvent(0);
      var1.sendEvent(6);
   }

   void appletClose() {
      final AppletViewerPanel var1 = this.panel;
      (new Thread(new Runnable() {
         public void run() {
            AppletViewer.this.appletShutdown(var1);
            AppletViewer.appletPanels.removeElement(var1);
            AppletViewer.this.dispose();
            if (AppletViewer.countApplets() == 0) {
               AppletViewer.this.appletSystemExit();
            }

         }
      })).start();
   }

   private void appletSystemExit() {
      if (this.factory.isStandalone()) {
         System.exit(0);
      }

   }

   protected void appletQuit() {
      (new Thread(new Runnable() {
         public void run() {
            Enumeration var1 = AppletViewer.appletPanels.elements();

            while(var1.hasMoreElements()) {
               AppletPanel var2 = (AppletPanel)var1.nextElement();
               AppletViewer.this.appletShutdown(var2);
            }

            AppletViewer.this.appletSystemExit();
         }
      })).start();
   }

   public void processUserAction(ActionEvent var1) {
      String var2 = ((MenuItem)var1.getSource()).getLabel();
      if (amh.getMessage("menuitem.restart").equals(var2)) {
         this.appletRestart();
      } else if (amh.getMessage("menuitem.reload").equals(var2)) {
         this.appletReload();
      } else if (amh.getMessage("menuitem.clone").equals(var2)) {
         this.appletClone();
      } else if (amh.getMessage("menuitem.stop").equals(var2)) {
         this.appletStop();
      } else if (amh.getMessage("menuitem.save").equals(var2)) {
         this.appletSave();
      } else if (amh.getMessage("menuitem.start").equals(var2)) {
         this.appletStart();
      } else if (amh.getMessage("menuitem.tag").equals(var2)) {
         this.appletTag();
      } else if (amh.getMessage("menuitem.info").equals(var2)) {
         this.appletInfo();
      } else if (amh.getMessage("menuitem.encoding").equals(var2)) {
         this.appletCharacterEncoding();
      } else if (amh.getMessage("menuitem.edit").equals(var2)) {
         this.appletEdit();
      } else if (amh.getMessage("menuitem.print").equals(var2)) {
         this.appletPrint();
      } else if (amh.getMessage("menuitem.props").equals(var2)) {
         networkProperties();
      } else if (amh.getMessage("menuitem.close").equals(var2)) {
         this.appletClose();
      } else if (this.factory.isStandalone() && amh.getMessage("menuitem.quit").equals(var2)) {
         this.appletQuit();
      }
   }

   public static int countApplets() {
      return appletPanels.size();
   }

   public static void skipSpace(Reader var0) throws IOException {
      while(c >= 0 && (c == 32 || c == 9 || c == 10 || c == 13)) {
         c = var0.read();
      }

   }

   public static String scanIdentifier(Reader var0) throws IOException {
      StringBuffer var1;
      for(var1 = new StringBuffer(); c >= 97 && c <= 122 || c >= 65 && c <= 90 || c >= 48 && c <= 57 || c == 95; c = var0.read()) {
         var1.append((char)c);
      }

      return var1.toString();
   }

   public static Hashtable scanTag(Reader var0) throws IOException {
      Hashtable var1 = new Hashtable();
      skipSpace(var0);

      while(c >= 0 && c != 62) {
         String var2 = scanIdentifier(var0);
         String var3 = "";
         skipSpace(var0);
         if (c == 61) {
            int var4 = -1;
            c = var0.read();
            skipSpace(var0);
            if (c == 39 || c == 34) {
               var4 = c;
               c = var0.read();
            }

            StringBuffer var5;
            for(var5 = new StringBuffer(); c > 0 && (var4 < 0 && c != 32 && c != 9 && c != 10 && c != 13 && c != 62 || var4 >= 0 && c != var4); c = var0.read()) {
               var5.append((char)c);
            }

            if (c == var4) {
               c = var0.read();
            }

            skipSpace(var0);
            var3 = var5.toString();
         }

         if (!var3.equals("")) {
            var1.put(var2.toLowerCase(Locale.ENGLISH), var3);
         }

         while(c != 62 && c >= 0 && (c < 97 || c > 122) && (c < 65 || c > 90) && (c < 48 || c > 57) && c != 95) {
            c = var0.read();
         }
      }

      return var1;
   }

   private static Reader makeReader(InputStream var0) {
      if (encoding != null) {
         try {
            return new BufferedReader(new InputStreamReader(var0, encoding));
         } catch (IOException var2) {
         }
      }

      InputStreamReader var1 = new InputStreamReader(var0);
      encoding = var1.getEncoding();
      return new BufferedReader(var1);
   }

   public static void parse(URL var0, String var1) throws IOException {
      encoding = var1;
      parse(var0, System.out, new StdAppletViewerFactory());
   }

   public static void parse(URL var0) throws IOException {
      parse(var0, System.out, new StdAppletViewerFactory());
   }

   public static void parse(URL var0, PrintStream var1, AppletViewerFactory var2) throws IOException {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      String var6 = amh.getMessage("parse.warning.requiresname");
      String var7 = amh.getMessage("parse.warning.paramoutside");
      String var8 = amh.getMessage("parse.warning.applet.requirescode");
      String var9 = amh.getMessage("parse.warning.applet.requiresheight");
      String var10 = amh.getMessage("parse.warning.applet.requireswidth");
      String var11 = amh.getMessage("parse.warning.object.requirescode");
      String var12 = amh.getMessage("parse.warning.object.requiresheight");
      String var13 = amh.getMessage("parse.warning.object.requireswidth");
      String var14 = amh.getMessage("parse.warning.embed.requirescode");
      String var15 = amh.getMessage("parse.warning.embed.requiresheight");
      String var16 = amh.getMessage("parse.warning.embed.requireswidth");
      String var17 = amh.getMessage("parse.warning.appnotLongersupported");
      URLConnection var18 = var0.openConnection();
      Reader var19 = makeReader(var18.getInputStream());
      var0 = var18.getURL();
      int var20 = 1;
      Hashtable var21 = null;

      while(true) {
         String var22;
         do {
            while(true) {
               do {
                  c = var19.read();
                  if (c == -1) {
                     var19.close();
                     return;
                  }
               } while(c != 60);

               c = var19.read();
               if (c == 47) {
                  c = var19.read();
                  var22 = scanIdentifier(var19);
                  break;
               }

               var22 = scanIdentifier(var19);
               Hashtable var23;
               if (var22.equalsIgnoreCase("param")) {
                  var23 = scanTag(var19);
                  String var24 = (String)var23.get("name");
                  if (var24 == null) {
                     var1.println(var6);
                  } else {
                     String var25 = (String)var23.get("value");
                     if (var25 == null) {
                        var1.println(var6);
                     } else if (var21 != null) {
                        var21.put(var24.toLowerCase(), var25);
                     } else {
                        var1.println(var7);
                     }
                  }
               } else if (var22.equalsIgnoreCase("applet")) {
                  var3 = true;
                  var21 = scanTag(var19);
                  if (var21.get("code") == null && var21.get("object") == null) {
                     var1.println(var8);
                     var21 = null;
                  } else if (var21.get("width") == null) {
                     var1.println(var10);
                     var21 = null;
                  } else if (var21.get("height") == null) {
                     var1.println(var9);
                     var21 = null;
                  }
               } else if (var22.equalsIgnoreCase("object")) {
                  var4 = true;
                  var21 = scanTag(var19);
                  if (var21.get("codebase") != null) {
                     var21.remove("codebase");
                  }

                  if (var21.get("width") == null) {
                     var1.println(var13);
                     var21 = null;
                  } else if (var21.get("height") == null) {
                     var1.println(var12);
                     var21 = null;
                  }
               } else if (var22.equalsIgnoreCase("embed")) {
                  var5 = true;
                  var21 = scanTag(var19);
                  if (var21.get("code") == null && var21.get("object") == null) {
                     var1.println(var14);
                     var21 = null;
                  } else if (var21.get("width") == null) {
                     var1.println(var16);
                     var21 = null;
                  } else if (var21.get("height") == null) {
                     var1.println(var15);
                     var21 = null;
                  }
               } else if (var22.equalsIgnoreCase("app")) {
                  var1.println(var17);
                  var23 = scanTag(var19);
                  var22 = (String)var23.get("class");
                  if (var22 != null) {
                     var23.remove("class");
                     var23.put("code", var22 + ".class");
                  }

                  var22 = (String)var23.get("src");
                  if (var22 != null) {
                     var23.remove("src");
                     var23.put("codebase", var22);
                  }

                  if (var23.get("width") == null) {
                     var23.put("width", "100");
                  }

                  if (var23.get("height") == null) {
                     var23.put("height", "100");
                  }

                  printTag(var1, var23);
                  var1.println();
               }
            }
         } while(!var22.equalsIgnoreCase("applet") && !var22.equalsIgnoreCase("object") && !var22.equalsIgnoreCase("embed"));

         if (var4 && var21.get("code") == null && var21.get("object") == null) {
            var1.println(var11);
            var21 = null;
         }

         if (var21 != null) {
            var2.createAppletViewer(x, y, var0, var21);
            x += 30;
            y += 30;
            Dimension var26 = Toolkit.getDefaultToolkit().getScreenSize();
            if (x > var26.width - 300 || y > var26.height - 300) {
               x = 0;
               y = 2 * var20 * 30;
               ++var20;
            }
         }

         var21 = null;
         var3 = false;
         var4 = false;
         var5 = false;
      }
   }

   /** @deprecated */
   @Deprecated
   public static void main(String[] var0) {
      Main.main(var0);
   }

   private static void checkConnect(URL var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         try {
            Permission var2 = var0.openConnection().getPermission();
            if (var2 != null) {
               var1.checkPermission(var2);
            } else {
               var1.checkConnect(var0.getHost(), var0.getPort());
            }
         } catch (IOException var3) {
            var1.checkConnect(var0.getHost(), var0.getPort());
         }
      }

   }

   static {
      systemParam.put("codebase", "codebase");
      systemParam.put("code", "code");
      systemParam.put("alt", "alt");
      systemParam.put("width", "width");
      systemParam.put("height", "height");
      systemParam.put("align", "align");
      systemParam.put("vspace", "vspace");
      systemParam.put("hspace", "hspace");
      x = 0;
      y = 0;
      encoding = null;
      amh = new AppletMessageHandler("appletviewer");
   }

   private final class UserActionListener implements ActionListener {
      private UserActionListener() {
      }

      public void actionPerformed(ActionEvent var1) {
         AppletViewer.this.processUserAction(var1);
      }

      // $FF: synthetic method
      UserActionListener(Object var2) {
         this();
      }
   }
}
