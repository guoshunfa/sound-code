package com.sun.media.sound;

import java.util.Random;
import javax.sound.midi.Patch;
import javax.sound.sampled.AudioFormat;

public final class EmergencySoundbank {
   private static final String[] general_midi_instruments = new String[]{"Acoustic Grand Piano", "Bright Acoustic Piano", "Electric Grand Piano", "Honky-tonk Piano", "Electric Piano 1", "Electric Piano 2", "Harpsichord", "Clavi", "Celesta", "Glockenspiel", "Music Box", "Vibraphone", "Marimba", "Xylophone", "Tubular Bells", "Dulcimer", "Drawbar Organ", "Percussive Organ", "Rock Organ", "Church Organ", "Reed Organ", "Accordion", "Harmonica", "Tango Accordion", "Acoustic Guitar (nylon)", "Acoustic Guitar (steel)", "Electric Guitar (jazz)", "Electric Guitar (clean)", "Electric Guitar (muted)", "Overdriven Guitar", "Distortion Guitar", "Guitar harmonics", "Acoustic Bass", "Electric Bass (finger)", "Electric Bass (pick)", "Fretless Bass", "Slap Bass 1", "Slap Bass 2", "Synth Bass 1", "Synth Bass 2", "Violin", "Viola", "Cello", "Contrabass", "Tremolo Strings", "Pizzicato Strings", "Orchestral Harp", "Timpani", "String Ensemble 1", "String Ensemble 2", "SynthStrings 1", "SynthStrings 2", "Choir Aahs", "Voice Oohs", "Synth Voice", "Orchestra Hit", "Trumpet", "Trombone", "Tuba", "Muted Trumpet", "French Horn", "Brass Section", "SynthBrass 1", "SynthBrass 2", "Soprano Sax", "Alto Sax", "Tenor Sax", "Baritone Sax", "Oboe", "English Horn", "Bassoon", "Clarinet", "Piccolo", "Flute", "Recorder", "Pan Flute", "Blown Bottle", "Shakuhachi", "Whistle", "Ocarina", "Lead 1 (square)", "Lead 2 (sawtooth)", "Lead 3 (calliope)", "Lead 4 (chiff)", "Lead 5 (charang)", "Lead 6 (voice)", "Lead 7 (fifths)", "Lead 8 (bass + lead)", "Pad 1 (new age)", "Pad 2 (warm)", "Pad 3 (polysynth)", "Pad 4 (choir)", "Pad 5 (bowed)", "Pad 6 (metallic)", "Pad 7 (halo)", "Pad 8 (sweep)", "FX 1 (rain)", "FX 2 (soundtrack)", "FX 3 (crystal)", "FX 4 (atmosphere)", "FX 5 (brightness)", "FX 6 (goblins)", "FX 7 (echoes)", "FX 8 (sci-fi)", "Sitar", "Banjo", "Shamisen", "Koto", "Kalimba", "Bag pipe", "Fiddle", "Shanai", "Tinkle Bell", "Agogo", "Steel Drums", "Woodblock", "Taiko Drum", "Melodic Tom", "Synth Drum", "Reverse Cymbal", "Guitar Fret Noise", "Breath Noise", "Seashore", "Bird Tweet", "Telephone Ring", "Helicopter", "Applause", "Gunshot"};

   public static SF2Soundbank createSoundbank() throws Exception {
      SF2Soundbank var0 = new SF2Soundbank();
      var0.setName("Emergency GM sound set");
      var0.setVendor("Generated");
      var0.setDescription("Emergency generated soundbank");
      SF2Layer var1 = new_bass_drum(var0);
      SF2Layer var2 = new_snare_drum(var0);
      SF2Layer var3 = new_tom(var0);
      SF2Layer var4 = new_open_hihat(var0);
      SF2Layer var5 = new_closed_hihat(var0);
      SF2Layer var6 = new_crash_cymbal(var0);
      SF2Layer var7 = new_side_stick(var0);
      SF2Layer[] var8 = new SF2Layer[128];
      var8[35] = var1;
      var8[36] = var1;
      var8[38] = var2;
      var8[40] = var2;
      var8[41] = var3;
      var8[43] = var3;
      var8[45] = var3;
      var8[47] = var3;
      var8[48] = var3;
      var8[50] = var3;
      var8[42] = var5;
      var8[44] = var5;
      var8[46] = var4;
      var8[49] = var6;
      var8[51] = var6;
      var8[52] = var6;
      var8[55] = var6;
      var8[57] = var6;
      var8[59] = var6;
      var8[37] = var7;
      var8[39] = var7;
      var8[53] = var7;
      var8[54] = var7;
      var8[56] = var7;
      var8[58] = var7;
      var8[69] = var7;
      var8[70] = var7;
      var8[75] = var7;
      var8[60] = var7;
      var8[61] = var7;
      var8[62] = var7;
      var8[63] = var7;
      var8[64] = var7;
      var8[65] = var7;
      var8[66] = var7;
      var8[67] = var7;
      var8[68] = var7;
      var8[71] = var7;
      var8[72] = var7;
      var8[73] = var7;
      var8[74] = var7;
      var8[76] = var7;
      var8[77] = var7;
      var8[78] = var7;
      var8[79] = var7;
      var8[80] = var7;
      var8[81] = var7;
      SF2Instrument var9 = new SF2Instrument(var0);
      var9.setName("Standard Kit");
      var9.setPatch(new ModelPatch(0, 0, true));
      var0.addInstrument(var9);

      for(int var10 = 0; var10 < var8.length; ++var10) {
         if (var8[var10] != null) {
            SF2InstrumentRegion var11 = new SF2InstrumentRegion();
            var11.setLayer(var8[var10]);
            var11.putBytes(43, new byte[]{(byte)var10, (byte)var10});
            var9.getRegions().add(var11);
         }
      }

      SF2Layer var49 = new_gpiano(var0);
      SF2Layer var50 = new_gpiano2(var0);
      SF2Layer var12 = new_piano_hammer(var0);
      SF2Layer var13 = new_piano1(var0);
      SF2Layer var14 = new_epiano1(var0);
      SF2Layer var15 = new_epiano2(var0);
      SF2Layer var16 = new_guitar1(var0);
      SF2Layer var17 = new_guitar_pick(var0);
      SF2Layer var18 = new_guitar_dist(var0);
      SF2Layer var19 = new_bass1(var0);
      SF2Layer var20 = new_bass2(var0);
      SF2Layer var21 = new_synthbass(var0);
      SF2Layer var22 = new_string2(var0);
      SF2Layer var23 = new_orchhit(var0);
      SF2Layer var24 = new_choir(var0);
      SF2Layer var25 = new_solostring(var0);
      SF2Layer var26 = new_organ(var0);
      SF2Layer var27 = new_ch_organ(var0);
      SF2Layer var28 = new_bell(var0);
      SF2Layer var29 = new_flute(var0);
      SF2Layer var30 = new_timpani(var0);
      SF2Layer var31 = new_melodic_toms(var0);
      SF2Layer var32 = new_trumpet(var0);
      SF2Layer var33 = new_trombone(var0);
      SF2Layer var34 = new_brass_section(var0);
      SF2Layer var35 = new_horn(var0);
      SF2Layer var36 = new_sax(var0);
      SF2Layer var37 = new_oboe(var0);
      SF2Layer var38 = new_bassoon(var0);
      SF2Layer var39 = new_clarinet(var0);
      SF2Layer var40 = new_reverse_cymbal(var0);
      newInstrument(var0, "Piano", new Patch(0, 0), var49, var12);
      newInstrument(var0, "Piano", new Patch(0, 1), var50, var12);
      newInstrument(var0, "Piano", new Patch(0, 2), var13);
      SF2Instrument var42 = newInstrument(var0, "Honky-tonk Piano", new Patch(0, 3), var13, var13);
      SF2InstrumentRegion var43 = (SF2InstrumentRegion)var42.getRegions().get(0);
      var43.putInteger(8, 80);
      var43.putInteger(52, 30);
      var43 = (SF2InstrumentRegion)var42.getRegions().get(1);
      var43.putInteger(8, 30);
      newInstrument(var0, "Rhodes", new Patch(0, 4), var15);
      newInstrument(var0, "Rhodes", new Patch(0, 5), var15);
      newInstrument(var0, "Clavinet", new Patch(0, 6), var14);
      newInstrument(var0, "Clavinet", new Patch(0, 7), var14);
      newInstrument(var0, "Rhodes", new Patch(0, 8), var15);
      newInstrument(var0, "Bell", new Patch(0, 9), var28);
      newInstrument(var0, "Bell", new Patch(0, 10), var28);
      newInstrument(var0, "Vibraphone", new Patch(0, 11), var28);
      newInstrument(var0, "Marimba", new Patch(0, 12), var28);
      newInstrument(var0, "Marimba", new Patch(0, 13), var28);
      newInstrument(var0, "Bell", new Patch(0, 14), var28);
      newInstrument(var0, "Rock Organ", new Patch(0, 15), var26);
      newInstrument(var0, "Rock Organ", new Patch(0, 16), var26);
      newInstrument(var0, "Perc Organ", new Patch(0, 17), var26);
      newInstrument(var0, "Rock Organ", new Patch(0, 18), var26);
      newInstrument(var0, "Church Organ", new Patch(0, 19), var27);
      newInstrument(var0, "Accordion", new Patch(0, 20), var26);
      newInstrument(var0, "Accordion", new Patch(0, 21), var26);
      newInstrument(var0, "Accordion", new Patch(0, 22), var26);
      newInstrument(var0, "Accordion", new Patch(0, 23), var26);
      newInstrument(var0, "Guitar", new Patch(0, 24), var16, var17);
      newInstrument(var0, "Guitar", new Patch(0, 25), var16, var17);
      newInstrument(var0, "Guitar", new Patch(0, 26), var16, var17);
      newInstrument(var0, "Guitar", new Patch(0, 27), var16, var17);
      newInstrument(var0, "Guitar", new Patch(0, 28), var16, var17);
      newInstrument(var0, "Distorted Guitar", new Patch(0, 29), var18);
      newInstrument(var0, "Distorted Guitar", new Patch(0, 30), var18);
      newInstrument(var0, "Guitar", new Patch(0, 31), var16, var17);
      newInstrument(var0, "Finger Bass", new Patch(0, 32), var19);
      newInstrument(var0, "Finger Bass", new Patch(0, 33), var19);
      newInstrument(var0, "Finger Bass", new Patch(0, 34), var19);
      newInstrument(var0, "Frettless Bass", new Patch(0, 35), var20);
      newInstrument(var0, "Frettless Bass", new Patch(0, 36), var20);
      newInstrument(var0, "Frettless Bass", new Patch(0, 37), var20);
      newInstrument(var0, "Synth Bass1", new Patch(0, 38), var21);
      newInstrument(var0, "Synth Bass2", new Patch(0, 39), var21);
      newInstrument(var0, "Solo String", new Patch(0, 40), var22, var25);
      newInstrument(var0, "Solo String", new Patch(0, 41), var22, var25);
      newInstrument(var0, "Solo String", new Patch(0, 42), var22, var25);
      newInstrument(var0, "Solo String", new Patch(0, 43), var22, var25);
      newInstrument(var0, "Solo String", new Patch(0, 44), var22, var25);
      newInstrument(var0, "Def", new Patch(0, 45), var13);
      newInstrument(var0, "Harp", new Patch(0, 46), var28);
      newInstrument(var0, "Timpani", new Patch(0, 47), var30);
      newInstrument(var0, "Strings", new Patch(0, 48), var22);
      var42 = newInstrument(var0, "Slow Strings", new Patch(0, 49), var22);
      var43 = (SF2InstrumentRegion)var42.getRegions().get(0);
      var43.putInteger(34, 2500);
      var43.putInteger(38, 2000);
      newInstrument(var0, "Synth Strings", new Patch(0, 50), var22);
      newInstrument(var0, "Synth Strings", new Patch(0, 51), var22);
      newInstrument(var0, "Choir", new Patch(0, 52), var24);
      newInstrument(var0, "Choir", new Patch(0, 53), var24);
      newInstrument(var0, "Choir", new Patch(0, 54), var24);
      SF2Instrument var44 = newInstrument(var0, "Orch Hit", new Patch(0, 55), var23, var23, var30);
      var43 = (SF2InstrumentRegion)var44.getRegions().get(0);
      var43.putInteger(51, -12);
      var43.putInteger(48, -100);
      newInstrument(var0, "Trumpet", new Patch(0, 56), var32);
      newInstrument(var0, "Trombone", new Patch(0, 57), var33);
      newInstrument(var0, "Trombone", new Patch(0, 58), var33);
      newInstrument(var0, "Trumpet", new Patch(0, 59), var32);
      newInstrument(var0, "Horn", new Patch(0, 60), var35);
      newInstrument(var0, "Brass Section", new Patch(0, 61), var34);
      newInstrument(var0, "Brass Section", new Patch(0, 62), var34);
      newInstrument(var0, "Brass Section", new Patch(0, 63), var34);
      newInstrument(var0, "Sax", new Patch(0, 64), var36);
      newInstrument(var0, "Sax", new Patch(0, 65), var36);
      newInstrument(var0, "Sax", new Patch(0, 66), var36);
      newInstrument(var0, "Sax", new Patch(0, 67), var36);
      newInstrument(var0, "Oboe", new Patch(0, 68), var37);
      newInstrument(var0, "Horn", new Patch(0, 69), var35);
      newInstrument(var0, "Bassoon", new Patch(0, 70), var38);
      newInstrument(var0, "Clarinet", new Patch(0, 71), var39);
      newInstrument(var0, "Flute", new Patch(0, 72), var29);
      newInstrument(var0, "Flute", new Patch(0, 73), var29);
      newInstrument(var0, "Flute", new Patch(0, 74), var29);
      newInstrument(var0, "Flute", new Patch(0, 75), var29);
      newInstrument(var0, "Flute", new Patch(0, 76), var29);
      newInstrument(var0, "Flute", new Patch(0, 77), var29);
      newInstrument(var0, "Flute", new Patch(0, 78), var29);
      newInstrument(var0, "Flute", new Patch(0, 79), var29);
      newInstrument(var0, "Organ", new Patch(0, 80), var26);
      newInstrument(var0, "Organ", new Patch(0, 81), var26);
      newInstrument(var0, "Flute", new Patch(0, 82), var29);
      newInstrument(var0, "Organ", new Patch(0, 83), var26);
      newInstrument(var0, "Organ", new Patch(0, 84), var26);
      newInstrument(var0, "Choir", new Patch(0, 85), var24);
      newInstrument(var0, "Organ", new Patch(0, 86), var26);
      newInstrument(var0, "Organ", new Patch(0, 87), var26);
      newInstrument(var0, "Synth Strings", new Patch(0, 88), var22);
      newInstrument(var0, "Organ", new Patch(0, 89), var26);
      newInstrument(var0, "Def", new Patch(0, 90), var13);
      newInstrument(var0, "Choir", new Patch(0, 91), var24);
      newInstrument(var0, "Organ", new Patch(0, 92), var26);
      newInstrument(var0, "Organ", new Patch(0, 93), var26);
      newInstrument(var0, "Organ", new Patch(0, 94), var26);
      newInstrument(var0, "Organ", new Patch(0, 95), var26);
      newInstrument(var0, "Organ", new Patch(0, 96), var26);
      newInstrument(var0, "Organ", new Patch(0, 97), var26);
      newInstrument(var0, "Bell", new Patch(0, 98), var28);
      newInstrument(var0, "Organ", new Patch(0, 99), var26);
      newInstrument(var0, "Organ", new Patch(0, 100), var26);
      newInstrument(var0, "Organ", new Patch(0, 101), var26);
      newInstrument(var0, "Def", new Patch(0, 102), var13);
      newInstrument(var0, "Synth Strings", new Patch(0, 103), var22);
      newInstrument(var0, "Def", new Patch(0, 104), var13);
      newInstrument(var0, "Def", new Patch(0, 105), var13);
      newInstrument(var0, "Def", new Patch(0, 106), var13);
      newInstrument(var0, "Def", new Patch(0, 107), var13);
      newInstrument(var0, "Marimba", new Patch(0, 108), var28);
      newInstrument(var0, "Sax", new Patch(0, 109), var36);
      newInstrument(var0, "Solo String", new Patch(0, 110), var22, var25);
      newInstrument(var0, "Oboe", new Patch(0, 111), var37);
      newInstrument(var0, "Bell", new Patch(0, 112), var28);
      newInstrument(var0, "Melodic Toms", new Patch(0, 113), var31);
      newInstrument(var0, "Marimba", new Patch(0, 114), var28);
      newInstrument(var0, "Melodic Toms", new Patch(0, 115), var31);
      newInstrument(var0, "Melodic Toms", new Patch(0, 116), var31);
      newInstrument(var0, "Melodic Toms", new Patch(0, 117), var31);
      newInstrument(var0, "Reverse Cymbal", new Patch(0, 118), var40);
      newInstrument(var0, "Reverse Cymbal", new Patch(0, 119), var40);
      newInstrument(var0, "Guitar", new Patch(0, 120), var16);
      newInstrument(var0, "Def", new Patch(0, 121), var13);
      var44 = newInstrument(var0, "Seashore/Reverse Cymbal", new Patch(0, 122), var40);
      var43 = (SF2InstrumentRegion)var44.getRegions().get(0);
      var43.putInteger(37, 1000);
      var43.putInteger(36, 18500);
      var43.putInteger(38, 4500);
      var43.putInteger(8, -4500);
      var44 = newInstrument(var0, "Bird/Flute", new Patch(0, 123), var29);
      var43 = (SF2InstrumentRegion)var44.getRegions().get(0);
      var43.putInteger(51, 24);
      var43.putInteger(36, -3000);
      var43.putInteger(37, 1000);
      newInstrument(var0, "Def", new Patch(0, 124), var7);
      var44 = newInstrument(var0, "Seashore/Reverse Cymbal", new Patch(0, 125), var40);
      var43 = (SF2InstrumentRegion)var44.getRegions().get(0);
      var43.putInteger(37, 1000);
      var43.putInteger(36, 18500);
      var43.putInteger(38, 4500);
      var43.putInteger(8, -4500);
      newInstrument(var0, "Applause/crash_cymbal", new Patch(0, 126), var6);
      newInstrument(var0, "Gunshot/side_stick", new Patch(0, 127), var7);
      SF2Instrument[] var51 = var0.getInstruments();
      int var45 = var51.length;

      for(int var46 = 0; var46 < var45; ++var46) {
         SF2Instrument var47 = var51[var46];
         Patch var48 = var47.getPatch();
         if (!(var48 instanceof ModelPatch) || !((ModelPatch)var48).isPercussion()) {
            var47.setName(general_midi_instruments[var48.getProgram()]);
         }
      }

      return var0;
   }

