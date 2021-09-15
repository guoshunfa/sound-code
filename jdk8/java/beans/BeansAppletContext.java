package java.beans;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

class BeansAppletContext implements AppletContext {
   Applet target;
   Hashtable<URL, Object> imageCache = new Hashtable();

   BeansAppletContext(Applet var1) {
      this.target = var1;
   }

   public AudioClip getAudioClip(URL var1) {
      try {
         return (AudioClip)var1.getContent();
      } catch (Exception var3) {
         return null;
      }
   }

   public synchronized Image getImage(URL var1) {
      Object var2 = this.imageCache.get(var1);
      if (var2 != null) {
         return (Image)var2;
      } else {
         try {
            var2 = var1.getContent();
            if (var2 == null) {
               return null;
            } else if (var2 instanceof Image) {
               this.imageCache.put(var1, var2);
               return (Image)var2;
            } else {
               Image var3 = this.target.createImage((ImageProducer)var2);
               this.imageCache.put(var1, var3);
               return var3;
            }
         } catch (Exception var4) {
            return null;
         }
      }
   }

   public Applet getApplet(String var1) {
      return null;
   }

   public Enumeration<Applet> getApplets() {
      Vector var1 = new Vector();
      var1.addElement(this.target);
      return var1.elements();
   }

   public void showDocument(URL var1) {
   }

   public void showDocument(URL var1, String var2) {
   }

   public void showStatus(String var1) {
   }

   public void setStream(String var1, InputStream var2) throws IOException {
   }

   public InputStream getStream(String var1) {
      return null;
   }

   public Iterator<String> getStreamKeys() {
      return null;
   }
}
