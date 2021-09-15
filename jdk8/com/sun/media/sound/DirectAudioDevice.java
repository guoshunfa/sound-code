package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

final class DirectAudioDevice extends AbstractMixer {
   private static final int CLIP_BUFFER_TIME = 1000;
   private static final int DEFAULT_LINE_BUFFER_TIME = 500;
   private int deviceCountOpened = 0;
   private int deviceCountStarted = 0;

   DirectAudioDevice(DirectAudioDeviceProvider.DirectAudioDeviceInfo var1) {
      super(var1, (Control[])null, (Line.Info[])null, (Line.Info[])null);
      DirectAudioDevice.DirectDLI var2 = this.createDataLineInfo(true);
      if (var2 != null) {
         this.sourceLineInfo = new Line.Info[2];
         this.sourceLineInfo[0] = var2;
         this.sourceLineInfo[1] = new DirectAudioDevice.DirectDLI(Clip.class, var2.getFormats(), var2.getHardwareFormats(), 32, -1);
      } else {
         this.sourceLineInfo = new Line.Info[0];
      }

      DirectAudioDevice.DirectDLI var3 = this.createDataLineInfo(false);
      if (var3 != null) {
         this.targetLineInfo = new Line.Info[1];
         this.targetLineInfo[0] = var3;
      } else {
         this.targetLineInfo = new Line.Info[0];
      }

   }

