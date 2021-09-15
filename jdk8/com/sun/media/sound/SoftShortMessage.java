package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public final class SoftShortMessage extends ShortMessage {
   int channel = 0;

   public int getChannel() {
      return this.channel;
   }

   public void setMessage(int var1, int var2, int var3, int var4) throws InvalidMidiDataException {
      this.channel = var2;
      super.setMessage(var1, var2 & 15, var3, var4);
   }

   public Object clone() {
      SoftShortMessage var1 = new SoftShortMessage();

      try {
         var1.setMessage(this.getCommand(), this.getChannel(), this.getData1(), this.getData2());
         return var1;
      } catch (InvalidMidiDataException var3) {
         throw new IllegalArgumentException(var3);
      }
   }
}
