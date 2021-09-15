package com.sun.media.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;

public final class SF2Instrument extends ModelInstrument {
   String name = "";
   int preset = 0;
   int bank = 0;
   long library = 0L;
   long genre = 0L;
   long morphology = 0L;
   SF2GlobalRegion globalregion = null;
   List<SF2InstrumentRegion> regions = new ArrayList();

   public SF2Instrument() {
      super((Soundbank)null, (Patch)null, (String)null, (Class)null);
   }

   public SF2Instrument(SF2Soundbank var1) {
      super(var1, (Patch)null, (String)null, (Class)null);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public Patch getPatch() {
      return this.bank == 128 ? new ModelPatch(0, this.preset, true) : new ModelPatch(this.bank << 7, this.preset, false);
   }

   public void setPatch(Patch var1) {
      if (var1 instanceof ModelPatch && ((ModelPatch)var1).isPercussion()) {
         this.bank = 128;
         this.preset = var1.getProgram();
      } else {
         this.bank = var1.getBank() >> 7;
         this.preset = var1.getProgram();
      }

   }

   public Object getData() {
      return null;
   }

   public long getGenre() {
      return this.genre;
   }

   public void setGenre(long var1) {
      this.genre = var1;
   }

   public long getLibrary() {
      return this.library;
   }

   public void setLibrary(long var1) {
      this.library = var1;
   }

   public long getMorphology() {
      return this.morphology;
   }

   public void setMorphology(long var1) {
      this.morphology = var1;
   }

   public List<SF2InstrumentRegion> getRegions() {
      return this.regions;
   }

   public SF2GlobalRegion getGlobalRegion() {
      return this.globalregion;
   }

   public void setGlobalZone(SF2GlobalRegion var1) {
      this.globalregion = var1;
   }

   public String toString() {
      return this.bank == 128 ? "Drumkit: " + this.name + " preset #" + this.preset : "Instrument: " + this.name + " bank #" + this.bank + " preset #" + this.preset;
   }

   public ModelPerformer[] getPerformers() {
      int var1 = 0;

      SF2InstrumentRegion var3;
      for(Iterator var2 = this.regions.iterator(); var2.hasNext(); var1 += var3.getLayer().getRegions().size()) {
         var3 = (SF2InstrumentRegion)var2.next();
      }

      ModelPerformer[] var45 = new ModelPerformer[var1];
      int var46 = 0;
      SF2GlobalRegion var4 = this.globalregion;
      Iterator var5 = this.regions.iterator();

      while(var5.hasNext()) {
         SF2InstrumentRegion var6 = (SF2InstrumentRegion)var5.next();
         HashMap var7 = new HashMap();
         var7.putAll(var6.getGenerators());
         if (var4 != null) {
            var7.putAll(var4.getGenerators());
         }

         SF2Layer var8 = var6.getLayer();
         SF2GlobalRegion var9 = var8.getGlobalRegion();
         Iterator var10 = var8.getRegions().iterator();

         while(var10.hasNext()) {
            SF2LayerRegion var11 = (SF2LayerRegion)var10.next();
            ModelPerformer var12 = new ModelPerformer();
            if (var11.getSample() != null) {
               var12.setName(var11.getSample().getName());
            } else {
               var12.setName(var8.getName());
            }

            var45[var46++] = var12;
            byte var13 = 0;
            byte var14 = 127;
            byte var15 = 0;
            byte var16 = 127;
            if (var11.contains(57)) {
               var12.setExclusiveClass(var11.getInteger(57));
            }

            byte[] var17;
            if (var11.contains(43)) {
               var17 = var11.getBytes(43);
               if (var17[0] >= 0 && var17[0] > var13) {
                  var13 = var17[0];
               }

               if (var17[1] >= 0 && var17[1] < var14) {
                  var14 = var17[1];
               }
            }

            if (var11.contains(44)) {
               var17 = var11.getBytes(44);
               if (var17[0] >= 0 && var17[0] > var15) {
                  var15 = var17[0];
               }

               if (var17[1] >= 0 && var17[1] < var16) {
                  var16 = var17[1];
               }
            }

            if (var6.contains(43)) {
               var17 = var6.getBytes(43);
               if (var17[0] > var13) {
                  var13 = var17[0];
               }

               if (var17[1] < var14) {
                  var14 = var17[1];
               }
            }

            if (var6.contains(44)) {
               var17 = var6.getBytes(44);
               if (var17[0] > var15) {
                  var15 = var17[0];
               }

               if (var17[1] < var16) {
                  var16 = var17[1];
               }
            }

            var12.setKeyFrom(var13);
            var12.setKeyTo(var14);
            var12.setVelFrom(var15);
            var12.setVelTo(var16);
            short var47 = var11.getShort(0);
            short var18 = var11.getShort(1);
            short var19 = var11.getShort(2);
            short var20 = var11.getShort(3);
            int var49 = var47 + var11.getShort(4) * '耀';
            int var48 = var18 + var11.getShort(12) * '耀';
            int var50 = var19 + var11.getShort(45) * '耀';
            int var51 = var20 + var11.getShort(50) * '耀';
            var50 -= var49;
            var51 -= var49;
            SF2Sample var21 = var11.getSample();
            int var22 = var21.originalPitch;
            if (var11.getShort(58) != -1) {
               var22 = var11.getShort(58);
            }

            float var23 = (float)(-var22 * 100 + var21.pitchCorrection);
            ModelByteBuffer var24 = var21.getDataBuffer();
            ModelByteBuffer var25 = var21.getData24Buffer();
            if (var49 != 0 || var48 != 0) {
               var24 = var24.subbuffer((long)(var49 * 2), var24.capacity() + (long)(var48 * 2));
               if (var25 != null) {
                  var25 = var25.subbuffer((long)var49, var25.capacity() + (long)var48);
               }
            }

            ModelByteBufferWavetable var26 = new ModelByteBufferWavetable(var24, var21.getFormat(), var23);
            if (var25 != null) {
               var26.set8BitExtensionBuffer(var25);
            }

            HashMap var27 = new HashMap();
            if (var9 != null) {
               var27.putAll(var9.getGenerators());
            }

            var27.putAll(var11.getGenerators());
            Iterator var28 = var7.entrySet().iterator();

            short var30;
            while(var28.hasNext()) {
               Map.Entry var29 = (Map.Entry)var28.next();
               if (!var27.containsKey(var29.getKey())) {
                  var30 = var11.getShort((Integer)var29.getKey());
               } else {
                  var30 = (Short)var27.get(var29.getKey());
               }

               var30 += (Short)var29.getValue();
               var27.put(var29.getKey(), var30);
            }

            short var52 = this.getGeneratorValue(var27, 54);
            if ((var52 == 1 || var52 == 3) && var21.startLoop >= 0L && var21.endLoop > 0L) {
               var26.setLoopStart((float)((int)(var21.startLoop + (long)var50)));
               var26.setLoopLength((float)((int)(var21.endLoop - var21.startLoop + (long)var51 - (long)var50)));
               if (var52 == 1) {
                  var26.setLoopType(1);
               }

               if (var52 == 3) {
                  var26.setLoopType(2);
               }
            }

            var12.getOscillators().add(var26);
            short var53 = this.getGeneratorValue(var27, 33);
            var30 = this.getGeneratorValue(var27, 34);
            short var31 = this.getGeneratorValue(var27, 35);
            short var32 = this.getGeneratorValue(var27, 36);
            short var33 = this.getGeneratorValue(var27, 37);
            short var34 = this.getGeneratorValue(var27, 38);
            short var35;
            float var36;
            ModelIdentifier var37;
            ModelIdentifier var38;
            if (var31 != -12000) {
               var35 = this.getGeneratorValue(var27, 39);
               var31 = (short)(var31 + 60 * var35);
               var36 = (float)(-var35 * 128);
               var37 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
               var38 = ModelDestination.DESTINATION_EG1_HOLD;
               var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var37), (double)var36, new ModelDestination(var38)));
            }

            if (var32 != -12000) {
               var35 = this.getGeneratorValue(var27, 40);
               var32 = (short)(var32 + 60 * var35);
               var36 = (float)(-var35 * 128);
               var37 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
               var38 = ModelDestination.DESTINATION_EG1_DECAY;
               var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var37), (double)var36, new ModelDestination(var38)));
            }

            this.addTimecentValue(var12, ModelDestination.DESTINATION_EG1_DELAY, var53);
            this.addTimecentValue(var12, ModelDestination.DESTINATION_EG1_ATTACK, var30);
            this.addTimecentValue(var12, ModelDestination.DESTINATION_EG1_HOLD, var31);
            this.addTimecentValue(var12, ModelDestination.DESTINATION_EG1_DECAY, var32);
            var33 = (short)(1000 - var33);
            if (var33 < 0) {
               var33 = 0;
            }

            if (var33 > 1000) {
               var33 = 1000;
            }

            this.addValue(var12, ModelDestination.DESTINATION_EG1_SUSTAIN, var33);
            this.addTimecentValue(var12, ModelDestination.DESTINATION_EG1_RELEASE, var34);
            short var54;
            short var55;
            short var56;
            if (this.getGeneratorValue(var27, 11) != 0 || this.getGeneratorValue(var27, 7) != 0) {
               var35 = this.getGeneratorValue(var27, 25);
               var54 = this.getGeneratorValue(var27, 26);
               var55 = this.getGeneratorValue(var27, 27);
               var56 = this.getGeneratorValue(var27, 28);
               short var39 = this.getGeneratorValue(var27, 29);
               short var40 = this.getGeneratorValue(var27, 30);
               short var41;
               float var42;
               ModelIdentifier var43;
               ModelIdentifier var44;
               if (var55 != -12000) {
                  var41 = this.getGeneratorValue(var27, 31);
                  var55 = (short)(var55 + 60 * var41);
                  var42 = (float)(-var41 * 128);
                  var43 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
                  var44 = ModelDestination.DESTINATION_EG2_HOLD;
                  var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var43), (double)var42, new ModelDestination(var44)));
               }

               if (var56 != -12000) {
                  var41 = this.getGeneratorValue(var27, 32);
                  var56 = (short)(var56 + 60 * var41);
                  var42 = (float)(-var41 * 128);
                  var43 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
                  var44 = ModelDestination.DESTINATION_EG2_DECAY;
                  var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var43), (double)var42, new ModelDestination(var44)));
               }

               this.addTimecentValue(var12, ModelDestination.DESTINATION_EG2_DELAY, var35);
               this.addTimecentValue(var12, ModelDestination.DESTINATION_EG2_ATTACK, var54);
               this.addTimecentValue(var12, ModelDestination.DESTINATION_EG2_HOLD, var55);
               this.addTimecentValue(var12, ModelDestination.DESTINATION_EG2_DECAY, var56);
               if (var39 < 0) {
                  var39 = 0;
               }

               if (var39 > 1000) {
                  var39 = 1000;
               }

               this.addValue(var12, ModelDestination.DESTINATION_EG2_SUSTAIN, (double)(1000 - var39));
               this.addTimecentValue(var12, ModelDestination.DESTINATION_EG2_RELEASE, var40);
               double var61;
               if (this.getGeneratorValue(var27, 11) != 0) {
                  var61 = (double)this.getGeneratorValue(var27, 11);
                  var43 = ModelSource.SOURCE_EG2;
                  var44 = ModelDestination.DESTINATION_FILTER_FREQ;
                  var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var43), var61, new ModelDestination(var44)));
               }

               if (this.getGeneratorValue(var27, 7) != 0) {
                  var61 = (double)this.getGeneratorValue(var27, 7);
                  var43 = ModelSource.SOURCE_EG2;
                  var44 = ModelDestination.DESTINATION_PITCH;
                  var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var43), var61, new ModelDestination(var44)));
               }
            }

            if (this.getGeneratorValue(var27, 10) != 0 || this.getGeneratorValue(var27, 5) != 0 || this.getGeneratorValue(var27, 13) != 0) {
               var35 = this.getGeneratorValue(var27, 22);
               var54 = this.getGeneratorValue(var27, 21);
               this.addTimecentValue(var12, ModelDestination.DESTINATION_LFO1_DELAY, var54);
               this.addValue(var12, ModelDestination.DESTINATION_LFO1_FREQ, var35);
            }

            var35 = this.getGeneratorValue(var27, 24);
            var54 = this.getGeneratorValue(var27, 23);
            this.addTimecentValue(var12, ModelDestination.DESTINATION_LFO2_DELAY, var54);
            this.addValue(var12, ModelDestination.DESTINATION_LFO2_FREQ, var35);
            double var57;
            ModelIdentifier var58;
            ModelIdentifier var59;
            if (this.getGeneratorValue(var27, 6) != 0) {
               var57 = (double)this.getGeneratorValue(var27, 6);
               var59 = ModelSource.SOURCE_LFO2;
               var58 = ModelDestination.DESTINATION_PITCH;
               var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var59, false, true), var57, new ModelDestination(var58)));
            }

            if (this.getGeneratorValue(var27, 10) != 0) {
               var57 = (double)this.getGeneratorValue(var27, 10);
               var59 = ModelSource.SOURCE_LFO1;
               var58 = ModelDestination.DESTINATION_FILTER_FREQ;
               var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var59, false, true), var57, new ModelDestination(var58)));
            }

            if (this.getGeneratorValue(var27, 5) != 0) {
               var57 = (double)this.getGeneratorValue(var27, 5);
               var59 = ModelSource.SOURCE_LFO1;
               var58 = ModelDestination.DESTINATION_PITCH;
               var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var59, false, true), var57, new ModelDestination(var58)));
            }

            if (this.getGeneratorValue(var27, 13) != 0) {
               var57 = (double)this.getGeneratorValue(var27, 13);
               var59 = ModelSource.SOURCE_LFO1;
               var58 = ModelDestination.DESTINATION_GAIN;
               var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var59, false, true), var57, new ModelDestination(var58)));
            }

            if (var11.getShort(46) != -1) {
               var57 = (double)var11.getShort(46) / 128.0D;
               this.addValue(var12, ModelDestination.DESTINATION_KEYNUMBER, var57);
            }

            if (var11.getShort(47) != -1) {
               var57 = (double)var11.getShort(47) / 128.0D;
               this.addValue(var12, ModelDestination.DESTINATION_VELOCITY, var57);
            }

            if (this.getGeneratorValue(var27, 8) < 13500) {
               var55 = this.getGeneratorValue(var27, 8);
               var56 = this.getGeneratorValue(var27, 9);
               this.addValue(var12, ModelDestination.DESTINATION_FILTER_FREQ, var55);
               this.addValue(var12, ModelDestination.DESTINATION_FILTER_Q, var56);
            }

            int var60 = 100 * this.getGeneratorValue(var27, 51);
            var60 += this.getGeneratorValue(var27, 52);
            if (var60 != 0) {
               this.addValue(var12, ModelDestination.DESTINATION_PITCH, (short)var60);
            }

            if (this.getGeneratorValue(var27, 17) != 0) {
               var56 = this.getGeneratorValue(var27, 17);
               this.addValue(var12, ModelDestination.DESTINATION_PAN, var56);
            }

            if (this.getGeneratorValue(var27, 48) != 0) {
               var56 = this.getGeneratorValue(var27, 48);
               this.addValue(var12, ModelDestination.DESTINATION_GAIN, (double)(-0.376287F * (float)var56));
            }

            if (this.getGeneratorValue(var27, 15) != 0) {
               var56 = this.getGeneratorValue(var27, 15);
               this.addValue(var12, ModelDestination.DESTINATION_CHORUS, var56);
            }

            if (this.getGeneratorValue(var27, 16) != 0) {
               var56 = this.getGeneratorValue(var27, 16);
               this.addValue(var12, ModelDestination.DESTINATION_REVERB, var56);
            }

            if (this.getGeneratorValue(var27, 56) != 100) {
               var56 = this.getGeneratorValue(var27, 56);
               if (var56 == 0) {
                  var59 = ModelDestination.DESTINATION_PITCH;
                  var12.getConnectionBlocks().add(new ModelConnectionBlock((ModelSource)null, (double)(var22 * 100), new ModelDestination(var59)));
               } else {
                  var59 = ModelDestination.DESTINATION_PITCH;
                  var12.getConnectionBlocks().add(new ModelConnectionBlock((ModelSource)null, (double)(var22 * (100 - var56)), new ModelDestination(var59)));
               }

               var59 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
               var58 = ModelDestination.DESTINATION_PITCH;
               var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(var59), (double)(128 * var56), new ModelDestination(var58)));
            }

            var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_VELOCITY, new ModelTransform() {
               public double transform(double var1) {
                  return var1 < 0.5D ? 1.0D - var1 * 2.0D : 0.0D;
               }
            }), -2400.0D, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ)));
            var12.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO2, false, true, 0), new ModelSource(new ModelIdentifier("midi_cc", "1", 0), false, false, 0), 50.0D, new ModelDestination(ModelDestination.DESTINATION_PITCH)));
            Iterator var62;
            SF2Modulator var63;
            if (var8.getGlobalRegion() != null) {
               var62 = var8.getGlobalRegion().getModulators().iterator();

               while(var62.hasNext()) {
                  var63 = (SF2Modulator)var62.next();
                  this.convertModulator(var12, var63);
               }
            }

            var62 = var11.getModulators().iterator();

            while(var62.hasNext()) {
               var63 = (SF2Modulator)var62.next();
               this.convertModulator(var12, var63);
            }

            if (var4 != null) {
               var62 = var4.getModulators().iterator();

               while(var62.hasNext()) {
                  var63 = (SF2Modulator)var62.next();
                  this.convertModulator(var12, var63);
               }
            }

            var62 = var6.getModulators().iterator();

            while(var62.hasNext()) {
               var63 = (SF2Modulator)var62.next();
               this.convertModulator(var12, var63);
            }
         }
      }

      return var45;
   }

   private void convertModulator(ModelPerformer var1, SF2Modulator var2) {
      ModelSource var3 = convertSource(var2.getSourceOperator());
      ModelSource var4 = convertSource(var2.getAmountSourceOperator());
      if (var3 != null || var2.getSourceOperator() == 0) {
         if (var4 != null || var2.getAmountSourceOperator() == 0) {
            double var5 = (double)var2.getAmount();
            double[] var7 = new double[1];
            ModelSource[] var8 = new ModelSource[1];
            var7[0] = 1.0D;
            ModelDestination var9 = convertDestination(var2.getDestinationOperator(), var7, var8);
            var5 *= var7[0];
            if (var9 != null) {
               if (var2.getTransportOperator() == 2) {
                  ((ModelStandardTransform)var9.getTransform()).setTransform(4);
               }

               ModelConnectionBlock var10 = new ModelConnectionBlock(var3, var4, var5, var9);
               if (var8[0] != null) {
                  var10.addSource(var8[0]);
               }

               var1.getConnectionBlocks().add(var10);
            }
         }
      }
   }

   private static ModelSource convertSource(int var0) {
      if (var0 == 0) {
         return null;
      } else {
         ModelIdentifier var1 = null;
         int var2 = var0 & 127;
         if ((var0 & 128) != 0) {
            var1 = new ModelIdentifier("midi_cc", Integer.toString(var2));
         } else {
            if (var2 == 2) {
               var1 = ModelSource.SOURCE_NOTEON_VELOCITY;
            }

            if (var2 == 3) {
               var1 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
            }

            if (var2 == 10) {
               var1 = ModelSource.SOURCE_MIDI_POLY_PRESSURE;
            }

            if (var2 == 13) {
               var1 = ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
            }

            if (var2 == 14) {
               var1 = ModelSource.SOURCE_MIDI_PITCH;
            }

            if (var2 == 16) {
               var1 = new ModelIdentifier("midi_rpn", "0");
            }
         }

         if (var1 == null) {
            return null;
         } else {
            ModelSource var3 = new ModelSource(var1);
            ModelStandardTransform var4 = (ModelStandardTransform)var3.getTransform();
            if ((256 & var0) != 0) {
               var4.setDirection(true);
            } else {
               var4.setDirection(false);
            }

            if ((512 & var0) != 0) {
               var4.setPolarity(true);
            } else {
               var4.setPolarity(false);
            }

            if ((1024 & var0) != 0) {
               var4.setTransform(1);
            }

            if ((2048 & var0) != 0) {
               var4.setTransform(2);
            }

            if ((3072 & var0) != 0) {
               var4.setTransform(3);
            }

            return var3;
         }
      }
   }

   static ModelDestination convertDestination(int var0, double[] var1, ModelSource[] var2) {
      ModelIdentifier var3 = null;
      switch(var0) {
      case 5:
         var3 = ModelDestination.DESTINATION_PITCH;
         var2[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
         break;
      case 6:
         var3 = ModelDestination.DESTINATION_PITCH;
         var2[0] = new ModelSource(ModelSource.SOURCE_LFO2, false, true);
         break;
      case 7:
         var3 = ModelDestination.DESTINATION_PITCH;
         var2[0] = new ModelSource(ModelSource.SOURCE_EG2, false, true);
         break;
      case 8:
         var3 = ModelDestination.DESTINATION_FILTER_FREQ;
         break;
      case 9:
         var3 = ModelDestination.DESTINATION_FILTER_Q;
         break;
      case 10:
         var3 = ModelDestination.DESTINATION_FILTER_FREQ;
         var2[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
         break;
      case 11:
         var3 = ModelDestination.DESTINATION_FILTER_FREQ;
         var2[0] = new ModelSource(ModelSource.SOURCE_EG2, false, true);
      case 12:
      case 14:
      case 18:
      case 19:
      case 20:
      case 31:
      case 32:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 49:
      case 50:
      default:
         break;
      case 13:
         var3 = ModelDestination.DESTINATION_GAIN;
         var1[0] = -0.3762870132923126D;
         var2[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
         break;
      case 15:
         var3 = ModelDestination.DESTINATION_CHORUS;
         break;
      case 16:
         var3 = ModelDestination.DESTINATION_REVERB;
         break;
      case 17:
         var3 = ModelDestination.DESTINATION_PAN;
         break;
      case 21:
         var3 = ModelDestination.DESTINATION_LFO1_DELAY;
         break;
      case 22:
         var3 = ModelDestination.DESTINATION_LFO1_FREQ;
         break;
      case 23:
         var3 = ModelDestination.DESTINATION_LFO2_DELAY;
         break;
      case 24:
         var3 = ModelDestination.DESTINATION_LFO2_FREQ;
         break;
      case 25:
         var3 = ModelDestination.DESTINATION_EG2_DELAY;
         break;
      case 26:
         var3 = ModelDestination.DESTINATION_EG2_ATTACK;
         break;
      case 27:
         var3 = ModelDestination.DESTINATION_EG2_HOLD;
         break;
      case 28:
         var3 = ModelDestination.DESTINATION_EG2_DECAY;
         break;
      case 29:
         var3 = ModelDestination.DESTINATION_EG2_SUSTAIN;
         var1[0] = -1.0D;
         break;
      case 30:
         var3 = ModelDestination.DESTINATION_EG2_RELEASE;
         break;
      case 33:
         var3 = ModelDestination.DESTINATION_EG1_DELAY;
         break;
      case 34:
         var3 = ModelDestination.DESTINATION_EG1_ATTACK;
         break;
      case 35:
         var3 = ModelDestination.DESTINATION_EG1_HOLD;
         break;
      case 36:
         var3 = ModelDestination.DESTINATION_EG1_DECAY;
         break;
      case 37:
         var3 = ModelDestination.DESTINATION_EG1_SUSTAIN;
         var1[0] = -1.0D;
         break;
      case 38:
         var3 = ModelDestination.DESTINATION_EG1_RELEASE;
         break;
      case 46:
         var3 = ModelDestination.DESTINATION_KEYNUMBER;
         break;
      case 47:
         var3 = ModelDestination.DESTINATION_VELOCITY;
         break;
      case 48:
         var3 = ModelDestination.DESTINATION_GAIN;
         var1[0] = -0.3762870132923126D;
         break;
      case 51:
         var1[0] = 100.0D;
         var3 = ModelDestination.DESTINATION_PITCH;
         break;
      case 52:
         var3 = ModelDestination.DESTINATION_PITCH;
      }

      return var3 != null ? new ModelDestination(var3) : null;
   }

   private void addTimecentValue(ModelPerformer var1, ModelIdentifier var2, short var3) {
      double var4;
      if (var3 == -12000) {
         var4 = Double.NEGATIVE_INFINITY;
      } else {
         var4 = (double)var3;
      }

      var1.getConnectionBlocks().add(new ModelConnectionBlock(var4, new ModelDestination(var2)));
   }

   private void addValue(ModelPerformer var1, ModelIdentifier var2, short var3) {
      double var4 = (double)var3;
      var1.getConnectionBlocks().add(new ModelConnectionBlock(var4, new ModelDestination(var2)));
   }

   private void addValue(ModelPerformer var1, ModelIdentifier var2, double var3) {
      var1.getConnectionBlocks().add(new ModelConnectionBlock(var3, new ModelDestination(var2)));
   }

   private short getGeneratorValue(Map<Integer, Short> var1, int var2) {
      return var1.containsKey(var2) ? (Short)var1.get(var2) : SF2Region.getDefaultValue(var2);
   }
}