   private DirectAudioDevice.DirectDLI createDataLineInfo(boolean var1) {
      Vector var2 = new Vector();
      AudioFormat[] var3 = null;
      AudioFormat[] var4 = null;
      synchronized(var2) {
         nGetFormats(this.getMixerIndex(), this.getDeviceID(), var1, var2);
         if (var2.size() > 0) {
            int var6 = var2.size();
            int var7 = var6;
            var3 = new AudioFormat[var6];

            int var8;
            boolean var12;
            for(var8 = 0; var8 < var6; ++var8) {
               AudioFormat var9 = (AudioFormat)var2.elementAt(var8);
               var3[var8] = var9;
               int var10 = var9.getSampleSizeInBits();
               boolean var11 = var9.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
               var12 = var9.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
               if (var11 || var12) {
                  ++var7;
               }
            }

            var4 = new AudioFormat[var7];
            var8 = 0;

            for(int var16 = 0; var16 < var6; ++var16) {
               AudioFormat var17 = var3[var16];
               var4[var8++] = var17;
               int var18 = var17.getSampleSizeInBits();
               var12 = var17.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
               boolean var13 = var17.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
               if (var18 == 8) {
                  if (var12) {
                     var4[var8++] = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var17.getSampleRate(), var18, var17.getChannels(), var17.getFrameSize(), var17.getSampleRate(), var17.isBigEndian());
                  } else if (var13) {
                     var4[var8++] = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var17.getSampleRate(), var18, var17.getChannels(), var17.getFrameSize(), var17.getSampleRate(), var17.isBigEndian());
                  }
               } else if (var18 > 8 && (var12 || var13)) {
                  var4[var8++] = new AudioFormat(var17.getEncoding(), var17.getSampleRate(), var18, var17.getChannels(), var17.getFrameSize(), var17.getSampleRate(), !var17.isBigEndian());
               }
            }

            return var4 != null ? new DirectAudioDevice.DirectDLI(var1 ? SourceDataLine.class : TargetDataLine.class, var4, var3, 32, -1) : null;
         }
      }

      return var4 != null ? new DirectAudioDevice.DirectDLI(var1 ? SourceDataLine.class : TargetDataLine.class, var4, var3, 32, -1) : null;
   }

   public Line getLine(Line.Info var1) throws LineUnavailableException {
      Line.Info var2 = this.getLineInfo(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Line unsupported: " + var1);
      } else {
         if (var2 instanceof DataLine.Info) {
            DataLine.Info var3 = (DataLine.Info)var2;
            int var5 = -1;
            AudioFormat[] var6 = null;
            if (var1 instanceof DataLine.Info) {
               var6 = ((DataLine.Info)var1).getFormats();
               var5 = ((DataLine.Info)var1).getMaxBufferSize();
            }

            AudioFormat var4;
            if (var6 != null && var6.length != 0) {
               var4 = var6[var6.length - 1];
               if (!Toolkit.isFullySpecifiedPCMFormat(var4)) {
                  var4 = null;
               }
            } else {
               var4 = null;
            }

            if (var3.getLineClass().isAssignableFrom(DirectAudioDevice.DirectSDL.class)) {
               return new DirectAudioDevice.DirectSDL(var3, var4, var5, this);
            }

            if (var3.getLineClass().isAssignableFrom(DirectAudioDevice.DirectClip.class)) {
               return new DirectAudioDevice.DirectClip(var3, var4, var5, this);
            }

            if (var3.getLineClass().isAssignableFrom(DirectAudioDevice.DirectTDL.class)) {
               return new DirectAudioDevice.DirectTDL(var3, var4, var5, this);
            }
         }

         throw new IllegalArgumentException("Line unsupported: " + var1);
      }
   }

   public int getMaxLines(Line.Info var1) {
      Line.Info var2 = this.getLineInfo(var1);
      if (var2 == null) {
         return 0;
      } else {
         return var2 instanceof DataLine.Info ? this.getMaxSimulLines() : 0;
      }
   }

   protected void implOpen() throws LineUnavailableException {
   }

   protected void implClose() {
   }

   protected void implStart() {
   }

   protected void implStop() {
   }

   int getMixerIndex() {
      return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)this.getMixerInfo()).getIndex();
   }

   int getDeviceID() {
      return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)this.getMixerInfo()).getDeviceID();
   }

   int getMaxSimulLines() {
      return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)this.getMixerInfo()).getMaxSimulLines();
   }

   private static void addFormat(Vector var0, int var1, int var2, int var3, float var4, int var5, boolean var6, boolean var7) {
      AudioFormat.Encoding var8 = null;
      switch(var5) {
      case 0:
         var8 = var6 ? AudioFormat.Encoding.PCM_SIGNED : AudioFormat.Encoding.PCM_UNSIGNED;
         break;
      case 1:
         var8 = AudioFormat.Encoding.ULAW;
         if (var1 != 8) {
            var1 = 8;
            var2 = var3;
         }
         break;
      case 2:
         var8 = AudioFormat.Encoding.ALAW;
         if (var1 != 8) {
            var1 = 8;
            var2 = var3;
         }
      }

      if (var8 != null) {
         if (var2 <= 0) {
            if (var3 > 0) {
               var2 = (var1 + 7) / 8 * var3;
            } else {
               var2 = -1;
            }
         }

         var0.add(new AudioFormat(var8, var4, var1, var3, var2, var4, var7));
      }
   }

   protected static AudioFormat getSignOrEndianChangedFormat(AudioFormat var0) {
      boolean var1 = var0.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
      boolean var2 = var0.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
      if (var0.getSampleSizeInBits() > 8 && var1) {
         return new AudioFormat(var0.getEncoding(), var0.getSampleRate(), var0.getSampleSizeInBits(), var0.getChannels(), var0.getFrameSize(), var0.getFrameRate(), !var0.isBigEndian());
      } else {
         return var0.getSampleSizeInBits() != 8 || !var1 && !var2 ? null : new AudioFormat(var1 ? AudioFormat.Encoding.PCM_UNSIGNED : AudioFormat.Encoding.PCM_SIGNED, var0.getSampleRate(), var0.getSampleSizeInBits(), var0.getChannels(), var0.getFrameSize(), var0.getFrameRate(), var0.isBigEndian());
      }
   }

   private static native void nGetFormats(int var0, int var1, boolean var2, Vector var3);

   private static native long nOpen(int var0, int var1, boolean var2, int var3, float var4, int var5, int var6, int var7, boolean var8, boolean var9, int var10) throws LineUnavailableException;

   private static native void nStart(long var0, boolean var2);

   private static native void nStop(long var0, boolean var2);

   private static native void nClose(long var0, boolean var2);

   private static native int nWrite(long var0, byte[] var2, int var3, int var4, int var5, float var6, float var7);

   private static native int nRead(long var0, byte[] var2, int var3, int var4, int var5);

   private static native int nGetBufferSize(long var0, boolean var2);

   private static native boolean nIsStillDraining(long var0, boolean var2);

   private static native void nFlush(long var0, boolean var2);

   private static native int nAvailable(long var0, boolean var2);

   private static native long nGetBytePosition(long var0, boolean var2, long var3);

   private static native void nSetBytePosition(long var0, boolean var2, long var3);

   private static native boolean nRequiresServicing(long var0, boolean var2);

   private static native void nService(long var0, boolean var2);

   private static class DirectBAOS extends ByteArrayOutputStream {
      DirectBAOS() {
      }

      public byte[] getInternalBuffer() {
         return this.buf;
      }
   }

   private static final class DirectClip extends DirectAudioDevice.DirectDL implements Clip, Runnable, AutoClosingClip {
      private volatile Thread thread;
      private volatile byte[] audioData;
      private volatile int frameSize;
      private volatile int m_lengthInFrames;
      private volatile int loopCount;
      private volatile int clipBytePosition;
      private volatile int newFramePosition;
      private volatile int loopStartFrame;
      private volatile int loopEndFrame;
      private boolean autoclosing;

      private DirectClip(DataLine.Info var1, AudioFormat var2, int var3, DirectAudioDevice var4) {
         super(var1, var4, var2, var3, var4.getMixerIndex(), var4.getDeviceID(), true);
         this.audioData = null;
         this.autoclosing = false;
      }

      public void open(AudioFormat var1, byte[] var2, int var3, int var4) throws LineUnavailableException {
         Toolkit.isFullySpecifiedAudioFormat(var1);
         byte[] var5 = new byte[var4];
         System.arraycopy(var2, var3, var5, 0, var4);
         this.open(var1, var5, var4 / var1.getFrameSize());
      }

      private void open(AudioFormat var1, byte[] var2, int var3) throws LineUnavailableException {
         Toolkit.isFullySpecifiedAudioFormat(var1);
         synchronized(this.mixer) {
            if (this.isOpen()) {
               throw new IllegalStateException("Clip is already open with format " + this.getFormat() + " and frame lengh of " + this.getFrameLength());
            }

            this.audioData = var2;
            this.frameSize = var1.getFrameSize();
            this.m_lengthInFrames = var3;
            this.bytePosition = 0L;
            this.clipBytePosition = 0;
            this.newFramePosition = -1;
            this.loopStartFrame = 0;
            this.loopEndFrame = var3 - 1;
            this.loopCount = 0;

            try {
               this.open(var1, (int)Toolkit.millis2bytes(var1, 1000L));
            } catch (LineUnavailableException var7) {
               this.audioData = null;
               throw var7;
            } catch (IllegalArgumentException var8) {
               this.audioData = null;
               throw var8;
            }

            byte var5 = 6;
            this.thread = JSSecurityManager.createThread(this, "Direct Clip", true, var5, false);
            this.thread.start();
         }

         if (this.isAutoClosing()) {
            this.getEventDispatcher().autoClosingClipOpened(this);
         }

      }

      public void open(AudioInputStream var1) throws LineUnavailableException, IOException {
         Toolkit.isFullySpecifiedAudioFormat(this.format);
         synchronized(this.mixer) {
            Object var3 = null;
            if (this.isOpen()) {
               throw new IllegalStateException("Clip is already open with format " + this.getFormat() + " and frame lengh of " + this.getFrameLength());
            } else {
               int var4 = (int)var1.getFrameLength();
               int var5 = 0;
               byte[] var12;
               if (var4 != -1) {
                  int var6 = var4 * var1.getFormat().getFrameSize();
                  var12 = new byte[var6];
                  int var7 = var6;
                  int var8 = 0;

                  while(var7 > 0 && var8 >= 0) {
                     var8 = var1.read(var12, var5, var7);
                     if (var8 > 0) {
                        var5 += var8;
                        var7 -= var8;
                     } else if (var8 == 0) {
                        Thread.yield();
                     }
                  }
               } else {
                  short var13 = 16384;
                  DirectAudioDevice.DirectBAOS var14 = new DirectAudioDevice.DirectBAOS();
                  byte[] var15 = new byte[var13];
                  int var9 = 0;

                  while(var9 >= 0) {
                     var9 = var1.read(var15, 0, var15.length);
                     if (var9 > 0) {
                        var14.write(var15, 0, var9);
                        var5 += var9;
                     } else if (var9 == 0) {
                        Thread.yield();
                     }
                  }

                  var12 = var14.getInternalBuffer();
               }

               var4 = var5 / var1.getFormat().getFrameSize();
               this.open(var1.getFormat(), var12, var4);
            }
         }
      }

      public int getFrameLength() {
         return this.m_lengthInFrames;
      }

      public long getMicrosecondLength() {
         return Toolkit.frames2micros(this.getFormat(), (long)this.getFrameLength());
      }

      public void setFramePosition(int var1) {
         if (var1 < 0) {
            var1 = 0;
         } else if (var1 >= this.getFrameLength()) {
            var1 = this.getFrameLength();
         }

         if (this.doIO) {
            this.newFramePosition = var1;
         } else {
            this.clipBytePosition = var1 * this.frameSize;
            this.newFramePosition = -1;
         }

         this.bytePosition = (long)(var1 * this.frameSize);
         this.flush();
         synchronized(this.lockNative) {
            DirectAudioDevice.nSetBytePosition(this.id, this.isSource, (long)(var1 * this.frameSize));
         }
      }

      public long getLongFramePosition() {
         return super.getLongFramePosition();
      }

      public synchronized void setMicrosecondPosition(long var1) {
         long var3 = Toolkit.micros2frames(this.getFormat(), var1);
         this.setFramePosition((int)var3);
      }

      public void setLoopPoints(int var1, int var2) {
         if (var1 >= 0 && var1 < this.getFrameLength()) {
            if (var2 >= this.getFrameLength()) {
               throw new IllegalArgumentException("illegal value for end: " + var2);
            } else {
               if (var2 == -1) {
                  var2 = this.getFrameLength() - 1;
                  if (var2 < 0) {
                     var2 = 0;
                  }
               }

               if (var2 < var1) {
                  throw new IllegalArgumentException("End position " + var2 + "  preceeds start position " + var1);
               } else {
                  this.loopStartFrame = var1;
                  this.loopEndFrame = var2;
               }
            }
         } else {
            throw new IllegalArgumentException("illegal value for start: " + var1);
         }
      }

      public void loop(int var1) {
         this.loopCount = var1;
         this.start();
      }

      void implOpen(AudioFormat var1, int var2) throws LineUnavailableException {
         if (this.audioData == null) {
            throw new IllegalArgumentException("illegal call to open() in interface Clip");
         } else {
            super.implOpen(var1, var2);
         }
      }

      void implClose() {
         Thread var1 = this.thread;
         this.thread = null;
         this.doIO = false;
         if (var1 != null) {
            synchronized(this.lock) {
               this.lock.notifyAll();
            }

            try {
               var1.join(2000L);
            } catch (InterruptedException var4) {
            }
         }

         super.implClose();
         this.audioData = null;
         this.newFramePosition = -1;
         this.getEventDispatcher().autoClosingClipClosed(this);
      }

      void implStart() {
         super.implStart();
      }

      void implStop() {
         super.implStop();
         this.loopCount = 0;
      }

      public void run() {
         Thread var1 = Thread.currentThread();

         label200:
         while(this.thread == var1) {
            synchronized(this.lock) {
               if (!this.doIO) {
                  try {
                     this.lock.wait();
                  } catch (InterruptedException var11) {
                  } finally {
                     if (this.thread != var1) {
                        break;
                     }

                  }
               }
            }

            while(true) {
               while(true) {
                  int var2;
                  long var3;
                  do {
                     int var7;
                     do {
                        do {
                           do {
                              if (!this.doIO) {
                                 continue label200;
                              }

                              if (this.newFramePosition >= 0) {
                                 this.clipBytePosition = this.newFramePosition * this.frameSize;
                                 this.newFramePosition = -1;
                              }

                              var2 = this.getFrameLength() - 1;
                              if (this.loopCount > 0 || this.loopCount == -1) {
                                 var2 = this.loopEndFrame;
                              }

                              var3 = (long)(this.clipBytePosition / this.frameSize);
                              int var5 = (int)((long)var2 - var3 + 1L);
                              int var6 = var5 * this.frameSize;
                              if (var6 > this.getBufferSize()) {
                                 var6 = Toolkit.align(this.getBufferSize(), this.frameSize);
                              }

                              var7 = this.write(this.audioData, this.clipBytePosition, var6);
                              this.clipBytePosition += var7;
                           } while(!this.doIO);
                        } while(this.newFramePosition >= 0);
                     } while(var7 < 0);

                     var3 = (long)(this.clipBytePosition / this.frameSize);
                  } while(var3 <= (long)var2);

                  if (this.loopCount <= 0 && this.loopCount != -1) {
                     this.drain();
                     this.stop();
                  } else {
                     if (this.loopCount != -1) {
                        --this.loopCount;
                     }

                     this.newFramePosition = this.loopStartFrame;
                  }
               }
            }
         }

      }

      public boolean isAutoClosing() {
         return this.autoclosing;
      }

      public void setAutoClosing(boolean var1) {
         if (var1 != this.autoclosing) {
            if (this.isOpen()) {
               if (var1) {
                  this.getEventDispatcher().autoClosingClipOpened(this);
               } else {
                  this.getEventDispatcher().autoClosingClipClosed(this);
               }
            }

            this.autoclosing = var1;
         }

      }

      protected boolean requiresServicing() {
         return false;
      }

      // $FF: synthetic method
      DirectClip(DataLine.Info var1, AudioFormat var2, int var3, DirectAudioDevice var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   private static final class DirectTDL extends DirectAudioDevice.DirectDL implements TargetDataLine {
      private DirectTDL(DataLine.Info var1, AudioFormat var2, int var3, DirectAudioDevice var4) {
         super(var1, var4, var2, var3, var4.getMixerIndex(), var4.getDeviceID(), false);
      }

      public int read(byte[] var1, int var2, int var3) {
         this.flushing = false;
         if (var3 == 0) {
            return 0;
         } else if (var3 < 0) {
            throw new IllegalArgumentException("illegal len: " + var3);
         } else if (var3 % this.getFormat().getFrameSize() != 0) {
            throw new IllegalArgumentException("illegal request to read non-integral number of frames (" + var3 + " bytes, frameSize = " + this.getFormat().getFrameSize() + " bytes)");
         } else if (var2 < 0) {
            throw new ArrayIndexOutOfBoundsException(var2);
         } else if ((long)var2 + (long)var3 > (long)var1.length) {
            throw new ArrayIndexOutOfBoundsException(var1.length);
         } else {
            if (!this.isActive() && this.doIO) {
               this.setActive(true);
               this.setStarted(true);
            }

            int var4 = 0;

            while(this.doIO && !this.flushing) {
               int var5;
               synchronized(this.lockNative) {
                  var5 = DirectAudioDevice.nRead(this.id, var1, var2, var3, this.softwareConversionSize);
                  if (var5 < 0) {
                     break;
                  }

                  this.bytePosition += (long)var5;
                  if (var5 > 0) {
                     this.drained = false;
                  }
               }

               var3 -= var5;
               var4 += var5;
               if (var3 <= 0) {
                  break;
               }

               var2 += var5;
               synchronized(this.lock) {
                  try {
                     this.lock.wait((long)this.waitTime);
                  } catch (InterruptedException var9) {
                  }
               }
            }

            if (this.flushing) {
               var4 = 0;
            }

            return var4;
         }
      }

      // $FF: synthetic method
      DirectTDL(DataLine.Info var1, AudioFormat var2, int var3, DirectAudioDevice var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   private static final class DirectSDL extends DirectAudioDevice.DirectDL implements SourceDataLine {
      private DirectSDL(DataLine.Info var1, AudioFormat var2, int var3, DirectAudioDevice var4) {
         super(var1, var4, var2, var3, var4.getMixerIndex(), var4.getDeviceID(), true);
      }

      // $FF: synthetic method
      DirectSDL(DataLine.Info var1, AudioFormat var2, int var3, DirectAudioDevice var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   private static class DirectDL extends AbstractDataLine implements EventDispatcher.LineMonitor {
      protected final int mixerIndex;
      protected final int deviceID;
      protected long id;
      protected int waitTime;
      protected volatile boolean flushing = false;
      protected final boolean isSource;
      protected volatile long bytePosition;
      protected volatile boolean doIO = false;
      protected volatile boolean stoppedWritten = false;
      protected volatile boolean drained = false;
      protected boolean monitoring = false;
      protected int softwareConversionSize = 0;
      protected AudioFormat hardwareFormat;
      private final DirectAudioDevice.DirectDL.Gain gainControl = new DirectAudioDevice.DirectDL.Gain();
      private final DirectAudioDevice.DirectDL.Mute muteControl = new DirectAudioDevice.DirectDL.Mute();
      private final DirectAudioDevice.DirectDL.Balance balanceControl = new DirectAudioDevice.DirectDL.Balance();
      private final DirectAudioDevice.DirectDL.Pan panControl = new DirectAudioDevice.DirectDL.Pan();
      private float leftGain;
      private float rightGain;
      protected volatile boolean noService = false;
      protected final Object lockNative = new Object();

      protected DirectDL(DataLine.Info var1, DirectAudioDevice var2, AudioFormat var3, int var4, int var5, int var6, boolean var7) {
         super(var1, var2, (Control[])null, var3, var4);
         this.mixerIndex = var5;
         this.deviceID = var6;
         this.waitTime = 10;
         this.isSource = var7;
      }

      void implOpen(AudioFormat var1, int var2) throws LineUnavailableException {
         Toolkit.isFullySpecifiedAudioFormat(var1);
         if (!this.isSource) {
            JSSecurityManager.checkRecordPermission();
         }

         byte var3 = 0;
         if (var1.getEncoding().equals(AudioFormat.Encoding.ULAW)) {
            var3 = 1;
         } else if (var1.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
            var3 = 2;
         }

         if (var2 <= -1) {
            var2 = (int)Toolkit.millis2bytes(var1, 500L);
         }

         DirectAudioDevice.DirectDLI var4 = null;
         if (this.info instanceof DirectAudioDevice.DirectDLI) {
            var4 = (DirectAudioDevice.DirectDLI)this.info;
         }

         if (this.isSource) {
            if (!var1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !var1.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
               this.controls = new Control[0];
            } else if (var1.getChannels() <= 2 && var1.getSampleSizeInBits() <= 16) {
               if (var1.getChannels() == 1) {
                  this.controls = new Control[2];
               } else {
                  this.controls = new Control[4];
                  this.controls[2] = this.balanceControl;
                  this.controls[3] = this.panControl;
               }

               this.controls[0] = this.gainControl;
               this.controls[1] = this.muteControl;
            } else {
               this.controls = new Control[0];
            }
         }

         this.hardwareFormat = var1;
         this.softwareConversionSize = 0;
         if (var4 != null && !var4.isFormatSupportedInHardware(var1)) {
            AudioFormat var5 = DirectAudioDevice.getSignOrEndianChangedFormat(var1);
            if (var4.isFormatSupportedInHardware(var5)) {
               this.hardwareFormat = var5;
               this.softwareConversionSize = var1.getFrameSize() / var1.getChannels();
            }
         }

         var2 = var2 / var1.getFrameSize() * var1.getFrameSize();
         this.id = DirectAudioDevice.nOpen(this.mixerIndex, this.deviceID, this.isSource, var3, this.hardwareFormat.getSampleRate(), this.hardwareFormat.getSampleSizeInBits(), this.hardwareFormat.getFrameSize(), this.hardwareFormat.getChannels(), this.hardwareFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED), this.hardwareFormat.isBigEndian(), var2);
         if (this.id == 0L) {
            throw new LineUnavailableException("line with format " + var1 + " not supported.");
         } else {
            this.bufferSize = DirectAudioDevice.nGetBufferSize(this.id, this.isSource);
            if (this.bufferSize < 1) {
               this.bufferSize = var2;
            }

            this.format = var1;
            this.waitTime = (int)Toolkit.bytes2millis(var1, (long)this.bufferSize) / 4;
            if (this.waitTime < 10) {
               this.waitTime = 1;
            } else if (this.waitTime > 1000) {
               this.waitTime = 1000;
            }

            this.bytePosition = 0L;
            this.stoppedWritten = false;
            this.doIO = false;
            this.calcVolume();
         }
      }

      void implStart() {
         if (!this.isSource) {
            JSSecurityManager.checkRecordPermission();
         }

         synchronized(this.lockNative) {
            DirectAudioDevice.nStart(this.id, this.isSource);
         }

         this.monitoring = this.requiresServicing();
         if (this.monitoring) {
            this.getEventDispatcher().addLineMonitor(this);
         }

         this.doIO = true;
         if (this.isSource && this.stoppedWritten) {
            this.setStarted(true);
            this.setActive(true);
         }

      }

      void implStop() {
         if (!this.isSource) {
            JSSecurityManager.checkRecordPermission();
         }

         if (this.monitoring) {
            this.getEventDispatcher().removeLineMonitor(this);
            this.monitoring = false;
         }

         synchronized(this.lockNative) {
            DirectAudioDevice.nStop(this.id, this.isSource);
         }

         synchronized(this.lock) {
            this.doIO = false;
            this.lock.notifyAll();
         }

         this.setActive(false);
         this.setStarted(false);
         this.stoppedWritten = false;
      }

      void implClose() {
         if (!this.isSource) {
            JSSecurityManager.checkRecordPermission();
         }

         if (this.monitoring) {
            this.getEventDispatcher().removeLineMonitor(this);
            this.monitoring = false;
         }

         this.doIO = false;
         long var1 = this.id;
         this.id = 0L;
         synchronized(this.lockNative) {
            DirectAudioDevice.nClose(var1, this.isSource);
         }

         this.bytePosition = 0L;
         this.softwareConversionSize = 0;
      }

      public int available() {
         if (this.id == 0L) {
            return 0;
         } else {
            synchronized(this.lockNative) {
               int var1 = DirectAudioDevice.nAvailable(this.id, this.isSource);
               return var1;
            }
         }
      }

      public void drain() {
         this.noService = true;
         int var1 = 0;
         long var2 = this.getLongFramePosition();
         boolean var4 = false;

         while(!this.drained) {
            synchronized(this.lockNative) {
               if (this.id == 0L || !this.doIO || !DirectAudioDevice.nIsStillDraining(this.id, this.isSource)) {
                  break;
               }
            }

            if (var1 % 5 == 4) {
               long var5 = this.getLongFramePosition();
               var4 |= var5 != var2;
               if (var1 % 50 > 45) {
                  if (!var4) {
                     break;
                  }

                  var4 = false;
                  var2 = var5;
               }
            }

            ++var1;
            synchronized(this.lock) {
               try {
                  this.lock.wait(10L);
               } catch (InterruptedException var8) {
               }
            }
         }

         if (this.doIO && this.id != 0L) {
            this.drained = true;
         }

         this.noService = false;
      }

      public void flush() {
         if (this.id != 0L) {
            this.flushing = true;
            synchronized(this.lock) {
               this.lock.notifyAll();
            }

            synchronized(this.lockNative) {
               if (this.id != 0L) {
                  DirectAudioDevice.nFlush(this.id, this.isSource);
               }
            }

            this.drained = true;
         }

      }

      public long getLongFramePosition() {
         long var1;
         synchronized(this.lockNative) {
            var1 = DirectAudioDevice.nGetBytePosition(this.id, this.isSource, this.bytePosition);
         }

         if (var1 < 0L) {
            var1 = 0L;
         }

         return var1 / (long)this.getFormat().getFrameSize();
      }

      public int write(byte[] var1, int var2, int var3) {
         this.flushing = false;
         if (var3 == 0) {
            return 0;
         } else if (var3 < 0) {
            throw new IllegalArgumentException("illegal len: " + var3);
         } else if (var3 % this.getFormat().getFrameSize() != 0) {
            throw new IllegalArgumentException("illegal request to write non-integral number of frames (" + var3 + " bytes, frameSize = " + this.getFormat().getFrameSize() + " bytes)");
         } else if (var2 < 0) {
            throw new ArrayIndexOutOfBoundsException(var2);
         } else if ((long)var2 + (long)var3 > (long)var1.length) {
            throw new ArrayIndexOutOfBoundsException(var1.length);
         } else {
            if (!this.isActive() && this.doIO) {
               this.setActive(true);
               this.setStarted(true);
            }

            int var4 = 0;

            while(!this.flushing) {
               int var5;
               synchronized(this.lockNative) {
                  var5 = DirectAudioDevice.nWrite(this.id, var1, var2, var3, this.softwareConversionSize, this.leftGain, this.rightGain);
                  if (var5 < 0) {
                     break;
                  }

                  this.bytePosition += (long)var5;
                  if (var5 > 0) {
                     this.drained = false;
                  }
               }

               var3 -= var5;
               var4 += var5;
               if (!this.doIO || var3 <= 0) {
                  break;
               }

               var2 += var5;
               synchronized(this.lock) {
                  try {
                     this.lock.wait((long)this.waitTime);
                  } catch (InterruptedException var9) {
                  }
               }
            }

            if (var4 > 0 && !this.doIO) {
               this.stoppedWritten = true;
            }

            return var4;
         }
      }

      protected boolean requiresServicing() {
         return DirectAudioDevice.nRequiresServicing(this.id, this.isSource);
      }

      public void checkLine() {
         synchronized(this.lockNative) {
            if (this.monitoring && this.doIO && this.id != 0L && !this.flushing && !this.noService) {
               DirectAudioDevice.nService(this.id, this.isSource);
            }

         }
      }

      private void calcVolume() {
         if (this.getFormat() != null) {
            if (this.muteControl.getValue()) {
               this.leftGain = 0.0F;
               this.rightGain = 0.0F;
            } else {
               float var1 = this.gainControl.getLinearGain();
               if (this.getFormat().getChannels() == 1) {
                  this.leftGain = var1;
                  this.rightGain = var1;
               } else {
                  float var2 = this.balanceControl.getValue();
                  if (var2 < 0.0F) {
                     this.leftGain = var1;
                     this.rightGain = var1 * (var2 + 1.0F);
                  } else {
                     this.leftGain = var1 * (1.0F - var2);
                     this.rightGain = var1;
                  }
               }

            }
         }
      }

      private final class Pan extends FloatControl {
         private Pan() {
            super(FloatControl.Type.PAN, -1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right");
         }

         public void setValue(float var1) {
            this.setValueImpl(var1);
            DirectDL.this.balanceControl.setValueImpl(var1);
            DirectDL.this.calcVolume();
         }

         void setValueImpl(float var1) {
            super.setValue(var1);
         }

         // $FF: synthetic method
         Pan(Object var2) {
            this();
         }
      }

      private final class Balance extends FloatControl {
         private Balance() {
            super(FloatControl.Type.BALANCE, -1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right");
         }

         public void setValue(float var1) {
            this.setValueImpl(var1);
            DirectDL.this.panControl.setValueImpl(var1);
            DirectDL.this.calcVolume();
         }

         void setValueImpl(float var1) {
            super.setValue(var1);
         }

         // $FF: synthetic method
         Balance(Object var2) {
            this();
         }
      }

      private final class Mute extends BooleanControl {
         private Mute() {
            super(BooleanControl.Type.MUTE, false, "True", "False");
         }

         public void setValue(boolean var1) {
            super.setValue(var1);
            DirectDL.this.calcVolume();
         }

         // $FF: synthetic method
         Mute(Object var2) {
            this();
         }
      }

      protected final class Gain extends FloatControl {
         private float linearGain;

         private Gain() {
            super(FloatControl.Type.MASTER_GAIN, Toolkit.linearToDB(0.0F), Toolkit.linearToDB(2.0F), Math.abs(Toolkit.linearToDB(1.0F) - Toolkit.linearToDB(0.0F)) / 128.0F, -1, 0.0F, "dB", "Minimum", "", "Maximum");
            this.linearGain = 1.0F;
         }

         public void setValue(float var1) {
            float var2 = Toolkit.dBToLinear(var1);
            super.setValue(Toolkit.linearToDB(var2));
            this.linearGain = var2;
            DirectDL.this.calcVolume();
         }

         float getLinearGain() {
            return this.linearGain;
         }

         // $FF: synthetic method
         Gain(Object var2) {
            this();
         }
      }
   }

   private static final class DirectDLI extends DataLine.Info {
      final AudioFormat[] hardwareFormats;

      private DirectDLI(Class var1, AudioFormat[] var2, AudioFormat[] var3, int var4, int var5) {
         super(var1, var2, var4, var5);
         this.hardwareFormats = var3;
      }

      public boolean isFormatSupportedInHardware(AudioFormat var1) {
         if (var1 == null) {
            return false;
         } else {
            for(int var2 = 0; var2 < this.hardwareFormats.length; ++var2) {
               if (var1.matches(this.hardwareFormats[var2])) {
                  return true;
               }
            }

            return false;
         }
      }

      private AudioFormat[] getHardwareFormats() {
         return this.hardwareFormats;
      }

      // $FF: synthetic method
      DirectDLI(Class var1, AudioFormat[] var2, AudioFormat[] var3, int var4, int var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }
}
