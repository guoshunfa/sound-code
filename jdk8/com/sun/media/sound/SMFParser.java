package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

final class SMFParser {
   private static final int MTrk_MAGIC = 1297379947;
   private static final boolean STRICT_PARSER = false;
   private static final boolean DEBUG = false;
   int tracks;
   DataInputStream stream;
   private int trackLength = 0;
   private byte[] trackData = null;
   private int pos = 0;

   private int readUnsigned() throws IOException {
      return this.trackData[this.pos++] & 255;
   }

   private void read(byte[] var1) throws IOException {
      System.arraycopy(this.trackData, this.pos, var1, 0, var1.length);
      this.pos += var1.length;
   }

   private long readVarInt() throws IOException {
      long var1 = 0L;
      boolean var3 = false;

      int var4;
      do {
         var4 = this.trackData[this.pos++] & 255;
         var1 = (var1 << 7) + (long)(var4 & 127);
      } while((var4 & 128) != 0);

      return var1;
   }

   private int readIntFromStream() throws IOException {
      try {
         return this.stream.readInt();
      } catch (EOFException var2) {
         throw new EOFException("invalid MIDI file");
      }
   }

   boolean nextTrack() throws IOException, InvalidMidiDataException {
      this.trackLength = 0;

      while(this.stream.skipBytes(this.trackLength) == this.trackLength) {
         int var1 = this.readIntFromStream();
         this.trackLength = this.readIntFromStream();
         if (var1 == 1297379947) {
            if (this.trackLength < 0) {
               return false;
            }

            try {
               this.trackData = new byte[this.trackLength];
            } catch (OutOfMemoryError var4) {
               throw new IOException("Track length too big", var4);
            }

            try {
               this.stream.readFully(this.trackData);
            } catch (EOFException var3) {
               return false;
            }

            this.pos = 0;
            return true;
         }
      }

      return false;
   }

   private boolean trackFinished() {
      return this.pos >= this.trackLength;
   }

   void readTrack(Track var1) throws IOException, InvalidMidiDataException {
      try {
         long var2 = 0L;
         int var4 = 0;

         Object var6;
         for(boolean var5 = false; !this.trackFinished() && !var5; var1.add(new MidiEvent((MidiMessage)var6, var2))) {
            int var7 = -1;
            boolean var8 = false;
            var2 += this.readVarInt();
            int var9 = this.readUnsigned();
            if (var9 >= 128) {
               var4 = var9;
            } else {
               var7 = var9;
            }

            switch(var4 & 240) {
            case 128:
            case 144:
            case 160:
            case 176:
            case 224:
               if (var7 == -1) {
                  var7 = this.readUnsigned();
               }

               int var19 = this.readUnsigned();
               var6 = new FastShortMessage(var4 | var7 << 8 | var19 << 16);
               break;
            case 192:
            case 208:
               if (var7 == -1) {
                  var7 = this.readUnsigned();
               }

               var6 = new FastShortMessage(var4 | var7 << 8);
               break;
            case 240:
               switch(var4) {
               case 240:
               case 247:
                  int var10 = (int)this.readVarInt();
                  byte[] var11 = new byte[var10];
                  this.read(var11);
                  SysexMessage var12 = new SysexMessage();
                  var12.setMessage(var4, var11, var10);
                  var6 = var12;
                  continue;
               case 255:
                  int var13 = this.readUnsigned();
                  int var14 = (int)this.readVarInt();

                  byte[] var15;
                  try {
                     var15 = new byte[var14];
                  } catch (OutOfMemoryError var17) {
                     throw new IOException("Meta length too big", var17);
                  }

                  this.read(var15);
                  MetaMessage var16 = new MetaMessage();
                  var16.setMessage(var13, var15, var14);
                  var6 = var16;
                  if (var13 == 47) {
                     var5 = true;
                  }
                  continue;
               default:
                  throw new InvalidMidiDataException("Invalid status byte: " + var4);
               }
            default:
               throw new InvalidMidiDataException("Invalid status byte: " + var4);
            }
         }

      } catch (ArrayIndexOutOfBoundsException var18) {
         throw new EOFException("invalid MIDI file");
      }
   }
}
