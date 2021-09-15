package com.sun.media.sound;

import java.util.Vector;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

abstract class AbstractMixer extends AbstractLine implements Mixer {
   protected static final int PCM = 0;
   protected static final int ULAW = 1;
   protected static final int ALAW = 2;
   private final Mixer.Info mixerInfo;
   protected Line.Info[] sourceLineInfo;
   protected Line.Info[] targetLineInfo;
   private boolean started = false;
   private boolean manuallyOpened = false;
   private final Vector sourceLines = new Vector();
   private final Vector targetLines = new Vector();

   protected AbstractMixer(Mixer.Info var1, Control[] var2, Line.Info[] var3, Line.Info[] var4) {
      super(new Line.Info(Mixer.class), (AbstractMixer)null, var2);
      this.mixer = this;
      if (var2 == null) {
         var2 = new Control[0];
      }

      this.mixerInfo = var1;
      this.sourceLineInfo = var3;
      this.targetLineInfo = var4;
   }

   public final Mixer.Info getMixerInfo() {
      return this.mixerInfo;
   }

   public final Line.Info[] getSourceLineInfo() {
      Line.Info[] var1 = new Line.Info[this.sourceLineInfo.length];
      System.arraycopy(this.sourceLineInfo, 0, var1, 0, this.sourceLineInfo.length);
      return var1;
   }

   public final Line.Info[] getTargetLineInfo() {
      Line.Info[] var1 = new Line.Info[this.targetLineInfo.length];
      System.arraycopy(this.targetLineInfo, 0, var1, 0, this.targetLineInfo.length);
      return var1;
   }

   public final Line.Info[] getSourceLineInfo(Line.Info var1) {
      Vector var3 = new Vector();

      int var2;
      for(var2 = 0; var2 < this.sourceLineInfo.length; ++var2) {
         if (var1.matches(this.sourceLineInfo[var2])) {
            var3.addElement(this.sourceLineInfo[var2]);
         }
      }

      Line.Info[] var4 = new Line.Info[var3.size()];

      for(var2 = 0; var2 < var4.length; ++var2) {
         var4[var2] = (Line.Info)var3.elementAt(var2);
      }

      return var4;
   }

   public final Line.Info[] getTargetLineInfo(Line.Info var1) {
      Vector var3 = new Vector();

      int var2;
      for(var2 = 0; var2 < this.targetLineInfo.length; ++var2) {
         if (var1.matches(this.targetLineInfo[var2])) {
            var3.addElement(this.targetLineInfo[var2]);
         }
      }

      Line.Info[] var4 = new Line.Info[var3.size()];

      for(var2 = 0; var2 < var4.length; ++var2) {
         var4[var2] = (Line.Info)var3.elementAt(var2);
      }

      return var4;
   }

   public final boolean isLineSupported(Line.Info var1) {
      int var2;
      for(var2 = 0; var2 < this.sourceLineInfo.length; ++var2) {
         if (var1.matches(this.sourceLineInfo[var2])) {
            return true;
         }
      }

      for(var2 = 0; var2 < this.targetLineInfo.length; ++var2) {
         if (var1.matches(this.targetLineInfo[var2])) {
            return true;
         }
      }

      return false;
   }

   public abstract Line getLine(Line.Info var1) throws LineUnavailableException;

   public abstract int getMaxLines(Line.Info var1);

   protected abstract void implOpen() throws LineUnavailableException;

   protected abstract void implStart();

   protected abstract void implStop();

   protected abstract void implClose();

