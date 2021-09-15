package java.applet;

import java.net.URL;

public interface AppletStub {
   boolean isActive();

   URL getDocumentBase();

   URL getCodeBase();

   String getParameter(String var1);

   AppletContext getAppletContext();

   void appletResize(int var1, int var2);
}
