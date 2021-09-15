package com.sun.media.sound;

import java.util.TreeMap;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public final class SoftReceiver implements MidiDeviceReceiver {
   boolean open = true;
   private final Object control_mutex;
   private final SoftSynthesizer synth;
   TreeMap<Long, Object> midimessages;
   SoftMainMixer mainmixer;

   public SoftReceiver(SoftSynthesizer var1) {
      this.control_mutex = var1.control_mutex;
      this.synth = var1;
      this.mainmixer = var1.getMainMixer();
      if (this.mainmixer != null) {
         this.midimessages = this.mainmixer.midimessages;
      }

   }

   public MidiDevice getMidiDevice() {
      return this.synth;
   }

   public void send(MidiMessage var1, long var2) {
      synchronized(this.control_mutex) {
         if (!this.open) {
            throw new IllegalStateException("Receiver is not open");
         }
      }

      if (var2 != -1L) {
         synchronized(this.control_mutex) {
            this.mainmixer.activity();

            while(this.midimessages.get(var2) != null) {
               ++var2;
            }

            if (var1 instanceof ShortMessage && ((ShortMessage)var1).getChannel() > 15) {
               this.midimessages.put(var2, var1.clone());
            } else {
               this.midimessages.put(var2, var1.getMessage());
            }
         }
      } else {
         this.mainmixer.processMessage(var1);
      }

   }

   public void close() {
      synchronized(this.control_mutex) {
         this.open = false;
      }

      this.synth.removeReceiver(this);
   }
}
