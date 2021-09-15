package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;

public final class DLSInstrument extends ModelInstrument {
   int preset = 0;
   int bank = 0;
   boolean druminstrument = false;
   byte[] guid = null;
   DLSInfo info = new DLSInfo();
   List<DLSRegion> regions = new ArrayList();
   List<DLSModulator> modulators = new ArrayList();

   public DLSInstrument() {
      super((Soundbank)null, (Patch)null, (String)null, (Class)null);
   }

   public DLSInstrument(DLSSoundbank var1) {
      super(var1, (Patch)null, (String)null, (Class)null);
   }

   public DLSInfo getInfo() {
      return this.info;
   }

   public String getName() {
      return this.info.name;
   }

   public void setName(String var1) {
      this.info.name = var1;
   }

   public ModelPatch getPatch() {
      return new ModelPatch(this.bank, this.preset, this.druminstrument);
   }

   public void setPatch(Patch var1) {
      if (var1 instanceof ModelPatch && ((ModelPatch)var1).isPercussion()) {
         this.druminstrument = true;
         this.bank = var1.getBank();
         this.preset = var1.getProgram();
      } else {
         this.druminstrument = false;
         this.bank = var1.getBank();
         this.preset = var1.getProgram();
      }

   }

   public Object getData() {
      return null;
   }

   public List<DLSRegion> getRegions() {
      return this.regions;
   }

   public List<DLSModulator> getModulators() {
      return this.modulators;
   }

   public String toString() {
      return this.druminstrument ? "Drumkit: " + this.info.name + " bank #" + this.bank + " preset #" + this.preset : "Instrument: " + this.info.name + " bank #" + this.bank + " preset #" + this.preset;
   }

   private ModelIdentifier convertToModelDest(int var1) {
      if (var1 == 0) {
         return null;
      } else if (var1 == 1) {
         return ModelDestination.DESTINATION_GAIN;
      } else if (var1 == 3) {
         return ModelDestination.DESTINATION_PITCH;
      } else if (var1 == 4) {
         return ModelDestination.DESTINATION_PAN;
      } else if (var1 == 260) {
         return ModelDestination.DESTINATION_LFO1_FREQ;
      } else if (var1 == 261) {
         return ModelDestination.DESTINATION_LFO1_DELAY;
      } else if (var1 == 518) {
         return ModelDestination.DESTINATION_EG1_ATTACK;
      } else if (var1 == 519) {
         return ModelDestination.DESTINATION_EG1_DECAY;
      } else if (var1 == 521) {
         return ModelDestination.DESTINATION_EG1_RELEASE;
      } else if (var1 == 522) {
         return ModelDestination.DESTINATION_EG1_SUSTAIN;
      } else if (var1 == 778) {
         return ModelDestination.DESTINATION_EG2_ATTACK;
      } else if (var1 == 779) {
         return ModelDestination.DESTINATION_EG2_DECAY;
      } else if (var1 == 781) {
         return ModelDestination.DESTINATION_EG2_RELEASE;
      } else if (var1 == 782) {
         return ModelDestination.DESTINATION_EG2_SUSTAIN;
      } else if (var1 == 5) {
         return ModelDestination.DESTINATION_KEYNUMBER;
      } else if (var1 == 128) {
         return ModelDestination.DESTINATION_CHORUS;
      } else if (var1 == 129) {
         return ModelDestination.DESTINATION_REVERB;
      } else if (var1 == 276) {
         return ModelDestination.DESTINATION_LFO2_FREQ;
      } else if (var1 == 277) {
         return ModelDestination.DESTINATION_LFO2_DELAY;
      } else if (var1 == 523) {
         return ModelDestination.DESTINATION_EG1_DELAY;
      } else if (var1 == 524) {
         return ModelDestination.DESTINATION_EG1_HOLD;
      } else if (var1 == 525) {
         return ModelDestination.DESTINATION_EG1_SHUTDOWN;
      } else if (var1 == 783) {
         return ModelDestination.DESTINATION_EG2_DELAY;
      } else if (var1 == 784) {
         return ModelDestination.DESTINATION_EG2_HOLD;
      } else if (var1 == 1280) {
         return ModelDestination.DESTINATION_FILTER_FREQ;
      } else {
         return var1 == 1281 ? ModelDestination.DESTINATION_FILTER_Q : null;
      }
   }

