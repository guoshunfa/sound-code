package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.sampled.AudioFormat;

public abstract class ModelInstrument extends Instrument {
   protected ModelInstrument(Soundbank var1, Patch var2, String var3, Class<?> var4) {
      super(var1, var2, var3, var4);
   }

   public ModelDirector getDirector(ModelPerformer[] var1, MidiChannel var2, ModelDirectedPlayer var3) {
      return new ModelStandardIndexedDirector(var1, var3);
   }

   public ModelPerformer[] getPerformers() {
      return new ModelPerformer[0];
   }

   public ModelChannelMixer getChannelMixer(MidiChannel var1, AudioFormat var2) {
      return null;
   }

   public final Patch getPatchAlias() {
      Patch var1 = this.getPatch();
      int var2 = var1.getProgram();
      int var3 = var1.getBank();
      if (var3 != 0) {
         return var1;
      } else {
         boolean var4 = false;
         if (this.getPatch() instanceof ModelPatch) {
            var4 = ((ModelPatch)this.getPatch()).isPercussion();
         }

         return var4 ? new Patch(15360, var2) : new Patch(15488, var2);
      }
   }

   public final String[] getKeys() {
      String[] var1 = new String[128];
      ModelPerformer[] var2 = this.getPerformers();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ModelPerformer var5 = var2[var4];

         for(int var6 = var5.getKeyFrom(); var6 <= var5.getKeyTo(); ++var6) {
            if (var6 >= 0 && var6 < 128 && var1[var6] == null) {
               String var7 = var5.getName();
               if (var7 == null) {
                  var7 = "untitled";
               }

               var1[var6] = var7;
            }
         }
      }

      return var1;
   }

   public final boolean[] getChannels() {
      boolean var1 = false;
      if (this.getPatch() instanceof ModelPatch) {
         var1 = ((ModelPatch)this.getPatch()).isPercussion();
      }

      if (var1) {
         boolean[] var5 = new boolean[16];

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = false;
         }

         var5[9] = true;
         return var5;
      } else {
         int var2 = this.getPatch().getBank();
         boolean[] var3;
         int var4;
         if (var2 >> 7 != 120 && var2 >> 7 != 121) {
            var3 = new boolean[16];

            for(var4 = 0; var4 < var3.length; ++var4) {
               var3[var4] = true;
            }

            var3[9] = false;
            return var3;
         } else {
            var3 = new boolean[16];

            for(var4 = 0; var4 < var3.length; ++var4) {
               var3[var4] = true;
            }

            return var3;
         }
      }
   }
}