   public static SF2Layer new_bell(SF2Soundbank var0) {
      Random var1 = new Random(102030201L);
      byte var2 = 8;
      int var3 = 4096 * var2;
      double[] var4 = new double[var3 * 2];
      double var5 = (double)(var2 * 25);
      double var7 = 0.01D;
      double var9 = 0.05D;
      double var11 = 0.2D;
      double var13 = 1.0E-5D;
      double var15 = var11;
      double var17 = Math.pow(var13 / var11, 0.025D);

      for(int var19 = 0; var19 < 40; ++var19) {
         double var20 = 1.0D + (var1.nextDouble() * 2.0D - 1.0D) * 0.01D;
         double var22 = var7 + (var9 - var7) * ((double)var19 / 40.0D);
         complexGaussianDist(var4, var5 * (double)(var19 + 1) * var20, var22, var15);
         var15 *= var17;
      }

      SF2Sample var24 = newSimpleFFTSample(var0, "EPiano", var4, var5);
      SF2Layer var25 = newLayer(var0, "EPiano", var24);
      SF2Region var21 = (SF2Region)var25.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -12000);
      var21.putInteger(38, 0);
      var21.putInteger(36, 4000);
      var21.putInteger(37, 1000);
      var21.putInteger(26, 1200);
      var21.putInteger(30, 12000);
      var21.putInteger(11, -9000);
      var21.putInteger(8, 16000);
      return var25;
   }

   public static SF2Layer new_guitar1(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 0.01D;
      double var8 = 0.01D;
      double var10 = 2.0D;
      double var12 = 0.01D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.025D);
      double[] var18 = new double[40];

      int var19;
      for(var19 = 0; var19 < 40; ++var19) {
         var18[var19] = var14;
         var14 *= var16;
      }

      var18[0] = 2.0D;
      var18[1] = 0.5D;
      var18[2] = 0.45D;
      var18[3] = 0.2D;
      var18[4] = 1.0D;
      var18[5] = 0.5D;
      var18[6] = 2.0D;
      var18[7] = 1.0D;
      var18[8] = 0.5D;
      var18[9] = 1.0D;
      var18[9] = 0.5D;
      var18[10] = 0.2D;
      var18[11] = 1.0D;
      var18[12] = 0.7D;
      var18[13] = 0.5D;
      var18[14] = 1.0D;

      for(var19 = 0; var19 < 40; ++var19) {
         double var20 = var6 + (var8 - var6) * ((double)var19 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var19 + 1), var20, var18[var19]);
      }

      SF2Sample var22 = newSimpleFFTSample(var0, "Guitar", var3, var4);
      SF2Layer var23 = newLayer(var0, "Guitar", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -12000);
      var21.putInteger(38, 0);
      var21.putInteger(36, 2400);
      var21.putInteger(37, 1000);
      var21.putInteger(26, -100);
      var21.putInteger(30, 12000);
      var21.putInteger(11, -6000);
      var21.putInteger(8, 16000);
      var21.putInteger(48, -20);
      return var23;
   }

   public static SF2Layer new_guitar_dist(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 0.01D;
      double var8 = 0.01D;
      double var10 = 2.0D;
      double var12 = 0.01D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.025D);
      double[] var18 = new double[40];

      int var19;
      for(var19 = 0; var19 < 40; ++var19) {
         var18[var19] = var14;
         var14 *= var16;
      }

      var18[0] = 5.0D;
      var18[1] = 2.0D;
      var18[2] = 0.45D;
      var18[3] = 0.2D;
      var18[4] = 1.0D;
      var18[5] = 0.5D;
      var18[6] = 2.0D;
      var18[7] = 1.0D;
      var18[8] = 0.5D;
      var18[9] = 1.0D;
      var18[9] = 0.5D;
      var18[10] = 0.2D;
      var18[11] = 1.0D;
      var18[12] = 0.7D;
      var18[13] = 0.5D;
      var18[14] = 1.0D;

      for(var19 = 0; var19 < 40; ++var19) {
         double var20 = var6 + (var8 - var6) * ((double)var19 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var19 + 1), var20, var18[var19]);
      }

      SF2Sample var22 = newSimpleFFTSample_dist(var0, "Distorted Guitar", var3, var4, 10000.0D);
      SF2Layer var23 = newLayer(var0, "Distorted Guitar", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -12000);
      var21.putInteger(38, 0);
      var21.putInteger(8, 8000);
      return var23;
   }

   public static SF2Layer new_guitar_pick(SF2Soundbank var0) {
      byte var2 = 2;
      int var3 = 4096 * var2;
      double[] var4 = new double[2 * var3];
      Random var5 = new Random(3049912L);

      int var6;
      for(var6 = 0; var6 < var4.length; var6 += 2) {
         var4[var6] = 2.0D * (var5.nextDouble() - 0.5D);
      }

      fft(var4);

      for(var6 = var3 / 2; var6 < var4.length; ++var6) {
         var4[var6] = 0.0D;
      }

      for(var6 = 0; var6 < 2048 * var2; ++var6) {
         var4[var6] *= Math.exp(-Math.abs((double)(var6 - 23) / (double)var2) * 1.2D) + Math.exp(-Math.abs((double)(var6 - 40) / (double)var2) * 0.9D);
      }

      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.8D);
      var4 = realPart(var4);
      double var13 = 1.0D;

      for(int var8 = 0; var8 < var4.length; ++var8) {
         var4[var8] *= var13;
         var13 *= 0.9994D;
      }

      fadeUp((double[])var4, 80);
      SF2Sample var9 = newSimpleDrumSample(var0, "Guitar Noise", var4);
      SF2Layer var10 = new SF2Layer(var0);
      var10.setName("Guitar Noise");
      SF2GlobalRegion var11 = new SF2GlobalRegion();
      var10.setGlobalZone(var11);
      var0.addResource(var10);
      SF2LayerRegion var12 = new SF2LayerRegion();
      var12.putInteger(38, 12000);
      var12.setSample(var9);
      var10.getRegions().add(var12);
      return var10;
   }

   public static SF2Layer new_gpiano(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 0.2D;
      double var8 = 0.001D;
      double var10 = var6;
      double var12 = Math.pow(var8 / var6, 0.06666666666666667D);
      double[] var14 = new double[30];

      int var15;
      for(var15 = 0; var15 < 30; ++var15) {
         var14[var15] = var10;
         var10 *= var12;
      }

      var14[0] *= 2.0D;
      var14[4] *= 2.0D;
      var14[12] *= 0.9D;
      var14[13] *= 0.7D;

      for(var15 = 14; var15 < 30; ++var15) {
         var14[var15] *= 0.5D;
      }

      for(var15 = 0; var15 < 30; ++var15) {
         double var16 = 0.2D;
         double var18 = var14[var15];
         if (var15 > 10) {
            var16 = 5.0D;
            var18 *= 10.0D;
         }

         int var20 = 0;
         if (var15 > 5) {
            var20 = (var15 - 5) * 7;
         }

         complexGaussianDist(var3, var4 * (double)(var15 + 1) + (double)var20, var16, var18);
      }

      SF2Sample var21 = newSimpleFFTSample(var0, "Grand Piano", var3, var4, 200);
      SF2Layer var22 = newLayer(var0, "Grand Piano", var21);
      SF2Region var17 = (SF2Region)var22.getRegions().get(0);
      var17.putInteger(54, 1);
      var17.putInteger(34, -7000);
      var17.putInteger(38, 0);
      var17.putInteger(36, 4000);
      var17.putInteger(37, 1000);
      var17.putInteger(26, -6000);
      var17.putInteger(30, 12000);
      var17.putInteger(11, -5500);
      var17.putInteger(8, 18000);
      return var22;
   }

   public static SF2Layer new_gpiano2(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 0.2D;
      double var8 = 0.001D;
      double var10 = var6;
      double var12 = Math.pow(var8 / var6, 0.05D);
      double[] var14 = new double[30];

      int var15;
      for(var15 = 0; var15 < 30; ++var15) {
         var14[var15] = var10;
         var10 *= var12;
      }

      var14[0] *= 1.0D;
      var14[4] *= 2.0D;
      var14[12] *= 0.9D;
      var14[13] *= 0.7D;

      for(var15 = 14; var15 < 30; ++var15) {
         var14[var15] *= 0.5D;
      }

      for(var15 = 0; var15 < 30; ++var15) {
         double var16 = 0.2D;
         double var18 = var14[var15];
         if (var15 > 10) {
            var16 = 5.0D;
            var18 *= 10.0D;
         }

         int var20 = 0;
         if (var15 > 5) {
            var20 = (var15 - 5) * 7;
         }

         complexGaussianDist(var3, var4 * (double)(var15 + 1) + (double)var20, var16, var18);
      }

      SF2Sample var21 = newSimpleFFTSample(var0, "Grand Piano", var3, var4, 200);
      SF2Layer var22 = newLayer(var0, "Grand Piano", var21);
      SF2Region var17 = (SF2Region)var22.getRegions().get(0);
      var17.putInteger(54, 1);
      var17.putInteger(34, -7000);
      var17.putInteger(38, 0);
      var17.putInteger(36, 4000);
      var17.putInteger(37, 1000);
      var17.putInteger(26, -6000);
      var17.putInteger(30, 12000);
      var17.putInteger(11, -5500);
      var17.putInteger(8, 18000);
      return var22;
   }

   public static SF2Layer new_piano_hammer(SF2Soundbank var0) {
      byte var2 = 2;
      int var3 = 4096 * var2;
      double[] var4 = new double[2 * var3];
      Random var5 = new Random(3049912L);

      int var6;
      for(var6 = 0; var6 < var4.length; var6 += 2) {
         var4[var6] = 2.0D * (var5.nextDouble() - 0.5D);
      }

      fft(var4);

      for(var6 = var3 / 2; var6 < var4.length; ++var6) {
         var4[var6] = 0.0D;
      }

      for(var6 = 0; var6 < 2048 * var2; ++var6) {
         var4[var6] *= Math.exp(-Math.abs((double)(var6 - 37) / (double)var2) * 0.05D);
      }

      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.6D);
      var4 = realPart(var4);
      double var13 = 1.0D;

      for(int var8 = 0; var8 < var4.length; ++var8) {
         var4[var8] *= var13;
         var13 *= 0.9997D;
      }

      fadeUp((double[])var4, 80);
      SF2Sample var9 = newSimpleDrumSample(var0, "Piano Hammer", var4);
      SF2Layer var10 = new SF2Layer(var0);
      var10.setName("Piano Hammer");
      SF2GlobalRegion var11 = new SF2GlobalRegion();
      var10.setGlobalZone(var11);
      var0.addResource(var10);
      SF2LayerRegion var12 = new SF2LayerRegion();
      var12.putInteger(38, 12000);
      var12.setSample(var9);
      var10.getRegions().add(var12);
      return var10;
   }

   public static SF2Layer new_piano1(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 0.2D;
      double var8 = 1.0E-4D;
      double var10 = var6;
      double var12 = Math.pow(var8 / var6, 0.025D);
      double[] var14 = new double[30];

      int var15;
      for(var15 = 0; var15 < 30; ++var15) {
         var14[var15] = var10;
         var10 *= var12;
      }

      var14[0] *= 5.0D;
      var14[2] *= 0.1D;
      var14[7] *= 5.0D;

      for(var15 = 0; var15 < 30; ++var15) {
         double var16 = 0.2D;
         double var18 = var14[var15];
         if (var15 > 12) {
            var16 = 5.0D;
            var18 *= 10.0D;
         }

         int var20 = 0;
         if (var15 > 5) {
            var20 = (var15 - 5) * 7;
         }

         complexGaussianDist(var3, var4 * (double)(var15 + 1) + (double)var20, var16, var18);
      }

      complexGaussianDist(var3, var4 * 15.5D, 1.0D, 0.1D);
      complexGaussianDist(var3, var4 * 17.5D, 1.0D, 0.01D);
      SF2Sample var21 = newSimpleFFTSample(var0, "EPiano", var3, var4, 200);
      SF2Layer var22 = newLayer(var0, "EPiano", var21);
      SF2Region var17 = (SF2Region)var22.getRegions().get(0);
      var17.putInteger(54, 1);
      var17.putInteger(34, -12000);
      var17.putInteger(38, 0);
      var17.putInteger(36, 4000);
      var17.putInteger(37, 1000);
      var17.putInteger(26, -1200);
      var17.putInteger(30, 12000);
      var17.putInteger(11, -5500);
      var17.putInteger(8, 16000);
      return var22;
   }

   public static SF2Layer new_epiano1(SF2Soundbank var0) {
      Random var1 = new Random(302030201L);
      byte var2 = 8;
      int var3 = 4096 * var2;
      double[] var4 = new double[var3 * 2];
      double var5 = (double)(var2 * 25);
      double var7 = 0.05D;
      double var9 = 0.05D;
      double var11 = 0.2D;
      double var13 = 1.0E-4D;
      double var15 = var11;
      double var17 = Math.pow(var13 / var11, 0.025D);

      for(int var19 = 0; var19 < 40; ++var19) {
         double var20 = 1.0D + (var1.nextDouble() * 2.0D - 1.0D) * 1.0E-4D;
         double var22 = var7 + (var9 - var7) * ((double)var19 / 40.0D);
         complexGaussianDist(var4, var5 * (double)(var19 + 1) * var20, var22, var15);
         var15 *= var17;
      }

      SF2Sample var24 = newSimpleFFTSample(var0, "EPiano", var4, var5);
      SF2Layer var25 = newLayer(var0, "EPiano", var24);
      SF2Region var21 = (SF2Region)var25.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -12000);
      var21.putInteger(38, 0);
      var21.putInteger(36, 4000);
      var21.putInteger(37, 1000);
      var21.putInteger(26, 1200);
      var21.putInteger(30, 12000);
      var21.putInteger(11, -9000);
      var21.putInteger(8, 16000);
      return var25;
   }

   public static SF2Layer new_epiano2(SF2Soundbank var0) {
      Random var1 = new Random(302030201L);
      byte var2 = 8;
      int var3 = 4096 * var2;
      double[] var4 = new double[var3 * 2];
      double var5 = (double)(var2 * 25);
      double var7 = 0.01D;
      double var9 = 0.05D;
      double var11 = 0.2D;
      double var13 = 1.0E-5D;
      double var15 = var11;
      double var17 = Math.pow(var13 / var11, 0.025D);

      for(int var19 = 0; var19 < 40; ++var19) {
         double var20 = 1.0D + (var1.nextDouble() * 2.0D - 1.0D) * 1.0E-4D;
         double var22 = var7 + (var9 - var7) * ((double)var19 / 40.0D);
         complexGaussianDist(var4, var5 * (double)(var19 + 1) * var20, var22, var15);
         var15 *= var17;
      }

      SF2Sample var24 = newSimpleFFTSample(var0, "EPiano", var4, var5);
      SF2Layer var25 = newLayer(var0, "EPiano", var24);
      SF2Region var21 = (SF2Region)var25.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -12000);
      var21.putInteger(38, 0);
      var21.putInteger(36, 8000);
      var21.putInteger(37, 1000);
      var21.putInteger(26, 2400);
      var21.putInteger(30, 12000);
      var21.putInteger(11, -9000);
      var21.putInteger(8, 16000);
      var21.putInteger(48, -100);
      return var25;
   }

   public static SF2Layer new_bass1(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 0.05D;
      double var8 = 0.05D;
      double var10 = 0.2D;
      double var12 = 0.02D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.04D);
      double[] var18 = new double[25];

      int var19;
      for(var19 = 0; var19 < 25; ++var19) {
         var18[var19] = var14;
         var14 *= var16;
      }

      var18[0] *= 8.0D;
      var18[1] *= 4.0D;
      var18[3] *= 8.0D;
      var18[5] *= 8.0D;

      for(var19 = 0; var19 < 25; ++var19) {
         double var20 = var6 + (var8 - var6) * ((double)var19 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var19 + 1), var20, var18[var19]);
      }

      SF2Sample var22 = newSimpleFFTSample(var0, "Bass", var3, var4);
      SF2Layer var23 = newLayer(var0, "Bass", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -12000);
      var21.putInteger(38, 0);
      var21.putInteger(36, 4000);
      var21.putInteger(37, 1000);
      var21.putInteger(26, -3000);
      var21.putInteger(30, 12000);
      var21.putInteger(11, -5000);
      var21.putInteger(8, 11000);
      var21.putInteger(48, -100);
      return var23;
   }

   public static SF2Layer new_synthbass(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 0.05D;
      double var8 = 0.05D;
      double var10 = 0.2D;
      double var12 = 0.02D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.04D);
      double[] var18 = new double[25];

      int var19;
      for(var19 = 0; var19 < 25; ++var19) {
         var18[var19] = var14;
         var14 *= var16;
      }

      var18[0] *= 16.0D;
      var18[1] *= 4.0D;
      var18[3] *= 16.0D;
      var18[5] *= 8.0D;

      for(var19 = 0; var19 < 25; ++var19) {
         double var20 = var6 + (var8 - var6) * ((double)var19 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var19 + 1), var20, var18[var19]);
      }

      SF2Sample var22 = newSimpleFFTSample(var0, "Bass", var3, var4);
      SF2Layer var23 = newLayer(var0, "Bass", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -12000);
      var21.putInteger(38, 0);
      var21.putInteger(36, 4000);
      var21.putInteger(37, 1000);
      var21.putInteger(26, -3000);
      var21.putInteger(30, 12000);
      var21.putInteger(11, -3000);
      var21.putInteger(9, 100);
      var21.putInteger(8, 8000);
      var21.putInteger(48, -100);
      return var23;
   }

   public static SF2Layer new_bass2(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 0.05D;
      double var8 = 0.05D;
      double var10 = 0.2D;
      double var12 = 0.002D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.04D);
      double[] var18 = new double[25];

      int var19;
      for(var19 = 0; var19 < 25; ++var19) {
         var18[var19] = var14;
         var14 *= var16;
      }

      var18[0] *= 8.0D;
      var18[1] *= 4.0D;
      var18[3] *= 8.0D;
      var18[5] *= 8.0D;

      for(var19 = 0; var19 < 25; ++var19) {
         double var20 = var6 + (var8 - var6) * ((double)var19 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var19 + 1), var20, var18[var19]);
      }

      SF2Sample var22 = newSimpleFFTSample(var0, "Bass2", var3, var4);
      SF2Layer var23 = newLayer(var0, "Bass2", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -8000);
      var21.putInteger(38, 0);
      var21.putInteger(36, 4000);
      var21.putInteger(37, 1000);
      var21.putInteger(26, -6000);
      var21.putInteger(30, 12000);
      var21.putInteger(8, 5000);
      var21.putInteger(48, -100);
      return var23;
   }

   public static SF2Layer new_solostring(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 2.0D;
      double var8 = 2.0D;
      double var10 = 0.2D;
      double var12 = 0.01D;
      double[] var14 = new double[18];
      double var15 = var10;
      double var17 = Math.pow(var12 / var10, 0.025D);

      int var19;
      for(var19 = 0; var19 < var14.length; ++var19) {
         var15 *= var17;
         var14[var19] = var15;
      }

      var14[0] *= 5.0D;
      var14[1] *= 5.0D;
      var14[2] *= 5.0D;
      var14[3] *= 4.0D;
      var14[4] *= 4.0D;
      var14[5] *= 3.0D;
      var14[6] *= 3.0D;
      var14[7] *= 2.0D;

      for(var19 = 0; var19 < var14.length; ++var19) {
         double var20 = var6 + (var8 - var6) * ((double)var19 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var19 + 1), var20, var15);
      }

      SF2Sample var22 = newSimpleFFTSample(var0, "Strings", var3, var4);
      SF2Layer var23 = newLayer(var0, "Strings", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -5000);
      var21.putInteger(38, 1000);
      var21.putInteger(36, 4000);
      var21.putInteger(37, -100);
      var21.putInteger(8, 9500);
      var21.putInteger(24, -1000);
      var21.putInteger(6, 15);
      return var23;
   }

   public static SF2Layer new_orchhit(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 2.0D;
      double var8 = 80.0D;
      double var10 = 0.2D;
      double var12 = 0.001D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.025D);

      for(int var18 = 0; var18 < 40; ++var18) {
         double var19 = var6 + (var8 - var6) * ((double)var18 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var18 + 1), var19, var14);
         var14 *= var16;
      }

      complexGaussianDist(var3, var4 * 4.0D, 300.0D, 1.0D);
      SF2Sample var21 = newSimpleFFTSample(var0, "Och Strings", var3, var4);
      SF2Layer var22 = newLayer(var0, "Och Strings", var21);
      SF2Region var20 = (SF2Region)var22.getRegions().get(0);
      var20.putInteger(54, 1);
      var20.putInteger(34, -5000);
      var20.putInteger(38, 200);
      var20.putInteger(36, 200);
      var20.putInteger(37, 1000);
      var20.putInteger(8, 9500);
      return var22;
   }

   public static SF2Layer new_string2(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 2.0D;
      double var8 = 80.0D;
      double var10 = 0.2D;
      double var12 = 0.001D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.025D);

      for(int var18 = 0; var18 < 40; ++var18) {
         double var19 = var6 + (var8 - var6) * ((double)var18 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var18 + 1), var19, var14);
         var14 *= var16;
      }

      SF2Sample var21 = newSimpleFFTSample(var0, "Strings", var3, var4);
      SF2Layer var22 = newLayer(var0, "Strings", var21);
      SF2Region var20 = (SF2Region)var22.getRegions().get(0);
      var20.putInteger(54, 1);
      var20.putInteger(34, -5000);
      var20.putInteger(38, 1000);
      var20.putInteger(36, 4000);
      var20.putInteger(37, -100);
      var20.putInteger(8, 9500);
      return var22;
   }

   public static SF2Layer new_choir(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 25);
      double var6 = 2.0D;
      double var8 = 80.0D;
      double var10 = 0.2D;
      double var12 = 0.001D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.025D);
      double[] var18 = new double[40];

      int var19;
      for(var19 = 0; var19 < var18.length; ++var19) {
         var14 *= var16;
         var18[var19] = var14;
      }

      var18[5] *= 0.1D;
      var18[6] *= 0.01D;
      var18[7] *= 0.1D;
      var18[8] *= 0.1D;

      for(var19 = 0; var19 < var18.length; ++var19) {
         double var20 = var6 + (var8 - var6) * ((double)var19 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var19 + 1), var20, var18[var19]);
      }

      SF2Sample var22 = newSimpleFFTSample(var0, "Strings", var3, var4);
      SF2Layer var23 = newLayer(var0, "Strings", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -5000);
      var21.putInteger(38, 1000);
      var21.putInteger(36, 4000);
      var21.putInteger(37, -100);
      var21.putInteger(8, 9500);
      return var23;
   }

   public static SF2Layer new_organ(SF2Soundbank var0) {
      Random var1 = new Random(102030201L);
      byte var2 = 1;
      int var3 = 4096 * var2;
      double[] var4 = new double[var3 * 2];
      double var5 = (double)(var2 * 15);
      double var7 = 0.01D;
      double var9 = 0.01D;
      double var11 = 0.2D;
      double var13 = 0.001D;
      double var15 = var11;
      double var17 = Math.pow(var13 / var11, 0.025D);

      for(int var19 = 0; var19 < 12; ++var19) {
         double var20 = var7 + (var9 - var7) * ((double)var19 / 40.0D);
         complexGaussianDist(var4, var5 * (double)(var19 + 1), var20, var15 * (0.5D + 3.0D * var1.nextDouble()));
         var15 *= var17;
      }

      SF2Sample var22 = newSimpleFFTSample(var0, "Organ", var4, var5);
      SF2Layer var23 = newLayer(var0, "Organ", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -6000);
      var21.putInteger(38, -1000);
      var21.putInteger(36, 4000);
      var21.putInteger(37, -100);
      var21.putInteger(8, 9500);
      return var23;
   }

   public static SF2Layer new_ch_organ(SF2Soundbank var0) {
      byte var1 = 1;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      double var6 = 0.01D;
      double var8 = 0.01D;
      double var10 = 0.2D;
      double var12 = 0.001D;
      double var14 = var10;
      double var16 = Math.pow(var12 / var10, 0.016666666666666666D);
      double[] var18 = new double[60];

      int var19;
      for(var19 = 0; var19 < var18.length; ++var19) {
         var14 *= var16;
         var18[var19] = var14;
      }

      var18[0] *= 5.0D;
      var18[1] *= 2.0D;
      var18[2] = 0.0D;
      var18[4] = 0.0D;
      var18[5] = 0.0D;
      var18[7] *= 7.0D;
      var18[9] = 0.0D;
      var18[10] = 0.0D;
      var18[12] = 0.0D;
      var18[15] *= 7.0D;
      var18[18] = 0.0D;
      var18[20] = 0.0D;
      var18[24] = 0.0D;
      var18[27] *= 5.0D;
      var18[29] = 0.0D;
      var18[30] = 0.0D;
      var18[33] = 0.0D;
      var18[36] *= 4.0D;
      var18[37] = 0.0D;
      var18[39] = 0.0D;
      var18[42] = 0.0D;
      var18[43] = 0.0D;
      var18[47] = 0.0D;
      var18[50] *= 4.0D;
      var18[52] = 0.0D;
      var18[55] = 0.0D;
      var18[57] = 0.0D;
      var18[10] *= 0.1D;
      var18[11] *= 0.1D;
      var18[12] *= 0.1D;
      var18[13] *= 0.1D;
      var18[17] *= 0.1D;
      var18[18] *= 0.1D;
      var18[19] *= 0.1D;
      var18[20] *= 0.1D;

      for(var19 = 0; var19 < 60; ++var19) {
         double var20 = var6 + (var8 - var6) * ((double)var19 / 40.0D);
         complexGaussianDist(var3, var4 * (double)(var19 + 1), var20, var18[var19]);
         var14 *= var16;
      }

      SF2Sample var22 = newSimpleFFTSample(var0, "Organ", var3, var4);
      SF2Layer var23 = newLayer(var0, "Organ", var22);
      SF2Region var21 = (SF2Region)var23.getRegions().get(0);
      var21.putInteger(54, 1);
      var21.putInteger(34, -10000);
      var21.putInteger(38, -1000);
      return var23;
   }

   public static SF2Layer new_flute(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      complexGaussianDist(var3, var4 * 1.0D, 0.001D, 0.5D);
      complexGaussianDist(var3, var4 * 2.0D, 0.001D, 0.5D);
      complexGaussianDist(var3, var4 * 3.0D, 0.001D, 0.5D);
      complexGaussianDist(var3, var4 * 4.0D, 0.01D, 0.5D);
      complexGaussianDist(var3, var4 * 4.0D, 100.0D, 120.0D);
      complexGaussianDist(var3, var4 * 6.0D, 100.0D, 40.0D);
      complexGaussianDist(var3, var4 * 8.0D, 100.0D, 80.0D);
      complexGaussianDist(var3, var4 * 5.0D, 0.001D, 0.05D);
      complexGaussianDist(var3, var4 * 6.0D, 0.001D, 0.06D);
      complexGaussianDist(var3, var4 * 7.0D, 0.001D, 0.04D);
      complexGaussianDist(var3, var4 * 8.0D, 0.005D, 0.06D);
      complexGaussianDist(var3, var4 * 9.0D, 0.005D, 0.06D);
      complexGaussianDist(var3, var4 * 10.0D, 0.01D, 0.1D);
      complexGaussianDist(var3, var4 * 11.0D, 0.08D, 0.7D);
      complexGaussianDist(var3, var4 * 12.0D, 0.08D, 0.6D);
      complexGaussianDist(var3, var4 * 13.0D, 0.08D, 0.6D);
      complexGaussianDist(var3, var4 * 14.0D, 0.08D, 0.6D);
      complexGaussianDist(var3, var4 * 15.0D, 0.08D, 0.5D);
      complexGaussianDist(var3, var4 * 16.0D, 0.08D, 0.5D);
      complexGaussianDist(var3, var4 * 17.0D, 0.08D, 0.2D);
      complexGaussianDist(var3, var4 * 1.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 2.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 3.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 4.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 5.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 6.0D, 20.0D, 9.0D);
      complexGaussianDist(var3, var4 * 7.0D, 20.0D, 9.0D);
      complexGaussianDist(var3, var4 * 8.0D, 20.0D, 9.0D);
      complexGaussianDist(var3, var4 * 9.0D, 20.0D, 8.0D);
      complexGaussianDist(var3, var4 * 10.0D, 30.0D, 8.0D);
      complexGaussianDist(var3, var4 * 11.0D, 30.0D, 9.0D);
      complexGaussianDist(var3, var4 * 12.0D, 30.0D, 9.0D);
      complexGaussianDist(var3, var4 * 13.0D, 30.0D, 8.0D);
      complexGaussianDist(var3, var4 * 14.0D, 30.0D, 8.0D);
      complexGaussianDist(var3, var4 * 15.0D, 30.0D, 7.0D);
      complexGaussianDist(var3, var4 * 16.0D, 30.0D, 7.0D);
      complexGaussianDist(var3, var4 * 17.0D, 30.0D, 6.0D);
      SF2Sample var6 = newSimpleFFTSample(var0, "Flute", var3, var4);
      SF2Layer var7 = newLayer(var0, "Flute", var6);
      SF2Region var8 = (SF2Region)var7.getRegions().get(0);
      var8.putInteger(54, 1);
      var8.putInteger(34, -6000);
      var8.putInteger(38, -1000);
      var8.putInteger(36, 4000);
      var8.putInteger(37, -100);
      var8.putInteger(8, 9500);
      return var7;
   }

   public static SF2Layer new_horn(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      double var6 = 0.5D;
      double var8 = 1.0E-11D;
      double var10 = var6;
      double var12 = Math.pow(var8 / var6, 0.025D);

      for(int var14 = 0; var14 < 40; ++var14) {
         if (var14 == 0) {
            complexGaussianDist(var3, var4 * (double)(var14 + 1), 0.1D, var10 * 0.2D);
         } else {
            complexGaussianDist(var3, var4 * (double)(var14 + 1), 0.1D, var10);
         }

         var10 *= var12;
      }

      complexGaussianDist(var3, var4 * 2.0D, 100.0D, 1.0D);
      SF2Sample var17 = newSimpleFFTSample(var0, "Horn", var3, var4);
      SF2Layer var15 = newLayer(var0, "Horn", var17);
      SF2Region var16 = (SF2Region)var15.getRegions().get(0);
      var16.putInteger(54, 1);
      var16.putInteger(34, -6000);
      var16.putInteger(38, -1000);
      var16.putInteger(36, 4000);
      var16.putInteger(37, -100);
      var16.putInteger(26, -500);
      var16.putInteger(30, 12000);
      var16.putInteger(11, 5000);
      var16.putInteger(8, 4500);
      return var15;
   }

   public static SF2Layer new_trumpet(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      double var6 = 0.5D;
      double var8 = 1.0E-5D;
      double var10 = var6;
      double var12 = Math.pow(var8 / var6, 0.0125D);
      double[] var14 = new double[80];

      int var15;
      for(var15 = 0; var15 < 80; ++var15) {
         var14[var15] = var10;
         var10 *= var12;
      }

      var14[0] *= 0.05D;
      var14[1] *= 0.2D;
      var14[2] *= 0.5D;
      var14[3] *= 0.85D;

      for(var15 = 0; var15 < 80; ++var15) {
         complexGaussianDist(var3, var4 * (double)(var15 + 1), 0.1D, var14[var15]);
      }

      complexGaussianDist(var3, var4 * 5.0D, 300.0D, 3.0D);
      SF2Sample var18 = newSimpleFFTSample(var0, "Trumpet", var3, var4);
      SF2Layer var16 = newLayer(var0, "Trumpet", var18);
      SF2Region var17 = (SF2Region)var16.getRegions().get(0);
      var17.putInteger(54, 1);
      var17.putInteger(34, -10000);
      var17.putInteger(38, 0);
      var17.putInteger(36, 4000);
      var17.putInteger(37, -100);
      var17.putInteger(26, -4000);
      var17.putInteger(30, -2500);
      var17.putInteger(11, 5000);
      var17.putInteger(8, 4500);
      var17.putInteger(9, 10);
      return var16;
   }

   public static SF2Layer new_brass_section(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      double var6 = 0.5D;
      double var8 = 0.005D;
      double var10 = var6;
      double var12 = Math.pow(var8 / var6, 0.03333333333333333D);
      double[] var14 = new double[30];

      for(int var15 = 0; var15 < 30; ++var15) {
         var14[var15] = var10;
         var10 *= var12;
      }

      var14[0] *= 0.8D;
      var14[1] *= 0.9D;
      double var20 = 5.0D;

      for(int var17 = 0; var17 < 30; ++var17) {
         complexGaussianDist(var3, var4 * (double)(var17 + 1), 0.1D * var20, var14[var17] * var20);
         var20 += 6.0D;
      }

      complexGaussianDist(var3, var4 * 6.0D, 300.0D, 2.0D);
      SF2Sample var21 = newSimpleFFTSample(var0, "Brass Section", var3, var4);
      SF2Layer var18 = newLayer(var0, "Brass Section", var21);
      SF2Region var19 = (SF2Region)var18.getRegions().get(0);
      var19.putInteger(54, 1);
      var19.putInteger(34, -9200);
      var19.putInteger(38, -1000);
      var19.putInteger(36, 4000);
      var19.putInteger(37, -100);
      var19.putInteger(26, -3000);
      var19.putInteger(30, 12000);
      var19.putInteger(11, 5000);
      var19.putInteger(8, 4500);
      return var18;
   }

   public static SF2Layer new_trombone(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      double var6 = 0.5D;
      double var8 = 0.001D;
      double var10 = var6;
      double var12 = Math.pow(var8 / var6, 0.0125D);
      double[] var14 = new double[80];

      int var15;
      for(var15 = 0; var15 < 80; ++var15) {
         var14[var15] = var10;
         var10 *= var12;
      }

      var14[0] *= 0.3D;
      var14[1] *= 0.7D;

      for(var15 = 0; var15 < 80; ++var15) {
         complexGaussianDist(var3, var4 * (double)(var15 + 1), 0.1D, var14[var15]);
      }

      complexGaussianDist(var3, var4 * 6.0D, 300.0D, 2.0D);
      SF2Sample var18 = newSimpleFFTSample(var0, "Trombone", var3, var4);
      SF2Layer var16 = newLayer(var0, "Trombone", var18);
      SF2Region var17 = (SF2Region)var16.getRegions().get(0);
      var17.putInteger(54, 1);
      var17.putInteger(34, -8000);
      var17.putInteger(38, -1000);
      var17.putInteger(36, 4000);
      var17.putInteger(37, -100);
      var17.putInteger(26, -2000);
      var17.putInteger(30, 12000);
      var17.putInteger(11, 5000);
      var17.putInteger(8, 4500);
      var17.putInteger(9, 10);
      return var16;
   }

   public static SF2Layer new_sax(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      double var6 = 0.5D;
      double var8 = 0.01D;
      double var10 = var6;
      double var12 = Math.pow(var8 / var6, 0.025D);

      for(int var14 = 0; var14 < 40; ++var14) {
         if (var14 != 0 && var14 != 2) {
            complexGaussianDist(var3, var4 * (double)(var14 + 1), 0.1D, var10);
         } else {
            complexGaussianDist(var3, var4 * (double)(var14 + 1), 0.1D, var10 * 4.0D);
         }

         var10 *= var12;
      }

      complexGaussianDist(var3, var4 * 4.0D, 200.0D, 1.0D);
      SF2Sample var17 = newSimpleFFTSample(var0, "Sax", var3, var4);
      SF2Layer var15 = newLayer(var0, "Sax", var17);
      SF2Region var16 = (SF2Region)var15.getRegions().get(0);
      var16.putInteger(54, 1);
      var16.putInteger(34, -6000);
      var16.putInteger(38, -1000);
      var16.putInteger(36, 4000);
      var16.putInteger(37, -100);
      var16.putInteger(26, -3000);
      var16.putInteger(30, 12000);
      var16.putInteger(11, 5000);
      var16.putInteger(8, 4500);
      return var15;
   }

   public static SF2Layer new_oboe(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      complexGaussianDist(var3, var4 * 5.0D, 100.0D, 80.0D);
      complexGaussianDist(var3, var4 * 1.0D, 0.01D, 0.53D);
      complexGaussianDist(var3, var4 * 2.0D, 0.01D, 0.51D);
      complexGaussianDist(var3, var4 * 3.0D, 0.01D, 0.48D);
      complexGaussianDist(var3, var4 * 4.0D, 0.01D, 0.49D);
      complexGaussianDist(var3, var4 * 5.0D, 0.01D, 5.0D);
      complexGaussianDist(var3, var4 * 6.0D, 0.01D, 0.51D);
      complexGaussianDist(var3, var4 * 7.0D, 0.01D, 0.5D);
      complexGaussianDist(var3, var4 * 8.0D, 0.01D, 0.59D);
      complexGaussianDist(var3, var4 * 9.0D, 0.01D, 0.61D);
      complexGaussianDist(var3, var4 * 10.0D, 0.01D, 0.52D);
      complexGaussianDist(var3, var4 * 11.0D, 0.01D, 0.49D);
      complexGaussianDist(var3, var4 * 12.0D, 0.01D, 0.51D);
      complexGaussianDist(var3, var4 * 13.0D, 0.01D, 0.48D);
      complexGaussianDist(var3, var4 * 14.0D, 0.01D, 0.51D);
      complexGaussianDist(var3, var4 * 15.0D, 0.01D, 0.46D);
      complexGaussianDist(var3, var4 * 16.0D, 0.01D, 0.35D);
      complexGaussianDist(var3, var4 * 17.0D, 0.01D, 0.2D);
      complexGaussianDist(var3, var4 * 18.0D, 0.01D, 0.1D);
      complexGaussianDist(var3, var4 * 19.0D, 0.01D, 0.5D);
      complexGaussianDist(var3, var4 * 20.0D, 0.01D, 0.1D);
      SF2Sample var6 = newSimpleFFTSample(var0, "Oboe", var3, var4);
      SF2Layer var7 = newLayer(var0, "Oboe", var6);
      SF2Region var8 = (SF2Region)var7.getRegions().get(0);
      var8.putInteger(54, 1);
      var8.putInteger(34, -6000);
      var8.putInteger(38, -1000);
      var8.putInteger(36, 4000);
      var8.putInteger(37, -100);
      var8.putInteger(8, 9500);
      return var7;
   }

   public static SF2Layer new_bassoon(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      complexGaussianDist(var3, var4 * 2.0D, 100.0D, 40.0D);
      complexGaussianDist(var3, var4 * 4.0D, 100.0D, 20.0D);
      complexGaussianDist(var3, var4 * 1.0D, 0.01D, 0.53D);
      complexGaussianDist(var3, var4 * 2.0D, 0.01D, 5.0D);
      complexGaussianDist(var3, var4 * 3.0D, 0.01D, 0.51D);
      complexGaussianDist(var3, var4 * 4.0D, 0.01D, 0.48D);
      complexGaussianDist(var3, var4 * 5.0D, 0.01D, 1.49D);
      complexGaussianDist(var3, var4 * 6.0D, 0.01D, 0.51D);
      complexGaussianDist(var3, var4 * 7.0D, 0.01D, 0.5D);
      complexGaussianDist(var3, var4 * 8.0D, 0.01D, 0.59D);
      complexGaussianDist(var3, var4 * 9.0D, 0.01D, 0.61D);
      complexGaussianDist(var3, var4 * 10.0D, 0.01D, 0.52D);
      complexGaussianDist(var3, var4 * 11.0D, 0.01D, 0.49D);
      complexGaussianDist(var3, var4 * 12.0D, 0.01D, 0.51D);
      complexGaussianDist(var3, var4 * 13.0D, 0.01D, 0.48D);
      complexGaussianDist(var3, var4 * 14.0D, 0.01D, 0.51D);
      complexGaussianDist(var3, var4 * 15.0D, 0.01D, 0.46D);
      complexGaussianDist(var3, var4 * 16.0D, 0.01D, 0.35D);
      complexGaussianDist(var3, var4 * 17.0D, 0.01D, 0.2D);
      complexGaussianDist(var3, var4 * 18.0D, 0.01D, 0.1D);
      complexGaussianDist(var3, var4 * 19.0D, 0.01D, 0.5D);
      complexGaussianDist(var3, var4 * 20.0D, 0.01D, 0.1D);
      SF2Sample var6 = newSimpleFFTSample(var0, "Flute", var3, var4);
      SF2Layer var7 = newLayer(var0, "Flute", var6);
      SF2Region var8 = (SF2Region)var7.getRegions().get(0);
      var8.putInteger(54, 1);
      var8.putInteger(34, -6000);
      var8.putInteger(38, -1000);
      var8.putInteger(36, 4000);
      var8.putInteger(37, -100);
      var8.putInteger(8, 9500);
      return var7;
   }

   public static SF2Layer new_clarinet(SF2Soundbank var0) {
      byte var1 = 8;
      int var2 = 4096 * var1;
      double[] var3 = new double[var2 * 2];
      double var4 = (double)(var1 * 15);
      complexGaussianDist(var3, var4 * 1.0D, 0.001D, 0.5D);
      complexGaussianDist(var3, var4 * 2.0D, 0.001D, 0.02D);
      complexGaussianDist(var3, var4 * 3.0D, 0.001D, 0.2D);
      complexGaussianDist(var3, var4 * 4.0D, 0.01D, 0.1D);
      complexGaussianDist(var3, var4 * 4.0D, 100.0D, 60.0D);
      complexGaussianDist(var3, var4 * 6.0D, 100.0D, 20.0D);
      complexGaussianDist(var3, var4 * 8.0D, 100.0D, 20.0D);
      complexGaussianDist(var3, var4 * 5.0D, 0.001D, 0.1D);
      complexGaussianDist(var3, var4 * 6.0D, 0.001D, 0.09D);
      complexGaussianDist(var3, var4 * 7.0D, 0.001D, 0.02D);
      complexGaussianDist(var3, var4 * 8.0D, 0.005D, 0.16D);
      complexGaussianDist(var3, var4 * 9.0D, 0.005D, 0.96D);
      complexGaussianDist(var3, var4 * 10.0D, 0.01D, 0.9D);
      complexGaussianDist(var3, var4 * 11.0D, 0.08D, 1.2D);
      complexGaussianDist(var3, var4 * 12.0D, 0.08D, 1.8D);
      complexGaussianDist(var3, var4 * 13.0D, 0.08D, 1.6D);
      complexGaussianDist(var3, var4 * 14.0D, 0.08D, 1.2D);
      complexGaussianDist(var3, var4 * 15.0D, 0.08D, 0.9D);
      complexGaussianDist(var3, var4 * 16.0D, 0.08D, 0.5D);
      complexGaussianDist(var3, var4 * 17.0D, 0.08D, 0.2D);
      complexGaussianDist(var3, var4 * 1.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 2.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 3.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 4.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 5.0D, 10.0D, 8.0D);
      complexGaussianDist(var3, var4 * 6.0D, 20.0D, 9.0D);
      complexGaussianDist(var3, var4 * 7.0D, 20.0D, 9.0D);
      complexGaussianDist(var3, var4 * 8.0D, 20.0D, 9.0D);
      complexGaussianDist(var3, var4 * 9.0D, 20.0D, 8.0D);
      complexGaussianDist(var3, var4 * 10.0D, 30.0D, 8.0D);
      complexGaussianDist(var3, var4 * 11.0D, 30.0D, 9.0D);
      complexGaussianDist(var3, var4 * 12.0D, 30.0D, 9.0D);
      complexGaussianDist(var3, var4 * 13.0D, 30.0D, 8.0D);
      complexGaussianDist(var3, var4 * 14.0D, 30.0D, 8.0D);
      complexGaussianDist(var3, var4 * 15.0D, 30.0D, 7.0D);
      complexGaussianDist(var3, var4 * 16.0D, 30.0D, 7.0D);
      complexGaussianDist(var3, var4 * 17.0D, 30.0D, 6.0D);
      SF2Sample var6 = newSimpleFFTSample(var0, "Clarinet", var3, var4);
      SF2Layer var7 = newLayer(var0, "Clarinet", var6);
      SF2Region var8 = (SF2Region)var7.getRegions().get(0);
      var8.putInteger(54, 1);
      var8.putInteger(34, -6000);
      var8.putInteger(38, -1000);
      var8.putInteger(36, 4000);
      var8.putInteger(37, -100);
      var8.putInteger(8, 9500);
      return var7;
   }

   public static SF2Layer new_timpani(SF2Soundbank var0) {
      char var3 = '耀';
      double[] var4 = new double[2 * var3];
      double var5 = 48.0D;
      complexGaussianDist(var4, var5 * 2.0D, 0.2D, 1.0D);
      complexGaussianDist(var4, var5 * 3.0D, 0.2D, 0.7D);
      complexGaussianDist(var4, var5 * 5.0D, 10.0D, 1.0D);
      complexGaussianDist(var4, var5 * 6.0D, 9.0D, 1.0D);
      complexGaussianDist(var4, var5 * 8.0D, 15.0D, 1.0D);
      complexGaussianDist(var4, var5 * 9.0D, 18.0D, 0.8D);
      complexGaussianDist(var4, var5 * 11.0D, 21.0D, 0.5D);
      complexGaussianDist(var4, var5 * 13.0D, 28.0D, 0.3D);
      complexGaussianDist(var4, var5 * 14.0D, 22.0D, 0.1D);
      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.5D);
      var4 = realPart(var4);
      double var7 = (double)var4.length;

      for(int var9 = 0; var9 < var4.length; ++var9) {
         double var10 = 1.0D - (double)var9 / var7;
         var4[var9] *= var10 * var10;
      }

      fadeUp((double[])var4, 40);
      double[] var1 = var4;
      short var12 = 16384;
      var4 = new double[2 * var12];
      Random var15 = new Random(3049912L);

      int var6;
      for(var6 = 0; var6 < var4.length; var6 += 2) {
         var4[var6] = 2.0D * (var15.nextDouble() - 0.5D) * 0.1D;
      }

      fft(var4);

      for(var6 = var12 / 2; var6 < var4.length; ++var6) {
         var4[var6] = 0.0D;
      }

      for(var6 = 4096; var6 < 8192; ++var6) {
         var4[var6] = 1.0D - (double)(var6 - 4096) / 4096.0D;
      }

      for(var6 = 0; var6 < 300; ++var6) {
         var7 = 1.0D - (double)var6 / 300.0D;
         var4[var6] *= 1.0D + 20.0D * var7 * var7;
      }

      for(var6 = 0; var6 < 24; ++var6) {
         var4[var6] = 0.0D;
      }

      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.9D);
      var4 = realPart(var4);
      double var18 = 1.0D;

      for(int var8 = 0; var8 < var4.length; ++var8) {
         var4[var8] *= var18;
         var18 *= 0.9998D;
      }

      double[] var2 = var4;

      for(int var13 = 0; var13 < var2.length; ++var13) {
         var1[var13] += var2[var13] * 0.02D;
      }

      normalize(var1, 0.9D);
      SF2Sample var14 = newSimpleDrumSample(var0, "Timpani", var1);
      SF2Layer var16 = new SF2Layer(var0);
      var16.setName("Timpani");
      SF2GlobalRegion var17 = new SF2GlobalRegion();
      var16.setGlobalZone(var17);
      var0.addResource(var16);
      SF2LayerRegion var19 = new SF2LayerRegion();
      var19.putInteger(38, 12000);
      var19.putInteger(48, -100);
      var19.setSample(var14);
      var16.getRegions().add(var19);
      return var16;
   }

   public static SF2Layer new_melodic_toms(SF2Soundbank var0) {
      short var3 = 16384;
      double[] var4 = new double[2 * var3];
      complexGaussianDist(var4, 30.0D, 0.5D, 1.0D);
      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.8D);
      var4 = realPart(var4);
      double var5 = (double)var4.length;

      for(int var7 = 0; var7 < var4.length; ++var7) {
         var4[var7] *= 1.0D - (double)var7 / var5;
      }

      double[] var1 = var4;
      var3 = 16384;
      var4 = new double[2 * var3];
      Random var11 = new Random(3049912L);

      int var6;
      for(var6 = 0; var6 < var4.length; var6 += 2) {
         var4[var6] = 2.0D * (var11.nextDouble() - 0.5D) * 0.1D;
      }

      fft(var4);

      for(var6 = var3 / 2; var6 < var4.length; ++var6) {
         var4[var6] = 0.0D;
      }

      for(var6 = 4096; var6 < 8192; ++var6) {
         var4[var6] = 1.0D - (double)(var6 - 4096) / 4096.0D;
      }

      for(var6 = 0; var6 < 200; ++var6) {
         double var14 = 1.0D - (double)var6 / 200.0D;
         var4[var6] *= 1.0D + 20.0D * var14 * var14;
      }

      for(var6 = 0; var6 < 30; ++var6) {
         var4[var6] = 0.0D;
      }

      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.9D);
      var4 = realPart(var4);
      double var15 = 1.0D;

      for(int var8 = 0; var8 < var4.length; ++var8) {
         var4[var8] *= var15;
         var15 *= 0.9996D;
      }

      double[] var2 = var4;

      int var9;
      for(var9 = 0; var9 < var2.length; ++var9) {
         var1[var9] += var2[var9] * 0.5D;
      }

      for(var9 = 0; var9 < 5; ++var9) {
         var1[var9] *= (double)var9 / 5.0D;
      }

      normalize(var1, 0.99D);
      SF2Sample var10 = newSimpleDrumSample(var0, "Melodic Toms", var1);
      var10.setOriginalPitch(63);
      SF2Layer var12 = new SF2Layer(var0);
      var12.setName("Melodic Toms");
      SF2GlobalRegion var13 = new SF2GlobalRegion();
      var12.setGlobalZone(var13);
      var0.addResource(var12);
      SF2LayerRegion var16 = new SF2LayerRegion();
      var16.putInteger(38, 12000);
      var16.putInteger(48, -100);
      var16.setSample(var10);
      var12.getRegions().add(var16);
      return var12;
   }

   public static SF2Layer new_reverse_cymbal(SF2Soundbank var0) {
      short var2 = 16384;
      double[] var3 = new double[2 * var2];
      Random var4 = new Random(3049912L);

      int var5;
      for(var5 = 0; var5 < var3.length; var5 += 2) {
         var3[var5] = 2.0D * (var4.nextDouble() - 0.5D);
      }

      for(var5 = var2 / 2; var5 < var3.length; ++var5) {
         var3[var5] = 0.0D;
      }

      for(var5 = 0; var5 < 100; ++var5) {
         var3[var5] = 0.0D;
      }

      for(var5 = 0; var5 < 1024; ++var5) {
         double var6 = (double)var5 / 1024.0D;
         var3[var5] = 1.0D - var6;
      }

      SF2Sample var8 = newSimpleFFTSample(var0, "Reverse Cymbal", var3, 100.0D, 20);
      SF2Layer var9 = new SF2Layer(var0);
      var9.setName("Reverse Cymbal");
      SF2GlobalRegion var10 = new SF2GlobalRegion();
      var9.setGlobalZone(var10);
      var0.addResource(var9);
      SF2LayerRegion var11 = new SF2LayerRegion();
      var11.putInteger(34, -200);
      var11.putInteger(36, -12000);
      var11.putInteger(54, 1);
      var11.putInteger(38, -1000);
      var11.putInteger(37, 1000);
      var11.setSample(var8);
      var9.getRegions().add(var11);
      return var9;
   }

   public static SF2Layer new_snare_drum(SF2Soundbank var0) {
      short var3 = 16384;
      double[] var4 = new double[2 * var3];
      complexGaussianDist(var4, 24.0D, 0.5D, 1.0D);
      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.5D);
      var4 = realPart(var4);
      double var5 = (double)var4.length;

      for(int var7 = 0; var7 < var4.length; ++var7) {
         var4[var7] *= 1.0D - (double)var7 / var5;
      }

      double[] var1 = var4;
      var3 = 16384;
      var4 = new double[2 * var3];
      Random var11 = new Random(3049912L);

      int var6;
      for(var6 = 0; var6 < var4.length; var6 += 2) {
         var4[var6] = 2.0D * (var11.nextDouble() - 0.5D) * 0.1D;
      }

      fft(var4);

      for(var6 = var3 / 2; var6 < var4.length; ++var6) {
         var4[var6] = 0.0D;
      }

      for(var6 = 4096; var6 < 8192; ++var6) {
         var4[var6] = 1.0D - (double)(var6 - 4096) / 4096.0D;
      }

      for(var6 = 0; var6 < 300; ++var6) {
         double var14 = 1.0D - (double)var6 / 300.0D;
         var4[var6] *= 1.0D + 20.0D * var14 * var14;
      }

      for(var6 = 0; var6 < 24; ++var6) {
         var4[var6] = 0.0D;
      }

      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.9D);
      var4 = realPart(var4);
      double var15 = 1.0D;

      for(int var8 = 0; var8 < var4.length; ++var8) {
         var4[var8] *= var15;
         var15 *= 0.9998D;
      }

      double[] var2 = var4;

      int var9;
      for(var9 = 0; var9 < var2.length; ++var9) {
         var1[var9] += var2[var9];
      }

      for(var9 = 0; var9 < 5; ++var9) {
         var1[var9] *= (double)var9 / 5.0D;
      }

      SF2Sample var10 = newSimpleDrumSample(var0, "Snare Drum", var1);
      SF2Layer var12 = new SF2Layer(var0);
      var12.setName("Snare Drum");
      SF2GlobalRegion var13 = new SF2GlobalRegion();
      var12.setGlobalZone(var13);
      var0.addResource(var12);
      SF2LayerRegion var16 = new SF2LayerRegion();
      var16.putInteger(38, 12000);
      var16.putInteger(56, 0);
      var16.putInteger(48, -100);
      var16.setSample(var10);
      var12.getRegions().add(var16);
      return var12;
   }

   public static SF2Layer new_bass_drum(SF2Soundbank var0) {
      short var3 = 16384;
      double[] var4 = new double[2 * var3];
      complexGaussianDist(var4, 10.0D, 2.0D, 1.0D);
      complexGaussianDist(var4, 17.2D, 2.0D, 1.0D);
      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.9D);
      var4 = realPart(var4);
      double var5 = (double)var4.length;

      for(int var7 = 0; var7 < var4.length; ++var7) {
         var4[var7] *= 1.0D - (double)var7 / var5;
      }

      double[] var1 = var4;
      var3 = 4096;
      var4 = new double[2 * var3];
      Random var11 = new Random(3049912L);

      int var6;
      for(var6 = 0; var6 < var4.length; var6 += 2) {
         var4[var6] = 2.0D * (var11.nextDouble() - 0.5D) * 0.1D;
      }

      fft(var4);

      for(var6 = var3 / 2; var6 < var4.length; ++var6) {
         var4[var6] = 0.0D;
      }

      for(var6 = 1024; var6 < 2048; ++var6) {
         var4[var6] = 1.0D - (double)(var6 - 1024) / 1024.0D;
      }

      for(var6 = 0; var6 < 512; ++var6) {
         var4[var6] = (double)(10 * var6) / 512.0D;
      }

      for(var6 = 0; var6 < 10; ++var6) {
         var4[var6] = 0.0D;
      }

      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.9D);
      var4 = realPart(var4);
      double var14 = 1.0D;

      for(int var8 = 0; var8 < var4.length; ++var8) {
         var4[var8] *= var14;
         var14 *= 0.999D;
      }

      double[] var2 = var4;

      int var9;
      for(var9 = 0; var9 < var2.length; ++var9) {
         var1[var9] += var2[var9] * 0.5D;
      }

      for(var9 = 0; var9 < 5; ++var9) {
         var1[var9] *= (double)var9 / 5.0D;
      }

      SF2Sample var10 = newSimpleDrumSample(var0, "Bass Drum", var1);
      SF2Layer var12 = new SF2Layer(var0);
      var12.setName("Bass Drum");
      SF2GlobalRegion var13 = new SF2GlobalRegion();
      var12.setGlobalZone(var13);
      var0.addResource(var12);
      SF2LayerRegion var15 = new SF2LayerRegion();
      var15.putInteger(38, 12000);
      var15.putInteger(56, 0);
      var15.putInteger(48, -100);
      var15.setSample(var10);
      var12.getRegions().add(var15);
      return var12;
   }

   public static SF2Layer new_tom(SF2Soundbank var0) {
      short var3 = 16384;
      double[] var4 = new double[2 * var3];
      complexGaussianDist(var4, 30.0D, 0.5D, 1.0D);
      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.8D);
      var4 = realPart(var4);
      double var5 = (double)var4.length;

      for(int var7 = 0; var7 < var4.length; ++var7) {
         var4[var7] *= 1.0D - (double)var7 / var5;
      }

      double[] var1 = var4;
      var3 = 16384;
      var4 = new double[2 * var3];
      Random var11 = new Random(3049912L);

      int var6;
      for(var6 = 0; var6 < var4.length; var6 += 2) {
         var4[var6] = 2.0D * (var11.nextDouble() - 0.5D) * 0.1D;
      }

      fft(var4);

      for(var6 = var3 / 2; var6 < var4.length; ++var6) {
         var4[var6] = 0.0D;
      }

      for(var6 = 4096; var6 < 8192; ++var6) {
         var4[var6] = 1.0D - (double)(var6 - 4096) / 4096.0D;
      }

      for(var6 = 0; var6 < 200; ++var6) {
         double var14 = 1.0D - (double)var6 / 200.0D;
         var4[var6] *= 1.0D + 20.0D * var14 * var14;
      }

      for(var6 = 0; var6 < 30; ++var6) {
         var4[var6] = 0.0D;
      }

      randomPhase(var4, new Random(3049912L));
      ifft(var4);
      normalize(var4, 0.9D);
      var4 = realPart(var4);
      double var15 = 1.0D;

      for(int var8 = 0; var8 < var4.length; ++var8) {
         var4[var8] *= var15;
         var15 *= 0.9996D;
      }

      double[] var2 = var4;

      int var9;
      for(var9 = 0; var9 < var2.length; ++var9) {
         var1[var9] += var2[var9] * 0.5D;
      }

      for(var9 = 0; var9 < 5; ++var9) {
         var1[var9] *= (double)var9 / 5.0D;
      }

      normalize(var1, 0.99D);
      SF2Sample var10 = newSimpleDrumSample(var0, "Tom", var1);
      var10.setOriginalPitch(50);
      SF2Layer var12 = new SF2Layer(var0);
      var12.setName("Tom");
      SF2GlobalRegion var13 = new SF2GlobalRegion();
      var12.setGlobalZone(var13);
      var0.addResource(var12);
      SF2LayerRegion var16 = new SF2LayerRegion();
      var16.putInteger(38, 12000);
      var16.putInteger(48, -100);
      var16.setSample(var10);
      var12.getRegions().add(var16);
      return var12;
   }

   public static SF2Layer new_closed_hihat(SF2Soundbank var0) {
      short var2 = 16384;
      double[] var3 = new double[2 * var2];
      Random var4 = new Random(3049912L);

      int var5;
      for(var5 = 0; var5 < var3.length; var5 += 2) {
         var3[var5] = 2.0D * (var4.nextDouble() - 0.5D) * 0.1D;
      }

      fft(var3);

      for(var5 = var2 / 2; var5 < var3.length; ++var5) {
         var3[var5] = 0.0D;
      }

      for(var5 = 4096; var5 < 8192; ++var5) {
         var3[var5] = 1.0D - (double)(var5 - 4096) / 4096.0D;
      }

      for(var5 = 0; var5 < 2048; ++var5) {
         var3[var5] = 0.2D + 0.8D * ((double)var5 / 2048.0D);
      }

      randomPhase(var3, new Random(3049912L));
      ifft(var3);
      normalize(var3, 0.9D);
      var3 = realPart(var3);
      double var12 = 1.0D;

      for(int var7 = 0; var7 < var3.length; ++var7) {
         var3[var7] *= var12;
         var12 *= 0.9996D;
      }

      double[] var1 = var3;

      for(int var8 = 0; var8 < 5; ++var8) {
         var1[var8] *= (double)var8 / 5.0D;
      }

      SF2Sample var9 = newSimpleDrumSample(var0, "Closed Hi-Hat", var1);
      SF2Layer var10 = new SF2Layer(var0);
      var10.setName("Closed Hi-Hat");
      SF2GlobalRegion var11 = new SF2GlobalRegion();
      var10.setGlobalZone(var11);
      var0.addResource(var10);
      SF2LayerRegion var13 = new SF2LayerRegion();
      var13.putInteger(38, 12000);
      var13.putInteger(56, 0);
      var13.putInteger(57, 1);
      var13.setSample(var9);
      var10.getRegions().add(var13);
      return var10;
   }

   public static SF2Layer new_open_hihat(SF2Soundbank var0) {
      short var2 = 16384;
      double[] var3 = new double[2 * var2];
      Random var4 = new Random(3049912L);

      int var5;
      for(var5 = 0; var5 < var3.length; var5 += 2) {
         var3[var5] = 2.0D * (var4.nextDouble() - 0.5D);
      }

      for(var5 = var2 / 2; var5 < var3.length; ++var5) {
         var3[var5] = 0.0D;
      }

      for(var5 = 0; var5 < 200; ++var5) {
         var3[var5] = 0.0D;
      }

      for(var5 = 0; var5 < 8192; ++var5) {
         double var6 = (double)var5 / 8192.0D;
         var3[var5] = var6;
      }

      SF2Sample var8 = newSimpleFFTSample(var0, "Open Hi-Hat", var3, 1000.0D, 5);
      SF2Layer var9 = new SF2Layer(var0);
      var9.setName("Open Hi-Hat");
      SF2GlobalRegion var10 = new SF2GlobalRegion();
      var9.setGlobalZone(var10);
      var0.addResource(var9);
      SF2LayerRegion var11 = new SF2LayerRegion();
      var11.putInteger(36, 1500);
      var11.putInteger(54, 1);
      var11.putInteger(38, 1500);
      var11.putInteger(37, 1000);
      var11.putInteger(56, 0);
      var11.putInteger(57, 1);
      var11.setSample(var8);
      var9.getRegions().add(var11);
      return var9;
   }

   public static SF2Layer new_crash_cymbal(SF2Soundbank var0) {
      short var2 = 16384;
      double[] var3 = new double[2 * var2];
      Random var4 = new Random(3049912L);

      int var5;
      for(var5 = 0; var5 < var3.length; var5 += 2) {
         var3[var5] = 2.0D * (var4.nextDouble() - 0.5D);
      }

      for(var5 = var2 / 2; var5 < var3.length; ++var5) {
         var3[var5] = 0.0D;
      }

      for(var5 = 0; var5 < 100; ++var5) {
         var3[var5] = 0.0D;
      }

      for(var5 = 0; var5 < 1024; ++var5) {
         double var6 = (double)var5 / 1024.0D;
         var3[var5] = var6;
      }

      SF2Sample var8 = newSimpleFFTSample(var0, "Crash Cymbal", var3, 1000.0D, 5);
      SF2Layer var9 = new SF2Layer(var0);
      var9.setName("Crash Cymbal");
      SF2GlobalRegion var10 = new SF2GlobalRegion();
      var9.setGlobalZone(var10);
      var0.addResource(var9);
      SF2LayerRegion var11 = new SF2LayerRegion();
      var11.putInteger(36, 1800);
      var11.putInteger(54, 1);
      var11.putInteger(38, 1800);
      var11.putInteger(37, 1000);
      var11.putInteger(56, 0);
      var11.setSample(var8);
      var9.getRegions().add(var11);
      return var9;
   }

   public static SF2Layer new_side_stick(SF2Soundbank var0) {
      short var2 = 16384;
      double[] var3 = new double[2 * var2];
      Random var4 = new Random(3049912L);

      int var5;
      for(var5 = 0; var5 < var3.length; var5 += 2) {
         var3[var5] = 2.0D * (var4.nextDouble() - 0.5D) * 0.1D;
      }

      fft(var3);

      for(var5 = var2 / 2; var5 < var3.length; ++var5) {
         var3[var5] = 0.0D;
      }

      for(var5 = 4096; var5 < 8192; ++var5) {
         var3[var5] = 1.0D - (double)(var5 - 4096) / 4096.0D;
      }

      for(var5 = 0; var5 < 200; ++var5) {
         double var6 = 1.0D - (double)var5 / 200.0D;
         var3[var5] *= 1.0D + 20.0D * var6 * var6;
      }

      for(var5 = 0; var5 < 30; ++var5) {
         var3[var5] = 0.0D;
      }

      randomPhase(var3, new Random(3049912L));
      ifft(var3);
      normalize(var3, 0.9D);
      var3 = realPart(var3);
      double var12 = 1.0D;

      for(int var7 = 0; var7 < var3.length; ++var7) {
         var3[var7] *= var12;
         var12 *= 0.9996D;
      }

      double[] var1 = var3;

      for(int var8 = 0; var8 < 10; ++var8) {
         var1[var8] *= (double)var8 / 10.0D;
      }

      SF2Sample var9 = newSimpleDrumSample(var0, "Side Stick", var1);
      SF2Layer var10 = new SF2Layer(var0);
      var10.setName("Side Stick");
      SF2GlobalRegion var11 = new SF2GlobalRegion();
      var10.setGlobalZone(var11);
      var0.addResource(var10);
      SF2LayerRegion var13 = new SF2LayerRegion();
      var13.putInteger(38, 12000);
      var13.putInteger(56, 0);
      var13.putInteger(48, -50);
      var13.setSample(var9);
      var10.getRegions().add(var13);
      return var10;
   }

   public static SF2Sample newSimpleFFTSample(SF2Soundbank var0, String var1, double[] var2, double var3) {
      return newSimpleFFTSample(var0, var1, var2, var3, 10);
   }

   public static SF2Sample newSimpleFFTSample(SF2Soundbank var0, String var1, double[] var2, double var3, int var5) {
      int var6 = var2.length / 2;
      AudioFormat var7 = new AudioFormat(44100.0F, 16, 1, true, false);
      double var8 = var3 / (double)var6 * (double)var7.getSampleRate() * 0.5D;
      randomPhase(var2);
      ifft(var2);
      var2 = realPart(var2);
      normalize(var2, 0.9D);
      float[] var10 = toFloat(var2);
      var10 = loopExtend(var10, var10.length + 512);
      fadeUp(var10, var5);
      byte[] var11 = toBytes(var10, var7);
      SF2Sample var12 = new SF2Sample(var0);
      var12.setName(var1);
      var12.setData(var11);
      var12.setStartLoop(256L);
      var12.setEndLoop((long)(var6 + 256));
      var12.setSampleRate((long)var7.getSampleRate());
      double var13 = 81.0D + 12.0D * Math.log(var8 / 440.0D) / Math.log(2.0D);
      var12.setOriginalPitch((int)var13);
      var12.setPitchCorrection((byte)((int)(-(var13 - (double)((int)var13)) * 100.0D)));
      var0.addResource(var12);
      return var12;
   }

   public static SF2Sample newSimpleFFTSample_dist(SF2Soundbank var0, String var1, double[] var2, double var3, double var5) {
      int var7 = var2.length / 2;
      AudioFormat var8 = new AudioFormat(44100.0F, 16, 1, true, false);
      double var9 = var3 / (double)var7 * (double)var8.getSampleRate() * 0.5D;
      randomPhase(var2);
      ifft(var2);
      var2 = realPart(var2);

      for(int var11 = 0; var11 < var2.length; ++var11) {
         var2[var11] = (1.0D - Math.exp(-Math.abs(var2[var11] * var5))) * Math.signum(var2[var11]);
      }

      normalize(var2, 0.9D);
      float[] var16 = toFloat(var2);
      var16 = loopExtend(var16, var16.length + 512);
      fadeUp((float[])var16, 80);
      byte[] var12 = toBytes(var16, var8);
      SF2Sample var13 = new SF2Sample(var0);
      var13.setName(var1);
      var13.setData(var12);
      var13.setStartLoop(256L);
      var13.setEndLoop((long)(var7 + 256));
      var13.setSampleRate((long)var8.getSampleRate());
      double var14 = 81.0D + 12.0D * Math.log(var9 / 440.0D) / Math.log(2.0D);
      var13.setOriginalPitch((int)var14);
      var13.setPitchCorrection((byte)((int)(-(var14 - (double)((int)var14)) * 100.0D)));
      var0.addResource(var13);
      return var13;
   }

   public static SF2Sample newSimpleDrumSample(SF2Soundbank var0, String var1, double[] var2) {
      int var3 = var2.length;
      AudioFormat var4 = new AudioFormat(44100.0F, 16, 1, true, false);
      byte[] var5 = toBytes(toFloat(realPart(var2)), var4);
      SF2Sample var6 = new SF2Sample(var0);
      var6.setName(var1);
      var6.setData(var5);
      var6.setStartLoop(256L);
      var6.setEndLoop((long)(var3 + 256));
      var6.setSampleRate((long)var4.getSampleRate());
      var6.setOriginalPitch(60);
      var0.addResource(var6);
      return var6;
   }

   public static SF2Layer newLayer(SF2Soundbank var0, String var1, SF2Sample var2) {
      SF2LayerRegion var3 = new SF2LayerRegion();
      var3.setSample(var2);
      SF2Layer var4 = new SF2Layer(var0);
      var4.setName(var1);
      var4.getRegions().add(var3);
      var0.addResource(var4);
      return var4;
   }

   public static SF2Instrument newInstrument(SF2Soundbank var0, String var1, Patch var2, SF2Layer... var3) {
      SF2Instrument var4 = new SF2Instrument(var0);
      var4.setPatch(var2);
      var4.setName(var1);
      var0.addInstrument(var4);

      for(int var5 = 0; var5 < var3.length; ++var5) {
         SF2InstrumentRegion var6 = new SF2InstrumentRegion();
         var6.setLayer(var3[var5]);
         var4.getRegions().add(var6);
      }

      return var4;
   }

   public static void ifft(double[] var0) {
      (new FFT(var0.length / 2, 1)).transform(var0);
   }

   public static void fft(double[] var0) {
      (new FFT(var0.length / 2, -1)).transform(var0);
   }

   public static void complexGaussianDist(double[] var0, double var1, double var3, double var5) {
      for(int var7 = 0; var7 < var0.length / 4; ++var7) {
         var0[var7 * 2] += var5 * 1.0D / (var3 * Math.sqrt(6.283185307179586D)) * Math.exp(-0.5D * Math.pow(((double)var7 - var1) / var3, 2.0D));
      }

   }

   public static void randomPhase(double[] var0) {
      for(int var1 = 0; var1 < var0.length; var1 += 2) {
         double var2 = Math.random() * 2.0D * 3.141592653589793D;
         double var4 = var0[var1];
         var0[var1] = Math.sin(var2) * var4;
         var0[var1 + 1] = Math.cos(var2) * var4;
      }

   }

   public static void randomPhase(double[] var0, Random var1) {
      for(int var2 = 0; var2 < var0.length; var2 += 2) {
         double var3 = var1.nextDouble() * 2.0D * 3.141592653589793D;
         double var5 = var0[var2];
         var0[var2] = Math.sin(var3) * var5;
         var0[var2 + 1] = Math.cos(var3) * var5;
      }

   }

   public static void normalize(double[] var0, double var1) {
      double var3 = 0.0D;

      for(int var5 = 0; var5 < var0.length; ++var5) {
         if (var0[var5] > var3) {
            var3 = var0[var5];
         }

         if (-var0[var5] > var3) {
            var3 = -var0[var5];
         }
      }

      if (var3 != 0.0D) {
         double var8 = var1 / var3;

         for(int var7 = 0; var7 < var0.length; ++var7) {
            var0[var7] *= var8;
         }

      }
   }

   public static void normalize(float[] var0, double var1) {
      double var3 = 0.5D;

      for(int var5 = 0; var5 < var0.length; ++var5) {
         if ((double)var0[var5 * 2] > var3) {
            var3 = (double)var0[var5 * 2];
         }

         if ((double)(-var0[var5 * 2]) > var3) {
            var3 = (double)(-var0[var5 * 2]);
         }
      }

      double var8 = var1 / var3;

      for(int var7 = 0; var7 < var0.length; ++var7) {
         var0[var7 * 2] = (float)((double)var0[var7 * 2] * var8);
      }

   }

   public static double[] realPart(double[] var0) {
      double[] var1 = new double[var0.length / 2];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = var0[var2 * 2];
      }

      return var1;
   }

   public static double[] imgPart(double[] var0) {
      double[] var1 = new double[var0.length / 2];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = var0[var2 * 2];
      }

      return var1;
   }

   public static float[] toFloat(double[] var0) {
      float[] var1 = new float[var0.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = (float)var0[var2];
      }

      return var1;
   }

   public static byte[] toBytes(float[] var0, AudioFormat var1) {
      byte[] var2 = new byte[var0.length * var1.getFrameSize()];
      return AudioFloatConverter.getConverter(var1).toByteArray(var0, var2);
   }

   public static void fadeUp(double[] var0, int var1) {
      double var2 = (double)var1;

      for(int var4 = 0; var4 < var1; ++var4) {
         var0[var4] *= (double)var4 / var2;
      }

   }

   public static void fadeUp(float[] var0, int var1) {
      double var2 = (double)var1;

      for(int var4 = 0; var4 < var1; ++var4) {
         var0[var4] = (float)((double)var0[var4] * ((double)var4 / var2));
      }

   }

   public static double[] loopExtend(double[] var0, int var1) {
      double[] var2 = new double[var1];
      int var3 = var0.length;
      int var4 = 0;

      for(int var5 = 0; var5 < var2.length; ++var5) {
         var2[var5] = var0[var4];
         ++var4;
         if (var4 == var3) {
            var4 = 0;
         }
      }

      return var2;
   }

   public static float[] loopExtend(float[] var0, int var1) {
      float[] var2 = new float[var1];
      int var3 = var0.length;
      int var4 = 0;

      for(int var5 = 0; var5 < var2.length; ++var5) {
         var2[var5] = var0[var4];
         ++var4;
         if (var4 == var3) {
            var4 = 0;
         }
      }

      return var2;
   }
}
