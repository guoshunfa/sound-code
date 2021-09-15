package sun.applet;

import com.sun.media.sound.JavaSoundAudioClip;
import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AppletAudioClip implements AudioClip {
   private URL url = null;
   private AudioClip audioClip = null;
   boolean DEBUG = false;

   public AppletAudioClip(URL var1) {
      this.url = var1;

      try {
         InputStream var2 = var1.openStream();
         this.createAppletAudioClip(var2);
      } catch (IOException var3) {
         if (this.DEBUG) {
            System.err.println("IOException creating AppletAudioClip" + var3);
         }
      }

   }

   public AppletAudioClip(URLConnection var1) {
      try {
         this.createAppletAudioClip(var1.getInputStream());
      } catch (IOException var3) {
         if (this.DEBUG) {
            System.err.println("IOException creating AppletAudioClip" + var3);
         }
      }

   }

   public AppletAudioClip(byte[] var1) {
      try {
         ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
         this.createAppletAudioClip(var2);
      } catch (IOException var3) {
         if (this.DEBUG) {
            System.err.println("IOException creating AppletAudioClip " + var3);
         }
      }

   }

   void createAppletAudioClip(InputStream var1) throws IOException {
      try {
         this.audioClip = new JavaSoundAudioClip(var1);
      } catch (Exception var3) {
         throw new IOException("Failed to construct the AudioClip: " + var3);
      }
   }

   public synchronized void play() {
      if (this.audioClip != null) {
         this.audioClip.play();
      }

   }

   public synchronized void loop() {
      if (this.audioClip != null) {
         this.audioClip.loop();
      }

   }

   public synchronized void stop() {
      if (this.audioClip != null) {
         this.audioClip.stop();
      }

   }
}
