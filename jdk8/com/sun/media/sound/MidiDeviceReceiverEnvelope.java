package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public final class MidiDeviceReceiverEnvelope implements MidiDeviceReceiver {
   private final MidiDevice device;
   private final Receiver receiver;

   public MidiDeviceReceiverEnvelope(MidiDevice var1, Receiver var2) {
      if (var1 != null && var2 != null) {
         this.device = var1;
         this.receiver = var2;
      } else {
         throw new NullPointerException();
      }
   }

   public void close() {
      this.receiver.close();
   }

   public void send(MidiMessage var1, long var2) {
      this.receiver.send(var1, var2);
   }

   public MidiDevice getMidiDevice() {
      return this.device;
   }

   public Receiver getReceiver() {
      return this.receiver;
   }
}