   public final Line[] getSourceLines() {
      synchronized(this.sourceLines) {
         Line[] var1 = new Line[this.sourceLines.size()];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var1[var3] = (Line)this.sourceLines.elementAt(var3);
         }

         return var1;
      }
   }

   public final Line[] getTargetLines() {
      synchronized(this.targetLines) {
         Line[] var1 = new Line[this.targetLines.size()];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var1[var3] = (Line)this.targetLines.elementAt(var3);
         }

         return var1;
      }
   }

   public final void synchronize(Line[] var1, boolean var2) {
      throw new IllegalArgumentException("Synchronization not supported by this mixer.");
   }

   public final void unsynchronize(Line[] var1) {
      throw new IllegalArgumentException("Synchronization not supported by this mixer.");
   }

   public final boolean isSynchronizationSupported(Line[] var1, boolean var2) {
      return false;
   }

   public final synchronized void open() throws LineUnavailableException {
      this.open(true);
   }

   final synchronized void open(boolean var1) throws LineUnavailableException {
      if (!this.isOpen()) {
         this.implOpen();
         this.setOpen(true);
         if (var1) {
            this.manuallyOpened = true;
         }
      }

   }

   final synchronized void open(Line var1) throws LineUnavailableException {
      if (!this.equals(var1)) {
         if (this.isSourceLine(var1.getLineInfo())) {
            if (!this.sourceLines.contains(var1)) {
               this.open(false);
               this.sourceLines.addElement(var1);
            }
         } else if (this.isTargetLine(var1.getLineInfo()) && !this.targetLines.contains(var1)) {
            this.open(false);
            this.targetLines.addElement(var1);
         }

      }
   }

   final synchronized void close(Line var1) {
      if (!this.equals(var1)) {
         this.sourceLines.removeElement(var1);
         this.targetLines.removeElement(var1);
         if (this.sourceLines.isEmpty() && this.targetLines.isEmpty() && !this.manuallyOpened) {
            this.close();
         }

      }
   }

   public final synchronized void close() {
      if (this.isOpen()) {
         Line[] var1 = this.getSourceLines();

         int var2;
         for(var2 = 0; var2 < var1.length; ++var2) {
            var1[var2].close();
         }

         var1 = this.getTargetLines();

         for(var2 = 0; var2 < var1.length; ++var2) {
            var1[var2].close();
         }

         this.implClose();
         this.setOpen(false);
      }

      this.manuallyOpened = false;
   }

   final synchronized void start(Line var1) {
      if (!this.equals(var1)) {
         if (!this.started) {
            this.implStart();
            this.started = true;
         }

      }
   }

   final synchronized void stop(Line var1) {
      if (!this.equals(var1)) {
         Vector var2 = (Vector)this.sourceLines.clone();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            if (var2.elementAt(var3) instanceof AbstractDataLine) {
               AbstractDataLine var4 = (AbstractDataLine)var2.elementAt(var3);
               if (var4.isStartedRunning() && !var4.equals(var1)) {
                  return;
               }
            }
         }

         Vector var6 = (Vector)this.targetLines.clone();

         for(int var7 = 0; var7 < var6.size(); ++var7) {
            if (var6.elementAt(var7) instanceof AbstractDataLine) {
               AbstractDataLine var5 = (AbstractDataLine)var6.elementAt(var7);
               if (var5.isStartedRunning() && !var5.equals(var1)) {
                  return;
               }
            }
         }

         this.started = false;
         this.implStop();
      }
   }

   final boolean isSourceLine(Line.Info var1) {
      for(int var2 = 0; var2 < this.sourceLineInfo.length; ++var2) {
         if (var1.matches(this.sourceLineInfo[var2])) {
            return true;
         }
      }

      return false;
   }

   final boolean isTargetLine(Line.Info var1) {
      for(int var2 = 0; var2 < this.targetLineInfo.length; ++var2) {
         if (var1.matches(this.targetLineInfo[var2])) {
            return true;
         }
      }

      return false;
   }

   final Line.Info getLineInfo(Line.Info var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2;
         for(var2 = 0; var2 < this.sourceLineInfo.length; ++var2) {
            if (var1.matches(this.sourceLineInfo[var2])) {
               return this.sourceLineInfo[var2];
            }
         }

         for(var2 = 0; var2 < this.targetLineInfo.length; ++var2) {
            if (var1.matches(this.targetLineInfo[var2])) {
               return this.targetLineInfo[var2];
            }
         }

         return null;
      }
   }
}
