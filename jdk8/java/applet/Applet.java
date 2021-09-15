package java.applet;

import java.awt.AWTPermission;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Panel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.applet.AppletAudioClip;

public class Applet extends Panel {
   private transient AppletStub stub;
   private static final long serialVersionUID = -5836846270535785031L;
   AccessibleContext accessibleContext = null;

   public Applet() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      }
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         var1.defaultReadObject();
      }
   }

   public final void setStub(AppletStub var1) {
      if (this.stub != null) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkPermission(new AWTPermission("setAppletStub"));
         }
      }

      this.stub = var1;
   }

   public boolean isActive() {
      return this.stub != null ? this.stub.isActive() : false;
   }

   public URL getDocumentBase() {
      return this.stub.getDocumentBase();
   }

   public URL getCodeBase() {
      return this.stub.getCodeBase();
   }

   public String getParameter(String var1) {
      return this.stub.getParameter(var1);
   }

   public AppletContext getAppletContext() {
      return this.stub.getAppletContext();
   }

   public void resize(int var1, int var2) {
      Dimension var3 = this.size();
      if (var3.width != var1 || var3.height != var2) {
         super.resize(var1, var2);
         if (this.stub != null) {
            this.stub.appletResize(var1, var2);
         }
      }

   }

   public void resize(Dimension var1) {
      this.resize(var1.width, var1.height);
   }

   public boolean isValidateRoot() {
      return true;
   }

   public void showStatus(String var1) {
      this.getAppletContext().showStatus(var1);
   }

   public Image getImage(URL var1) {
      return this.getAppletContext().getImage(var1);
   }

   public Image getImage(URL var1, String var2) {
      try {
         return this.getImage(new URL(var1, var2));
      } catch (MalformedURLException var4) {
         return null;
      }
   }

   public static final AudioClip newAudioClip(URL var0) {
      return new AppletAudioClip(var0);
   }

   public AudioClip getAudioClip(URL var1) {
      return this.getAppletContext().getAudioClip(var1);
   }

   public AudioClip getAudioClip(URL var1, String var2) {
      try {
         return this.getAudioClip(new URL(var1, var2));
      } catch (MalformedURLException var4) {
         return null;
      }
   }

   public String getAppletInfo() {
      return null;
   }

   public Locale getLocale() {
      Locale var1 = super.getLocale();
      return var1 == null ? Locale.getDefault() : var1;
   }

   public String[][] getParameterInfo() {
      return (String[][])null;
   }

   public void play(URL var1) {
      AudioClip var2 = this.getAudioClip(var1);
      if (var2 != null) {
         var2.play();
      }

   }

   public void play(URL var1, String var2) {
      AudioClip var3 = this.getAudioClip(var1, var2);
      if (var3 != null) {
         var3.play();
      }

   }

   public void init() {
   }

   public void start() {
   }

   public void stop() {
   }

   public void destroy() {
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Applet.AccessibleApplet();
      }

      return this.accessibleContext;
   }

   protected class AccessibleApplet extends Panel.AccessibleAWTPanel {
      private static final long serialVersionUID = 8127374778187708896L;

      protected AccessibleApplet() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.FRAME;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         var1.add(AccessibleState.ACTIVE);
         return var1;
      }
   }
}
