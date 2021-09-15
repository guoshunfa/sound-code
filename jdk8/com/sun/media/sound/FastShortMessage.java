package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

final class FastShortMessage extends ShortMessage {
   private int packedMsg;

   FastShortMessage(int var1) throws InvalidMidiDataException {
      this.packedMsg = var1;
      this.getDataLength(var1 & 255);
   }

   FastShortMessage(ShortMessage var1) {
      this.packedMsg = var1.getStatus() | var1.getData1() << 8 | var1.getData2() << 16;
   }

   int getPackedMsg() {
      return this.packedMsg;
   }

   public byte[] getMessage() {
      int var1 = 0;

      try {
         var1 = this.getDataLength(this.packedMsg & 255) + 1;
      } catch (InvalidMidiDataException var3) {
      }

      byte[] var2 = new byte[var1];
      if (var1 > 0) {
         var2[0] = (byte)(this.packedMsg & 255);
         if (var1 > 1) {
            var2[1] = (byte)((this.packedMsg & '\uff00') >> 8);
            if (var1 > 2) {
               var2[2] = (byte)((this.packedMsg & 16711680) >> 16);
            }
         }
      }

      return var2;
   }

   public int getLength() {
      try {
         return this.getDataLength(this.packedMsg & 255) + 1;
      } catch (InvalidMidiDataException var2) {
         return 0;
      }
   }

   public void setMessage(int var1) throws InvalidMidiDataException {
      int var2 = this.getDataLength(var1);
      if (var2 != 0) {
         super.setMessage(var1);
      }

      this.packedMsg = this.packedMsg & 16776960 | var1 & 255;
   }

   public void setMessage(int var1, int var2, int var3) throws InvalidMidiDataException {
      this.getDataLength(var1);
      this.packedMsg = var1 & 255 | (var2 & 255) << 8 | (var3 & 255) << 16;
   }

   public void setMessage(int var1, int var2, int var3, int var4) throws InvalidMidiDataException {
      this.getDataLength(var1);
      this.packedMsg = var1 & 240 | var2 & 15 | (var3 & 255) << 8 | (var4 & 255) << 16;
   }

   public int getChannel() {
      return this.packedMsg & 15;
   }

   public int getCommand() {
      return this.packedMsg & 240;
   }

   public int getData1() {
      return (this.packedMsg & '\uff00') >> 8;
   }

   public int getData2() {
      return (this.packedMsg & 16711680) >> 16;
   }

   public int getStatus() {
      return this.packedMsg & 255;
   }

   public Object clone() {
      try {
         return new FastShortMessage(this.packedMsg);
      } catch (InvalidMidiDataException var2) {
         return null;
      }
   }
}
