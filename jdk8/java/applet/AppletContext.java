package java.applet;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

public interface AppletContext {
   AudioClip getAudioClip(URL var1);

   Image getImage(URL var1);

   Applet getApplet(String var1);

   Enumeration<Applet> getApplets();

   void showDocument(URL var1);

   void showDocument(URL var1, String var2);

   void showStatus(String var1);

   void setStream(String var1, InputStream var2) throws IOException;

   InputStream getStream(String var1);

   Iterator<String> getStreamKeys();
}
