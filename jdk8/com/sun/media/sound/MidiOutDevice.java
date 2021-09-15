package com.sun.media.sound;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

final class MidiOutDevice extends AbstractMidiDevice {
   MidiOutDevice(AbstractMidiDeviceProvider.Info var1) {
      super(var1);
   }

   protected synchronized void implOpen() throws MidiUnavailableException {
      int var1 = ((AbstractMidiDeviceProvider.Info)this.getDeviceInfo()).getIndex();
      this.id = this.nOpen(var1);
      if (this.id == 0L) {
         throw new MidiUnavailableException("Unable to open native device");
      }
   }

   protected synchronized void implClose() {
      long var1 = this.id;
      this.id = 0L;
      super.implClose();
      this.nClose(var1);
   }

   public long getMicrosecondPosition() {
      long var1 = -1L;
      if (this.isOpen()) {
         var1 = this.nGetTimeStamp(this.id);
      }

      return var1;
   }

   protected boolean hasReceivers() {
      return true;
   }

   protected Receiver createReceiver() {
      return new MidiOutDevice.MidiOutReceiver();
   }

   private native long nOpen(int var1) throws MidiUnavailableException;

   private native void nClose(long var1);

   private native void nSendShortMessage(long var1, int var3, long var4);

   private native void nSendLongMessage(long var1, byte[] var3, int var4, long var5);

   private native long nGetTimeStamp(long var1);

   final class MidiOutReceiver extends AbstractMidiDevice.AbstractReceiver {
      MidiOutReceiver() {
         super();
      }

      void implSend(MidiMessage var1, long var2) {
         int var4 = var1.getLength();
         int var5 = var1.getStatus();
         if (var4 <= 3 && var5 != 240 && var5 != 247) {
            int var8;
            if (var1 instanceof ShortMessage) {
               if (var1 instanceof FastShortMessage) {
                  var8 = ((FastShortMessage)var1).getPackedMsg();
               } else {
                  ShortMessage var9 = (ShortMessage)var1;
                  var8 = var5 & 255 | (var9.getData1() & 255) << 8 | (var9.getData2() & 255) << 16;
               }
            } else {
               var8 = 0;
               byte[] var10 = var1.getMessage();
               if (var4 > 0) {
                  var8 = var10[0] & 255;
                  if (var4 > 1) {
                     if (var5 == 255) {
                        return;
                     }

                     var8 |= (var10[1] & 255) << 8;
                     if (var4 > 2) {
                        var8 |= (var10[2] & 255) << 16;
                     }
                  }
               }
            }

            MidiOutDevice.this.nSendShortMessage(MidiOutDevice.this.id, var8, var2);
         } else {
            byte[] var6;
            if (var1 instanceof FastSysexMessage) {
               var6 = ((FastSysexMessage)var1).getReadOnlyMessage();
            } else {
               var6 = var1.getMessage();
            }

            int var7 = Math.min(var4, var6.length);
            if (var7 > 0) {
               MidiOutDevice.this.nSendLongMessage(MidiOutDevice.this.id, var6, var7, var2);
            }
         }

      }

      synchronized void sendPackedMidiMessage(int var1, long var2) {
         if (this.isOpen() && MidiOutDevice.this.id != 0L) {
            MidiOutDevice.this.nSendShortMessage(MidiOutDevice.this.id, var1, var2);
         }

      }
   }
}
