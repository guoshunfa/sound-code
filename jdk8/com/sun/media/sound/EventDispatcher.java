package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

final class EventDispatcher implements Runnable {
   private static final int AUTO_CLOSE_TIME = 5000;
   private final ArrayList eventQueue = new ArrayList();
   private Thread thread = null;
   private final ArrayList<EventDispatcher.ClipInfo> autoClosingClips = new ArrayList();
   private final ArrayList<EventDispatcher.LineMonitor> lineMonitors = new ArrayList();
   static final int LINE_MONITOR_TIME = 400;

   synchronized void start() {
      if (this.thread == null) {
         this.thread = JSSecurityManager.createThread(this, "Java Sound Event Dispatcher", true, -1, true);
      }

   }

   void processEvent(EventDispatcher.EventInfo var1) {
      int var2 = var1.getListenerCount();
      int var4;
      if (var1.getEvent() instanceof LineEvent) {
         LineEvent var11 = (LineEvent)var1.getEvent();

         for(var4 = 0; var4 < var2; ++var4) {
            try {
               ((LineListener)var1.getListener(var4)).update(var11);
            } catch (Throwable var7) {
            }
         }

      } else if (var1.getEvent() instanceof MetaMessage) {
         MetaMessage var10 = (MetaMessage)var1.getEvent();

         for(var4 = 0; var4 < var2; ++var4) {
            try {
               ((MetaEventListener)var1.getListener(var4)).meta(var10);
            } catch (Throwable var8) {
            }
         }

      } else if (!(var1.getEvent() instanceof ShortMessage)) {
         Printer.err("Unknown event type: " + var1.getEvent());
      } else {
         ShortMessage var3 = (ShortMessage)var1.getEvent();
         var4 = var3.getStatus();
         if ((var4 & 240) == 176) {
            for(int var5 = 0; var5 < var2; ++var5) {
               try {
                  ((ControllerEventListener)var1.getListener(var5)).controlChange(var3);
               } catch (Throwable var9) {
               }
            }
         }

      }
   }

   void dispatchEvents() {
      EventDispatcher.EventInfo var1 = null;
      synchronized(this) {
         try {
            if (this.eventQueue.size() == 0) {
               if (this.autoClosingClips.size() <= 0 && this.lineMonitors.size() <= 0) {
                  this.wait();
               } else {
                  short var3 = 5000;
                  if (this.lineMonitors.size() > 0) {
                     var3 = 400;
                  }

                  this.wait((long)var3);
               }
            }
         } catch (InterruptedException var5) {
         }

         if (this.eventQueue.size() > 0) {
            var1 = (EventDispatcher.EventInfo)this.eventQueue.remove(0);
         }
      }

      if (var1 != null) {
         this.processEvent(var1);
      } else {
         if (this.autoClosingClips.size() > 0) {
            this.closeAutoClosingClips();
         }

         if (this.lineMonitors.size() > 0) {
            this.monitorLines();
         }
      }

   }

   private synchronized void postEvent(EventDispatcher.EventInfo var1) {
      this.eventQueue.add(var1);
      this.notifyAll();
   }

   public void run() {
      while(true) {
         try {
            this.dispatchEvents();
         } catch (Throwable var2) {
         }
      }
   }

   void sendAudioEvents(Object var1, List var2) {
      if (var2 != null && var2.size() != 0) {
         this.start();
         EventDispatcher.EventInfo var3 = new EventDispatcher.EventInfo(var1, var2);
         this.postEvent(var3);
      }
   }

   private void closeAutoClosingClips() {
      synchronized(this.autoClosingClips) {
         long var2 = System.currentTimeMillis();

         for(int var4 = this.autoClosingClips.size() - 1; var4 >= 0; --var4) {
            EventDispatcher.ClipInfo var5 = (EventDispatcher.ClipInfo)this.autoClosingClips.get(var4);
            if (var5.isExpired(var2)) {
               AutoClosingClip var6 = var5.getClip();
               if (var6.isOpen() && var6.isAutoClosing()) {
                  if (!var6.isRunning() && !var6.isActive() && var6.isAutoClosing()) {
                     var6.close();
                  }
               } else {
                  this.autoClosingClips.remove(var4);
               }
            }
         }

      }
   }

   private int getAutoClosingClipIndex(AutoClosingClip var1) {
      synchronized(this.autoClosingClips) {
         for(int var3 = this.autoClosingClips.size() - 1; var3 >= 0; --var3) {
            if (var1.equals(((EventDispatcher.ClipInfo)this.autoClosingClips.get(var3)).getClip())) {
               return var3;
            }
         }

         return -1;
      }
   }

   void autoClosingClipOpened(AutoClosingClip var1) {
      boolean var2 = false;
      int var8;
      synchronized(this.autoClosingClips) {
         var8 = this.getAutoClosingClipIndex(var1);
         if (var8 == -1) {
            this.autoClosingClips.add(new EventDispatcher.ClipInfo(var1));
         }
      }

      if (var8 == -1) {
         synchronized(this) {
            this.notifyAll();
         }
      }

   }

   void autoClosingClipClosed(AutoClosingClip var1) {
   }

   private void monitorLines() {
      synchronized(this.lineMonitors) {
         for(int var2 = 0; var2 < this.lineMonitors.size(); ++var2) {
            ((EventDispatcher.LineMonitor)this.lineMonitors.get(var2)).checkLine();
         }

      }
   }

   void addLineMonitor(EventDispatcher.LineMonitor var1) {
      synchronized(this.lineMonitors) {
         if (this.lineMonitors.indexOf(var1) >= 0) {
            return;
         }

         this.lineMonitors.add(var1);
      }

      synchronized(this) {
         this.notifyAll();
      }
   }

   void removeLineMonitor(EventDispatcher.LineMonitor var1) {
      synchronized(this.lineMonitors) {
         if (this.lineMonitors.indexOf(var1) >= 0) {
            this.lineMonitors.remove(var1);
         }
      }
   }

   interface LineMonitor {
      void checkLine();
   }

   private class ClipInfo {
      private final AutoClosingClip clip;
      private final long expiration;

      ClipInfo(AutoClosingClip var2) {
         this.clip = var2;
         this.expiration = System.currentTimeMillis() + 5000L;
      }

      AutoClosingClip getClip() {
         return this.clip;
      }

      boolean isExpired(long var1) {
         return var1 > this.expiration;
      }
   }

   private class EventInfo {
      private final Object event;
      private final Object[] listeners;

      EventInfo(Object var2, List var3) {
         this.event = var2;
         this.listeners = var3.toArray();
      }

      Object getEvent() {
         return this.event;
      }

      int getListenerCount() {
         return this.listeners.length;
      }

      Object getListener(int var1) {
         return this.listeners[var1];
      }
   }
}
