package com.sun.media.sound;

import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

abstract class AbstractLine implements Line {
   protected final Line.Info info;
   protected Control[] controls;
   AbstractMixer mixer;
   private boolean open = false;
   private final Vector listeners = new Vector();
   private static final Map<ThreadGroup, EventDispatcher> dispatchers = new WeakHashMap();

   protected AbstractLine(Line.Info var1, AbstractMixer var2, Control[] var3) {
      if (var3 == null) {
         var3 = new Control[0];
      }

      this.info = var1;
      this.mixer = var2;
      this.controls = var3;
   }

   public final Line.Info getLineInfo() {
      return this.info;
   }

   public final boolean isOpen() {
      return this.open;
   }

   public final void addLineListener(LineListener var1) {
      synchronized(this.listeners) {
         if (!this.listeners.contains(var1)) {
            this.listeners.addElement(var1);
         }

      }
   }

   public final void removeLineListener(LineListener var1) {
      this.listeners.removeElement(var1);
   }

   public final Control[] getControls() {
      Control[] var1 = new Control[this.controls.length];

      for(int var2 = 0; var2 < this.controls.length; ++var2) {
         var1[var2] = this.controls[var2];
      }

      return var1;
   }

   public final boolean isControlSupported(Control.Type var1) {
      if (var1 == null) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.controls.length; ++var2) {
            if (var1 == this.controls[var2].getType()) {
               return true;
            }
         }

         return false;
      }
   }

   public final Control getControl(Control.Type var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < this.controls.length; ++var2) {
            if (var1 == this.controls[var2].getType()) {
               return this.controls[var2];
            }
         }
      }

      throw new IllegalArgumentException("Unsupported control type: " + var1);
   }

   final void setOpen(boolean var1) {
      boolean var2 = false;
      long var3 = this.getLongFramePosition();
      synchronized(this) {
         if (this.open != var1) {
            this.open = var1;
            var2 = true;
         }
      }

      if (var2) {
         if (var1) {
            this.sendEvents(new LineEvent(this, LineEvent.Type.OPEN, var3));
         } else {
            this.sendEvents(new LineEvent(this, LineEvent.Type.CLOSE, var3));
         }
      }

   }

   final void sendEvents(LineEvent var1) {
      this.getEventDispatcher().sendAudioEvents(var1, this.listeners);
   }

   public final int getFramePosition() {
      return (int)this.getLongFramePosition();
   }

   public long getLongFramePosition() {
      return -1L;
   }

   final AbstractMixer getMixer() {
      return this.mixer;
   }

   final EventDispatcher getEventDispatcher() {
      ThreadGroup var1 = Thread.currentThread().getThreadGroup();
      synchronized(dispatchers) {
         EventDispatcher var3 = (EventDispatcher)dispatchers.get(var1);
         if (var3 == null) {
            var3 = new EventDispatcher();
            dispatchers.put(var1, var3);
            var3.start();
         }

         return var3;
      }
   }

   public abstract void open() throws LineUnavailableException;

   public abstract void close();
}
