package sun.audio;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class AudioPlayer extends Thread {
   private final AudioDevice devAudio;
   private static final boolean DEBUG = false;
   public static final AudioPlayer player = getAudioPlayer();

   private static ThreadGroup getAudioThreadGroup() {
      ThreadGroup var0;
      for(var0 = currentThread().getThreadGroup(); var0.getParent() != null && var0.getParent().getParent() != null; var0 = var0.getParent()) {
      }

      return var0;
   }

   private static AudioPlayer getAudioPlayer() {
      PrivilegedAction var1 = new PrivilegedAction() {
         public Object run() {
            AudioPlayer var1 = new AudioPlayer();
            var1.setPriority(10);
            var1.setDaemon(true);
            var1.start();
            return var1;
         }
      };
      AudioPlayer var0 = (AudioPlayer)AccessController.doPrivileged(var1);
      return var0;
   }

   private AudioPlayer() {
      super(getAudioThreadGroup(), "Audio Player");
      this.devAudio = AudioDevice.device;
      this.devAudio.open();
   }

   public synchronized void start(InputStream var1) {
      this.devAudio.openChannel(var1);
      this.notify();
   }

   public synchronized void stop(InputStream var1) {
      this.devAudio.closeChannel(var1);
   }

   public void run() {
      this.devAudio.play();

      while(true) {
         try {
            Thread.sleep(5000L);
         } catch (Exception var2) {
            return;
         }
      }
   }

   // $FF: synthetic method
   AudioPlayer(Object var1) {
      this();
   }
}
