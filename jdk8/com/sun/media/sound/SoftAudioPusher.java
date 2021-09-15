package com.sun.media.sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public final class SoftAudioPusher implements Runnable {
   private volatile boolean active = false;
   private SourceDataLine sourceDataLine = null;
   private Thread audiothread;
   private final AudioInputStream ais;
   private final byte[] buffer;

   public SoftAudioPusher(SourceDataLine var1, AudioInputStream var2, int var3) {
      this.ais = var2;
      this.buffer = new byte[var3];
      this.sourceDataLine = var1;
   }

   public synchronized void start() {
      if (!this.active) {
         this.active = true;
         this.audiothread = new Thread(this);
         this.audiothread.setDaemon(true);
         this.audiothread.setPriority(10);
         this.audiothread.start();
      }
   }

   public synchronized void stop() {
      if (this.active) {
         this.active = false;

         try {
            this.audiothread.join();
         } catch (InterruptedException var2) {
         }

      }
   }

   public void run() {
      byte[] var1 = this.buffer;
      AudioInputStream var2 = this.ais;
      SourceDataLine var3 = this.sourceDataLine;

      try {
         while(this.active) {
            int var4 = var2.read(var1);
            if (var4 < 0) {
               break;
            }

            var3.write(var1, 0, var4);
         }
      } catch (IOException var5) {
         this.active = false;
      }

   }
}
