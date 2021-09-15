package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public final class DataPusher implements Runnable {
   private static final int AUTO_CLOSE_TIME = 5000;
   private static final boolean DEBUG = false;
   private final SourceDataLine source;
   private final AudioFormat format;
   private final AudioInputStream ais;
   private final byte[] audioData;
   private final int audioDataByteLength;
   private int pos;
   private int newPos;
   private boolean looping;
   private Thread pushThread;
   private int wantedState;
   private int threadState;
   private final int STATE_NONE;
   private final int STATE_PLAYING;
   private final int STATE_WAITING;
   private final int STATE_STOPPING;
   private final int STATE_STOPPED;
   private final int BUFFER_SIZE;

   public DataPusher(SourceDataLine var1, AudioFormat var2, byte[] var3, int var4) {
      this(var1, var2, (AudioInputStream)null, var3, var4);
   }

   public DataPusher(SourceDataLine var1, AudioInputStream var2) {
      this(var1, var2.getFormat(), var2, (byte[])null, 0);
   }

   private DataPusher(SourceDataLine var1, AudioFormat var2, AudioInputStream var3, byte[] var4, int var5) {
      this.newPos = -1;
      this.pushThread = null;
      this.STATE_NONE = 0;
      this.STATE_PLAYING = 1;
      this.STATE_WAITING = 2;
      this.STATE_STOPPING = 3;
      this.STATE_STOPPED = 4;
      this.BUFFER_SIZE = 16384;
      this.source = var1;
      this.format = var2;
      this.ais = var3;
      this.audioDataByteLength = var5;
      this.audioData = var4 == null ? null : Arrays.copyOf(var4, var4.length);
   }

   public synchronized void start() {
      this.start(false);
   }

   public synchronized void start(boolean var1) {
      try {
         if (this.threadState == 3) {
            this.stop();
         }

         this.looping = var1;
         this.newPos = 0;
         this.wantedState = 1;
         if (!this.source.isOpen()) {
            this.source.open(this.format);
         }

         this.source.flush();
         this.source.start();
         if (this.pushThread == null) {
            this.pushThread = JSSecurityManager.createThread(this, (String)null, false, -1, true);
         }

         this.notifyAll();
      } catch (Exception var3) {
      }

   }

   public synchronized void stop() {
      if (this.threadState != 3 && this.threadState != 4 && this.pushThread != null) {
         this.wantedState = 2;
         if (this.source != null) {
            this.source.flush();
         }

         this.notifyAll();
         int var1 = 50;

         while(var1-- >= 0 && this.threadState == 1) {
            try {
               this.wait(100L);
            } catch (InterruptedException var3) {
            }
         }

      }
   }

   synchronized void close() {
      if (this.source != null) {
         this.source.close();
      }

   }

   public void run() {
      Object var1 = null;
      boolean var2 = this.ais != null;
      byte[] var10;
      if (var2) {
         var10 = new byte[16384];
      } else {
         var10 = this.audioData;
      }

      while(true) {
         while(this.wantedState != 3) {
            if (this.wantedState == 2) {
               try {
                  synchronized(this) {
                     this.threadState = 2;
                     this.wantedState = 3;
                     this.wait(5000L);
                  }
               } catch (InterruptedException var8) {
               }
            } else {
               if (this.newPos >= 0) {
                  this.pos = this.newPos;
                  this.newPos = -1;
               }

               this.threadState = 1;
               int var3 = 16384;
               if (var2) {
                  try {
                     this.pos = 0;
                     var3 = this.ais.read(var10, 0, var10.length);
                  } catch (IOException var9) {
                     var3 = -1;
                  }
               } else {
                  if (var3 > this.audioDataByteLength - this.pos) {
                     var3 = this.audioDataByteLength - this.pos;
                  }

                  if (var3 == 0) {
                     var3 = -1;
                  }
               }

               if (var3 < 0) {
                  if (!var2 && this.looping) {
                     this.pos = 0;
                  } else {
                     this.wantedState = 2;
                     this.source.drain();
                  }
               } else {
                  int var4 = this.source.write(var10, this.pos, var3);
                  this.pos += var4;
               }
            }
         }

         this.threadState = 3;
         this.source.flush();
         this.source.stop();
         this.source.flush();
         this.source.close();
         this.threadState = 4;
         synchronized(this) {
            this.pushThread = null;
            this.notifyAll();
            return;
         }
      }
   }
}
