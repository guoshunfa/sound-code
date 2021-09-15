package java.awt;

import java.awt.peer.DesktopPeer;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import sun.awt.AppContext;
import sun.awt.DesktopBrowse;
import sun.awt.SunToolkit;

public class Desktop {
   private DesktopPeer peer = Toolkit.getDefaultToolkit().createDesktopPeer(this);

   private Desktop() {
   }

   public static synchronized Desktop getDesktop() {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else if (!isDesktopSupported()) {
         throw new UnsupportedOperationException("Desktop API is not supported on the current platform");
      } else {
         AppContext var0 = AppContext.getAppContext();
         Desktop var1 = (Desktop)var0.get(Desktop.class);
         if (var1 == null) {
            var1 = new Desktop();
            var0.put(Desktop.class, var1);
         }

         return var1;
      }
   }

   public static boolean isDesktopSupported() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      return var0 instanceof SunToolkit ? ((SunToolkit)var0).isDesktopSupported() : false;
   }

   public boolean isSupported(Desktop.Action var1) {
      return this.peer.isSupported(var1);
   }

   private static void checkFileValidation(File var0) {
      if (!var0.exists()) {
         throw new IllegalArgumentException("The file: " + var0.getPath() + " doesn't exist.");
      }
   }

   private void checkActionSupport(Desktop.Action var1) {
      if (!this.isSupported(var1)) {
         throw new UnsupportedOperationException("The " + var1.name() + " action is not supported on the current platform!");
      }
   }

   private void checkAWTPermission() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new AWTPermission("showWindowWithoutWarningBanner"));
      }

   }

   public void open(File var1) throws IOException {
      var1 = new File(var1.getPath());
      this.checkAWTPermission();
      this.checkExec();
      this.checkActionSupport(Desktop.Action.OPEN);
      checkFileValidation(var1);
      this.peer.open(var1);
   }

   public void edit(File var1) throws IOException {
      var1 = new File(var1.getPath());
      this.checkAWTPermission();
      this.checkExec();
      this.checkActionSupport(Desktop.Action.EDIT);
      var1.canWrite();
      checkFileValidation(var1);
      this.peer.edit(var1);
   }

   public void print(File var1) throws IOException {
      var1 = new File(var1.getPath());
      this.checkExec();
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPrintJobAccess();
      }

      this.checkActionSupport(Desktop.Action.PRINT);
      checkFileValidation(var1);
      this.peer.print(var1);
   }

   public void browse(URI var1) throws IOException {
      SecurityException var2 = null;

      try {
         this.checkAWTPermission();
         this.checkExec();
      } catch (SecurityException var6) {
         var2 = var6;
      }

      this.checkActionSupport(Desktop.Action.BROWSE);
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 == null) {
         this.peer.browse(var1);
      } else {
         URL var3 = null;

         try {
            var3 = var1.toURL();
         } catch (MalformedURLException var5) {
            throw new IllegalArgumentException("Unable to convert URI to URL", var5);
         }

         DesktopBrowse var4 = DesktopBrowse.getInstance();
         if (var4 == null) {
            throw var2;
         } else {
            var4.browse(var3);
         }
      }
   }

   public void mail() throws IOException {
      this.checkAWTPermission();
      this.checkExec();
      this.checkActionSupport(Desktop.Action.MAIL);
      URI var1 = null;

      try {
         var1 = new URI("mailto:?");
         this.peer.mail(var1);
      } catch (URISyntaxException var3) {
      }

   }

   public void mail(URI var1) throws IOException {
      this.checkAWTPermission();
      this.checkExec();
      this.checkActionSupport(Desktop.Action.MAIL);
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!"mailto".equalsIgnoreCase(var1.getScheme())) {
         throw new IllegalArgumentException("URI scheme is not \"mailto\"");
      } else {
         this.peer.mail(var1);
      }
   }

   private void checkExec() throws SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new FilePermission("<<ALL FILES>>", "execute"));
      }

   }

   public static enum Action {
      OPEN,
      EDIT,
      PRINT,
      MAIL,
      BROWSE;
   }
}
