package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.SequenceInputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.midi.spi.MidiFileWriter;

public final class StandardMidiFileWriter extends MidiFileWriter {
   private static final int MThd_MAGIC = 1297377380;
   private static final int MTrk_MAGIC = 1297379947;
   private static final int ONE_BYTE = 1;
   private static final int TWO_BYTE = 2;
   private static final int SYSEX = 3;
   private static final int META = 4;
   private static final int ERROR = 5;
   private static final int IGNORE = 6;
   private static final int MIDI_TYPE_0 = 0;
   private static final int MIDI_TYPE_1 = 1;
   private static final int bufferSize = 16384;
   private DataOutputStream tddos;
   private static final int[] types = new int[]{0, 1};
   private static final long mask = 127L;

   public int[] getMidiFileTypes() {
      int[] var1 = new int[types.length];
      System.arraycopy(types, 0, var1, 0, types.length);
      return var1;
   }

   public int[] getMidiFileTypes(Sequence var1) {
      Track[] var3 = var1.getTracks();
      int[] var2;
      if (var3.length == 1) {
         var2 = new int[]{0, 1};
      } else {
         var2 = new int[]{1};
      }

      return var2;
   }

   public boolean isFileTypeSupported(int var1) {
      for(int var2 = 0; var2 < types.length; ++var2) {
         if (var1 == types[var2]) {
            return true;
         }
      }

      return false;
   }

   public int write(Sequence var1, int var2, OutputStream var3) throws IOException {
      Object var4 = null;
      boolean var5 = false;
      long var6 = 0L;
      if (!this.isFileTypeSupported(var2, var1)) {
         throw new IllegalArgumentException("Could not write MIDI file");
      } else {
         InputStream var8 = this.getFileStream(var2, var1);
         if (var8 == null) {
            throw new IllegalArgumentException("Could not write MIDI file");
         } else {
            int var10;
            for(byte[] var9 = new byte[16384]; (var10 = var8.read(var9)) >= 0; var6 += (long)var10) {
               var3.write(var9, 0, var10);
            }

            return (int)var6;
         }
      }
   }

   public int write(Sequence var1, int var2, File var3) throws IOException {
      FileOutputStream var4 = new FileOutputStream(var3);
      int var5 = this.write(var1, var2, (OutputStream)var4);
      var4.close();
      return var5;
   }

   private InputStream getFileStream(int var1, Sequence var2) throws IOException {
      Track[] var3 = var2.getTracks();
      byte var4 = 0;
      byte var5 = 14;
      boolean var6 = false;
      PipedOutputStream var9 = null;
      DataOutputStream var10 = null;
      PipedInputStream var11 = null;
      InputStream[] var12 = null;
      Object var13 = null;
      SequenceInputStream var14 = null;
      if (var1 == 0) {
         if (var3.length != 1) {
            return null;
         }
      } else if (var1 == 1) {
         if (var3.length < 1) {
            return null;
         }
      } else if (var3.length == 1) {
         var1 = 0;
      } else {
         if (var3.length <= 1) {
            return null;
         }

         var1 = 1;
      }

      var12 = new InputStream[var3.length];
      int var15 = 0;

      int var16;
      for(var16 = 0; var16 < var3.length; ++var16) {
         try {
            var12[var15] = this.writeTrack(var3[var16], var1);
            ++var15;
         } catch (InvalidMidiDataException var18) {
         }
      }

      if (var15 == 1) {
         var13 = var12[0];
      } else {
         if (var15 <= 1) {
            throw new IllegalArgumentException("invalid MIDI data in sequence");
         }

         var13 = var12[0];

         for(var16 = 1; var16 < var3.length; ++var16) {
            if (var12[var16] != null) {
               var13 = new SequenceInputStream((InputStream)var13, var12[var16]);
            }
         }
      }

      var9 = new PipedOutputStream();
      var10 = new DataOutputStream(var9);
      var11 = new PipedInputStream(var9);
      var10.writeInt(1297377380);
      var10.writeInt(var5 - 8);
      if (var1 == 0) {
         var10.writeShort(0);
      } else {
         var10.writeShort(1);
      }

      var10.writeShort((short)var15);
      float var8 = var2.getDivisionType();
      int var7;
      if (var8 == 0.0F) {
         var7 = var2.getResolution();
      } else {
         short var19;
         if (var8 == 24.0F) {
            var19 = -6144;
            var7 = var19 + (var2.getResolution() & 255);
         } else if (var8 == 25.0F) {
            var19 = -6400;
            var7 = var19 + (var2.getResolution() & 255);
         } else if (var8 == 29.97F) {
            var19 = -7424;
            var7 = var19 + (var2.getResolution() & 255);
         } else {
            if (var8 != 30.0F) {
               return null;
            }

            var19 = -7680;
            var7 = var19 + (var2.getResolution() & 255);
         }
      }

      var10.writeShort(var7);
      var14 = new SequenceInputStream(var11, (InputStream)var13);
      var10.close();
      int var10000 = var4 + var5;
      return var14;
   }

