package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

final class FastSysexMessage extends SysexMessage {
   FastSysexMessage(byte[] var1) throws InvalidMidiDataException {
      super(var1);
      if (var1.length == 0 || (var1[0] & 255) != 240 && (var1[0] & 255) != 247) {
         super.setMessage(var1, var1.length);
      }

   }

   byte[] getReadOnlyMessage() {
      return this.data;
   }

   public void setMessage(byte[] var1, int var2) throws InvalidMidiDataException {
      if (var1.length == 0 || (var1[0] & 255) != 240 && (var1[0] & 255) != 247) {
         super.setMessage(var1, var1.length);
      }

      this.length = var2;
      this.data = new byte[this.length];
      System.arraycopy(var1, 0, this.data, 0, var2);
   }
}