   private ModelIdentifier convertToModelSrc(int var1) {
      if (var1 == 0) {
         return null;
      } else if (var1 == 1) {
         return ModelSource.SOURCE_LFO1;
      } else if (var1 == 2) {
         return ModelSource.SOURCE_NOTEON_VELOCITY;
      } else if (var1 == 3) {
         return ModelSource.SOURCE_NOTEON_KEYNUMBER;
      } else if (var1 == 4) {
         return ModelSource.SOURCE_EG1;
      } else if (var1 == 5) {
         return ModelSource.SOURCE_EG2;
      } else if (var1 == 6) {
         return ModelSource.SOURCE_MIDI_PITCH;
      } else if (var1 == 129) {
         return new ModelIdentifier("midi_cc", "1", 0);
      } else if (var1 == 135) {
         return new ModelIdentifier("midi_cc", "7", 0);
      } else if (var1 == 138) {
         return new ModelIdentifier("midi_cc", "10", 0);
      } else if (var1 == 139) {
         return new ModelIdentifier("midi_cc", "11", 0);
      } else if (var1 == 256) {
         return new ModelIdentifier("midi_rpn", "0", 0);
      } else if (var1 == 257) {
         return new ModelIdentifier("midi_rpn", "1", 0);
      } else if (var1 == 7) {
         return ModelSource.SOURCE_MIDI_POLY_PRESSURE;
      } else if (var1 == 8) {
         return ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
      } else if (var1 == 9) {
         return ModelSource.SOURCE_LFO2;
      } else if (var1 == 10) {
         return ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
      } else if (var1 == 219) {
         return new ModelIdentifier("midi_cc", "91", 0);
      } else {
         return var1 == 221 ? new ModelIdentifier("midi_cc", "93", 0) : null;
      }
   }

   private ModelConnectionBlock convertToModel(DLSModulator var1) {
      ModelIdentifier var2 = this.convertToModelSrc(var1.getSource());
      ModelIdentifier var3 = this.convertToModelSrc(var1.getControl());
      ModelIdentifier var4 = this.convertToModelDest(var1.getDestination());
      int var5 = var1.getScale();
      double var6;
      if (var5 == Integer.MIN_VALUE) {
         var6 = Double.NEGATIVE_INFINITY;
      } else {
         var6 = (double)var5 / 65536.0D;
      }

      if (var4 == null) {
         return null;
      } else {
         ModelSource var8 = null;
         ModelSource var9 = null;
         ModelConnectionBlock var10 = new ModelConnectionBlock();
         ModelSource var11;
         if (var3 != null) {
            var11 = new ModelSource();
            if (var3 == ModelSource.SOURCE_MIDI_PITCH) {
               ((ModelStandardTransform)var11.getTransform()).setPolarity(true);
            } else if (var3 == ModelSource.SOURCE_LFO1 || var3 == ModelSource.SOURCE_LFO2) {
               ((ModelStandardTransform)var11.getTransform()).setPolarity(true);
            }

            var11.setIdentifier(var3);
            var10.addSource(var11);
            var9 = var11;
         }

         if (var2 != null) {
            var11 = new ModelSource();
            if (var2 == ModelSource.SOURCE_MIDI_PITCH) {
               ((ModelStandardTransform)var11.getTransform()).setPolarity(true);
            } else if (var2 == ModelSource.SOURCE_LFO1 || var2 == ModelSource.SOURCE_LFO2) {
               ((ModelStandardTransform)var11.getTransform()).setPolarity(true);
            }

            var11.setIdentifier(var2);
            var10.addSource(var11);
            var8 = var11;
         }

         ModelDestination var20 = new ModelDestination();
         var20.setIdentifier(var4);
         var10.setDestination(var20);
         if (var1.getVersion() == 1) {
            if (var1.getTransform() == 1) {
               if (var8 != null) {
                  ((ModelStandardTransform)var8.getTransform()).setTransform(1);
                  ((ModelStandardTransform)var8.getTransform()).setDirection(true);
               }

               if (var9 != null) {
                  ((ModelStandardTransform)var9.getTransform()).setTransform(1);
                  ((ModelStandardTransform)var9.getTransform()).setDirection(true);
               }
            }
         } else if (var1.getVersion() == 2) {
            int var12 = var1.getTransform();
            int var13 = var12 >> 15 & 1;
            int var14 = var12 >> 14 & 1;
            int var15 = var12 >> 10 & 8;
            int var16 = var12 >> 9 & 1;
            int var17 = var12 >> 8 & 1;
            int var18 = var12 >> 4 & 8;
            byte var19;
            if (var8 != null) {
               var19 = 0;
               if (var15 == 3) {
                  var19 = 3;
               }

               if (var15 == 1) {
                  var19 = 1;
               }

               if (var15 == 2) {
                  var19 = 2;
               }

               ((ModelStandardTransform)var8.getTransform()).setTransform(var19);
               ((ModelStandardTransform)var8.getTransform()).setPolarity(var14 == 1);
               ((ModelStandardTransform)var8.getTransform()).setDirection(var13 == 1);
            }

            if (var9 != null) {
               var19 = 0;
               if (var18 == 3) {
                  var19 = 3;
               }

               if (var18 == 1) {
                  var19 = 1;
               }

               if (var18 == 2) {
                  var19 = 2;
               }

               ((ModelStandardTransform)var9.getTransform()).setTransform(var19);
               ((ModelStandardTransform)var9.getTransform()).setPolarity(var17 == 1);
               ((ModelStandardTransform)var9.getTransform()).setDirection(var16 == 1);
            }
         }

         var10.setScale(var6);
         return var10;
      }
   }