   private int getType(int var1) {
      if ((var1 & 240) == 240) {
         switch(var1) {
         case 240:
         case 247:
            return 3;
         case 255:
            return 4;
         default:
            return 6;
         }
      } else {
         switch(var1 & 240) {
         case 128:
         case 144:
         case 160:
         case 176:
         case 224:
            return 2;
         case 192:
         case 208:
            return 1;
         default:
            return 5;
         }
      }
   }

   private int writeVarInt(long var1) throws IOException {
      int var3 = 1;

      int var4;
      for(var4 = 63; var4 > 0 && (var1 & 127L << var4) == 0L; var4 -= 7) {
      }

      while(var4 > 0) {
         this.tddos.writeByte((int)((var1 & 127L << var4) >> var4 | 128L));
         var4 -= 7;
         ++var3;
      }

      this.tddos.writeByte((int)(var1 & 127L));
      return var3;
   }

   private InputStream writeTrack(Track var1, int var2) throws IOException, InvalidMidiDataException {
      int var3 = 0;
      boolean var4 = false;
      int var5 = var1.size();
      PipedOutputStream var6 = new PipedOutputStream();
      DataOutputStream var7 = new DataOutputStream(var6);
      PipedInputStream var8 = new PipedInputStream(var6);
      ByteArrayOutputStream var9 = new ByteArrayOutputStream();
      this.tddos = new DataOutputStream(var9);
      ByteArrayInputStream var10 = null;
      SequenceInputStream var11 = null;
      long var12 = 0L;
      long var14 = 0L;
      long var16 = 0L;
      int var18 = -1;

      for(int var19 = 0; var19 < var5; ++var19) {
         MidiEvent var20 = var1.get(var19);
         Object var27 = null;
         ShortMessage var28 = null;
         MetaMessage var29 = null;
         SysexMessage var30 = null;
         var16 = var20.getTick();
         var14 = var20.getTick() - var12;
         var12 = var20.getTick();
         int var21 = var20.getMessage().getStatus();
         int var22 = this.getType(var21);
         int var24;
         int var26;
         byte[] var31;
         switch(var22) {
         case 1:
            var28 = (ShortMessage)var20.getMessage();
            var24 = var28.getData1();
            var3 += this.writeVarInt(var14);
            if (var21 != var18) {
               var18 = var21;
               this.tddos.writeByte(var21);
               ++var3;
            }

            this.tddos.writeByte(var24);
            ++var3;
            break;
         case 2:
            var28 = (ShortMessage)var20.getMessage();
            var24 = var28.getData1();
            int var25 = var28.getData2();
            var3 += this.writeVarInt(var14);
            if (var21 != var18) {
               var18 = var21;
               this.tddos.writeByte(var21);
               ++var3;
            }

            this.tddos.writeByte(var24);
            ++var3;
            this.tddos.writeByte(var25);
            ++var3;
            break;
         case 3:
            var30 = (SysexMessage)var20.getMessage();
            var26 = var30.getLength();
            var31 = var30.getMessage();
            var3 += this.writeVarInt(var14);
            var18 = var21;
            this.tddos.writeByte(var31[0]);
            ++var3;
            var3 += this.writeVarInt((long)(var31.length - 1));
            this.tddos.write(var31, 1, var31.length - 1);
            var3 += var31.length - 1;
            break;
         case 4:
            var29 = (MetaMessage)var20.getMessage();
            var26 = var29.getLength();
            var31 = var29.getMessage();
            var3 += this.writeVarInt(var14);
            var18 = var21;
            this.tddos.write(var31, 0, var31.length);
            var3 += var31.length;
         case 5:
         case 6:
            break;
         default:
            throw new InvalidMidiDataException("internal file writer error");
         }
      }

      var7.writeInt(1297379947);
      var7.writeInt(var3);
      var3 += 8;
      var10 = new ByteArrayInputStream(var9.toByteArray());
      var11 = new SequenceInputStream(var8, var10);
      var7.close();
      this.tddos.close();
      return var11;
   }
}
