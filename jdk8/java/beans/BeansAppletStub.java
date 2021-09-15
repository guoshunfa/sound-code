package java.beans;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;

class BeansAppletStub implements AppletStub {
   transient boolean active;
   transient Applet target;
   transient AppletContext context;
   transient URL codeBase;
   transient URL docBase;

   BeansAppletStub(Applet var1, AppletContext var2, URL var3, URL var4) {
      this.target = var1;
      this.context = var2;
      this.codeBase = var3;
      this.docBase = var4;
   }

   public boolean isActive() {
      return this.active;
   }

   public URL getDocumentBase() {
      return this.docBase;
   }

   public URL getCodeBase() {
      return this.codeBase;
   }

   public String getParameter(String var1) {
      return null;
   }

   public AppletContext getAppletContext() {
      return this.context;
   }

   public void appletResize(int var1, int var2) {
   }
}
