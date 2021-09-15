package com.sun.media.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public final class SoftMixingMixer implements Mixer {
   static final String INFO_NAME = "Gervill Sound Mixer";
   static final String INFO_VENDOR = "OpenJDK Proposal";
   static final String INFO_DESCRIPTION = "Software Sound Mixer";
   static final String INFO_VERSION = "1.0";
   static final Mixer.Info info = new SoftMixingMixer.Info();
   final Object control_mutex = this;
   boolean implicitOpen = false;
   private boolean open = false;
   private SoftMixingMainMixer mainmixer = null;
   private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
   private SourceDataLine sourceDataLine = null;
   private SoftAudioPusher pusher = null;
   private AudioInputStream pusher_stream = null;
   private final float controlrate = 147.0F;
   private final long latency = 100000L;
   private final boolean jitter_correction = false;
   private final List<LineListener> listeners = new ArrayList();
   private final Line.Info[] sourceLineInfo = new Line.Info[2];

   public SoftMixingMixer() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 1; var2 <= 2; ++var2) {
         var1.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 8, var2, var2, -1.0F, false));
         var1.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, 8, var2, var2, -1.0F, false));

         for(int var3 = 16; var3 < 32; var3 += 8) {
            var1.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, var3, var2, var2 * var3 / 8, -1.0F, false));
            var1.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, var3, var2, var2 * var3 / 8, -1.0F, false));
            var1.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, var3, var2, var2 * var3 / 8, -1.0F, true));
            var1.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, var3, var2, var2 * var3 / 8, -1.0F, true));
         }

         var1.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, var2, var2 * 4, -1.0F, false));
         var1.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, var2, var2 * 4, -1.0F, true));
         var1.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, var2, var2 * 8, -1.0F, false));
         var1.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, var2, var2 * 8, -1.0F, true));
      }

      AudioFormat[] var4 = (AudioFormat[])var1.toArray(new AudioFormat[var1.size()]);
      this.sourceLineInfo[0] = new DataLine.Info(SourceDataLine.class, var4, -1, -1);
      this.sourceLineInfo[1] = new DataLine.Info(Clip.class, var4, -1, -1);
   }

   public Line getLine(Line.Info var1) throws LineUnavailableException {
      if (!this.isLineSupported(var1)) {
         throw new IllegalArgumentException("Line unsupported: " + var1);
      } else if (var1.getLineClass() == SourceDataLine.class) {
         return new SoftMixingSourceDataLine(this, (DataLine.Info)var1);
      } else if (var1.getLineClass() == Clip.class) {
         return new SoftMixingClip(this, (DataLine.Info)var1);
      } else {
         throw new IllegalArgumentException("Line unsupported: " + var1);
      }
   }

   public int getMaxLines(Line.Info var1) {
      if (var1.getLineClass() == SourceDataLine.class) {
         return -1;
      } else {
         return var1.getLineClass() == Clip.class ? -1 : 0;
      }
   }

   public Mixer.Info getMixerInfo() {
      return info;
   }

   public Line.Info[] getSourceLineInfo() {
      Line.Info[] var1 = new Line.Info[this.sourceLineInfo.length];
      System.arraycopy(this.sourceLineInfo, 0, var1, 0, this.sourceLineInfo.length);
      return var1;
   }

   public Line.Info[] getSourceLineInfo(Line.Info var1) {
      ArrayList var3 = new ArrayList();

      for(int var2 = 0; var2 < this.sourceLineInfo.length; ++var2) {
         if (var1.matches(this.sourceLineInfo[var2])) {
            var3.add(this.sourceLineInfo[var2]);
         }
      }

      return (Line.Info[])var3.toArray(new Line.Info[var3.size()]);
   }

   public Line[] getSourceLines() {
      synchronized(this.control_mutex) {
         if (this.mainmixer == null) {
            return new Line[0];
         } else {
            SoftMixingDataLine[] var3 = this.mainmixer.getOpenLines();
            Line[] var1 = new Line[var3.length];

            for(int var4 = 0; var4 < var1.length; ++var4) {
               var1[var4] = var3[var4];
            }

            return var1;
         }
      }
   }

   public Line.Info[] getTargetLineInfo() {
      return new Line.Info[0];
   }

   public Line.Info[] getTargetLineInfo(Line.Info var1) {
      return new Line.Info[0];
   }

   public Line[] getTargetLines() {
      return new Line[0];
   }

   public boolean isLineSupported(Line.Info var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < this.sourceLineInfo.length; ++var2) {
            if (var1.matches(this.sourceLineInfo[var2])) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isSynchronizationSupported(Line[] var1, boolean var2) {
      return false;
   }

   public void synchronize(Line[] var1, boolean var2) {
      throw new IllegalArgumentException("Synchronization not supported by this mixer.");
   }

   public void unsynchronize(Line[] var1) {
      throw new IllegalArgumentException("Synchronization not supported by this mixer.");
   }

   public void addLineListener(LineListener var1) {
      synchronized(this.control_mutex) {
         this.listeners.add(var1);
      }
   }

   private void sendEvent(LineEvent var1) {
      if (this.listeners.size() != 0) {
         LineListener[] var2 = (LineListener[])this.listeners.toArray(new LineListener[this.listeners.size()]);
         LineListener[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            LineListener var6 = var3[var5];
            var6.update(var1);
         }

      }
   }

   public void close() {
      if (this.isOpen()) {
         this.sendEvent(new LineEvent(this, LineEvent.Type.CLOSE, -1L));
         SoftAudioPusher var1 = null;
         AudioInputStream var2 = null;
         synchronized(this.control_mutex) {
            if (this.pusher != null) {
               var1 = this.pusher;
               var2 = this.pusher_stream;
               this.pusher = null;
               this.pusher_stream = null;
            }
         }

         if (var1 != null) {
            var1.stop();

            try {
               var2.close();
            } catch (IOException var7) {
               var7.printStackTrace();
            }
         }

         synchronized(this.control_mutex) {
            if (this.mainmixer != null) {
               this.mainmixer.close();
            }

            this.open = false;
            if (this.sourceDataLine != null) {
               this.sourceDataLine.drain();
               this.sourceDataLine.close();
               this.sourceDataLine = null;
            }

         }
      }
   }

   public Control getControl(Control.Type var1) {
      throw new IllegalArgumentException("Unsupported control type : " + var1);
   }

   public Control[] getControls() {
      return new Control[0];
   }

   public Line.Info getLineInfo() {
      return new Line.Info(Mixer.class);
   }

   public boolean isControlSupported(Control.Type var1) {
      return false;
   }

   public boolean isOpen() {
      synchronized(this.control_mutex) {
         return this.open;
      }
   }

   public void open() throws LineUnavailableException {
      if (this.isOpen()) {
         this.implicitOpen = false;
      } else {
         this.open((SourceDataLine)null);
      }
   }

   public void open(SourceDataLine var1) throws LineUnavailableException {
      if (this.isOpen()) {
         this.implicitOpen = false;
      } else {
         synchronized(this.control_mutex) {
            try {
               if (var1 != null) {
                  this.format = var1.getFormat();
               }

               AudioInputStream var3 = this.openStream(this.getFormat());
               if (var1 == null) {
                  synchronized(SoftMixingMixerProvider.mutex) {
                     SoftMixingMixerProvider.lockthread = Thread.currentThread();
                  }

                  boolean var30 = false;

                  try {
                     var30 = true;
                     Mixer var4 = AudioSystem.getMixer((Mixer.Info)null);
                     if (var4 != null) {
                        DataLine.Info var5 = null;
                        AudioFormat var6 = null;
                        Line.Info[] var7 = var4.getSourceLineInfo();

                        label313:
                        for(int var8 = 0; var8 < var7.length; ++var8) {
                           if (var7[var8].getLineClass() == SourceDataLine.class) {
                              DataLine.Info var9 = (DataLine.Info)var7[var8];
                              AudioFormat[] var10 = var9.getFormats();

                              for(int var11 = 0; var11 < var10.length; ++var11) {
                                 AudioFormat var12 = var10[var11];
                                 if ((var12.getChannels() == 2 || var12.getChannels() == -1) && (var12.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || var12.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && (var12.getSampleRate() == -1.0F || (double)var12.getSampleRate() == 48000.0D) && (var12.getSampleSizeInBits() == -1 || var12.getSampleSizeInBits() == 16)) {
                                    var5 = var9;
                                    int var13 = var12.getChannels();
                                    boolean var14 = var12.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
                                    float var15 = var12.getSampleRate();
                                    boolean var16 = var12.isBigEndian();
                                    int var17 = var12.getSampleSizeInBits();
                                    if (var17 == -1) {
                                       var17 = 16;
                                    }

                                    if (var13 == -1) {
                                       var13 = 2;
                                    }

                                    if (var15 == -1.0F) {
                                       var15 = 48000.0F;
                                    }

                                    var6 = new AudioFormat(var15, var17, var13, var14, var16);
                                    break label313;
                                 }
                              }
                           }
                        }

                        if (var6 != null) {
                           this.format = var6;
                           var1 = (SourceDataLine)var4.getLine(var5);
                        }
                     }

                     if (var1 == null) {
                        var1 = AudioSystem.getSourceDataLine(this.format);
                        var30 = false;
                     } else {
                        var30 = false;
                     }
                  } finally {
                     if (var30) {
                        synchronized(SoftMixingMixerProvider.mutex) {
                           SoftMixingMixerProvider.lockthread = null;
                        }
                     }
                  }

                  synchronized(SoftMixingMixerProvider.mutex) {
                     SoftMixingMixerProvider.lockthread = null;
                  }

                  if (var1 == null) {
                     throw new IllegalArgumentException("No line matching " + info.toString() + " is supported.");
                  }
               }

               this.getClass();
               double var38 = 100000.0D;
               int var39;
               if (!var1.isOpen()) {
                  var39 = this.getFormat().getFrameSize() * (int)((double)this.getFormat().getFrameRate() * (var38 / 1000000.0D));
                  var1.open(this.getFormat(), var39);
                  this.sourceDataLine = var1;
               }

               if (!var1.isActive()) {
                  var1.start();
               }

               var39 = 512;

               try {
                  var39 = var3.available();
               } catch (IOException var32) {
               }

               int var40 = var1.getBufferSize();
               var40 -= var40 % var39;
               if (var40 < 3 * var39) {
                  var40 = 3 * var39;
               }

               this.pusher = new SoftAudioPusher(var1, var3, var39);
               this.pusher_stream = var3;
               this.pusher.start();
            } catch (LineUnavailableException var36) {
               if (this.isOpen()) {
                  this.close();
               }

               throw new LineUnavailableException(var36.toString());
            }

         }
      }
   }

   public AudioInputStream openStream(AudioFormat var1) throws LineUnavailableException {
      if (this.isOpen()) {
         throw new LineUnavailableException("Mixer is already open");
      } else {
         synchronized(this.control_mutex) {
            this.open = true;
            this.implicitOpen = false;
            if (var1 != null) {
               this.format = var1;
            }

            this.mainmixer = new SoftMixingMainMixer(this);
            this.sendEvent(new LineEvent(this, LineEvent.Type.OPEN, -1L));
            return this.mainmixer.getInputStream();
         }
      }
   }

   public void removeLineListener(LineListener var1) {
      synchronized(this.control_mutex) {
         this.listeners.remove(var1);
      }
   }

   public long getLatency() {
      synchronized(this.control_mutex) {
         return 100000L;
      }
   }

   public AudioFormat getFormat() {
      synchronized(this.control_mutex) {
         return this.format;
      }
   }

   float getControlRate() {
      return 147.0F;
   }

   SoftMixingMainMixer getMainMixer() {
      return !this.isOpen() ? null : this.mainmixer;
   }

   private static class Info extends Mixer.Info {
      Info() {
         super("Gervill Sound Mixer", "OpenJDK Proposal", "Software Sound Mixer", "1.0");
      }
   }
}
