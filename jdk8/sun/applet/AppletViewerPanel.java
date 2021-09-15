package sun.applet;

import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

class AppletViewerPanel extends AppletPanel {
   static boolean debug = false;
   URL documentURL;
   URL baseURL;
   Hashtable atts;
   private static final long serialVersionUID = 8890989370785545619L;

   AppletViewerPanel(URL var1, Hashtable var2) {
      this.documentURL = var1;
      this.atts = var2;
      String var3 = this.getParameter("codebase");
      if (var3 != null) {
         if (!var3.endsWith("/")) {
            var3 = var3 + "/";
         }

         try {
            this.baseURL = new URL(var1, var3);
         } catch (MalformedURLException var8) {
         }
      }

      if (this.baseURL == null) {
         String var4 = var1.getFile();
         int var5 = var4.lastIndexOf(47);
         if (var5 >= 0 && var5 < var4.length() - 1) {
            try {
               this.baseURL = new URL(var1, var4.substring(0, var5 + 1));
            } catch (MalformedURLException var7) {
            }
         }
      }

      if (this.baseURL == null) {
         this.baseURL = var1;
      }

   }

   public String getParameter(String var1) {
      return (String)this.atts.get(var1.toLowerCase());
   }

   public URL getDocumentBase() {
      return this.documentURL;
   }

   public URL getCodeBase() {
      return this.baseURL;
   }

   public int getWidth() {
      String var1 = this.getParameter("width");
      return var1 != null ? Integer.valueOf(var1) : 0;
   }

   public int getHeight() {
      String var1 = this.getParameter("height");
      return var1 != null ? Integer.valueOf(var1) : 0;
   }

   public boolean hasInitialFocus() {
      if (!this.isJDK11Applet() && !this.isJDK12Applet()) {
         String var1 = this.getParameter("initial_focus");
         return var1 == null || !var1.toLowerCase().equals("false");
      } else {
         return false;
      }
   }

   public String getCode() {
      return this.getParameter("code");
   }

   public String getJarFiles() {
      return this.getParameter("archive");
   }

   public String getSerializedObject() {
      return this.getParameter("object");
   }

   public AppletContext getAppletContext() {
      return (AppletContext)this.getParent();
   }

   static void debug(String var0) {
      if (debug) {
         System.err.println("AppletViewerPanel:::" + var0);
      }

   }

   static void debug(String var0, Throwable var1) {
      if (debug) {
         var1.printStackTrace();
         debug(var0);
      }

   }
}
