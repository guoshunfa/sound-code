package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;

abstract class AbstractDataLine extends AbstractLine implements DataLine {
   private final AudioFormat defaultFormat;
   private final int defaultBufferSize;
   protected final Object lock;
   protected AudioFormat format;
   protected int bufferSize;
   protected boolean running;
   private boolean started;
   private boolean active;

   protected AbstractDataLine(DataLine.Info var1, AbstractMixer var2, Control[] var3) {
      this(var1, var2, var3, (AudioFormat)null, -1);
   }

   protected AbstractDataLine(DataLine.Info var1, AbstractMixer var2, Control[] var3, AudioFormat var4, int var5) {
      super(var1, var2, var3);
      this.lock = new Object();
      this.running = false;
      this.started = false;
      this.active = false;
      if (var4 != null) {
         this.defaultFormat = var4;
      } else {
         this.defaultFormat = new AudioFormat(44100.0F, 16, 2, true, Platform.isBigEndian());
      }

      if (var5 > 0) {
         this.defaultBufferSize = var5;
      } else {
         this.defaultBufferSize = (int)(this.defaultFormat.getFrameRate() / 2.0F) * this.defaultFormat.getFrameSize();
      }

      this.format = this.defaultFormat;
      this.bufferSize = this.defaultBufferSize;
   }

   public final void open(AudioFormat var1, int var2) throws LineUnavailableException {
      synchronized(this.mixer) {
         if (!this.isOpen()) {
            Toolkit.isFullySpecifiedAudioFormat(var1);
            this.mixer.open(this);

            try {
               this.implOpen(var1, var2);
               this.setOpen(true);
            } catch (LineUnavailableException var6) {
               this.mixer.close(this);
               throw var6;
            }
         } else {
            if (!var1.matches(this.getFormat())) {
               throw new IllegalStateException("Line is already open with format " + this.getFormat() + " and bufferSize " + this.getBufferSize());
            }

            if (var2 > 0) {
               this.setBufferSize(var2);
            }
         }

      }
   }

   public final void open(AudioFormat var1) throws LineUnavailableException {
      this.open(var1, -1);
   }

   public int available() {
      return 0;
   }

   public void drain() {
   }

   public void flush() {
   }

   public final void start() {
      synchronized(this.mixer) {
         if (this.isOpen() && !this.isStartedRunning()) {
            this.mixer.start(this);
            this.implStart();
            this.running = true;
         }
      }

      synchronized(this.lock) {
         this.lock.notifyAll();
      }
   }

   public final void stop() {
      synchronized(this.mixer) {
         if (this.isOpen() && this.isStartedRunning()) {
            this.implStop();
            this.mixer.stop(this);
            this.running = false;
            if (this.started && !this.isActive()) {
               this.setStarted(false);
            }
         }
      }

      synchronized(this.lock) {
         this.lock.notifyAll();
      }
   }

   public final boolean isRunning() {
      return this.started;
   }

   public final boolean isActive() {
      return this.active;
   }

   public final long getMicrosecondPosition() {
      long var1 = this.getLongFramePosition();
      if (var1 != -1L) {
         var1 = Toolkit.frames2micros(this.getFormat(), var1);
      }

      return var1;
   }

   public final AudioFormat getFormat() {
      return this.format;
   }

   public final int getBufferSize() {
      return this.bufferSize;
   }

   public final int setBufferSize(int var1) {
      return this.getBufferSize();
   }

   public final float getLevel() {
      return -1.0F;
   }

   final boolean isStartedRunning() {
      return this.running;
   }

   final void setActive(boolean var1) {
      synchronized(this) {
         if (this.active != var1) {
            this.active = var1;
         }

      }
   }

   final void setStarted(boolean var1) {
      boolean var2 = false;
      long var3 = this.getLongFramePosition();
      synchronized(this) {
         if (this.started != var1) {
            this.started = var1;
            var2 = true;
         }
      }

      if (var2) {
         if (var1) {
            this.sendEvents(new LineEvent(this, LineEvent.Type.START, var3));
         } else {
            this.sendEvents(new LineEvent(this, LineEvent.Type.STOP, var3));
         }
      }

   }

   final void setEOM() {
      this.setStarted(false);
   }

   public final void open() throws LineUnavailableException {
      this.open(this.format, this.bufferSize);
   }

   public final void close() {
      synchronized(this.mixer) {
         if (this.isOpen()) {
            this.stop();
            this.setOpen(false);
            this.implClose();
            this.mixer.close(this);
            this.format = this.defaultFormat;
            this.bufferSize = this.defaultBufferSize;
         }

      }
   }

   abstract void implOpen(AudioFormat var1, int var2) throws LineUnavailableException;

   abstract void implClose();

   abstract void implStart();

   abstract void implStop();
}
