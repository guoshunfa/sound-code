package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public final class MidiDeviceTransmitterEnvelope implements MidiDeviceTransmitter {
   private final MidiDevice device;
   private final Transmitter transmitter;

   public MidiDeviceTransmitterEnvelope(MidiDevice var1, Transmitter var2) {
      if (var1 != null && var2 != null) {
         this.device = var1;
         this.transmitter = var2;
      } else {
         throw new NullPointerException();
      }
   }

   public void setReceiver(Receiver var1) {
      this.transmitter.setReceiver(var1);
   }

   public Receiver getReceiver() {
      return this.transmitter.getReceiver();
   }

   public void close() {
      this.transmitter.close();
   }

   public MidiDevice getMidiDevice() {
      return this.device;
   }

   public Transmitter getTransmitter() {
      return this.transmitter;
   }
}
