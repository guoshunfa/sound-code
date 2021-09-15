package com.sun.media.sound;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

final class MidiInDevice extends AbstractMidiDevice implements Runnable {
   private Thread midiInThread = null;

   MidiInDevice(AbstractMidiDeviceProvider.Info var1) {
      super(var1);
   }

   protected synchronized void implOpen() throws MidiUnavailableException {
      int var1 = ((MidiInDeviceProvider.MidiInDeviceInfo)this.getDeviceInfo()).getIndex();
      this.id = this.nOpen(var1);
      if (this.id == 0L) {
         throw new MidiUnavailableException("Unable to open native device");
      } else {
         if (this.midiInThread == null) {
            this.midiInThread = JSSecurityManager.createThread(this, "Java Sound MidiInDevice Thread", false, -1, true);
         }

         this.nStart(this.id);
      }
   }

   protected synchronized void implClose() {
      long var1 = this.id;
      this.id = 0L;
      super.implClose();
      this.nStop(var1);
      if (this.midiInThread != null) {
         try {
            this.midiInThread.join(1000L);
         } catch (InterruptedException var4) {
         }
      }

      this.nClose(var1);
   }

   public long getMicrosecondPosition() {
      long var1 = -1L;
      if (this.isOpen()) {
         var1 = this.nGetTimeStamp(this.id);
      }

      return var1;
   }

   protected boolean hasTransmitters() {
      return true;
   }

   protected Transmitter createTransmitter() {
      return new MidiInDevice.MidiInTransmitter();
   }

   public void run() {
      while(this.id != 0L) {
         this.nGetMessages(this.id);
         if (this.id != 0L) {
            try {
               Thread.sleep(1L);
            } catch (InterruptedException var2) {
            }
         }
      }

      this.midiInThread = null;
   }

   void callbackShortMessage(int var1, long var2) {
      if (var1 != 0 && this.id != 0L) {
         this.getTransmitterList().sendMessage(var1, var2);
      }
   }

   void callbackLongMessage(byte[] var1, long var2) {
      if (this.id != 0L && var1 != null) {
         this.getTransmitterList().sendMessage(var1, var2);
      }
   }

   private native long nOpen(int var1) throws MidiUnavailableException;

   private native void nClose(long var1);

   private native void nStart(long var1) throws MidiUnavailableException;

   private native void nStop(long var1);

   private native long nGetTimeStamp(long var1);

   private native void nGetMessages(long var1);

   private final class MidiInTransmitter extends AbstractMidiDevice.BasicTransmitter {
      private MidiInTransmitter() {
         super();
      }

      // $FF: synthetic method
      MidiInTransmitter(Object var2) {
         this();
      }
   }
}