   public ModelPerformer[] getPerformers() {
      ArrayList var1 = new ArrayList();
      HashMap var2 = new HashMap();
      Iterator var3 = this.getModulators().iterator();

      while(var3.hasNext()) {
         DLSModulator var4 = (DLSModulator)var3.next();
         var2.put(var4.getSource() + "x" + var4.getControl() + "=" + var4.getDestination(), var4);
      }

      HashMap var14 = new HashMap();
      Iterator var15 = this.regions.iterator();

      while(var15.hasNext()) {
         DLSRegion var5 = (DLSRegion)var15.next();
         ModelPerformer var6 = new ModelPerformer();
         var6.setName(var5.getSample().getName());
         var6.setSelfNonExclusive((var5.getFusoptions() & 1) != 0);
         var6.setExclusiveClass(var5.getExclusiveClass());
         var6.setKeyFrom(var5.getKeyfrom());
         var6.setKeyTo(var5.getKeyto());
         var6.setVelFrom(var5.getVelfrom());
         var6.setVelTo(var5.getVelto());
         var14.clear();
         var14.putAll(var2);
         Iterator var7 = var5.getModulators().iterator();

         while(var7.hasNext()) {
            DLSModulator var8 = (DLSModulator)var7.next();
            var14.put(var8.getSource() + "x" + var8.getControl() + "=" + var8.getDestination(), var8);
         }

         List var16 = var6.getConnectionBlocks();
         Iterator var17 = var14.values().iterator();

         while(var17.hasNext()) {
            DLSModulator var9 = (DLSModulator)var17.next();
            ModelConnectionBlock var10 = this.convertToModel(var9);
            if (var10 != null) {
               var16.add(var10);
            }
         }

         DLSSample var18 = var5.getSample();
         DLSSampleOptions var19 = var5.getSampleoptions();
         if (var19 == null) {
            var19 = var18.getSampleoptions();
         }

         ModelByteBuffer var20 = var18.getDataBuffer();
         float var11 = (float)(-var19.unitynote * 100 + var19.finetune);
         ModelByteBufferWavetable var12 = new ModelByteBufferWavetable(var20, var18.getFormat(), var11);
         var12.setAttenuation(var12.getAttenuation() / 65536.0F);
         if (var19.getLoops().size() != 0) {
            DLSSampleLoop var13 = (DLSSampleLoop)var19.getLoops().get(0);
            var12.setLoopStart((float)((int)var13.getStart()));
            var12.setLoopLength((float)((int)var13.getLength()));
            if (var13.getType() == 0L) {
               var12.setLoopType(1);
            }

            if (var13.getType() == 1L) {
               var12.setLoopType(2);
            } else {
               var12.setLoopType(1);
            }
         }

         var6.getConnectionBlocks().add(new ModelConnectionBlock(1.0D, new ModelDestination(new ModelIdentifier("filter", "type", 1))));
         var6.getOscillators().add(var12);
         var1.add(var6);
      }

      return (ModelPerformer[])var1.toArray(new ModelPerformer[var1.size()]);
   }

   public byte[] getGuid() {
      return this.guid == null ? null : Arrays.copyOf(this.guid, this.guid.length);
   }

   public void setGuid(byte[] var1) {
      this.guid = var1 == null ? null : Arrays.copyOf(var1, var1.length);
   }
}
