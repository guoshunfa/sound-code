package com.sun.media.sound;

import java.util.Arrays;

public final class ModelStandardDirector implements ModelDirector {
   private final ModelPerformer[] performers;
   private final ModelDirectedPlayer player;
   private boolean noteOnUsed = false;
   private boolean noteOffUsed = false;

   public ModelStandardDirector(ModelPerformer[] var1, ModelDirectedPlayer var2) {
      this.performers = (ModelPerformer[])Arrays.copyOf((Object[])var1, var1.length);
      this.player = var2;
      ModelPerformer[] var3 = this.performers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelPerformer var6 = var3[var5];
         if (var6.isReleaseTriggered()) {
            this.noteOffUsed = true;
         } else {
            this.noteOnUsed = true;
         }
      }

   }

   public void close() {
   }

   public void noteOff(int var1, int var2) {
      if (this.noteOffUsed) {
         for(int var3 = 0; var3 < this.performers.length; ++var3) {
            ModelPerformer var4 = this.performers[var3];
            if (var4.getKeyFrom() <= var1 && var4.getKeyTo() >= var1 && var4.getVelFrom() <= var2 && var4.getVelTo() >= var2 && var4.isReleaseTriggered()) {
               this.player.play(var3, (ModelConnectionBlock[])null);
            }
         }

      }
   }

   public void noteOn(int var1, int var2) {
      if (this.noteOnUsed) {
         for(int var3 = 0; var3 < this.performers.length; ++var3) {
            ModelPerformer var4 = this.performers[var3];
            if (var4.getKeyFrom() <= var1 && var4.getKeyTo() >= var1 && var4.getVelFrom() <= var2 && var4.getVelTo() >= var2 && !var4.isReleaseTriggered()) {
               this.player.play(var3, (ModelConnectionBlock[])null);
            }
         }

      }
   }
}
